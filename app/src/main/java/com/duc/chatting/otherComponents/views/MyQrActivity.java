package com.duc.chatting.otherComponents.views;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.service.chooser.ChooserAction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duc.chatting.R;
import com.duc.chatting.databinding.ActivityMyQrBinding;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.duc.chatting.utilities.widgets.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MyQrActivity extends BaseActivity {
    ActivityMyQrBinding binding;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMyQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(this);
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(preferenceManager.getString(Contants.KEY_USER_ID), BarcodeFormat.QR_CODE, 400, 400);
            binding.qrCode.setImageBitmap(bitmap);
        } catch(Exception e) {

        }
        binding.imageDownload.setOnClickListener(v -> {
            binding.qrCode.setDrawingCacheEnabled(true);
            Bitmap bitmap = binding.qrCode.getDrawingCache();

            if (bitmap != null) {
                saveImageToGallery(bitmap);
            }
        });
        binding.btnShare.setOnClickListener(v -> {
            buttonShare(binding.qrCode);
        });

    }

    @Override
    protected void onRetryConnection() {

    }
    private void buttonShare(ImageView view){
        Drawable drawable = view.getDrawable();
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        StorageManager storageManager=(StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume=storageManager.getStorageVolumes().get(0);
        String stringPath=MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmap,"Shared Image",null);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(stringPath));
        startActivity(Intent.createChooser(intent,"Share the image ...."));
    }
//    private void share(ImageView bitmap)
//    {
//        Intent sendIntent = new Intent(Intent.ACTION_SEND)
//                .setType("text.plain")
//                .putExtra(Intent., bitmap);
//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        ChooserAction[] actions = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            actions = new ChooserAction[]{
//                    new ChooserAction.Builder(
//                            Icon.createWithResource(getApplicationContext(), R.drawable.ic_custom_action),
//                            "Custom",
//                            PendingIntent.getBroadcast(
//                                    getApplicationContext(),
//                                    1,
//                                    new Intent(Intent.ACTION_VIEW),
//                                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
//                            )
//                    ).build()
//            };
//        }
//        shareIntent.putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, actions);
//        getApplication().startActivity(shareIntent);
//    }
    private void saveImageToGallery(Bitmap bitmap) {
        String filename = "QR_" + System.currentTimeMillis() + ".png";

        OutputStream fos;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyQR");

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString() + "/MyQR";
                File file = new File(imagesDir);
                if (!file.exists()) file.mkdirs();

                File image = new File(file, filename);
                fos = new FileOutputStream(image);

                // Gửi broadcast để ảnh hiện ra trong thư viện ngay
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(image));
                sendBroadcast(intent);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (fos != null) fos.close();

            Toast.makeText(this, "Saved into gallery!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Saving error!", Toast.LENGTH_SHORT).show();
        }
    }

}