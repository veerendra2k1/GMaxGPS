/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.internal.ImageRequest;
import com.gmaxgps.Fragments.MainFragment;
import com.gmaxgps.MainApplication;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.Utils.ImageDownloaderTask;
import com.google.android.gms.common.api.Response;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    TextView menu = null;
    TextView logout = null;

    ImageView back_button = null;
    TextView user_email = null;
    CircleImageView imageView = null;
    public static final int REQUEST_DEVICE = 1;
    Context ct = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);

        init(headerview);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_layout, new MainFragment())
                    .commit();
        }
    }

    private void init(View headerview)
    {
        menu = (TextView) headerview.findViewById(R.id.menu);

        user_email = (TextView) headerview.findViewById(R.id.user_email);
        logout = (TextView) headerview.findViewById(R.id.logout);

        back_button = (ImageView) headerview.findViewById(R.id.back_button);
        imageView = (CircleImageView) headerview.findViewById(R.id.imageView);


        String email =  AppSession.getValue(ct, Consts.EMAIL  );



        String fullname = AppSession.getValue(ct, Consts.USER_NAME);

        if(fullname == null || fullname.equalsIgnoreCase(""))
        {
             fullname = AppSession.getValue(ct, Consts.PHONE_NUMBER);
            user_email.setText(fullname);
        }
        else {
            logout.setVisibility(View.VISIBLE);
            user_email.setText(fullname);
        }


        try
        {
            String img_url = AppSession.getValue(ct, Consts.IMAGE_URI  );
            if(img_url!= null && !img_url.equalsIgnoreCase(""))
            {
        /*        Glide.with(ct)
                        .load(img_url)
                        .placeholder(R.drawable.defualt_image)
                        .into(imageView);*/

                new ImageDownloaderTask(imageView,img_url, ct).execute(img_url);

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ct, DevicesActivity.class), REQUEST_DEVICE);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                try {


                    AlertDialog.Builder updateAlert = new AlertDialog.Builder(ct);
                    updateAlert.setIcon(R.drawable.logo);
                    updateAlert.setTitle("Gmax GPS");
                    updateAlert.setMessage("Are you sure Do you want to log out?");

                    updateAlert.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    try {
                                        AppSession.save(ct,Consts.EMAIL,"");
                                        AppSession.save(ct, Consts.USER_NAME, "");

                                        AppSession.save(ct, Consts.GOOGLE_ID, "");
                                        AppSession.save(ct, Consts.IMAGE_URI, "");

                                        AppSession.save(ct, Consts.FACEBOOK_ID,"");
                                        AppSession.save(ct, Consts.PHONE_NUMBER,"");

                                        PreferenceManager.getDefaultSharedPreferences(ct)
                                                .edit().putBoolean(MainApplication.PREFERENCE_AUTHENTICATED, false).apply();
                                        ((MainApplication)getApplication()).removeService();

                                        resetStore();

                                        startActivity(new Intent(ct, LoginActivity.class));
                                        finish();
                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }


                                }
                            });

                    updateAlert.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();


                                }
                            });

                    AlertDialog alertUp = updateAlert.create();
                    alertUp.setCanceledOnTouchOutside(false);
                    alertUp.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private  void resetStore()
    {
        AppSession.save(ct, Consts.INTRO_SCREEN , "");
        AppSession.save(ct, Consts.FIRBASE_USER_ID,"");
        AppSession.save(ct, Consts.IMAGE_URI,"");
        AppSession.save(ct, Consts.USER_ID,"");
        AppSession.save(ct, Consts.EMAIL,"");
        AppSession.save(ct, Consts.FIRST_NAME,"");
        AppSession.save(ct, Consts.LAST_NAME,"");
        AppSession.save(ct, Consts.USER_NAME,"");
    }

}
