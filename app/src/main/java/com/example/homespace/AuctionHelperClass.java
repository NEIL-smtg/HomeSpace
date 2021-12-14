package com.example.homespace;

public class AuctionHelperClass {

    String category,tenure,furnishing,title,description,itemType;
    float builtup,landArea,price;
    int rooms,bedrooms,toilets;

    public AuctionHelperClass(
            String category, String tenure, String furnishing,
            String title, String description, String itemType, float builtup,
            float landArea, float price, int rooms, int bedrooms, int toilets
    ) {
        this.category = category;
        this.tenure = tenure;
        this.furnishing = furnishing;
        this.title = title;
        this.description = description;
        this.itemType=itemType;
        this.builtup = builtup;
        this.landArea = landArea;
        this.price = price;
        this.rooms = rooms;
        this.bedrooms = bedrooms;
        this.toilets = toilets;
    }

    public AuctionHelperClass(){}

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

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
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
}
