package com.mx.app.helpps;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AgendarCita extends AppCompatActivity {

    SessionManager session;
    String idPsicologo;
    String idUser;
    String precio;
    String item;
    Calendar calendar = Calendar.getInstance();
    private Button btn_fecha;
    private Button btn_hora;
    private Button btn_agendar;
    private ProgressDialog pDialog;
    private Spinner tipoC;
    private TextView txt_costo;
    Boolean fechaMod = false;
    Boolean horaMod = false;

    JSONParser jsonParser = new JSONParser();

    private static final String REGISTERCITA_URL = "http://psicologoonline.com.mx/myappconect/WS/registro_Cita.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agendar_cita);

        btn_fecha = (Button) findViewById(R.id.butonFecha);
        btn_hora = (Button) findViewById(R.id.butonHora);
        btn_agendar = (Button) findViewById(R.id.butonAgendar);
        tipoC = (Spinner) findViewById(R.id.tipoC);
        txt_costo = (TextView) findViewById(R.id.Costo);

        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            idPsicologo = extras.get("id").toString();
        }
        Log.d("ID en Citas",idPsicologo);


        btn_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFecha();
            }
        });

        btn_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHora();
            }
        });

        btn_agendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateCita().execute();
            }
        });

        tipoC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              String option = parent.getSelectedItem().toString();
                item = option;
                if(option.equals("Presencial")){
                    precio = "450.00";
                    String text = "$ "+precio+" MXN";
                    txt_costo.setText(text);
                }else{
                    precio = "250.00";
                    String text = "$ "+precio+" MXN";
                    txt_costo.setText(text);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        updateTxtFecha();
        updateTxtHora();

        //Validar sesion
        session.checkLogin();
        //Extraer datos de session
        HashMap<String, String> user = session.getUserDetails();
        //Email
        idUser = user.get(SessionManager.KEY_ID);
        //Setear datos sesion
        Log.d("Usuario: ",idUser);

    }


    private void updateFecha(){
        new DatePickerDialog(this, d, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private  void updateHora(){
        new TimePickerDialog(this, t, calendar.get(Calendar.HOUR_OF_DAY),0, true).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (year < calendar.get(Calendar.YEAR))

                view.updateDate(calendar
                        .get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            if (monthOfYear < calendar.get(Calendar.MONTH) && year == calendar.get(Calendar.YEAR))
                view.updateDate(calendar
                        .get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            if (dayOfMonth < calendar.get(Calendar.DAY_OF_MONTH) && year == calendar.get(Calendar.YEAR) &&
                    monthOfYear == calendar.get(Calendar.MONTH))
                view.updateDate(calendar
                        .get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            fechaMod = true;
            updateTxtFecha();

        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            horaMod = true;
            updateTxtHora();
        }
    };

    private void updateTxtFecha(){
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH)+1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        if(fechaMod == true){
            btn_fecha.setText(dia + "/" + mes + "/" + anio);
        }

    }

    private void updateTxtHora(){
        int minuto = calendar.get(Calendar.MINUTE);
        int hora = calendar.get(Calendar.HOUR_OF_DAY);

        if(horaMod == true){
            btn_hora.setText(hora + ":" + minuto + ":" + "00");
        }

    }


    class CreateCita extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AgendarCita.this);
            pDialog.setMessage("Agendando Cíta...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String psicologo = idPsicologo;
            String usuario = idUser;
            String fecha = btn_fecha.getText().toString();
            String hora = btn_hora.getText().toString();
            String tipo = item;
            String costo = precio;
            String status = "1";
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("psicologo", psicologo));
                params.add(new BasicNameValuePair("usuario", usuario));
                params.add(new BasicNameValuePair("fecha", fecha));
                params.add(new BasicNameValuePair("hora", hora));
                params.add(new BasicNameValuePair("tipo", tipo));
                params.add(new BasicNameValuePair("costo", costo));
                params.add(new BasicNameValuePair("estatus", status));

                Log.d("URL: ", REGISTERCITA_URL);

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTERCITA_URL, "POST", params);

                // full json response
                Log.d("Json Crea Cita: ", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Cita Creada: ", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Error al crear Cita!", json.getString(TAG_MESSAGE));
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
                Toast.makeText(AgendarCita.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
