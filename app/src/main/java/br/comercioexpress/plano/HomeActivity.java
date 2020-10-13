package br.comercioexpress.plano;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import classes.CL_Filial;
import classes.CL_Usuario;
import controllers.CTL_Filial;
import controllers.CTL_Usuario;
import models.CriaBanco;
import models.MDL_Usuario;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;


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

        try {
            View view = navigationView.getHeaderView(0);

            TextView lb_NomeVendedor = (TextView) view.findViewById(R.id.lb_NomeVendedor);
            TextView lb_EmailVendedor = (TextView) view.findViewById(R.id.lb_EmailVendedor);

            cl_Usuario = new CL_Usuario();
            ctl_Usuario = new CTL_Usuario(getApplicationContext(), cl_Usuario);

            String vf_NmUsuario = cl_Usuario.getNmUsuarioSistema();

            cl_Filial = new CL_Filial();
            ctl_Filial = new CTL_Filial(getApplicationContext(), cl_Filial);

            String vf_Filial = cl_Filial.getNomeFilial();

            try {
                lb_NomeVendedor.setText(vf_NmUsuario);
                lb_EmailVendedor.setText(vf_Filial);
            } catch (Exception e) {
                lb_NomeVendedor.setText("");
                lb_EmailVendedor.setText("");
            }
        }catch (Exception e){
            Log.d("CARREGARVENDEDOR", "Não foi possivel carregar o vendedor por causa dessa situação: " + e.getMessage());
        }

        carregaListaClientes("");

        final TextView lb_TituloClientes = (TextView) findViewById(R.id.lb_TituloClientes);

        sv_Clientes.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String vf_Nome) {

                carregaListaClientes(vf_Nome);

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


    public void carregaListaClientes(String parametroNomeRazaoSocial){
        BancoController crud = new BancoController(getBaseContext());
        Cursor cursorCliente = crud.carregaClientes();
        if(parametroNomeRazaoSocial.trim() != ""){
            cursorCliente = crud.carregaClientesNome(parametroNomeRazaoSocial);
        }
        final Cursor cursor = cursorCliente;

        final List<String> codigo = new ArrayList<>();
        List<String> nomeRazaoSocial = new ArrayList<>();
        List<String> nomeFantasia = new ArrayList<>();
        List<String> telefone = new ArrayList<>();
        List<String> email = new ArrayList<>();

        if (cursor != null) {
            while(!cursor.isAfterLast()) {

                String codigoCliente = "";
                try{
                    int indexPontoCdCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)).indexOf(".");
                    codigoCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)).substring(0, indexPontoCdCliente);
                    //int codigoCliente = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)));
                }catch (Exception e){
                    codigoCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE));
                }


                codigo.add(codigoCliente);
                nomeRazaoSocial.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
                nomeFantasia.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NMFANTASIA)));
                telefone.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONE)));
                email.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EMAIL)));

                cursor.moveToNext();
            }
        }

        lista = (ListView)findViewById(R.id.listView);
        ListaClientesCustomizadaAdapter adapter = new ListaClientesCustomizadaAdapter(this, codigo, nomeRazaoSocial, nomeFantasia, telefone, email);
        lista.setAdapter(adapter);

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


}
