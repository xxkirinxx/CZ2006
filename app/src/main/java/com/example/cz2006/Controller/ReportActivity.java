package com.example.cz2006.Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cz2006.Entity.Report;
import com.example.cz2006.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.cz2006.Constants.PERMISSIONS_REQUEST_ACCESS_CAMERA;
import static com.example.cz2006.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class ReportActivity extends AppCompatActivity implements LocationListener {
    private FusedLocationProviderClient fusedLocationClient;
    private Location loc;
    private LocationManager locationManager;
    private ImageView IVPreviewImage;
    private EditText editTextMulti;
    private Button btnSubmit;
    private TextView locationTextView;

    private boolean tappedBtnGetLoc = false;
    private Report report = new Report();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationTextView = findViewById(R.id.locationTextView);
        Button BtnGetLoc = findViewById(R.id.BtnGetLoc);

        BtnGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    checkPermission();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ReportActivity.this);
                } catch (Exception e) {
                    Log.d("error", e.getMessage());
                    return;
                }

                if (loc == null) {
                    Toast.makeText(ReportActivity.this,
                            "Please try again in a few seconds. If the problem still persists, please fix your GPS.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                report.setLocation(loc);
                tappedBtnGetLoc = true;
                locationTextView.setText(String.format("Latitude: %s, Longitude: %s", report.getLatitude(), report.getLongitude()));
            }
        });

        // Upload Image
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camPermission()) return;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PERMISSIONS_REQUEST_ACCESS_CAMERA);
            }
        });

        editTextMulti = findViewById(R.id.editTextTextMultiLine);

        // Submit Button Actions
        btnSubmit = findViewById(R.id.button4);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if report still has missing fields
                if (reportNotComplete()) return;

                // convert bitmap to string for logging
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                report.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);

                // Log all for now. To upload to database.
                Log.d("Photo", temp);
                Log.d("Report Details", report.getDetails());
                Log.d("Location", getAddress(report.getLatitude(), report.getLongitude()));

                Toast.makeText(ReportActivity.this,
                        "Submitting Report...",
                        Toast.LENGTH_SHORT).show();

                // reset values after submitting form
                editTextMulti.setText("");
                IVPreviewImage.setImageBitmap(null);
                locationTextView.setText("");
                tappedBtnGetLoc = false;
                loc = null;

                Toast.makeText(ReportActivity.this,
                        "Report Submitted!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        loc = location;
    }

    public boolean reportNotComplete() {
        if (report.getBitmap() == null) {
            Toast.makeText(ReportActivity.this,
                    "Please upload a photo.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        report.setDetails(editTextMulti.getText().toString());
        if (report.getDetails().trim().equals("")) {
            Toast.makeText(ReportActivity.this,
                    "Please provide details.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!tappedBtnGetLoc || report.getLocation() == null) {
            Toast.makeText(ReportActivity.this,
                    "Please provide a location.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    // Get Image and Set to ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                IVPreviewImage.setImageBitmap(bitmap);
                report.setBitmap(bitmap);
            } catch (NullPointerException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }

    // Check cam permission and get if not already granted
    public boolean camPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
            return false;
        }
        return true;
    }

    // perm for loc
    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    // convert latitude & longitude into actual address
    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }
}