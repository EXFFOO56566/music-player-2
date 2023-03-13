package com.music.songplayer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.View;

import com.github.channguyen.rsv.RangeSliderView;
import com.music.songplayer.Common;
import com.music.songplayer.R;
import com.music.songplayer.Utils.PreferencesHelper;

public class RecentlyAddedDialog extends DialogFragment {

    private RangeSliderView mSmallSlider;
    private int mNoOfWeeks;
    private Common mApp;
    private AlertDialog mAlertDialog;
    final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
        @Override
        public void onSlide(int index) {
            mNoOfWeeks = index + 1;
            if (mNoOfWeeks == 1)
                mAlertDialog.setMessage(mNoOfWeeks + " " + "Week");
            else
                mAlertDialog.setMessage(mNoOfWeeks + " " + "Weeks");
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_recentlyadded, null);
        mSmallSlider = (RangeSliderView) view.findViewById(R.id.rsv_small);
        mApp = (Common) getActivity().getApplicationContext();
        builder.setTitle(R.string.recently_added_desc);

        if (PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, 1) != 0) {
            mSmallSlider.setInitialIndex(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, 1) - 1);
        } else {
            mSmallSlider.setInitialIndex(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, 1));
        }

        if (mNoOfWeeks == 1) {
            builder.setMessage(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, 1) + " " + "Week");
        } else {
            builder.setMessage(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, 1) + " " + "Weeks");

        }


        mSmallSlider.setOnSlideListener(listener);


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.RECENTLY_ADDED_WEEKS, mNoOfWeeks);
                RecentlyAddedDialog.this.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecentlyAddedDialog.this.dismiss();
            }
        });
        builder.setView(view);
        mAlertDialog = builder.create();
        return mAlertDialog;
    }
}
