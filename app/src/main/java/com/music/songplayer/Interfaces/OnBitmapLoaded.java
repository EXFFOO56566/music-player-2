package com.music.songplayer.Interfaces;

import android.graphics.Bitmap;

/**
 * Created by REYANSH on 18/09/2016.
 */
public interface OnBitmapLoaded {
    void onBitmapLoaded(String mimeType, Bitmap bitmap, byte[] mByteArray);
}
