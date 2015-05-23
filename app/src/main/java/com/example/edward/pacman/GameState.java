package com.example.edward.pacman;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class GameState implements Parcelable, Serializable{
    public List<CharacterState> cs;
    public int[][] board;

    public GameState(List<CharacterState> cs, int[][] board) {
        this.cs = cs;
        this.board = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, board[0].length);
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
        for (int[] ints : board) {
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
