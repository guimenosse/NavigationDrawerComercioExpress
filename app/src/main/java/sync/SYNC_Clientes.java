package sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.desenvolvimento.navigationdrawercomercioexpress.BancoController;
import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;
import com.example.desenvolvimento.navigationdrawercomercioexpress.Funcoes;
import com.example.desenvolvimento.navigationdrawercomercioexpress.MySSLSocketFactory;

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

import classes.CL_Clientes;
import classes.CL_Pedidos;
import controllers.CTL_Clientes;
import models.MDL_Clientes;
import models.MDL_Usuario;

public class SYNC_Clientes {

    CL_Clientes cl_Clientes;
    CTL_Clientes ctl_Clientes;
    MDL_Clientes mdl_Clientes;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    protected String vc_CdClienteBanco = "";

    public SYNC_Clientes(Context context){

        this.vc_Context = context;
        this.mdl_Clientes = new MDL_Clientes(context);
        this.funcoes = new Funcoes();

        MDL_Usuario mdl_Usuario = new MDL_Usuario(context);
        this.vc_CdClienteBanco = mdl_Usuario.fuSelecionarCdClienteBanco();
    }

    public String FU_SincronizarClientePedido(CL_Clientes cl_Clientes){

        //Ao enviar o cliente para o servidor deve ser retornado o codigo deste cliente salvo na base de dados
        String vf_CdClienteServidor = "";

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
                    "WebService2.php?user=" + vc_CdClienteBanco
                    + "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + cl_Clientes.getNomeRzSocial().replace(" ", "espaco")
                    + "&nmfantasia=" + cl_Clientes.getNomeFantasia().replace(" ", "espaco") +
                    "&cep=" + cl_Clientes.getCep() + "&endereco=" + cl_Clientes.getEndereco().replace(" ", "espaco")
                    + "&numero=" + cl_Clientes.getNumEndereco() + "&complemento=" + cl_Clientes.getComplemento().replace(" ", "espaco") +
                    "&bairro=" + cl_Clientes.getBairro().replace(" ", "espaco") + "&uf=" + cl_Clientes.getUf()
                    + "&cidade=" + cl_Clientes.getCidade().replace(" ", "espaco") + "&tipopessoa=" + cl_Clientes.getTipoPessoa() +
                    "&cgc=" + cl_Clientes.getCpfCnpj() + "&telefone=" + cl_Clientes.getTelefone() + "&telefoneadicional=" + cl_Clientes.getTelefoneAdicional()
                    + "&fax=" + cl_Clientes.getFax() + "&classificacao=" + cl_Clientes.getClassificacao() +
                    "&contato=" + cl_Clientes.getNomeContato().replace(" ", "espaco") + "&email=" + cl_Clientes.getEmail()
                    + "&vendedor=" + cl_Clientes.getVendedor() + "&tipocliente=" + cl_Clientes.getTipoCliente().replace(" ", "espaco")
                    + "&dtcadastro=" + cl_Clientes.getDtCadastro().replace(" ", "espaco") + "" +
                    "&obscliente=" + cl_Clientes.getObservacao().replace(" ", "espaco") + "";
            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);

                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);

                    if (!jObject.getString("CdCliente").equals("null")) {

                        cl_Clientes.setCdCliente(jObject.getString("CdCliente"));
                        CTL_Clientes ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);

                        if(ctl_Clientes.fuAlteraClientePedido()){
                            vf_CdClienteServidor = cl_Clientes.getCdCliente();
                        }

                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return "";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return "";
            }

        } catch (Throwable t) {
            return "";
        }

        return vf_CdClienteServidor;
    }
}
