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

import br.comercioexpress.plano.Funcoes;
import br.comercioexpress.plano.MySSLSocketFactory;
import classes.CL_Clientes;
import classes.CL_Configuracao;
import classes.CL_Filial;
import classes.CL_Produtos;
import classes.CL_ProdutosAPI;
import classes.CL_TiposCliente;
import classes.CL_Usuario;
import controllers.CTL_Clientes;
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

    String bodyString = "";

    public SYNC_Produtos(Context context){

        this.vc_Context = context;
        this.mdl_Produtos = new MDL_Produtos(context);
        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);
        cl_Usuario = ctl_Usuario.fuSelecionarUsuarioAPI();
        bodyString = ctl_Usuario.buildRequestBodyString(cl_Usuario);

        cl_Configuracao = new CL_Configuracao();
        ctl_Configuracao = new CTL_Configuracao(vc_Context, cl_Configuracao);
        ctl_Configuracao.fuCarregarConfiguracoes();

        cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(context, cl_Filial);
        ctl_Filial.fuBuscaCdFilialSelecionada();
    }

    public boolean FU_SincronizarProdutosAPI(){

        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/SelecionarProdutosExpress")
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                if(!mdl_Produtos.fuDeletarTodosProdutos()){
                    return false;
                }

                // Parsing JSON to Java object using Gson
                Gson gson = new Gson();
                List<CL_ProdutosAPI> produtos = gson.fromJson(jsonResponse, new TypeToken<List<CL_ProdutosAPI>>(){}.getType());
                // Agora você pode adicionar os novos tipos de cliente ao seu banco de dados ou lista
                for (CL_ProdutosAPI produto : produtos) {

                    cl_Produtos = new CL_Produtos();
                    cl_Produtos.setCdProduto(produto.getCdProduto());
                    cl_Produtos.setDescricao(produto.getDescricao());
                    try{
                        String complementoDescricao = produto.getComplemento();
                        cl_Produtos.setComplementoDescricao(complementoDescricao);
                    }catch (Exception e_Complemento){
                        cl_Produtos.setComplementoDescricao("");
                    }

                    if (!cl_Produtos.getDescricao().equals("null")) {
                        cl_Produtos.setEstoqueAtual(produto.getEstAtual());
                        cl_Produtos.setVlUnitario(produto.getVlVenda());
                        cl_Produtos.setVlAtacado(produto.getVlAtacado());
                        cl_Produtos.setDtUltimaAlteracao(produto.getDtUltimaAlteracao());

                        String vf_Desconto = "0";
                        if(Double.parseDouble(produto.getPercDescMaxVendedor()) > 0) {
                            vf_Desconto = produto.getPercDescMaxVendedor();
                        }else if(Double.parseDouble(produto.getDescCategoria()) > 0) {
                            vf_Desconto = produto.getDescCategoria();
                        }else if(Double.parseDouble(produto.getDescDepartamento()) > 0) {
                            vf_Desconto = produto.getDescDepartamento();
                        }
                        cl_Produtos.setDescMaxPermitido(vf_Desconto);

                        cl_Produtos.setDescMaxPermitidoA(produto.getDescontoA());
                        cl_Produtos.setDescMaxPermitidoB(produto.getDescontoB());
                        cl_Produtos.setDescMaxPermitidoC(produto.getDescontoC());
                        cl_Produtos.setDescMaxPermitidoD(produto.getDescontoD());
                        cl_Produtos.setDescMaxPermitidoE(produto.getDescontoE());
                        cl_Produtos.setDescMaxPermitidoFidelidade(produto.getDescontoFidelidade());
                        cl_Produtos.setCdRefEstoque(produto.getCdRefEstoque());

                        if(cl_Configuracao.getFgControlaEstoquePedido().equals("S")){
                            double vf_QtdePedSaida = Double.parseDouble(produto.getQtdePedSaida());
                            double vf_EstAtual = Double.parseDouble(produto.getEstAtual());
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
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }

        return true;
    }

    public boolean FU_SincronizarPrecosIndividualizadosProdutosAPI(){

        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/SelecionarProdutosExpressIndividualizado/")
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                MDL_Produtos mdl_Produtos = new MDL_Produtos(vc_Context);

                // Parsing JSON to Java object using Gson
                Gson gson = new Gson();
                List<CL_ProdutosAPI> produtos = gson.fromJson(jsonResponse, new TypeToken<List<CL_ProdutosAPI>>(){}.getType());
                // Agora você pode adicionar os novos tipos de cliente ao seu banco de dados ou lista
                for (CL_ProdutosAPI produto : produtos) {

                    try {

                        if (!produto.getCdProduto().equals("null")) {

                            String VA_CdProduto = produto.getCdProduto();

                            if(Double.parseDouble(produto.getVlVenda()) > 0){
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getVlVenda());
                            }
                            if (Double.parseDouble(produto.getDescontoA()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoA());
                            }
                            if (Double.parseDouble(produto.getDescontoB()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoB());
                            }
                            if (Double.parseDouble(produto.getDescontoC()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoC());
                            }
                            if (Double.parseDouble(produto.getDescontoD()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoD());
                            }
                            if (Double.parseDouble(produto.getDescontoE()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoE());
                            }
                            if (Double.parseDouble(produto.getDescontoFidelidade()) > 0) {
                                mdl_Produtos.fuAtualizarValorUnitarioFilial(VA_CdProduto, produto.getDescontoFidelidade());
                            }

                        }
                    }catch(Exception e3) {
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

                    try{
                        cl_Produtos = new CL_Produtos();

                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject e = jArray.getJSONObject(i);
                        String s = e.getString("post");
                        JSONObject jObject = new JSONObject(s);


                        cl_Produtos.setCdProduto(jObject.getString("CdProduto"));
                        cl_Produtos.setDescricao(e.getString("post2"));

                        try{
                            String complementoDescricao = e.getString("postComplemento");
                            cl_Produtos.setComplementoDescricao(complementoDescricao);
                        }catch (Exception e_Complemento){
                            cl_Produtos.setComplementoDescricao("");
                        }

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
                    }catch (Exception e_Insert){
                        e_Insert.getMessage();
                        String error_Insert = e_Insert.getMessage();
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
