package com.music.songplayer.Activities;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.songplayer.Common;
import com.music.songplayer.Interfaces.OnAdapterItemClicked;
import com.music.songplayer.Models.Song;
import com.music.songplayer.NowPlaying.NowPlayingActivity;
import com.music.songplayer.R;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;


public class SubListViewAdapter extends RecyclerView.Adapter<SubListViewAdapter.SongHolder> implements BubbleTextGetter {

    private Context mContext;
    private OnAdapterItemClicked mAdapterClickListener;
    private Common mApp;
    private ArrayList<Song> mData;


    public SubListViewAdapter(Context context, ArrayList<Song> data, OnAdapterItemClicked listener) {
        mContext = context;
        mApp = (Common) mContext.getApplicationContext();
        this.mData = data;
        mAdapterClickListener = listener;
    }


    @Override
    public SubListViewAdapter.SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_sub_browser_item, parent, false);
        return new SubListViewAdapter.SongHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubListViewAdapter.SongHolder holder, int position) {
        holder.title.setText(mData.get(position)._title);
        holder.artist.setText(mData.get(position)._artist);
        holder.mTrackNo.setText(mData.get(position)._trackNumber > 0 ? "" + mData.get(position)._trackNumber : "");
        holder.duration.setText(MusicUtils.makeShortTimeString(mContext, mData.get(position)._duration / 1000));


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void update(ArrayList<Song> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return String.valueOf(mData.get(pos)._title.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    /**
    *Create the {@link RecyclerView.ViewHolder } class under the adapter to avoid
     * unnecessary confusion.
    */

    class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView mTrackNo;
        private TextView artist;
        private TextView duration;
        private ImageView mOverFlow;

        public SongHolder(View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.listViewTitleText);
            artist =  itemView.findViewById(R.id.listViewSubText);
            duration =  itemView.findViewById(R.id.listViewRightSubText);
            mTrackNo =  itemView.findViewById(R.id.listViewTrackNumber);

            /**
            *Never ever set type face in {@link #onBindViewHolder(ViewHolder, int, List)}
             * it will mess up the performance.
            */
            title.setTypeface(TypefaceHelper.getTypeface(mContext, TypefaceHelper.FUTURA_BOOK));
            artist.setTypeface(TypefaceHelper.getTypeface(mContext, TypefaceHelper.FUTURA_BOOK));
            duration.setTypeface(TypefaceHelper.getTypeface(mContext, TypefaceHelper.FUTURA_BOOK));

            mOverFlow = (ImageView) itemView.findViewById(R.id.listViewOverflow);
            mOverFlow.setVisibility(View.VISIBLE);
            mOverFlow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.listViewOverflow) {
                if (mAdapterClickListener != null) {
                    mAdapterClickListener.OnPopUpMenuClicked(v, getAdapterPosition());
                    return;
                }
            }
            mApp.getPlayBackStarter().playSongs(mData, getAdapterPosition());
            mContext.startActivity(new Intent(mContext, NowPlayingActivity.class));
        }
    }


}
