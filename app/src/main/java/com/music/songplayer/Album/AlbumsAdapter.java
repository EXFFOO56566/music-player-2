package com.music.songplayer.Album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.music.songplayer.Activities.PrivacyPolicyScreen;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.music.songplayer.Activities.TracksSubFragment;
import com.music.songplayer.Common;
import com.music.songplayer.LauncherActivity.MainActivity;
import com.music.songplayer.Models.Album;
import com.music.songplayer.R;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.Constants;
import com.music.songplayer.Utils.CursorHelper;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.Random;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ItemHolder> implements BubbleTextGetter {

    private ArrayList<Album> mData;
    private AlbumFragment mAlbumFragment;

    public AlbumsAdapter(AlbumFragment albumFragment) {
        mAlbumFragment = albumFragment;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.albumName.setText(mData.get(position)._albumName);
        holder.artistName.setText(mData.get(position)._artistName);
        ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(mData.get(position)._Id).toString(), holder.albumart, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }


            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //holder.albumart.setImageResource(R.drawable.ic_placeholder);
                int padding = MusicUtils.getDPFromPixel(45);
                holder.albumart.setPadding(padding, padding, padding, padding);
                //holder.albumart.setBackgroundColor(R.color.blue_gray);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.albumart.setPadding(0, 0, 0, 0);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void updateData(ArrayList<Album> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return String.valueOf(mData.get(pos)._albumName.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }

    @Override
    public void onViewDetachedFromWindow(ItemHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView albumName;
        private TextView artistName;
        private ImageView albumart;
        private ImageView mOverFlow;

        public ItemHolder(View itemView) {
            super(itemView);
            int mWidth = Common.getItemWidth();

            albumName = itemView.findViewById(R.id.gridViewTitleText);
            artistName = itemView.findViewById(R.id.gridViewSubText);
            albumart = itemView.findViewById(R.id.gridViewImage);

            albumName.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
            artistName.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) albumart.getLayoutParams();
            params.width = mWidth;
            params.height = mWidth;
            albumart.setLayoutParams(params);


            mOverFlow = itemView.findViewById(R.id.overflow);
            mOverFlow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.overflow) {
                mAlbumFragment.onPopUpMenuClickListener(v, getAdapterPosition());
                return;
            }
            if (mAlbumFragment.checkIfAlbumsEmpty(CursorHelper.getTracksForSelection("ALBUMS", "" + mData.get(getAdapterPosition())._Id), getAdapterPosition())) {
                return;
            }

            Random rand = new Random();
            int randomNum = 0 + rand.nextInt(5);

            if (randomNum == 0) {
                if (!Common.getInstance().requestNewInterstitial()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._albumName);
                    bundle.putString(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._artistName);
                    bundle.putString(Constants.FROM_WHERE, "ALBUMS");
                    bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._Id);
                    TracksSubFragment tracksSubFragment = new TracksSubFragment();
                    tracksSubFragment.setArguments(bundle);
                    ((MainActivity) mAlbumFragment.getActivity()).addFragment(tracksSubFragment);

                } else {

                    Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();

                            Common.getInstance().mInterstitialAd.setAdListener(null);
                            Common.getInstance().mInterstitialAd = null;
                            Common.getInstance().ins_adRequest = null;
                            Common.getInstance().LoadAds();

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._albumName);
                            bundle.putString(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._artistName);
                            bundle.putString(Constants.FROM_WHERE, "ALBUMS");
                            bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._Id);
                            TracksSubFragment tracksSubFragment = new TracksSubFragment();
                            tracksSubFragment.setArguments(bundle);
                            ((MainActivity) mAlbumFragment.getActivity()).addFragment(tracksSubFragment);

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

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._albumName);
                bundle.putString(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._artistName);
                bundle.putString(Constants.FROM_WHERE, "ALBUMS");
                bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._Id);
                TracksSubFragment tracksSubFragment = new TracksSubFragment();
                tracksSubFragment.setArguments(bundle);
                ((MainActivity) mAlbumFragment.getActivity()).addFragment(tracksSubFragment);

            }


        }
    }
}

