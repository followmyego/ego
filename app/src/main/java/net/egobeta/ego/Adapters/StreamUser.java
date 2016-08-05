package net.egobeta.ego.Adapters;

import android.graphics.drawable.Drawable;

/**
 * Created by Lucas on 05/08/2016.
 */
public class StreamUser {
    String facebookId;
    Drawable facebookPic;

    public StreamUser(String facebookId, Drawable facebookPic){
        super();
        this.facebookId = facebookId;
        this.facebookPic = facebookPic;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Drawable getFacebookPic() {
        return facebookPic;
    }

    public void setFacebookPic(Drawable facebookPic) {
        this.facebookPic = facebookPic;
    }
}
