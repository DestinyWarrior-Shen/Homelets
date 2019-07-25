package com.example.homelessservices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LandingActivity extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent intent = new Intent(LandingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
    /**
     * Get the preferred theme from preference file
     * @return the selected theme
     */
    private int getSelectedTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE);;
        int selectedTheme = sharedPreferences.getInt("theme", 0);
        return selectedTheme;
    }
}
