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
        listOfNames.add("Friends");
        listOfNames.add("Friends of Friends");
        listOfNames.add("Follower");
        listOfNames.add("Following");
        listOfNames.add("Where you Live");
        listOfNames.add("Common Hometown");
        listOfNames.add("Common like");
        listOfNames.add("Same Birthday");
        listOfNames.add("Common Workplace");
        listOfNames.add("Common School");
        listOfNames.add("Music you love");
        listOfNames.add("Movies you love");
        listOfNames.add("Books you love");

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



//    @Override
//    public void onClick(View view) {

//        if(view == doneButton){
//            String final_fruit_selection = "";
//            for(String Selections : listOfNames)
//            {
//                final_fruit_selection = final_fruit_selection + Selections + "\n";
//            }
//            Toast.makeText(getActivity(), final_fruit_selection, Toast.LENGTH_LONG).show();
////            ((Main_OnBoarding) getActivity()).goToMainActivity();
//        } else {
//            boolean checked = ((CheckBox) view).isChecked();
//
//            if(view.getId() == R.id.checkBox1) {
//                if (checked) {
//                    listOfNames.add(0, "Friends");
//                    Toast.makeText(getActivity(), "Friends added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Friends");
//                    Toast.makeText(getActivity(), "Friends removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox2) {
//                if(checked){
//                    listOfNames.add(1, "Friends of Friends");
//                    Toast.makeText(getActivity(), "Friends of Friends added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Friends of Friends");
//                    Toast.makeText(getActivity(), "Friends of Friends removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox3){
//                if(checked){
//                    listOfNames.add(2, "Follower");
//                    Toast.makeText(getActivity(), "Follower added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Follower");
//                    Toast.makeText(getActivity(), "Follower removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox4){
//                if(checked){
//                    listOfNames.add(3, "Following");
//                    Toast.makeText(getActivity(), "Following added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Following");
//                    Toast.makeText(getActivity(), "Following removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox5){
//                if(checked){
//                    listOfNames.add(4, "Where you Live");
//                    Toast.makeText(getActivity(), "Where you Live added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Where you Live");
//                    Toast.makeText(getActivity(), "Where you Live removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox6){
//                if(checked){
//                    listOfNames.add(5, "Common Hometown");
//                    Toast.makeText(getActivity(), "Common Hometown added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Common Hometown");
//                    Toast.makeText(getActivity(), "Common Hometown removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox7){
//                if(checked){
//                    listOfNames.add(6, "Common like");
//                    Toast.makeText(getActivity(), "Common like added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Common like");
//                    Toast.makeText(getActivity(), "Common like removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox8){
//                if(checked){
//                    listOfNames.add(7, "Same Birthday");
//                    Toast.makeText(getActivity(), "Same Birthday added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Same Birthday");
//                    Toast.makeText(getActivity(), "Same Birthday removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox9){
//                if(checked){
//                    listOfNames.add(8, "Common Workplace");
//                    Toast.makeText(getActivity(), "Common Workplace added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Common Workplace");
//                    Toast.makeText(getActivity(), "Common Workplace removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox10){
//                if(checked){
//                    listOfNames.add(9, "Common School");
//                    Toast.makeText(getActivity(), "Common School added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Common School");
//                    Toast.makeText(getActivity(), "Common School removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox11){
//                if(checked){
//                    listOfNames.add(10, "Music you love");
//                    Toast.makeText(getActivity(), "Music you love added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Music you love");
//                    Toast.makeText(getActivity(), "Music you love removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox12){
//                if(checked){
//                    listOfNames.add(11, "Movies you love");
//                    Toast.makeText(getActivity(), "Movies you love added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Movies you love");
//                    Toast.makeText(getActivity(), "Movies you love removed", Toast.LENGTH_LONG).show();
//                }
//            } else if(view.getId() == R.id.checkBox13){
//                if(checked){
//                    listOfNames.add(12, "Books you love");
//                    Toast.makeText(getActivity(), "Books you love added", Toast.LENGTH_LONG).show();
//                } else {
//                    listOfNames.remove("Books you love");
//                    Toast.makeText(getActivity(), "Books you love removed", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        }
//    }


//    public class BadgeListAdapter extends BaseAdapter {
//
//
//        private int[] imagesList;
//        private ArrayList<String> namesList;
//
//
//        public BadgeListAdapter(int[] images, ArrayList<String> names) {
//            this.imagesList = images;
//            this.namesList = names;
//        }
//
//        public void setItems(ArrayList<String> arrList){
//            this.namesList = arrList;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return namesList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return namesList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            System.out.println("getView" + position + " " + convertView);
//
//            LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = mInflater.inflate(R.layout.badge_list_item, null);
//            ImageView badgeImage = (ImageView) convertView.findViewById(R.id.badge_image);
//            TextView badgeName = (TextView) convertView.findViewById(R.id.badge_name);
//            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.radioButton);
//
//
//
//            badgeImage.setImageResource(imagesList[position]);
//            badgeName.setText(namesList.get(position));
//            badgeName.setTypeface(typeface);
//
//
//
//
//
//            return convertView;
//        }
//
//
////        public class ViewHolder{
////            ImageView badgeImage;
////            TextView badgeName;
////            RadioButton radioButton;
////        }
//    }
}



