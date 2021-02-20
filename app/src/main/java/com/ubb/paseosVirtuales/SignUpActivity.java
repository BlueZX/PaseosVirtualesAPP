package com.ubb.paseosVirtuales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.DataBase.ParametrosDTO;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.SnackbarHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton closeButton;
    private Button signInButton;
    private Button signUpButton;
    private Context c = this;
    private TextInputEditText emailET;
    private TextInputEditText nameET;
    private TextInputEditText passwordET;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();

    private VolleyHelper volleyHelper;
    private JSONObject postData = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        closeButton = findViewById(R.id.btn_close_signup);
        signUpButton = findViewById(R.id.btn_signup);
        signInButton = findViewById(R.id.btn_go_login_signup);
        emailET = findViewById(R.id.et_signup_email);
        passwordET = findViewById(R.id.et_signup_password);
        nameET = findViewById(R.id.et_signup_name);

        volleyHelper = new VolleyHelper(this, GlobalHelper.ENDPOINT);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if(passwordET.getText().toString().length() > 5) {
                        postData.put("email", emailET.getText().toString());
                        postData.put("password", passwordET.getText().toString());
                        postData.put("name", nameET.getText().toString());

                        volleyHelper.post("api/usuarios/new", postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("SignUp", response.toString());
                                try {
                                    if(response.get("ok").toString() == "true"){
                                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "Te has registrado exitosamente, Para ingresar confirma tu correo electronico", Color.parseColor("#22bb33"));
                                        Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                                    }
                                } catch (JSONException e) {
                                    messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error.networkResponse.statusCode == 400){
                                    messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                                }
                                else{
                                    messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No es posible conectar con el servidor", Color.RED);
                                }

                                error.printStackTrace();
                            }
                        });
                    }
                    else{
                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "La contraseña debe ser de un minimo de 6 caracteres", Color.RED);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InitActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
