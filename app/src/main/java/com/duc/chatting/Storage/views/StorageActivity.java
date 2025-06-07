package com.duc.chatting.Storage.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.duc.chatting.R;
import com.duc.chatting.Storage.viewModels.StorageViewModel;
import com.duc.chatting.chat.adapters.FileListAdapter;
import com.duc.chatting.chat.adapters.ImageListAdapter;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.chat.viewmodels.ReceiverConservationViewModel;
import com.duc.chatting.databinding.ActivityStorageBinding;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.duc.chatting.utilities.widgets.BaseActivity;

public class StorageActivity extends BaseActivity {
    ActivityStorageBinding binding;
    private FileListAdapter fileListAdapter;
    private ImageListAdapter imageListAdapter;
    private StorageViewModel viewModel;
    private PreferenceManager preferenceManager;
    private boolean isImageExpanded = true;
    private boolean isPdfExpanded  = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStorageBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(this);
        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);
        viewModel.getListFile(preferenceManager.getString(Contants.KEY_USER_ID));
        viewModel.getListImage(preferenceManager.getString(Contants.KEY_USER_ID));
        viewModel.getListPDFMutableLiveData().observe(this, pdfs -> {
            fileListAdapter = new FileListAdapter(pdfs, this::onUserClickedFileMessage);
            binding.recyclerViewFile.setAdapter(fileListAdapter);

        });
        viewModel.getListImageMutableLiveData().observe(this, images -> {
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
            binding.recyclerViewFileImage.setLayoutManager(gridLayoutManager);

            imageListAdapter = new ImageListAdapter(images);
            binding.recyclerViewFileImage.setAdapter(imageListAdapter);

        });
        binding.sectionImage.setOnClickListener(v -> {
            isImageExpanded = !isImageExpanded;
            binding.recyclerViewFileImage.setVisibility(isImageExpanded ? View.VISIBLE : View.GONE);
            binding.arrowImage.setRotation(isImageExpanded ? 0f : 180f);
        });
        binding.sectionPdf.setOnClickListener(v -> {
            isPdfExpanded = !isPdfExpanded;
            binding.recyclerViewFile.setVisibility(isPdfExpanded ? View.VISIBLE : View.GONE);
            binding.arrowPdf.setRotation(isPdfExpanded ? 0f : 180f);
        });
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
    }
    @SuppressLint("IntentReset")
    public void onUserClickedFileMessage(PDFClass pdfClass)
    {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        intent.setData(Uri.parse(pdfClass.getUrlFile()));
        startActivity(intent);
    }
    @Override
    protected void onRetryConnection() {

    }
}