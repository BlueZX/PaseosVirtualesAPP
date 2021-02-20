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
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.DataBase.ParametrosDTO;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.SnackbarHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();

    private ImageButton closeButton;
    private Button signInButton;
    private Button signUpButton;
    private TextInputEditText emailET;
    private TextInputEditText passwordET;
    private Context c = this;

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

        emailET.setText("castroduranivan@gmail.com");
        passwordET.setText("colok55");

        volleyHelper = new VolleyHelper(this, GlobalHelper.ENDPOINT);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    postData.put("email", emailET.getText().toString());
                    postData.put("password", passwordET.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                volleyHelper.post("api/auth", postData, new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Login", response.toString());
                        try {
                            if(response.get("ok").toString() == "true"){
                                Modelo obj = new Modelo();
                                ParametrosDTO param = new ParametrosDTO();

                                param.setId("1");
                                param.setNombre("LOGGED_IN");
                                param.setValue("true");

                                obj.insertParametros( LoginActivity.this, param);

                                param.setId("4");
                                param.setNombre("uid");
                                param.setValue(response.get("uid").toString());

                                obj.insertParametros( LoginActivity.this, param);

                                param.setId("2");
                                param.setNombre("TOKEN");
                                param.setValue(response.get("token").toString());

                                int resInsert = obj.insertParametros( LoginActivity.this, param);

                                if(resInsert == 1){
                                    Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado, porfavor intentalo de nuevo", Color.RED);
                                }
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
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "El email o contraseña introducidos no son correctos o no se ha validado el correo electronico", Color.RED);
                        }
                        else{
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No es posible conectar con el servidor", Color.RED);
                        }

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
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }
}
