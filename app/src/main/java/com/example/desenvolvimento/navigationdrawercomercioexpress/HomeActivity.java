package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView lista;
    private Toast toast;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    int countCli;

    MaterialSearchView sv_Clientes;

    MenuItem me_BuscarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sv_Clientes = (MaterialSearchView) findViewById(R.id.sv_Clientes);
        sv_Clientes.setVoiceSearch(true); //or false

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent secondActivity;
                secondActivity = new Intent(HomeActivity.this, CadastroClientes.class);
                secondActivity.putExtra("codigo", "0");
                startActivity(secondActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BancoController crud = new BancoController(getBaseContext());
        final Cursor cursor = crud.carregaClientes();

        String[] nomeCampos = new String[] {CriaBanco.ID, CriaBanco.RZSOCIAL};
        int[] idViews = new int[] {R.id.cdCliente, R.id.rzsociallist};

        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewteste, cursor, nomeCampos, idViews, 0);
        lista = (ListView)findViewById(R.id.listView);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codigo;
                cursor.moveToPosition(position);
                codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                Intent intent = new Intent(HomeActivity.this, CadastroClientes.class);
                intent.putExtra("codigo", codigo);
                startActivity(intent);
            }
        });


        final EditText tb_buscarcliente = (EditText)findViewById(R.id.tb_buscarcliente);

        tb_buscarcliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BancoController crud = new BancoController(getBaseContext());
                final Cursor cursor = crud.carregaClientesNome(tb_buscarcliente.getText().toString());

                String[] nomeCampos = new String[]{CriaBanco.RZSOCIAL};
                int[] idViews = new int[]{R.id.rzsociallist};

                SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewteste, cursor, nomeCampos, idViews, 0);
                lista = (ListView) findViewById(R.id.listView);
                lista.setAdapter(adaptador);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String codigo;
                        cursor.moveToPosition(position);
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent(HomeActivity.this, CadastroClientes.class);
                        intent.putExtra("codigo", codigo);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final TextView lb_TituloClientes = (TextView) findViewById(R.id.lb_TituloClientes);

        sv_Clientes.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String vf_Nome) {

                String vf_RazaoSocialNome = vf_Nome;
                try{
                    BancoController crud = new BancoController(getBaseContext());
                    final Cursor cursor = crud.carregaClientesNome(vf_RazaoSocialNome);

                    String[] nomeCampos = new String[]{CriaBanco.RZSOCIAL};
                    int[] idViews = new int[]{R.id.rzsociallist};

                    SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewteste, cursor, nomeCampos, idViews, 0);
                    lista = (ListView) findViewById(R.id.listView);
                    lista.setAdapter(adaptador);

                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String codigo;
                            cursor.moveToPosition(position);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                            Intent intent = new Intent(HomeActivity.this, CadastroClientes.class);
                            intent.putExtra("codigo", codigo);
                            startActivity(intent);
                        }
                    });
                }catch (Exception e){
                    MensagemUtil.addMsg(HomeActivity.this, "Não foi possível selecionar o cliente");
                }

                return false;
            }
        });

        sv_Clientes.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                me_BuscarCliente.setVisible(false);
                lb_TituloClientes.setWidth(0);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                me_BuscarCliente.setVisible(true);
                lb_TituloClientes.setWidth(550);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.buscar_cliente);

        sv_Clientes.setMenuItem(item);

        me_BuscarCliente = menu.findItem(R.id.buscar_cliente);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, Opcoes.class);
            startActivity(secondActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(secondActivity);
            // Handle the camera action
        } else if (id == R.id.nav_pedidos) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, Pedidos.class);
            startActivity(secondActivity);


        } else if (id == R.id.nav_produtos) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, Produtos.class);
            secondActivity.putExtra("selecaoProdutos", "N");
            startActivity(secondActivity);

        } else if (id == R.id.nav_opcoes) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, Opcoes.class);
            startActivity(secondActivity);

        } else if (id == R.id.nav_visaogeral) {
            Intent secondActivity;
            secondActivity = new Intent(HomeActivity.this, VisaoGeralNova.class);
            startActivity(secondActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}
