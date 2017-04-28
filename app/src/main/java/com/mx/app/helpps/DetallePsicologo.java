package com.mx.app.helpps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.snowdream.android.widget.SmartImageView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;

import org.json.JSONException;

import java.math.BigDecimal;

public class DetallePsicologo extends BaseActivity implements View.OnClickListener {

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "Ab6770ILJ9RJoVHn2pmilljKZgHTF4onFt-puNGsVtN5XiTMXEGg1Ghm-G6tvboqcL4qgs4-O9bsF4bB";
    private static final int REQUEST_CODE_PAYMENT = 1;

    String fileImage = "http://psicologoonline.com.mx/myappconect/FOTOS/";
    SessionManager session;
    Button tonline;
    Button cita;
    Button ubicacion;
    RatingBar ratingBar;
    TextView nombre,especialidad,cedula,descripcion,consultorio,estatus;
    SmartImageView foto;
    String idPsicologo;
    String TelPsicologo;
    String Estatusval;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)

            // configuracion minima del ente
            .merchantName("Mi tienda")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.mi_tienda.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.mi_tienda.com/legal"));

    PayPalPayment thingToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_psicologo2);

        //Inicia Clase PayPal
        Intent intent1 = new Intent(this, PayPalService.class);
        intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nombre = (TextView)findViewById(R.id.nombreDetalle);
        especialidad = (TextView)findViewById(R.id.Especialidad);
        cedula = (TextView)findViewById(R.id.Cedula);
        foto = (SmartImageView)findViewById(R.id.fotoDetalle);
        descripcion = (TextView)findViewById(R.id.Presentacion);
        consultorio = (TextView) findViewById(R.id.consultorio);
        estatus = (TextView) findViewById(R.id.Estatus);

        session = new SessionManager(getApplicationContext());


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            idPsicologo = extras.get("id").toString();
            String nom = (String)extras.get("nombre");
            String esp = (String)extras.get("especialidad");
            String ced = (String)extras.get("cedula");
            String des = (String)extras.get("descripcion");
            String img = (String)extras.get("img");
            String est = (String)extras.get("estatus");
            TelPsicologo = (String)extras.get("telefono");
            Estatusval = est;


            String UrlFinal = fileImage+img;
            Rect rect = new Rect(foto.getLeft(), foto.getTop(), foto.getRight(), foto.getBottom());


            foto.setImageUrl(UrlFinal, rect);
            nombre.setText(nom);
            especialidad.setText(esp);
            cedula.setText(ced);
            descripcion.setText(des);
            consultorio.setText("Av. Lomas Verdes 122, Naucalpan,Edo. Mex. del culguacan");
            estatus.setText(est);
        }
        //Log.d("IdPsicologo OnCreate: ",idPsicologo);

        tonline = (Button)findViewById(R.id.Tonline);
        tonline.setOnClickListener(this);

        if(Estatusval.equals("DESACTIVO") || Estatusval.equals("OCUPADO")){
            tonline.setEnabled(false);
        }else{
            tonline.setEnabled(true);
        }


        cita = (Button)findViewById(R.id.AgendarCita);
        cita.setOnClickListener(this);

        ubicacion = (Button)findViewById(R.id.UbicarConsultorio);
        ubicacion.setOnClickListener(this);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Tonline:
                //Solicitar el pago del servicio
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Debe realizar el pago del servicio para continuar")
                        .setTitle("Advertencia")
                        .setCancelable(false)
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Continuar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        thingToBuy = new PayPalPayment(new BigDecimal("300.00"), "MXN",
                                                "TerapiaOnline", PayPalPayment.PAYMENT_INTENT_SALE);
                                        Intent intent = new Intent(DetallePsicologo.this,
                                                PaymentActivity.class);

                                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                                    }
                                });
                AlertDialog alert1 = builder1.create();
                alert1.show();

                break;
            case R.id.AgendarCita:
                Intent in = new Intent(getApplicationContext(), AgendarCita.class);
                in.putExtra("id",idPsicologo);
                startActivity(in);
                break;
            case R.id.UbicarConsultorio:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //Obteniendo el resultado del pago

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data
                    .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {

                    // informacion extra del pedido
                    System.out.println(confirm.toJSONObject().toString(4));
                    System.out.println(confirm.getPayment().toJSONObject()
                            .toString(4));

                    Toast.makeText(getApplicationContext(), "Pago realizado correctamente",
                            Toast.LENGTH_LONG).show();

                    //PopUp para elegir tipo de terapia
                    final CharSequence[] items = {"Video llamada","Chat Escrito","Llamada de Voz"};
                    Log.d("0", "onClick: En switch");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Como desea comunicarse?");
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Haz elegido la opcion: " + items[item] , Toast.LENGTH_SHORT);
                            toast.show();
                            dialog.cancel();
                            String selectOption = items[item].toString();
                            OnStartServiceSinch(selectOption);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            System.out.println("El usuario canceló el pago");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /////////////////////////////////

    private void OnStartServiceSinch(String option){

        if(option == "Llamada de Voz") {

            try {

                Call call = getSinchServiceInterface().callUser(idPsicologo);
                if (call == null) {
                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(getApplicationContext(), "El servicio no se inicia. Intente detener el servicio y comenzar de nuevo antes de "
                            + "realizar una llamada.", Toast.LENGTH_LONG).show();
                    return;
                }
                String callId = call.getCallId();
                Intent callScreen = new Intent(getApplicationContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        }

        if(option == "Video llamada") {

            try {

                Call call = getSinchServiceInterface().callUserVideo(idPsicologo);
                if (call == null) {
                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(getApplicationContext(), "El servicio no se inicia. Intente detener el servicio y comenzar de nuevo antes de "
                            + "realizar una llamada.", Toast.LENGTH_LONG).show();
                    return;
                }
                String callId = call.getCallId();
                Intent callScreen = new Intent(getApplicationContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        }

        if(option == "Chat Escrito") {

            openMessagingActivity();
        }

    }

    private void openMessagingActivity() {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.putExtra("recipient",idPsicologo);
        startActivity(messagingActivity);
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
