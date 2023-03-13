package com.music.songplayer.Songs;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.music.songplayer.Activities.PrivacyPolicyScreen;
import com.music.songplayer.Common;
import com.music.songplayer.Interfaces.OnAdapterItemClicked;
import com.music.songplayer.Models.Song;
import com.music.songplayer.NowPlaying.NowPlayingActivity;
import com.music.songplayer.R;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by REYANSH on 4/21/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemHolder> implements BubbleTextGetter {

    private Context mContext;
    private OnAdapterItemClicked mAdapterClickListener;
    private Common mApp;
    private ArrayList<Song> mData;

    public SongsAdapter(Context context, OnAdapterItemClicked listener) {
        mContext = context;
        mApp = (Common) mContext.getApplicationContext();
        mAdapterClickListener = listener;
        mData = new ArrayList<>();
    }

    @Override
    public SongsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        return new SongsAdapter.SongHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.ItemHolder itemHolder, int position) {
        switch (itemHolder.getItemViewType()) {
            default:
                SongsAdapter.SongHolder holder = (SongsAdapter.SongHolder) itemHolder;
                holder.title.setText(mData.get(position)._title);
                holder.artist.setText(mData.get(position)._artist);
                holder.duration.setText(MusicUtils.makeShortTimeString(mContext, mData.get(position)._duration / 1000));
                //holder.mAlbumArt.setVisibility(View.VISIBLE);
               // ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(mData.get(position)._albumId).toString(), holder.mAlbumArt);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void update(ArrayList<Song> data) {
        mData.clear();
        mData.addAll(data);
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


    class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemView) {
            super(itemView);
        }
    }


    class SongHolder extends SongsAdapter.ItemHolder implements View.OnClickListener {

        private TextView title;
        private TextView artist;
        private ImageView mAlbumArt;
        private TextView duration;
        private ImageView mOverFlow;

        public SongHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listViewTitleText);
            artist = (TextView) itemView.findViewById(R.id.listViewSubText);
            mAlbumArt = (ImageView) itemView.findViewById(R.id.listViewLeftIcon);
            duration = (TextView) itemView.findViewById(R.id.listViewRightSubText);

            title.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
            artist.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
            duration.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));


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


            Random rand = new Random();
            int randomNum = 0 + rand.nextInt(5);

            if (randomNum == 0) {
                if (!Common.getInstance().requestNewInterstitial()) {

                    mApp.getPlayBackStarter().playSongs(mData, getAdapterPosition());
                    mContext.startActivity(new Intent(mContext, NowPlayingActivity.class));

                } else {

                    Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();

                            Common.getInstance().mInterstitialAd.setAdListener(null);
                            Common.getInstance().mInterstitialAd = null;
                            Common.getInstance().ins_adRequest = null;
                            Common.getInstance().LoadAds();

                            mApp.getPlayBackStarter().playSongs(mData, getAdapterPosition());
                            mContext.startActivity(new Intent(mContext, NowPlayingActivity.class));
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    });
                }
            } else {

                mApp.getPlayBackStarter().playSongs(mData, getAdapterPosition());
                mContext.startActivity(new Intent(mContext, NowPlayingActivity.class));
            }

        }
    }
}
