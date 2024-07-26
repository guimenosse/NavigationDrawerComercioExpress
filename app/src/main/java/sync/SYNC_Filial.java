package sync;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import br.comercioexpress.plano.BancoController;
import br.comercioexpress.plano.Funcoes;
import br.comercioexpress.plano.MySSLSocketFactory;
import classes.CL_Filial;
import classes.CL_FilialAPI;
import classes.CL_Produtos;
import classes.CL_ProdutosAPI;
import classes.CL_Usuario;
import controllers.CTL_Filial;
import controllers.CTL_Produtos;
import controllers.CTL_Usuario;
import models.MDL_Filial;

public class SYNC_Filial {

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;
    MDL_Filial mdl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    String bodyString = "";

    public SYNC_Filial(Context context){
        this.vc_Context = context;
        this.mdl_Filial = new MDL_Filial(context);
        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);
        cl_Usuario = ctl_Usuario.fuSelecionarUsuarioAPI();
        bodyString = ctl_Usuario.buildRequestBodyString(cl_Usuario);

    }

    public boolean FU_SincronizarFilialAPI(){
        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/SelecionarFilialExpress")
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                if(!mdl_Filial.fuDeletarFilial()){
                    return false;
                }

                // Parsing JSON to Java object using Gson
                Gson gson = new Gson();
                List<CL_FilialAPI> filiais = gson.fromJson(jsonResponse, new TypeToken<List<CL_FilialAPI>>(){}.getType());
                // Agora você pode adicionar os novos tipos de cliente ao seu banco de dados ou lista
                for (CL_FilialAPI filial : filiais) {

                    if (!filial.getNmFilial().equals("null") && !filial.getNmFilial().trim().equals("")) {
                        if(!mdl_Filial.fuInserirFilial(String.valueOf(filial.getCdFilial()), filial.getNmFilial(), "N", "N")){
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }

        return true;
    }


    public boolean FU_SincronizarAutorizacaoUsuarioAPI(){

        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/VerificarAutorizacao/PAR/CBFILIAL")
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                if (!jsonResponse.equals("null") && !jsonResponse.equals("") && jsonResponse.equals("true")) {
                    if (!mdl_Filial.fuAtualizarAutorizaTrocaFilial("S")) {
                        return false;
                    }
                }

            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }

        return true;
    }

    //Funções antigas do WebService em PHP
    public boolean FU_SincronizarFilial(){

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=filial";
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


                if(!mdl_Filial.fuDeletarFilial()){
                    return false;
                }

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("CdFilial").equals("null") && !jObject.getString("CdFilial").trim().equals("")) {
                        if(!mdl_Filial.fuInserirFilial(jObject.getString("CdFilial"), jObject.getString("NmFilial"), "N", "N")){
                            return false;
                        }
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
    public boolean FU_SincronizarAutorizacaoUsuario(){

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

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(ccm, p);
            String url = "https://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=autorizacao&cdsistema=PAR&cdfuncao=CBFILIAL&nmusuario=" + cl_Usuario.getNmUsuarioSistema().replace(" ", "espaco") + "";
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

                    if (!s.equals("null") && !s.equals("") && s.equals("true")) {
                        if(!mdl_Filial.fuAtualizarAutorizaTrocaFilial("S")){
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
