package sync;

import android.content.Context;
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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.comercioexpress.plano.Funcoes;
import br.comercioexpress.plano.MySSLSocketFactory;
import classes.CL_Configuracao;
import classes.CL_ConfiguracaoAPI;
import classes.CL_Filial;
import classes.CL_Usuario;
import classes.CL_UsuarioAPI;
import controllers.CTL_Configuracao;
import controllers.CTL_Usuario;
import models.MDL_Produtos;

public class SYNC_Configuracao {

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Configuracao cl_Configuracao;
    CTL_Configuracao ctl_Configuracao;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    String bodyString = "";

    public SYNC_Configuracao(Context context){

        this.vc_Context = context;
        this.cl_Configuracao = new CL_Configuracao();

        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        cl_Usuario = ctl_Usuario.fuSelecionarUsuarioAPI();
        bodyString = ctl_Usuario.buildRequestBodyString(cl_Usuario);

    }

    public boolean FU_SincronizarConfiguracoesAPI(){

        try {

            OkHttpClient client = new OkHttpClient();

            // Construir a string e logá-la


            // Criar o RequestBody
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/SelecionarConfiguracaoCliente")
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                // Parsing JSON to Java object using Gson
                Gson gson = new Gson();
                CL_ConfiguracaoAPI cl_configuracaoAPI = gson.fromJson(jsonResponse, CL_ConfiguracaoAPI.class);

                cl_Configuracao.setFgControlaEstoquePedido(cl_configuracaoAPI.fgControlaEstoquePedido);
                cl_Configuracao.setFgPrecoIndividualizado(cl_configuracaoAPI.precoIndividualizado);
                ctl_Configuracao = new CTL_Configuracao(vc_Context, cl_Configuracao);

                if(!ctl_Configuracao.fuAtualizarConfiguracoes()){
                    return false;
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
    public boolean FU_SincronizarFgControlaEstoquePedido(){
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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=fgcontrolaestoquepedido";
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

                MDL_Produtos mdl_Produtos = new MDL_Produtos(vc_Context);

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);
                    try {

                        if (!jObject.getString("FgControlaEstoquePedido").equals("null")) {
                            cl_Configuracao.setFgControlaEstoquePedido(jObject.getString("FgControlaEstoquePedido"));
                            ctl_Configuracao = new CTL_Configuracao(vc_Context, cl_Configuracao);

                            if(!ctl_Configuracao.fuAtualizarFgControlaEstoquePedido()){
                                return false;
                            }
                        }
                    }catch(Exception e3) {
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

    public String removeAccent(final String str) {
        String strNoAccent = Normalizer.normalize(str, Normalizer.Form.NFD);
        strNoAccent = strNoAccent.replaceAll("[^\\p{ASCII}]", "");
        return strNoAccent;
    }
}
