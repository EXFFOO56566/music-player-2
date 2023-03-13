package com.music.songplayer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.music.songplayer.AsyncTasks.AsyncTaskDelete;
import com.music.songplayer.Interfaces.OnTaskCompleted;
import com.music.songplayer.Models.Song;
import com.music.songplayer.R;

import java.io.File;
import java.util.ArrayList;

public class DeleteAlertDialog extends DialogFragment {

    private OnTaskCompleted mOnDeletedListener;
    private ArrayList<Song> mFiles;
    private int mPosition;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set the dialog title.
        builder.setTitle(R.string.delete);
        StringBuilder message = new StringBuilder();

        message.append(getString(R.string.these_files_will_be_deleted_permanently) + "\n \n");

        for (int i = 0; i < mFiles.size(); i++) {
            File file = new File(mFiles.get(i)._path);
            message.append(file.getName() + ".\n");
        }

        builder.setMessage(message);

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                DeleteAlertDialog.this.dismiss();
            }
        });

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                DeleteAlertDialog.this.dismiss();
                AsyncTaskDelete asyncTaskDelete = new AsyncTaskDelete(DeleteAlertDialog.this.getActivity());
                asyncTaskDelete.setListener(mOnDeletedListener);
                asyncTaskDelete.execute(mFiles);
            }
        });

        return builder.create();

    }


    public void setTaskCompletionListener(OnTaskCompleted deleteListener) {
        mOnDeletedListener = deleteListener;
    }

    public void setFiles(ArrayList<Song> files) {
        mFiles = files;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}

