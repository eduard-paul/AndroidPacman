package com.example.edward.pacman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "EdwardWiFi";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private Collection<WifiP2pDevice> d;

    int serverPort = 4566;
    Server server = null;
    SocketReader socketReader = null;
    Thread thread, t2;

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
            for (WifiP2pDevice device : d) {
                Log.d(TAG, device.deviceName);
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
                if (server != null) return;
                MainActivity.isServer =true;
                server = new Server(serverPort);
                server.addLocalUser(MainActivity.user);
                server.start();
            } else if (info.groupFormed) {
                if(socketReader != null) return;
                MainActivity.isServer = false;
                socketReader = new SocketReader(groupOwnerAddress,serverPort);
                thread = new Thread(socketReader);
                thread.start();
            }
            }
        };
    }


    class SocketReader implements Runnable {

        String groupOwnerAddress;
        int serverPort;
        Socket socket, dataSocket;
        ServerSocket ss;
        DataInputStream in;
        DataOutputStream out, dout;
        ObjectInputStream doin;
        ObjectOutputStream oout;
        DataInputStream din;

        public SocketReader(String groupOwnerAddress, int port){
            this.groupOwnerAddress = groupOwnerAddress;
            this.serverPort = port;
        }

        String recv() {
            String result = "";
            try {
                result = in.readUTF();
            } catch (IOException ignored) {
            }
            return result;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private void RefreshRoomList() {
            try {
                out.writeUTF("RefreshRoomList");
                Log.d(TAG, "RefreshRoomList");
                String line = recv();
                Log.d(TAG, "RefreshRoomList"+line);
            } catch (IOException ignored) {
            }
        }

        public void run() {
            try {
                socket = new Socket(groupOwnerAddress, serverPort);
                Log.d(TAG, "Client socket connected");

                ss = new ServerSocket(socket.getLocalPort() + 1);
                dataSocket = ss.accept();
                Log.d(TAG, "Data socket connected");

                InputStream sin = socket.getInputStream();
                OutputStream sout = socket.getOutputStream();

                in = new DataInputStream(sin);
                out = new DataOutputStream(sout);

                doin = new ObjectInputStream(dataSocket
                        .getInputStream());
                din = new DataInputStream(dataSocket
                        .getInputStream());
                dout = new DataOutputStream(dataSocket
                        .getOutputStream());
                oout = new ObjectOutputStream(socket
                        .getOutputStream());

                RefreshRoomList();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!dataSocket.isClosed()) {
                try {
                    String line;
                    dataSocket.setSoTimeout(1000);
                    do
                        line = din.readUTF();
                    while (!line.equals("StartGame"));

                    int playerId = din.readInt();

                    t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mActivity, MainActivity.PlayingActivity.class);
                            mActivity.startActivity(intent);
                        }
                    });
                    t2.start();


                    //noinspection InfiniteLoopStatement
                    while (true) {
                        MainActivity.gs = (GameState) doin.readObject();
                        if (MainActivity.gs.cs.size() != 0 && playerId != -1) {
                            if (MainActivity.gs.cs.get(0).winnerId == 0) {
                                MainActivity.winOrLose = 0;
                            } else if (MainActivity.gs.cs.get(0).winnerId == playerId) {
                                MainActivity.winOrLose = 1;
                            } else {
                                MainActivity.winOrLose = -1;
                            }
                        }
                    }

                } catch (SocketTimeoutException ignored) {
                } catch (Exception e){
                    Disconnect();
                }

            }
        }
        private void Disconnect() {
            try {
                if (!socket.isClosed())
                    socket.close();
                if (!dataSocket.isClosed())
                    dataSocket.close();
                if (!ss.isClosed())
                    ss.close();
            } catch (IOException ignored) {
            }
        }
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
            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                Log.d(TAG,"Connected");
                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else {
                Log.d(TAG, "Disconnected");
                socketReader = null;
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.d(TAG,"WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
