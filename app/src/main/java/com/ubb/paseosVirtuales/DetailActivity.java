package com.ubb.paseosVirtuales;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView nameTv;
    private TextView detailTv;
    private TextView extraInfoTv;
    private TextView dateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        String name = getIntent().getExtras().getString("name");
        String detail = getIntent().getExtras().getString("detail");
        String extraInfo = getIntent().getExtras().getString("extraInfo");
        String dateStr = getIntent().getExtras().getString("date");

        nameTv = (TextView) findViewById(R.id.detail_name_tv);
        detailTv = (TextView) findViewById(R.id.detail_detail_tv);
        extraInfoTv = (TextView) findViewById(R.id.detail_extra_tv);
        dateTv = (TextView) findViewById(R.id.detail_date_tv);

        String date = "Fecha desconocida";

        if(!dateStr.isEmpty()){
            date = dateStr.split("T",2)[0];
        }


        nameTv.setText(name);
        detailTv.setText(detail);
        extraInfoTv.setText(extraInfo);
        dateTv.setText(date);

    }

}
