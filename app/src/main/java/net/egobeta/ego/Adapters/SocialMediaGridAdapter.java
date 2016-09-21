package net.egobeta.ego.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.egobeta.ego.R;

/**
 * Created by Lucas on 16/09/2016.
 */
public class SocialMediaGridAdapter extends BaseAdapter {

    //Declare variables
    private Context context;
    private Activity activity;
    private final Integer[] images;

    //Constructor for this class
    public SocialMediaGridAdapter(Context context, Integer[] images, Activity activity) {
        this.context = context;
        this.images = images;
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.grid_view_social_media, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.socialMediaLink);


        // Read your drawable from somewhere
        Drawable dra = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            dra = activity.getDrawable(images[position]);
        }
        else
        {
            dra = activity.getResources().getDrawable(images[position]);
        }
        assert dra != null;
        Bitmap bitmapa = ((BitmapDrawable) dra).getBitmap();
        // Scale it to 50 x 50
        Drawable d = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmapa, 150, 150, true));
        imageView.setImageDrawable(d);
        return rowView;
    }




}