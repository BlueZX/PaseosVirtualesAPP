package com.ubb.paseosVirtuales;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ubb.paseosVirtuales.DataBase.Modelo;

public class InitActivity extends AppCompatActivity {

    private Button signup;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Modelo obj = new Modelo();

        Log.d("Logeado",obj.getParametro(this,"LOGGED_IN"));
        if(obj.getParametro(this,"LOGGED_IN").equals("true")){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        }
        else{
            setContentView(R.layout.activity_init);

            startButton = findViewById(R.id.btn_init);
            signup = findViewById(R.id.btn_init_signup);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}