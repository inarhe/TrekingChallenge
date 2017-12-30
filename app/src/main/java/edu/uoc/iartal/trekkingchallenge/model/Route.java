package edu.uoc.iartal.trekkingchallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

// Route object class. Implements Parcelable to pass route object between activities
public class Route implements Parcelable {
    private String idRoute, name, headerPhoto, trackPhoto, profilePhoto, season, startPlace, time, trackLink, meteo;
    private String type, difficult, distance, description, region, township;
    private Double lng, lat;
    private Float sumRatings;
    private int numRatings, decline, ascent;
    private Map<String, String> finished = new HashMap<>();

    public Route() {

    }

    public Route(String idRoute, String name, String headerPhoto, String trackPhoto, String startPplace, String profilePhoto, String season, String time,
                 String trackLink, String meteo, String type, Double lng, Double lat, Float sumRatings, String distance, String difficult, String description,
                 int decline, int ascent, String region, String township, int numRatings) {
        this.idRoute = idRoute;
        this.name = name;
        this.headerPhoto = headerPhoto;
        this.trackPhoto = trackPhoto;
        this.profilePhoto = profilePhoto;
        this.startPlace = startPplace;
        this.season = season;
        this.time = time;
        this.trackLink = trackLink;
        this.meteo = meteo;
        this.type = type;
        this.lng = lng;
        this.lat = lat;
        this.sumRatings = sumRatings;
        this.distance = distance;
        this.difficult = difficult;
        this.description = description;
        this.decline = decline;
        this.ascent = ascent;
        this.region = region;
        this.township = township;
        this.numRatings = numRatings;
    }

    public Route(Parcel in) {
        this.idRoute = in.readString();
        this.name = in.readString();
        this.headerPhoto = in.readString();
        this.trackPhoto = in.readString();
        this.profilePhoto = in.readString();
        this.startPlace = in.readString();
        this.season = in.readString();
        this.time = in.readString();
        this.trackLink = in.readString();
        this.meteo = in.readString();
        this.type = in.readString();
        this.sumRatings = in.readFloat();
        this.distance = in.readString();
        this.difficult = in.readString();
        this.description = in.readString();
        this.decline = in.readInt();
        this.ascent = in.readInt();
        this.region = in.readString();
        this.township = in.readString();
        this.numRatings = in.readInt();
        in.readMap(finished, String.class.getClassLoader());
    }

    public String getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(String idRoute) {
        this.idRoute = idRoute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderPhoto() {
        return headerPhoto;
    }

    public void setHeaderPhoto(String headerPhoto) {
        this.headerPhoto = headerPhoto;
    }

    public String getTrackPhoto() {
        return trackPhoto;
    }

    public void setTrackPhoto(String trackPhoto) {
        this.trackPhoto = trackPhoto;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrackLink() {
        return trackLink;
    }

    public void setTrackLink(String trackLink) {
        this.trackLink = trackLink;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getMeteo() {
        return meteo;
    }

    public void setMeteo(String meteo) {
        this.meteo = meteo;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Float getSumRatings() {
        return sumRatings;
    }

    public void setSumRatings(Float sumRatings) {
        this.sumRatings = sumRatings;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDecline() {
        return decline;
    }

    public void setDecline(Integer decline) {
        this.decline = decline;
    }

    public Integer getAscent() {
        return ascent;
    }

    public void setAscent(Integer ascent) {
        this.ascent = ascent;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public Map<String, String> getFinished() {
        return this.finished;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idRoute);
        dest.writeString(name);
        dest.writeString(headerPhoto);
        dest.writeString(trackPhoto);
        dest.writeString(profilePhoto);
        dest.writeString(startPlace);
        dest.writeString(season);
        dest.writeString(time);
        dest.writeString(trackLink);
        dest.writeString(meteo);
        dest.writeString(type);
        dest.writeFloat(sumRatings);
        dest.writeString(distance);
        dest.writeString(difficult);
        dest.writeString(description);
        dest.writeInt(decline);
        dest.writeInt(ascent);
        dest.writeString(region);
        dest.writeString(township);
        dest.writeInt(numRatings);
        dest.writeMap(finished);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {

        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

}
