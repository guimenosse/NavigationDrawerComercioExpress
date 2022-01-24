package sync;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import classes.CL_Produtos;
import classes.CL_Usuario;
import controllers.CTL_Produtos;
import controllers.CTL_Usuario;
import models.CriaBanco;
import br.comercioexpress.plano.Funcoes;
import br.comercioexpress.plano.MySSLSocketFactory;

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

import classes.CL_Clientes;
import classes.CL_Filial;
import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import controllers.CTL_Clientes;
import controllers.CTL_Filial;
import controllers.CTL_ItemPedido;
import controllers.CTL_Pedidos;
import models.MDL_Pedidos;
import models.MDL_Usuario;

public class SYNC_Pedidos extends AppCompatActivity {

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;
    MDL_Pedidos mdl_Pedidos;

    CL_Filial cl_Filial;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;


    public SYNC_Pedidos(Context context){

        this.vc_Context = context;
        this.mdl_Pedidos = new MDL_Pedidos(context);
        this.funcoes = new Funcoes();

        cl_Usuario = new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(context, cl_Filial);
        ctl_Filial.fuBuscaCdFilialSelecionada();
    }

    public boolean FU_EnviarPedido(CL_Pedidos cl_Pedido){

        try {

            cl_Pedidos = cl_Pedido;

            cl_Pedidos.setObsPedido("  CONDIÇÃO DE PAGAMENTO INFORMADA: " + cl_Pedidos.getCondPgto().replace(" ", "espaco") + "pulalinha" + cl_Pedidos.getObsPedido());
            cl_Pedidos.setObsPedido(cl_Pedidos.getObsPedido().replace(" ", "espaco"));


            String vf_CdCliente = cl_Pedidos.getCdCliente();
            try {
                int vf_IndexPontoCdCliente = cl_Pedido.getCdCliente().indexOf(".");
                vf_CdCliente = vf_CdCliente.substring(0, vf_IndexPontoCdCliente).replace(".", "");
                cl_Pedidos.setCdCliente(vf_CdCliente);
            }catch (Exception e_Cliente){
                cl_Pedidos.setCdCliente(vf_CdCliente);
            }

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco()
                    + "&format=json&num=10&method=inserirpedidofrete&vltotal=" + cl_Pedidos.getVlTotal().replace(".", ",")
                    + "&dtemissao=" + cl_Pedidos.getDtEmissao().replace(" ", "espaco") + ""
                    + "&cdvendedor=" + cl_Pedidos.getCdVendedor() + "&cdemitente=" + cl_Pedidos.getCdCliente()
                    + "&rzsocial=" + cl_Pedidos.getNomeRzSocial().replace(" ", "espaco")
                    + "&percdesconto=" + cl_Pedidos.getPercDesconto().replace(".", ",")
                    + "&vldesconto=" + cl_Pedidos.getVlDesconto().replace(".", ",") + ""
                    + "&vlfrete=" + cl_Pedidos.getVlFrete().replace(".", ",")
                    + "&condpgto=" + cl_Pedidos.getCondPgto().replace(" ", "espaco")
                    + "&obs=" + substituirCaracteres(cl_Pedidos.getObsPedido().replace(" ", "espaco").replace("\n", "pulalinha"))
                    + "&filial=" + cl_Filial.getCdFilial() + "";
            HttpPost httppost = new HttpPost(url);

            //Replace da observação
            //.replace("Ç", "C").replace("ç", "c")

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

                    if (!jObject.getString("NumPedido").equals("null")) {
                        String vf_NumPedidoServidor = jObject.getString("NumPedido");
                        int indexPonto = vf_NumPedidoServidor.indexOf(".");
                        String vf_NumPedidoServidorInt = "";
                        if(indexPonto == -1){
                            vf_NumPedidoServidorInt = vf_NumPedidoServidor;
                        }else{
                            vf_NumPedidoServidorInt = vf_NumPedidoServidor.substring(0, indexPonto).replace(".", "");
                        }

                        cl_Pedidos.setNumPedidoServidor(vf_NumPedidoServidorInt);
                        ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);

                        ctl_Pedidos.fuAlterarNumPedidoServidor();

                        if (FU_SincronizaItemPedido(vf_NumPedidoServidorInt)) {
                            FU_AlteraSituacaoPedido(vf_NumPedidoServidorInt);
                        } else {
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

    public boolean FU_EnviarTodosPedidos(Cursor rs_Pedido) {

        if (rs_Pedido != null) {
            while (!rs_Pedido.isAfterLast()) {

                cl_Pedidos = new CL_Pedidos();

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                        cl_Pedidos.setNumPedido(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setNumPedido("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null")
                            && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {

                        String vf_VlTotal = "";

                        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                        cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

                        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(vc_Context, cl_ItemPedido);

                        double vf_VlTotalDouble = 0;
                        if(ctl_ItemPedido.fuCarregaTodosItensPedido()){
                            Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;
                            while (!rs_ItemPedido.isAfterLast()) {
                                vf_VlTotalDouble = vf_VlTotalDouble + Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                                rs_ItemPedido.moveToNext();
                            }
                        }

                        if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).trim().equals("")) {
                            vf_VlTotalDouble = vf_VlTotalDouble + Double.parseDouble(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).replace(",", "."));
                        }

                        if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                            vf_VlTotalDouble = vf_VlTotalDouble - Double.parseDouble(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).replace(",", "."));
                        }

                        vf_VlTotal = String.format("%.2f", vf_VlTotalDouble);
                        cl_Pedidos.setVlTotal(vf_VlTotal);
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlTotal("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).trim().equals("")) {
                        cl_Pedidos.setDtEmissao(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setDtEmissao(funcoes.getDateTime());
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).trim().equals("")) {
                        cl_Pedidos.setCdVendedor(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCdVendedor("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                        cl_Pedidos.setCdCliente(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCdCliente("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).trim().equals("")) {
                        cl_Pedidos.setNomeRzSocial(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setNomeRzSocial("");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        cl_Pedidos.setPercDesconto(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                    } else {
                        cl_Pedidos.setPercDesconto("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setPercDesconto("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        cl_Pedidos.setVlDesconto(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    } else {
                        cl_Pedidos.setVlDesconto("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlDesconto("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).trim().equals("")) {
                        cl_Pedidos.setVlFrete(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)));
                    } else {
                        cl_Pedidos.setVlFrete("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlFrete("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")) {
                        cl_Pedidos.setCondPgto(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCondPgto("DINHEIRO");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)).trim().equals("")) {
                        cl_Pedidos.setObsPedido(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)).replace(" ", "espaco"));
                    } else {
                        cl_Pedidos.setObsPedido("espaco");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setObsPedido("espaco");
                }

                cl_Pedidos.setObsPedido(cl_Pedidos.getObsPedido() + "  CONDIÇÃO DE PAGAMENTO INFORMADA: " + cl_Pedidos.getCondPgto().replace(" ", "espaco") + "");
                cl_Pedidos.setObsPedido(cl_Pedidos.getObsPedido().replace(" ", "espaco"));

                try {
                    CL_Clientes cl_Cliente = new CL_Clientes();
                    cl_Cliente.setCdCliente(cl_Pedidos.getCdCliente());

                    CTL_Clientes ctl_Cliente = new CTL_Clientes(this.vc_Context, cl_Cliente);

                    if(ctl_Cliente.fuSelecionarClienteSincronizacao("cdCliente")){
                        if (cl_Cliente.getFgSincronizado().equals("N")) {
                            SYNC_Clientes sync_Clientes = new SYNC_Clientes(vc_Context);
                            if(sync_Clientes.FU_SincronizarClientePedido(cl_Cliente)){
                                cl_Pedidos.setCdCliente(cl_Cliente.getCdCliente());
                            }else{
                                mensagem = sync_Clientes.mensagem;
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    try {
                        CL_Clientes cl_Cliente = new CL_Clientes();
                        cl_Cliente.setCdCliente(cl_Pedidos.getCdCliente());

                        CTL_Clientes ctl_Cliente = new CTL_Clientes(this.vc_Context, cl_Cliente);

                        if(ctl_Cliente.fuSelecionarClienteSincronizacao("cpfCnpj")) {
                            if (cl_Cliente.getFgSincronizado().equals("N")) {
                                SYNC_Clientes sync_Clientes = new SYNC_Clientes(vc_Context);
                                if(sync_Clientes.FU_SincronizarClientePedido(cl_Cliente)){
                                    cl_Pedidos.setCdCliente(cl_Cliente.getCdCliente());
                                }else{
                                    mensagem = sync_Clientes.mensagem;
                                    return false;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
                    String url = "https://www.planosistemas.com.br/" +
                            "WebService2.php?user=" + cl_Usuario.getCdClienteBanco()
                            + "&format=json&num=10&method=inserirpedidofrete&vltotal=" + cl_Pedidos.getVlTotal()
                            + "&dtemissao=" + cl_Pedidos.getDtEmissao() + ""
                            + "&cdvendedor=" + cl_Pedidos.getCdVendedor() + "&cdemitente=" + cl_Pedidos.getCdCliente() + "&rzsocial=" + cl_Pedidos.getNomeRzSocial()
                            + "&percdesconto=" + cl_Pedidos.getPercDesconto() + "&vldesconto=" + cl_Pedidos.getVlDesconto() + ""
                            + "&vlfrete=" + cl_Pedidos.getVlFrete()
                            + "&condpgto=" + cl_Pedidos.getCondPgto().replace(" ", "espaco") + "&obs=" + cl_Pedidos.getObsPedido().replace("\n", "pulalinha")
                            + "&filial=" + cl_Filial.getCdFilial() + "";
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

                            if (!jObject.getString("NumPedido").equals("null")) {
                                String vf_NumPedidoServidor = jObject.getString("NumPedido");
                                int indexPonto = vf_NumPedidoServidor.indexOf(".");
                                String vf_NumPedidoServidorInt = "";
                                if(indexPonto == -1){
                                    vf_NumPedidoServidorInt = vf_NumPedidoServidor;
                                }else{
                                    vf_NumPedidoServidorInt = vf_NumPedidoServidor.substring(0, indexPonto).replace(".", "");
                                }
                                cl_Pedidos.setNumPedidoServidor(vf_NumPedidoServidorInt);
                                ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);

                                ctl_Pedidos.fuAlterarNumPedidoServidor();

                                if (FU_SincronizaItemPedido(vf_NumPedidoServidorInt)) {
                                    FU_AlteraSituacaoPedido(vf_NumPedidoServidorInt);

                                    cl_Pedidos.setFgSituacao("E");
                                    CTL_Pedidos vf_Ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);
                                    vf_Ctl_Pedidos.fuAlterarSituacaoPedido();


                                } else {
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

                rs_Pedido.moveToNext();
            }
        }
        return true;
    }

    public boolean FU_SincronizaItemPedido(String numPedidoServidor){

        //cl_Pedidos.getNumPedido();
        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
        cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(vc_Context, cl_ItemPedido);


        if(ctl_ItemPedido.fuCarregaTodosItensPedido()) {

            Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;

            while (!rs_ItemPedido.isAfterLast()) {

                cl_ItemPedido = new CL_ItemPedido();

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                        cl_ItemPedido.setId(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setId("0");
                }


                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).trim().equals("")) {
                        cl_ItemPedido.setNumPedido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setNumPedido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                        cl_ItemPedido.setCdProduto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setCdProduto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                        cl_ItemPedido.setDescricao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setDescricao("espaco");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                        cl_ItemPedido.setQtde(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setQtde("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setPercDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setPercDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlMaxDescPermitido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlMaxDescPermitido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setVlDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                        cl_ItemPedido.setVlUnitario(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlUnitario("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlLiquido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlLiquido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        cl_ItemPedido.setVlTotal(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlTotal("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.OBSERVACAOITEMPEDIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.OBSERVACAOITEMPEDIDO)).trim().equals("")) {
                        cl_ItemPedido.setObservacao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.OBSERVACAOITEMPEDIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setObservacao("espaco");
                }

                String vf_CdRefEstoque = "";

                //Mandar junto o CdRefEstoque do produto.
                CL_Produtos cl_Produtos = new CL_Produtos();
                cl_Produtos.setCdProduto(cl_ItemPedido.getCdProduto());

                CTL_Produtos ctl_Produtos = new CTL_Produtos(vc_Context, cl_Produtos);

                try{
                    vf_CdRefEstoque = ctl_Produtos.fuBuscarCdRefEstoque();
                }catch (Exception e){
                    vf_CdRefEstoque = "";
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
                            "WebService2.php?user=" + cl_Usuario.getCdClienteBanco()
                            + "&format=json&num=10&method=inseriritempedidonovo&numpedido=" + numPedidoServidor + "&cdproduto="
                            + cl_ItemPedido.getCdProduto().replace(" ", "espaco") + "&id=" + cl_ItemPedido.getId()
                            + "&qtde=" + cl_ItemPedido.getQtde().replace(".", ",") + "&vlunitario=" + cl_ItemPedido.getVlUnitario()
                            + "&vltotal=" + cl_ItemPedido.getVlTotal().replace(".", ",")
                            + "&dtemissao=" + cl_Pedidos.getDtEmissao().replace(" ", "espaco")
                            + "&descricao=" + cl_ItemPedido.getDescricao().replace(" ", "espaco")
                            + "&vldesconto=" + cl_ItemPedido.getVlDesconto().replace(".", ",")
                            + "&percdesconto=" + cl_ItemPedido.getPercDesconto().replace(".", ",")
                            + "&cdrefestoque=" + vf_CdRefEstoque
                            + "&observacao=" + substituirCaracteres(cl_ItemPedido.getObservacao().replace(" ", "espaco"))
                            + "&filial=" + cl_Filial.getCdFilial() + "";

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

                rs_ItemPedido.moveToNext();

            }
        }
        return true;

    }

    public boolean FU_AlteraSituacaoPedido(String numPedidoServidor){

        String dataRegistro = funcoes.getDateTime().substring(0, 16);

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco()
                    + "&format=json&num=10&method=alteraSituacaoPedidoNovo&numpedido=" + numPedidoServidor
                    + "&filial=" + cl_Filial.getCdFilial()
                    + "&dtregistro=" + dataRegistro.replace(" ", "espaco")
                    + "&usuario=" + cl_Usuario.getNmUsuarioSistema().replace(" ", "espaco") + "";
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

            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;

            }

        } catch (Throwable t) {
            return false;
        }

        return true;
    }

    public boolean FU_CancelarPedido(String numPedido) {

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=cancelarPedido&numpedido=" + numPedido + "";
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
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public String removeAccent(final String str) {
        String strNoAccent = Normalizer.normalize(str, Normalizer.Form.NFD);
        strNoAccent = strNoAccent.replaceAll("[^A-Za-z0-9]","");
        return strNoAccent;
    }

    public String substituirCaracteres(String str){
        String strReplaced = str.replace("Ç", "CEDILHAMAIUSCULO").replace("ç", "cedilhaminusculo");
        strReplaced = strReplaced.replace("$", "CARACTERECIFRAO").replace("%", "CARACTEREPORCENTAGEM");
        strReplaced = strReplaced.replace("&", "CARACTEREECOMERCIAL").replace("#", "CARACTEREJOGODAVELHA");
        strReplaced = strReplaced.replace("'", "").replace("\\", "CARACTEBARRAINVERTIDA");
        strReplaced = strReplaced.replace("/", "CARACTEREBARRANORMAL").replace("*", "CARACTEASTERISCO");

        return strReplaced;
    }
}
