package com.example.edward.pacman;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {


    String TAG = "PacMain";
    static Handler hIn, hOut;
    private View mDecorView;
    static public GameState gs;
    static int winOrLose = 0;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    static BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    public static boolean isServer;
    public static boolean isLocalGame;
    static LocalUser user;
    static String roomListString = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        setContentView(R.layout.activity_main);
        handlerIOInit();
        wifiInit();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.button) {

            isLocalGame = true;
            hOut.sendMessage(hOut.obtainMessage(User.NEW_ROOM, 1, 0));

        } else if(id==R.id.button2) {

            Intent intent = new Intent(MainActivity.this,MainActivity.RoomList.class);
            startActivity(intent);

            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "discoverPeers onSuccess");
                }
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "discoverPeers onFailure");
                }
            });
        }
    }

    private void handlerIOInit() {
        hIn = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, Integer.toString(msg.what));
                switch (msg.what) {
                    case User.GAME_STATE:
                        gs = (GameState) msg.obj;
                        if (gs.cs.size() != 0) {
                            if (gs.cs.get(0).winnerId == 0) {
                                winOrLose = 0;
                            } else if (gs.cs.get(0).winnerId == user.playerId) {
                                winOrLose = 1;
                            } else {
                                winOrLose = -1;
                            }
                        }
                        break;
                    case User.START_GAME:
                        Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                        startActivity(intent);
                        break;
                    case User.REFRESH:
                        roomListString = (String) msg.obj;
                        break;
                }
            }
        };
        user = new LocalUser(hIn);
        hOut = user.getHandler();
    }

    private void wifiInit() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
//////////////////////////////////


    public static class PlayingActivity extends ActionBarActivity {
        private View mDecorView;
        private DrawView drawView;
        private DisplayMetrics displayMetrics;
        private static final int PINK = Color.rgb(255, 182, 193);
        private static final int LIGHT_BLUE = Color.rgb(132,112,255);
        private static final int ORANGE = Color.rgb(255,102,0);
        int cellSize = 20;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mDecorView = getWindow().getDecorView();
            drawView = new DrawView(this);
            setContentView(drawView);

            Display display = getWindowManager().getDefaultDisplay();
            displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            int cS1 = displayMetrics.heightPixels/31;
            int cS2 = displayMetrics.widthPixels/28;
            cellSize = Math.min(cS1,cS2);

            drawView.setOnTouchListener(new OnSwipeTouchListener(this){
                @Override
                public void onSwipeRight() {
                    if (!isServer && !isLocalGame) try {
                        ((WiFiDirectBroadcastReceiver)mReceiver).socketReader.dout.writeUTF("Right");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } else {
                        hOut.sendMessage(hOut.obtainMessage(User.TURN, User.RIGHT, 0));
                    }
                }

                @Override
                public void onSwipeLeft() {
                    if (!isServer && !isLocalGame) try {
                        ((WiFiDirectBroadcastReceiver)mReceiver).socketReader.dout.writeUTF("Left");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } else {
                        hOut.sendMessage(hOut.obtainMessage(User.TURN, User.LEFT, 0));
                    }
                }

                @Override
                public void onSwipeTop() {
                    if (!isServer && !isLocalGame) try {
                        ((WiFiDirectBroadcastReceiver)mReceiver).socketReader.dout.writeUTF("Up");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } else {
                        hOut.sendMessage(hOut.obtainMessage(User.TURN, User.UP, 0));
                    }
                }

                @Override
                public void onSwipeBottom() {
                    if (!isServer && !isLocalGame) try {
                        ((WiFiDirectBroadcastReceiver)mReceiver).socketReader.dout.writeUTF("Down");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } else {
                        hOut.sendMessage(hOut.obtainMessage(User.TURN, User.DOWN, 0));
                    }
                }
            });
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            if (!isServer && !isLocalGame) {
                send("LeaveRoom");
            } else {
                hOut.sendEmptyMessage(User.LEAVE_ROOM);
            }
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            if (hasFocus) {
                mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }

        class DrawView extends View {
            Paint p;
            int x = -1 ,y = -1;
            int timer = 0;

            public DrawView(Context context) {
                super(context);
                p = new Paint();
                p.setTextSize(200);
                p.setAntiAlias(true);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(Color.BLACK);
                p.setColor(Color.RED);
                if (gs==null) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    invalidate();
                    return;
                }
                for (int row = 0; row < gs.board.length; row++) {
                    for (int col = 0; col < gs.board[0].length; col++) {
                        switch (gs.board[row][col]) {
                            case -1:
                                p.setColor(Color.BLUE);
                                canvas.drawRect(col * cellSize, row * cellSize,
                                        (col + 1) * cellSize, (row + 1) * cellSize, p);
                                break;
                            case 0:
                                p.setColor(Color.BLACK);
                                canvas.drawRect(col * cellSize, row * cellSize,
                                        (col + 1) * cellSize, (row + 1) * cellSize, p);
                                break;
                            case 5:
                                p.setColor(ORANGE);
                                canvas.drawCircle(col * cellSize + cellSize/2,
                                        row * cellSize + cellSize/2, cellSize / 8, p);
                                break;
                            default:
                                p.setColor(Color.BLACK);
                                break;
                        }
                    }
                }

                for (Iterator iterator = gs.cs.iterator(); iterator.hasNext(); ) {
                    CharacterState ch =  (CharacterState)iterator.next();
                    p.setColor(Color.DKGRAY);
                    int x = ch.x * cellSize + cellSize/2;
                    int y = ch.y * cellSize + cellSize/2;
                    if (Math.abs(ch.direction) > 1)
                        x += (ch.dist * cellSize / 2);
                    else
                        y += (ch.dist * cellSize / 2);
                    switch (ch.id) {
                        case 1:
                            p.setColor(Color.YELLOW);
                            break;
                        case 2:
                            p.setColor(Color.GREEN);
                            break;
                        case 3:
                            p.setColor(Color.BLUE);
                            break;
                        case 4:
                            p.setColor(Color.YELLOW);
                            break;
                        case -1:
                            p.setColor(Color.RED);
                            break;
                        case -2:
                            p.setColor(PINK);
                            break;
                        case -3:
                            p.setColor(LIGHT_BLUE);
                            break;
                        case -4:
                            p.setColor(ORANGE);
                            break;
                        default:
                            break;
                    }
                    final RectF oval = new RectF();
                    oval.set(y - cellSize / 2, x - cellSize / 2, y + cellSize / 2, x + cellSize / 2);
                    if (ch.id>0) {
                        timer = (timer+1)%10;
                        float tmp = (float)timer/5;
                        if (timer>5) tmp = (float)(10-timer)/5;
                        float direction=0;
                        switch (ch.direction){
                            case 1:
                                direction = 0;
                                break;
                            case -1:
                                direction = 180;
                                break;
                            case 2:
                                direction = 90;
                                break;
                            case -2:
                                direction = 270;
                                break;
                        }
                        canvas.drawArc(oval,55*tmp+direction,360-110*tmp,true,p);
                    } else {
                        canvas.drawArc(oval, 180, 180, true, p);
                        canvas.drawRect(y-cellSize/2,x,y+cellSize/2,x+cellSize/2,p);
                    }
                }

                p.setColor(Color.WHITE);


                int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
                if (winOrLose == 1) {
                    int xPos = (int)(canvas.getWidth()
                            - p.getTextSize() * Math.abs("You win!".length() / 2)) / 2;
                    while (xPos<0){
                        p.setTextSize(p.getTextSize()-20);
                        xPos = (int)(canvas.getWidth()
                                - p.getTextSize() * Math.abs("You win!".length() / 2)) / 2;
                    }
                    canvas.drawText("You win!",xPos, yPos, p);
                } else if (winOrLose == -1) {
                    int xPos = (int)(canvas.getWidth()
                            - p.getTextSize() * Math.abs("You lose!".length() / 2)) / 2;
                    while (xPos<0){
                        p.setTextSize(p.getTextSize()-20);
                        xPos = (int)(canvas.getWidth()
                                - p.getTextSize() * Math.abs("You win!".length() / 2)) / 2;
                    }
                    canvas.drawText("You lose!",xPos, yPos,p);
                }
                invalidate();
            }

            public void drawPoint(int _x, int _y){
                x=_x; y=_y;
                invalidate();
            }
        }

    }

    static class Receiver extends AsyncTask<Void,Void,String>{
        WiFiDirectBroadcastReceiver.SocketReader socketReader;
        @Override
        protected String doInBackground(Void... voids) {
            socketReader = ((WiFiDirectBroadcastReceiver)mReceiver).socketReader;
            String line = socketReader.recv();
            return line;
        }
    }

    static class Sender extends AsyncTask<String,Void,Void> {
        WiFiDirectBroadcastReceiver.SocketReader socketReader;
        @Override
        protected Void doInBackground(String... strings) {
            socketReader = ((WiFiDirectBroadcastReceiver)mReceiver).socketReader;
            try {
                int cnt = 0;
                for (String string : strings) {
                    socketReader.out.writeUTF(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static void send(String s){
        Sender sender = new Sender();
        sender.execute(s);
    }

    static String recv(){
        Receiver receiver = new Receiver();
        receiver.execute();
        String line = null;
        try {
            line = receiver.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static class RoomList extends ActionBarActivity {
        String TAG = "PacRoom";
        String[] roomList = null;
        String myRoom = "";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_room_list);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(TAG, "itemClick: position = " + position + ", id = "
                            + id);
                    String checkedName = ((TextView) view).getText().toString();
                    if (checkedName.substring(0, 2).equals(">>")) {
                        myRoom = "";
                        if (!isServer && !isLocalGame) {
                            send("LeaveRoom");
                        }else {
                            hOut.sendEmptyMessage(User.LEAVE_ROOM);
                        }
                        RefreshRoomList();
                    } else if (myRoom.equals("")) {
                        String name = checkedName.substring(0, checkedName.length() - 6);
                        if (!isServer && !isLocalGame) {
                            send("EnterRoom:" + name);
                            String answer = recv();
                            if (answer.equals("success")) {
                                GetRoom(name);
                            }
                        } else {
                            Message msg = hOut.obtainMessage(User.ENTER_ROOM,name);
                            hOut.sendMessage(msg);

                        }
                    }
                }
            });
            RefreshRoomList();
        }

        public void RefreshRoomList() {
            String line = "empty";
            if (!isServer && !isLocalGame) {
                send("RefreshRoomList");
                line = recv();
            } else {
                hOut.sendEmptyMessage(User.REFRESH);
                line = roomListString;
            }
            Log.d(TAG, line);
            if (isServer) myRoom = user.myRoomName;
                if (!line.equals("empty")) {
                    roomList = line.split(":");
//                    for (String string : roomList) {
                    for(int i=0;i<roomList.length;i++){
                        if (roomList[i].substring(0, roomList[i].length() - 6).equals(myRoom)) {
                            roomList[i] = ">>" + roomList[i] + "<<";
                        }
                    }
                    ListView listView = (ListView) findViewById(R.id.listView);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, roomList);
                    listView.setAdapter(adapter);
                } else {
                    roomList = null;
                    ListView listView = (ListView) findViewById(R.id.listView);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, new LinkedList<String>());
                    listView.setAdapter(adapter);
                }
        }

        private void GetRoom(String name) {
            myRoom = name;
            RefreshRoomList();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_room_list, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            final int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.item1) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.dialog_title);

                final View view = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.dialog, null);
                builder.setView(view);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText roomNameText = (EditText) view.findViewById(R.id.editText);
                        EditText numPlayersText = (EditText) view.findViewById(R.id.editText2);
                        String roomName = roomNameText.getText().toString();
                        String numPlayers = numPlayersText.getText().toString();
                        if (roomName != null){
                            if (!isServer && !isLocalGame) {
                                String answer = null;
                                send("CreateRoom:" + numPlayers+":"+roomName);
                                answer = recv();
                                RefreshRoomList();
                                if (answer.equals("success")) {
                                    GetRoom(roomName);
                                }
                            } else {
                                Message msg = hOut.obtainMessage(User.NEW_ROOM,
                                        Integer.valueOf(numPlayers),0,
                                        roomName);
                                hOut.sendMessage(msg);
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            } else if (id == R.id.item2){
                RefreshRoomList();
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onBackPressed() {
            if (myRoom != "") {
                myRoom = "";
                if (!isServer && !isLocalGame) {
                    send("LeaveRoom");
                } else {
                    hOut.sendEmptyMessage(User.LEAVE_ROOM);
                }
            }
            super.onBackPressed();
        }
    }

}
