package com.example.edward.pacman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

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
                    case NEW_ROOM:
                        if (myRoom==null) {
                            String name = (String) msg.obj;
                            CreateRoom(name, msg.arg1);
                        }
                        break;
                    case LEAVE_ROOM:
                        LeaveRoom();
                        break;
                    case TURN:
                        if (myRoom!=null) myRoom.Command(msg.arg1,playerId);
                        break;
                    case REFRESH:
                        SendRoomList();
                        break;
                    case ENTER_ROOM:
                        EnterRoom((String) msg.obj);
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
    public void setServer(Server server){
        this.server = server;
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
        if (server == null) {
            myRoom = new Room(name, maxPlayers, this);
            myRoomName = name;
            out.sendMessage(out.obtainMessage(NEW_ROOM, SUCCESS, 0));
        } else {
            boolean failed = false;
            for (Room room : server.rooms) { // Check if the same already exists
                if (room.name.equals(name))
                    failed = true;
            }
            if (!failed) {
                myRoom = new Room(server, name, maxPlayers, this);
                server.rooms.offer(myRoom);
                myRoomName = name;
                out.sendMessage(out.obtainMessage(NEW_ROOM,SUCCESS,0));
            } else {
                out.sendMessage(out.obtainMessage(NEW_ROOM,FAIL,0));
            }
        }
    }
    @Override
    public synchronized void EnterRoom(String name) {
        boolean failed = true;
        for (Room room : server.rooms) {
            if (room.name.equals(name)) {
                if (room.currPlayers < room.maxPlayers && !room.IsStarted()) {
                    room.AddPlayer(this);
                    myRoomName = name;
                    myRoom = room;
                    failed = false;
                } else {
                    room.AddSpectator(this);
                    myRoomName = name;
                    myRoom = room;
                    failed = false;
                }
            }
        }
        if (!failed) {
            out.sendMessage(out.obtainMessage(ENTER_ROOM,SUCCESS,0));
        } else {
            out.sendMessage(out.obtainMessage(ENTER_ROOM,FAIL,0));
        }
    }
    @Override
    public synchronized void SpectateRoom(String line) {

    }
    @Override
    public void SendRoomList() {
        Message msg;
        String list = new String();
        for (Room room : server.rooms) {
            list += ":" + room.name + " " + "[" + room.currPlayers
                    + "/" + room.maxPlayers + "]";
        }
        if (list.isEmpty()) {
            msg = out.obtainMessage(REFRESH, "empty");
        }
        else {
            msg = out.obtainMessage(REFRESH,list.substring(1));
        }
        out.sendMessage(msg);
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
