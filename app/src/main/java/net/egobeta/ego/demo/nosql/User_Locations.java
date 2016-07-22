package net.egobeta.ego.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lucas on 15/07/2016.
 */
@DynamoDBTable(tableName = "User_Locations")
public class User_Locations {
    private String hashKey;
    private String facebookId;
    private String geoJson;
    private String geoHash;

    @DynamoDBHashKey(attributeName = "hashKey")
    @DynamoDBAttribute(attributeName = "hashKey")
    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    @DynamoDBRangeKey(attributeName = "rangeKey")
    @DynamoDBIndexHashKey(attributeName = "rangeKey")
    public String getFacebookId(){
        return facebookId;
    }

    public void setFacebookId(String facebookId){
        this.facebookId = facebookId;
    }

    @DynamoDBAttribute(attributeName = "geoJson")
    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    @DynamoDBAttribute(attributeName = "geohash")
    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }
}
