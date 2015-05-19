package com.example.edward.pacman;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by edward on 16.05.15.
 */
public class GameState implements Parcelable{
    public List<CharacterState> cs;
    public int[][] board;

    public GameState(List<CharacterState> cs, int[][] board) {
        this.cs = cs;
        this.board = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }

    public GameState(Parcel parcel) {
        parcel.readList(cs,List.class.getClassLoader());
        int length = parcel.readInt();
        board = new int[length][];
        for (int i = 0; i < length; i++) {
            parcel.readIntArray(board[i]);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(cs);
        parcel.writeInt(board.length);
        for (int j = 0; j < board.length; j++) {
            int[] ints = board[j];
            parcel.writeIntArray(ints);
        }
    }

    public static final Parcelable.Creator<GameState> CREATOR
            = new Parcelable.Creator<GameState>(){

        @Override
        public GameState createFromParcel(Parcel parcel) {
            return new GameState(parcel);
        }

        @Override
        public GameState[] newArray(int i) {
            return new GameState[i];
        }
    };
}
