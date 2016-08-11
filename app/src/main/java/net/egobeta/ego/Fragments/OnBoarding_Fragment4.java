package net.egobeta.ego.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.egobeta.ego.Adapters.BadgeItem;


import net.egobeta.ego.OnBoarding.Main_OnBoarding;
import net.egobeta.ego.R;

import java.util.ArrayList;
import java.util.List;


public class OnBoarding_Fragment4 extends Fragment implements CompoundButton.OnCheckedChangeListener  {

    private static final String ARG_PARAM1 = "param1";
    private int position;
    private View view;
    Typeface typeface;
//    private BadgeListAdapter adapter;
    static Context context;

    //View Items
    private TextView headerText;
    private ImageButton doneButton;
    public static ListView listView;
    private CheckBox friends_Box;

    //Badge Names
    public final static String KEY_PERMISSION_FRIENDS = "Friends";
    public final static String KEY_PERMISSION_FRIENDS_OF_FRIENDS = "Friends of Friends";
    public final static String KEY_PERMISSION_INSTAGRAM_FOLLOWERS = "Follower";
    public final static String KEY_PERMISSION_INSTAGRAM_FOLLOWING = "Following";
    public final static String KEY_PERMISSION_LOCATION = "Where you Live";
    public final static String KEY_PERMISSION_HOMETOWN = "Common Hometown";
    public final static String KEY_PERMISSION_LIKES = "Common like";
    public final static String KEY_PERMISSION_BIRTHDAY = "Same Birthday";
    public final static String KEY_PERMISSION_WORK = "Common Workplace";
    public final static String KEY_PERMISSION_SCHOOL = "Common School";
    public final static String KEY_PERMISSION_MUSIC = "Music you love";
    public final static String KEY_PERMISSION_MOVIES = "Movies you love";
    public final static String KEY_PERMISSION_BOOKS = "Books you love";
    public final static String KEY_PERMISSION_PROFFESIONAL_SKILLS = "Proffesional Skills";







    ArrayList<String> listOfNames = new ArrayList<String>();


    public static ArrayList<BadgeItem> badgeList;
    BadgeAdapter badgeAdapter;

    public ArrayList<String> confirmedList = new ArrayList<String>();


    public static OnBoarding_Fragment4 newInstance(String param1) {
        OnBoarding_Fragment4 fragment = new OnBoarding_Fragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OnBoarding_Fragment4() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            String arg = getArguments().getString(ARG_PARAM1);
            assert arg != null;
            position = Integer.parseInt(arg);
        }

        /**Initialize font*/
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.onboarding_slide_4, container, false);
        listView = (ListView) view.findViewById(R.id.privacyListView);

        InitializeViewItems();
        InitializeListView();

        return view;
    }


    private void InitializeViewItems() {

        headerText = (TextView) view.findViewById(R.id.header_text);
        doneButton = (ImageButton) view.findViewById(R.id.onboarding_button);

        headerText.setTypeface(typeface);


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(BadgeItem badgeItem : badgeList){
                    if(badgeItem.isSelected()){
                        if(!confirmedList.contains(badgeItem.getBadgeName())){
                            confirmedList.add(badgeItem.getBadgeName());
                        }

                    }
                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("privacy_preferences", confirmedList);
                ((Main_OnBoarding) getActivity()).goToLoadFacebookPermissions(confirmedList);
            }
        });
    }

    private void InitializeListView() {
        listOfNames.add(KEY_PERMISSION_FRIENDS);
        listOfNames.add(KEY_PERMISSION_FRIENDS_OF_FRIENDS);
        listOfNames.add(KEY_PERMISSION_INSTAGRAM_FOLLOWERS);
        listOfNames.add(KEY_PERMISSION_INSTAGRAM_FOLLOWING);
        listOfNames.add(KEY_PERMISSION_LOCATION);
        listOfNames.add(KEY_PERMISSION_HOMETOWN);
        listOfNames.add(KEY_PERMISSION_LIKES);
        listOfNames.add(KEY_PERMISSION_BIRTHDAY);
        listOfNames.add(KEY_PERMISSION_WORK);
        listOfNames.add(KEY_PERMISSION_SCHOOL);
        listOfNames.add(KEY_PERMISSION_MUSIC);
        listOfNames.add(KEY_PERMISSION_MOVIES);
        listOfNames.add(KEY_PERMISSION_BOOKS);

        int[] badgeImages = {
                R.drawable.friend,
                R.drawable.friends_of_friend,
                R.drawable.follower,
                R.drawable.following,
                R.drawable.location_icon,
                R.drawable.same_hometown,
                R.drawable.common_like,
                R.drawable.shared_birthday,
                R.drawable.common_workplace,
                R.drawable.same_school,
                R.drawable.loves_the_same_music,
                R.drawable.loves_the_same_movie,
                R.drawable.loves_the_same_books
        };

        badgeList = new ArrayList<BadgeItem>();



        for(int i = 0; i < listOfNames.size(); i++){
            badgeList.add(new BadgeItem(listOfNames.get(i), badgeImages[i]));
            System.out.println(listOfNames.get(i) + badgeImages[i]);

        }



        badgeAdapter = new BadgeAdapter(badgeList, getContext(), typeface, listView);
        listView.setAdapter(badgeAdapter);


    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = (Integer)buttonView.getTag();
        if(pos != ListView.INVALID_POSITION){
            BadgeItem badgeItem = badgeList.get(pos);
            badgeItem.setSelected(isChecked);

            if(confirmedList.contains(badgeItem.getBadgeName()) && !isChecked){
                confirmedList.remove(badgeItem.getBadgeName());
            }
        }
    }


    public void setCheckListener(CheckBox view){

        view.setOnCheckedChangeListener(this);
    }





    public class BadgeAdapter extends ArrayAdapter<BadgeItem> {

        private List<BadgeItem> badgeList;
        private Context context;
        private Typeface typeface;

        public BadgeAdapter(List<BadgeItem> badgeList, Context context, Typeface typeface, ListView listView) {
            super(context, R.layout.badge_list_item, badgeList);
            this.badgeList = badgeList;
            this.context = context;
            this.typeface = typeface;

        }

        private class BadgeItemHolder {
            ImageView badgeImage;
            TextView badgeName;
            CheckBox checkBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            BadgeItemHolder holder = null;

            if (convertView == null) {
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.badge_list_item, null);
                holder = new BadgeItemHolder();

                holder.badgeImage = (ImageView) convertView.findViewById(R.id.badge_image);
                holder.badgeName = (TextView) convertView.findViewById(R.id.badge_name);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                setCheckListener(holder.checkBox);

                convertView.setTag(holder);
                convertView.setTag(R.id.badge_image, holder.badgeImage);
                convertView.setTag(R.id.badge_name, holder.badgeName);
                convertView.setTag(R.id.checkBox, holder.checkBox);
            } else {
                holder = (BadgeItemHolder) convertView.getTag();
            }



            holder.checkBox.setTag(position);

            BadgeItem badgeItem = badgeList.get(position);
            holder.badgeName.setText(badgeItem.getBadgeName());
            holder.badgeName.setTypeface(typeface);
            holder.badgeImage.setImageResource(badgeItem.getBadgeImage());
            holder.checkBox.setChecked(badgeItem.isSelected());



            return convertView;
        }

    }

}



