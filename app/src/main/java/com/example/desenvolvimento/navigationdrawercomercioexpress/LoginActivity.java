package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import org.apache.http.client.methods.HttpGet;
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
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    int countCli;

    String usuarioString;
    String senhaString;

    EditText tb_emailLogin, tb_senhaLogin;

    Button btn_Logar;

    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Set up the login form.
        tb_emailLogin = (EditText)findViewById(R.id.tb_emailLogin);
        tb_senhaLogin = (EditText) findViewById(R.id.tb_senhaLogin);
        btn_Logar = (Button) findViewById(R.id.btn_Logar);

        btn_Logar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            usuarioString = tb_emailLogin.getText().toString();
            senhaString = tb_senhaLogin.getText().toString();

                if(usuarioString.equals("") || usuarioString == null){
                    tb_emailLogin.setError("Usuário obrigatório!");
                }else if(senhaString.equals("") || senhaString == null){
                    tb_senhaLogin.setError("Senha obrigatória!");
                }else {
                    if(verificaConexao()) {
                        new LoadingAsync().execute();
                    }else{
                        MensagemUtil.addMsg(LoginActivity.this, "É necessário conexão com a internet para entrar no aplicativo!");
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            Toast.makeText(getApplicationContext(), "Pressione o botão Voltar novamente para fechar o aplicativo.", Toast.LENGTH_LONG).show();
            this.lastBackPressTime = System.currentTimeMillis();

        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class LoadingAsync extends AsyncTask<Void, Void, Void>{
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

        String validou = "N", validouBanco = "N", validouUsuarioSistema = "N", validouVendedor = "N", validouTipoCliente = "N", sincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Validando login e sincronizando clientes e produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(FU_ValidaLogin()){
                validou = "S";

                BancoController crud = new BancoController(getBaseContext());
                crud.insereLogin(usuarioString, senhaString);

                if(FU_BuscaCdClienteBanco(usuarioString, senhaString)) {
                    validouBanco = "S";
                    if(FU_BuscaUsuarioSistema(usuarioString, senhaString)){
                        validouUsuarioSistema = "S";
                        if(FU_BuscaVendedor(usuarioString, senhaString)){
                            validouVendedor = "S";
                            if(FU_BuscaTipoCliente()) {
                                validouTipoCliente = "S";
                                if (FU_Sincronizar()) {
                                    sincronizou = "S";

                                    Intent secondActivity;
                                    secondActivity = new Intent(LoginActivity.this, Filial.class);
                                    secondActivity.putExtra("operacao", "L");
                                    startActivity(secondActivity);
                                } else {
                                    sincronizou = "N";
                                }
                            }else{
                                validouTipoCliente = "N";
                            }
                        }else{
                            validouVendedor = "N";
                        }
                    }else{
                        validouUsuarioSistema = "N";
                    }
                }else{
                    validouBanco = "N";
                }
            }else{
                validou = "N";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if(validou.equals("S")) {
                if(validouBanco.equals("S")){
                    if(validouUsuarioSistema.equals("S")){
                        if(validouVendedor.equals("S")) {
                            if(validouTipoCliente.equals("S")) {
                                if (sincronizou.equals("S")) {
                                    MensagemUtil.addMsg(LoginActivity.this, "Clientes e produtos sincronizados com sucesso!");
                                } else {
                                    MensagemUtil.addMsg(LoginActivity.this, "Não foi possivel realizar a sincronização de clientes e produtos. Favor verificar a conexão com a internet e o banco de dados!");
                                }
                            }else{
                                MensagemUtil.addMsg(LoginActivity.this, "Não foi possivel realizar a sincronização dos tipos de cliente. Favor verificar a conexão com a internet e o banco de dados!");
                            }
                        }else{
                            MensagemUtil.addMsg(LoginActivity.this, "Não foi possivel realizar a sincronização do vendedor padrão do usuário. Favor verificar a conexão com a internet e o banco de dados!");
                        }
                    }else{
                        MensagemUtil.addMsg(LoginActivity.this, "Não foi possivel realizar a sincronização do usuário cadastrado no sistema. Favor verificar a conexão com a internet e o banco de dados!");
                    }
                }else{
                    MensagemUtil.addMsg(LoginActivity.this, "Não foi possivel realizar a sincronização do código do cliente. Favor verificar a conexão com a internet e o banco de dados!");
                }

            }else{
                MensagemUtil.addMsg(LoginActivity.this, "Usuário ou senha incorretos!");
            }
        }
    }

    protected boolean FU_ValidaLogin(){

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 20000;

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
            //httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("SSLSocketFactory", SSLSocketFactory.getSocketFactory(), 443));
            String url = "https://www.planosistemas.com.br/" +
                    "WebService.php?user=740&format=json&num=10&method=login&usuario=" + usuarioString + "&senha=" + senhaString + "";

            HttpPost httppost = new HttpPost(url);

            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


                if(jArray.length() == 0){
                    return false;
                }else {
                    for (int i = 0; i < jArray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject e = jArray.getJSONObject(i);
                        String s = e.getString("post");
                        JSONObject jObject = new JSONObject(s);

                        return true;

                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    protected boolean FU_Sincronizar(){

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 50000;
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
                url = "https://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientenovo";
            }else {
                url = "https://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=clientevendedor&cdvendedor=" + crud.selecionaVendedor();
            }
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
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

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
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
            String url = "https://www.planosistemas.com.br/" +
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

                    if (!s2.equals("null")) {
                        String VA_Desconto = "0";
                        if(jObject.getString("PercDescMaxVendedor").equals("null")){
                            VA_Desconto = "0";
                        }else if(Double.parseDouble(jObject.getString("PercDescMaxVendedor")) > 0) {
                            VA_Desconto = jObject.getString("PercDescMaxVendedor");
                        }else if(Double.parseDouble(jObject.getString("DescCategoria")) > 0) {
                            VA_Desconto = jObject.getString("DescCategoria");
                        }else if(Double.parseDouble(jObject.getString("DescDepartamento")) > 0) {
                            VA_Desconto = jObject.getString("DescDepartamento");
                        }

                        //crud.atualizarDescontos(jObject.getString("CdProduto"), jObject.getString("VlVenda"), VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);
                        //crud.inserirProdutoFilial(jObject.getString("CdProduto"), sDescricao, jObject.getString("EstAtual"), jObjectPrecoFilial.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);


                        crud.inserirProdutoFilial(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("VlAtacado"), jObject.getString("DtUltAlteracao"), VA_Desconto, jObject.getString("DescontoA"), jObject.getString("DescontoB"), jObject.getString("DescontoC"), jObject.getString("DescontoD"), jObject.getString("DescontoE"), jObject.getString("DescontoFidelidade"));


                        //crud.inserirProduto(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto);

                    }

                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
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

    public boolean FU_BuscaUsuarioSistema(String usuario, String senha){
        BancoController crud = new BancoController(getBaseContext());

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
            String url = "https://www.planosistemas.com.br/" +
                    "WebService.php?user=740&format=json&num=10&method=nmusuariosistema&usuario=" + usuarioString + "&senha=" + senhaString + "";
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

                countCli = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("NmUsuarioSistema").equals("null")) {
                        crud.insereNmUsuarioSistema(jObject.getString("NmUsuarioSistema"));
                        try {
                            Cursor cursorLogin = crud.carregaDadosTabelaLogin();
                            String usuarioBanco = cursorLogin.getString(cursorLogin.getColumnIndex(CriaBanco.USUARIOBANCO));
                            String senhaBanco = cursorLogin.getString(cursorLogin.getColumnIndex(CriaBanco.SENHABANCO));
                            String vendedordefalt = cursorLogin.getString(cursorLogin.getColumnIndex(CriaBanco.CDVENDEDORDEFAULT));
                            String cdclientebanco = cursorLogin.getString(cursorLogin.getColumnIndex(CriaBanco.CDCLIENTEBANCO));
                            String VA_NmUsuarioSistema = cursorLogin.getString(cursorLogin.getColumnIndex(CriaBanco.NMUSUARIOSISTEMA));
                            String Ola = "";
                        }catch (Exception e2){
                            e2.printStackTrace();
                        }
                        //
                    }


                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public boolean FU_BuscaVendedor(String usuario, String senha){
        BancoController crud = new BancoController(getBaseContext());

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
            String url = "https://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=vendedor&nmusuario=" + crud.selecionarNmUsuarioSistema().replace(" ", "espaco") + "";
            HttpPost httppost = new HttpPost(url);

            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse9
                 JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                countCli = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("CdVendedorDefalt").equals("null")) {
                        crud.insereVendedor(jObject.getString("CdVendedorDefalt"));
                        String VA_CdVendedorBanco = crud.selecionaVendedor();
                        String VA_CdVendedor = jObject.getString("CdVendedorDefalt");
                    }

                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public boolean FU_BuscaCdClienteBanco(String usuario, String senha){

        BancoController crud = new BancoController(getBaseContext());

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
            String url = "https://www.planosistemas.com.br/" +
                    "WebService.php?user=740&format=json&num=10&method=banco&usuario=" + usuarioString + "&senha=" + senhaString + "";
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

                countCli = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("CdCliente").equals("null") && !jObject.getString("CdCliente").trim().equals("")) {
                        crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        //
                    }else{
                        return false;
                    }

                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public boolean FU_BuscaTipoCliente(){

        BancoController crud = new BancoController(getBaseContext());

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
            String url = "https://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=tipocliente";
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                countCli = 0;
                crud.deletaTipoCliente();
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    String s2 = e.getString("post2");
                    //JSONObject jObject = new JSONObject(s);

                    if (!s2.equals("null") && !s2.trim().equals("")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        String VA_NmTipo = s2;
                        //VA_NmTipo.replace("\\u00c7", "Ç");
                        crud.insereTipoCliente(s, VA_NmTipo);
                        //
                    }

                }


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        } catch (Throwable t) {
            return false;
        }
        return true;
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


}

