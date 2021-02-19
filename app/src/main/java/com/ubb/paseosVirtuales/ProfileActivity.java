package com.ubb.paseosVirtuales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.SnackbarHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity {

    private Dialog loading;
    private VolleyHelper volleyHelper;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private TextView tvLoading;
    private TextInputEditText nameET;
    private TextInputEditText emailET;
    private TextInputEditText passwordET;
    private TextInputEditText comfirmPasswordET;
    private ImageView imagen;
    private Button save;
    private  JSONObject postData = new JSONObject();
    private Context c = this;
    private String token;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        volleyHelper = new VolleyHelper(this, GlobalHelper.ENDPOINT);
        loading = new Dialog(this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        tvLoading = (TextView) loading.findViewById(R.id.tv_loading);
        tvLoading.setText("Cargando información de usuario");
        nameET = (TextInputEditText) findViewById(R.id.et_profile_name);
        emailET = (TextInputEditText) findViewById(R.id.et_profile_email);
        passwordET = (TextInputEditText) findViewById(R.id.et_profile_password);
        comfirmPasswordET = (TextInputEditText) findViewById(R.id.et_profile_confirm_password);
        save = (Button) findViewById(R.id.btn_profile_save);
        imagen = (ImageView) findViewById(R.id.img_profile);

        loading.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // setting the action on clicking on the back button ..
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Modelo obj = new Modelo();
        uid = obj.getParametro(this,"uid");
        token = obj.getParametro(this,"TOKEN");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLoading.setText("Guardando actualización de datos");

                try {
                    postData.put("email", emailET.getText().toString());
                    postData.put("password", passwordET.getText().toString());
                    postData.put("name", nameET.getText().toString());

                    if((passwordET.getText().toString().equals(comfirmPasswordET.getText().toString()))){
                        if(passwordET.getText().toString().length() > 5){
                            loading.show();
                            volleyHelper.put("api/usuarios/" + uid, postData, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("profile_save", response.toString());
                                    try {
                                        loading.dismiss();

                                        if (response.get("ok").toString() == "true") {
                                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "Se han actualizado los datos correctamente", Color.parseColor("#22bb33"));
                                        }
                                        else{
                                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                                        }

                                    } catch (JSONException e) {
                                        loading.dismiss();
                                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loading.dismiss();
                                    if(error.networkResponse.statusCode == 400){
                                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "Los datos no pueden ser los mismos que los anteriores", Color.RED);
                                    }
                                    else{
                                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No es posible conectar con el servidor", Color.RED);
                                    }

                                }
                            }, token);
                        }
                        else{
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "La contraseña tiene que tener un minimo de 6 caracteres", Color.RED);
                        }
                    }
                    else {
                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "Las contraseñas deben ser iguales", Color.RED);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        volleyHelper.get("api/usuarios/" + uid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("profile", response.toString());
                try {
                    if(response.get("ok").toString() == "true"){
                        loading.dismiss();
                        JSONObject user = response.getJSONObject("usuario");
                        nameET.setText(user.getString("name"));
                        emailET.setText(user.getString("email"));

                    }
                    else{
                        messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                        loading.dismiss();
                    }
                } catch (JSONException e) {
                    messageSnackbarHelper.showMessageWithDismiss((Activity) c, "A ocurrido un error inesperado", Color.RED);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                messageSnackbarHelper.showMessageWithDismiss((Activity) ProfileActivity.this, "No es posible conectar con el servidor", Color.RED);

            }
        }, token);
    }

    public void setImg(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicación"), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri path = data.getData();
            imagen.setImageURI(path);

            String filePath = getPath(path);
            Log.d("fileData",filePath);
            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

            if ( file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                volleyHelper.post("api/uploads/usuario/" + uid, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("profile_save", response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if(!res.getBoolean("ok")){
                                messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No se pudo subir la img al servidor, por favor intentelo de nuevo", Color.RED);
                            }
                            else{
                                postData.put("img", res.getString("img"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No se pudo subir la img al servidor, por favor intentelo de nuevo", Color.RED);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error.networkResponse.statusCode == 400){
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No se pudo subir la img al servidor, por favor intentelo de nuevo", Color.RED);
                        }
                        else{
                            messageSnackbarHelper.showMessageWithDismiss((Activity) c, "No se pudo subir la img al servidor, por favor intentelo de nuevo", Color.RED);
                        }

                    }
                }, token, filePath);
            } else {
                imagen.setImageDrawable(getDrawable(R.drawable.no_disponible));
                messageSnackbarHelper.showMessageWithDismiss((Activity) c, "Formato de la imagen no valido", Color.RED);
            }


        }
    }

    public String getPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(),  uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
