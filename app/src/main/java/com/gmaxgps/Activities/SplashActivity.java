package com.gmaxgps.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.gmaxgps.R;
import com.gmaxgps.Utils.CommonFunction;
import com.gmaxgps.Utils.Consts;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Timer;
import java.util.TimerTask;

import static com.gmaxgps.Utils.AppSession.getValue;


public class SplashActivity extends AppCompatActivity {

    Context ct = this;
Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // CommonFunction.changeStatusBarColor(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {


                    if (CommonFunction.isNetworkAvailable(ct)) {
                        String intro_screen = getValue(ct, Consts.INTRO_SCREEN);


                        if (intro_screen == null || intro_screen.equalsIgnoreCase("")) {
                            Intent in = new Intent(SplashActivity.this, AppIntroActivity.class);
                            startActivity(in);
                            finish();

                        } else {
                            startActivity(new Intent(ct, LoginActivity.class));
                            finish();
                        }

                    }
                    else
                    {

                        Toast.makeText(ct, Consts.NETWORK_FAILER, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        },1500);



    }

}
