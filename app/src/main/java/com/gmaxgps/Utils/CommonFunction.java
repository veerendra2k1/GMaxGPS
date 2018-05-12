package com.gmaxgps.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.gmaxgps.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maroof Ahmed Siddique on 9/26/2016.
 */
public class CommonFunction {
    private static String keys = "";

    public CommonFunction() {

    }
public static void hideKeyBoard(Context context){
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
}
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static String gettime()
    {
        String time = "";
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }

    public static String convertBitmapToString(Bitmap bm) {

        byte[] byte_arr = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte_arr = stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(byte_arr, Base64.DEFAULT);

    }
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }
    public static int getDevicewidth(Context ct) {
        DisplayMetrics displayMetrics = ct.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context ct) {
        DisplayMetrics displayMetrics = ct.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static String getdate() {
        String time = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    public static String getDateToString(Date date) {
        String time = "";
        try {
           // Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            time = df.format(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }


    public static String getCurrentdateTime() {
        String time = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            time = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;

    }



    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1000;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String GetDDMM(String dd_mm_yyyy)
    {
        String date_time = "";

        String day = "",Month = "",year = "";
        String appint_array[] = dd_mm_yyyy.split(" ");
        if (appint_array != null && appint_array.length > 0)
        {
            String date_suggestby_counsel = appint_array[0];

            String datesplitarray[] = date_suggestby_counsel.split("/");

            if (datesplitarray != null && datesplitarray.length > 2)
            {
                day = datesplitarray[1];
                Month = datesplitarray[0];

            }
            date_time = day+"/"+Month;

        }
        return date_time;
    }



public static String ChangeDateFormat(String mm_dd_yyyy)
{
    String date_time = "";

    String day = "",Month = "",year = "";
    String appint_array[] = mm_dd_yyyy.split(" ");
    if (appint_array != null && appint_array.length > 0)
    {
        String date_suggestby_counsel = appint_array[0];

        String datesplitarray[] = date_suggestby_counsel.split("/");

        if (datesplitarray != null && datesplitarray.length > 2)
        {
            day = datesplitarray[1];
            Month = datesplitarray[0];
            year = datesplitarray[2];
        }
        date_time = day+"/"+Month+"/"+year +" "+appint_array[1];

    }
    return date_time;
}


    public static int Getdatediff(String time , String CompareDate)
    {
        int day = 0;
        try {
            String outputPattern = "MM/dd/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(CompareDate);
            Date Date2 = format.parse(time);
            long mills = Date2.getTime() - Date1.getTime();
            long Day1 = mills / (1000 * 60 * 60);

            day = (int) Day1 / 24;


           /* if (day < 0)
                day = 0;*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    public static boolean isSpecialChar(String word) {

        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(word);
        return hasSpecial.find();
    }

    public static boolean isUpperCase(String word) {

        Pattern letter = Pattern.compile("[A-Z]");
        Matcher hasLetter = letter.matcher(word);
        return hasLetter.find();
    }

    public static boolean isNumber(String word) {

        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasDigit = digit.matcher(word);
        return hasDigit.find();
    }

   /* public static void fetchTriggerWords()
    {

        ParseQuery<ParseObject> fetchTrigger = ParseQuery.getQuery("keywords");
        fetchTrigger.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects != null && objects.size() > 0) {
                    ParseObject mFetchObject = objects.get(0);
                    keys = mFetchObject.getString("keys");
                }
            }
        });
    }*/

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean is_numeric(String msg) {
        boolean is_num = false;
        for (int i = 0; i < 10; i++) {
            if (msg.contains("" + i)) {
                is_num = true;
                break;
            }
        }
        return is_num;
    }




    final static String TODAYS_STEPS = "todays_steps";

    public static int gettodaysstep(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TODAYS_STEPS, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void storertodaystep(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TODAYS_STEPS, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    final static String TODAYS_RECOMONDED_STEP = "todays_recomonded_steps";

    public static int gettodaysrecomndedstep(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TODAYS_RECOMONDED_STEP, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void settodaysrecomndedstep(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TODAYS_RECOMONDED_STEP, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }

    /*final static String LOCAL_LATLANG = "local_lat_lang";
    public static int GetLatLang(Context context)
    {
        int restoredText = 0;
        try
        {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(LOCAL_LATLANG, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void SetLatLang(Context context,int steps)
    {
        try
        {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(LOCAL_LATLANG,steps);
            editor.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }*/


    public static int Getdiffbettwodate(String newtime, String oldtime) {
        int day = 0;
        try {
            String outputPattern = "MM/dd/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(outputPattern);


            Date Date1 = format.parse(newtime);
            Date Date2 = format.parse(oldtime);
            long mills = Date1.getTime() - Date2.getTime();
            long Day1 = mills / (1000 * 60 * 60);

            day = (int) Day1 / 24;


            if (day < 0)
                day = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    final static String TARGET_PUSH_DATE = "target_push_date";

    public static int GetTargetPushDate(Context context) {
        int restoredText = 0;
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getInt(TARGET_PUSH_DATE, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void SetTargetPushDate(Context context, int steps) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putInt(TARGET_PUSH_DATE, steps);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    final static String FITNESS_OFF_ON = "fitnesss_off_on";

    public static String getfitnessonoff(Context context) {
        String restoredText = "";
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getString(FITNESS_OFF_ON, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void setfitnessonoff(Context context, String onoff) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putString(FITNESS_OFF_ON, onoff);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    final static String TODAYS_DATE = "todays_date";

    public static String gettodaysdate(Context context) {
        String restoredText = "";
        try {
            SharedPreferences prefs = context.getSharedPreferences("Silent", context.MODE_PRIVATE);
            restoredText = prefs.getString(TODAYS_DATE, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restoredText;
    }

    public static void storertodaydate(Context context, String date) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("Silent", context.MODE_PRIVATE).edit();
            editor.putString(TODAYS_DATE, date);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


 /*   public static void increaem2water()
    {
        ParseQuery<ParseObject> qrcode = ParseQuery.getQuery("VirtualAvatarStats");
        qrcode.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        qrcode.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (objects != null || objects.size() > 0)
                {
                    try
                    {

                        ParseObject vertualinfo = objects.get(0);
                        int waterpercent = vertualinfo.getInt("me2_water");

                        int total_scratch_count =0;

                        try
                        {
                            total_scratch_count = vertualinfo.getInt("total_scratch_count");

                            total_scratch_count = total_scratch_count +1;


                            if(waterpercent <= 90)
                            {
                                MainActivity.water  = waterpercent  + 10;
                            }



                        }
                        catch (Exception e3)
                        {
                            e3.printStackTrace();
                        }


                        if(waterpercent <= 90)
                        {
                            waterpercent = waterpercent + 10;

                            MainActivity.water_progress.setProgress(waterpercent);
                            vertualinfo.put("me2_water",waterpercent);
                            vertualinfo.put("scratch_count",MainActivity.scratch_count);
                            vertualinfo.put("total_scratch_count",total_scratch_count);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);

                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }
                        else
                        {

                            vertualinfo.put("scratch_count",MainActivity.scratch_count);
                            vertualinfo.put("total_scratch_count",total_scratch_count);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);

                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }

                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }
*/





   /* public static void decreasehearfood()
    {
        ParseQuery<ParseObject> qrcode = ParseQuery.getQuery("VirtualAvatarStats");
        qrcode.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        qrcode.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (objects != null || objects.size() > 0)
                {
                    try
                    {

                        ParseObject vertualinfo = objects.get(0);
                        int waterpercent = vertualinfo.getInt("heart_food");


                        try
                        {


                            if(waterpercent >= 20)
                            {
                                MainActivity.food  = waterpercent  - 10;
                            }



                        }
                        catch (Exception e3)
                        {
                            e3.printStackTrace();
                        }



                        if(waterpercent >= 20)
                        {
                            waterpercent = waterpercent - 10;
                            vertualinfo.put("heart_food",waterpercent);

                            MainActivity.food_progress.setProgress(waterpercent);

                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);

                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }

                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }*/




   /* public static void increaehug_oxygen()
    {
        ParseQuery<ParseObject> qrcode = ParseQuery.getQuery("VirtualAvatarStats");
        qrcode.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        qrcode.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (objects != null || objects.size() > 0)
                {
                    try
                    {

                        ParseObject vertualinfo = objects.get(0);
                        int waterpercent = vertualinfo.getInt("hug_oxygen");

                        int total_scratch_count =0;

                        try
                        {
                             total_scratch_count = vertualinfo.getInt("total_scratch_count");

                            total_scratch_count = total_scratch_count +1;

                            if(waterpercent <= 90) {
                                MainActivity.oxygen  = waterpercent  + 10;
                            }

                        }
                        catch (Exception e3)
                        {
                            e3.printStackTrace();
                        }

                        if(waterpercent <= 90)
                        {
                            waterpercent = waterpercent + 10;
                            vertualinfo.put("hug_oxygen",waterpercent);
                            vertualinfo.put("total_scratch_count", total_scratch_count);
                            vertualinfo.put("scratch_count",MainActivity.scratch_count);
                            int waterpercent1 = (int) waterpercent/10;
                            MainActivity.oxygenlevel.setBackgroundColor(Color.parseColor(MainActivity.colorsking[waterpercent1-1]));


                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);

                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }
                        else
                        {
                            vertualinfo.put("scratch_count",MainActivity.scratch_count);
                            vertualinfo.put("total_scratch_count", total_scratch_count);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);

                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }

                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }*/








    /*public static void increaehug_comments_count()
    {
        ParseQuery<ParseObject> qrcode = ParseQuery.getQuery("VirtualAvatarStats");
        qrcode.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        qrcode.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {


                if (objects != null || objects.size() > 0)
                {
                    try
                    {
                        MainActivity.comments_count = MainActivity.comments_count + 1;
                            ParseObject vertualinfo = objects.get(0);
                            vertualinfo.put("comments_count",MainActivity.comments_count);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            vertualinfo.setACL(acl);
                            vertualinfo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });


                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }


*/

    public static Date StringtoDate(String stringdate) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            date = format.parse(stringdate);
            System.out.println(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }


    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output;

        /*if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }*/
        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;


        r = bitmap.getWidth() / 2;


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap get_bitmap_image(String path) {
        Bitmap bmp = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            bmp = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bmp;
    }

    public static String getIntenalSDCardPath() {
        String Path = "";
        Path = "/storage/sdcard1/Android/data/com.helio.silentsecret/Cypher";
        File fPath = new File(Path);
        if (null != fPath) {
            if (!fPath.exists()) {
                Path = "";
                //Path = "/storage/sdcard0/Android/data/com.connectmore.ActivityClasses/m-AdCall";
                Path = "/storage/sdcard0/Android/data/com.helio.silentsecret/Cypher";

                fPath = null;
                fPath = new File(Path);
                if (!fPath.exists()) {
                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    String TARGET_BASE_PATH = extStorageDirectory + File.separator +
                            "Android" + File.separator + "data" + File.separator + "com.helio.silentsecret" + File.separator +
                            "Cypher";

                    fPath = null;
                    fPath = new File(TARGET_BASE_PATH);
                    fPath.mkdirs();
                    Path = TARGET_BASE_PATH;
                }
            }
        }
        fPath = null;
        //ShowLog("CollectionFunctions", "path :: "+Path);
        return Path;
    }







    public static String getLocalTime(String datetime)
    {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datetime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            current = Calendar.getInstance();


            TimeZone tzCurrent = current.getTimeZone();
            int offset = tzCurrent.getRawOffset();
            if (tzCurrent.inDaylightTime(new Date())) {
                offset = offset + tzCurrent.getDSTSavings();
            }

            Calendar current1 = Calendar.getInstance();

            current1.setTime(myDate);
            miliSeconds = current1.getTimeInMillis();
            miliSeconds = miliSeconds + offset;
            resultdate = new Date(miliSeconds);
            SimpleDateFormat destFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            TimeZone london = TimeZone.getTimeZone("Europe/London");
            long now = resultdate.getTime();
            Date resultdate1 = new Date(miliSeconds - london.getOffset(now));
            result = destFormat.format(resultdate1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }
    private static Calendar current;
    private static long miliSeconds;
    private static Date resultdate;

    public static String getGMTTime(String datetime)
    {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datetime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            current = Calendar.getInstance();
            current.setTime(myDate);
            miliSeconds = current.getTimeInMillis();
            TimeZone tzCurrent = current.getTimeZone();
            int offset = tzCurrent.getRawOffset();
            if (tzCurrent.inDaylightTime(new Date())) {
                offset = offset + tzCurrent.getDSTSavings();
            }
            miliSeconds = miliSeconds - offset;
            resultdate = new Date(miliSeconds);
            SimpleDateFormat destFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            TimeZone london = TimeZone.getTimeZone("Europe/London");
            long now = resultdate.getTime();
            Date resultdate1 = new Date(miliSeconds + london.getOffset(now));
            result = destFormat.format(resultdate1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public static double PI = 3.14159265;
    public static double TWOPI = 2*PI;
    public static boolean coordinate_is_inside_polygon(
            double latitude, double longitude,
            ArrayList<Double> lat_array, ArrayList<Double> long_array)
    {
        int i;
        double angle=0;
        double point1_lat;
        double point1_long;
        double point2_lat;
        double point2_long;
        int n = lat_array.size();

        for (i=0;i<n;i++) {
            point1_lat = lat_array.get(i) - latitude;
            point1_long = long_array.get(i) - longitude;
            point2_lat = lat_array.get((i+1)%n) - latitude;
            //you should have paid more attention in high school geometry.
            point2_long = long_array.get((i+1)%n) - longitude;
            angle += Angle2D(point1_lat,point1_long,point2_lat,point2_long);
        }

        if (Math.abs(angle) < PI)
            return false;
        else
            return true;
    }


    public static double Angle2D(double y1, double x1, double y2, double x2)
    {
        double dtheta,theta1,theta2;

        theta1 = Math.atan2(y1,x1);
        theta2 = Math.atan2(y2,x2);
        dtheta = theta2 - theta1;
        while (dtheta > PI)
            dtheta -= TWOPI;
        while (dtheta < -PI)
            dtheta += TWOPI;

        return(dtheta);
    }


    public static ProgressDialog progressDialog = null;
    public static void cancelProgress() {

        try {
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public static void showProgressDialog(final Context context, final String title)
    {

        if (progressDialog != null) {
            try {
                if (progressDialog.isShowing())
                {
                    cancelProgress();
                } else {
                    try {
                        progressDialog.setTitle(title);
                        progressDialog.setIcon(R.mipmap.ic_launcher);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(title);
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(true);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
