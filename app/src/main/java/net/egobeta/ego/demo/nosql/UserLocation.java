package net.egobeta.ego.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lucas on 20/06/2016.
 */
@DynamoDBTable(tableName = "User_Locations")
public class UserLocation {
    private String userId;
    private String facebookId;
    private String longitude;
    private String latitude;



    @DynamoDBHashKey(attributeName = "user_id")
    @DynamoDBAttribute(attributeName = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = "facebook_id")
    public String getFacebookId(){
        return facebookId;
    }

    public void setFacebookId(String facebookId){
        this.facebookId = facebookId;
    }

    @DynamoDBIndexHashKey(attributeName = "longitude")
    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @DynamoDBAttribute(attributeName = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


}

