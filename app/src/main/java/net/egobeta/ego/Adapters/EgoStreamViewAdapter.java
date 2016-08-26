package net.egobeta.ego.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.egobeta.ego.R;
import net.egobeta.ego.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 30/06/2016.
 */
public class EgoStreamViewAdapter extends BaseAdapter {

    private final Context context;
    private Activity activity;
    private ArrayList<String> friends_Ids;
    private List<UserItem> userList;
    ArrayList<Integer> badgeImages = new ArrayList<>();



    public EgoStreamViewAdapter(List<UserItem> userList, Context context, ArrayList<String> friends_Ids, Activity activity) {
        this.context = context;
        this.friends_Ids = friends_Ids; /**This should hold be the facebook id's*/
        this.activity = activity;
        this.userList = userList;
        notifyDataSetChanged();
        initializeBadgeImageArrayList();
    }

    private void initializeBadgeImageArrayList() {
        badgeImages.add(R.drawable.common_workplace);
        badgeImages.add(R.drawable.shared_birthday);
        badgeImages.add(R.drawable.same_school); //this should be swapped out with skills image
        badgeImages.add(R.drawable.loves_the_same_music);
        badgeImages.add(R.drawable.loves_the_same_books);
        badgeImages.add(R.drawable.loves_the_same_movie);
        badgeImages.add(R.drawable.same_school);
        badgeImages.add(R.drawable.same_hometown);
        badgeImages.add(R.drawable.location_icon);
        badgeImages.add(R.drawable.common_like);

    }

    public class UserViewHolder {
        RoundedImageView userProfilePic;
        ImageView badgeBackground;
        ImageView badge;
    }


    public void setItems(List<UserItem> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void addAllItems(List<UserItem> addThisList){
        userList.addAll(addThisList);
        notifyDataSetChanged();
    }

    public void addItem(UserItem item){
        userList.add(item);
        notifyDataSetChanged();
    }




    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView" + position + " " + convertView);

        UserViewHolder holder = null;


        if(convertView == null){
            System.out.println("EgoStreamViewAdapter: ONE");
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.stream_gridviewitem, null);
            holder = new UserViewHolder();

            holder.userProfilePic = (RoundedImageView) convertView.findViewById(R.id.img);
            holder.badgeBackground = (ImageView) convertView.findViewById(R.id.badge_background);
            holder.badge = (ImageView) convertView.findViewById(R.id.friend_badge);
            convertView.setTag(holder);
            convertView.setTag(R.id.img, holder.userProfilePic);
            convertView.setTag(R.id.badge_background, holder.badgeBackground);
            convertView.setTag(R.id.friend_badge, holder.badge);
        } else {
            System.out.println("EgoStreamViewAdapter: TWO");
            holder = (UserViewHolder)convertView.getTag();
        }

        holder.userProfilePic.setTag(position);

        UserItem userItem = userList.get(position);

        //Detect if the person is a friend or not
        boolean isFriend = friends_Ids.contains(userItem.getFacebookId());



//        if(userItem.getProfilePicture() == null){
//            System.out.println("EgoStreamViewAdapter: THREE");
//
//            if(isFriend){
//                holder.badgeBackground.setVisibility(View.VISIBLE);
//                holder.badge.setVisibility(View.VISIBLE);
//                System.out.println("EgoStreamViewAdapter: friend");
//            }
//            userItem.setViewItem(holder.userProfilePic, isFriend);
//
//
//        } else {
//            System.out.println("EgoStreamViewAdapter: FOUR");
//
//            if(isFriend){
//                holder.badgeBackground.setVisibility(View.VISIBLE);
//                holder.badge.setVisibility(View.VISIBLE);
//                System.out.println("EgoStreamViewAdapter: friend");
//            }
//
//            holder.userProfilePic.setImageDrawable(userItem.getProfilePicture());
//        }
        if(isFriend){
            holder.badgeBackground.setVisibility(View.VISIBLE);
            holder.badge.setImageResource(R.drawable.friend);
            holder.badge.setVisibility(View.VISIBLE);
            System.out.println("EgoStreamViewAdapter: friend");
        } else {
            //Check what badge we need to display.
            for(int i = 0; i < 10; i ++){
                if(i != 0){
                    if(userItem.getBadge() == i){
                        holder.badgeBackground.setVisibility(View.VISIBLE);
                        holder.badge.setImageResource(badgeImages.get(i));
                        holder.badge.setVisibility(View.VISIBLE);
                    }

                }

            }
        }
        Picasso.with(context)
                .load("https://graph.facebook.com/" + userItem.getFacebookId() + "/picture?width=190&height=190")
                .placeholder(R.drawable.default_user_image)
                .into(holder.userProfilePic);




        return convertView;
    }



}
