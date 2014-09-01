package com.amrit.meet;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.amrit.meet.Service.ServiceHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.provider.Settings.Secure;

public class MainMap extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        setUpMapIfNeeded();

    }

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

//        new GetContacts().execute();
        new GetRoom().execute();



    }
    String android_id;
    String group_uuid;
    private static String urlGetContacts = "http://192.168.1.166:6006/getUsersInLocation";
    private static String urlGetRoom = "http://192.168.1.166:6006/hostLocation";
    JSONArray contacts = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_UUID = "uuid";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_TIMESTAMP = "timestamp";

        private class GetContacts extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                //make a list pair
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("uuid", android_id));
                params.add(new BasicNameValuePair("group_uuid", group_uuid));

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(urlGetContacts, ServiceHandler.POST, params);

                Log.d("Response Members: ", "> " + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONArray jsonObj = new JSONArray(jsonStr);

                        // looping through All Contacts
                        for (int i = 0; i < jsonObj.length(); i++) {
                            JSONObject c = jsonObj.getJSONObject(i);

                            String uuid = c.getString(TAG_UUID);
                            Double latitude = c.getDouble(TAG_LATITUDE);
                            Double longitude = c.getDouble(TAG_LONGITUDE);
                            String timestamp = c.getString(TAG_TIMESTAMP);

//                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(uuid.charAt(0)+""));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("haha"));
//                            Log.d("Response2", uuid);
//                            Log.d("Response2", latitude);
//                            Log.d("Response2", longitude);
//                            Log.d("Response2", timestamp);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
//                if (pDialog.isShowing())
//                    pDialog.dismiss();
                /**
                 * Updating parsed JSON data into ListView
                 * */


            }
        }

        private class GetRoom extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(urlGetRoom, ServiceHandler.POST);
                jsonStr = jsonStr.replaceAll("^\"|\"$", "");
//                group_uuid = jsonStr;
                group_uuid = "zanyzebra8";
                Log.d("Response Room: ", "> " + group_uuid);
                new GetContacts().execute();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
    //                if (pDialog.isShowing())
    //                    pDialog.dismiss();
                /**
                 * Updating parsed JSON data into ListView
                 * */


            }
        }
    }
