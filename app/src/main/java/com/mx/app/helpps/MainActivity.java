package com.mx.app.helpps;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SessionManager session;

    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    TextView usersesion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());


        appbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);



        /*
        //Eventos del Drawer Layout
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        */

        navView = (NavigationView)findViewById(R.id.navview);
        navView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    boolean fragmentTransaction = false;
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.menu_psicologos:
                            fragment = new PsicologosFragment();
                            fragmentTransaction = true;
                            break;
                        case R.id.menu_servicios:
                            fragment = new Servicios();
                            fragmentTransaction = true;
                            break;
                        case R.id.menu_pago:
                            fragment = new Fragment3();
                            fragmentTransaction = true;
                            break;
                        case R.id.nav_log_out:

                            AlertDialog.Builder builder = new AlertDialog.Builder(navView.getContext());
                            builder.setMessage("Esta seguro de cerrar sesión?")
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
                                                    session.logoutUser();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                            break;
                    }

                    if(fragmentTransaction) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();

                        menuItem.setChecked(true);
                        getSupportActionBar().setTitle(menuItem.getTitle());
                    }

                    drawerLayout.closeDrawers();

                    return true;
                }
            });

        /*setContentView(R.layout.header_navview);
        TextView usuario = (TextView)findViewById(R.id.nombreSesion);

        //Validar sesion
        session.checkLogin();
        //Extraer datos de session
        HashMap<String, String> user = session.getUserDetails();
        //Email
        String email = user.get(SessionManager.KEY_EMAIL);
        //Mostrar Email
        usuario.setText(email);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //Desactiva el atras del telefono
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
