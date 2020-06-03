package br.comercioexpress.plano;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import classes.CL_Clientes;
import classes.CL_Filial;
import classes.CL_Pedidos;
import classes.CL_Usuario;
import controllers.CTL_Clientes;
import controllers.CTL_Filial;
import controllers.CTL_Pedidos;
import controllers.CTL_Usuario;
import models.CriaBanco;
import models.MDL_Usuario;
import sync.SYNC_Clientes;
import sync.SYNC_Configuracao;
import sync.SYNC_Filial;
import sync.SYNC_Pedidos;
import sync.SYNC_Produtos;

public class Opcoes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Funcoes funcoes = new Funcoes();

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    SYNC_Pedidos sync_Pedidos;
    SYNC_Clientes sync_Clientes;
    SYNC_Produtos sync_Produtos;
    SYNC_Filial sync_Filial;
    SYNC_Configuracao sync_Configuracao;

    Context vc_Context;

    TextView lb_sincronizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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

        sync_Pedidos = new SYNC_Pedidos(getApplicationContext());
        sync_Clientes = new SYNC_Clientes(getApplicationContext());
        sync_Produtos = new SYNC_Produtos(getApplicationContext());
        sync_Filial = new SYNC_Filial(getApplicationContext());
        sync_Configuracao = new SYNC_Configuracao(getApplicationContext());

        vc_Context = getApplicationContext();

        TextView tb_usuario = (TextView) findViewById(R.id.tb_usuarioOpcoes);
        tb_usuario.setText("Usuário: " + cl_Usuario.getUsuario());

        Button sc_trocarUsuario = (Button) findViewById(R.id.sc_trocarUsuario);

        sc_trocarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondActivity;
                secondActivity = new Intent(Opcoes.this, LoginActivity.class);
                startActivity(secondActivity);
            }

        });

        Button sc_atualizarDados = (Button) findViewById(R.id.sc_atualizardados);

        sc_atualizarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao()) {
                    LoadingAsyncSincronizar async_Sincronizar = new LoadingAsyncSincronizar();
                    async_Sincronizar.execute();
                } else {
                    MensagemUtil.addMsg(Opcoes.this, "É necessário ter conexão com a internet para realizar a sincronização!");
                }
            }
        });

        lb_sincronizacao = (TextView)findViewById(R.id.lb_ultimaatualizacaoData);
        lb_sincronizacao.setText(cl_Usuario.getDtUltimaSincronizacao());

        TextView lb_filial = (TextView)findViewById(R.id.lb_filialselecionada);
        lb_filial.setText("Filial: " + cl_Filial.getNomeFilial());

        Button sc_trocafilial = (Button)findViewById(R.id.sc_trocafilial);

        if(cl_Filial.getAutorizaTrocaFilial().equals("S") && cl_Filial.getTotalFiliais() > 1){
            sc_trocafilial.setVisibility(View.VISIBLE);
        }else{
            sc_trocafilial.setVisibility(View.INVISIBLE);
        }

        sc_trocafilial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CL_Pedidos cl_Pedidos = new CL_Pedidos();
                CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                if(ctl_Pedidos.fuPossuiPedidosAbertos()){
                    MensagemUtil.addMsg(Opcoes.this, "Existem pedidos em aberto. Todos os pedidos devem ser enviados antes da troca de filial.");
                }else{
                    Intent secondActivity;
                    secondActivity = new Intent(Opcoes.this, Filial.class);
                    secondActivity.putExtra("operacao", "T");
                    startActivity(secondActivity);
                }

            }
        });
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
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
        getMenuInflater().inflate(R.menu.opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, HomeActivity.class);
            startActivity(secondActivity);
            // Handle the camera action
        } else if (id == R.id.nav_pedidos) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Pedidos.class);
            startActivity(secondActivity);


        } else if (id == R.id.nav_produtos) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Produtos.class);
            secondActivity.putExtra("selecaoProdutos", "N");
            startActivity(secondActivity);

        } else if (id == R.id.nav_opcoes) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Opcoes.class);
            startActivity(secondActivity);

        } else if (id == R.id.nav_visaogeral) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, VisaoGeralNova.class);
            startActivity(secondActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LoadingAsyncSincronizar extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Opcoes.this);

        String sincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando clientes e produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (FU_Sincronizar()) {
                sincronizou = "S";

                /*Intent secondActivity;
                secondActivity = new Intent(Opcoes.this, Filial.class);
                secondActivity.putExtra("operacao", "L");
                startActivity(secondActivity);*/
            } else {
                sincronizou = "N";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            if (sincronizou.equals("S")) {
                MensagemUtil.addMsg(Opcoes.this, "Clientes e produtos sincronizados com sucesso!");
                lb_sincronizacao.setText(ctl_Usuario.cl_Usuario.getDtUltimaSincronizacao());

            } else {
                MensagemUtil.addMsg(Opcoes.this, "Não foi possivel realizar a sincronização de clientes e produtos. Favor verificar a conexão com a internet e o banco de dados!");
            }


        }
    }

    protected boolean FU_Sincronizar(){

        CL_Clientes cl_Clientes = new CL_Clientes();
        CTL_Clientes ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);

        if(ctl_Clientes.fuSelecionarClientesNaoSincronizados()){
            if(!sync_Clientes.FU_SincronizarClientes(ctl_Clientes.rs_Cliente)){
                return false;
            }
        }

        if(!sync_Clientes.FU_SincronizarTodosClientesServidor()){
            return false;
        }

        if(!sync_Configuracao.FU_SincronizarFgControlaEstoquePedido()){
            return false;
        }

        if(!sync_Produtos.FU_SincronizarTodosProdutosServidor()){
            return false;
        }

        if(!sync_Clientes.FU_SincronizarConfiguracaoPrecoIndividualizado()){
            return false;
        }


        if(cl_Filial.getPrecoIndividualizado().equals("S")){
            if(!sync_Produtos.FU_SincronizarPrecosIndividualizadosProdutos()){
                return false;
            }
        }

        if(!sync_Clientes.FU_SincronizarTipoCliente()){
            return false;
        }

        ctl_Usuario.cl_Usuario.setDtUltimaSincronizacao(funcoes.getDateTime());

        if(!ctl_Usuario.fuAtualizarSincronizacao()){
            return false;
        }

        return true;
    }
}
