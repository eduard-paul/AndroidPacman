package com.example.edward.pacman;

/**
 * Created by Edward on 22.05.2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "EdwardWiFi";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private Collection<WifiP2pDevice> d;

    ServerSocket serverSocket = null;
    Socket socket = null;
    List<Socket> clients;

    public Collection<WifiP2pDevice> getDevices(){
        return d;
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

        myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                Log.d(TAG,"WifiP2pManager.PeerListListener.onPeersAvailable");
                d = wifiP2pDeviceList.getDeviceList();
                for (Iterator<WifiP2pDevice> iterator = d.iterator(); iterator.hasNext(); ) {
                    WifiP2pDevice device = iterator.next();
                    Log.d(TAG,device.deviceName);
                }
            }
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                // InetAddress from WifiP2pInfo struct.
                final String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
                Log.d(TAG,groupOwnerAddress);
                // After the group negotiation, we can determine the group owner.
                if (info.groupFormed && info.isGroupOwner) {
                    // Do whatever tasks are specific to the group owner.
                    // One common case is creating a server thread and accepting
                    // incoming connections.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                serverSocket = new ServerSocket(4566);
//                                socket = serverSocket.accept();
//                                Log.d(TAG, "Server socket connected");
//                                InputStream in = socket.getInputStream();
//                                int recv = in.read();
//                                in.close();
//                                Log.d(TAG, Integer.toString(recv));


                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
//                                if (serverSocket!=null){
//                                    if (!serverSocket.isClosed()){
//                                        try {
//                                            serverSocket.close();
//                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
//                                        }
//                                    }
//                                }
//                                if (socket!=null){
//                                    if (!socket.isClosed()){
//                                        try {
//                                            socket.close();
//                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
//                                        }
//                                    }
//                                }
                            }
                        }
                        private Socket getNewConn() {
                            Socket s = null;
                            try {
                                s = serverSocket.accept();
                            } catch (IOException e) {
                                shutdownServer();
                            }
                            return s;
                        }
                        private void shutdownServer() {
                            for (Socket s : clients) {
                                try {
                                    s.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //server.textArea.append("1\n");
                            }
                            if (!serverSocket.isClosed()) {
                                try {
                                    serverSocket.close();
                                    //server.textArea.append("2\n");
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    }).start();


                } else if (info.groupFormed) {
                    // The other device acts as the client. In this case,
                    // you'll want to create a client thread that connects to the group
                    // owner.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = new Socket(groupOwnerAddress, 4566);
//                                socket.connect((new InetSocketAddress(groupOwnerAddress, 4566)), 500);
                                Log.d(TAG, "Client socket connected");
                                OutputStream out = socket.getOutputStream();
                                out.write(15);
                                out.flush();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
//                                if (socket != null) {
//                                    if (socket.isConnected()) {
//                                        try {
//                                            socket.close();
//                                        } catch (IOException e) {
//                                            //catch logic
//                                        }
//                                    }
//                                }
                            }
                        }
                    }).start();
                }
            }
        };

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Log.d(TAG,"Wifi P2P is enabled");
            } else {
                // Wi-Fi P2P is not enabled
                Log.d(TAG,"Wi-Fi P2P is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION");

            if (mManager == null) {
                return;
            }

            mManager.requestPeers(mChannel, myPeerListListener);

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION");

            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                Log.d(TAG,"Connected");
                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else {
                Log.d(TAG,"Disconnected");
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
