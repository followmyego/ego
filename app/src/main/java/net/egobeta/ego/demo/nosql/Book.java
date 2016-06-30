package net.egobeta.ego.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Lucas on 20/06/2016.
 */
@DynamoDBTable(tableName = "Books")
public class Book {
    private String title;
    private String author;
    private int price = 0;
    private String isbn;
    private Boolean hardCover;
    private Boolean awesome;

    @DynamoDBHashKey(attributeName = "ISBN")
    @DynamoDBAttribute(attributeName = "ISBN")
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @DynamoDBIndexRangeKey(attributeName = "Title")
    @DynamoDBAttribute(attributeName = "Title")
    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    @DynamoDBIndexHashKey(attributeName = "Author")
    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @DynamoDBAttribute(attributeName = "Price")
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "Hardcover")
    public Boolean getHardCover() {
        return hardCover;
    }

    public void setHardCover(Boolean hardCover) {
        this.hardCover = hardCover;
    }

    @DynamoDBAttribute(attributeName = "Awesome(Title)")
    public Boolean getAwesome() {
        return awesome;
    }

    public void setAwesome(Boolean awesome) {
        this.awesome = awesome;
    }
}
