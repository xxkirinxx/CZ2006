package com.example.cz2006.Controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cz2006.Entity.LocationData;
import com.example.cz2006.Entity.LocationLatLng;
import com.example.cz2006.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.cz2006.Constants.ERROR_DIALOG_REQUEST;
import static com.example.cz2006.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;

    public ArrayList <LocationData> neaResult = new ArrayList();
    public Marker locMarker;

    private static final String TAG = "MainActivity";

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AndroidNetworking.initialize(getApplicationContext());
        //getVolleyResponse();

        setContentView(R.layout.activity_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        neaAPI();
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful())
                {
                    Location location= task.getResult();
                    if(location!=null)
                    {
                        try
                        {
                            Geocoder geoCoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                            Log.d(TAG, "onComplete: " + geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1));
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(checkMapServices()){
//            if(mLocationPermissionGranted){
//                getChatrooms();
//            }
//            else{
//                getLocationPermission();
//            }
//        }
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng Singapore = new LatLng(1.3521, 103.8198);
        mMap.addMarker(new MarkerOptions().position(Singapore).title("Marker in Singapore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Singapore));

        getLocationPermission();
        int a = ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("CZ2006", String.valueOf(a));
        getLastKnownLocation();
        mMap.setMyLocationEnabled(true);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Calling Data Gov Dengue Cluster's API
    private void neaAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String URL = "https://www.nea.gov.sg/api/OneMap/GetMapData/DENGUE_CLUSTER";

        StringRequest objectRequest = new StringRequest(
                Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Rest Response", response.toString());
                response = response.replace(",{","^{");
                String[] res = response.split("\\^");

                //Exclude Line 0
                for(int i = 1;i < res.length;i++){
                    try {
                        res[i] = res[i].replace("\\", "");
                        JSONObject obj = new JSONObject(res[i]);
                        LocationData location = new LocationData();
                        location.setDesc(obj.getString("DESCRIPTION"));
                        location.setCaseSize(obj.getString("CASE_SIZE"));
                        location.setLatLng(obj.getString("LatLng"));
                        neaResult.add(location);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                addDengueCluster();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Response", error.toString());
            }
        }
        );

        requestQueue.add(objectRequest);
    }

    public void addDengueCluster(){

        if(neaResult.size() > 0){
            for(int i = 0; i < neaResult.size();i++){

                ArrayList<LocationLatLng> latLngs = new ArrayList<LocationLatLng>();

                LocationData ld = neaResult.get(i);
                String latLng = ld.getLatLng();
                String[] latLngCut = latLng.split("\\|");
                for(int j = 0; j < latLngCut.length;j++){
                    String[] res = latLngCut[j].split(",");
                    LocationLatLng llg = new LocationLatLng();
                    llg.setLocLat(Double.parseDouble(res[0]));
                    llg.setLocLng(Double.parseDouble(res[1]));
                    //llg.setlocCaseSize(ld.getCaseSize());
                    latLngs.add(llg);
                }
                if(latLngs.size()>0){
                    PolygonOptions poly = new PolygonOptions();

                    if(Double.parseDouble(ld.getCaseSize())>= 10){
                        poly.fillColor(Color.RED);
                    }else if(Double.parseDouble(ld.getCaseSize())<10 && Double.parseDouble(ld.getCaseSize())>0){
                        poly.fillColor(Color.YELLOW);
                    }else if(Double.parseDouble(ld.getCaseSize())==0){
                        poly.fillColor(Color.GREEN);
                    }

                    // Initial point
                    //poly.add(new LatLng(9.6632139, 80.0133258));

                    // ... then the rest.
                    for(int k = 0; k < latLngs.size(); k++)
                    {
                        LocationLatLng ll = latLngs.get(k);
                        poly.add(new LatLng(ll.getLocLat(), ll.getLocLng()));
                    }

                    // Done! Add to map.
                    mMap.addPolygon(poly);

                }else{
                    // Error handling
                }
            }

            /*if(latLngs.size()>0){
                PolygonOptions poly = new PolygonOptions();

                if
                poly.fillColor(Color.clusterColor);

                // Initial point
                //poly.add(new LatLng(9.6632139, 80.0133258);

                // ... then the rest.
                for(int i = 0; i < length; i++)
                {
                    poly.add(new LatLng(array[i].a, array[i].b));
                }

                // Done! Add to map.
                mMap.addPolygon(poly);
            }else{
                // Error handling
            }*/




        }

    }
}