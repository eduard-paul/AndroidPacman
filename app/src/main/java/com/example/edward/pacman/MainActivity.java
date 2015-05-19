package com.example.edward.pacman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;


public class MainActivity extends ActionBarActivity {


    String TAG = "PacMain";
    static Handler in, out;
    private View mDecorView;
    static GameState gs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        setContentView(R.layout.activity_main);
        in = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,Integer.toString(msg.what));
                switch (msg.what){
                    case User.GAME_STATE:
                        gs = (GameState)msg.obj;
                        break;
                    case User.START_GAME:
                        Intent intent = new Intent(MainActivity.this,PlayingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };
        User user = new User(in);
        out = user.getHandler();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        out.sendMessage(out.obtainMessage(User.NEW_ROOM, 1, 0));
    }


//////////////////////////////////


    public static class PlayingActivity extends ActionBarActivity {
        private View mDecorView;
        private DrawView drawView;
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
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            int cS1 = displayMetrics.heightPixels/31;
            int cS2 = displayMetrics.widthPixels/28;
            cellSize = Math.min(cS1,cS2);
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            out.sendEmptyMessage(User.LEAVE_ROOM);
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

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            drawView.drawPoint((int)event.getX(),(int)event.getY());
            return super.onTouchEvent(event);
        }

        class DrawView extends View {
            Paint p;
            int x = -1 ,y = -1;

            public DrawView(Context context) {
                super(context);
                p = new Paint();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(Color.BLACK);
                p.setColor(Color.RED);
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
                    int x = ch.cell.x * cellSize + cellSize/2;
                    int y = ch.cell.y * cellSize + cellSize/2;
                    if (Math.abs(ch.direction) > 1)
                        x += (ch.dist * cellSize / 2);
                    else
                        y += (ch.dist * cellSize / 2);
                    canvas.drawCircle(y, x, cellSize / 2, p);
                }

                invalidate();
            }

            public void drawPoint(int _x, int _y){
                x=_x; y=_y;
                invalidate();
            }
        }

    }
}
