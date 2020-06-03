package br.comercioexpress.plano;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

import classes.CL_Usuario;
import controllers.CTL_Usuario;
import models.CriaBanco;
import sync.SYNC_Clientes;
import sync.SYNC_Configuracao;
import sync.SYNC_Produtos;
import sync.SYNC_Usuario;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    Funcoes funcoes = new Funcoes();

    String usuarioString;
    String senhaString;

    EditText tb_emailLogin, tb_senhaLogin;

    Button btn_Logar;

    private long lastBackPressTime = 0;

    SYNC_Usuario sync_Usuario;
    Context vc_Context;

    String vc_Mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sync_Usuario = new SYNC_Usuario(getApplicationContext());
        vc_Context = getApplicationContext();

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
                        LoadingAsync async_Login = new LoadingAsync();
                        async_Login.execute();
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

        String sincronizou = "N";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Validando login e sincronizando clientes e produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (FU_SincronizarLogin()) {
                sincronizou = "S";
            } else {
                sincronizou = "N";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();

            if (sincronizou.equals("S")) {
                MensagemUtil.addMsg(LoginActivity.this, "Clientes e produtos sincronizados com sucesso!");

                Intent secondActivity;
                secondActivity = new Intent(LoginActivity.this, Filial.class);
                secondActivity.putExtra("operacao", "L");
                startActivity(secondActivity);

            } else {
                MensagemUtil.addMsg(LoginActivity.this, vc_Mensagem);
            }
        }
    }

    protected boolean FU_SincronizarLogin(){

        if(!sync_Usuario.FU_ValidaLogin(usuarioString, senhaString)){
            vc_Mensagem = "Usuário ou senha incorretos!";
            return false;
        }

        if(!sync_Usuario.FU_BuscarCdClienteBanco()){
            vc_Mensagem = "Não foi possivel realizar a sincronização do código do cliente. Favor verificar a conexão com a internet e o banco de dados!";
            return false;
        }

        if (!sync_Usuario.FU_BuscaNmUsuarioSistema()) {
            vc_Mensagem = "Não foi possivel realizar a sincronização do usuário cadastrado no sistema. Favor verificar a conexão com a internet e o banco de dados!";
            return false;
        }

        if(!sync_Usuario.FU_BuscaCdVendedorDefault()){
            vc_Mensagem = "Não foi possivel realizar a sincronização do vendedor padrão do usuário. Favor verificar a conexão com a internet e o banco de dados!";
            return false;
        }

        SYNC_Configuracao sync_Configuracao = new SYNC_Configuracao(vc_Context);

        if(!sync_Configuracao.FU_SincronizarFgControlaEstoquePedido()){
            return false;
        }

        SYNC_Clientes sync_Clientes = new SYNC_Clientes(vc_Context);

        if(!sync_Clientes.FU_SincronizarTipoCliente()){
            vc_Mensagem = "Não foi possivel realizar a sincronização dos tipos de cliente. Favor verificar a conexão com a internet e o banco de dados!";
            return false;
        }

        if(!sync_Clientes.FU_SincronizarTodosClientesServidor()){
            return false;
        }

        SYNC_Produtos sync_Produtos = new SYNC_Produtos(vc_Context);

        if(!sync_Produtos.FU_SincronizarTodosProdutosServidor()){
            return false;
        }

        CL_Usuario cl_Usuario = new CL_Usuario();
        CTL_Usuario ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        ctl_Usuario.cl_Usuario.setDtUltimaSincronizacao(funcoes.getDateTime());

        if(!ctl_Usuario.fuAtualizarSincronizacao()){
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

