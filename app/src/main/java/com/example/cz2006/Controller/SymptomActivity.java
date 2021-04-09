package com.example.cz2006.Controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cz2006.R;

import java.util.ArrayList;

public class SymptomActivity extends AppCompatActivity {
    private CheckBox mFeverCheck, mSoreCheck, mNauseaCheck, mRashesCheck, mHeadacheCheck, mEyeCheck, mSwollenCheck, mJointCheck;
    private Button mResultButton;
    private TextView mResultText;
    private TextView alertTextView;
    private ArrayList<String> mResult;
    private ArrayList<String> mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        mFeverCheck = findViewById(R.id.check_fever);
        mSoreCheck = findViewById(R.id.check_sore);
        mNauseaCheck = findViewById(R.id.check_nausea);
        mRashesCheck = findViewById(R.id.check_rashes);
        mHeadacheCheck = findViewById(R.id.check_headache);
        mEyeCheck = findViewById(R.id.check_eye);
        mSwollenCheck = findViewById(R.id.check_swollen);
        mJointCheck = findViewById(R.id.check_joint);

        mResultButton = findViewById(R.id.write_result);
        mResultText = findViewById(R.id.result);

        mResult = new ArrayList<>();
        mPoints = new ArrayList<>();
        mResultText.setEnabled(false);

        mFeverCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mFeverCheck.isChecked()) {
                    mResult.add("High fever");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("High fever");
                    mPoints.remove("x");
                }
            }
        });

        mSoreCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mSoreCheck.isChecked()) {
                    mResult.add("Sore bones");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Sore bones");
                    mPoints.remove("x");
                }
            }
        });

        mNauseaCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mNauseaCheck.isChecked()) {
                    mResult.add("Nausea and vomitting");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Nausea and vomitting");
                    mPoints.remove("x");
                }
            }
        });

        mRashesCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mRashesCheck.isChecked()) {
                    mResult.add("Rashes on body");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Rashes on body");
                    mPoints.remove("x");
                }
            }
        });

        mHeadacheCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mHeadacheCheck.isChecked()) {
                    mResult.add("Headache for more than 3 days");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Headache for more than 3 days");
                    mPoints.remove("x");
                }
            }
        });

        mEyeCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mEyeCheck.isChecked()) {
                    mResult.add("Pain behind the eyes");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Pain behind the eyes");
                    mPoints.remove("x");
                }
            }
        });

        mSwollenCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mSwollenCheck.isChecked()) {
                    mResult.add("Swollen glands");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Swollen glands");
                    mPoints.remove("x");
                }
            }
        });

        mJointCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                if(mJointCheck.isChecked()) {
                    mResult.add("Joint pain");
                    mPoints.add("x");
                }
                else {
                    mResult.remove("Joint pain");
                    mPoints.remove("x");
                }
            }
        });

        mResultButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Your Symptoms: \n");
                for (String s : mResult)
                    stringBuilder.append(s).append("\n");
                StringBuilder points = new StringBuilder();
                for (String x : mPoints)
                    points.append(x);
                if (points.length() > 2)
                    stringBuilder.append("\n").append("You are advised to see a doctor.");
                else
                    stringBuilder.append("\n").append("Do continue to monitor your health.");

                AlertDialog.Builder builder = new AlertDialog.Builder(SymptomActivity.this);
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

                mResultText.setText(stringBuilder.toString());
                mResultText.setEnabled(false);
            }

        });
    }
}