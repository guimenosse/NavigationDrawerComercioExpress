package sync;

import android.content.Context;
import android.util.Log;

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
import classes.CL_Configuracao;
import classes.CL_Filial;
import classes.CL_Produtos;
import classes.CL_Usuario;
import controllers.CTL_Configuracao;
import controllers.CTL_Filial;
import controllers.CTL_Produtos;
import controllers.CTL_Usuario;
import models.MDL_Produtos;
import models.MDL_Usuario;

public class SYNC_Produtos {

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Filial cl_Filial;

    CL_Configuracao cl_Configuracao;
    CTL_Configuracao  ctl_Configuracao;

    CL_Produtos cl_Produtos;
    CTL_Produtos ctl_Produtos;
    MDL_Produtos mdl_Produtos;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    public SYNC_Produtos(Context context){

        this.vc_Context = context;
        this.mdl_Produtos = new MDL_Produtos(context);
        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        cl_Configuracao = new CL_Configuracao();
        ctl_Configuracao = new CTL_Configuracao(vc_Context, cl_Configuracao);
        ctl_Configuracao.fuCarregarFgControlaEstoquePedido();

        cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(context, cl_Filial);
        ctl_Filial.fuBuscaCdFilialSelecionada();
    }

    public boolean FU_SincronizarTodosProdutosServidor(){

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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=produtos";
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

                if(!mdl_Produtos.fuDeletarTodosProdutos()){
                    return false;
                }

                for (int i = 0; i < jArray.length(); i++) {

                    cl_Produtos = new CL_Produtos();

                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");
                    JSONObject jObject = new JSONObject(s);


                    cl_Produtos.setCdProduto(jObject.getString("CdProduto"));
                    if(cl_Produtos.getCdProduto().equals("2071")){
                        String teste = "";
                        String testando =  teste + "";
                    }
                    cl_Produtos.setDescricao(e.getString("post2"));

                    if (!cl_Produtos.getDescricao().equals("null")) {

                        cl_Produtos.setEstoqueAtual(jObject.getString("EstAtual"));
                        cl_Produtos.setVlUnitario(jObject.getString("VlVenda"));
                        cl_Produtos.setVlAtacado(jObject.getString("VlAtacado"));
                        cl_Produtos.setDtUltimaAlteracao(jObject.getString("DtUltAlteracao"));

                        String vf_Desconto = "0";
                        if(Double.parseDouble(jObject.getString("PercDescMaxVendedor")) > 0) {
                            vf_Desconto = jObject.getString("PercDescMaxVendedor");
                        }else if(Double.parseDouble(jObject.getString("DescCategoria")) > 0) {
                            vf_Desconto = jObject.getString("DescCategoria");
                        }else if(Double.parseDouble(jObject.getString("DescDepartamento")) > 0) {
                            vf_Desconto = jObject.getString("DescDepartamento");
                        }
                        cl_Produtos.setDescMaxPermitido(vf_Desconto);

                        cl_Produtos.setDescMaxPermitidoA(jObject.getString("DescontoA"));
                        cl_Produtos.setDescMaxPermitidoB(jObject.getString("DescontoB"));
                        cl_Produtos.setDescMaxPermitidoC(jObject.getString("DescontoC"));
                        cl_Produtos.setDescMaxPermitidoD(jObject.getString("DescontoD"));
                        cl_Produtos.setDescMaxPermitidoE(jObject.getString("DescontoE"));
                        cl_Produtos.setDescMaxPermitidoFidelidade(jObject.getString("DescontoFidelidade"));
                        cl_Produtos.setCdRefEstoque(jObject.getString("CdRefEstoque"));

                        if(cl_Configuracao.getFgControlaEstoquePedido().equals("S")){
                            double vf_QtdePedSaida = Double.parseDouble(jObject.getString("QtdePedSaida"));
                            double vf_EstAtual = Double.parseDouble(jObject.getString("EstAtual"));
                            double vf_QtdeDisponivel = vf_EstAtual - vf_QtdePedSaida;

                            cl_Produtos.setQtdeDisponivel(String.valueOf(vf_QtdeDisponivel));
                        }else{
                            cl_Produtos.setQtdeDisponivel("");
                        }

                        ctl_Produtos =  new CTL_Produtos(vc_Context, cl_Produtos);

                        if(!ctl_Produtos.fuInserirProdutoFilial()){

                        }

                    }

                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();

            }

        } catch (Throwable t) {
            return false;
        }

        return true;

    }

    public boolean FU_SincronizarPrecosIndividualizadosProdutos(){
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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=produtoprecofilial&cdfilial=" + cl_Filial.getCdFilial() + "";
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

                        if (!jObject.getString("CdProduto").equals("null")) {

                            String VA_CdProduto = jObject.getString("CdProduto");

                            if(Double.parseDouble(jObject.getString("VlVenda")) > 0){
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("VlVenda"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoA")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoA"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoB")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoB"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoC")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoC"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoD")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoD"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoE")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoE"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoFidelidade")) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("PercDescontoFidelidade"));
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

}
