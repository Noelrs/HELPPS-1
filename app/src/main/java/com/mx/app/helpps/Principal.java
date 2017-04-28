package com.mx.app.helpps;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class Principal extends AppCompatActivity {

    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        //Session
        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView usuario = (TextView)findViewById(R.id.usuario);

        Resources res = getResources();
        String texto = res.getString(R.string.principal_parrafo);

        CharSequence textoInterpretado = Html.fromHtml(texto);

        TextView textView = (TextView)findViewById(R.id.parrafoPrincipal);
        textView.setText(textoInterpretado);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

        //Validar sesion
        session.checkLogin();
        //Extraer datos de session
        HashMap<String, String> user = session.getUserDetails();
        //Email
        String email = user.get(SessionManager.KEY_EMAIL);
        //Mostrar Email
        usuario.setText(email);

    }

    /*Codigo para mostrar el menu...Incluir en todas las ventanas*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch (id){
            case R.id.settings:
                //Intent cerrarSesion = new Intent(this, Login.class);
                //startActivity(cerrarSesion);
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
