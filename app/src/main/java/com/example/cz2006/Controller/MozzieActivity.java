package com.example.cz2006.Controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cz2006.R;

import java.util.ArrayList;

public class MozzieActivity extends AppCompatActivity {

    private CheckBox B1Checked, B2Checked,B3Checked,B4Checked,B5Checked;
    private Button resultButton;
    private ArrayList<String> points;
    private TextView alertTextView;
    private TextView completionText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mozzie);

        ImageView mozziePhoto = (ImageView) findViewById(R.id.mozziePhoto); //get id of mozzie image
        mozziePhoto.setImageResource(R.drawable.fivesteps);

        B1Checked = findViewById(R.id.checkBox1);
        B2Checked = findViewById(R.id.checkBox2);
        B3Checked = findViewById(R.id.checkBox3);
        B4Checked = findViewById(R.id.checkBox4);
        B5Checked = findViewById(R.id.checkBox5);
        resultButton = findViewById(R.id.completionButton);
        completionText = findViewById(R.id.completionText);
        points = new ArrayList<>();
        completionText.setEnabled(false);

        B1Checked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(B1Checked.isChecked()) {

                    points.add("x");
                }
                else {

                    points.remove("x");
                }
            }
        });
        B2Checked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(B2Checked.isChecked()) {

                    points.add("x");
                }
                else {

                    points.remove("x");
                }
            }
        });
        B3Checked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(B3Checked.isChecked()) {

                    points.add("x");
                }
                else {

                    points.remove("x");
                }
            }
        });
        B4Checked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(B4Checked.isChecked()) {

                    points.add("x");
                }
                else {

                    points.remove("x");
                }
            }
        });
        B5Checked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(B5Checked.isChecked()) {

                    points.add("x");
                }
                else {

                    points.remove("x");
                }
            }
        });

        resultButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();

                StringBuilder point = new StringBuilder();
                for (String x : points)
                    point.append(x);
                if (point.length() > 4)
                    stringBuilder.append("\n").append("You have completed the mozzie wipeout!");
                else
                    stringBuilder.append("\n").append("You have not completed the mozzie wipeout :(");

                AlertDialog.Builder builder = new AlertDialog.Builder(MozzieActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Nozzie Mozzie");
                builder.setMessage(stringBuilder.toString());



                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                completionText.setText(stringBuilder.toString());
                completionText.setEnabled(false);
            }
        });





    }
}