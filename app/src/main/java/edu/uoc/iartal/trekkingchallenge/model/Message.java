package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

// Message object class. Implements Parcelable to pass message object between activities
public class Message implements Parcelable{

    private String id, text, user, group, trip;

    // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    public Message(){

    }

    public Message(String id, String text, String group, String user, String trip) {
        this.id = id;
        this.text = text;
        this.group = group;
        this.user = user;
        this.trip = trip;
    }

    public Message(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.group = in.readString();
        this.user = in.readString();
        this.trip = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(group);
        dest.writeString(user);
        dest.writeString(trip);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
