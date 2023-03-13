package com.music.songplayer.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.music.songplayer.Common;
import com.music.songplayer.FileDirectory.FolderFragment;
import com.music.songplayer.LauncherActivity.MainActivity;
import com.music.songplayer.R;
import com.music.songplayer.Utils.FileUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.io.File;
import java.util.ArrayList;


public class RenameDialog extends DialogFragment {

    private View mView;
    private EditText mEditText;
    private File mFile;
    private File mParentFile;
    private ArrayList<String> mFileNames;
    private Button mPositiveButton;
    private String mFileExt;
    private String mFileName;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_rename, null);
        builder.setView(mView);
        mFile = new File(getArguments().getString("FILE_PATH"));

        if (mFile.isFile()) {
            mFileExt = "." + FileUtils.getFileExtension(mFile.getName());
            mFileName = mFile.getName().substring(0, mFile.getName().lastIndexOf("."));
            if (mFileName.equalsIgnoreCase("")){
                mFileName=mFile.getName();
            }
        } else {
            mFileExt = "";
            mFileName = mFile.getName();
        }

        mParentFile = mFile.getParentFile();
        mFileNames = new ArrayList<>();
        builder.setTitle(R.string.rename);
        for (File file : mParentFile.listFiles()) {
            mFileNames.add(file.getName());
        }

        mEditText = (EditText) mView.findViewById(R.id.edit_text);
        mEditText.setText(mFileName);
        mEditText.setSelection(mFileName.length());
        mEditText.setTypeface(TypefaceHelper.getTypeface(Common.getInstance(), TypefaceHelper.FUTURA_BOOK));

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If there is any other files with the same name. Warn.
                if (mFileNames.contains(s.toString().trim())) {
                    mPositiveButton.setEnabled(false);
                } else {
                    mPositiveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtils.rename(mFile, mEditText.getText().toString() + mFileExt);
                FolderFragment fragment = (FolderFragment) ((MainActivity) RenameDialog.this.getActivity()).mAdapter.getFragment(5);
                fragment.refreshListView();

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPositiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);

    }
}
