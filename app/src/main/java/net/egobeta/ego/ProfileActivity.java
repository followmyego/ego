package net.egobeta.ego;



import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;


import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.Adapters.UserItem;
import net.egobeta.ego.Fragments.Fragment_Main;
import net.egobeta.ego.Fragments.ProfileFragment;
import net.egobeta.ego.Fragments.ScrollTabHolderFragment;
import net.egobeta.ego.ImportedClasses.AutoResizeTextView;
import net.egobeta.ego.ImportedClasses.BlurTransformation;
import net.egobeta.ego.ImportedClasses.FacebookPictureViewRound;


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
import java.util.List;


import android.content.Context;
import android.content.Intent;



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

import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;




import net.astuetz.PagerSlidingTabStrip;

import net.egobeta.ego.InstagramClasses.ImageLoader;
import net.egobeta.ego.Interfaces.ScrollTabHolder;
import net.egobeta.ego.Library.LocalDataBase;
import net.egobeta.ego.Settings.SettingsActivity;
import net.egobeta.ego.Table_Classes.User_Profile;
import net.flavienlaurent.notboringactionbar.AlphaForegroundColorSpan;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;


import android.app.AlertDialog;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener, View.OnClickListener {

    private final static String LOG_TAG = "Profile Activity";
    private boolean expanded = false;
    //AWS Variables
    DynamoDBMapper mapper = null;
    public IdentityManager identityManager = null; //The identity manager used to keep track of the current user account.
    User_Profile userProfile = null;
    String imageURL;

    //Declare Variables
    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    public Drawable upArrow;
    private PagerAdapter mPagerAdapter;
    public static Context context;
    private TypedValue mTypedValue = new TypedValue();
    static AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    public AlphaAnimation alpha;
    private Typeface typeface;
    public BlurTransformation blurTransformation;
    ImageLoader imageLoader;
    private static final int[] ITEM_DRAWABLES = {
            R.drawable.block_icon,
            R.drawable.pin_icon,
            R.drawable.upload_icon
    };

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
    private String status;
    private int views;
    private String firstName;
    private String lastName;
    private String age;
    private String email;
    private String snapchat_username;
    private String instagram_id;
    private String twitter_id;
    private String google_plus_id;
    private String linkedIn_id;
    private String instagram_photos_connected;

    //View Item Variables
    private static View mHeader;
    private static RelativeLayout userInfoLayout;
    private AutoResizeTextView mStatus;
    private TextView profilePageViews;
    private TextView name;
    private static ImageView home_menu_image;
    static ImageView home_menu_image2;
    private static RoundedImageView profilePicture;
    public ImageButton tapToEdit;
    public static ScrollView scrollView;
    private SlidingMenu slidingMenu;
    private ViewPager mViewPager;
    private static Toolbar toolbar;
    public PagerSlidingTabStrip mPagerSlidingTabStrip;
    private Button pinButton;
    private Button uploadButton;
    private Button blockButton;

    private ImageView pinButtonImage;


    ScrollTabHolderFragment fragment;
    private AbsListView absListView;
    ImageView mHeaderPicture;
    private Activity activity;
    private static ImageButton closeButton;
    private static FrameLayout picMenuBackground;
    private boolean scrolled = false;
    boolean backIsVisible = false;
    private int userInfoScrollFade2;
    private int mMinHeaderTranslation2;
    private int position;
    private LocalDataBase mLocalDataBase;
    boolean isPinned = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Set up and initialize aws variables **/
        initializeAWSVariables();

        /** Initialize facebookId, context, typeface**/
        setUpNeededVariables();

        /** Set Content View **/
        setContentView(R.layout.activity_profile);

        /** Chain ends at updateUI method which then initializes the pager adapter **/
        new LoadUserProfile().execute();

        /** Restart this Activity if an uncaught exception is found **/
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this,
//                MainActivity.class));
    }

    public class LoadUserProfile extends AsyncTask<Void, Void, Void> {
        final ProgressDialog dialog = ProgressDialog.show(ProfileActivity.this,
                getString(R.string.settings_fragment_dialog_title),
                getString(R.string.settings_fragment_dialog_message));

        public LoadUserProfile() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            /** Load the user profile that was clicked form the stream **/
            try {
                userProfile = mapper.load(User_Profile.class, facebookId);
            } catch (final AmazonServiceException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if(userProfile != null){
                /** Fetch item variables from loaded User_Profile class **/
                fetchUserProfileVariables();

                /** Update the ui and displayed info from the user's profile info **/
                updateUI(dialog);
            } else {
                /** Fetch basic user variables if theres no data **/
                fetchDefaultProfileVariables();

                /** Update the ui and displayed info from the user's profile info **/
                updateUI(dialog);
            }
        }

    }

    /** Anything on the ui thread that needs changing after sync gets updated here **/
    private void updateUI(final ProgressDialog dialog) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }

                /**Initialize dimension variables for the animations*/
                initializeDimensionItems();

                /**Initialize view item variables*/
                initializeViewItems();

                /** Sets the profile image and blur **/
                updateProfileAndBackgroundImage();

                /** NEXT RELEASE VERSION: Sliding Menu Feature **/
                /**Create the pull out Sliding menu*/
//                createMenuDrawer();

                /**Method to set view item fonts*/
                setViewFonts();

                /**Capitalize 1st letter of the following variable.*/
                firstName = capitalizeFirstCharacter(firstName);
                if (status == null) {
                    status = "Hello there! :)";
                    status = capitalizeFirstCharacter(status);
                } else {
                    status = capitalizeFirstCharacter(status);
                }

                /**Set View values from user info variables*/
                mStatus.setText(status);
                profilePageViews.setText(views + " VIEWS");
                name.setText(firstName);

                /**Set up the ViewPager and PagerAdapter*/
                setUpViewPager();

                /**Set up the SlidingTabStrip*/
                initializeSlidingTabStrip();

                /**Create top toolbar/menu bar*/
                setUpToolBar();

                /**Get rid of the default arrow image*/
                removeDefaultMenuButton();

            }
        });
    }

    /** Fetch item variables from the loaded User_Profile class **/
    private void fetchUserProfileVariables() {
        facebookId = userProfile.getFacebookId();
        status = userProfile.getStatus();
        views = userProfile.getViews();
        firstName = userProfile.getFirstName();
        lastName = userProfile.getLastName();
        age = userProfile.getAge();
        email = userProfile.getEmail();
        snapchat_username = userProfile.getSnapchat_username();
        instagram_id = userProfile.getInstagram_id();
        twitter_id = userProfile.getTwitter_id();
        google_plus_id = userProfile.getGoogle_plus_id();
        linkedIn_id = userProfile.getLinkedIn_id();
        instagram_photos_connected = userProfile.getInstagram_photos_connected();
    }

    /** Fetch item variables from the loaded User_Profile class **/
    private void fetchDefaultProfileVariables() {
        status = "Hey everyone! I am new to ego!";
        views = 137;
        firstName = "User";
        lastName = "User";
        age = "20";
        email = "SomeEmail";
        snapchat_username = "";
        instagram_id = "";
        twitter_id = "";
        google_plus_id = "";
        linkedIn_id = "";
        instagram_photos_connected = "no";

        userProfile = new User_Profile();
        userProfile.setStatus(status);
        userProfile.setViews(views);
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setAge(age);
        userProfile.setEmail(email);
        userProfile.setSnapchat_username(snapchat_username);
        userProfile.setInstagram_photos_connected(instagram_id);
        userProfile.setTwitter_id(twitter_id);
        userProfile.setGoogle_plus_id(google_plus_id);
        userProfile.setLinkedIn_id(linkedIn_id);
        userProfile.setInstagram_photos_connected(instagram_photos_connected);
    }

    //Initialize some needed variables
    private void setUpNeededVariables() {
        activity = this;
        facebookId = getIntent().getStringExtra("facebook_id");
        position = getIntent().getIntExtra("position", 0);
        isPinned = getIntent().getBooleanExtra("is_pinned", false);
        context = getApplicationContext();
        mLocalDataBase = new LocalDataBase(context);

        //Initialize Image Loader class for profile pic and instagram pics.
        imageLoader = new ImageLoader(activity.getApplicationContext());

        //Initialize font
        typeface = Typeface.createFromAsset(getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
    }

    private void initializeAWSVariables() {
        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);
        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        //Initialize the mapper for DynamoDB
        mapper = awsMobileClient.getDynamoDBMapper();
    }

    private void setUpViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setTabHolderScrollingContent(this);
        mViewPager.setAdapter(mPagerAdapter);
    }




    //Create the pull out Sliding menu
    private void createMenuDrawer() {
        if(slidingMenu == null){
            System.out.println("MAINACTIVITY: createMenuDrawer");

            slidingMenu = new SlidingMenu(ProfileActivity.this);
            slidingMenu.attachToActivity(ProfileActivity.this, SlidingMenu.SLIDING_CONTENT, true);
            slidingMenu.setFadeDegree(0f);
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            slidingMenu.setBehindOffsetRes(R.dimen.behindOffSetRes);
            slidingMenu.setMenu(R.layout.sliding_menu_frame);
            final View view = slidingMenu.getRootView();

            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.viewingYouProgressBar);
            final ImageView eyeballImage = (ImageView) view.findViewById(R.id.viewing_you_image);
            final TextView viewingYouText = (TextView) view.findViewById(R.id.viewing_you_text);
            TextView noViewersText1 = (TextView) view.findViewById(R.id.viewersHolderText1);
            TextView noViewersText2 = (TextView) view.findViewById(R.id.viewersHolderText2);
            TextView settingsText = (TextView) view.findViewById(R.id.settingsButtonText);
            ImageView settingsButton = (ImageView) view.findViewById(R.id.settings_Button);
            final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.noViewersHolder);

            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    settings();
                    logout(); // I have the settings button logging the user out for now
                }
            });
            relativeLayout.setVisibility(View.GONE);
            eyeballImage.setVisibility(View.VISIBLE);
            viewingYouText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            Button logoutButton = (Button) view.findViewById(R.id.logout_Button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            /** This method gets called when the menu has finished opening **/
            slidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
                @Override
                public void onOpened() {
                    /** check to see who is viewing your profile **/
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(1400);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //If no one is viewing you, show these graphics
                                        eyeballImage.setVisibility(View.INVISIBLE);
                                        viewingYouText.setVisibility(View.INVISIBLE);
                                        relativeLayout.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            });

            /** This method gets called when the menu has finished closing **/
            slidingMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
                @Override
                public void onClosed() {
                    //If no one is viewing you, show these graphics
                    eyeballImage.setVisibility(View.VISIBLE);
                    viewingYouText.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            noViewersText1.setTypeface(typeface);
            noViewersText2.setTypeface(typeface);
            viewingYouText.setTypeface(typeface);
            settingsText.setTypeface(typeface);
        }
    }

    //Create top toolbar/menu bar
    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(this);
    }

    public void logout(){
        identityManager.signOut();
        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    //Method to go to user settings
    public void settings() {
        slidingMenu.toggle();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        this.finish();
    }

    //Initialize dimension variables for the animations
    private void initializeDimensionItems() {
        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.profile_activity_min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.profile_activity_header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();

        userInfoScrollFade = getResources().getDimensionPixelSize(R.dimen.user_info_scroll_fade);

        mMinHeaderTranslation1 = -userInfoScrollFade + getActionBarHeight();

        userInfoScrollFade2 = getResources().getDimensionPixelSize(R.dimen.user_info_scroll_fade2);
        mMinHeaderTranslation2 = -mMinHeaderHeight + getActionBarHeight();
    }

    //Initialize view item variables
    private void initializeViewItems(){


        mHeader = findViewById(R.id.header); /**ENTIRE HEADER**/
        home_menu_image = (ImageView) findViewById(R.id.toolbar_icon); /**HEADER - HOME MENU IMAGE**/
        home_menu_image2 = (ImageView) findViewById(R.id.toolbar_icon2); /**HEADER - HOME MENU IMAGE**/
        mHeaderPicture = (ImageView) findViewById(R.id.header_picture); /**HEADER - BLURRED BACKGROUND**/

        picMenuBackground = (FrameLayout) findViewById(R.id.profile_picture_menu);
        pinButtonImage = (ImageView) findViewById(R.id.pin_button);
        pinButton = (Button) findViewById(R.id.pinButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        blockButton = (Button) findViewById(R.id.blockButton);

        closeButton = (ImageButton) findViewById(R.id.close_button);
        profilePicture = (RoundedImageView) findViewById(R.id.profile_picture); /**HEADER - PROFILE PICTURE**/
        userInfoLayout = (RelativeLayout) findViewById(R.id.user_info_layout); /**HEADER - RELATIVE LAYOUT**/
        name = (TextView) findViewById(R.id.first_name); /**HEADER - RELATIVE LAYOUT -  USER'S NAME**/
        profilePageViews = (TextView) findViewById(R.id.profile_views); /**HEADER - RELATIVE LAYOUT -  PAGE VIEWS**/
        mStatus = (AutoResizeTextView) findViewById(R.id.etStatus); /**HEADER - RELATIVE LAYOUT -  USER'S STATUS**/
        tapToEdit = (ImageButton) findViewById(R.id.tapToEdit); /**HEADER - RELATIVE LAYOUT -  TAP TO EDIT**/


        /**Set OnClickListener*/
        tapToEdit.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        pinButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        blockButton.setOnClickListener(this);

        /** set the color for the buttons depending on values **/
        if(isPinned){
            int color = Color.parseColor("#55C1AD");
            pinButtonImage.setColorFilter(color);
        }

        /** if the profile is not our, disable the tapToEdit button **/
        if(position > 2){
            tapToEdit.setVisibility(View.GONE);
        }

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
        return home_menu_image;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition && !expanded) {
            picMenuBackground.setVisibility(View.GONE);

            absListView = view;
            int scrollY = getScrollY(view);

            if(scrollY == 0){
                scrolled = false;
            } else {
                scrolled = true;
            }

            mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
            float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
            float ratio1 = clamp(mHeader.getTranslationY() / mMinHeaderTranslation1, 0.0f, 1.0f);
            float ratio2 = clamp(mHeader.getTranslationY() / mMinHeaderTranslation2, 0.0f, 1.0f);


            interpolate(profilePicture, getHomeMenuImageIconView(), sSmoothInterpolator.getInterpolation(ratio));
            interpolate(picMenuBackground, getHomeMenuImageIconView(), sSmoothInterpolator.getInterpolation(ratio));
//            interpolate(userInfoLayout, getHomeMenuImageIconView(), sSmoothInterpolator.getInterpolation(ratio));
//            setTitleAlpha(clamp(5.0F * ratio1 - 4.0F, 0.0F, 1.0F));
            setTitleAlpha(clamp((5.0F * ratio2 - 4.0F) * -1, 0.5F, 1.0F));
            setHomeAsUpAlpha(clamp((5.0F * ratio1 - 4.0F) * -1, 0.0F, 1.0F));
        } else {
            picMenuBackground.startAnimation(profilePictureAnimation2());
            profilePicture.startAnimation(profilePictureAnimation());
        }
    }

    //Method to animate the title fade in/out
    private static void setTitleAlpha(float alpha) {
        profilePicture.setAlpha(alpha * 1);
        toolbar.setTitle(" ");

    }

    //Method to animate the homeAsUp indicator fade in/out
    private static void setHomeAsUpAlpha(float alpha) {
        toolbar.setAlpha(alpha * 1);
        home_menu_image.setAlpha(alpha * 1);
        userInfoLayout.setAlpha(alpha * 1);
        closeButton.setAlpha(alpha * 1);
        picMenuBackground.setAlpha(alpha * 1 + alpha * 1 /2);
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
                break;
            case R.id.profile_picture:
                if(!scrolled){

                    if(!backIsVisible){
                        picMenuBackground.setVisibility(View.VISIBLE);
                        backIsVisible = true;
                    }
                    picMenuBackground.startAnimation(profilePictureAnimation2());
                    profilePicture.startAnimation(profilePictureAnimation());

                } else {
                    absListView.post(new Runnable() {
                        @Override
                        public void run() {
                            absListView.smoothScrollToPosition(0);
                        }
                    });
                }
                break;
            case R.id.close_button:
                if(!scrolled){
                    ProfileActivity.this.finish();
                }
                break;
            case R.id.pinButton:
                if(position > 2){
                    /** Code to pin the user **/
//                    final UserPinned userPinned = UserPinned.getInstance(context);
//                    pinTheUser(userPinned);
                    loadPinnedUsers();
                } else {
                    picMenuBackground.startAnimation(profilePictureAnimation2());
                    profilePicture.startAnimation(profilePictureAnimation());
                    Toast.makeText(ProfileActivity.this, "Your list of pinned users", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.uploadButton:
                /** Code to upload a new user profile **/
                picMenuBackground.startAnimation(profilePictureAnimation2());
                profilePicture.startAnimation(profilePictureAnimation());
                Toast.makeText(ProfileActivity.this, "This feature is not ready yet.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.blockButton:
                /** Code to block the user **/
                picMenuBackground.startAnimation(profilePictureAnimation2());
                profilePicture.startAnimation(profilePictureAnimation());
                Toast.makeText(ProfileActivity.this, "This feature is not ready yet.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /** Sync user's preferences only if user is signed in **/
//    private void syncPinnedUsers() {
//        // sync only if user is signed in
//        if (AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
//            final UserPinned userPinned = UserPinned.getInstance(getApplicationContext());
//            userPinned.getDataset().synchronize(new DefaultSyncCallback() {
//                @Override
//                public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
//                    super.onSuccess(dataset, updatedRecords);
//                    Log.d(LOG_TAG, "successfully synced user settings");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
////                            loadPinnedUsers();
//                            final UserPinned userPinned = UserPinned.getInstance(context);
//                            pinTheUser(userPinned);
//                        }
//                    });
//                }
//            });
//        }
//    }

    /** userPermissions.loadFromDataset(); get called here **/
    private void loadPinnedUsers() {
        final UserPinned userPinned = UserPinned.getInstance(getApplicationContext());
        final Dataset dataset = userPinned.getDataset();
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(R.string.settings_fragment_dialog_title),
                getString(R.string.settings_fragment_dialog_message));
        Log.d(LOG_TAG, "Loading user settings from remote");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                super.onSuccess(dataset, updatedRecords);
                userPinned.loadFromDataset();
                pinTheUser(userPinned, dialog);

            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.w(LOG_TAG, "Failed to load user settings from remote, using default.", dse);
                updateUI(dialog);
            }

            @Override
            public boolean onDatasetsMerged(final Dataset dataset,
                                            final List<String> datasetNames) {
                // Handle dataset merge. One can selectively copy records from merged datasets
                // if needed. Here, simply discard merged datasets
                for (String name : datasetNames) {
                    Log.d(LOG_TAG, "found merged datasets: " + name);
                    AWSMobileClient.defaultMobileClient().getSyncManager().openOrCreateDataset(name).delete();
                }
                return true;
            }
        });
    }

    private void pinTheUser(final UserPinned userPinned, final ProgressDialog dialog) {
        /** Method to sync the user's privacy settings with Cognito Sync **/
        boolean removedUser = false;


        String pinnedUser1 = userPinned.getPinnedUser1();
        String pinnedUser2 = userPinned.getPinnedUser2();
        String pinnedUser3 = userPinned.getPinnedUser3();
        String pinnedUser4 = userPinned.getPinnedUser4();
        String pinnedUser5 = userPinned.getPinnedUser5();

        //Create temporary list of ids
        ArrayList<String> listOfIds = new ArrayList<String>();
        for(int i = 0; i < Fragment_Main.userList.size(); i++){
            listOfIds.add(Fragment_Main.userList.get(i).getFacebookId());
        }

        /** Check if the user is already a pinned user **/
        if(isPinned){
            if(facebookId.equals(pinnedUser1)){
                userPinned.setPinnedUser1("empty");
                removedUser = true;
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
            } else if(facebookId.equals(pinnedUser2)){
                userPinned.setPinnedUser2("empty");
                removedUser = true;
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));

            } else if(facebookId.equals(pinnedUser3)){
                userPinned.setPinnedUser3("empty");
                removedUser = true;
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));

            } else if(facebookId.equals(pinnedUser4)){
                userPinned.setPinnedUser4("empty");
                removedUser = true;
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));

            } else if(facebookId.equals(pinnedUser5)){
                userPinned.setPinnedUser5("empty");
                removedUser = true;
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
            }

        } else {
            /** Check if we have an available pinned space **/
            if(pinnedUser1.equals("empty")) {
                userPinned.setPinnedUser1(facebookId);
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
                Fragment_Main.userList.add(3, new UserItem(context, facebookId, 0, true));

            } else if(pinnedUser2.equals("empty")) {
                userPinned.setPinnedUser2(facebookId);
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
                Fragment_Main.userList.add(4, new UserItem(context, facebookId, 0, true));

            } else if(pinnedUser3.equals("empty")) {
                userPinned.setPinnedUser3(facebookId);
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
                Fragment_Main.userList.add(5, new UserItem(context, facebookId, 0, true));

            } else if(pinnedUser4.equals("empty")) {
                userPinned.setPinnedUser4(facebookId);
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
                Fragment_Main.userList.add(6, new UserItem(context, facebookId, 0, true));

            } else if(pinnedUser5.equals("empty")) {
                userPinned.setPinnedUser5(facebookId);
                Fragment_Main.userList.remove(listOfIds.indexOf(facebookId));
                Fragment_Main.userList.add(7, new UserItem(context, facebookId, 0, true));

            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ProfileActivity.this, "Sorry, you already have 5 people pinned", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }





        final boolean removed_user = removedUser;


            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {
                    userPinned.saveToDataset();
                    return null;
                }

                @Override
                protected void onPostExecute(final Void aVoid) {
                    // save user settings to remote on background thread
                    userPinned.getDataset().synchronize(new Dataset.SyncCallback() {
                        @Override
                        public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                            //Get the info from facebook based on the privacy settings
                            runOnUI(dialog, removed_user);
                        }

                        @Override
                        public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                            Log.d(LOG_TAG, "onConflict - dataset conflict");
                            return false;
                        }

                        @Override
                        public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                            Log.d(LOG_TAG, "onDatasetDeleted - dataset deleted");
                            return false;
                        }

                        @Override
                        public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                            Log.d(LOG_TAG, "onDatasetsMerged - datasets merged");
                            return false;
                        }

                        @Override
                        public void onFailure(DataStorageException dse) {
                            Log.e(LOG_TAG, "onFailure - " + dse.getMessage(), dse);
                        }
                    });
                }
            }.execute();
    }

    public void runOnUI(final ProgressDialog dialog, final boolean removedUser){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if(removedUser){
                    Toast.makeText(ProfileActivity.this, "Removed a pinned user", Toast.LENGTH_SHORT).show();
                    int color = Color.parseColor("#000000");
                    pinButtonImage.setColorFilter(color);
                } else {
                    Toast.makeText(ProfileActivity.this, "Successfully updated your pinned users", Toast.LENGTH_SHORT).show();
                    int color = Color.parseColor("#55C1AD");
                    pinButtonImage.setColorFilter(color);
                }

                picMenuBackground.startAnimation(profilePictureAnimation2());
                profilePicture.startAnimation(profilePictureAnimation());
                Fragment_Main.adapter.notifyDataSetChanged();
            }
        });
    }

    private Animation profilePictureAnimation() {

        float topFloat = 1;
        float bottomFloat = 0.35f;

        Animation animation = new ScaleAnimation(
                expanded ? bottomFloat : topFloat, //from X
                expanded ? topFloat : bottomFloat, //to X
                expanded ? bottomFloat : topFloat, //form Y
                expanded ? topFloat : bottomFloat, //to Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(180);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);

        if(!expanded){
            pinButton.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            blockButton.setVisibility(View.VISIBLE);

            expanded = true;
            ProfileFragment.mListView.setActivated(false);
        } else {
            pinButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            blockButton.setVisibility(View.GONE);

            expanded = false;
            ProfileFragment.mListView.setActivated(true);
        }

        return animation;
    }

    private Animation profilePictureAnimation2() {

        float topFloat = 1;
        float bottomFloat = 0.35f;

        Animation animation = new ScaleAnimation(
                expanded ? topFloat : bottomFloat,//from X
                expanded ? bottomFloat : topFloat,//to X
                expanded ? topFloat : bottomFloat,//form Y
                expanded ? bottomFloat : topFloat,//to Y
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(180);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);

        return animation;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return true;
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






    //Method to update the user profile picture and background after it is retrieved from the database
    public void updateProfileAndBackgroundImage() {

        //Set background image to be profile image and adda blur and
//        blurTransformation = new BlurTransformation(ProfileActivity.this, BLUR_RADIUS);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.profileBackground);
        activity.getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), MainActivity.image));
        relativeLayout.setVisibility(View.VISIBLE);

        //Facebook ProfilePic URL
        imageURL = "https://graph.facebook.com/" + facebookId + "/picture?width=700&height=700";

        //Load profile picture
        /** Benchmark these two libraries to see which one is faster **/
//        imageLoader.DisplayImage(imageURL, profilePicture);
        Picasso.with(context).load(imageURL).into(profilePicture);
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
        input.setText(status);

        // Set up the buttons
        statusUpdateBuilder.setPositiveButton("Say it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //Check if the status is null or empty
                if (!(input.getText().toString().trim()).equals("")) {
                    status = input.getText().toString().trim();
                    mStatus.setText(status);
                    /**Send updated user status to the server here*/
//                    new UpdateUserStatus(context).execute(user.facebookId, user_status);
                } else {
                    status = "Hey everyone! I will update my status soon.";
                    mStatus.setText(status);
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
        public Fragment getItem(int pos) {

            fragment = (ScrollTabHolderFragment) ProfileFragment.newInstance(context, pos, toolbar, facebookId, userProfile, mapper, position);

            mScrollTabHolders.put(pos, fragment);
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

