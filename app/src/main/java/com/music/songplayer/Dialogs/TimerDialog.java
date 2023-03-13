package com.music.songplayer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.music.songplayer.R;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.Timer;

public class TimerDialog extends DialogFragment {


    private TextView mTimerTextView;
    private Timer mTimer;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_timer, null);
        mTimerTextView = (TextView) view.findViewById(R.id.text_view_timer_dialog);

        mTimerTextView.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
        builder.setView(view);

        builder.setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(R.string.stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }
}
