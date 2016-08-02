package net.egobeta.ego.Fragments;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

import net.egobeta.ego.Interfaces.ScrollTabHolder;

/**
 * Created by Lucas on 29/06/2016.
 */
public abstract class ScrollTabHolderFragment extends Fragment implements ScrollTabHolder {

    protected ScrollTabHolder mScrollTabHolder;

    public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
        mScrollTabHolder = scrollTabHolder;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        // nothing
    }


}
