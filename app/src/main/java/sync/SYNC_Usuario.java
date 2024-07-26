package sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.comercioexpress.plano.Funcoes;
import br.comercioexpress.plano.MySSLSocketFactory;
import classes.CL_Filial;
import classes.CL_Usuario;
import classes.CL_UsuarioAPI;
import controllers.CTL_Filial;
import controllers.CTL_Usuario;
import models.CriaBanco;
import models.MDL_Filial;
import models.MDL_Usuario;

public class SYNC_Usuario {

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;
    MDL_Usuario mdl_Usuario;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;



    public SYNC_Usuario(Context context){
        this.vc_Context = context;
        this.mdl_Usuario = new MDL_Usuario(context);
        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();

        //ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(context, cl_Filial);
        ctl_Filial.fuBuscaCdFilialSelecionada();
    }

    public boolean FU_BuscaUsuarioCadastradoAPI(String usuario, String senha){
        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/SelecionarBancoCliente/" + usuario + "/" + senha)
                    .method("GET", null)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();


            if(response.code() == 200) {

                String jsonResponse = response.body().string();
                //System.out.println("Response JSON: " + jsonResponse);

                // Parsing JSON to Java object using Gson
                Gson gson = new Gson();
                CL_UsuarioAPI classTestUsuario = gson.fromJson(jsonResponse, CL_UsuarioAPI.class);

                // Displaying the parsed data
                int vf_CdUsuario = classTestUsuario.cdUsuario;
                String vf_Usuario = classTestUsuario.usuario;
                String vf_Senha = classTestUsuario.senha;
                int vf_CdCliente = classTestUsuario.cdCliente;
                String vf_FgBI = classTestUsuario.fgBI;
                String vf_FgMaster = classTestUsuario.fgMaster;
                String vf_NmUsuarioSistema = classTestUsuario.nmUsuarioSistema;
                String vf_Ip = classTestUsuario.ip;
                String vf_UsuarioSQL = classTestUsuario.usuarioSQL;
                String vf_SenhaSQL = classTestUsuario.senhaSQL;
                String vf_NmBanco = classTestUsuario.nmBanco;
                int vf_CdVendedorDefault = classTestUsuario.cdVendedorDefault;

                cl_Usuario.setCdUsuario(String.valueOf(vf_CdUsuario));
                cl_Usuario.setUsuario(vf_Usuario);
                cl_Usuario.setSenha(vf_Senha);
                cl_Usuario.setCdClienteBanco(String.valueOf(vf_CdCliente));
                cl_Usuario.setNmUsuarioSistema(vf_NmUsuarioSistema);
                cl_Usuario.setIp(vf_Ip);
                cl_Usuario.setUsuarioSQL(vf_UsuarioSQL);
                cl_Usuario.setSenhaSQL(vf_SenhaSQL);
                cl_Usuario.setNmBanco(vf_NmBanco);
                cl_Usuario.setCdVendedorDefault(String.valueOf(vf_CdVendedorDefault));

                if (mdl_Usuario.fuSalvarUsuarioAPI(cl_Usuario)) {
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    //Funções antigas do WebService em PHP
    public boolean FU_ValidaLogin(String usuario, String senha){

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
                    "WebService.php?user=740&format=json&num=10&method=login&usuario=" + usuario + "&senha=" + senha + "";

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

                        cl_Usuario.setUsuario(usuario);
                        cl_Usuario.setSenha(senha);

                        if (mdl_Usuario.fuInserirLogin(usuario, senha)) {
                            return true;
                        }else{
                            return false;
                        }

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

    public boolean FU_BuscarCdClienteBanco(){

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


            HttpConnectionParams.setConnectionTimeout(p, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            String url = "https://www.planosistemas.com.br/" +
                    "WebService.php?user=740&format=json&num=10&method=banco&usuario=" + cl_Usuario.getUsuario() + "&senha=" + cl_Usuario.getSenha() + "";
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

                    if (!jObject.getString("CdCliente").equals("null") && !jObject.getString("CdCliente").trim().equals("")) {
                        mdl_Usuario.fuInserirCdClienteBanco(jObject.getString("CdCliente"));
                        cl_Usuario.setCdClienteBanco(jObject.getString("CdCliente"));
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

    public boolean FU_BuscaNmUsuarioSistema(){

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
                    "WebService.php?user=740&format=json&num=10&method=nmusuariosistema&usuario=" + cl_Usuario.getUsuario() + "&senha=" + cl_Usuario.getSenha() + "";
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

                    if (!jObject.getString("NmUsuarioSistema").equals("null")) {
                        cl_Usuario.setNmUsuarioSistema(jObject.getString("NmUsuarioSistema"));
                        if(mdl_Usuario.fuInserirNmUsuarioSistema(jObject.getString("NmUsuarioSistema"))){
                            return true;
                        }else{
                            return false;
                        }
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

    public boolean FU_BuscaCdVendedorDefault(){

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=vendedor&nmusuario=" + cl_Usuario.getNmUsuarioSistema().replace(" ", "espaco") + "";
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

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("CdVendedorDefalt").equals("null")) {
                        String vf_CdVendedorDefault = jObject.getString("CdVendedorDefalt");
                        int vf_IndexPontoCdVendedor = vf_CdVendedorDefault.indexOf(".");
                        vf_CdVendedorDefault = vf_CdVendedorDefault.substring(0, vf_IndexPontoCdVendedor).replace(".", "");

                        cl_Usuario.setCdVendedorDefault(vf_CdVendedorDefault);
                        if(mdl_Usuario.fuInserirCdVendedorDefault(jObject.getString("CdVendedorDefalt"))){
                            return true;
                        }else{
                            return false;
                        }
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
}
