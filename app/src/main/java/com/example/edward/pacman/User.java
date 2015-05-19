package com.example.edward.pacman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by edward on 16.05.15.
 */
abstract public class User {
    public static final int NEW_ROOM = 1;
    public static final int LEAVE_ROOM = 2;
    public static final int START_GAME = 3;
    public static final int GAME_STATE = 4;
    public static final int TURN = 5;
    public static final int RIGHT = 1;
    public static final int LEFT = -1;
    public static final int UP = 2;
    public static final int DOWN = -2;

    String TAG = "PacUser";
    String myRoomName = "";
    Room myRoom = null;
    int playerId = -1;

    public abstract Handler getHandler();

    public abstract void SendBoard(int[][] board);

    public abstract void LeaveRoom();

    public abstract void CustomRoom(String line);

    public abstract void CreateRoom(String name, int maxPlayers);

    public abstract void EnterRoom(String line);

    public abstract void SpectateRoom(String line);

    public abstract void SendRoomList();

    public abstract void close();

    public abstract void SendState(GameState gs);

    public abstract void SendStart();
}
