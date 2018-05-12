/*
 * Copyright 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmaxgps.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gmaxgps.Fragments.LoginFragment;
import com.gmaxgps.MainApplication;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.WebService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    String firebase_id = "";
    Context ct = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.gmaxgps",         PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.v("haskey:", sign);


               // Toast.makeText(getApplicationContext(),sign,         Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }



        if (savedInstanceState == null)
        {
            firebase_id = AppSession.getValue(this, Consts.FIRBASE_USER_ID);
            if(firebase_id!= null && !firebase_id.equalsIgnoreCase(""))
            {
                login();
            }
            else
            {
                Intent i = new Intent(this, LoginOptions.class);
                startActivity(i);
                finish();
            }


            /*getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new LoginFragment())
                    .commit();*/
        }
    }

    private void login()
    {

        String email = AppSession.getValue(ct, Consts.EMAIL);


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ct);

        preferences
                .edit()
                .putBoolean(MainApplication.PREFERENCE_AUTHENTICATED, true)
                .putString(MainApplication.PREFERENCE_EMAIL, email)
                .putString(MainApplication.PREFERENCE_PASSWORD, Consts.PASSWORD)
                .apply();

        final ProgressDialog progress = new ProgressDialog(ct);
        progress.setMessage(getString(R.string.app_loading));
        progress.setCancelable(false);
        progress.show();
        final MainApplication application = (MainApplication) getApplication();
        application.getServiceAsync(new MainApplication.GetServiceCallback()
        {
            @Override
            public void onServiceReady(OkHttpClient client, Retrofit retrofit, WebService service) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }

                startActivity(new Intent(ct, MainActivity.class));
                finish();
            }

            @Override
            public boolean onFailure()
            {
                if (progress.isShowing())
                {
                    progress.dismiss();

                    Intent i = new Intent(ct, LoginOptions.class);
                    startActivity(i);
                    finish();
                }

                return false;


            }
        });
    }

}
