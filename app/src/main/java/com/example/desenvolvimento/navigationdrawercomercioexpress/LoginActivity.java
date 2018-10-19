package com.example.desenvolvimento.navigationdrawercomercioexpress;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private long lastBackPressTime = 0;

    int countCli;
    int timer;

    String usuarioString;
    String senhaString;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            usuarioString = mEmailView.getText().toString();
            senhaString = mPasswordView.getText().toString();

                if(usuarioString.equals("") || usuarioString == null){
                    mEmailView.setError("Usuário obrigatório!");
                }else if(senhaString.equals("") || senhaString == null){
                    mPasswordView.setError("Senha obrigatória!");
                }else {
                    if(verificaConexao()) {
                        new LoadingAsync().execute();
                    }else{
                        MensagemUtil.addMsg(LoginActivity.this, "É necessário conexão com a internet para entrar no aplicativo!");
                    }
                }

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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

        int TIMEOUT_MILLISEC = 10000;

        try {
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
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

        int TIMEOUT_MILLISEC = 10000;
        try {

            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
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

                        crud.inserirCliente(jObject.getString("CdCliente"), sRzSocial, sNmFantasia, jObject.getString("Cep"), sEndereco, jObject.getString("NumEndereco"), sComplemento, sBairro, jObject.getString("Uf"), sCidade, VA_Cgc, jObject.getString("InscEst"), VA_telefone.replace(" ", ""), VA_telefoneAdicional.replace(" ", ""), VA_fax.replace(" ", ""), sNmContatoCliente, jObject.getString("Email"), sNmTipo, jObject.getString("CdVendedor"), jObject.getString("FgTipoPessoa"), jObject.getString("DtUltAlteracao"), jObject.getString("DtCadastro"), "S", sObsCliente, jObject.getString("Classificacao"), jObject.getString("Fidelidade"));
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

            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
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


                        crud.inserirProdutoFilial(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, jObject.getString("DescontoA"), jObject.getString("DescontoB"), jObject.getString("DescontoC"), jObject.getString("DescontoD"), jObject.getString("DescontoE"), jObject.getString("DescontoFidelidade"));


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

            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
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

            HttpParams p = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
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
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
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

            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
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

