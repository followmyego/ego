package net.egobeta.ego.Table_Classes;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lucas on 20/06/2016.
 */
@DynamoDBTable(tableName = "user_profile")
public class User_Profile {


    private String facebookId;
    private String status;
    private int views;
    private String firstName;
    private String lastName;
    private String age;
    private String email;

    private String snapchat_username;
    private int instagram_id;
    private int twitter_id;
    private int google_plus_id;
    private int linkedIn_id;

    @DynamoDBHashKey(attributeName = "facebook_id")
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = "views")
    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @DynamoDBAttribute(attributeName = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute(attributeName = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute(attributeName = "age")
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @DynamoDBAttribute(attributeName = "snapchat_username")
    public String getSnapchat_username() {
        return snapchat_username;
    }

    public void setSnapchat_username(String snapchat_username) {
        this.snapchat_username = snapchat_username;
    }

    @DynamoDBAttribute(attributeName = "instagram_id")
    public int getInstagram_id() {
        return instagram_id;
    }

    public void setInstagram_id(int instagram_id) {
        this.instagram_id = instagram_id;
    }

    @DynamoDBAttribute(attributeName = "twitter_id")
    public int getTwitter_id() {
        return twitter_id;
    }

    public void setTwitter_id(int twitter_id) {
        this.twitter_id = twitter_id;
    }

    @DynamoDBAttribute(attributeName = "googlePlus_id")
    public int getGoogle_plus_id() {
        return google_plus_id;
    }

    public void setGoogle_plus_id(int google_plus_id) {
        this.google_plus_id = google_plus_id;
    }

    @DynamoDBAttribute(attributeName = "linkedIn_id")
    public int getLinkedIn_id() {
        return linkedIn_id;
    }

    public void setLinkedIn_id(int linkedIn_id) {
        this.linkedIn_id = linkedIn_id;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

