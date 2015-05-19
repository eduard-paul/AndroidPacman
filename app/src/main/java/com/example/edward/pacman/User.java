package com.example.edward.pacman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by edward on 16.05.15.
 */
public class User {
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
    Handler in, out;

    User(Handler handler) {
        playerId = 1;
        out = handler;
        in = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, Integer.toString(msg.what));
                switch (msg.what) {
                    case 1:
                        CreateRoom("Roomname",msg.arg1);
                        break;
                    case 2:
                        LeaveRoom();
                        break;
                    case TURN:
                        myRoom.Command(msg.arg1,playerId);
                        break;
                }
            }
        };
    }

    public Handler getHandler() {
        return in;
    }

    public void SendBoard(int[][] board) {

    }

    public synchronized void LeaveRoom() {
        if ("spectate".equals(myRoomName))
            myRoom.RemoveSpectator(this);
        else
            myRoom.RemovePlayer(this);
        myRoomName = "";
        myRoom = null;
        playerId = -1;
    }

    public synchronized void CustomRoom(String line) {

    }

    public synchronized void CreateRoom(String name, int maxPlayers) {
        myRoom = new Room(name, maxPlayers, this);
        myRoomName = name;
    }

    public synchronized void EnterRoom(String line) {

    }

    public synchronized void SpectateRoom(String line) {

    }

    public void SendRoomList() {
        out.sendEmptyMessage(3);
    }

    public synchronized void close() {

    }

    public void SendState(GameState gs) {
        Message msg = out.obtainMessage(GAME_STATE,gs);
        out.sendMessage(msg);
    }

    public void SendStart() {
        out.sendEmptyMessage(START_GAME);
    }
}
