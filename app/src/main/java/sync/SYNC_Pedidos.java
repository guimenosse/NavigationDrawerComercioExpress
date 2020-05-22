package sync;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;
    MDL_Pedidos mdl_Pedidos;

    CL_Filial cl_Filial;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    protected String vc_CdClienteBanco = "", vc_NmUsuarioSistema = "";

    public SYNC_Pedidos(Context context){

        this.vc_Context = context;
        this.mdl_Pedidos = new MDL_Pedidos(context);
        this.funcoes = new Funcoes();

        MDL_Usuario mdl_Usuario = new MDL_Usuario(context);
        this.vc_CdClienteBanco = mdl_Usuario.fuSelecionarCdClienteBanco();
        this.vc_NmUsuarioSistema = mdl_Usuario.fuSelecionarNmUsuarioSistema();

        cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(context, cl_Filial);
        ctl_Filial.fuBuscaCdFilialSelecionada();
    }

    public boolean FU_EnviarPedido(CL_Pedidos cl_Pedido){

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
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + vc_CdClienteBanco
                    + "&format=json&num=10&method=inserirpedido&vltotal=" + cl_Pedidos.getVlTotal()
                    + "&dtemissao=" + cl_Pedidos.getDtEmissao() + ""
                    + "&cdvendedor=" + cl_Pedidos.getCdVendedor() + "&cdemitente=" + cl_Pedidos.getCdCliente() + "&rzsocial=" + cl_Pedidos.getNomeRzSocial()
                    + "&percdesconto=" + cl_Pedidos.getPercDesconto() + "&vldesconto=" + cl_Pedidos.getVlDesconto() + ""
                    + "&vlfrete=" + cl_Pedidos.getVlFrete()
                    + "&condpgto=" + cl_Pedidos.getCondPgto() + "&obs=" + cl_Pedidos.getObsPedido().replace("\n", "pulalinha")
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
                        if (FU_SincronizaItemPedido(vf_NumPedidoServidor)) {
                            FU_AlteraSituacaoPedido(vf_NumPedidoServidor);
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
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).trim().equals("")) {
                        cl_Pedidos.setNumPedido(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).replace(" ", "espaco"));
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

                        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

                        double vf_VlTotalDouble = 0;
                        if(ctl_ItemPedido.fuCarregaTodosItensPedido()){
                            Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;
                            while (!rs_ItemPedido.isAfterLast()) {
                                vf_VlTotalDouble = vf_VlTotalDouble + Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                                rs_ItemPedido.moveToNext();
                            }
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

                cl_Pedidos.setObsPedido(cl_Pedidos.getObsPedido() + "  CONDIÇÃO DE PAGAMENTO INFORMADA: " + cl_Pedidos.getCondPgto() + "");
                cl_Pedidos.setObsPedido(cl_Pedidos.getObsPedido().replace(" ", "espaco"));

                try {
                    CL_Clientes cl_Cliente = new CL_Clientes();
                    cl_Cliente.setCdCliente(cl_Pedidos.getCdCliente());

                    CTL_Clientes ctl_Cliente = new CTL_Clientes(this.vc_Context, cl_Cliente);

                    if(ctl_Cliente.fuSelecionarClienteSincronizacao("cdCliente")){
                        if (cl_Cliente.getFgSincronizado().equals("N")) {
                            SYNC_Clientes sync_Clientes = new SYNC_Clientes(vc_Context);
                            cl_Pedidos.setCdCliente(sync_Clientes.FU_SincronizarClientePedido(cl_Cliente));
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
                                cl_Pedidos.setCdCliente(sync_Clientes.FU_SincronizarClientePedido(cl_Cliente));
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
                    String url = "http://www.planosistemas.com.br/" +
                            "WebService2.php?user=" + vc_CdClienteBanco
                            + "&format=json&num=10&method=inserirpedido&vltotal=" + cl_Pedidos.getVlTotal()
                            + "&dtemissao=" + cl_Pedidos.getDtEmissao() + ""
                            + "&cdvendedor=" + cl_Pedidos.getCdVendedor() + "&cdemitente=" + cl_Pedidos.getCdCliente() + "&rzsocial=" + cl_Pedidos.getNomeRzSocial()
                            + "&percdesconto=" + cl_Pedidos.getPercDesconto() + "&vldesconto=" + cl_Pedidos.getVlDesconto() + ""
                            + "&vlfrete=" + cl_Pedidos.getVlFrete()
                            + "&condpgto=" + cl_Pedidos.getCondPgto() + "&obs=" + cl_Pedidos.getObsPedido().replace("\n", "pulalinha")
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

                        /*map.put("idusers", jObject.getString("RzSocial"));
                        map.put("UserName", jObject.getString("Endereco"));
                        map.put("FullName", jObject.getString("Telefone"));*/

                        /*map.put("idusers", jObject.getString("idusers"));
                        map.put("UserName", jObject.getString("UserName"));
                        map.put("FullName", jObject.getString("FullName"));*/

                            //mylist.add(map);
                            if (!jObject.getString("NumPedido").equals("null")) {
                                String vf_NumPedidoServidor = jObject.getString("NumPedido");
                                if (FU_SincronizaItemPedido(vf_NumPedidoServidor)) {
                                    FU_AlteraSituacaoPedido(vf_NumPedidoServidor);

                                } else {
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
        }
        return true;
    }

    public boolean FU_SincronizaItemPedido(String numPedidoServidor){

        //cl_Pedidos.getNumPedido();
        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
        cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);


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
                            + "&format=json&num=10&method=inseriritempedido&numpedido=" + numPedidoServidor + "&cdproduto="
                            + cl_ItemPedido.getCdProduto().replace(" ", "espaco") + "&id=" + cl_ItemPedido.getId()
                            + "&qtde=" + cl_ItemPedido.getQtde() + "&vlunitario=" + cl_ItemPedido.getVlUnitario()
                            + "&vltotal=" + cl_ItemPedido.getVlTotal()
                            + "&dtemissao=" + cl_Pedidos.getDtEmissao()
                            + "&descricao=" + cl_ItemPedido.getDescricao().replace(" ", "espaco")
                            + "&vldesconto=" + cl_ItemPedido.getVlDesconto()
                            + "&percdesconto=" + cl_ItemPedido.getPercDesconto()
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
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + vc_CdClienteBanco
                    + "&format=json&num=10&method=alteraSituacaoPedidoNovo&numpedido=" + numPedidoServidor
                    + "&filial=" + cl_Filial.getCdFilial()
                    + "&dtregistro=" + dataRegistro.replace(" ", "espaco")
                    + "&usuario=" + vc_NmUsuarioSistema.replace(" ", "espaco") + "";
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
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + vc_CdClienteBanco + "&format=json&num=10&method=cancelarPedido&numpedido=" + numPedido + "";
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
}
