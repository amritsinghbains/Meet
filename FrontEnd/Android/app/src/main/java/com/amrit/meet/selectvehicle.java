package com.amrit.meet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class selectvehicle extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectvehicle);
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setTitle(R.string.select_vehicle);
        turnGPSOn(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selectvehicle, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            sendEmail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendEmail(){
        String[] TO = {"amrit.singh.bains@live.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Meet on Android Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I really like this app and ");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(selectvehicle.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
    public void turnGPSOn(final Activity activity) {
        LocationManager locationManager = (LocationManager) this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(activity);
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Enable location"
                    + " services to find current location.  Click OK to go to"
                    + " location services settings to let you do so.";

            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                    Toast.makeText(selectvehicle.this,
                                            "Enable Location to get Full Features.", Toast.LENGTH_LONG).show();
                                }
                            });
            builder.create().show();
        }
    }

    public void car1_tap(View view){
        goToMap("car1");
    }
    public void car2_tap(View view){
        goToMap("car2");
    }
    public void car3_tap(View view){
        goToMap("car3");
    }
    public void car4_tap(View view){
        goToMap("car4");
    }

    public void bike1_tap(View view){
        goToMap("bike1");
    }
    public void bike2_tap(View view){
        goToMap("bike2");
    }
    public void bike3_tap(View view){
        goToMap("bike3");
    }

    public void plane1_tap(View view){
        goToMap("plane1");
    }
    public void plane2_tap(View view){
        goToMap("plane2");
    }

    public void truck1_tap(View view){
        goToMap("truck1");
    }

    public void goToMap(String vehicle){
        Intent intent = new Intent(this, MapsActivity.class).putExtra("my_vehicle_name", vehicle);;
        startActivity(intent);
        finish();
    }

}
