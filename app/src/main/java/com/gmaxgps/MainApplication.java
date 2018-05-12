/*
 * Copyright 2015 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
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
package com.gmaxgps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.model.Position;
import com.gmaxgps.model.User;
import com.google.android.gms.ads.internal.gmsg.HttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainApplication extends MultiDexApplication {

    public static final String PREFERENCE_AUTHENTICATED = "authenticated";
    public static final String PREFERENCE_URL = "url";
    public static final String PREFERENCE_EMAIL = "email";
    public static final String PREFERENCE_PHONE = "phone";
    public static final String PREFERENCE_PASSWORD = "password";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    private static final String DEFAULT_SERVER = "http://63.142.252.27/"; // local - http://10.0.2.2:8082

    public interface GetServiceCallback {
        void onServiceReady(OkHttpClient client, Retrofit retrofit, WebService service);
        boolean onFailure();
    }

    private SharedPreferences preferences;

    private OkHttpClient client;
    private WebService service;
    private Retrofit retrofit;
    private User user;
    private Position position;
Context ct = this;
    private final List<GetServiceCallback> callbacks = new LinkedList<>();

    public void getServiceAsync(GetServiceCallback callback) {
        if (service != null)
        {
            callback.onServiceReady(client, retrofit, service);
        } else {
            if (callbacks.isEmpty()) {
                initService();
            }
            callbacks.add(callback);
        }
    }

    public WebService getService() { return service; }
    public User getUser() { return user; }
    public Position getPosition(){return position;}

    public void removeService() {
        service = null;
        user = null;
        position = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!preferences.contains(PREFERENCE_URL)) {
            preferences.edit().putString(PREFERENCE_URL, DEFAULT_SERVER).apply();
        }
    }

    private void initService() {
        final String url = preferences.getString(PREFERENCE_URL, null);
        String email = preferences.getString(PREFERENCE_EMAIL, null);
        final String password = preferences.getString(PREFERENCE_PASSWORD, null);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient.Builder()
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager)).build();

        try {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(url)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        } catch (IllegalArgumentException e) {
           // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            for (GetServiceCallback callback : callbacks) {
                callback.onFailure();
            }
            callbacks.clear();
        }

        final WebService service = retrofit.create(WebService.class);

        service.addSession(email, password).enqueue(new WebServiceCallback<User>(this)
        {
            @Override
            public void onSuccess(Response<User> response) {
                MainApplication.this.service = service;
                MainApplication.this.user = response.body();
                for (GetServiceCallback callback : callbacks)
                {
                    callback.onServiceReady(client, retrofit, service);
                }
                callbacks.clear();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t)
            {
               /* boolean handled = false;
                for (GetServiceCallback callback : callbacks)
                {
                    handled = callback.onFailure();
                }
                callbacks.clear();
                if (!handled)
                {
                    super.onFailure(call, t);
                }*/

                SignUpNewUser();

            }
        });
    }



    private void SignUpNewUser()
    {
       new TestAsync().execute();
    }





    class TestAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

            String response = "";

        protected String doInBackground(Void...arg0) {

            String email = preferences.getString(PREFERENCE_EMAIL, null);
            final String password = preferences.getString(PREFERENCE_PASSWORD, null);
            final String url = preferences.getString(PREFERENCE_URL, null);
            final String phone = preferences.getString(PREFERENCE_PHONE, null);
            final String first_name = preferences.getString(FIRST_NAME, null);
            final String last_name = preferences.getString(LAST_NAME, null);

          /*  HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("code", "testcode"));
            urlParameters.add(new BasicNameValuePair("monthact", "feb-2015"));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);*/





            try
            {
                HttpPost httpPost = new HttpPost(url+"api/users");
                httpPost.setHeader(HTTP.CONTENT_TYPE,
                        "application/json");
                HttpParams httpParameters = new BasicHttpParams();

                int timeoutConnection = 15000;

                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);






                JSONObject main_object = new JSONObject();



                main_object.put("email", email);
                main_object.put("password", password);
                main_object.put("name", first_name + " "+ last_name);
                main_object.put("phone", phone);


                StringEntity se = new StringEntity(main_object.toString(), "UTF-8");
                httpPost.setEntity(se);


                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();


                response = EntityUtils.toString(httpEntity, HTTP.UTF_8);
                if(response!= null)
                {

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }




            return "You are at PostExecute";
        }



        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(response!= null && !response.equalsIgnoreCase(""))
            {


                try {
                    String user_id = "" , email = "";

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("id"))
                        user_id =  jsonObject.getString("id");

                    AppSession.save(ct, Consts.USER_ID , user_id);

                    initService();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }
    }


}
