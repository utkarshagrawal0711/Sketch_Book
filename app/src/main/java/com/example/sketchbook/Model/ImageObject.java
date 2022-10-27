package com.example.sketchbook.Model;

import android.graphics.Bitmap;

public class ImageObject {
    public Bitmap bitmap;
    public int imageLeft;
    public int imageTop;

    ImageObject() {
    }

    public ImageObject(Bitmap bitmap, int imageLeft, int imageTop) {
        this.bitmap = bitmap;
        this.imageLeft = imageLeft;
        this.imageTop = imageTop;
    }
}
