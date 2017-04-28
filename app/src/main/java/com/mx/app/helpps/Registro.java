package com.mx.app.helpps;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Registro extends AppCompatActivity implements View.OnClickListener {
    private EditText email, pass, nombre, apellidos, telefono;
    private Button  mRegister;

    // Progress Dialog
    private ProgressDialog pDialog;
    boolean resp;
    String msgVal;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String REGISTER_URL = "http://psicologoonline.com.mx/myappconect/WS/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.contrasena);
        nombre = (EditText)findViewById(R.id.nombre);
        apellidos = (EditText)findViewById(R.id.apellidos);
        telefono = (EditText)findViewById(R.id.telefono);
        mRegister = (Button)findViewById(R.id.registrarme);
        mRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        validaCampos();
        if(!resp){
            Toast.makeText(this,msgVal,Toast.LENGTH_LONG).show();
            return;
        }else {
            new CreateUser().execute();
        }
    }

    private Boolean validaCampos(){
        resp = true;
        String Email = email.getText().toString();
        String password = pass.getText().toString();
        String Nombre = nombre.getText().toString();
        String Apellidos = apellidos.getText().toString();
        String Telefono = telefono.getText().toString();

        int count = Telefono.length();
        if (Email.isEmpty()) {
            resp = false;
            msgVal = "Correo electronico es requerido";
            return resp;
        }
        if (password.isEmpty()) {
            resp = false;
            msgVal = "Password es requerido";
            return resp;
        }
        if (Nombre.isEmpty()) {
            resp = false;
            msgVal = "Nombre es requerido";
            return resp;
        }
        if (Apellidos.isEmpty()) {
            resp = false;
            msgVal = "Apellidos es requerido";
            return resp;
        }
        if (Telefono.isEmpty()) {
            resp = false;
            msgVal = "Numero es requerido";
            return resp;
        }
        if(count != 10){
            resp = false;
            msgVal = "Telefono debe ser de 10 digitos";
            return resp;
        }


        return resp;
    }

    class CreateUser extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registro.this);
            pDialog.setMessage("Creando Usuario...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String Email = email.getText().toString();
            String password = pass.getText().toString();
            String Nombre = nombre.getText().toString();
            String Apellidos = apellidos.getText().toString();
            String Telefono = telefono.getText().toString();
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", Email));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("nombre", Nombre));
                params.add(new BasicNameValuePair("apellidos", Apellidos));
                params.add(new BasicNameValuePair("telefono", Telefono));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Usuario Creado!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Error en Registro!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Registro.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }


}
