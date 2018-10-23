package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Filial extends AppCompatActivity {

    private ListView lista;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    String operacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        operacao = this.getIntent().getStringExtra("operacao");

        if(operacao.equals("L")) {
            if(verificaConexao()) {
                if (FU_Sincronizar()) {
                    if (FU_SincronizarAutorizacaoUsuario()) {

                    } else {
                        MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a autorização da(s) filial(ais)!");
                    }
                } else {
                    MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a(s) filial(ais)!");
                }
            }else{
                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("ATENÇÃO!");
                //define a mensagem
                builder.setMessage("É necessária conexão com a internet para que as filiais sejam sincronizadas.");

                //define um botão como positivo
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        if(verificaConexao()) {
                            if (FU_Sincronizar()) {
                                if (FU_SincronizarAutorizacaoUsuario()) {

                                } else {
                                    MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a autorização da(s) filial(ais)!");
                                }
                            } else {
                                MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a(s) filial(ais)!");
                            }
                        }else{
                            Intent secondActivity;
                            secondActivity = new Intent(Filial.this, Filial.class);
                            secondActivity.putExtra("operacao", "L");
                            startActivity(secondActivity);
                        }

                    }
                });
                //define um botão como negativo.
                /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                    }
                });*/
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
            }
        }



        builder = new AlertDialog.Builder(this);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BancoController crud = new BancoController(getBaseContext());

        if(crud.countFilial() > 1){

            if(crud.autorizaTrocaFilial().equals("N")){


                //define o titulo
                builder.setTitle("ATENÇÃO!");
                //define a mensagem
                builder.setMessage("Você não possui permissão para troca de filial! Portanto uma vez escolhida a filial não será mais possível realizar a troca de filial.");

                //define um botão como positivo
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                    }
                });
                //define um botão como negativo.
                /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                    }
                });*/
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/
            }

            final Cursor cursor = crud.buscaFilial();

            String[] nomeCampos = new String[] {CriaBanco.ID, CriaBanco.FILIAL};
            int[] idViews = new int[] {R.id.cdfilial, R.id.filial};

            SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewfilial, cursor, nomeCampos, idViews, 0);
            lista = (ListView)findViewById(R.id.listViewFilial);
            lista.setAdapter(adaptador);

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String codigo;
                    cursor.moveToPosition(position);
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));



                    //builder = new AlertDialog.Builder(this);

                    //define o titulo
                    builder.setTitle("ATENÇÃO!");
                    //define a mensagem
                    builder.setMessage("Deseja mesmo selecionar esta filial?");

                    //define um botão como positivo
                    builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                            BancoController crud = new BancoController(getBaseContext());
                            crud.selecionaFilial(Integer.parseInt(codigo));
                            //FU_Sincronizar_PrecoIndividualizado();
                            //if(crud.buscaPrecoIndividualizado().equals("S")) {
                            new LoadingAsyncOpcoes().execute();

                            //}

                        }
                    });
                    //define um botão como negativo.
                /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                    }
                });*/
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/


                }
            });


            final EditText tb_buscarfilial = (EditText)findViewById(R.id.tb_buscarfilial);

            tb_buscarfilial.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    BancoController crud = new BancoController(getBaseContext());
                    final Cursor cursor = crud.buscaFilialNome(tb_buscarfilial.getText().toString());

                    String[] nomeCampos = new String[] {CriaBanco.ID, CriaBanco.FILIAL};
                    int[] idViews = new int[] {R.id.cdfilial, R.id.filial};

                    SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewfilial, cursor, nomeCampos, idViews, 0);
                    lista = (ListView)findViewById(R.id.listViewFilial);
                    lista.setAdapter(adaptador);

                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final String codigo;
                            cursor.moveToPosition(position);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));

                            //builder = new AlertDialog.Builder(this);

                            //define o titulo
                            builder.setTitle("ATENÇÃO!");
                            //define a mensagem
                            builder.setMessage("Deseja mesmo selecionar esta filial?");

                            //define um botão como positivo
                            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                                    BancoController crud = new BancoController(getBaseContext());
                                    crud.selecionaFilial(Integer.parseInt(codigo));
                                    Intent intent = new Intent(Filial.this, Pedidos.class);
                                    startActivity(intent);
                                }
                            });
                            //define um botão como negativo.
                /*builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                    }
                });*/
                            //cria o AlertDialog
                            alerta = builder.create();
                            //Exibe
                            alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/
                        }
                    });
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }else{
            Cursor cursorFilial = crud.buscaFilial();
            crud.selecionaFilial(Integer.parseInt(cursorFilial.getString(cursorFilial.getColumnIndexOrThrow(CriaBanco.ID))));

            Intent intent = new Intent(Filial.this, Pedidos.class);
            startActivity(intent);
        }
    }

    /*private class LoadingAsyncFilial extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Filial.this);
        String validou = "N", validouAutorizacao = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando a(s) filial(ais)...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(FU_Sincronizar()){
                validou = "S";
                if(FU_SincronizarAutorizacaoUsuario()){
                    validouAutorizacao = "S";
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(validou.equals("S")) {
                if(validouAutorizacao.equals("S")) {
                    MensagemUtil.addMsg(Filial.this, "Filial(ais) sincronizada(s) com sucesso!");
                }else{
                    MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a autorização da(s) filial(ais)!");
                }
            }else {
                MensagemUtil.addMsg(Filial.this, "Não foi possivel sincronizar a(s) filial(ais)!");
            }

        }
    }*/

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

    public boolean FU_Sincronizar(){

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 10000;
        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            //HttpParams httpParams = new BasicHttpParams();
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            //
            // p.setParameter("name", pvo.getName());
            //p.setParameter("user", "1");

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=filial";
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


                crud.deletaFilial();
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

                    if (!jObject.getString("CdFilial").equals("null") && !jObject.getString("CdFilial").trim().equals("")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        crud.inserirFilial(jObject.getString("CdFilial"), jObject.getString("NmFilial"), "N", "N");
                        //
                    }else{
                        return false;
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

    protected boolean FU_SincronizarClientesProdutosFilial() {

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 10000;

        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            //HttpParams httpParams = new BasicHttpParams();
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            //
            // p.setParameter("name", pvo.getName());
            //p.setParameter("user", "1");

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=precoindividualizado";
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

                    if (!jObject.getString("PrecoIndividualizado").equals("null") && !jObject.getString("PrecoIndividualizado").equals("") && jObject.getString("PrecoIndividualizado").equals("S")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        try {
                            if (crud.verificaColunaPrecoIndividualizado().equals("S")) {
                                crud.atualizarPrecoIndividualizado("S");
                            }else{
                                try {
                                    crud.criaColunaPrecoIndividualizado();
                                    crud.atualizarPrecoIndividualizado("S");
                                }catch (Exception e5){

                                    return false;
                                }
                            }
                        }catch (Exception e2) {
                            try {
                                crud.criaColunaPrecoIndividualizado();
                                crud.atualizarPrecoIndividualizado("S");
                            }catch (Exception e4){

                                return false;
                            }
                        }
                        //
                    }else{
                        try {
                            if (crud.verificaColunaPrecoIndividualizado().equals("S")) {
                                crud.atualizarPrecoIndividualizado("N");
                            }else{
                                crud.criaColunaPrecoIndividualizado();
                            }
                        }catch (Exception e2) {
                            crud.criaColunaPrecoIndividualizado();
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

        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            //HttpParams httpParams = new BasicHttpParams();
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            //
            // p.setParameter("name", pvo.getName());
            //p.setParameter("user", "1");

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

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!s2.equals("null")) {
                        String VA_Desconto = "0";
                        if(Double.parseDouble(jObject.getString("PercDescMaxVendedor")) > 0) {
                            VA_Desconto = jObject.getString("PercDescMaxVendedor");
                        }else if(Double.parseDouble(jObject.getString("DescCategoria")) > 0) {
                            VA_Desconto = jObject.getString("DescCategoria");
                        }else if(Double.parseDouble(jObject.getString("DescDepartamento")) > 0) {
                            VA_Desconto = jObject.getString("DescDepartamento");
                        }

                        //crud.atualizarDescontos(jObject.getString("CdProduto"), jObject.getString("VlVenda"), VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);
                        //crud.inserirProdutoFilial(jObject.getString("CdProduto"), sDescricao, jObject.getString("EstAtual"), jObjectPrecoFilial.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);


                        crud.inserirProdutoFilial(jObject.getString("CdProduto"), s2, jObject.getString("EstAtual"), jObject.getString("VlVenda"), jObject.getString("VlAtacado"), jObject.getString("DtUltAlteracao"), VA_Desconto, jObject.getString("DescontoA"), jObject.getString("DescontoB"), jObject.getString("DescontoC"), jObject.getString("DescontoD"), jObject.getString("DescontoE"), jObject.getString("DescontoFidelidade"));

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


        if(crud.buscaPrecoIndividualizado().equals("S")){



        }

        return true;

    }

    protected boolean FU_SincronizarAutorizacaoUsuario(){

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 10000;
        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            //HttpParams httpParams = new BasicHttpParams();
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            //
            // p.setParameter("name", pvo.getName());
            //p.setParameter("user", "1");

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=autorizacao&cdsistema=PAR&cdfuncao=CBFILIAL&nmusuario=" + crud.selecionarNmUsuarioSistema() + "";
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
                    //JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                    //mylist.add(map);

                    if (!s.equals("null") && !s.equals("") && s.equals("true")) {
                        //crud.insereCdClienteBanco(jObject.getString("CdCliente"));
                        crud.atualizarAutorizaTrocaFilial("S");
                        //
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

    private class LoadingAsyncOpcoes extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Filial.this);

        String sincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            FU_SincronizarClientesProdutosFilial();
            BancoController crud = new BancoController(getBaseContext());

            if(crud.buscaPrecoIndividualizado().equals("S")) {
                if (FU_SincronizaPrecoFilial()) {
                    sincronizou = "S";

                } else {
                    sincronizou = "N";
                }
            }else{
                sincronizou = "S";
            }

            //FU_Sincronizar();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            if (sincronizou.equals("S")) {
                MensagemUtil.addMsg(Filial.this, "Produtos sincronizados com sucesso!");
                Intent intent = new Intent(Filial.this, Pedidos.class);
                startActivity(intent);

            } else {
                MensagemUtil.addMsg(Filial.this, "Não foi possivel realizar a sincronização dos produtos. Favor verificar a conexão com a internet e o banco de dados!");
            }


        }
    }

    protected boolean FU_SincronizaPrecoFilial(){

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 10000;

        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            //HttpParams httpParams = new BasicHttpParams();
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            //
            // p.setParameter("name", pvo.getName());
            //p.setParameter("user", "1");

            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);

            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=produtoprecofilial&cdfilial=" + crud.buscaFilialSelecionada() + "";
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
                    try {
                        //JSONObject jObject = new JSONObject(s);

                            /*map.put("idusers", jObject.getString("RzSocial"));
                            map.put("UserName", jObject.getString("Endereco"));
                            map.put("FullName", jObject.getString("Telefone"));*/

                            /*map.put("idusers", jObject.getString("idusers"));
                            map.put("UserName", jObject.getString("UserName"));
                            map.put("FullName", jObject.getString("FullName"));*/

                        //mylist.add(map);

                        if (!jObject.getString("CdProduto").equals("null")) {
                            //crud.insereCdClienteBanco(jObject.getString("CdCliente"));

                            String VA_DescontoA = "0";
                            String VA_DescontoB = "0";
                            String VA_DescontoC = "0";
                            String VA_DescontoD = "0";
                            String VA_DescontoE = "0";
                            String VA_DescontoFidelidade = "0";

                            String VA_CdProduto = jObject.getString("CdProduto");

                            if(Double.parseDouble(jObject.getString("VlVenda")) > 0){
                                crud.atualizarValorUnitarioFilial(VA_CdProduto, jObject.getString("VlVenda"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoA")) > 0) {
                                crud.atualizarDescontoA(VA_CdProduto, jObject.getString("PercDescontoA"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoB")) > 0) {
                                crud.atualizarDescontoB(VA_CdProduto, jObject.getString("PercDescontoB"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoC")) > 0) {
                                crud.atualizarDescontoC(VA_CdProduto, jObject.getString("PercDescontoC"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoD")) > 0) {
                                crud.atualizarDescontoD(VA_CdProduto, jObject.getString("PercDescontoD"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoE")) > 0) {
                                crud.atualizarDescontoE(VA_CdProduto, jObject.getString("PercDescontoE"));
                            }
                            if (Double.parseDouble(jObject.getString("PercDescontoFidelidade")) > 0) {
                                crud.atualizarDescontoFidelidade(VA_CdProduto, jObject.getString("PercDescontoFidelidade"));
                            }

                            //crud.atualizarDescontos(jObject.getString("CdProduto"), jObject.getString("VlVenda"), VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);
                            //crud.inserirProdutoFilial(jObject.getString("CdProduto"), sDescricao, jObject.getString("EstAtual"), jObjectPrecoFilial.getString("VlVenda"), jObject.getString("DtUltAlteracao"), VA_Desconto, VA_DescontoA, VA_DescontoB, VA_DescontoC, VA_DescontoD, VA_DescontoE, VA_DescontoFidelidade);

                            //
                        }
                    }catch(Exception e3) {
                        e3.printStackTrace();

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
