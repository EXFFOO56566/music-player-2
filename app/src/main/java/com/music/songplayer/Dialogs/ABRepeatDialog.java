package com.music.songplayer.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.music.songplayer.Common;
import com.music.songplayer.NowPlaying.NowPlayingActivity;
import com.music.songplayer.R;
import com.music.songplayer.Utils.Constants;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.PreferencesHelper;
import com.music.songplayer.Utils.TypefaceHelper;
import com.music.songplayer.Views.RangeSeekBar;


public class ABRepeatDialog extends DialogFragment {


    private Common mApp;

    private int repeatPointA;
    private int repeatPointB;

    private int currentSongDurationMillis;

    private TextView repeatSongATime;
    private TextView repeatSongBTime;
    private SeekBar mSeekBar;

    private RangeSeekBar<Integer> mRangeSeekBar;
    private ViewGroup viewGroup;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mApp = (Common) getActivity().getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.a_b_repeat);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_ab_repeat, null);
        mSeekBar = view.findViewById(R.id.repeat_song_range_placeholder_seekbar);

        repeatSongATime = view.findViewById(R.id.repeat_song_range_A_time);
        repeatSongBTime = view.findViewById(R.id.repeat_song_range_B_time);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSeekBar.getLayoutParams();
        viewGroup = (ViewGroup) mSeekBar.getParent();
        viewGroup.removeView(mSeekBar);


        repeatSongATime.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));
        repeatSongBTime.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));

        TextView textView = view.findViewById(R.id.repeat_song_range_instructions);
        textView.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));

        currentSongDurationMillis = mApp.getService().getMediaPlayer().getDuration();

        mRangeSeekBar = new RangeSeekBar<>(0, currentSongDurationMillis, getActivity().getApplicationContext());

        mRangeSeekBar.setLayoutParams(params);
        viewGroup.addView(mRangeSeekBar);


        if (PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.REPEAT_MODE, Constants.REPEAT_OFF) == Constants.A_B_REPEAT) {

            repeatSongATime.setText(MusicUtils.convertMillisToMinsSecs(mApp.getService().getRepeatSongRangePointA()));
            repeatSongBTime.setText(MusicUtils.convertMillisToMinsSecs(mApp.getService().getRepeatSongRangePointB()));


            repeatPointA = mApp.getService().getRepeatSongRangePointA();
            repeatPointB = mApp.getService().getRepeatSongRangePointB();

            mRangeSeekBar.setSelectedMinValue(repeatPointA);
            mRangeSeekBar.setSelectedMaxValue(repeatPointB);

        } else {
            repeatSongATime.setText("0:00");
            repeatSongBTime.setText(MusicUtils.convertMillisToMinsSecs(currentSongDurationMillis));

            repeatPointA = 0;
            repeatPointB = currentSongDurationMillis;
        }

        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                repeatPointA = minValue;
                repeatPointB = maxValue;
                repeatSongATime.setText(MusicUtils.convertMillisToMinsSecs(minValue));
                repeatSongBTime.setText(MusicUtils.convertMillisToMinsSecs(maxValue));
            }
        });

        repeatSongATime.setText(MusicUtils.convertMillisToMinsSecs(repeatPointA));
        repeatSongBTime.setText(MusicUtils.convertMillisToMinsSecs(repeatPointB));


        builder.setView(view);

        builder.setPositiveButton(R.string.repeat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.REPEAT_MODE, Constants.A_B_REPEAT);
                mApp.getService().setRepeatSongRange(repeatPointA, repeatPointB);
                ((NowPlayingActivity) ABRepeatDialog.this.getActivity()).applyRepeatButton();
                ABRepeatDialog.this.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }


}
