package com.example.cz2006.Controller;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cz2006.R;

public class MozzieActivity extends AppCompatActivity {

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    private TextView completionText;
    int x = 0;
    int cb1, cb2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView mozziePhoto = (ImageView) findViewById(R.id.mozziePhoto); //get id of mozzie image
        mozziePhoto.setImageResource(R.drawable.mozzie);

        /* checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);

        completionText = findViewById(R.id.completionText);
        if (checkBox1.isChecked() == true && checkBox2.isChecked() == true
                && checkBox3.isChecked() == true && checkBox4.isChecked() == true &&
                checkBox5.isChecked() == true) {
            x = 5;
        }

        if(checkBox1.isChecked() == true && checkBox2.isChecked() == true
                && checkBox3.isChecked() == true && checkBox4.isChecked() == true &&
                checkBox5.isChecked() == true) {
            completionText.setVisibility(View.VISIBLE);
        }
        else {
            completionText.setVisibility(View.INVISIBLE);
        }

        if (checkBox1.isChecked() == true) {
            int cb1 = 1;
        }
        if (checkBox2.isChecked() == true) {
            int cb2 = 1;
        }
        int allCompleted = cb1 + cb2;
        if (allCompleted == 2){
            completionText.setVisibility(View.VISIBLE);
        }
        else {
            completionText.setVisibility(View.INVISIBLE);
        }
        */






    }
}