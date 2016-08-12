package net.egobeta.ego.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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
    private ArrayList<String> arrList;
    private List<UserItem> userList;



    public EgoStreamViewAdapter(List<UserItem> userList, Context context, ArrayList<String> arrList, Activity activity) {
        this.context = context;
        this.arrList = arrList; /**This should hold be the facebook id's*/
        this.activity = activity;
        this.userList = userList;
        notifyDataSetChanged();
    }

    public class UserViewHolder {
        RoundedImageView userProfilePic;
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
//            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stream_gridviewitem, null);
            holder = new UserViewHolder();

            holder.userProfilePic = (RoundedImageView) convertView.findViewById(R.id.img);

            convertView.setTag(holder);
            convertView.setTag(R.id.img, holder.userProfilePic);
        } else {
            System.out.println("EgoStreamViewAdapter: TWO");
            holder = (UserViewHolder)convertView.getTag();
        }

        holder.userProfilePic.setTag(position);

        UserItem userItem = userList.get(position);
        if(userItem.getProfilePicture() == null){
            System.out.println("EgoStreamViewAdapter: THREE");
            userItem.setViewItem(holder.userProfilePic);
        } else {
            System.out.println("EgoStreamViewAdapter: FOUR");
            holder.userProfilePic.setImageDrawable(userItem.getProfilePicture());
        }

        return convertView;
    }



}
