package com.music.songplayer.PlayList;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.music.songplayer.Activities.PrivacyPolicyScreen;
import com.music.songplayer.Activities.TracksSubFragment;
import com.music.songplayer.Common;
import com.music.songplayer.LauncherActivity.MainActivity;
import com.music.songplayer.Models.Playlist;
import com.music.songplayer.R;
import com.music.songplayer.Utils.BubbleTextGetter;
import com.music.songplayer.Utils.Constants;
import com.music.songplayer.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Reyansh on 31/07/2016.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> implements BubbleTextGetter {
    private ArrayList<Playlist> data;
    private PlaylistFragment mFragmentPlaylist;

    PlaylistAdapter(PlaylistFragment fragmentPlaylist) {
        mFragmentPlaylist = fragmentPlaylist;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.mPlaylistName.setText(data.get(position)._name);

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void updateData(ArrayList<Playlist> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (data.size() > 0)
            return String.valueOf(data.get(pos)._name.charAt(0));
        else {
            return "-";
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPlaylistName;
        private ImageView mOverFlow;

        public ItemHolder(View itemView) {
            super(itemView);
            mPlaylistName = (TextView) itemView.findViewById(R.id.gridViewTitleText);
            mPlaylistName.setTypeface(TypefaceHelper.getTypeface(itemView.getContext().getApplicationContext(), TypefaceHelper.FUTURA_BOOK));

            mOverFlow = (ImageView) itemView.findViewById(R.id.overflow);
            mOverFlow.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.overflow) {
                mFragmentPlaylist.onPopUpMenuClickListener(v, getAdapterPosition());
                return;
            }

            Random rand = new Random();
            int randomNum = 0 + rand.nextInt(5);

            if (randomNum == 0) {
                if (!Common.getInstance().requestNewInterstitial()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.HEADER_TITLE, data.get(getAdapterPosition())._name);
                    bundle.putString(Constants.HEADER_SUB_TITLE, "");
                    bundle.putString(Constants.FROM_WHERE, "PLAYLISTS");
                    bundle.putLong(Constants.SELECTION_VALUE, data.get(getAdapterPosition())._id);
                    TracksSubFragment tracksSubGridViewFragment = new TracksSubFragment();
                    tracksSubGridViewFragment.setArguments(bundle);
                    ((MainActivity) mFragmentPlaylist.getActivity()).addFragment(tracksSubGridViewFragment);

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
                            bundle.putString(Constants.HEADER_TITLE, data.get(getAdapterPosition())._name);
                            bundle.putString(Constants.HEADER_SUB_TITLE, "");
                            bundle.putString(Constants.FROM_WHERE, "PLAYLISTS");
                            bundle.putLong(Constants.SELECTION_VALUE, data.get(getAdapterPosition())._id);
                            TracksSubFragment tracksSubGridViewFragment = new TracksSubFragment();
                            tracksSubGridViewFragment.setArguments(bundle);
                            ((MainActivity) mFragmentPlaylist.getActivity()).addFragment(tracksSubGridViewFragment);

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
                bundle.putString(Constants.HEADER_TITLE, data.get(getAdapterPosition())._name);
                bundle.putString(Constants.HEADER_SUB_TITLE, "");
                bundle.putString(Constants.FROM_WHERE, "PLAYLISTS");
                bundle.putLong(Constants.SELECTION_VALUE, data.get(getAdapterPosition())._id);
                TracksSubFragment tracksSubGridViewFragment = new TracksSubFragment();
                tracksSubGridViewFragment.setArguments(bundle);
                ((MainActivity) mFragmentPlaylist.getActivity()).addFragment(tracksSubGridViewFragment);

            }



        }

    }
}
