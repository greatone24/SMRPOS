package com.dee.smrpos.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dee.smrpos.R;

import java.io.File;

public final class ProductImageLoader {
    private ProductImageLoader() {
    }

    public static void load(Context context, String imageName, ImageView imageView) {
        Object image = R.drawable.ic_coffee;

        if (imageName != null && !imageName.isEmpty()) {
            Uri uri = Uri.parse(imageName);
            if ("content".equals(uri.getScheme()) || "file".equals(uri.getScheme())) {
                image = uri;
            } else {
                File file = new File(imageName);
                if (file.isAbsolute()) {
                    image = file;
                } else {
                    int resourceId = context.getResources().getIdentifier(
                            imageName, "drawable", context.getPackageName());
                    if (resourceId != 0) {
                        image = resourceId;
                    }
                }
            }
        }

        Glide.with(imageView)
                .load(image)
                .placeholder(R.drawable.ic_coffee)
                .error(R.drawable.ic_coffee)
                .into(imageView);
    }
}
