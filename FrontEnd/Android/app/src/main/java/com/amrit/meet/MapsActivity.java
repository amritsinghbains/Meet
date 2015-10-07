package com.amrit.meet;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Hashtable markers;
    private Map<String, Integer> iconImages;
    private Map<String, String> iconImagesText;
    private Map<String, Integer> iconImagesSound;
    private int animateLocationCount = 0;
    String deviceId;
    String vehicleName;
    ImageButton flashButtonOn;
    MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);
        iconImages = new HashMap<String, Integer>();
        iconImagesText = new HashMap<String, String>();
        iconImagesSound = new HashMap<String, Integer>();
        initializeIcons();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        vehicleName = getIntent().getStringExtra("my_vehicle_name");
        setUpMapIfNeeded();
        connectWebSocket();
        markers = new Hashtable();
        flashButtonOn = (ImageButton) findViewById(R.id.my_vehicle);
        flashButtonOn.setImageResource(iconImages.get(vehicleName));
        mPlayer = MediaPlayer.create(MapsActivity.this, iconImagesSound.get(vehicleName));
    }

    private void initializeIcons(){
        iconImages.put("bike1",R.drawable.bike1);
        iconImages.put("bike2",R.drawable.bike2);
        iconImages.put("bike3",R.drawable.bike3);
        iconImages.put("car1",R.drawable.car1);
        iconImages.put("car2",R.drawable.car2);
        iconImages.put("car3",R.drawable.car3);
        iconImages.put("car4",R.drawable.car4);
        iconImages.put("plane1",R.drawable.plane1);
        iconImages.put("plane2",R.drawable.plane2);
        iconImages.put("truck1",R.drawable.truck1);

        iconImagesText.put("bike1","White Walker");
        iconImagesText.put("bike2","Black Bull");
        iconImagesText.put("bike3","Indian Bullet");
        iconImagesText.put("car1","White Car");
        iconImagesText.put("car2","Pick up Truck");
        iconImagesText.put("car3","Convertible");
        iconImagesText.put("car4","Cop Car");
        iconImagesText.put("plane1","Cruiser Place");
        iconImagesText.put("plane2","Fighter Jet");
        iconImagesText.put("truck1","Sport Truck");

        iconImagesSound.put("bike1",R.raw.bike);
        iconImagesSound.put("bike2",R.raw.bike);
        iconImagesSound.put("bike3",R.raw.bike);
        iconImagesSound.put("car1",R.raw.car);
        iconImagesSound.put("car2",R.raw.car);
        iconImagesSound.put("car3",R.raw.car);
        iconImagesSound.put("car4",R.raw.copcar);
        iconImagesSound.put("plane1",R.raw.plane);
        iconImagesSound.put("plane2",R.raw.plane);
        iconImagesSound.put("truck1",R.raw.truck);

    }

    public void my_vehicle_tap(View view){
//        mPlayer.start();
        if(markers.size() == 0){
            Toast.makeText(MapsActivity.this,
                    "You are driving the " + iconImagesText.get(vehicleName), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MapsActivity.this,
                    "You are driving the " + iconImagesText.get(vehicleName) + " with " + markers.size() + " other vehicles in your area", Toast.LENGTH_LONG).show();
        }
    }
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//            mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                if(animateLocationCount < 1) {
                    animateLocationCount++;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f));
                }
                sendMessage(loc.latitude, loc.longitude);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

    }

    private int socketConnectedFlag = 0;

    WebSocketClient mWebSocketClient;
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://meetsocket.herokuapp.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
//                Log.i("Websocket", "Opened");
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                socketConnectedFlag = 1;
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonarray = new JSONArray(message);
                            for(int i=0; i<jsonarray.length(); i++){
                                JSONObject obj = jsonarray.getJSONObject(i);
                                if(!deviceId.equals(obj.getString("uniqueid"))) {
                                    if (markers.get(obj.getString("uniqueid")) == null) {
//                                    Toast.makeText(MapsActivity.this,
//                                            "new location" + markers.get(obj.getString("uniqueid")), Toast.LENGTH_SHORT).show();
                                        MarkerOptions a = new MarkerOptions()
                                                .position(new LatLng(obj.getDouble("lat"), obj.getDouble("long"))).title(obj.getString("time"));

                                        a.icon(BitmapDescriptorFactory.fromResource(iconImages.get(obj.getString("name"))));
                                        Marker m = mMap.addMarker(a);
                                        markers.put(obj.getString("uniqueid"), m);
                                    } else {

                                        Marker b = (Marker) markers.get(obj.getString("uniqueid"));
                                        //get angle between points
                                        double angle = angleFromCoordinate(b.getPosition().latitude, b.getPosition().longitude, obj.getDouble("lat"), obj.getDouble("long"));
                                        b.setPosition(new LatLng(obj.getDouble("lat"), obj.getDouble("long")));
                                        b.setIcon(BitmapDescriptorFactory.fromResource(iconImages.get(obj.getString("name"))));
                                        b.setTitle(obj.getString("time"));
                                        if (angle > 5 && angle < 355) {
                                            b.setRotation((float) angle);
                                        }
                                        markers.put(obj.getString("uniqueid"), b);

                                    }
                                }
                            }
                        }catch(Exception e){

                        }
                        }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
//                Log.i("Websocket", "Closed " + s);
                socketConnectedFlag = 0;
            }

            @Override
            public void onError(Exception e) {
//                Log.i("Websocket", "Error " + e.getMessage());
                socketConnectedFlag = 0;
            }
        };
        mWebSocketClient.connect();
    }

    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }

    public void sendMessage(Double latitude, Double longitude) {
        String messageToSend = "{\"name\": \"" + vehicleName + "\",\"uniqueid\": \"" + deviceId + "\",\"lat\": \"" + latitude.toString() + "\",\"long\": \"" + longitude.toString() + "\",\"time\": \"1000\"}";
        if (socketConnectedFlag == 1) {
            mWebSocketClient.send(messageToSend);
        }

    }

}
