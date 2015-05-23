package com.example.edward.pacman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Edward on 19.05.2015.
 */
public class LocalUser extends User {
    Handler in, out;

    LocalUser(Handler handler) {
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

    @Override
    public Handler getHandler() {
        return in;
    }
    @Override
    public void SendBoard(int[][] board) {

    }
    @Override
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
    @Override
    public synchronized void EnterRoom(String line) {

    }
    @Override
    public synchronized void SpectateRoom(String line) {

    }
    @Override
    public void SendRoomList() {
        out.sendEmptyMessage(3);
    }
    @Override
    public synchronized void close() {

    }
    @Override
    public void SendState(GameState gs) {
        Message msg = out.obtainMessage(GAME_STATE,gs);
        out.sendMessage(msg);
    }
    @Override
    public void SendStart() {
        out.sendEmptyMessage(START_GAME);
    }
}
