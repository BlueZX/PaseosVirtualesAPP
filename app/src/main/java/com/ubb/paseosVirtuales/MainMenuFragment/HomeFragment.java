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
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Response;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.LoginActivity;
import com.ubb.paseosVirtuales.ProfileActivity;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.helper.DialogHelper;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private ImageButton btnLogout;
    private ImageButton btnProfile;
    private DialogHelper dialogHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = (ImageButton) view.findViewById(R.id.logout_button);
        btnProfile = (ImageButton) view.findViewById(R.id.profile_button);

        dialogHelper = new DialogHelper(getContext());

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