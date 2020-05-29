package sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import br.comercioexpress.plano.BancoController;
import br.comercioexpress.plano.MensagemUtil;
import classes.CL_Filial;
import classes.CL_Usuario;
import controllers.CTL_Filial;
import controllers.CTL_Pedidos;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import classes.CL_Clientes;
import classes.CL_Pedidos;
import controllers.CTL_Clientes;
import models.MDL_Clientes;
import models.MDL_Filial;
import models.MDL_Usuario;

public class SYNC_Clientes {

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Clientes cl_Clientes;
    CTL_Clientes ctl_Clientes;
    MDL_Clientes mdl_Clientes;

    Funcoes funcoes;

    public String mensagem = "";

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;


    public SYNC_Clientes(Context context){

        this.vc_Context = context;
        this.mdl_Clientes = new MDL_Clientes(context);
        this.funcoes = new Funcoes();

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);

        cl_Filial = new CL_Filial();
        ctl_Filial = new CTL_Filial(context, cl_Filial);

    }

    //Função que ira sincronizar todos os clientes com FgSincronizado igual à N
    public boolean FU_SincronizarClientes(Cursor rs_Cliente){

        if(rs_Cliente!=null) {
            rs_Cliente.moveToFirst();

            while (!rs_Cliente.isAfterLast()) {

                CL_Clientes cl_Cliente = new CL_Clientes();

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                        cl_Cliente.setId(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)));
                    } else {
                        cl_Cliente.setId("0");
                    }
                }catch (Exception e){
                    cl_Cliente.setId("0");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)).trim().equals("")) {
                        cl_Cliente.setCdCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)));
                    } else {
                        cl_Cliente.setCdCliente("0");
                    }
                }catch (Exception e){
                    cl_Cliente.setCdCliente("0");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).trim().equals("")) {
                        cl_Cliente.setNomeRzSocial(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).replace("'", ""));
                    } else {
                        cl_Cliente.setNomeRzSocial("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setNomeRzSocial("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).trim().equals("")) {
                        cl_Cliente.setNomeFantasia(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).replace("'", ""));
                    } else {
                        cl_Cliente.setNomeFantasia("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setNomeFantasia("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).trim().equals("")) {
                        cl_Cliente.setCep(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).replace(".", "").replace("-", ""));
                    } else {
                        cl_Cliente.setCep("0");
                    }
                }catch (Exception e){
                    cl_Cliente.setCep("0");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).trim().equals("")) {
                        cl_Cliente.setEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).replace("'", ""));
                    } else {
                        cl_Cliente.setEndereco("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setEndereco("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).trim().equals("")) {
                        cl_Cliente.setClassificacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).replace("'", ""));
                    } else {
                        cl_Cliente.setClassificacao("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setClassificacao("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).trim().equals("")) {
                        cl_Cliente.setNumEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)));
                    } else {
                        cl_Cliente.setNumEndereco("0");
                    }
                }catch (Exception e){
                    cl_Cliente.setNumEndereco("0");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).trim().equals("")) {
                        cl_Cliente.setComplemento(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).replace("'", ""));
                    } else {
                        cl_Cliente.setComplemento("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setComplemento("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).trim().equals("")) {
                        cl_Cliente.setBairro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).replace("'", ""));
                    } else {
                        cl_Cliente.setBairro("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setBairro("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).trim().equals("")) {
                        cl_Cliente.setUf(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)));
                    } else {
                        cl_Cliente.setUf("PR");
                    }
                }catch (Exception e){
                    cl_Cliente.setUf("PR");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).trim().equals("")) {
                        cl_Cliente.setCidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).replace("'", ""));
                    } else {
                        cl_Cliente.setCidade("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setCidade("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).trim().equals("")) {
                        cl_Cliente.setCpfCnpj(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).replace(".", "").replace("-", "").replace("/", ""));
                    } else {
                        cl_Cliente.setCpfCnpj("0");
                    }
                }catch (Exception e){
                    cl_Cliente.setCpfCnpj("0");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).trim().equals("")) {
                        cl_Cliente.setTelefone(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).replace("(", "").replace("-", "").replace(")", ""));
                    } else {
                        cl_Cliente.setTelefone("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setTelefone("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).trim().equals("")) {
                        cl_Cliente.setTelefoneAdicional(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).replace("(", "").replace("-", "").replace(")", ""));
                    } else {
                        cl_Cliente.setTelefoneAdicional("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setTelefoneAdicional("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).trim().equals("")) {
                        cl_Cliente.setFax(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).replace("(", "").replace("-", "").replace(")", ""));
                        ;
                    } else {
                        cl_Cliente.setFax("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setFax("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).trim().equals("")) {
                        cl_Cliente.setNomeContato(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).replace("'", ""));
                    } else {
                        cl_Cliente.setNomeContato("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setNomeContato("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).trim().equals("")) {
                        cl_Cliente.setEmail(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).replace("'", ""));
                    } else {
                        cl_Cliente.setEmail("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setEmail("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).trim().equals("")) {
                        cl_Cliente.setTipoPessoa(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)));
                    } else {
                        cl_Cliente.setTipoPessoa("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setTipoPessoa("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).trim().equals("")) {
                        cl_Cliente.setTipoCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)));
                    } else {
                        cl_Cliente.setTipoCliente("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setTipoCliente("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).trim().equals("")) {
                        cl_Cliente.setObservacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).replace("'", ""));
                    } else {
                        cl_Cliente.setObservacao("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setObservacao("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).trim().equals("")) {
                        cl_Cliente.setDtUltimaAlteracao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)));
                    } else {
                        cl_Cliente.setDtUltimaAlteracao("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setDtUltimaAlteracao("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).trim().equals("")) {
                        cl_Cliente.setDtCadastro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)));
                    } else {
                        cl_Cliente.setDtCadastro("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setDtCadastro("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).trim().equals("")) {
                        cl_Cliente.setVendedor(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)));
                    } else {
                        cl_Cliente.setVendedor("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setVendedor("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).trim().equals("")) {
                        cl_Cliente.setInscEstadual(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)));
                    } else {
                        cl_Cliente.setInscEstadual("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setInscEstadual("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).trim().equals("")) {
                        cl_Cliente.setFidelidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)));
                    } else {
                        cl_Cliente.setFidelidade("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setFidelidade("espaco");
                }

                try {
                    if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).trim().equals("")) {
                        cl_Cliente.setTipoPreco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)));
                    } else {
                        cl_Cliente.setTipoPreco("espaco");
                    }
                }catch (Exception e){
                    cl_Cliente.setTipoPreco("espaco");
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
                            "WebService2020.php?user=" + cl_Usuario.getCdClienteBanco() +
                            "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + cl_Cliente.getNomeRzSocial().replace(" ", "espaco") +
                            "&nmfantasia=" + cl_Cliente.getNomeFantasia().replace(" ", "espaco") +
                            "&cep=" + cl_Cliente.getCep() +
                            "&endereco=" + cl_Cliente.getEndereco().replace(" ", "espaco") +
                            "&classificacao=" + cl_Cliente.getClassificacao().replace(" ", "espaco") +
                            "&numero=" + cl_Cliente.getNumEndereco() +
                            "&complemento=" + cl_Cliente.getComplemento().replace(" ", "espaco") +
                            "&bairro=" + cl_Cliente.getBairro().replace(" ", "espaco") +
                            "&uf=" + cl_Cliente.getUf() +
                            "&cidade=" + cl_Cliente.getCidade().replace(" ", "espaco") +
                            "&tipopessoa=" + cl_Cliente.getTipoPessoa() +
                            "&cgc=" + cl_Cliente.getCpfCnpj() +
                            "&telefone=" + cl_Cliente.getTelefone() +
                            "&telefoneadicional=" + cl_Cliente.getTelefoneAdicional() +
                            "&fax=" + cl_Cliente.getFax() +
                            "&contato=" + cl_Cliente.getNomeContato().replace(" ", "espaco") +
                            "&email=" + cl_Cliente.getEmail() +
                            "&vendedor=" + cl_Cliente.getVendedor() +
                            "&tipocliente=" + cl_Cliente.getTipoCliente().replace(" ", "espaco") +
                            "&dtcadastro=" + cl_Cliente.getDtCadastro().replace(" ", "espaco") + "" +
                            "&obscliente=" + cl_Cliente.getObservacao().replace(" ", "espaco") +
                            "&inscestadual=" + cl_Cliente.getInscEstadual().replace(" ", "espaco") +
                            "&cdfilial=" + cl_Filial.getCdFilial() + "";

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

                            if (!jObject.getString("CdCliente").equals("null")) {

                                CL_Pedidos cl_Pedidos = new CL_Pedidos();
                                cl_Pedidos.setCdCliente(cl_Clientes.getCdCliente());

                                CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);

                                if (ctl_Pedidos.fuCarregarPedidosCliente()) {
                                    Cursor rs_Pedido = ctl_Pedidos.rs_Pedido;
                                    if (rs_Pedido.getCount() > 0) {
                                        while (!rs_Pedido.isAfterLast()) {

                                            cl_Pedidos.setCdCliente(jObject.getString("CdCliente"));
                                            cl_Pedidos.setNumPedido(rs_Pedido.getString(rs_Pedido.getColumnIndex(CriaBanco.ID)));

                                            ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);
                                            ctl_Pedidos.fuAlterarClientePedido();

                                            rs_Pedido.moveToNext();
                                        }
                                    }
                                }
                            }
                        }


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    //Toast.makeText(getApplicationContext(), "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }

                rs_Cliente.moveToNext();
            }
        }


        return true;
    }

    public boolean FU_SincronizarTodosClientesServidor(){

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
            String url = "";
            if(funcoes.verificaAutorizacao("SCV", "SCVF101TOV", cl_Usuario.getNmUsuarioSistema(), cl_Usuario.getCdClienteBanco())){
                url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=clientenovo&cdfilial=" + cl_Filial.getCdFilial() + "";
            }else {
                url = "http://www.planosistemas.com.br/" +
                        "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=clientevendedor&cdvendedor=" + cl_Usuario.getCdVendedorDefault() + "&cdfilial=" + cl_Filial.getCdFilial() + "";
            }
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

                if(!mdl_Clientes.fuDeletarTodosClientes()){
                    return false;
                }

                for (int i = 0; i < jArray.length(); i++) {

                    cl_Clientes = new CL_Clientes();

                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");

                    cl_Clientes.setNomeRzSocial(e.getString("post2"));
                    cl_Clientes.setNomeFantasia(e.getString("post3"));
                    cl_Clientes.setEndereco(e.getString("post4"));
                    cl_Clientes.setComplemento(e.getString("post5"));
                    cl_Clientes.setBairro(e.getString("post6"));
                    cl_Clientes.setCidade(e.getString("post7"));
                    cl_Clientes.setNomeContato(e.getString("post8"));
                    cl_Clientes.setTipoCliente(e.getString("post9"));
                    cl_Clientes.setObservacao(e.getString("post10"));

                    JSONObject jObject = new JSONObject(s);

                    if (!cl_Clientes.getNomeRzSocial().equals("null")) {

                        cl_Clientes.setTipoPessoa(jObject.getString("FgTipoPessoa"));
                        cl_Clientes.setCpfCnpj(jObject.getString("CGC"));

                        if (cl_Clientes.getTipoPessoa().equals("F")) {
                            cl_Clientes.setTipoPessoa("Física");
                            if(cl_Clientes.getCpfCnpj().length() < 11){
                                cl_Clientes.setCpfCnpj("0" + cl_Clientes.getCpfCnpj());
                            }
                        } else {
                            cl_Clientes.setTipoPessoa("Jurídica");
                            if(cl_Clientes.getCpfCnpj().length() < 14){
                                cl_Clientes.setCpfCnpj("0" + cl_Clientes.getCpfCnpj());
                            }
                        }


                        cl_Clientes.setTelefone(jObject.getString("Telefone").replace("(", "").replace(")", "").replace(" ", "").replace("-", ""));
                        cl_Clientes.setTelefoneAdicional(jObject.getString("TelefoneAdicional").replace("(", "").replace(")", "").replace(" ", "").replace("-", ""));
                        cl_Clientes.setFax(jObject.getString("Fax").replace("(", "").replace(")", "").replace(" ", "").replace("-", ""));

                        int vf_TamanhoTelefone = cl_Clientes.getTelefone().length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(vf_TamanhoTelefone > 0) {
                            if (cl_Clientes.getTelefone().substring(0, 1).equals("0")) {
                                cl_Clientes.setTelefone(cl_Clientes.getTelefone().substring(1, vf_TamanhoTelefone));
                            }
                        }

                        vf_TamanhoTelefone = cl_Clientes.getTelefoneAdicional().length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(vf_TamanhoTelefone > 0) {
                            if (cl_Clientes.getTelefoneAdicional().substring(0, 1).equals("0")) {
                                cl_Clientes.setTelefoneAdicional(cl_Clientes.getTelefoneAdicional().substring(1, vf_TamanhoTelefone));
                            }
                        }

                        vf_TamanhoTelefone = cl_Clientes.getFax().length();

                        //Verificar porque o cliente A C Doro esta vindo com um numero a menos.
                        if(vf_TamanhoTelefone > 0) {
                            if (cl_Clientes.getFax().substring(0, 1).equals("0")) {
                                cl_Clientes.setFax(cl_Clientes.getFax().substring(1, vf_TamanhoTelefone));
                            }
                        }

                        cl_Clientes.setCdCliente(jObject.getString("CdCliente"));
                        cl_Clientes.setCep(jObject.getString("Cep"));
                        cl_Clientes.setNumEndereco(jObject.getString("NumEndereco"));
                        cl_Clientes.setUf(jObject.getString("Uf"));
                        cl_Clientes.setInscEstadual(jObject.getString("InscEst"));
                        cl_Clientes.setEmail(jObject.getString("Email"));
                        cl_Clientes.setVendedor(jObject.getString("CdVendedor"));
                        cl_Clientes.setDtUltimaAlteracao(jObject.getString("DtUltAlteracao"));
                        cl_Clientes.setDtCadastro(jObject.getString("DtCadastro"));
                        cl_Clientes.setFgSincronizado("S");
                        cl_Clientes.setClassificacao(jObject.getString("Classificacao"));
                        cl_Clientes.setFidelidade(jObject.getString("Fidelidade"));
                        cl_Clientes.setTipoPreco(jObject.getString("TipoPreco"));

                        ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);
                        ctl_Clientes.fuInserirCliente();

                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

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
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            return false;
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Não foi possivel realizar a conexão com o servidor. Favor verificar a conexão com a internet. Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }

        return true;

    }

    public boolean FU_SincronizarClientePedido(CL_Clientes cl_Cliente){

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
                    "WebService2020.php?user=" + cl_Usuario.getCdClienteBanco() +
                    "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + cl_Cliente.getNomeRzSocial().replace(" ", "espaco") +
                    "&nmfantasia=" + cl_Cliente.getNomeFantasia().replace(" ", "espaco") +
                    "&cep=" + cl_Cliente.getCep() +
                    "&endereco=" + cl_Cliente.getEndereco().replace(" ", "espaco") +
                    "&classificacao=" + cl_Cliente.getClassificacao().replace(" ", "espaco") +
                    "&numero=" + cl_Cliente.getNumEndereco() +
                    "&complemento=" + cl_Cliente.getComplemento().replace(" ", "espaco") +
                    "&bairro=" + cl_Cliente.getBairro().replace(" ", "espaco") +
                    "&uf=" + cl_Cliente.getUf() +
                    "&cidade=" + cl_Cliente.getCidade().replace(" ", "espaco") +
                    "&tipopessoa=" + cl_Cliente.getTipoPessoa() +
                    "&cgc=" + cl_Cliente.getCpfCnpj() +
                    "&telefone=" + cl_Cliente.getTelefone() +
                    "&telefoneadicional=" + cl_Cliente.getTelefoneAdicional() +
                    "&fax=" + cl_Cliente.getFax() +
                    "&contato=" + cl_Cliente.getNomeContato().replace(" ", "espaco") +
                    "&email=" + cl_Cliente.getEmail() +
                    "&vendedor=" + cl_Cliente.getVendedor() +
                    "&tipocliente=" + cl_Cliente.getTipoCliente().replace(" ", "espaco") +
                    "&dtcadastro=" + cl_Cliente.getDtCadastro().replace(" ", "espaco") + "" +
                    "&obscliente=" + cl_Cliente.getObservacao().replace(" ", "espaco") +
                    "&inscestadual=" + cl_Cliente.getInscEstadual().replace(" ", "espaco") +
                    "&cdfilial=" + cl_Filial.getCdFilial() + "";

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


                        cl_Cliente.setCdCliente(jObject.getString("CdCliente"));
                        CTL_Clientes ctl_Clientes = new CTL_Clientes(vc_Context, cl_Cliente);

                        if(ctl_Clientes.fuAlteraClientePedido()){
                            vf_CdClienteServidor = cl_Cliente.getCdCliente();
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

    public boolean FU_SincronizarConfiguracaoPrecoIndividualizado(){
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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=precoindividualizado";
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


                    if (!jObject.getString("PrecoIndividualizado").equals("null") && !jObject.getString("PrecoIndividualizado").equals("") && jObject.getString("PrecoIndividualizado").equals("S")) {
                        ctl_Filial.fuAtualizarPrecoIndividualizado("S");
                    }else{
                        ctl_Filial.fuAtualizarPrecoIndividualizado("N");
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

    public boolean FU_SincronizarTipoCliente(){
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
                    "WebService2.php?user=" + cl_Usuario.getCdClienteBanco() + "&format=json&num=10&method=tipocliente";
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

                cl_Clientes = new CL_Clientes();
                ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);

                if(!ctl_Clientes.fuDeletarTodosTiposCliente()){
                    return false;
                }

                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String vf_CdTipo = e.getString("post");
                    String vf_NmTipo = e.getString("post2");

                    if (!vf_NmTipo.equals("null") && !vf_NmTipo.trim().equals("")) {
                        vf_NmTipo = vf_NmTipo.replace("\\u00c7", "Ç");

                        if(!ctl_Clientes.fuInserirTipoCliente(vf_CdTipo, vf_NmTipo)){
                            return false;
                        }
                    }

                }


                //Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;

            } catch (IOException e) {
                // TODO Auto-generated catch block
               /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
                return false;
            }
            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
           /*e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Não foi possivel realizar a sincronização dos clientes. Favor verificar a conexão com a internet.", Toast.LENGTH_LONG).show();*/
            return false;
        }

        return true;
    }
}
