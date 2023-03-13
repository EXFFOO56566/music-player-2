package com.music.songplayer.Artist;

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
import com.music.songplayer.Common;
import com.music.songplayer.LauncherActivity.MainActivity;
import com.music.songplayer.Models.Artist;
import com.music.songplayer.R;
import com.music.songplayer.SubGridViewFragment.TracksSubGridViewFragment;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.Constants;
import com.music.songplayer.Utils.CursorHelper;
import com.music.songplayer.Utils.Logger;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.Random;


public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ItemHolder> implements BubbleTextGetter {

    private ArrayList<Artist> mData;
    private FragmentArtist mFragmentArtist;
    private Common mApp;

    public ArtistsAdapter(FragmentArtist mFragmentArtist) {
        this.mFragmentArtist = mFragmentArtist;
        mApp = (Common) Common.getInstance().getApplicationContext();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.mArtistName.setText(mData.get(position)._artistName);

        ImageLoader.getInstance().displayImage(mData.get(position)._artistAlbumArt, holder.mArtistImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
               // holder.mArtistImage.setImageResource(R.drawable.ic_placeholder);
                int padding = MusicUtils.getDPFromPixel(45);
                holder.mArtistImage.setPadding(padding, padding, padding, padding);
                //holder.mArtistImage.setBackgroundColor(R.color.teal);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mArtistImage.setPadding(0, 0, 0, 0);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Logger.log("CANCELLED:-" + imageUri);
            }
        });


        String nooftracks = MusicUtils.makeLabel(mFragmentArtist.getActivity().getApplicationContext(), R.plurals.Nsongs, mData.get(position)._noOfTracksByArtist);
        String noofalbums = MusicUtils.makeLabel(mFragmentArtist.getActivity().getApplicationContext(), R.plurals.Nalbums, mData.get(position)._noOfAlbumsByArtist);
        holder.mDetails.setText(nooftracks + " | " + noofalbums);


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return String.valueOf(mData.get(pos)._artistName.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }


    public void updateData(ArrayList<Artist> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mArtistName;
        private TextView mDetails;
        private ImageView mArtistImage;
        private ImageView mOverFlowImageView;

        public ItemHolder(View itemView) {
            super(itemView);

           int mWidth =  Common.getItemWidth();

            mArtistName =  itemView.findViewById(R.id.gridViewTitleText);
            mDetails =  itemView.findViewById(R.id.gridViewSubText);
            mArtistImage = itemView.findViewById(R.id.gridViewImage);

            mArtistName.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
            mDetails.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mArtistImage.getLayoutParams();
            params.width = mWidth;
            params.height = mWidth;
            mArtistImage.setLayoutParams(params);


            mOverFlowImageView = itemView.findViewById(R.id.overflow);
            mOverFlowImageView.setVisibility(View.VISIBLE);
            mOverFlowImageView.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.overflow) {
                mFragmentArtist.onPopUpMenuClickListener(v, getAdapterPosition());
                return;
            }
            if (mFragmentArtist.checkAlbumsEmpty(CursorHelper.getAlbumsForSelection("ARTIST", "" + mData.get(getAdapterPosition())._artistId), getAdapterPosition()))
                return;

            Random rand = new Random();
            int randomNum = 0 + rand.nextInt(5);

            if (randomNum == 0) {
                if (!Common.getInstance().requestNewInterstitial()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._artistName);
                    bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsByArtist);
                    bundle.putString(Constants.FROM_WHERE, "ARTIST");
                    bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._artistAlbumArt);
                    bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._artistId);

                    TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                    tracksSubGridViewFragment.setArguments(bundle);
                    ((MainActivity) mFragmentArtist.getActivity()).addFragment(tracksSubGridViewFragment);

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
                            bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._artistName);
                            bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsByArtist);
                            bundle.putString(Constants.FROM_WHERE, "ARTIST");
                            bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._artistAlbumArt);
                            bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._artistId);

                            TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                            tracksSubGridViewFragment.setArguments(bundle);
                            ((MainActivity) mFragmentArtist.getActivity()).addFragment(tracksSubGridViewFragment);

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
                bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._artistName);
                bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsByArtist);
                bundle.putString(Constants.FROM_WHERE, "ARTIST");
                bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._artistAlbumArt);
                bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._artistId);

                TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                tracksSubGridViewFragment.setArguments(bundle);
                ((MainActivity) mFragmentArtist.getActivity()).addFragment(tracksSubGridViewFragment);
            }


        }
    }

}
