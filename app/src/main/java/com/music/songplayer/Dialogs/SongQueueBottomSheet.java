package com.music.songplayer.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.music.songplayer.Adapters.QueueAdapter;
import com.music.songplayer.Common;
import com.music.songplayer.NowPlaying.NowPlayingActivity;
import com.music.songplayer.R;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.PreferencesHelper;
import com.music.songplayer.Utils.helper.OnStartDragListener;
import com.music.songplayer.Utils.helper.SimpleItemTouchHelperCallback;
import com.music.songplayer.Views.FastScroller;

public class SongQueueBottomSheet extends BottomSheetDialogFragment implements OnStartDragListener {

    private Common mApp;
    private QueueAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View mView;
    private FastScroller mFastScroller;
    private ItemTouchHelper mItemTouchHelper;
    private Context mContext;
    private ImageButton mBackImageButton;
    private ImageButton mOverflowButton;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        mView = getActivity().getLayoutInflater().inflate(R.layout.layout_bottomsheet_queue, null, false);


        mContext = getActivity().getApplicationContext();
        mApp = (Common) getActivity().getApplicationContext();

        setHasOptionsMenu(true);
        mApp = (Common) getActivity().getApplicationContext();

        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mFastScroller = mView.findViewById(R.id.fast_scroller);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mBackImageButton = mView.findViewById(R.id.image_back_button);
        mOverflowButton = (ImageButton) mView.findViewById(R.id.image_button_overflow);

        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongQueueBottomSheet.this.dismiss();
            }
        });

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                BottomSheetDialog d = (BottomSheetDialog) dialog1;
                View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(SongQueueBottomSheet.this.getActivity(), v);
                popupMenu.inflate(R.menu.menu_playlist);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_clear) {

                            mApp.getService().getSongList().clear();
                            mApp.getService().setSongPos(0);
                            mApp.getService().stopSelf();
                            PreferencesHelper.getInstance().put(PreferencesHelper.Key.CURRENT_SONG_POSITION, 0);
                            PreferencesHelper.getInstance().put(PreferencesHelper.Key.SONG_CURRENT_SEEK_DURATION, 0);
                            PreferencesHelper.getInstance().put(PreferencesHelper.Key.SONG_TOTAL_SEEK_DURATION, 0);
                            SongQueueBottomSheet.this.getActivity().finish();

                        } else if (item.getItemId() == R.id.menu_save) {

                            PlaylistDialog playlistDialog = new PlaylistDialog();
                            Bundle bundle = new Bundle();
                            bundle.putLongArray("PLAYLIST_IDS", MusicUtils.getPlayListIds(mApp.getService().getSongList()));
                            playlistDialog.setArguments(bundle);
                            playlistDialog.show(SongQueueBottomSheet.this.getActivity().getSupportFragmentManager(), "FRAGMENT_TAG");

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        mAdapter = new QueueAdapter((NowPlayingActivity) getActivity(), ((NowPlayingActivity) getActivity()).mSongs, this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mFastScroller.setRecyclerView(mRecyclerView);
        if (mApp.isServiceRunning()) {
            mRecyclerView.getLayoutManager().scrollToPosition(mApp.getService().getCurrentSongIndex());
        } else {
            int pos = PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.CURRENT_SONG_POSITION, 0);
            mRecyclerView.getLayoutManager().scrollToPosition(pos);
        }

        dialog.setContentView(mView);

        ((View) mView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void removeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public QueueAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
