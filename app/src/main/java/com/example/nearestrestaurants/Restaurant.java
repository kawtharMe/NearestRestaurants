package com.example.nearestrestaurants;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Restaurant implements Parcelable {
    private String name, address;
    private double longitude,latitude;
    public Restaurant(String n, String a, double la, double lon){
        this.name = n;
        this.address = a;
        this.longitude = lon;
        this.latitude = la;
    }

    protected Restaurant(Parcel in) {
        name = in.readString();
        address = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
    }
}
