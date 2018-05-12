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
package com.gmaxgps.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmaxgps.Activities.DevicesActivity;
import com.gmaxgps.Activities.LoginActivity;
import com.gmaxgps.MainApplication;
import com.gmaxgps.R;
import com.gmaxgps.Utils.AppSession;
import com.gmaxgps.Utils.Consts;
import com.gmaxgps.WebService;
import com.gmaxgps.WebServiceCallback;
import com.gmaxgps.model.LocationUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.gmaxgps.model.Device;
import com.gmaxgps.model.Position;
import com.gmaxgps.model.Update;
import com.gmaxgps.model.User;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import retrofit2.Retrofit;

public class MainFragment extends SupportMapFragment
        implements OnMapReadyCallback {

    public static final int REQUEST_DEVICE = 1;
    public static final int RESULT_SUCCESS = 1;
    //  public static final int REQUEST_EVENT = 1 ;
    List<LocationUpdate> locationUpdates = new ArrayList<>();
    private GoogleMap map;

    boolean is_running = false;
    boolean smooth_move = false;
    boolean is_first_time = true;
    boolean isMarkerRotating = true;

    private Handler handler = new Handler();
    private Handler handler_update = new Handler();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<Long, Device> devices = new HashMap<>();
    private Map<Long, Position> positions = new HashMap<>();
    private Map<Long, Marker> markers = new HashMap<>();

    private WebSocketCall webSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMapAsync(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_devices:
                startActivityForResult(new Intent(getContext(), DevicesActivity.class), REQUEST_DEVICE);
                return true;

            //   case R.id.action_notify:
            //     startActivityForResult(new Intent(getContext(), NotifyActivity.class), REQUEST_EVENT);

            case R.id.action_logout:
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit().putBoolean(MainApplication.PREFERENCE_AUTHENTICATED, false).apply();
                ((MainApplication) getActivity().getApplication()).removeService();

                resetStore();
                getActivity().finish();
                startActivity(new Intent(getContext(), LoginActivity.class));
                return true;

            case R.id.maptypeHYBRID:
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }
            case R.id.maptypeNONE:
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                }
            case R.id.maptypeNORMAL:
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                }
            case R.id.maptypeSATELLITE:
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetStore() {
        AppSession.save(getContext(), Consts.FIRBASE_USER_ID, "");
        AppSession.save(getContext(), Consts.IMAGE_URI, "");
        AppSession.save(getContext(), Consts.USER_ID, "");
        AppSession.save(getContext(), Consts.EMAIL, "");
        AppSession.save(getContext(), Consts.FIRST_NAME, "");
        AppSession.save(getContext(), Consts.LAST_NAME, "");
        AppSession.save(getContext(), Consts.USER_NAME, "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DEVICE && resultCode == RESULT_SUCCESS) {
            long deviceId = data.getLongExtra(DevicesFragment.EXTRA_DEVICE_ID, 0);
            Position position = positions.get(deviceId);
            if (position != null) {
                map.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(position.getLatitude(), position.getLongitude())));
                markers.get(deviceId).showInfoWindow();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final MainApplication application = (MainApplication) getContext().getApplicationContext();
        final User user = application.getUser();
        // setMapStyle();


        // MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this,R.)

        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.getContext(), R.raw.mapstyle_nivi));

        map.setMapType(1);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
         map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(72,23),15));
        //  map.setTrafficEnabled(true);
        //  map.getUiSettings().setRotateGesturesEnabled(true);
        // map.getUiSettings().setCompassEnabled(true);

        // map.getUiSettings().setMyLocationButtonEnabled(true);

        // map.setMyLocationEnabled(true);

        // Marker marker = map.addMarker(new MarkerOptions()
        //        .position(new LatLng(user.getLatitude(), user.getLongitude()))
        //        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
        //        .title("My Car"));


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                @SuppressLint("RestrictedApi") View view = getLayoutInflater(null).inflate(R.layout.view_info, null);
                ((TextView) view.findViewById(R.id.title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.details)).setText(marker.getSnippet());
                return view;
            }
        });

        createWebSocket();

       // new UpdateLocationAsync().execute();


    }

    private String formatDetails(Position position) {
        final MainApplication application = (MainApplication) getContext().getApplicationContext();
        final User user = application.getUser();

        SimpleDateFormat dateFormat;
        if (user.getTwelveHourFormat()) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        String speedUnit = getString(R.string.user_kmh);
        double factor = 1;
        if (user.getSpeedUnit() != null) {
            switch (user.getSpeedUnit()) {
                case "kmh":
                    speedUnit = getString(R.string.user_kmh);
                    factor = 1.852;
                    // factor = 1;
                    break;
                case "mph":
                    speedUnit = getString(R.string.user_mph);
                    factor = 1.15078;
                    break;
                default:
                    speedUnit = getString(R.string.user_kn);
                    factor = 1;
                    break;
            }
        }
        double speed = position.getSpeed() * factor;

        return new StringBuilder()
                .append(getString(R.string.position_time)).append(": ")
                .append(dateFormat.format(position.getFixTime())).append('\n')
                .append(getString(R.string.position_latitude)).append(": ")
                .append(String.format("%.5f", position.getLatitude())).append('\n')
                .append(getString(R.string.position_longitude)).append(": ")
                .append(String.format("%.5f", position.getLongitude())).append('\n')
                .append(getString(R.string.position_altitude)).append(": ")
                .append(String.format("%.1f", position.getAltitude())).append('\n')
                .append(getString(R.string.position_speed)).append(": ")
                .append(String.format("%.1f", speed)).append(' ')
                .append(speedUnit).append('\n')
                .append(getString(R.string.position_course)).append(": ")
                .append(String.format("%.1f", position.getCourse()))
                .toString();



    }
int counter = 0;
    private void handleMessage(String message) throws IOException {
        Update update = objectMapper.readValue(message, Update.class);
        if (update != null && update.positions != null)
        {
            //map.clear();
            //markers.clear();
            for (Position position : update.positions)
            {
                long deviceId = position.getDeviceId();
                if (devices.containsKey(deviceId)) {
                    LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
                    Marker marker = markers.get(deviceId);
                    if (marker == null) {
                        marker = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_topview))
                                .title(devices.get(deviceId).getName()).position(location));

                        markers.put(deviceId, marker);


                    } else {
                       // marker.setPosition(location);
                    }

                    if (locationUpdates == null || locationUpdates.size() == 0  )
                    {
                        LocationUpdate locationUpdate = new LocationUpdate();
                        locationUpdate.setDeviceId(deviceId);
                        locationUpdate.setLocation(location);
                        locationUpdate.setlocation_update(true);
                        locationUpdates.add(locationUpdate);
                        marker.setPosition(location);
                        marker.setRotation( (float)position.getCourse());
                        marker.setSnippet(formatDetails(position));

                    } else
                        {
                            boolean already_exist = false;
                            for (int i = 0; i < locationUpdates.size(); i++)
                            {
                                if (locationUpdates.get(i).getDeviceId() == deviceId)
                                {
                                    already_exist = true;
                                    break;
                                }
                            }

                            if(already_exist)
                            {
                                for (int i = 0; i < locationUpdates.size(); i++)
                                {
                                    if (locationUpdates.get(i).getDeviceId() == deviceId )
                                    {

                                        LatLng location2 = new LatLng( locationUpdates.get(i).getLocation().latitude, locationUpdates.get(i).getLocation().longitude);
                                      if((location2.latitude == location.latitude) && (location2.longitude == location.longitude))
                                      {

                                          marker.setRotation( (float)position.getCourse());
                                          marker.setSnippet(formatDetails(position));
                                          marker.setPosition(location);
                                      }
                                      else
                                      {
                                          //marker.setRotation( (float)position.getCourse());
                                          marker.setSnippet(formatDetails(position));

                                          animateMarkerNew(location2  ,location  ,   marker);
                                      }


                                        locationUpdates.get(i).setLocation(location);
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                LocationUpdate locationUpdate = new LocationUpdate();
                                locationUpdate.setDeviceId(deviceId);
                                locationUpdate.setLocation(location);
                                locationUpdate.setlocation_update(true);
                                locationUpdates.add(locationUpdate);
                                marker.setPosition(location);
                                marker.setRotation( (float)position.getCourse());
                                marker.setSnippet(formatDetails(position));
                            }


                    }



                           /* if(deviceId == 17)
                            {
                                LatLng location2 = new LatLng( 27.165294355, 77.98941355);
                                animateMarkerNew( location ,location2  ,   marker);
                            }
                            else if(deviceId == 12)
                            {

                                LatLng location2 = new LatLng( 27.76985, 78.159975);
                                animateMarkerNew( location ,location2  ,   marker);
                            }
*/




                    positions.put(deviceId, position);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    private void reconnectWebSocket() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    createWebSocket();
                }
            }
        });
    }


    MainApplication application = null;

    private void createWebSocket() {
        if (application == null)
            application = (MainApplication) getActivity().getApplication();

        application.getServiceAsync(new MainApplication.GetServiceCallback() {
            @Override
            public void onServiceReady(final OkHttpClient client, final Retrofit retrofit, WebService service) {
                User user = application.getUser();

                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(27,78),13));
                if (is_first_time) {
                    is_first_time = false;
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(user.getLatitude(), user.getLongitude()), user.getZoom()));
                }


                //  Position position = application.getPosition();
                //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                //       new LatLng(position.getLatitude(), position.getLongitude()),user.getZoom()));


                service.getDevices().enqueue(new WebServiceCallback<List<Device>>(getContext()) {
                    @Override
                    public void onSuccess(retrofit2.Response<List<Device>> response) {
                        for (Device device : response.body()) {
                            if (device != null) {
                                devices.put(device.getId(), device);
                            }
                        }

                        Request request = new Request.Builder().url(retrofit.baseUrl().url().toString() + "api/socket").build();
                        webSocket = WebSocketCall.create(client, request);
                        webSocket.enqueue(new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                            }

                            @Override
                            public void onFailure(IOException e, Response response) {
                                is_running = false;
                                reconnectWebSocket();
                             //   new UpdateLocationAsync().execute();
                            }

                            @Override
                            public void onMessage(ResponseBody message) throws IOException
                            {
                                final String data = message.string();
                                if(is_running == false)
                                {

                                    is_running = true;
                                handler.postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        try {
                                            handleMessage(data);

                                            is_running = false;

                                           // new UpdateLocationAsync().execute();

                                           /* handler_update.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new UpdateLocationAsync().execute();
                                                }
                                            },3000);
*/

                                        } catch (IOException e) {
                                            Log.w(MainFragment.class.getSimpleName(), e);
                                        }
                                    }
                                }, 2000);
                                }


                            }

                            @Override
                            public void onPong(Buffer payload) {
                            }

                            @Override
                            public void onClose(int code, String reason) {
                                is_running = false;
                                reconnectWebSocket();
                                //new UpdateLocationAsync().execute();
                            }
                        });
                    }
                });
            }

            @Override
            public boolean onFailure() {
                return false;
            }
        });


    }


    private void animateMarkerNew(final LatLng startPosition, final LatLng destination, final Marker marker) {

        if (marker != null) {

            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        /*map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(18f)
                                .build()));*/
                        float bearing = getBearing(startPosition, new LatLng(newPosition.latitude, newPosition.longitude));

                        marker.setRotation(bearing);
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }


    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
    class UpdateLocationAsync extends AsyncTask<Void, Integer, String> {


        protected String doInBackground(Void... arg0) {


            return "You are at PostExecute";
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (is_running == false)
            {
                is_running = true;
                if (locationUpdates != null && locationUpdates.size() > 0) {
                    for (int i = 0; i < locationUpdates.size(); i++)

                    {

                        locationUpdates.get(i).setlocation_update(false);

                    }
                }


                createWebSocket();
            }
           /* handler_update.postDelayed(new Runnable()
            {
                @Override
                public void run() {

                }
            },5000);*/
        }
    }


    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


}
