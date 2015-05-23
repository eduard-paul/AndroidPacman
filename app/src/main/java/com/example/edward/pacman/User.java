package com.example.edward.pacman;

import android.os.Handler;

abstract public class User {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int NEW_ROOM = 1;
    public static final int LEAVE_ROOM = 2;
    public static final int START_GAME = 3;
    public static final int GAME_STATE = 4;
    public static final int TURN = 5;
    public static final int REFRESH = 6;
    public static final int ENTER_ROOM = 7;
    public static final int RIGHT = 1;
    public static final int LEFT = -1;
    public static final int UP = 2;
    public static final int DOWN = -2;

    String TAG = "PacUser";
    String myRoomName = "";
    Room myRoom = null;
    int playerId = -1;
    Server server = null;

    public abstract Handler getHandler();

    public abstract void LeaveRoom();

    public abstract void EnterRoom(String line);

    public abstract void SendRoomList();

    public abstract void close();

    public abstract void SendState(GameState gs);

    public abstract void SendStart();
}
