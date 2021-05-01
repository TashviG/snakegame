package com.example.lancelot.fullscreensnakegame;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    private static final long defaultSpeed = 275;
    private static final long slowSpeed = 450;
    private static final long fastSpeed = 100;

    private static RadioButton normal;
    private static RadioButton slow;
    private static RadioButton fast;
    private static RadioButton aqua;
    private static RadioButton defaultColor;
    private static RadioButton walls;
    private static RadioButton noWalls;

    private static long speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPref = this.getSharedPreferences("Settings", MODE_PRIVATE);

        boolean defaultSpeedChecked = sharedPref.getBoolean("defaultSpeed", true);
        boolean slowSpeedChecked = sharedPref.getBoolean("slowSpeed", false);
        boolean fastSpeedChecked = sharedPref.getBoolean("fastSpeed", false);
        if (defaultSpeedChecked) {
            normal = findViewById(R.id.radioButtonDefault);
            normal.setChecked(true);
        } else if(slowSpeedChecked) {
            slow = findViewById(R.id.radioButtonSlow);
            slow.setChecked(true);
        } else if (fastSpeedChecked){
            fast = findViewById(R.id.radioButtonFast);
            fast.setChecked(true);
        }

        boolean defaultColorChecked = sharedPref.getBoolean("defaultColor", true);
        if (defaultColorChecked) {
            defaultColor = findViewById(R.id.radioButtonNormalColor);
            defaultColor.setChecked(true);
        } else {
            aqua = findViewById(R.id.radioButtonAqua);
            aqua.setChecked(true);
        }

        boolean noWallsChecked = sharedPref.getBoolean("noWalls", true);
        if (noWallsChecked) {
            noWalls = findViewById(R.id.radioButtonNoWalls);
            noWalls.setChecked(true);
        } else {
            walls = findViewById(R.id.radioButtonWalls);
            walls.setChecked(true);
        }
    }

    public void onRadioButtonClicked(View view) {
        normal = findViewById(R.id.radioButtonDefault);
        slow = findViewById(R.id.radioButtonSlow);
        fast = findViewById(R.id.radioButtonFast);
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonDefault:
                if (checked)
                    speed = defaultSpeed;
                    updateSpeed(speed);
                    break;
            case R.id.radioButtonSlow:
                if (checked)
                    speed = slowSpeed;
                    updateSpeed(speed);
                    break;
            case R.id.radioButtonFast:
                if (checked)
                    speed = fastSpeed;
                    updateSpeed(speed);
                    break;
        }
        SharedPreferences sharedPref = this.getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean("defaultSpeed", normal.isChecked());
        prefEditor.putBoolean("slowSpeed", slow.isChecked());
        prefEditor.putBoolean("fastSpeed", fast.isChecked());
        prefEditor.apply();
    }

    public void updateSpeed(Long updatedSpeed) {
        FullscreenActivity.updateDelay = updatedSpeed;
    }

    public void changeColor(View view) {
        defaultColor = findViewById(R.id.radioButtonNormalColor);
        aqua = findViewById(R.id.radioButtonAqua);
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonNormalColor:
                if (checked) {
//                    int wallColor = Color.GREEN;
                    int snakeHeadColor = Color.RED;
                    int snakeTailColor = Color.GREEN;
                    int appleColor = Color.RED;
                    changeTheme(snakeHeadColor, snakeTailColor, appleColor);
                }
                break;
            case R.id.radioButtonAqua:
                if (checked) {
//                    int wallColor = Color.LTGRAY;
                    int snakeHeadColor = Color.rgb(0,255,255);
                    int snakeTailColor = Color.rgb(255,229,180);
                    int appleColor = Color.GREEN;
                    changeTheme(snakeHeadColor, snakeTailColor, appleColor);
                }
                break;
        }
        SharedPreferences sharedPref = this.getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean("defaultColor", defaultColor.isChecked());
        prefEditor.putBoolean("aquaColor", aqua.isChecked());
        prefEditor.apply();
    }

    private void changeTheme(int snakeHeadColor, int snakeTailColor, int appleColor) {
//        SnakeView.wallColor = wallColor;
        SnakeView.snakeHeadColor = snakeHeadColor;
        SnakeView.snakeTailColor = snakeTailColor;
        SnakeView.appleColor = appleColor;
    }

    public void changeWall(View view) {
        walls = findViewById(R.id.radioButtonWalls);
        noWalls = findViewById(R.id.radioButtonNoWalls);

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioGroupWall:
                if (checked) {
//                    boolean wallsExist = true;
                    int wallColor = Color.GREEN;
                    changeWallColor(true, wallColor);
                }
                break;
            case R.id.radioButtonNoWalls:
                if (checked) {
//                    boolean wallsExist = false;
                    int wallColor = Color.WHITE;
                    changeWallColor(false, wallColor);
                }
                break;
        }
        SharedPreferences sharedPref = this.getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean("noWalls", noWalls.isChecked());
        prefEditor.putBoolean("walls", walls.isChecked());
        prefEditor.apply();
    }

    private void changeWallColor (boolean walls, int wallColor) {
        GameEngine.wallsExist = walls;
        SnakeView.wallColor = wallColor;
    }
}

