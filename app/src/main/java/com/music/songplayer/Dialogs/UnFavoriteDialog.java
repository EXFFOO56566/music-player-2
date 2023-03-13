package com.music.songplayer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.widget.Toast;

import com.music.songplayer.Common;
import com.music.songplayer.Database.DataBaseHelper;
import com.music.songplayer.R;

public class UnFavoriteDialog extends DialogFragment {


    private Common mApp;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mApp = (Common) getActivity().getApplicationContext();
        builder.setTitle(R.string.unfavorite);
        builder.setMessage(R.string.unfavorite_long);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mApp.getDBAccessHelper().getWritableDatabase().delete(DataBaseHelper.FAVORITES_TABLE, null, null);
                Toast.makeText(UnFavoriteDialog.this.getActivity(), R.string.all_favorites_song_removed_from_playlist, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
