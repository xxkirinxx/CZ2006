package com.example.cz2006.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cz2006.Entity.Report;
import com.example.cz2006.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.cz2006.Constants.PERMISSIONS_REQUEST_ACCESS_CAMERA;
import static com.example.cz2006.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class ReportActivity extends AppCompatActivity implements LocationListener {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private ImageView IVPreviewImage;
    private EditText editTextMulti;
    private Button btnSubmit;
    private TextView locationTextView;
    private TextView textViewWordCount;
    private String address;
    private boolean tappedBtnGetLoc = false;
    private boolean uploadFailed = false;
    private Report report = new Report();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        locationTextView = findViewById(R.id.locationTextView);
        Button BtnGetLoc = findViewById(R.id.BtnGetLoc);

        // Get Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (!isLocationEnabled(ReportActivity.this)) {
            gpsSettingsPrompt();
//            return;
        }

        BtnGetLoc.setOnClickListener(new View.OnClickListener() {
             @SuppressLint("MissingPermission")
             @Override
             public void onClick(View v) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     if (ActivityCompat.checkSelfPermission(ReportActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                         ActivityCompat.requestPermissions(ReportActivity.this,
                                 new String[]{ACCESS_FINE_LOCATION},
                                 PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                     } else {
                         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ReportActivity.this);
                     }
                 }
                 fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                     @Override
                     public void onComplete(@NonNull Task<Location> task) {
                         if (task.isSuccessful()) {
                             Location location = task.getResult();
                             if (location != null) {
                                 report.setLocation(location);
                                 tappedBtnGetLoc = true;
                                 getAddress(report.getLatitude(), report.getLongitude());
                                 if (address == null) {
                                     Toast.makeText(ReportActivity.this,
                                            "Geocoding not working. Defaulting to LatLng. " +
                                                    "Try again later. If the problem persists, " +
                                                    "please restart your phone.",
                                            Toast.LENGTH_LONG).show();
                                     locationTextView.setText("Current Location:\n"
                                             + report.getLatitude() + ", " + report.getLongitude());
                                 } else {
                                     locationTextView.setText("Current Location:\n" + address);
                                 }
                                                 locationTextView.setText("Current Location:\n" + address);
                             } else {
                                 Toast.makeText(ReportActivity.this,
                                         "Location is null. Permissions not granted or GPS " +
                                                 "not on. Try again later.",
                                         Toast.LENGTH_SHORT).show();
                                 return;
                             }
                         }
                     }
                 });
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

        // Details Section
        editTextMulti = findViewById(R.id.editTextTextMultiLine);
        textViewWordCount = findViewById(R.id.textViewWordCount);
        editTextMulti.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // this will show characters remaining
                textViewWordCount.setText(150 - s.toString().length() + "/150 characters remaining");
            }
        });

        // Firebase setup
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // check if it is the right storage bucket
        FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        Log.i("er", "Bucket = " + opts.getStorageBucket());
        String uniqueId = UUID.randomUUID().toString();  // Generate UUID
        StorageReference storageRef = storage.getReference();

        // Get user details
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName();
        Log.d("display name", name);
        Log.d("uid", uid);
        StorageReference imagesRef = storageRef.child("reports/"
                                                    + "user_" + name + "/"
                                                    + "reportId_" + uniqueId + "/"
                                                    + "/image.jpeg");

        // Submit Button Actions
        btnSubmit = findViewById(R.id.button4);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if report still has missing fields
                if (reportNotComplete()) return;

                // resize and compress bitmap to keep it as small as possible
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                getResizedBitmap(report.getBitmap(), 600, 400).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);

                Toast.makeText(ReportActivity.this,
                        "Submitting Report...",
                        Toast.LENGTH_SHORT).show();

                // Upload Image to Firebase
                UploadTask uploadTask = imagesRef.putBytes(b);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("Failed", "Image failed to upload");
                        uploadFailed = true;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Succeed", "Image uploaded");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d("Progress", "Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Paused", "Upload is paused");
                    }
                });;

                // Upload Details & Location to Firebase
                StorageReference detailRef = storageRef.child("reports/"
                                                            + "user_" + name + "/"
                                                            + "reportId_" + uniqueId + "/"
                                                            + "/details.txt");
                String details = null;

                if (address == null) {
                    details = "Location: "
                            + "\n"
                            + "Latitude: " + report.getLatitude() + ", Longitude: " + report.getLongitude()
                            + "\n\n"
                            + "Details:\n"
                            + report.getDetails();
                } else {
                    details = "Location: " + address
                            + "\n"
                            + "Latitude: " + report.getLatitude() + ", Longitude: " + report.getLongitude()
                            + "\n\n"
                            + "Details:\n"
                            + report.getDetails();
                }

                detailRef.putBytes(details.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Succeed", "Uploaded details successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failed", "Details failed to upload");
                        uploadFailed = true;
                    }
                });

                // if upload failed, exit
                if (uploadFailed) {
                    Toast.makeText(ReportActivity.this,
                            "Upload has failed. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                    uploadFailed = false;  // reset bool
                    return;
                }

                // reset values after submitting form
                editTextMulti.setText("");
                IVPreviewImage.setImageBitmap(null);
                locationTextView.setText("");
                tappedBtnGetLoc = false;
                report.setLocation(null);

                Toast.makeText(ReportActivity.this,
                        "Report Submitted!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        report.setLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("provider", "disabled");
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

    public void gpsSettingsPrompt() {
        final AlertDialog.Builder builder =  new AlertDialog.Builder(ReportActivity.this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "GPS is disabled. Do you want to open your GPS settings?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                ReportActivity.this.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
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

    // Check cam permission and get if not already granted
    public boolean camPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
            return false;
        }
        return true;
    }

    // convert latitude & longitude into actual address
    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        String address = null;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            Log.e("address", address);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        
        this.address = address;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}