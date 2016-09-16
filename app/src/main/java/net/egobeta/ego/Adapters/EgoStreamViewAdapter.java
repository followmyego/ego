package net.egobeta.ego.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.egobeta.ego.InstagramClasses.ImageLoader;
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
    private ArrayList<UserItem> userList;
    private static LayoutInflater inflater = null;
    ImageLoader imageLoader;
    String facebookId;
    Typeface typeface;

    ArrayList<Integer> badgeImages = new ArrayList<>();



    public EgoStreamViewAdapter(ArrayList<UserItem> userList, Context context,
                                ArrayList<String> friends_Ids, Activity activity,
                                String facebookId, Typeface typeface) {
        this.context = context;
        this.friends_Ids = friends_Ids; /**This should hold be the facebook id's*/
        this.activity = activity;
        this.userList = userList;
        this.facebookId = facebookId;
        this.typeface = typeface;
        initializeBadgeImageArrayList();

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    private void initializeBadgeImageArrayList() {
        badgeImages = new ArrayList<>();
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
        TextView youTextOverlay;
        ImageView youOverlay;
        ImageView nearbyOverlay;

    }


    public void setItems(ArrayList<UserItem> userList){
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
        System.out.println("getItem badge#: " + position + " " + userList.get(position).getBadge());
        UserViewHolder holder = null;



        if(convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.stream_gridviewitem, null);
            holder = new UserViewHolder();

            holder.userProfilePic = (RoundedImageView) convertView.findViewById(R.id.img);
            holder.badgeBackground = (ImageView) convertView.findViewById(R.id.badge_background);
            holder.badge = (ImageView) convertView.findViewById(R.id.friend_badge);
            holder.youTextOverlay = (TextView) convertView.findViewById(R.id.text_overlay);
            holder.youOverlay = (ImageView) convertView.findViewById(R.id.overlay);
            holder.nearbyOverlay = (ImageView) convertView.findViewById(R.id.nearby_overlay);


            convertView.setTag(holder);
            convertView.setTag(R.id.friend_badge, holder.badge);
            convertView.setTag(R.id.img, holder.userProfilePic);
            convertView.setTag(R.id.overlay, holder.youOverlay);
            convertView.setTag(R.id.nearby_overlay, holder.nearbyOverlay);
            convertView.setTag(R.id.text_overlay, holder.youTextOverlay);
        } else {
            holder = (UserViewHolder) convertView.getTag();
        }

        holder.badge.setTag(position);
        holder.userProfilePic.setTag(position);
        holder.youOverlay.setTag(position);
        holder.nearbyOverlay.setTag(position);
        holder.youTextOverlay.setTag(position);

        UserItem userItem = userList.get(position);

        //Detect if the person is a friend or not
        boolean isFriend = friends_Ids.contains(userItem.getFacebookId());
        boolean isPinned = userItem.isPinned();



        if(isPinned){
            holder.nearbyOverlay.setVisibility(View.INVISIBLE);
            holder.userProfilePic.setVisibility(View.VISIBLE);
            holder.badgeBackground.setVisibility(View.VISIBLE);
            holder.badge.setImageResource(R.drawable.pinned_to_the_stream);
            holder.badge.setVisibility(View.VISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);
            System.out.println("EgoStreamViewAdapter: friend");
        } else if(isFriend) {
            holder.nearbyOverlay.setVisibility(View.INVISIBLE);
            holder.userProfilePic.setVisibility(View.VISIBLE);
            holder.badgeBackground.setVisibility(View.VISIBLE);
            holder.badge.setImageResource(R.drawable.friend);
            holder.badge.setVisibility(View.VISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);
            System.out.println("EgoStreamViewAdapter: friend");
        } else {
            //Check what badge we need to display.
            int badge = userItem.getBadge();
            if(facebookId.equals(userItem.getFacebookId())){
                holder.nearbyOverlay.setVisibility(View.INVISIBLE);
                holder.userProfilePic.setVisibility(View.VISIBLE);
                holder.badgeBackground.setVisibility(View.INVISIBLE);
                holder.badge.setVisibility(View.INVISIBLE);
                holder.youTextOverlay.setVisibility(View.VISIBLE);
                holder.youOverlay.setVisibility(View.VISIBLE);

                holder.youTextOverlay.setTypeface(typeface);
            }else if(badge == 0){
                holder.nearbyOverlay.setVisibility(View.INVISIBLE);
                System.out.println("badge is 0");
                holder.userProfilePic.setVisibility(View.VISIBLE);
                holder.badgeBackground.setVisibility(View.INVISIBLE);
                holder.badge.setVisibility(View.INVISIBLE);
                holder.youTextOverlay.setVisibility(View.INVISIBLE);
                holder.youOverlay.setVisibility(View.INVISIBLE);
            } else {
                holder.nearbyOverlay.setVisibility(View.INVISIBLE);
                System.out.println("badge is " + badge);
                holder.userProfilePic.setVisibility(View.VISIBLE);
                holder.badgeBackground.setVisibility(View.VISIBLE);
                holder.badge.setImageResource(badgeImages.get(badge - 1));
                holder.badge.setVisibility(View.VISIBLE);
                holder.youTextOverlay.setVisibility(View.INVISIBLE);
                holder.youOverlay.setVisibility(View.INVISIBLE);
            }
        }

        if (userItem.getFacebookId() != null){
            imageLoader.DisplayImage("https://graph.facebook.com/" + userItem.getFacebookId() + "/picture?width=500&height=500"
                    , holder.userProfilePic);
        } else {
            holder.nearbyOverlay.setVisibility(View.INVISIBLE);
            holder.userProfilePic.setVisibility(View.INVISIBLE);
            holder.badgeBackground.setVisibility(View.INVISIBLE);
            holder.badge.setVisibility(View.INVISIBLE);
            holder.youTextOverlay.setVisibility(View.INVISIBLE);
            holder.youOverlay.setVisibility(View.INVISIBLE);
        }

        if(userItem.isNearby){
            holder.nearbyOverlay.setVisibility(View.VISIBLE);
        } else {
            holder.nearbyOverlay.setVisibility(View.INVISIBLE);
        }





        return convertView;
    }



}
