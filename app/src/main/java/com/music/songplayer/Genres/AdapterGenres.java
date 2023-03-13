package com.music.songplayer.Genres;

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
import com.music.songplayer.Models.Genre;
import com.music.songplayer.R;
import com.music.songplayer.SubGridViewFragment.TracksSubGridViewFragment;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.Constants;
import com.music.songplayer.Utils.CursorHelper;
import com.music.songplayer.Utils.MusicUtils;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.Random;


public class AdapterGenres extends RecyclerView.Adapter<AdapterGenres.ItemHolder> implements BubbleTextGetter {
    private ArrayList<Genre> mData;
    private FragmentGenres mFragmentGenres;

    public AdapterGenres(FragmentGenres mFragmentGenres) {
        this.mFragmentGenres = mFragmentGenres;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.title.setText(mData.get(position)._genreName);
        ImageLoader.getInstance().displayImage(mData.get(position)._genreAlbumArt, holder.albumart, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
               // holder.albumart.setImageResource(R.drawable.ic_placeholder);
                int padding = MusicUtils.getDPFromPixel(45);
                holder.albumart.setPadding(padding, padding, padding, padding);
               // holder.albumart.setBackgroundColor(R.color.amber);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.albumart.setPadding(0, 0, 0, 0);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        try {
            holder.subTitle.setText(mData.get(position)._genreName);
            String noofalbums = MusicUtils.makeLabel(mFragmentGenres.getActivity().getApplicationContext(), R.plurals.Nalbums, mData.get(position)._noOfAlbumsInGenre);
            holder.subTitle.setText(noofalbums);
        } catch (Exception e) {
            e.printStackTrace();
            holder.subTitle.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return String.valueOf(mData.get(pos)._genreName.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
    }


    public void updateData(ArrayList<Genre> data) {
        this.mData = data;
        notifyDataSetChanged();
    }


    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView subTitle;
        private ImageView albumart;
        private ImageView mOverFlow;

        public ItemHolder(View itemView) {
            super(itemView);
            int mWidth =  Common.getItemWidth();

            title =  itemView.findViewById(R.id.gridViewTitleText);
            subTitle =  itemView.findViewById(R.id.gridViewSubText);
            albumart =  itemView.findViewById(R.id.gridViewImage);

            title.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));
            subTitle.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));


            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) albumart.getLayoutParams();
            params.width = mWidth;
            params.height = mWidth;
            albumart.setLayoutParams(params);

            mOverFlow =  itemView.findViewById(R.id.overflow);
            mOverFlow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.overflow) {
                mFragmentGenres.onPopUpMenuClickListener(v, getAdapterPosition());
                return;
            }

            if (mFragmentGenres.checkIfAlbumsEmpty(CursorHelper.getAlbumsForSelection("GENRES", "" + mData.get(getAdapterPosition())._genreId), getAdapterPosition())) {
                return;
            }

            Random rand = new Random();
            int randomNum = 0 + rand.nextInt(5);

            if (randomNum == 0) {
                if (!Common.getInstance().requestNewInterstitial()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._genreName);
                    bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsInGenre);
                    bundle.putString(Constants.FROM_WHERE, "GENRES");
                    bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._genreAlbumArt);
                    bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._genreId);
                    TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                    tracksSubGridViewFragment.setArguments(bundle);
                    ((MainActivity) mFragmentGenres.getActivity()).addFragment(tracksSubGridViewFragment);

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
                            bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._genreName);
                            bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsInGenre);
                            bundle.putString(Constants.FROM_WHERE, "GENRES");
                            bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._genreAlbumArt);
                            bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._genreId);
                            TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                            tracksSubGridViewFragment.setArguments(bundle);
                            ((MainActivity) mFragmentGenres.getActivity()).addFragment(tracksSubGridViewFragment);

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
                bundle.putString(Constants.HEADER_TITLE, mData.get(getAdapterPosition())._genreName);
                bundle.putInt(Constants.HEADER_SUB_TITLE, mData.get(getAdapterPosition())._noOfAlbumsInGenre);
                bundle.putString(Constants.FROM_WHERE, "GENRES");
                bundle.putString(Constants.COVER_PATH, mData.get(getAdapterPosition())._genreAlbumArt);
                bundle.putLong(Constants.SELECTION_VALUE, mData.get(getAdapterPosition())._genreId);
                TracksSubGridViewFragment tracksSubGridViewFragment = new TracksSubGridViewFragment();
                tracksSubGridViewFragment.setArguments(bundle);
                ((MainActivity) mFragmentGenres.getActivity()).addFragment(tracksSubGridViewFragment);

            }


        }
    }
}