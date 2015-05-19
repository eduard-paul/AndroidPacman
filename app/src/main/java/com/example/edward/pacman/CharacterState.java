package com.example.edward.pacman;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edward on 16.05.15.
 */
public class CharacterState implements Parcelable{
    public int id;
    public Point cell;
    public double dist;
    public int direction, speed;

    public CharacterState() {
    }

    public CharacterState(Parcel parcel) {
        id = parcel.readInt();
        cell = new Point(parcel.readInt(),parcel.readInt());
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
        parcel.writeInt(cell.x);
        parcel.writeInt(cell.y);
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
