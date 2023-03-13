package com.music.songplayer.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.music.songplayer.R;
import com.music.songplayer.TagEditor.Id3TagEditorActivity;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;


@SuppressLint("ValidFragment")
public class PermissionToEditSdCardDialog extends DialogFragment {

    private Activity mActivity;
    private Fragment mFragment;


    public PermissionToEditSdCardDialog(Activity context) {
        mActivity = context;
    }

    @SuppressLint("ValidFragment")
    public PermissionToEditSdCardDialog(Fragment context) {
        mFragment = context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.ask_for_permission_dailog, null);

        TextView textView1 = (TextView) view.findViewById(R.id.text_line_number_1);
        TextView textView2 = (TextView) view.findViewById(R.id.text_line_number_2);
        TextView textView3 = (TextView) view.findViewById(R.id.text_line_number_3);
        TextView textView4 = (TextView) view.findViewById(R.id.text_line_number_4);
        TextView textView5 = (TextView) view.findViewById(R.id.text_line_number_5);
        TextView textView6 = (TextView) view.findViewById(R.id.text_line_number_6);
        TextView textView7 = (TextView) view.findViewById(R.id.text_line_number_7);

        textView1.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));
        textView2.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), TypefaceHelper.FUTURA_BOLD));
        textView3.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));
        textView4.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));
        textView5.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));

        textView6.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), TypefaceHelper.FUTURA_BOLD));
        textView7.setTypeface(TypefaceHelper.getTypeface(getActivity().getApplicationContext(), "Futura-Condensed-Font"));

        builder.setView(view);
        builder.setTitle(R.string.grant_permission);

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (PermissionToEditSdCardDialog.this.getActivity() instanceof Id3TagEditorActivity) {
                    PermissionToEditSdCardDialog.this.getActivity().finish();
                }
            }
        });


        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                if (mFragment == null) {
                    mActivity.startActivityForResult(intent, MusicUtils.URI_REQUEST_CODE_DELETE);
                } else {
                    mFragment.startActivityForResult(intent, MusicUtils.URI_REQUEST_CODE_DELETE);
                }
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
