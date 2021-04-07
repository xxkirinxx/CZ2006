package com.example.cz2006.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cz2006.Entity.LocationData;
import com.example.cz2006.Entity.LocationLatLng;
import com.example.cz2006.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<LocationData> neaResult = new ArrayList();
    public ArrayList<LocationLatLng> latLngs = new ArrayList<LocationLatLng>();

    private static final int REQUEST_CHECK_SETTINGS = 100;

    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (TextView) findViewById(R.id.txtMessage);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_notifications)
                .build();

        checkPermissions();
        neaAPI();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void launchReport(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.ReportActivity.class);
        startActivity(i);

    }

    public void launchSymptom(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.SymptomActivity.class);
        startActivity(i);
    }

    public void launchLocation(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.LocationActivity.class);
        startActivity(i);

    }

    public void launchMozzie(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.MozzieActivity.class);
        startActivity(i);

    }

    public void launchProfile(View v) {
        Intent i = new Intent(this, com.example.cz2006.Controller.ProfileActivity.class);
        startActivity(i);

    }

    private void neaAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String URL = "https://www.nea.gov.sg/api/OneMap/GetMapData/DENGUE_CLUSTER";

        StringRequest objectRequest = new StringRequest(
                Request.Method.GET,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Rest Response", response.toString());
                response = response.replace(",{", "^{");
                String[] res = response.split("\\^");

                //Exclude Line 0
                for (int i = 1; i < res.length; i++) {
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

                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                LatLng currentllg= new LatLng(gpsTracker.latitude,gpsTracker.longitude);
                //update test area - serangoon
                //LatLng currentllg= new LatLng(1.3554,103.8679);
                getAllLatLngs();
                int area = getNearestArea(currentllg,latLngs);
                String desc = neaResult.get(area).getDesc();
                String nearCase = neaResult.get(area).getCaseSize();
                String message = "";
                //Toast.makeText(getApplicationContext(),desc,Toast.LENGTH_LONG);

                if(Double.parseDouble(nearCase) >= 10){
                    message = "RED <br/><br/>";
                    txtMessage.setBackgroundColor(Color.RED);
                }else if(Double.parseDouble(nearCase)<10 && Double.parseDouble(nearCase)>0){
                    message = "YELLOW <br/><br/>";
                    txtMessage.setBackgroundColor(Color.YELLOW);
                }else if(Double.parseDouble(nearCase)==0){
                    message = "GREEN <br/><br/>";
                    txtMessage.setBackgroundColor(Color.GREEN);
                }
                txtMessage.setText(Html.fromHtml("<big><u><span style:'text-align:center;'>"+message+ "</span></u></big><big>" + nearCase + "</big>" + " cases nearest to your current location." + "<br/> <br/> <b>Nearest Location:</b> " + desc));
                txtMessage.setPadding(30,30,30,30);
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

    public void getAllLatLngs() {

        if (neaResult.size() > 0) {
            for (int i = 0; i < neaResult.size(); i++) {


                LocationData ld = neaResult.get(i);
                String latLng = ld.getLatLng();
                String[] latLngCut = latLng.split("\\|");
                for (int j = 0; j < latLngCut.length; j++) {
                    String[] res = latLngCut[j].split(",");
                    LocationLatLng llg = new LocationLatLng();
                    llg.setLocLat(Double.parseDouble(res[0]));
                    llg.setLocLng(Double.parseDouble(res[1]));
                    llg.setpolyNear(i);
                    latLngs.add(llg);
                }
            }
        } else {
            // Error handling
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private int getNearestArea(LatLng current, ArrayList<LocationLatLng> target) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        for(int i = 0; i < target.size();i++){
            double rlat = target.get(i).getLocLat() - current.latitude;
            double rlng = target.get(i).getLocLng() - current.longitude;
            double dLat = Math.toRadians(rlat);
            double dLng = Math.toRadians(rlng);

            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);

            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(current.latitude)) * Math.cos(Math.toRadians(target.get(i).getLocLat()));

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            double dist = earthRadius * c;
            target.get(i).setcomDistance(dist);
        }
        return smallestDist(latLngs);
    }

    private int smallestDist(ArrayList<LocationLatLng> list) {
        double minDistance = 0;
        int caseNO = 0;
        LatLng closestLatLng = null;
        for (int i = 0; i < list.size(); i++) {
            Double latit = list.get(i).getLocLat();
            Double longit = list.get(i).getLocLng();
            LatLng newLatLng = new LatLng(latit, longit);
            Double distance = list.get(i).getcomDistance();
            if (minDistance == 0)
            {
                minDistance = distance;
            }
            else if (distance < minDistance) {
                closestLatLng = newLatLng;
                minDistance = distance;
                caseNO = list.get(i).getpolyNear();
            }
        }
        return caseNO;
    }

}