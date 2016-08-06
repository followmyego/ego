package net.egobeta.ego.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.egobeta.ego.Fragments.Fragment_Main;
import net.egobeta.ego.ImportedClasses.FacebookPictureViewRound;
import net.egobeta.ego.R;
import net.egobeta.ego.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 30/06/2016.
 */
public class EgoStreamViewAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<String> arrList;
    private List<BadgeItem> badgeList;



    public EgoStreamViewAdapter(Context context, ArrayList<String> arrList) {
        this.context = context;
        this.arrList = arrList; /**This should hold be the facebook id's*/

    }

    public void setItems(ArrayList<String> arrList){
        this.arrList = arrList;
        notifyDataSetChanged();
    }

    public void addAllItems(ArrayList<String> addThisList){
        arrList.addAll(addThisList);
        notifyDataSetChanged();
    }

    public void addItem(String item){
        arrList.add(item);
        notifyDataSetChanged();
    }




    @Override
    public int getCount() {
        return arrList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView" + position + " " + convertView);

        ViewHolder viewHolder = null;


        if(convertView == null){
            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.stream_gridviewitem, null);
            viewHolder = new ViewHolder();
            viewHolder.userProfilePic = (RoundedImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

//        viewHolder.userProfilePic.setPresetSize(FacebookPictureViewRound.NORMAL);
//        try{
//            viewHolder.userProfilePic.setProfileId(arrList.get(position));
//        } catch (OutOfMemoryError e){
//            System.out.println("SOUT" + " backToTop e");
//            Fragment_Main.backToTop();
//        }

        new LoadUserImageAsyncTask(viewHolder.userProfilePic, context).execute(arrList.get(position));

        return convertView;
    }


    public class ViewHolder{
        RoundedImageView userProfilePic;

    }
}
