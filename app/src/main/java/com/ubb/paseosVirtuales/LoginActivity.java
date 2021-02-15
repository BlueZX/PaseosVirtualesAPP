package com.ubb.paseosVirtuales;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ImageButton closeButton;
    private Button signInButton;
    private Button signUpButton;
    private TextInputEditText emailET;
    private TextInputEditText passwordET;

    private VolleyHelper volleyHelper;
    private  JSONObject postData = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        closeButton = findViewById(R.id.btn_close_login);
        signInButton = findViewById(R.id.btn_login);
        emailET = findViewById(R.id.et_login_email);
        passwordET = findViewById(R.id.et_login_password);
        signUpButton = findViewById(R.id.btn_go_signup);

        emailET.setText("ej@gmail.com");
        passwordET.setText("hola1234");

        try {
            postData.put("name", emailET.getText().toString());
            postData.put("job", passwordET.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        volleyHelper = new VolleyHelper(this, GlobalHelper.ENDPOINT);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                volleyHelper.post("api/users", postData, new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Login", response.toString());

                        Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InitActivity.class);
                startActivity(intent);
            }
        });
    }
}
