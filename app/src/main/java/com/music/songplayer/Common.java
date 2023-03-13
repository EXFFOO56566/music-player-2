
package com.music.songplayer;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.Settings;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
//import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.L;
import com.music.songplayer.Database.DataBaseHelper;
import com.music.songplayer.GoogleAnalytics.AnalyticsTrackers;
import com.music.songplayer.NowPlaying.NowPlayingActivity;
import com.music.songplayer.PlayBackStarter.PlayBackStarter;
import com.music.songplayer.Services.MusicService;
import com.music.songplayer.Utils.PreferencesHelper;


public class Common extends MultiDexApplication {

    public PublisherInterstitialAd mInterstitialAd;
    public PublisherAdRequest ins_adRequest;


    private static Common appInstance;

    /**
     * Enable or disable debugging and TAG
     */

    //Device orientation constants.
    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;
    //Device screen size/orientation identifiers.

    public static final String REGULAR = "regular";
    public static final String SMALL_TABLET = "small_tablet";
    public static final String LARGE_TABLET = "large_tablet";
    public static final String XLARGE_TABLET = "xlarge_tablet";
    public static final int REGULAR_SCREEN_PORTRAIT = 0;
    public static final int REGULAR_SCREEN_LANDSCAPE = 1;
    public static final int SMALL_TABLET_PORTRAIT = 2;
    public static final int SMALL_TABLET_LANDSCAPE = 3;
    public static final int LARGE_TABLET_PORTRAIT = 4;
    public static final int LARGE_TABLET_LANDSCAPE = 5;
    public static final int XLARGE_TABLET_PORTRAIT = 6;
    public static final int XLARGE_TABLET_LANDSCAPE = 7;
    /**
     * Constant and service instance
     */
    private static Common mContext;
    /**
     * UIL options
     */

    public DisplayImageOptions options = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .build();

    private MusicService mService;
    /**
     * DataBase instance
     */

    private boolean mIsServiceRunning = false;


    /**
     * {@link PlayBackStarter} handles all the playing and pausing adding to queue etc oprations
     */
    private PlayBackStarter mPlayBackStarter;

    /**
     * NowPlaying activity
     */


    /**
     * Firebase instance for crash report and logging
     */


    public static synchronized Common getInstance() {
        return mContext;
    }

    /**
     * Returns the orientation of the device.
     */
    public static int getOrientation() {
        if (mContext.getResources().getDisplayMetrics().widthPixels >
                mContext.getResources().getDisplayMetrics().heightPixels) {
            return ORIENTATION_LANDSCAPE;
        } else {
            return ORIENTATION_PORTRAIT;
        }
    }

    /**
     * Returns the current screen configuration of the device.
     */
    public static int getDeviceScreenConfiguration() {
        String screenSize = mContext.getResources().getString(R.string.screen_size);
        boolean landscape = false;
        if (getOrientation() == ORIENTATION_LANDSCAPE) {
            landscape = true;
        }

        if (screenSize.equals(REGULAR) && !landscape)
            return REGULAR_SCREEN_PORTRAIT;
        else if (screenSize.equals(REGULAR) && landscape)
            return REGULAR_SCREEN_LANDSCAPE;
        else if (screenSize.equals(SMALL_TABLET) && !landscape)
            return SMALL_TABLET_PORTRAIT;
        else if (screenSize.equals(SMALL_TABLET) && landscape)
            return SMALL_TABLET_LANDSCAPE;
        else if (screenSize.equals(LARGE_TABLET) && !landscape)
            return LARGE_TABLET_PORTRAIT;
        else if (screenSize.equals(LARGE_TABLET) && landscape)
            return LARGE_TABLET_LANDSCAPE;
        else if (screenSize.equals(XLARGE_TABLET) && !landscape)
            return XLARGE_TABLET_PORTRAIT;
        else if (screenSize.equals(XLARGE_TABLET) && landscape)
            return XLARGE_TABLET_LANDSCAPE;
        else
            return REGULAR_SCREEN_PORTRAIT;
    }

    public static String convertMillisToMinsSecs(int milliseconds) {

        int secondsValue = milliseconds / 1000 % 60;
        int minutesValue = (milliseconds / (1000 * 60)) % 60;
        int hoursValue = (milliseconds / (1000 * 60 * 60)) % 24;

        String seconds = "";
        String minutes = "";
        String hours = "";

        if (secondsValue < 10) {
            seconds = "0" + secondsValue;
        } else {
            seconds = "" + secondsValue;
        }

        if (minutesValue < 10) {
            minutes = "0" + minutesValue;
        } else {
            minutes = "" + minutesValue;
        }

        if (hoursValue < 10) {
            hours = "0" + hoursValue;
        } else {
            hours = "" + hoursValue;
        }

        String output = "";
        if (hoursValue != 0) {
            output = hours + ":" + minutes + ":" + seconds;
        } else {
            output = minutes + ":" + seconds;
        }

        return output;
    }


   /* public static int getVersionCode() {
        try {
            PackageInfo pInfo = Common.getInstance().getPackageManager().getPackageInfo(Common.getInstance().getPackageName(), 0);
            int version = pInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }*/

    /*public static String getVersionName() {
        try {
            PackageInfo pInfo = Common.getInstance().getPackageManager().getPackageInfo(Common.getInstance().getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }*/

    /**
     * Returns the no of column which will be applied to the grids on different devices
     */
    public static int getNumberOfColms() {
        int config = getDeviceScreenConfiguration();
        if (config == REGULAR_SCREEN_PORTRAIT) {
            return 2;
        } else if (config == LARGE_TABLET_LANDSCAPE) {
            return 6;
        } else if (config == LARGE_TABLET_PORTRAIT) {
            return 4;
        } else if (config == REGULAR_SCREEN_LANDSCAPE) {
            return 4;
        } else if (config == XLARGE_TABLET_LANDSCAPE) {
            return 8;

        } else if (config == XLARGE_TABLET_PORTRAIT) {
            return 6;
        }
        return 2;
    }

    public static int getItemWidth() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return  (metrics.widthPixels) / getNumberOfColms();
    }

    /*
     * Returns the status bar height for the current layout configuration.
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /*
     * Returns the navigation bar height for the current layout configuration.
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    public static String getAndroidId() {
        return Settings.Secure.ANDROID_ID;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        mContext = this;
        mPlayBackStarter = new PlayBackStarter(mContext);

        // Obtain the FirebaseAnalytics instance.

        initImageLoader();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        /**
         *disable UIL Logs
         */
        L.writeDebugLogs(false);

        LoadAds();
    }


    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean running) {
        mIsServiceRunning = running;
    }

    public MusicService getService() {
        return mService;
    }

    public void setService(MusicService service) {
        mService = service;
    }

    private void initImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);

        L.writeDebugLogs(false);
        L.disableLogging();
        L.writeLogs(false);
    }

    public DataBaseHelper getDBAccessHelper() {
        return DataBaseHelper.getDatabaseHelper(mContext);
    }

    public PlayBackStarter getPlayBackStarter() {
        return mPlayBackStarter;
    }

    /**
     * Converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */

    public float convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }


    public PreferencesHelper getSharedPreferencesHelper() {
        return PreferencesHelper.getInstance();
    }



    public void LoadAds() {

        try {

            Log.e("TAG", "LoadAds Called");
            mInterstitialAd = new PublisherInterstitialAd(this);

            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));

            ins_adRequest = new PublisherAdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("EA965DE183B804F71E5E6D353E6607DE")
                    .addTestDevice("5CE992DB43E8F2B50F7D2201A724526D")
                    .addTestDevice("6E5543AE954EAD6702405BFCCC34C9A2")
                    .addTestDevice("28373E4CC308EDBD5C5D39795CD4956A") //samsung
                    .addTestDevice("79E8DED973BDF7477739501E228D88E1") //samdung max|
                    .build();

            mInterstitialAd.loadAd(ins_adRequest);
        } catch (Exception e) {
        }
    }

    public boolean requestNewInterstitial() {

        try {
            if (mInterstitialAd.isLoaded()) {
                Log.e("T", "requestNewInterstitial isLoaded Called");
                mInterstitialAd.show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLoaded() {

        try {
            if (mInterstitialAd.isLoaded() && mInterstitialAd != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}