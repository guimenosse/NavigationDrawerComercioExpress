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
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

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

import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import controllers.CTL_ItemPedido;
import controllers.CTL_Pedidos;
import sync.SYNC_Pedidos;

public class Pedidos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;

    private ListView lista;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    int countCli;

    private long lastBackPressTime = 0;

    String codigo_lista;

    MaterialSearchView sv_Pedidos;

    MenuItem me_ExcluirPedidos;
    MenuItem me_Sincronizar;
    MenuItem me_BuscarPedido;

    SYNC_Pedidos sync_Pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sv_Pedidos = (MaterialSearchView) findViewById(R.id.sv_Pedidos);
        sv_Pedidos.setVoiceSearch(true); //or false

        sync_Pedidos = new SYNC_Pedidos(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent secondActivity;
                secondActivity = new Intent(Pedidos.this, ManutencaoPedidos.class);
                secondActivity.putExtra("operacao", "I");
                startActivityForResult(secondActivity, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        cl_Pedidos = new CL_Pedidos();
        ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

        FU_CalculaPrazoSincronizacao();
        suCarregarPedidos("");

        final TextView lb_TituloPedidos = (TextView) findViewById(R.id.lb_TituloPedidos);

        sv_Pedidos.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String vf_Nome) {

                suCarregarPedidos(vf_Nome);

                return false;
            }
        });

        sv_Pedidos.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                me_ExcluirPedidos.setVisible(false);
                me_Sincronizar.setVisible(false);
                me_BuscarPedido.setVisible(false);

                lb_TituloPedidos.setWidth(0);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                //vc_RazaoSocialNome = "";
                //su_AtualizarListaFiltros();

                me_ExcluirPedidos.setVisible(true);
                me_Sincronizar.setVisible(true);
                me_BuscarPedido.setVisible(true);

                lb_TituloPedidos.setWidth(550);
            }
        });

    }

    protected void suCarregarPedidos(String nomeRazaoSocial){

        String vf_NomeRazaoSocial = nomeRazaoSocial;

        try {
            if(ctl_Pedidos.fuCarregaTodosPedidos(vf_NomeRazaoSocial)) {

                final Cursor cursor = ctl_Pedidos.rs_Pedido;

                String[] nomeCamposPedidos = new String[]{CriaBanco.ID, CriaBanco.FGSITUACAO, CriaBanco.RZSOCIAL};
                int[] idViewsPedidos = new int[]{R.id.numPedido, R.id.situacaoPedido, R.id.clientepedido};

                SimpleCursorAdapter adaptadorPedidos = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewpedidos, cursor, nomeCamposPedidos, idViewsPedidos, 0);
                lista = (ListView) findViewById(R.id.listViewPedidos);
                lista.setAdapter(adaptadorPedidos);

                lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                     @Override
                                                     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                         final String codigo, situacao;
                                                         cursor.moveToPosition(position);
                                                         codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                                                         situacao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FGSITUACAO));

                                                         suCliqueLongoLista(codigo, situacao);

                                                         return true;
                                                     }
                                                 }

                );

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                 @Override
                                                 public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                         long id) {
                                                     String codigo;
                                                     cursor.moveToPosition(position);
                                                     codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                                                     Intent intent = new Intent(Pedidos.this, ManutencaoPedidos.class);
                                                     intent.putExtra("operacao", "A");
                                                     intent.putExtra("codigo", codigo);
                                                     //startActivity(intent);
                                                     startActivityForResult(intent, 1);
                                                 }
                                             }

                );
            }

        } catch (Exception e) {
            MensagemUtil.addMsg(Pedidos.this, e.getMessage().toString());
        }
    }

    //---------------------------------------Funções para sincronização de pedidos, items de pedido e clientes novos ---------------------
    protected void FU_Sincronizar(){

        int TIMEOUT_MILLISEC = 10000;

        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaClientesNaoSincronizados("N");

        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

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
                String inscestadualString = "";

                if(!cursor.getString(cursor.getColumnIndex("rzsocial")).equals("null") && !cursor.getString(cursor.getColumnIndex("rzsocial")).trim().equals("")) {
                    rzsocialString = cursor.getString(cursor.getColumnIndex("rzsocial"));
                }else{
                    rzsocialString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("nmfantasia")).equals("null") && !cursor.getString(cursor.getColumnIndex("nmfantasia")).trim().equals("")) {
                    nmfantasiaString = cursor.getString(cursor.getColumnIndex("nmfantasia"));
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
                    enderecoString = cursor.getString(cursor.getColumnIndex("endereco"));
                }else{
                    enderecoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("classificacao")).equals("null")  && !cursor.getString(cursor.getColumnIndex("classificacao")).trim().equals("")) {
                    classificacaoString = cursor.getString(cursor.getColumnIndex("classificacao"));
                }else{
                    classificacaoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("numero")).equals("null")  && !cursor.getString(cursor.getColumnIndex("numero")).trim().equals("")) {
                    numeroString = cursor.getString(cursor.getColumnIndex("numero"));
                }else{
                    numeroString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("complemento")).equals("null")  && !cursor.getString(cursor.getColumnIndex("complemento")).trim().equals("")) {
                    complementoString = cursor.getString(cursor.getColumnIndex("complemento"));
                }else{
                    complementoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("bairro")).equals("null")  && !cursor.getString(cursor.getColumnIndex("bairro")).trim().equals("")) {
                    bairroString = cursor.getString(cursor.getColumnIndex("bairro"));
                }else{
                    bairroString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("uf")).equals("null")  && !cursor.getString(cursor.getColumnIndex("uf")).trim().equals("")) {
                    estadoString = cursor.getString(cursor.getColumnIndex("uf"));
                }else{
                    estadoString = "PR";
                }
                if(!cursor.getString(cursor.getColumnIndex("cidade")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cidade")).trim().equals("")) {
                    cidadeString = cursor.getString(cursor.getColumnIndex("cidade"));
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
                    nmcontatoString = cursor.getString(cursor.getColumnIndex("nmcontatocliente"));
                }else{
                    nmcontatoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("email")).equals("null")  && !cursor.getString(cursor.getColumnIndex("email")).trim().equals("")) {
                    emailString = cursor.getString(cursor.getColumnIndex("email"));
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
                    obsclienteString = cursor.getString(cursor.getColumnIndex("obscliente"));
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
                    inscestadualString = cursor.getString(cursor.getColumnIndex("inscestadual"));
                }else{
                    inscestadualString = "espaco";
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
                            "&inscestadual" + inscestadualString.replace(" ", "espaco") +
                            "&obscliente=" + obsclienteString.replace(" ", "espaco") + "";
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
                                /*JSONObject json = new JSONObject(responseBody);
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


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
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
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientenovo";
            }else {
                url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientevendedor&cdvendedor=" + crud.selecionaVendedor();
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

                        crud.inserirCliente(jObject.getString("CdCliente"), sRzSocial, sNmFantasia, jObject.getString("Cep"), sEndereco, jObject.getString("NumEndereco"), sComplemento, sBairro, jObject.getString("Uf"), sCidade, VA_Cgc, jObject.getString("InscEst"), VA_telefone.replace(" ", ""), VA_telefoneAdicional.replace(" ", ""), VA_fax.replace(" ", ""), sNmContatoCliente, jObject.getString("Email"), sNmTipo, jObject.getString("CdVendedor"), jObject.getString("FgTipoPessoa"), jObject.getString("DtUltAlteracao"), jObject.getString("DtCadastro"), "S", sObsCliente, jObject.getString("Classificacao"), jObject.getString("Fidelidade"), "N");
                        countCli += 1;
                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
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

                        crud.inserirProduto(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto);

                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
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


            } catch (IOException e) {
                // TODO Auto-generated catch block
               /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
           /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/

        }

        crud.atualizaSincronizacao(getDateTime(), countCli);

    }

    public boolean FU_ConsisteEnviarOnline(String numpedido){

            String VA_possuiItemPedido = "N";
            BancoController crud = new BancoController(getBaseContext());
            Cursor cursor = crud.carregaDadosPedido(numpedido);
            VA_possuiItemPedido = crud.verificaItemPedido(numpedido);

            if(!VA_possuiItemPedido.equals("S")) {
                MensagemUtil.addMsg(Pedidos.this, "Deve ser informado ao menos um produto no pedido.");
                return  false;
            }
            if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") || cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")){
                MensagemUtil.addMsg(Pedidos.this, "Deve ser informada a forma de pagamento do pedido.");
                return false;
            }

        return true;
    }

    public boolean FU_EnviarPedido(String numpedido) {

        BancoController crud = new BancoController(getBaseContext());
        Cursor cursor = crud.carregaDadosPedido(numpedido);

        String vltotal = "", dtemissao = "", cdvendedor = "", cdemitente = "", rzsocial = "", percdesconto = "", vldesconto = "", condpgto = "", obs = "";

        String numpedidoSistema = "";

        if (cursor != null) {
            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                    vltotal = "";
                    Cursor cursorItemPedido = crud.carregaItemPedido(numpedido);

                    double VL_valorTotal = 0;
                    VL_valorTotal = VL_valorTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                    while (cursorItemPedido.moveToNext()) {
                        VL_valorTotal = VL_valorTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                    }

                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        VL_valorTotal = VL_valorTotal - Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).replace(",", "."));
                    }

                    vltotal = String.format("%.2f", VL_valorTotal);
                }
            } catch (Exception e) {
                vltotal = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).trim().equals("")) {
                    dtemissao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                dtemissao = getDateTime();
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).trim().equals("")) {
                    cdvendedor = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR));
                }
            } catch (Exception e) {
                cdvendedor = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                    cdemitente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE));
                }
            } catch (Exception e) {
                cdemitente = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).trim().equals("")) {
                    rzsocial = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                rzsocial = "";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                    percdesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO));
                }else{
                    percdesconto = "0";
                }
            } catch (Exception e) {
                percdesconto = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                    vldesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO));
                }else{
                    vldesconto = "0";
                }
            } catch (Exception e) {
                vldesconto = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")) {
                    condpgto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                condpgto = "DINHEIRO";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).trim().equals("")) {
                    obs = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).replace(" ", "espaco");
                }else{
                    obs = "espaco";
                }
            } catch (Exception e) {
                obs = "espaco";
            }

            obs += "  CONDIÇÃO DE PAGAMENTO INFORMADA: " + condpgto + "";
            obs = obs.replace(" ", "espaco");

            try {
                Cursor cursor_cliente = crud.carregaClienteByCdCliente(cdemitente);
                if(cursor_cliente.getString(cursor_cliente.getColumnIndexOrThrow(CriaBanco.FGSINCRONIZADO)).equals("N")){
                    cdemitente = FU_SincronizarCliente(cdemitente);
                }
            }catch (Exception e){
                try {
                    Cursor cursor_cliente = crud.carregaClienteByCGCNovoCliente(cdemitente);
                    if (cursor_cliente.getString(cursor_cliente.getColumnIndexOrThrow(CriaBanco.FGSINCRONIZADO)).equals("N")) {
                        cdemitente = FU_SincronizarCliente(cdemitente);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            int TIMEOUT_MILLISEC = 10000;
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
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inserirpedido&vltotal=" + vltotal + "&dtemissao=" + dtemissao + ""
                        + "&cdvendedor=" + cdvendedor + "&cdemitente=" + cdemitente + "&rzsocial=" + rzsocial + "&percdesconto=" + percdesconto + "&vldesconto=" + vldesconto + ""
                        + "&condpgto=" + condpgto + "&obs=" + obs.replace("\n", "pulalinha") + "&filial=" + crud.buscaFilialSelecionada() + "";
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
                        if (!jObject.getString("NumPedido").equals("null")) {
                            String NumPedido = jObject.getString("NumPedido");
                            if(FU_SincronizaItemPedido(NumPedido, dtemissao, numpedido)){
                                FU_AlteraSituacaoPedido(NumPedido);

                            }else{
                                return false;
                            }
                        }
                        /*if (!jObject.getString("NumPedido").equals("null")) {
                            String NumPedido = jObject.getString("NumPedido");
                            FU_SincronizaItemPedido(NumPedido, dtemissao, numpedido);
                            //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                            //
                        }*/

                    }


                    //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    return false;
                    /*e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    return false;
                    /*e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                }
                // Log.i(getClass().getSimpleName(), "send  task - end");

            } catch (Throwable t) {
                return false;
                    /*e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
            }
        }
        return true;
    }

    public boolean FU_SincronizaItemPedido(String NumPedido, String DtEmissao, String numpedidoCelular){
        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaItemPedido(numpedidoCelular);

        String cdproduto = "", qtde = "", vlunitario = "",  vltotal = "", descricao = "", percdesconto = "", vldesconto = "";
        int id = 0;

        if(cursor!=null) {

            while (!cursor.isAfterLast()) {
                id += 1;
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                        cdproduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    cdproduto = "0";
                }


                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                        qtde = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    qtde = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                        vlunitario = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vlunitario = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        vltotal = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vltotal = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                        descricao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    descricao = "";
                }
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        percdesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    percdesconto = "0";
                }
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        vldesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vldesconto = "0";
                }

                if(percdesconto.trim().equals("")){
                    percdesconto = "0";
                }


                int TIMEOUT_MILLISEC = 10000;
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
                            "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inseriritempedido&numpedido=" + NumPedido + "&cdproduto="
                            + cdproduto.replace(" ", "espaco") + "&id=" + id + "&qtde=" + qtde + "&vlunitario=" + vlunitario + "&vltotal=" + vltotal + "&dtemissao=" + DtEmissao + "&descricao=" +
                            descricao.replace(" ", "espaco") + "&vldesconto=" + vldesconto + "&percdesconto=" + percdesconto + "&filial=" + crud.buscaFilialSelecionada() + "";
                    HttpPost httppost = new HttpPost(url);

                    try {
                        Log.i(getClass().getSimpleName(), "send  task - start");
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("user", "1"));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String responseBody = httpclient.execute(httppost, responseHandler);


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        return false;
                        //e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        return false;
                        //e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    return false;
                    //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a conexão com o servidor. Favor verificar a conexão com a internet. Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
                }

                cursor.moveToNext();

            }
        }
        return true;

    }

    public boolean FU_AlteraSituacaoPedido(String NumPedido){
        BancoController crud = new BancoController(getBaseContext());

        String dataRegistro = getDateTime().substring(0, 16);
        int TIMEOUT_MILLISEC = 10000;
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
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=alteraSituacaoPedidoNovo&numpedido=" + NumPedido + "&filial=" + crud.buscaFilialSelecionada() + "&dtregistro=" + dataRegistro.replace(" ", "espaco") + "&usuario=" + crud.selecionarNmUsuarioSistema().replace(" ", "espaco") + "";
            HttpPost httppost = new HttpPost(url);

            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2) ;
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return  false;
                //e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
                //e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            return false;
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização do item do pedido. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a conexão com o servidor. Favor verificar a conexão com a internet. Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public String FU_SincronizarCliente(String cdemitente){
        String VA_CdCliente = "";

        int TIMEOUT_MILLISEC = 10000;

        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaClienteByCGCNovoCliente(cdemitente);

        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

                String idString = "";
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

                if(!cursor.getString(cursor.getColumnIndex(CriaBanco.ID)).equals("null") && !cursor.getString(cursor.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                    idString = cursor.getString(cursor.getColumnIndex(CriaBanco.ID));
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
                            "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + rzsocialString.replace(" ", "espaco") + "&nmfantasia=" + nmfantasiaString.replace(" ", "espaco") +
                            "&cep=" + cepString + "&endereco=" + enderecoString.replace(" ", "espaco") + "&numero=" + numeroString + "&complemento=" + complementoString.replace(" ", "espaco") +
                            "&bairro=" + bairroString.replace(" ", "espaco") + "&uf=" + estadoString + "&cidade=" + cidadeString.replace(" ", "espaco") + "&tipopessoa=" + tipopessoaString +
                            "&cgc=" + cnpjString + "&telefone=" + telefoneString + "&telefoneadicional=" + telefoneAdicionalString + "&fax=" + faxString + "&classificacao=" + classificacaoString +
                            "&contato=" + nmcontatoString.replace(" ", "espaco") + "&email=" + emailString + "&vendedor=" + vendedorString + "&tipocliente=" + tipoclienteString.replace(" ", "espaco") + "&dtcadastro=" + dtcadastro.replace(" ", "espaco") + "" +
                            "&obscliente=" + obsclienteString.replace(" ", "espaco") + "";
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

                            if (!jObject.getString("CdCliente").equals("null")) {

                                crud.alteraClientePedido(jObject.getString("CdCliente"), Integer.parseInt(idString));
                                cdemitente =  jObject.getString("CdCliente");

                            }
                        }


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
                }

                cursor.moveToNext();
            }
        }

        return cdemitente;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------Funções de menu - Excluir todos os Pedidos ---------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pedidos, menu);
        MenuItem item = menu.findItem(R.id.buscar_pedido);

        sv_Pedidos.setMenuItem(item);

        me_ExcluirPedidos = menu.findItem(R.id.action_excluirtodospedidos);
        me_Sincronizar = menu.findItem(R.id.sincronizar_pedidos);
        me_BuscarPedido = menu.findItem(R.id.buscar_pedido);

        return true;
    }

    @Override
    protected void onActivityResult(int codigo, int resultado, Intent intent) {
        suCarregarPedidos("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_excluirtodospedidos) {

                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Excluir Pedidos Enviados");
                //define a mensagem
                builder.setMessage("Deseja mesmo excluir todos os pedidos enviados?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        try {

                            CL_Pedidos cl_Pedidos = new CL_Pedidos();
                            CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                            if(ctl_Pedidos.fuCarregaTodosPedidosEnviados()){
                                Cursor rs_Pedidos = ctl_Pedidos.rs_Pedido;
                                while (!rs_Pedidos.isAfterLast()){

                                    cl_Pedidos.setNumPedido(rs_Pedidos.getString(rs_Pedidos.getColumnIndexOrThrow(CriaBanco.ID)));
                                    ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                                    ctl_Pedidos.fuDeletarPedido();

                                    rs_Pedidos.moveToNext();
                                }

                                MensagemUtil.addMsg(Pedidos.this, "Pedidos enviados excluídos com sucesso!");
                                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                                startActivity(intent);
                            }else{
                                MensagemUtil.addMsg(Pedidos.this, "Não foi encontrado nenhum pedido enviado para ser excluído.");
                            }

                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel excluir os pedidos enviados devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

                return true;

         //Atendimento 19441
        }else if(id == R.id.sincronizar_pedidos){
            if(verificaConexao()){

                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Sincronizar todos os pedidos abertos");
                //define a mensagem
                builder.setMessage("Deseja mesmo enviar online todos os seus pedidos em aberto?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            LoadingAsyncTodosPedidos async_TodosPedidos = new LoadingAsyncTodosPedidos();
                            async_TodosPedidos.execute();
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possível realizar a sincronização de todos os seus pedidos em aberto");
                        }
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }else{
                MensagemUtil.addMsg(Pedidos.this, "Por favor se conecte à internet para sincronizar todos os seus pedidos em aberto");
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------------------------------------------------------------------------

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

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            Toast.makeText(getApplicationContext(), "Pressione o botão Voltar novamente para fechar o aplicativo.", Toast.LENGTH_LONG).show();
            /*toast = Toast.makeText(getApplicationContext(), "Pressione o Botão Voltar novamente para fechar o Aplicativo.", Toast.LENGTH_LONG);
            toast.show();*/
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //super.onBackPressed();
        }
    }

     @SuppressWarnings("StatementWithEmptyBody")
     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         // Handle navigation view item clicks here.
         int id = item.getItemId();

         if (id == R.id.nav_clientes) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, HomeActivity.class);
             startActivity(secondActivity);
             // Handle the camera action
         } else if (id == R.id.nav_pedidos) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Pedidos.class);
             startActivity(secondActivity);


         } else if (id == R.id.nav_produtos) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Produtos.class);
             secondActivity.putExtra("selecaoProdutos", "N");
             startActivity(secondActivity);

         } else if (id == R.id.nav_opcoes) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Opcoes.class);
             startActivity(secondActivity);

         } else if (id == R.id.nav_visaogeral) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, VisaoGeralNova.class);
             startActivity(secondActivity);

         }
         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
         return true;
     }


    public void FU_CalculaPrazoSincronizacao(){

        BancoController crud = new BancoController(getBaseContext());

        String dateStart = crud.carregaDtSincronizacao();
        String dateStop = getDateTime();

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {

            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diferencaHoras = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*Date dateCadguia = format.parse(dateStart);
            Calendar dataInicial = Calendar.getInstance();
            dataInicial.setTime(dateCadguia);



            long diferenca = System.currentTimeMillis() - dataInicial.getTimeInMillis();
            long diferencaSeg = diferenca / 1000;    //DIFERENCA EM SEGUNDOS
            long diferencaMin = diferenca / (60 * 1000);    //DIFERENCA EM MINUTOS
            long diferencaHoras = diferenca / (60 * 60 * 1000);    // DIFERENCA EM HORAS*/

            builder = new AlertDialog.Builder(this);

            if(diffDays > 2 && diffDays < 3){
                MensagemUtil.addMsg(Pedidos.this, "Você não fez nenhuma sincronização nas últimas 48 horas, necessária a realização da sincronização.");
            }else if(diffDays >= 3){

                //define o titulo
                builder.setTitle("ATENÇÃO!");
                //define a mensagem
                builder.setMessage("Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização. É necessária conexão com a internet para a realização da sincronização.");

                //define um botão como positivo
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        try {
                            if(verificaConexao()) {
                                new LoadingAsyncOpcoesClientesProdutos().execute();
                            }else {
                                Intent secondActivity;
                                secondActivity = new Intent(Pedidos.this, Pedidos.class);
                                startActivity(secondActivity);
                            }
                        }catch (Exception e){
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização devido à seguinte situação:" + e.getMessage().toString());
                        }
                    }
                });
                //define um botão como negativo.
                /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                    }
                });*/
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class LoadingAsyncOpcoes extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Pedidos.this);
        BancoController crud = new BancoController(getBaseContext());
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Enviando o pedido online...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(FU_EnviarPedido(codigo_lista)) {
                validou = "S";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(validou.equals("S")){
                BancoController crud = new BancoController(getBaseContext());
                crud = new BancoController(getBaseContext());
                crud.alteraSituacaoPedido(codigo_lista, "ENVIADO");
                MensagemUtil.addMsg(Pedidos.this, "Pedido enviado com sucesso!");
                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                startActivity(intent);
            }else {
                MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.");
            }
        }
    }


    private class LoadingAsyncOpcoesClientesProdutos extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Pedidos.this);
        BancoController crud = new BancoController(getBaseContext());
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando clientes e produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            FU_Sincronizar() ;
            validou = "S";


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(validou.equals("S")){
                BancoController crud = new BancoController(getBaseContext());
                crud = new BancoController(getBaseContext());
                crud.alteraSituacaoPedido(codigo_lista, "ENVIADO");
                MensagemUtil.addMsg(Pedidos.this, "Clientes e produtos sincronizados com sucesso!");
                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                startActivity(intent);
            }else {
                MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização dos clientes e produtos. Favor verificar a conexão com a internet.");
            }
        }
    }

    /*Funções referentes ao atendimento 19441
    A primeira função ira mostrar uma mensagem na tela informando que a sincronização de todos os pedidos esta sendo feita
    e chamará a função que realizará o envio dos pedidos.
    A segunda função irá fazer uma varredura no banco de dados e verificar se existem atendimentos em aberto
    caso existam então o aplicativo irá realizar o envio de todos os pedidos
    */

    private class LoadingAsyncTodosPedidos extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialogPedidos;
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialogPedidos = ProgressDialog.show(Pedidos.this, "Sincronizando todos os pedidos", "Sincronizando todos pedidos em aberto...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (fuSincronizarTodosPedidosAbertos()) {
                validou = "S";

            } else {
                validou = "N";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialogPedidos.dismiss();

            if (validou.equals("N")) {
                MensagemUtil.addMsg(Pedidos.this, "Não foi encontrado nenhum pedido em aberto para sincronização");
            }else{
                MensagemUtil.addMsg(Pedidos.this, "Todos os seus pedidos que estavam em aberto foram sincronizados com o servidor");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    protected boolean fuSincronizarTodosPedidosAbertos(){

        if(ctl_Pedidos.fuPossuiPedidosAbertos()){
            Cursor rs_Pedidos = ctl_Pedidos.rs_Pedido;

            if(sync_Pedidos.FU_EnviarTodosPedidos(rs_Pedidos)){
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }

    }

    //Função referente ao atendimento 19449 de duplicação do pedido
    protected void suCliqueLongoLista(String numPedido, String fgSituacao){

        final String vf_NumPedido = numPedido;
        final String vf_FgSituacao = fgSituacao;
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();

        itens.add("Consultar");
        itens.add("Enviar Online");
        itens.add("Cancelar Pedido");
        itens.add("Duplicar Pedido");
        itens.add("Excluir");
        itens.add("Sair");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), R.layout.item_alerta, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(Pedidos.this);
        builder.setTitle("O que você deseja fazer com o pedido nº" + vf_NumPedido + "? ");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0) {
                    Intent intent = new Intent(Pedidos.this, ManutencaoPedidos.class);
                    intent.putExtra("operacao", "A");
                    intent.putExtra("codigo", vf_NumPedido);
                    startActivityForResult(intent, 1);
                } else if (arg1 == 1) {

                    //MensagemUtil.addMsg(Pedidos.this, "Foi clicado para enviar online");
                    if(vf_FgSituacao.equals("ENVIADO")) {
                        MensagemUtil.addMsg(Pedidos.this, "Pedido já foi enviado para o sistema online.");
                    }else if(vf_FgSituacao.equals("CANCELADO")){
                        MensagemUtil.addMsg(Pedidos.this, "Pedido não pode ser enviado, pois o pedido foi cancelado!");
                    }else {
                        try {
                            if (FU_ConsisteEnviarOnline(vf_NumPedido)) {
                                alerta.dismiss();
                                codigo_lista = vf_NumPedido;

                                new LoadingAsyncOpcoes().execute();

                            }
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar o envio do pedido");
                        }
                    }
                } else if (arg1 == 2) {
                    //MensagemUtil.addMsg(Pedidos.this, "Foi clicado para cancelar pedido");
                    //alerta.dismiss();
                    if(vf_FgSituacao.equals("ENVIADO")) {
                        MensagemUtil.addMsg(Pedidos.this, "Pedido não pode ser cancelado, pois já foi enviado para o sistema online.");
                    }else {
                        BancoController crud = new BancoController(getBaseContext());
                        try {
                            crud.alteraSituacaoPedido(vf_NumPedido, "CANCELADO");
                            MensagemUtil.addMsg(Pedidos.this, "Pedido cancelado com sucesso!");
                            Intent intent = new Intent(Pedidos.this, Pedidos.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }
                    }
                } else if (arg1 == 3) {

                    AlertDialog al_DuplicarPedido;
                    AlertDialog.Builder bu_DuplicarPedido;

                    bu_DuplicarPedido = new AlertDialog.Builder(Pedidos.this);

                    //define o titulo
                    bu_DuplicarPedido.setTitle("Duplicar pedido " + vf_NumPedido);
                    //define a mensagem
                    bu_DuplicarPedido.setMessage("Deseja mesmo duplicar o pedido " + vf_NumPedido + "?");

                    //define um botão como positivo
                    bu_DuplicarPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            CL_Pedidos cl_Pedido = new CL_Pedidos();
                            cl_Pedido.setNumPedido(vf_NumPedido);

                            CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedido);

                            if(ctl_Pedidos.fuCarregarPedido()){
                                try {
                                    String vf_NumPedidoOriginal = cl_Pedido.getNumPedido();
                                    if(ctl_Pedidos.fuDuplicarPedido()){

                                        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                                        cl_ItemPedido.setNumPedido(cl_Pedido.getNumPedido());

                                        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

                                        if(ctl_ItemPedido.fuDuplicarItensPedidoDuplicado(vf_NumPedidoOriginal)) {
                                            MensagemUtil.addMsg(Pedidos.this, "Pedido duplicado com sucesso!");
                                            Intent intent = new Intent(Pedidos.this, Pedidos.class);
                                            startActivity(intent);
                                        }else{
                                            MensagemUtil.addMsg(Pedidos.this, "Não foi possível duplicar os itens do pedido!");
                                        }
                                    }else{
                                        MensagemUtil.addMsg(Pedidos.this, "Não foi possível duplicar o pedido!");
                                    }
                                }catch (Exception e){
                                    MensagemUtil.addMsg(Pedidos.this, "Não foi possivel duplicar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                                }

                            }
                        }
                    });
                    //define um botão como negativo.
                    bu_DuplicarPedido.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    //cria o AlertDialog
                    al_DuplicarPedido = bu_DuplicarPedido.create();
                    //Exibe
                    al_DuplicarPedido.show();

                } else if (arg1 == 4) {
                    //MensagemUtil.addMsg(Pedidos.this, "Foi clicado para excluir pedido");
                    BancoController crud = new BancoController(getBaseContext());
                    try {
                        crud.deletaPedido(Integer.parseInt(vf_NumPedido));
                        MensagemUtil.addMsg(Pedidos.this, "Pedido excluido com sucesso!");
                        Intent intent = new Intent(Pedidos.this, Pedidos.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        MensagemUtil.addMsg(Pedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                    }
                } else if (arg1 == 5) {
                    alerta.dismiss();
                }

            }
        });

        alerta = builder.create();
        alerta.show();

    }

}
