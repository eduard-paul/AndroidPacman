package com.example.edward.pacman;

import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Edward on 22.05.2015.
 */
public class RemoteUser extends User implements Runnable {
    /** Request socket */
    Socket s;
    /** Data socket */
    Socket ds;
    private InputStream sin;
    private OutputStream sout;
    ObjectOutputStream dObjOut;
    ObjectInputStream oin;
    DataOutputStream dOut;
    DataInputStream din;
    String myRoomName = "";
    Room myRoom = null;
    int playerId = -1;
    Server server;

    RemoteUser(Socket socketParam, Server server) throws IOException {
        this.server = server;
        s = socketParam;
        ds = new Socket(s.getInetAddress(), s.getPort() + 1);
        this.sin = s.getInputStream();
        this.sout = s.getOutputStream();
        dObjOut = new ObjectOutputStream(ds.getOutputStream());
        dOut = new DataOutputStream(ds.getOutputStream());
        din = new DataInputStream(ds.getInputStream());
        oin = new ObjectInputStream(s.getInputStream());
        Thread secondReader = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!ds.isClosed()) {
                    String line = null;

                    try {
                        line = din.readUTF();
                        System.out
                                .println("The dumb client just sent me this line : "
                                        + line);
                    } catch (IOException e) {
                        close();
                    }

                    if (line == null) {
                        close();
                    } else {
                        myRoom.Command(line, playerId);
                    }
                }
            }
        });
        secondReader.start();
    }

    public void run() {

        while (!s.isClosed()) {

            String line = null;

            DataInputStream in = new DataInputStream(sin);

            try {
                line = in.readUTF();
                System.out
                        .println("The dumb client just sent me this line : "
                                + line);
            } catch (IOException e) {
                close();
            }

            if (line == null) {
                close();
            } else if (line.contains("EnterRoom:")) {
                EnterRoom(line);
            } else if (line.contains("LeaveRoom")) {
                LeaveRoom();
            } else if (line.contains("SpectateRoom:")) {
                SpectateRoom(line);
            }
        }
    }

    @Override
    public Handler getHandler() {
        return null;
    }

    public void SendBoard(int[][] board) {
        try {
            dObjOut.writeObject(board);
            dObjOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public synchronized void EnterRoom(String line) {
        DataOutputStream out = new DataOutputStream(sout);
        String name = line.substring(10);
        boolean failed = true;
        for (Room room : server.rooms) {
            if (room.name.equals(name)) {
                if (room.currPlayers < room.maxPlayers && !room.IsStarted()) {
                    room.AddPlayer(this);
                    myRoomName = name;
                    myRoom = room;
                    failed = false;
                }
            }
        }
        if (!failed) {
            try {
                out.writeUTF("success");
                out.flush();
            } catch (IOException e) {
            }
        } else {
            try {
                out.writeUTF("fail");
                out.flush();
            } catch (IOException e) {
            }
        }
    }

    public synchronized void SpectateRoom(String line) {
        DataOutputStream out = new DataOutputStream(sout);
        String name = line.substring(13);
        boolean failed = true;
        for (Room room : server.rooms) {
            if (room.name.equals(name)) {
                room.AddSpectator(this);
                myRoomName = "spectate";
                myRoom = room;
                failed = false;
            }
        }
        if (!failed) {
            try {
                out.writeUTF("success");
                out.flush();
            } catch (IOException e) {
            }
        } else {
            try {
                out.writeUTF("fail");
                out.flush();
            } catch (IOException e) {
            }
        }
    }

    public void SendRoomList() {
        DataOutputStream out = new DataOutputStream(sout);
        try {
            String list = new String();
            for (Room room : server.rooms) {
                list += ":" + room.name + " " + "[" + room.currPlayers
                        + "/" + room.maxPlayers + "]";
            }
            if (list.isEmpty())
                out.writeUTF("empty");
            else
                out.writeUTF(list.substring(1));
            out.flush();
        } catch (IOException e) {
            close();
        }
    }

    public synchronized void close() {
        if (!myRoomName.isEmpty()) { // If in some room leave it
            if ("spectate".equals(myRoomName))
                myRoom.RemoveSpectator(this);
            else
                myRoom.RemovePlayer(this);
        }
        server.allUsers.remove(this); // Remove itself from global user list
        if (!s.isClosed()) { // Try to close request socket
            try {
                s.close();
                Log.d(TAG, s.toString() + " disconnected");
            } catch (IOException ignored) {
            }
        }
        if (!ds.isClosed()) { // Try to close data socket
            try {
                ds.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void SendState(GameState gs) {
        try {
            dObjOut.writeObject(gs);
            dObjOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendStart() {
        try {
            dOut.writeUTF("StartGame");
            dOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

