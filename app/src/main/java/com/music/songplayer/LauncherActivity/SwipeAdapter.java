package com.music.songplayer.LauncherActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.music.songplayer.Album.AlbumFragment;
import com.music.songplayer.Artist.FragmentArtist;
import com.music.songplayer.FileDirectory.FolderFragment;
import com.music.songplayer.Genres.FragmentGenres;
import com.music.songplayer.PlayList.PlaylistFragment;
import com.music.songplayer.Songs.SongsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwipeAdapter extends FragmentPagerAdapter {
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private String mPageTile[];
    private ArrayList<Fragment> fragments;

    public SwipeAdapter(FragmentManager fm, String pageTiles[]) {
        super(fm);
        mPageTile=pageTiles;
        fragments = new ArrayList<>();

        mFragmentManager = fm;
        mFragmentTags = new HashMap<>();

        for (String tab : mPageTile) {
            if (tab.equalsIgnoreCase("ALBUMS")) {
                fragments.add(new AlbumFragment());
            } else if (tab.equalsIgnoreCase("ARTISTS")) {
                fragments.add(new FragmentArtist());
            } else if (tab.equalsIgnoreCase("PLAYLISTS")) {
                fragments.add(new PlaylistFragment());
            } else if (tab.equalsIgnoreCase("SONGS")) {
                fragments.add(new SongsFragment());
            } else if (tab.equalsIgnoreCase("DIRECTORY")) {
                fragments.add(new FolderFragment());
            } else if (tab.equalsIgnoreCase("GENRES")) {
                fragments.add(new FragmentGenres());
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTile[position];
    }

    @Override
    public int getCount() {
        return 6;
    }
}