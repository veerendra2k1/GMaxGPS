package com.gmaxgps.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmaxgps.Activities.AppIntroActivity;
import com.gmaxgps.Activities.LoginActivity;
import com.gmaxgps.R;


public class AppIntroPagerAdapter extends PagerAdapter {
    private Context mContext;
    LayoutInflater mLayoutInflater;
    private int[] mResources;
    private AppIntroActivity activity;


    public AppIntroPagerAdapter(AppIntroActivity appIntroActivity, Context mContext, int[] mResources) {
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
        this.activity = appIntroActivity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.adapter_appintropager, container, false);
        //ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_foster);
        ImageView iv_foster = (ImageView) itemView.findViewById(R.id.iv_foster);
        TextView ctvHeder = (TextView) itemView.findViewById(R.id.ctvHeder);
        TextView ctvIntro = (TextView) itemView.findViewById(R.id.ctvIntro);
        iv_foster.setImageResource(mResources[position]);
        setDescText(position, ctvHeder, ctvIntro);


        container.addView(itemView);
        ctvHeder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 2) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    ((Activity) mContext).finish();
                }

                int pos = position + 1;
                activity.scrollPage(pos);


            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setDescText(int pos, TextView ctvText, TextView ctvText1) {
        switch (pos) {
            case 0:
                ctvText.setText(mContext.getString(R.string.first_intro));
                ctvText1.setText(mContext.getString(R.string.first_intro_txt));
                break;
            case 1:
                ctvText.setText(mContext.getString(R.string.second_intro));
                ctvText1.setText(mContext.getString(R.string.second_intro_txt));
                break;
            case 2:
                ctvText.setText(mContext.getString(R.string.third_intro));
                ctvText1.setText(mContext.getString(R.string.third_intro_txt));
                break;
            case 3:
                ctvText.setText("Dashboard & Leaderboard!");
                ctvText1.setText(mContext.getString(R.string.fourth_intro_txt) );
                break;
            case 4:
                ctvText.setText("Internet and GPS!");
                ctvText1.setText(mContext.getString(R.string.fifth_intro_txt));
                break;
        }
    }
}