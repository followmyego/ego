package net.egobeta.ego.OnBoarding;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ScrollView;

import com.viewpagerindicator.CirclePageIndicator;

import net.egobeta.ego.Fragments.OnBoarding_Fragment123;
import net.egobeta.ego.Fragments.OnBoarding_Fragment4;
import net.egobeta.ego.Interfaces.ScrollTabHolder;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;
import net.egobeta.ego.Settings.SettingsActivity;

public class Main_OnBoarding extends AppCompatActivity {



    //Other variables
    private final static String LOG_TAG = Main_OnBoarding.class.getSimpleName(); //Class name for log messages.


    private PagerAdapter mPagerAdapter; //The pager adapter, which provides the pages to the view pager widget.


    static Context context;

    public Drawable upArrow;
    public static ScrollView scrollView;
    private ViewPager mViewPager;
    static CirclePageIndicator pageIndicator;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ACT DEBUG", "Main_OnBoarding: OnDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACT DEBUG", "Main_OnBoarding: OnPause");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACT DEBUG", "Main_OnBoarding: OnCreate");
        setContentView(R.layout.activity_main_onboarding);

        context = getApplicationContext();

        /**Set up the ViewPager and PagerAdapter*/
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        /**Get rid of the default arrow image*/
        removeDefaultMenuButton();


        /*Bind the Page indicator to the adapter*/
        pageIndicator = (CirclePageIndicator)findViewById(R.id.titles);
        int tabPageColor = Color.parseColor("#5055C1AD");
        int tabFillColor = Color.parseColor("#55C1AD");
        pageIndicator.setStrokeColor(tabPageColor);
        pageIndicator.setFillColor(tabFillColor);
        pageIndicator.setPageColor(tabPageColor);
        pageIndicator.setExtraSpacing(15f);
        pageIndicator.setViewPager(mViewPager);
    }

    public void goToMainActivity(){

//        startActivity(new Intent(Main_OnBoarding.this, MainActivity.class)
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        Intent intent = new Intent(Main_OnBoarding.this, MainActivity.class);
        startActivity(intent);
        this.finish();

    }


    //Using custom image view as home as up indicator, this gets rid of the default arrow image
    private void removeDefaultMenuButton(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            upArrow = getDrawable(R.drawable.back_arrow_transparent);
        } else {
            upArrow = getResources().getDrawable(R.drawable.back_arrow_transparent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            upArrow.setColorFilter(getColor(R.color.transparentBackground), PorterDuff.Mode.SRC_ATOP);
        }
    }



    /************************************** INNER CLASSES ******************************************/
    /***********************************************************************************************/

    //PagerAdapter for the sliding pages under user profile
    public class PagerAdapter extends FragmentPagerAdapter {


        private final String[] TITLES = {"1", "2", "3", "4"};

        private ScrollTabHolder mListener;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTabHolderScrollingContent(ScrollTabHolder listener) {
            mListener = listener;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }


        @Override
        public int getCount() {
            return TITLES.length;
        }


        @Override
        public Fragment getItem(int position) {
            if(position == 3){
                OnBoarding_Fragment4 fragment = OnBoarding_Fragment4.newInstance(position + "");
                return fragment;
            } else  {
                OnBoarding_Fragment123 fragment = OnBoarding_Fragment123.newInstance(position + "");
                return fragment;
            }
        }

    }
}

