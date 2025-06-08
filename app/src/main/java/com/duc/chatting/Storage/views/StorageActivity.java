package com.duc.chatting.Storage.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        viewModel.getListImage();
        viewModel.getListPDFMutableLiveData().observe(this, pdfs -> {
            binding.recyclerViewFile.setLayoutManager(new LinearLayoutManager(this));

            fileListAdapter = new FileListAdapter(pdfs, this::onUserClickedFileMessage);
            binding.recyclerViewFile.setAdapter(fileListAdapter);
            Log.d("Uw",String.valueOf(fileListAdapter.getItemCount()));

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
    public void openPdf(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Xem file PDF"));
    }
    @SuppressLint("IntentReset")
    public void onUserClickedFileMessage(PDFClass pdfClass) {

        String url = pdfClass.getUrlFile();
        try {
            Uri pdfUri = decodeBase64ToPdfAndGetUri(this, url, pdfClass.getFileName());
            openPdf(this, pdfUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Oops ,can not open file Pdf!", Toast.LENGTH_SHORT).show();
        }
    }
    public Uri decodeBase64ToPdfAndGetUri(Context context, String base64, String fileName) throws IOException {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(decodedBytes);
        fos.close();

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );
    }
    @Override
    protected void onRetryConnection() {

    }
}