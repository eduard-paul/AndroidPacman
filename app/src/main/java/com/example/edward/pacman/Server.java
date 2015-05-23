package com.example.edward.pacman;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends Thread {
    String TAG = "PacServer";

    private ServerSocket ss;
    private int port;
    BlockingQueue<User> allUsers = new LinkedBlockingQueue<>();
    BlockingQueue<Room> rooms = new LinkedBlockingQueue<>();
    Thread serverThread;

    public Server(int port) {
        this.port = port;
    }

    public void addLocalUser(LocalUser localUser){
        allUsers.offer(localUser);
        localUser.setServer(this);
    }

    private Socket getNewConn() {
        Socket s = null;
        try {
            s = ss.accept();
        } catch (IOException e) {
            shutdownServer();
        }
        return s;
    }

    private synchronized void shutdownServer() {
        for (User s : allUsers) {
            s.close();
        }
        if (!ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException ignored) {
            }
        }
    }
    @Override
    public void run()  {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverThread = Thread.currentThread();
        while (true) {
            Socket s = getNewConn();
            Log.d(TAG,s.toString() + " connected");
            if (serverThread.isInterrupted()) {
                break;
            } else //noinspection ConstantConditions
                if (s != null) {
                try {
                    final User processor = new RemoteUser(s, this);
                    final Thread thread = new Thread((RemoteUser)processor);
                    thread.setDaemon(true);
                    thread.start();
                    allUsers.offer(processor);
                    Log.d(TAG,s.toString() + " offered");
                } catch (IOException ignored) {
                }
            }
        }
    }
}
