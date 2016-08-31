package net.egobeta.ego;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.text.InputType;
import android.text.SpannableString;

import net.egobeta.ego.Fragments.ProfileFragment;
import net.egobeta.ego.Fragments.ScrollTabHolderFragment;
import net.egobeta.ego.ImportedClasses.AutoResizeTextView;
import net.egobeta.ego.ImportedClasses.BlurTransformation;
import net.egobeta.ego.ImportedClasses.FacebookPictureViewRound;
import net.egobeta.ego.InstagramClasses.ImageViewAdapterInstagram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;


import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.Button;



import net.astuetz.PagerSlidingTabStrip;

import net.egobeta.ego.Interfaces.ScrollTabHolder;
import net.egobeta.ego.Settings.SettingsActivity;
import net.flavienlaurent.notboringactionbar.AlphaForegroundColorSpan;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;



import android.app.AlertDialog;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener, View.OnClickListener {

    //Declare Variables
    private String getProfileImageURL = "http://www.myegotest.com/PhotoUpload/getAllImages.php";
    private static final String TAG = "DEBUGGING MESSAGE";
    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    private static Drawable d;
    public Drawable upArrow;
    private PagerAdapter mPagerAdapter;
    public static Context context;
    private TypedValue mTypedValue = new TypedValue();
    private static SpannableString mSpannableString;
    private static AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    public AlphaAnimation alpha;
    private Typeface typeface;
    public BlurTransformation blurTransformation;

    //Number Variables
    private int mMinHeaderTranslation;
    private int mMinHeaderTranslation1;
    private int mActionBarHeight;
    public int mMinHeaderHeight;
    public int userInfoScrollFade;
    private int mHeaderHeight;
    private static final float BLUR_RADIUS = 25F;
    private static RectF mRect1 = new RectF();
    private static RectF mRect2 = new RectF();

    //User Info Variables
    private String facebookId;
    public String email;
    private String firstName;
    private String lastName;
    public String gender;
    public String birthday;
    private String user_status;
    public String usingFacebookPic;
    public String pageViews;
    public String profilePic;

    //View Item Variables
    private static View mHeader;
    private static RelativeLayout userInfoLayout;
    private AutoResizeTextView mStatus;
    private TextView profilePageViews;
    private TextView name;
    private static ImageView home_menu_image;
    private static ImageView home_menu_image2;
    //    private static ImageView profilePicture;
    private static FacebookPictureViewRound profilePicture;
    public ImageButton tapToEdit;
    public static ScrollView scrollView;
    private SlidingMenu slidingMenu;
    private ViewPager mViewPager;
    private static Toolbar toolbar;
    public PagerSlidingTabStrip mPagerSlidingTabStrip;
    private static TextView toolbarTitle;

    ScrollTabHolderFragment fragment;
    private AbsListView absListView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private Resources resources;
    private ImageView mHeaderPicture;

    //Instagram stuffs
    private static String instagramProfileLinked;
    private static String instagramId;
    private static String instagramUsername;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "standard_resolution";
    public static final String TAG_URL = "url";
    public JSONObject jObj = null;
    Integer[] imageId = new Integer[10];
    ImageViewAdapterInstagram adapter;
//    private static InstagramApp instagramApp;
    private Button btnConnect;
    private HashMap<String, String> instagramUserInfoHashmap = new HashMap<String, String>();
//    private Handler handler = new Handler(new Handler.Callback() {
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == InstagramApp.WHAT_FINALIZE) {
//
//                //Store user instagram info from instagram login on localdatabase
//                instagramUserInfoHashmap = instagramApp.getUserInfo();
//                mLocalDataBase = new LocalDataBase(context);
//                mLocalDataBase.setInstagramID(instagramUserInfoHashmap.get("id"));
//                mLocalDataBase.setInstagramUsername(instagramApp.getUserName());
//
//                //Assign the class instagram variables thier values
//                user = mLocalDataBase.getUsersInfo();
//                facebookId = user.facebookId;
//                instagramId = mLocalDataBase.getInstagramID();
//                instagramUsername = mLocalDataBase.getInstagramUsername();
//
//                //Update user instagram information on server
//                new UpdateUserInstagramInfo(MainActivity2.this, "yes").execute(facebookId, instagramId, instagramUsername);
//
//                //Get logged in users instagram image links
////                getAllMediaImages(context);
//
//            } else if (msg.what == InstagramApp.WHAT_ERROR) {
//                Log.d(TAG, "Check your network.");
//            }
//            return false;
//        }
//    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = getApplicationContext();
        facebookId = getIntent().getStringExtra("facebook_id");

//        resources = getResources();
//        drawerArrowDrawable = new DrawerArrowDrawable(resources);
//        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.menuDrawerColor));

        /**Initialize font*/
        typeface = Typeface.createFromAsset(getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        /**Initialize dimension variables for the animations*/
        initializeDimensionItems();

        /**Initialize view item variables*/
        initializeViewItems();

        /**Create the pull out Sliding menu*/
        if(slidingMenu == null){
            createMenuDrawer();
        }

        /****************DEBUGGING*******************/
        /**Assign user info variables to info stored in SharedPreferences*/
        setUserInfoVariables();

        /**Method to set view item fonts*/
        setViewFonts();

        /**Capitalize 1st letter of the following variable.*/
        firstName = capitalizeFirstCharacter(firstName);
        if (!user_status.equals("")) {
            user_status = capitalizeFirstCharacter(user_status);
        }

        /**Set View values from user info variables*/
        mStatus.setText(user_status);
        profilePageViews.setText(pageViews + " VIEWS");
        name.setText(firstName);
        /****************DEBUGGING*******************/


        /**Set up the ViewPager and PagerAdapter*/
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setTabHolderScrollingContent(this);
        mViewPager.setAdapter(mPagerAdapter);

        /**Set up the SlidingTabStrip*/
        initializeSlidingTabStrip();


        /**Create top toolbar/menu bar*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(this);

        /**Get rid of the default arrow image*/
        removeDefaultMenuButton();


        /**Set OnClickListener*/
        tapToEdit.setOnClickListener(this);

        updateProfileAndBackgroundImage("");
    }




    //Create the pull out Sliding menu
    private void createMenuDrawer() {

        slidingMenu = new SlidingMenu(ProfileActivity.this);
        slidingMenu.attachToActivity(ProfileActivity.this, SlidingMenu.SLIDING_CONTENT, true);
        slidingMenu.setFadeDegree(1f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setBehindOffsetRes(R.dimen.behindOffSetRes);
        slidingMenu.setMenu(R.layout.sliding_menu_frame);

        View view = slidingMenu.getRootView();


        TextView viewingYou = (TextView) view.findViewById(R.id.viewing_you_text);
        TextView version = (TextView) view.findViewById(R.id.version);
        TextView year = (TextView) view.findViewById(R.id.year);
        ImageView imageView = (ImageView) view.findViewById(R.id.settingsButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings();
            }
        });

        viewingYou.setTypeface(typeface);
        version.setTypeface(typeface);
        year.setTypeface(typeface);

    }

    //Method to go to user settings
    public void settings() {
        slidingMenu.toggle();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
//        this.finish();
    }

    //Initialize dimension variables for the animations
    private void initializeDimensionItems() {
        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.profile_activity_min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.profile_activity_header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();

        userInfoScrollFade = getResources().getDimensionPixelSize(R.dimen.user_info_scroll_fade);
        mMinHeaderTranslation1 = -userInfoScrollFade + getActionBarHeight();
    }

    //Initialize view item variables
    private void initializeViewItems(){
        mHeader = findViewById(R.id.header); /**ENTIRE HEADER**/
        home_menu_image = (ImageView) findViewById(R.id.toolbar_icon); /**HEADER - HOME MENU IMAGE**/
        home_menu_image2 = (ImageView) findViewById(R.id.toolbar_icon2); /**HEADER - HOME MENU IMAGE**/
        mHeaderPicture = (ImageView) findViewById(R.id.header_picture); /**HEADER - BLURRED BACKGROUND**/


        profilePicture = (FacebookPictureViewRound) findViewById(R.id.profile_picture); /**HEADER - PROFILE PICTURE**/
        userInfoLayout = (RelativeLayout) findViewById(R.id.user_info_layout); /**HEADER - RELATIVE LAYOUT**/
        name = (TextView) findViewById(R.id.first_name); /**HEADER - RELATIVE LAYOUT -  USER'S NAME**/
        profilePageViews = (TextView) findViewById(R.id.profile_views); /**HEADER - RELATIVE LAYOUT -  PAGE VIEWS**/
        mStatus = (AutoResizeTextView) findViewById(R.id.etStatus); /**HEADER - RELATIVE LAYOUT -  USER'S STATUS**/
        tapToEdit = (ImageButton) findViewById(R.id.tapToEdit); /**HEADER - RELATIVE LAYOUT -  TAP TO EDIT**/
    }

    //Method for header animation
    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }

    //Set up the SlidingTabStrip
    private void initializeSlidingTabStrip() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.getTabBackground();
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
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
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    //Method to detect the scroll value of the Y axis
    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();
        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }
        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    //Method for header animation
    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    //Method for header animation
    private static RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    //Method for header animation
    private static void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    //Return the home menu button view
    private View getHomeMenuImageIconView() {
//        Drawable dra = getResources().getDrawable(R.drawable.side_arrow);
//        home_menu_image2.setImageDrawable(d);
//        home_menu_image2.setImageDrawable(dra);


        return home_menu_image;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            absListView = view;
            int scrollY = getScrollY(view);

            mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
            float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
            float ratio1 = clamp(mHeader.getTranslationY() / mMinHeaderTranslation1, 0.0f, 1.0f);

            interpolate(profilePicture, getHomeMenuImageIconView(), sSmoothInterpolator.getInterpolation(ratio));
//            interpolate(userInfoLayout, getHomeMenuImageIconView(), sSmoothInterpolator.getInterpolation(ratio));
            setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            setHomeAsUpAlpha(clamp((5.0F * ratio1 - 4.0F) * -1, 0.0F, 1.0F));
        }
    }

    //Method to animate the title fade in/out
    private static void setTitleAlpha(float alpha) {
//        mAlphaForegroundColorSpan.setAlpha(alpha);
//        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle(" ");

    }

    //Method to animate the homeAsUp indicator fade in/out
    private static void setHomeAsUpAlpha(float alpha) {
        toolbar.setAlpha(alpha * 1);
        home_menu_image.setAlpha(alpha * 1);
        userInfoLayout.setAlpha(alpha * 1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                slidingMenu.toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeaderPicture = (ImageView) findViewById(R.id.header_picture); /**HEADER - BLURRED BACKGROUND**/

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHeaderPicture = (ImageView) findViewById(R.id.header_picture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tapToEdit:
                updateStatus();
                break;
            case R.id.toolbar:
                absListView.post(new Runnable() {
                    @Override
                    public void run() {
                        absListView.smoothScrollToPosition(0);
                    }
                });
                Toast.makeText(this, "toolbar clicked mainActivity", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);

        currentHolder.adjustScroll((int) (mHeader.getHeight() + mHeader.getTranslationY()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void adjustScroll(int scrollHeight) {
        // nothing
    }

    //Method to set view item fonts
    private void setViewFonts() {
        name.setTypeface(typeface);
        mStatus.setTypeface(typeface);
        profilePageViews.setTypeface(typeface);

    }


    //Method to capitalize 1st letter of the String that's using this method.
    private String capitalizeFirstCharacter(String textInput) {
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }


    //Assign user info variables to info stored in SharedPreferences
    private void setUserInfoVariables() {
        email = "email";
        firstName = "firstName";
        lastName = "last_name";
        gender = "gender";
        birthday = "birthday";
        user_status = "user_status";
        usingFacebookPic = "yes";
        pageViews = "0";
    }



    //Method to update the user profile picture and background after it is retrieved from the database
    public void updateProfileAndBackgroundImage(String profilePic) {

        //Load profile picture
//        profilePicture = (ImageView) findViewById(R.id.profile_picture);
//        Picasso.with(context).load(profilePic).into(profilePicture);

        profilePicture = (FacebookPictureViewRound) findViewById(R.id.profile_picture);
        profilePicture.setPresetSize(FacebookPictureViewRound.LARGE);
        profilePicture.setProfileId(facebookId);
        //Set background image to be profile image and adda blur and
        blurTransformation = new BlurTransformation(ProfileActivity.this, BLUR_RADIUS);

    }


    //Method to update user status
    public void updateStatus(){
        AlertDialog.Builder statusUpdateBuilder = new AlertDialog.Builder(this);
        statusUpdateBuilder.setTitle("What would you like to say?");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        statusUpdateBuilder.setView(input);
        input.setText(user_status);

        // Set up the buttons
        statusUpdateBuilder.setPositiveButton("Say it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //Check if the status is null or empty
                if (!(input.getText().toString().trim()).equals("")) {
                    user_status = input.getText().toString().trim();
                    mStatus.setText(user_status);
                    /**Send updated user status to the server here*/
//                    new UpdateUserStatus(context).execute(user.facebookId, user_status);
                } else {
                    user_status = "Hey everyone! I will update my status soon.";
                    mStatus.setText(user_status);
                    /**Send updated user status to the server here*/
//                    new UpdateUserStatus(context).execute(user.facebookId, user_status);
                }
            }
        });
        statusUpdateBuilder.setNegativeButton("Nevermind.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        statusUpdateBuilder.show();
    }

    //AsyncTask to get profile pic url string from server
    public class GetProfilePicUrl extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection LucasHttpURLConnection = (HttpURLConnection) url.openConnection();
                LucasHttpURLConnection.setRequestMethod("POST");
                LucasHttpURLConnection.setDoOutput(true);
                LucasHttpURLConnection.setDoInput(true);
                LucasHttpURLConnection.setConnectTimeout(1000 * 6);
                LucasHttpURLConnection.setReadTimeout(1000 * 6);
                //OutputStream to get response
                OutputStream outputStream = LucasHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data =
                        URLEncoder.encode("facebook_id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //InputStream to get response
                InputStream IS = LucasHttpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                StringBuilder response = new StringBuilder();
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    response.append(json + "\n");
                    break;
                }
                bufferedReader.close();
                IS.close();
                LucasHttpURLConnection.disconnect();
                return response.toString().trim();
            } catch (MalformedInputException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            //If null Response
            if (result != null && !result.equals("")) {
                profilePic = returnParsedJsonObject(result);
                updateProfileAndBackgroundImage(profilePic);
                Toast.makeText(ProfileActivity.this, "ProfilePic Url: " + profilePic, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Sorry, there was an error. Please try again", Toast.LENGTH_LONG).show();
            }
        }


        //Method to parse json result and get the value of the key "image"
        private String returnParsedJsonObject(String result) {
            JSONObject resultObject = null;
            String returnedResult = "";
            try {
                resultObject = new JSONObject(result);
                returnedResult = resultObject.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
        private final String[] TITLES = {" "};

        private ScrollTabHolder mListener;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
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

            fragment = (ScrollTabHolderFragment) ProfileFragment.newInstance(context, position, toolbar, facebookId);

            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);

            }

            return fragment;

        }

        public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
            return mScrollTabHolders;
        }

    }


}

