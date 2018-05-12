package com.gmaxgps.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.gmaxgps.Adapter.AppIntroPagerAdapter;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.CommonFunction;
import com.gmaxgps.Utils.Consts;

import java.util.ArrayList;
import java.util.List;


public class AppIntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    ViewPager mViewPager;
    private AppIntroPagerAdapter mAdapter;
    private LinearLayout viewPagerCountDots;
    private int dotsCount;
    private ImageView[] dots;
    private Context mContext;
    private RelativeLayout llSkip,llStart;
    private TextView btnSkip;
    private TextView btnStart;
    int[] mResources = {R.drawable.splash_icon, R.drawable.splash_icon, R.drawable.splash_icon , R.drawable.splash_icon  , R.drawable.splash_icon};

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        mContext = AppIntroActivity.this;


        btnSkip = (TextView) findViewById(R.id.btnSkip);
        llSkip = (RelativeLayout) findViewById(R.id.llSkip);
        btnStart = (TextView) findViewById(R.id.btnStart);
        llStart = (RelativeLayout) findViewById(R.id.llStart);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerCountDots = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mAdapter = new AppIntroPagerAdapter(AppIntroActivity.this, mContext, mResources);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
        setPageViewIndicator();

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSession.save(mContext, Consts.INTRO_SCREEN , "true");
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSession.save(mContext, Consts.INTRO_SCREEN , "true");
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });


    }

    private void setPageViewIndicator()
    {

        try
        {
            Log.d("###setPageViewIndicator", " : called");
            dotsCount = mAdapter.getCount();
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++)
            {
                dots[i] = new ImageView(mContext);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        35,
                        35
                );

                params.setMargins(4, 0, 4, 0);

                final int presentPosition = i;
                dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mViewPager.setCurrentItem(presentPosition);
                        return true;
                    }

                });


                viewPagerCountDots.addView(dots[i], params);
            }

            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 4) {
            llStart.setClickable(true);
            llSkip.setClickable(false);
            llStart.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.GONE);

        } else {
            btnStart.setVisibility(View.GONE);
            btnSkip.setVisibility(View.VISIBLE);
            llSkip.setClickable(true);
            llStart.setClickable(true);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("###onPageSelected, pos ", String.valueOf(position));
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {

        } else {

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void scrollPage(int position) {
        mViewPager.setCurrentItem(position);
    }



}
