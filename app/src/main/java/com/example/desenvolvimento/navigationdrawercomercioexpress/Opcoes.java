package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Opcoes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BancoController crud;
    Cursor cursor;

    int countCli;

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

        crud = new BancoController(getBaseContext());
        String usuario = crud.carregaUsuario();

        TextView tb_usuario = (TextView) findViewById(R.id.tb_usuarioOpcoes);
        tb_usuario.setText("Usuário: " + usuario);

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
                    new LoadingAsyncOpcoes().execute();
                } else {
                    MensagemUtil.addMsg(Opcoes.this, "É necessário ter conexão com a internet para realizar a sincronização!");
                }
            }
        });

        TextView lb_sincronizacao = (TextView)findViewById(R.id.lb_ultimaatualizacaoData);
        String VA_DtUltSincronizacao = crud.carregaDtSincronizacao();
        lb_sincronizacao.setText(VA_DtUltSincronizacao);

        TextView lb_filial = (TextView)findViewById(R.id.lb_filialselecionada);
        lb_filial.setText("Filial: " + crud.buscaNmFilialSelecionada());

        Button sc_trocafilial = (Button)findViewById(R.id.sc_trocafilial);

        if(crud.autorizaTrocaFilial().equals("S") && crud.countFilial() > 1){
            sc_trocafilial.setVisibility(View.VISIBLE);
        }else{
            sc_trocafilial.setVisibility(View.INVISIBLE);
        }

        sc_trocafilial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String teste = crud.carregaPedidoAbertos();
                if(crud.carregaPedidoAbertos().equals("N")){
                    Intent secondActivity;
                    secondActivity = new Intent(Opcoes.this, Filial.class);
                    secondActivity.putExtra("operacao", "T");
                    startActivity(secondActivity);
                }else{
                    MensagemUtil.addMsg(Opcoes.this, "Existem pedidos em aberto. Todos os pedidos devem ser enviados antes da troca de filial.");
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, HomeActivity.class);
            startActivity(secondActivity);

        } else if (id == R.id.nav_gallery) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Pedidos.class);
            startActivity(secondActivity);

        }
        else if (id == R.id.nav_slideshow) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Produtos.class);
            secondActivity.putExtra("selecaoProdutos", "N");
            startActivity(secondActivity);

        } else if (id == R.id.nav_share) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Opcoes.class);
            startActivity(secondActivity);

        }/* else if (id == R.id.nav_send) {
            Intent secondActivity;
            secondActivity = new Intent(Opcoes.this, Sincronizar.class);
            startActivity(secondActivity);

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LoadingAsyncOpcoes extends AsyncTask<Void, Void, Void> {
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

                Intent secondActivity;
                secondActivity = new Intent(Opcoes.this, Filial.class);
                secondActivity.putExtra("operacao", "L");
                startActivity(secondActivity);
            } else {
                sincronizou = "N";
            }

            //FU_Sincronizar();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            if (sincronizou.equals("S")) {
                MensagemUtil.addMsg(Opcoes.this, "Clientes e produtos sincronizados com sucesso!");
                TextView lb_sincronizacao = (TextView) findViewById(R.id.lb_ultimaatualizacaoData);
                String VA_DtUltSincronizacao = crud.carregaDtSincronizacao();
                lb_sincronizacao.setText(VA_DtUltSincronizacao);
            } else {
                MensagemUtil.addMsg(Opcoes.this, "Não foi possivel realizar a sincronização de clientes e pedidos. Favor verificar a conexão com a internet e o banco de dados!");
            }


        }
    }

    protected boolean FU_Sincronizar(){

        int TIMEOUT_MILLISEC = 10000;

        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaClientesNaoSincronizados("N");

        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

                String cdclienteString = "";
                String rzsocialString = "";
                String nmfantasiaString = "";
                String cepString = "";
                String enderecoString = "";
                String classificacaoString = "";
                String numeroString = "";
                String complementoString = "";
                String bairroString = "";
                String estadoString = "";
                String cidadeString = "";
                String cnpjString = "";
                String telefoneString = "";
                String telefoneAdicionalString = "";
                String faxString = "";
                String nmcontatoString = "";
                String emailString = "";
                String tipopessoaString = "";
                String tipoclienteString = "";
                String obsclienteString = "";
                String vendedorString = "99";
                String dtultalteracao = "";
                String dtcadastro = "";
                String resultado = "";
                String inscestString = "";

                if(!cursor.getString(cursor.getColumnIndex("cdcliente")).equals("null") && !cursor.getString(cursor.getColumnIndex("cdcliente")).trim().equals("")) {
                    cdclienteString = cursor.getString(cursor.getColumnIndex("cdcliente")).replace("'", "");
                }else{
                    cdclienteString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("rzsocial")).equals("null") && !cursor.getString(cursor.getColumnIndex("rzsocial")).trim().equals("")) {
                    rzsocialString = cursor.getString(cursor.getColumnIndex("rzsocial")).replace("'", "");
                }else{
                    rzsocialString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("nmfantasia")).equals("null") && !cursor.getString(cursor.getColumnIndex("nmfantasia")).trim().equals("")) {
                    nmfantasiaString = cursor.getString(cursor.getColumnIndex("nmfantasia")).replace("'", "");
                }else{
                    nmfantasiaString = "espaco";
                }
                //nmfantasiaString = "Guilherme";
                if(!cursor.getString(cursor.getColumnIndex("cep")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cep")).trim().equals("")) {
                    cepString = cursor.getString(cursor.getColumnIndex("cep")).replace(".", "").replace("-", "");
                }else{
                    cepString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("endereco")).equals("null")  && !cursor.getString(cursor.getColumnIndex("endereco")).trim().equals("")) {
                    enderecoString = cursor.getString(cursor.getColumnIndex("endereco")).replace("'", "");
                }else{
                    enderecoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("classificacao")).equals("null")  && !cursor.getString(cursor.getColumnIndex("classificacao")).trim().equals("")) {
                    classificacaoString = cursor.getString(cursor.getColumnIndex("classificacao")).replace("'", "");
                }else{
                    classificacaoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("numero")).equals("null")  && !cursor.getString(cursor.getColumnIndex("numero")).trim().equals("")) {
                    numeroString = cursor.getString(cursor.getColumnIndex("numero"));
                }else{
                    numeroString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("complemento")).equals("null")  && !cursor.getString(cursor.getColumnIndex("complemento")).trim().equals("")) {
                    complementoString = cursor.getString(cursor.getColumnIndex("complemento")).replace("'", "");
                }else{
                    complementoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("bairro")).equals("null")  && !cursor.getString(cursor.getColumnIndex("bairro")).trim().equals("")) {
                    bairroString = cursor.getString(cursor.getColumnIndex("bairro")).replace("'", "");
                }else{
                    bairroString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("uf")).equals("null")  && !cursor.getString(cursor.getColumnIndex("uf")).trim().equals("")) {
                    estadoString = cursor.getString(cursor.getColumnIndex("uf"));
                }else{
                    estadoString = "PR";
                }
                if(!cursor.getString(cursor.getColumnIndex("cidade")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cidade")).trim().equals("")) {
                    cidadeString = cursor.getString(cursor.getColumnIndex("cidade")).replace("'", "");
                }else{
                    cidadeString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("cgc")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cgc")).trim().equals("")) {
                    cnpjString = cursor.getString(cursor.getColumnIndex("cgc")).replace(".", "").replace("-", "").replace("/", "");
                }else{
                    cnpjString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("telefone")).equals("null")  && !cursor.getString(cursor.getColumnIndex("telefone")).trim().equals("")) {
                    telefoneString = cursor.getString(cursor.getColumnIndex("telefone")).replace("(", "").replace("-", "").replace(")", "");
                }else{
                    telefoneString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("telefoneadicional")).equals("null")  && !cursor.getString(cursor.getColumnIndex("telefoneadicional")).trim().equals("")) {
                    telefoneAdicionalString = cursor.getString(cursor.getColumnIndex("telefoneadicional")).replace("(", "").replace("-", "").replace(")", "");;
                }else{
                    telefoneAdicionalString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("fax")).equals("null")  && !cursor.getString(cursor.getColumnIndex("fax")).trim().equals("")) {
                    faxString = cursor.getString(cursor.getColumnIndex("fax")).replace("(", "").replace("-", "").replace(")", "");;
                }else{
                    faxString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("nmcontatocliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("nmcontatocliente")).trim().equals("")) {
                    nmcontatoString = cursor.getString(cursor.getColumnIndex("nmcontatocliente")).replace("'", "");
                }else{
                    nmcontatoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("email")).equals("null")  && !cursor.getString(cursor.getColumnIndex("email")).trim().equals("")) {
                    emailString = cursor.getString(cursor.getColumnIndex("email")).replace("'", "");
                }else{
                    emailString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("tipopessoa")).equals("null")  && !cursor.getString(cursor.getColumnIndex("tipopessoa")).trim().equals("")) {
                    tipopessoaString = cursor.getString(cursor.getColumnIndex("tipopessoa"));
                }else{
                    tipopessoaString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("tipcliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("tipcliente")).trim().equals("")) {
                    tipoclienteString = cursor.getString(cursor.getColumnIndex("tipcliente"));
                }else{
                    tipoclienteString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("obscliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("obscliente")).trim().equals("")) {
                    obsclienteString = cursor.getString(cursor.getColumnIndex("obscliente")).replace("'", "");
                }else{
                    obsclienteString = "espaco";
                }
                //tipoclienteString = "EXPRESSPLANO";
                if(!cursor.getString(cursor.getColumnIndex("dtultalteracao")).equals("null")  && !cursor.getString(cursor.getColumnIndex("dtultalteracao")).trim().equals("")) {
                    dtultalteracao = cursor.getString(cursor.getColumnIndex("dtultalteracao"));
                }else{
                    dtultalteracao = "espaco";
                }

                if(!cursor.getString(cursor.getColumnIndex("dtcadastro")).equals("null")  && !cursor.getString(cursor.getColumnIndex("dtcadastro")).trim().equals("")) {
                    dtcadastro = cursor.getString(cursor.getColumnIndex("dtcadastro"));
                }else{
                    dtcadastro = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("vendedor")).equals("null")  && !cursor.getString(cursor.getColumnIndex("vendedor")).trim().equals("")) {
                    vendedorString = cursor.getString(cursor.getColumnIndex("vendedor"));
                }else{
                    vendedorString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("inscestadual")).equals("null")  && !cursor.getString(cursor.getColumnIndex("inscestadual")).trim().equals("")) {
                    inscestString = cursor.getString(cursor.getColumnIndex("inscestadual"));
                }else{
                    inscestString = "espaco";
                }

                try {
                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null, null);

                    MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                    HttpParams p = new BasicHttpParams();

                    HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
                    HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

                    SchemeRegistry registry = new SchemeRegistry();
                    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                    registry.register(new Scheme("https", sf, 443));

                    ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


                    HttpConnectionParams.setConnectionTimeout(p,
                            TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

                    HttpClient httpclient = new DefaultHttpClient(ccm, p);
                    String url = "http://www.planosistemas.com.br/" +
                            "WebService2.php?user=" + crud.selecionarCdClienteBanco() +
                            "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + rzsocialString.replace(" ", "espaco") +
                            "&nmfantasia=" + nmfantasiaString.replace(" ", "espaco") +
                            "&cep=" + cepString +
                            "&endereco=" + enderecoString.replace(" ", "espaco") +
                            "&classificacao=" + classificacaoString.replace(" ", "espaco") +
                            "&numero=" + numeroString +
                            "&complemento=" + complementoString.replace(" ", "espaco") +
                            "&bairro=" + bairroString.replace(" ", "espaco") +
                            "&uf=" + estadoString +
                            "&cidade=" + cidadeString.replace(" ", "espaco") +
                            "&tipopessoa=" + tipopessoaString +
                            "&cgc=" + cnpjString +
                            "&telefone=" + telefoneString +
                            "&telefoneadicional=" + telefoneAdicionalString +
                            "&fax=" + faxString +
                            "&contato=" + nmcontatoString.replace(" ", "espaco") +
                            "&email=" + emailString +
                            "&vendedor=" + vendedorString +
                            "&tipocliente=" + tipoclienteString.replace(" ", "espaco") +
                            "&dtcadastro=" + dtcadastro.replace(" ", "espaco") + "" +
                            "&obscliente=" + obsclienteString.replace(" ", "espaco") +
                            "&inscestadual=" + inscestString.replace(" ", "espaco") +
                            "&cdfilial=" + crud.buscaFilialSelecionada() + "";

                    HttpPost httppost = new HttpPost(url);

                    // Instantiate a GET HTTP method
                    try {
                        Log.i(getClass().getSimpleName(), "send  task - start");
                        //
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                                2);
                        nameValuePairs.add(new BasicNameValuePair("user", "1"));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String responseBody = httpclient.execute(httppost, responseHandler);
                        // Parse
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray jArray = json.getJSONArray("posts");
                        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                        for (int i = 0; i < jArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject e = jArray.getJSONObject(i);
                            String s = e.getString("post");
                            JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                                map.put("UserName", jObject.getString("Endereco"));
                                map.put("FullName", jObject.getString("Telefone"));*/

                                /*map.put("idusers", jObject.getString("idusers"));
                                map.put("UserName", jObject.getString("UserName"));
                                map.put("FullName", jObject.getString("FullName"));*/

                            //mylist.add(map);

                                    /*if(!jObject.getString("NmTipo").equals("null")) {
                                        crud.insereTipoCliente(jObject.getString("CdTipo"), jObject.getString("NmTipo"));
                                    }

                                }*/

                            if (!jObject.getString("CdCliente").equals("null")) {

                                Cursor cursor_pedidocliente = crud.carregaPedidoCliente(cdclienteString);
                                while (!cursor_pedidocliente.isAfterLast()) {
                                    int numerodopedido = Integer.parseInt(cursor_pedidocliente.getString(cursor_pedidocliente.getColumnIndex(CriaBanco.ID)));
                                    crud.alteraEmitentePedido(jObject.getString("CdCliente"), Integer.parseInt(cursor_pedidocliente.getString(cursor_pedidocliente.getColumnIndex(CriaBanco.ID))));
                                    cursor_pedidocliente.moveToNext();
                                }
                                //cdemitente =  jObject.getString("CdCliente");
                            }
                        }


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    //Toast.makeText(getApplicationContext(), "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }

                cursor.moveToNext();
            }
        }

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams p = new BasicHttpParams();

            HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            Funcoes funcoes = new Funcoes();
            String url = "";
            if(funcoes.verificaAutorizacao("SCV", "SCVF101TOV", crud.selecionarNmUsuarioSistema(), crud.selecionarCdClienteBanco())){
                url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientenovo&cdfilial=" + crud.buscaFilialSelecionada() + "";
            }else {
                url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientevendedor&cdvendedor=" + crud.selecionaVendedor() + "&cdfilial=" + crud.buscaFilialSelecionada() + "";
            }
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


                crud.deletaTodosClientes();

                try{
                    String teste = crud.verificaColunasClassificacaoFidelidade();
                }catch (Exception e){
                    crud.criaColunasClassificacaoFidelidade();
                }

                countCli = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    String sRzSocial = e.getString("post2");
                    String sNmFantasia = e.getString("post3");
                    String sEndereco = e.getString("post4");
                    String sComplemento = e.getString("post5");
                    String sBairro = e.getString("post6");
                    String sCidade = e.getString("post7");
                    String sNmContatoCliente = e.getString("post8");
                    String sNmTipo = e.getString("post9");
                    String sObsCliente = e.getString("post10");
                    JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!sRzSocial.equals("null")) {
                        String VA_TipoPessoa = jObject.getString("FgTipoPessoa");
                        String VA_Cgc = jObject.getString("CGC");
                        String VA_telefone = "", VA_telefoneAdicional = "", VA_fax = "";
                        VA_telefone = jObject.getString("Telefone").replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
                        VA_telefoneAdicional = jObject.getString("TelefoneAdicional").replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
                        VA_fax = jObject.getString("Fax").replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
                        if (VA_TipoPessoa.equals("F")) {
                            VA_TipoPessoa = "Física";
                            if(VA_Cgc.length() < 11){
                                VA_Cgc = "0" + VA_Cgc;
                            }
                        } else {
                            VA_TipoPessoa = "Jurídica";
                            if(VA_Cgc.length() < 14){
                                VA_Cgc = "0" + VA_Cgc;
                            }
                        }
                        int VA_tamanhoTelefone = VA_telefone.length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(VA_tamanhoTelefone > 0) {
                            if (VA_telefone.substring(0, 1).equals("0")) {
                                VA_telefone = VA_telefone.substring(1, VA_tamanhoTelefone);
                            }
                        }

                        VA_tamanhoTelefone = VA_telefoneAdicional.length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(VA_tamanhoTelefone > 0) {
                            if (VA_telefoneAdicional.substring(0, 1).equals("0")) {
                                VA_telefoneAdicional = VA_telefoneAdicional.substring(1, VA_tamanhoTelefone);
                            }
                        }

                        VA_tamanhoTelefone = VA_fax.length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(VA_tamanhoTelefone > 0) {
                            if (VA_fax.substring(0, 1).equals("0")) {
                                VA_fax = VA_fax.substring(1, VA_tamanhoTelefone);
                            }
                        }

                        crud.inserirCliente(jObject.getString("CdCliente"), sRzSocial, sNmFantasia, jObject.getString("Cep"), sEndereco, jObject.getString("NumEndereco"), sComplemento, sBairro, jObject.getString("Uf"), sCidade, VA_Cgc, jObject.getString("InscEst"), VA_telefone.replace(" ", ""), VA_telefoneAdicional.replace(" ", ""), VA_fax.replace(" ", ""), sNmContatoCliente, jObject.getString("Email"), sNmTipo, jObject.getString("CdVendedor"), jObject.getString("FgTipoPessoa"), jObject.getString("DtUltAlteracao"), jObject.getString("DtCadastro"), "S", sObsCliente, jObject.getString("Classificacao"), jObject.getString("Fidelidade"), jObject.getString("TipoPreco"));
                        countCli += 1;
                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            return false;
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a conexão com o servidor. Favor verificar a conexão com a internet. Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams p = new BasicHttpParams();

            HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=produtos";
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                crud.deletaTodosProdutos();

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    String s2 = e.getString("post2");
                    JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!s2.equals("null")) {
                        String VA_Desconto = "0";
                        if(Double.parseDouble(jObject.getString("PercDescMaxVendedor")) > 0) {
                            VA_Desconto = jObject.getString("PercDescMaxVendedor");
                        }else if(Double.parseDouble(jObject.getString("DescCategoria")) > 0) {
                            VA_Desconto = jObject.getString("DescCategoria");
                        }else if(Double.parseDouble(jObject.getString("DescDepartamento")) > 0) {
                            VA_Desconto = jObject.getString("DescDepartamento");
                        }

                        //crud.atualizarDescontos(jObject.getString("CdProduto"), jObject.getString("VlVenda"), VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);
                        //crud.inserirProdutoFilial(jObject.getString("CdProduto"), sDescricao, jObject.getString("EstAtual"), jObjectPrecoFilial.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);


                        crud.inserirProdutoFilial(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("VlAtacado"), jObject.getString("DtUltAlteracao"), VA_Desconto, jObject.getString("DescontoA"), jObject.getString("DescontoB"), jObject.getString("DescontoC"), jObject.getString("DescontoD"), jObject.getString("DescontoE"), jObject.getString("DescontoFidelidade"));

                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            return false;
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a conexão com o servidor. Favor verificar a conexão com a internet. Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams p = new BasicHttpParams();

            HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=precoindividualizado";
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!jObject.getString("PrecoIndividualizado").equals("null") && !jObject.getString("PrecoIndividualizado").equals("") && jObject.getString("PrecoIndividualizado").equals("S")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        try {
                            if (crud.verificaColunaPrecoIndividualizado().equals("S")) {
                                crud.atualizarPrecoIndividualizado("S");
                            }else{
                                try {
                                    crud.criaColunaPrecoIndividualizado();
                                    crud.atualizarPrecoIndividualizado("S");
                                }catch (Exception e5){

                                    //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

                                }
                            }
                        }catch (Exception e2) {
                            try {
                                crud.criaColunaPrecoIndividualizado();
                                crud.atualizarPrecoIndividualizado("S");
                            }catch (Exception e4){

                                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

                            }
                        }
                        //
                    }else{
                        try {
                            if (crud.verificaColunaPrecoIndividualizado().equals("S")) {
                                crud.atualizarPrecoIndividualizado("N");
                            }else{
                                crud.criaColunaPrecoIndividualizado();
                            }
                        }catch (Exception e2) {
                            crud.criaColunaPrecoIndividualizado();
                        }
                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                // TODO Auto-generated catch block
               /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
           /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
            return false;
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

        }


        if(crud.buscaPrecoIndividualizado().equals("S")){

            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams p = new BasicHttpParams();

                HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


                HttpConnectionParams.setConnectionTimeout(p,
                        TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

                HttpClient httpclient = new DefaultHttpClient(ccm, p);

                String url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=produtoprecofilial&cdfilial=" + crud.buscaFilialSelecionada() + "";
                HttpPost httppost = new HttpPost(url);

                // Instantiate a GET HTTP method
                try {
                    Log.i(getClass().getSimpleName(), "send  task - start");
                    //
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                            2);
                    nameValuePairs.add(new BasicNameValuePair("user", "1"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody = httpclient.execute(httppost, responseHandler);
                    // Parse
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray jArray = json.getJSONArray("posts");
                    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


                    for (int i = 0; i < jArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject e = jArray.getJSONObject(i);
                        String s = e.getString("post");
                        JSONObject jObject = new JSONObject(s);
                        try {
                            //JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                            //mylist.add(map);

                            if (!jObject.getString("CdProduto").equals("null")) {
                                //crud.insereCdClienteBanco(jObject.getString("CdCliente"));

                                String VA_DescontoA = "0";
                                String VA_DescontoB = "0";
                                String VA_DescontoC = "0";
                                String VA_DescontoD = "0";
                                String VA_DescontoE = "0";
                                String VA_DescontoFidelidade = "0";

                                String VA_CdProduto = jObject.getString("CdProduto");

                                if(Double.parseDouble(jObject.getString("VlVenda")) > 0){
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("VlVenda"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoA")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoA"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoB")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoB"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoC")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoC"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoD")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoD"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoE")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoE"));
                                }
                                if (Double.parseDouble(jObject.getString("PercDescontoFidelidade")) > 0) {
                                    crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoFidelidade"));
                                }

                                //crud.atualizarDescontos(jObject.getString("CdProduto"), jObject.getString("VlVenda"), VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);
                                //crud.inserirProdutoFilial(jObject.getString("CdProduto"), sDescricao, jObject.getString("EstAtual"), jObjectPrecoFilial.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);

                                //
                            }
                        }catch(Exception e3) {


                        }

                    }


                    //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                    return false;
                    //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    // TODO Auto-generated catch block
               /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                    return false;
                    //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

                }
                // Log.i(getClass().getSimpleName(), "send  task - end");



            } catch (Throwable t) {
           /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }


        }


        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams p = new BasicHttpParams();

            HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(p, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(p, registry);


            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=tipocliente";
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                crud.deletaTipoCliente();
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    String s2 = e.getString("post2");
                    //JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!s2.equals("null") && !s2.trim().equals("")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        String VA_NmTipo = s2;
                        VA_NmTipo.replace("\\u00c7", "Ç");
                        crud.insereTipoCliente(s, VA_NmTipo);
                        //
                    }

                    /*if (!jObject.getString("NmTipo").equals("null") && !jObject.getString("NmTipo").trim().equals("")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        String VA_NmTipo = jObject.getString("NmTipo");
                        crud.insereTipoCliente(jObject.getString("CdTipo"), jObject.getString("NmTipo"));
                        //
                    }*//*else{
                        return false;
                    }*/

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
               /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;
            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
           /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
            return false;
        }

        crud.atualizaSincronizacao(getDateTime(), countCli);


        return true;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
