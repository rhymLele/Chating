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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStorageBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferenceManager=new PreferenceManager(this);
        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);
        binding.textFile.setOnClickListener(v -> {
            if(binding.recyclerViewFile.getVisibility()== View.GONE)
            {
                binding.recyclerViewFile.setVisibility(View.VISIBLE);
                viewModel.getListFile(preferenceManager.getString(Contants.KEY_USER_ID));
            }else if(binding.recyclerViewFile.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFile.setVisibility(View.GONE);
            }
        });
        binding.textFileImage.setOnClickListener(v -> {
            if(binding.recyclerViewFileImage.getVisibility()==View.GONE)
            {
                binding.recyclerViewFileImage.setVisibility(View.VISIBLE);
                viewModel.getListImage(preferenceManager.getString(Contants.KEY_USER_ID));
            }else if(binding.recyclerViewFileImage.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFileImage.setVisibility(View.GONE);
            }
        });
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