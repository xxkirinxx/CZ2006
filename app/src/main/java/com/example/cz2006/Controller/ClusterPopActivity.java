package com.example.cz2006.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cz2006.R;

public class ClusterPopActivity extends Activity {

    Button btn_close;
    TextView txtDesc;
    TextView txtCS;
    RelativeLayout clusPop;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clusterpopactivity);
        Intent intent = getIntent();
        String newDesc = intent.getStringExtra("DengueClusterDesc");
        String newCaseSize = intent.getStringExtra("DengueClusterCS");
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtCS = (TextView) findViewById(R.id.txtCS);
        clusPop = (RelativeLayout) findViewById(R.id.clusterPop);
        btn_close = (Button) findViewById(R.id.btn_close);
        int caseSize = Integer.parseInt(newCaseSize);

        if(caseSize>= 10){
            clusPop.setBackgroundResource(R.drawable.clusterpop_bg);
        }else if(caseSize<10 && caseSize>0){
            clusPop.setBackgroundResource(R.drawable.clusterpop_bg_yellow);
        }else if(caseSize==0){
            clusPop.setBackgroundResource(R.drawable.clusterpop_bg_green);
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtDesc.setText(Html.fromHtml("<b>Locality</b>: " + newDesc));
        txtCS.setText(Html.fromHtml("<b>Case Size</b>: " + newCaseSize));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.88),(int)(height*.29));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);



    }
}
