package com.mx.app.helpps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Login extends BaseActivity implements SinchService.StartFailedListener{

    private EditText user, pass;
    private Button mSubmit;
    String userLog;

    private ProgressDialog pDialog;

    SessionManager session;

    // Clase JSONParser
    JSONParser jsonParser = new JSONParser();


    private static final String LOGIN_URL = "http://psicologoonline.com.mx/myappconect/WS/login_app_cliente.php";

    // La respuesta del JSON es
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Session Manager
        session = new SessionManager(getApplicationContext());

        // setup input fields
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.contrasena);
        user.setFocusable(true);

        // setup buttons
        mSubmit = (Button) findViewById(R.id.login);
        mSubmit.setEnabled(false);
        mSubmit.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
                new AttemptLogin().execute();
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        mSubmit.setEnabled(true);
       getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openMainActivity();
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Iniciando Sesión...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String id;
            String nombre;
            String apellidos;
            String telefono;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Correcto!", json.toString());
                    id = json.getString("id");
                    nombre = json.getString("nombre");
                    apellidos = json.getString("apellidos");
                    telefono = json.getString("telefono");

                    //Crea Session
                    session.createLoginSession(id,nombre,apellidos,username,telefono);
                    //Crea Instancia Sinch
                        userLog = telefono;
                        openMainActivity();
                        finish();
                        return json.getString(TAG_MESSAGE);



                } else {
                    Log.d("Login Error!", json.getString(TAG_MESSAGE));
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
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(userLog);
                Log.d("Inst. Sinch Creada...",userLog);
            }
            if (file_url != null) {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openMainActivity() {
        Intent i = new Intent(Login.this, MainActivity.class);
        startActivity(i);
    }



    @Override
    public Intent getSupportParentActivityIntent() {
        return null;
    }

    @Nullable
    @Override
    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return null;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
