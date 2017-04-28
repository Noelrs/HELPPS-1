package com.mx.app.helpps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity implements View.OnClickListener{
    Button registro;
    Button sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        registro = (Button) findViewById(R.id.registro);
        registro.setOnClickListener(this);

        sesion = (Button) findViewById(R.id.sesion);
        sesion.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.registro:
                Intent intent = new Intent(this, Registro.class);
                startActivity(intent);
                break;
            case R.id.sesion:
                Intent intent1 = new Intent(this, Login.class);
                startActivity(intent1);
                break;
            default:
                break;

        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
