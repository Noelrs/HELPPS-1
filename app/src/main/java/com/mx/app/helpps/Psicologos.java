package com.mx.app.helpps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by noelrs on 9/11/2016.
 */
public class Psicologos extends AppCompatActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParserOld jParser = new JSONParserOld();

    ArrayList<HashMap<String, String>> psicologosList;

    String fileImage = "http://psicologoonline.com.mx/myappconect/FOTOS/";

    SessionManager session;

    // url to get all products list
    private static String url_all_psicologos = "http://psicologoonline.com.mx/myappconect/QUERYS/get_all_psicologos2.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Ps_Psicologos";
    private static final String TAG_ID = "ID";
    private static final String TAG_NOMBRE = "NOMBRE";
    private static final String TAG_ESPECIALIDAD = "ESPECIALIDAD";
    private static final String TAG_CEDULA = "CEDULA";
    private static final String TAG_IMAGEN = "IMGSMALL";
    private static final String TAG_PAIS = "PAIS";
    private static final String TAG_CIUDAD = "CIUDAD";
    private static final String TAG_ESTATUS = "ESTATUS";
    private static final String TAG_DESCRIPCION = "DESCRIPCION";

    // products JSONArray
    JSONArray products = null;

    ArrayList ID = new ArrayList();
    ArrayList Nombre = new ArrayList();
    ArrayList Especialidad = new ArrayList();
    ArrayList Cedula = new ArrayList();
    ArrayList Ciudad = new ArrayList();
    ArrayList Pais = new ArrayList();
    ArrayList imagen = new ArrayList();
    ArrayList Estatus = new ArrayList();
    ArrayList Descripcion = new ArrayList();

    ListView lista;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.psicologos);
        session = new SessionManager(getApplicationContext());

        lista = (ListView) findViewById(R.id.listAllPsicologos);
        Cargardatos();


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), String.valueOf(ID.get(position).toString()), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DetallePsicologo.class);
                intent.putExtra("id",ID.get(position).toString());
                intent.putExtra("img", imagen.get(position).toString());
                intent.putExtra("nombre", Nombre.get(position).toString());
                intent.putExtra("especialidad", Especialidad.get(position).toString());
                intent.putExtra("cedula", Cedula.get(position).toString());
                intent.putExtra("descripcion", Descripcion.get(position).toString());
                startActivity(intent);

            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }//fin onCreate

    private void Cargardatos(){
        ID.clear();
        Nombre.clear();
        Especialidad.clear();
        Cedula.clear();
        Ciudad.clear();
        Pais.clear();
        imagen.clear();
        Descripcion.clear();


            pDialog = new ProgressDialog(Psicologos.this);
            pDialog.setMessage("Cargando psicologos. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        AsyncHttpClient Client = new AsyncHttpClient();
        Client.get(url_all_psicologos, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    pDialog.dismiss();

                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));

                        Log.d("Json:",jsonArray.toString());

                        for(int i = 0;i<jsonArray.length();i++){

                            ID.add(jsonArray.getJSONObject(i).getString(TAG_ID));
                            Nombre.add(jsonArray.getJSONObject(i).getString(TAG_NOMBRE));
                            Especialidad.add(jsonArray.getJSONObject(i).getString(TAG_ESPECIALIDAD));
                            Cedula.add(jsonArray.getJSONObject(i).getString(TAG_CEDULA));
                            Ciudad.add(jsonArray.getJSONObject(i).getString(TAG_CIUDAD));
                            Pais.add(jsonArray.getJSONObject(i).getString(TAG_PAIS));
                            imagen.add(jsonArray.getJSONObject(i).getString(TAG_IMAGEN));
                            Estatus.add(jsonArray.getJSONObject(i).getString(TAG_ESTATUS));
                            Descripcion.add(jsonArray.getJSONObject(i).getString(TAG_DESCRIPCION));

                            lista.setAdapter(new ImagenAdapter(getApplicationContext()));

                            Log.d("Nombres:",jsonArray.getJSONObject(i).getString(TAG_NOMBRE));


                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


    private class ImagenAdapter extends BaseAdapter{
        Context context;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvid,tvnombre,tvcedu,tvpais,tvesp,tvestatus;


        public ImagenAdapter(Context applicationContext) {
            this.context=applicationContext;
            layoutInflater=(LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagen.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.single_post_old, null);

            smartImageView = (SmartImageView)viewGroup.findViewById(R.id.fotoPs);
            tvnombre = (TextView)viewGroup.findViewById(R.id.single_post_tv_nombre);
            tvcedu = (TextView)viewGroup.findViewById(R.id.single_post_tv_cedula);
            tvpais = (TextView)viewGroup.findViewById(R.id.single_post_tv_lugar);
            tvesp = (TextView)viewGroup.findViewById(R.id.single_post_tv_especialidad);
            tvestatus = (TextView)viewGroup.findViewById(R.id.single_post_tv_sesion);
            tvid = (TextView)viewGroup.findViewById(R.id.single_post_tv_id);


            String UrlFinal = fileImage+imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());


            smartImageView.setImageUrl(UrlFinal, rect);
            tvid.setText(ID.get(position).toString());
            tvnombre.setText(Nombre.get(position).toString());
            tvcedu.setText(Cedula.get(position).toString());
            tvpais.setText(Pais.get(position).toString().concat("/").concat(Ciudad.get(position).toString()));
            tvesp.setText(Especialidad.get(position).toString());
            tvestatus.setText(Estatus.get(position).toString());



            return viewGroup;
        }
    }

    //Codigo para mostrar el menu...Incluir en todas las ventanas
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
