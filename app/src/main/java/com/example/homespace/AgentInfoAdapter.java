package com.example.homespace;

import android.os.Parcel;
import android.os.Parcelable;

public class AgentInfoAdapter implements Parcelable {

    String profilePic, title, thumbnail, id, itemType, pushID;

    public AgentInfoAdapter(String profilePic, String title, String thumbnail, String id, String itemType, String pushID) {
        this.profilePic = profilePic;
        this.title = title;
        this.thumbnail = thumbnail;
        this.id = id;
        this.itemType = itemType;
        this.pushID = pushID;
    }

    public AgentInfoAdapter(){}

    protected AgentInfoAdapter(Parcel in) {
        profilePic = in.readString();
        title = in.readString();
        thumbnail = in.readString();
        id = in.readString();
        itemType = in.readString();
        pushID = in.readString();
    }

    public static final Creator<AgentInfoAdapter> CREATOR = new Creator<AgentInfoAdapter>() {
        @Override
        public AgentInfoAdapter createFromParcel(Parcel in) {
            return new AgentInfoAdapter(in);
        }

        @Override
        public AgentInfoAdapter[] newArray(int size) {
            return new AgentInfoAdapter[size];
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

    public AgentInfoAdapter(String id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePic);
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeString(id);
        dest.writeString(itemType);
        dest.writeString(pushID);
    }
}
