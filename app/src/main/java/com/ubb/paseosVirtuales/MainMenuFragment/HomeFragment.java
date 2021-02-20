package com.ubb.paseosVirtuales.MainMenuFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.squareup.picasso.Picasso;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.LoginActivity;
import com.ubb.paseosVirtuales.ProfileActivity;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.helper.DialogHelper;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.SnackbarHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private ImageButton btnLogout;
    private ImageButton btnProfile;
    private DialogHelper dialogHelper;
    private VolleyHelper volleyHelper;
    private ImageView imagen;
    private TextView nombre;
    private TextView email;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private String token;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        imagen = (ImageView) root.findViewById(R.id.img_home_view);

        nombre = (TextView) root.findViewById(R.id.name_home_tv);
        email = (TextView) root.findViewById(R.id.email_home_tv);

        dialogHelper = new DialogHelper(getContext());

        volleyHelper = new VolleyHelper(getContext(), GlobalHelper.ENDPOINT);

        Modelo obj = new Modelo();
        uid = obj.getParametro(getContext(),"uid");
        token = obj.getParametro(getContext(),"TOKEN");

        getUserData();

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserData();
    }

    private void getUserData(){
        volleyHelper.get("api/usuarios/" + uid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("profile", response.toString());
                try {
                    if(response.get("ok").toString() == "true"){
                        JSONObject user = response.getJSONObject("usuario");

                        nombre.setText(user.getString("name"));
                        email.setText(user.getString("email"));

                        String urlImage = response.getString("img");
                        if(!urlImage.isEmpty()){
                            Picasso.with(getContext()).load(GlobalHelper.DOWNLOAD + "/uploads/usuario/" + urlImage).into(imagen);
                        }
                    }
                    else{
                        messageSnackbarHelper.showMessageWithDismiss(getActivity(), "A ocurrido un error inesperado", Color.RED);
                    }
                } catch (JSONException e) {
                    messageSnackbarHelper.showMessageWithDismiss(getActivity(), "A ocurrido un error inesperado", Color.RED);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageSnackbarHelper.showMessageWithDismiss(getActivity(), "No es posible conectar con el servidor", Color.RED);

            }
        }, token);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = (ImageButton) view.findViewById(R.id.logout_button);
        btnProfile = (ImageButton) view.findViewById(R.id.profile_button);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.show("Cerrar sesión", "¿Estás seguro que quieres cerrar sesión?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("cerra","sesion");
                        Modelo obj = new Modelo();
                        if(obj.dropTableParametros(getContext())){
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            }
        });
    }

}