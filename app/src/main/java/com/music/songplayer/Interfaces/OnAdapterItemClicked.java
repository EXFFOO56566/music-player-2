package com.music.songplayer.Interfaces;

import android.view.View;

/**
 * Created by REYANSH on 3/14/2017.
 */

public interface OnAdapterItemClicked {
    void OnPopUpMenuClicked(View view, int position);

    void OnShuffledClicked();
}
