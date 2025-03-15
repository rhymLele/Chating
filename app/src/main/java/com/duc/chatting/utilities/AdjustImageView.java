package com.duc.chatting.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AdjustImageView {

    public static void AdjustImageViewHeight(ImageView imageView, Bitmap bitmap, Context context) {
        int fixedHeightInDp = 300;

        int fixedHeightInPx = (int) (fixedHeightInDp * context.getResources().getDisplayMetrics().density);

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        int newWidth = (fixedHeightInPx * imageWidth) / imageHeight;

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = fixedHeightInPx;
        layoutParams.width = newWidth;
        imageView.setLayoutParams(layoutParams);
    }

}
