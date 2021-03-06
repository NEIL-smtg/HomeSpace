package com.example.homespace;

import android.os.Parcel;
import android.os.Parcelable;

public class AuctionHelperClass implements Parcelable {

    String category,tenure,furnishing,title,description,itemType,pushID;
    float builtup,landArea,price;
    int bedrooms,toilets;

    public AuctionHelperClass(
            String category, String tenure, String furnishing,
            String title, String description, String itemType,String pushID, float builtup,
            float landArea, float price, int bedrooms, int toilets
    ) {
        this.category = category;
        this.tenure = tenure;
        this.furnishing = furnishing;
        this.title = title;
        this.description = description;
        this.itemType=itemType;
        this.pushID = pushID;
        this.builtup = builtup;
        this.landArea = landArea;
        this.price = price;
        this.bedrooms = bedrooms;
        this.toilets = toilets;
    }

    public AuctionHelperClass(){}

    protected AuctionHelperClass(Parcel in) {
        category = in.readString();
        tenure = in.readString();
        furnishing = in.readString();
        title = in.readString();
        description = in.readString();
        itemType = in.readString();
        pushID = in.readString();
        builtup = in.readFloat();
        landArea = in.readFloat();
        price = in.readFloat();
        bedrooms = in.readInt();
        toilets = in.readInt();
    }

    public static final Creator<AuctionHelperClass> CREATOR = new Creator<AuctionHelperClass>() {
        @Override
        public AuctionHelperClass createFromParcel(Parcel in) {
            return new AuctionHelperClass(in);
        }

        @Override
        public AuctionHelperClass[] newArray(int size) {
            return new AuctionHelperClass[size];
        }
    };

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getFurnishing() {
        return furnishing;
    }

    public void setFurnishing(String furnishing) {
        this.furnishing = furnishing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getBuiltup() {
        return builtup;
    }

    public void setBuiltup(float builtup) {
        this.builtup = builtup;
    }

    public float getLandArea() {
        return landArea;
    }

    public void setLandArea(float landArea) {
        this.landArea = landArea;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getToilets() {
        return toilets;
    }

    public void setToilets(int toilets) {
        this.toilets = toilets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(tenure);
        dest.writeString(furnishing);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(itemType);
        dest.writeString(pushID);
        dest.writeFloat(builtup);
        dest.writeFloat(landArea);
        dest.writeFloat(price);
        dest.writeInt(bedrooms);
        dest.writeInt(toilets);
    }
}
