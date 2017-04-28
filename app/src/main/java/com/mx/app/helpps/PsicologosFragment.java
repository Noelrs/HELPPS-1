package com.mx.app.helpps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PsicologosFragment extends Fragment {
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParserOld jParser = new JSONParserOld();

    ArrayList<HashMap<String, String>> psicologosList;

    String fileImage = "http://psicologoonline.com.mx/myappconect/FOTOS/";

    //SessionManager session;

    // url to get all products list
    private static String url_all_psicologos = "http://psicologoonline.com.mx/myappconect/WS/get_all_psicologos.php";
    private static String url_psc_byEspecialidad = "";
    private static String url_solo_psicologos = "";
    private static String url_solo_terapeutas = "";

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
    private static final String TAG_TELEFONO = "TELEFONO";

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
    ArrayList Telefono = new ArrayList();

    ListView lista;
    ImageButton filtrar;
    View view;

    public PsicologosFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.psicologos_fragment, container, false);

        lista = (ListView)view.findViewById(R.id.listAllPsicologos);
        Cargardatos(url_all_psicologos);

        filtrar = (ImageButton)view.findViewById(R.id.btnFiltrar);
        filtrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarOpciones();
            }
        });

        lista.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), String.valueOf(ID.get(position).toString()), Toast.LENGTH_SHORT).show();
                //boolean fragmentTransaction = false;
                //Fragment fragment = null;
                Intent intent = new Intent(view.getContext(), DetallePsicologo.class);
                intent.putExtra("id",ID.get(position).toString());
                intent.putExtra("img", imagen.get(position).toString());
                intent.putExtra("nombre", Nombre.get(position).toString());
                intent.putExtra("especialidad", Especialidad.get(position).toString());
                intent.putExtra("cedula", Cedula.get(position).toString());
                intent.putExtra("descripcion", Descripcion.get(position).toString());
                intent.putExtra("estatus", Estatus.get(position).toString());
                intent.putExtra("telefono",Telefono.get(position).toString());
                startActivity(intent);



            }
        });
        return view;


    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);


    }

    private void MostrarOpciones(){

        //PopUp para elegir tipo de filtro
        final CharSequence[] items = {"Solo Psicólogos","Solo Terapeutas","Especialidad"};
        Log.d("0", "En tipos de Filtro");
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Filtrar la lista por?");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.d("0", "Tipo seleccionado");
                dialog.cancel();
                String selectOption = items[item].toString();
                FiltrarBusqueda(selectOption);

            }
        });

    }

    private void FiltrarBusqueda(String option){

        if(option.equals("Solo Psicólogos")){
            Cargardatos(url_solo_psicologos);
        }

        if(option.equals("Solo Terapeutas")){
            Cargardatos(url_solo_terapeutas);
        }

        if(option.equals("Especialidad")){

            //PopUp para elegir Especialidad
            final CharSequence[] items = {"Terapia breve","Parejas","Jovenes","Adicciones"};
            Log.d("0", "En elegir Especialidad");
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this.getContext());
            builder1.setTitle("Elija una Especialidad");
            builder1.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    Log.d("0", "Selecciono especialidad");
                    dialog.cancel();
                    String selectOption = items[item].toString();
                    Cargardatos(url_psc_byEspecialidad);

                }
            });

        }

    }


    private void Cargardatos(String Url){
        ID.clear();
        Nombre.clear();
        Especialidad.clear();
        Cedula.clear();
        Ciudad.clear();
        Pais.clear();
        imagen.clear();
        Descripcion.clear();
        Telefono.clear();


        pDialog = new ProgressDialog(view.getContext());
        pDialog.setMessage("Buscando psicologos. Por favor espere...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient Client = new AsyncHttpClient();
        Client.get(Url, new AsyncHttpResponseHandler() {
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
                            Telefono.add(jsonArray.getJSONObject(i).getString(TAG_TELEFONO));

                            lista.setAdapter(new ImagenAdapter(view.getContext()));

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


    private class ImagenAdapter extends BaseAdapter {
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
            Log.d("Nombre", tvnombre.toString());


            String UrlFinal = fileImage+imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
            Log.d("URL Imagen:",UrlFinal);

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

}
