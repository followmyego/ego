package net.egobeta.ego.Adapters;

/**
 * Created by Lucas on 03/08/2016.
 */

public class BadgeItem{
    String badgeName;
    int badgeImage;
    boolean selected = true;

    public BadgeItem(String badgeName, int badgeImage){
        super();
        this.badgeName = badgeName;
        this.badgeImage = badgeImage;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public int getBadgeImage() {
        return badgeImage;
    }

    public void setBadgeImage(int badgeImage) {
        this.badgeImage = badgeImage;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
