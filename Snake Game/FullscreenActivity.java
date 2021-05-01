package com.example.lancelot.fullscreensnakegame;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.MainThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import jp.wasabeef.blurry.Blurry;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnTouchListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private SnakeView mContentView;

    private GameEngine gameEngine;
    public static TextView score;
    public static TextView speed;
    private final Handler handler = new Handler();
    public static long updateDelay = 275;

    private float prevX, prevY;

    private boolean snakeStopped = false;
    private boolean startup = false;
    private boolean gameOver = false;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
        // Delayed display of UI elements
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        gameEngine = new GameEngine();
        gameEngine.initGame();

        mContentView = findViewById(R.id.snakeView);
        mContentView.setOnTouchListener(this);

        score = findViewById(R.id.textViewScore);
        speed = findViewById(R.id.textViewSpeedView);
        speed.setText("Speed: " + updateDelay);

        startUpdateHandler();
    }

    private void startUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            gameEngine.Update();

            if (gameEngine.getCurrentGameState() == GameState.Running) {
                handler.postDelayed(this, updateDelay);
            }
            if (gameEngine.getCurrentGameState() == GameState.Lost) {
                OnGameLost();
            }

            mContentView.setSnakeViewMap(gameEngine.getMap());
            mContentView.invalidate();
            }
        }, updateDelay);
    }

    private void OnGameLost() {
        Toast.makeText(this, "You have lost.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getX();
                float newY = event.getY();

                //Calculate where we swiped
                if (Math.abs(newX - prevX) > Math.abs(newY - prevY)) {
                    // LEFT - RIGHT directions
                    if (newX > prevX) {
                        // RIGHT
                        gameEngine.UpdateDirection(Direction.East);
                    } else {
                        // LEFT
                        gameEngine.UpdateDirection(Direction.West);
                    }
                } else {
                    //UP - DOWN directions
                    if (newY > prevY) {
                        // DOWN
                        gameEngine.UpdateDirection(Direction.South);
                    } else {
                        // UP
                        gameEngine.UpdateDirection(Direction.North);
                    }
                }
                break;
        }
        return true;
    }

    public void onBackPressed() {
//        if(gameEngine.getCurrentGameState() == GameState.Lost) {
//            finishGame();
//        } else if(gameEngine.getCurrentGameState() == GameState.Running) {
//            pauseGame();
//        } else {
            finishGame();
//        }
    }

    public void restart(View view) {
        recreate();
    }

    public void finishGame() {
        gameOver = true;
        snakeStopped = true;
        this.finish();
    }

    private void pauseGame() {
        snakeStopped = true;
        ViewGroup viewgroup = (ViewGroup) findViewById(android.R.id.content);
        Blurry.with(this).radius(8).sampling(3).onto(viewgroup);

        TextView pausedTextView = new TextView(this);
        pausedTextView.setText("Quit Game?");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Fipps-Regular.otf");
        pausedTextView.setTypeface(font);
        pausedTextView.setTextSize(22);
        pausedTextView.setTextColor(Color.BLACK);
        pausedTextView.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setCancelable(false);
        builder.setView(pausedTextView);
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gameOver = true;
                        snakeStopped = true;
                        FullscreenActivity.this.finish();
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Blurry.delete((ViewGroup) findViewById(android.R.id.content));
                        startUpdateHandler();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ViewGroup parent = (ViewGroup) pausedTextView.getParent();
        parent.setPadding(100, 0, 100, 100);
    }

}
