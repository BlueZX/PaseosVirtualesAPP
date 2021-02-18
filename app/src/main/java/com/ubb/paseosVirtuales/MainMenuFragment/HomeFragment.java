package com.ubb.paseosVirtuales.MainMenuFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.navigation.fragment.NavHostFragment;

import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.LoginActivity;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.helper.DialogHelper;

public class HomeFragment extends Fragment {

    private Button btnEstadisticas;
    private ImageButton btnLogout;

    private DialogHelper dialogHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = (ImageButton) view.findViewById(R.id.logout_button);

        dialogHelper = new DialogHelper(getContext());

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

        btnEstadisticas = (Button)  view.findViewById(R.id.btn_estadisticas);

        btnEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

}