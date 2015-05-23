package com.example.edward.pacman;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CharacterState implements Parcelable, Serializable{
    public int id, winnerId;
    public int x, y;
    public double dist;
    public int direction, speed;

    public CharacterState() {
    }

    public CharacterState(Parcel parcel) {
        id = parcel.readInt();
        x = parcel.readInt(); y = parcel.readInt();
        dist = parcel.readDouble();
        direction = parcel.readInt();
        speed = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeDouble(dist);
        parcel.writeInt(direction);
        parcel.writeInt(speed);
    }

    public static final Parcelable.Creator<CharacterState> CREATOR
            = new Parcelable.Creator<CharacterState>(){

        @Override
        public CharacterState createFromParcel(Parcel parcel) {
            return (new CharacterState(parcel));
        }

        @Override
        public CharacterState[] newArray(int i) {
            return new CharacterState[i];
        }
    };
}
