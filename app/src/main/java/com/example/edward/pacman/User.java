package com.example.edward.pacman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by edward on 16.05.15.
 */
public class User extends Thread {
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
                Log.d(TAG,Integer.toString(msg.what));
                out.sendEmptyMessage(2);
                switch (msg.what) {
                    case 1:
                        CreateRoom("Roomname",msg.arg1);
                        break;
                    case 2:
                        LeaveRoom();
                        break;
                }
            }
        };
    }

    @Override
    public void run() {
        while (true) {

        }
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
        out.sendEmptyMessage(4);
    }

    public void SendStart() {
        out.sendEmptyMessage(0);
    }
}
