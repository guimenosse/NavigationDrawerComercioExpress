package br.comercioexpress.plano;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import classes.CL_Usuario;
import controllers.CTL_Clientes;
import controllers.CTL_Filial;
import models.CriaBanco;
import sync.SYNC_Clientes;
import sync.SYNC_Filial;
import sync.SYNC_Produtos;

public class Filial extends AppCompatActivity {

    private ListView lista;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    String vc_Operacao;

    SYNC_Filial sync_Filial;
    SYNC_Clientes sync_Clientes;
    SYNC_Produtos sync_Produtos;

    CL_Filial cl_Filial;

    Context vc_Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vc_Operacao = this.getIntent().getStringExtra("operacao");

        sync_Filial = new SYNC_Filial(getApplicationContext());
        sync_Clientes = new SYNC_Clientes(getApplicationContext());
        vc_Context = getApplicationContext();


        if(vc_Operacao.equals("L")) {
            if(verificaConexao()) {
                LoadingAsyncSincronizar async_Sincronizar = new LoadingAsyncSincronizar();
                async_Sincronizar.execute();
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
                            LoadingAsyncSincronizar async_Sincronizar = new LoadingAsyncSincronizar();
                            async_Sincronizar.execute();
                        }else{
                            Intent secondActivity;
                            secondActivity = new Intent(Filial.this, Filial.class);
                            secondActivity.putExtra("operacao", "L");
                            startActivity(secondActivity);
                        }

                    }
                });
                alerta = builder.create();
                //Exibe
                alerta.show();
            }
        }else{
            suCarregaFiliais();
        }


    }

    protected void suCarregaFiliais(){
        builder = new AlertDialog.Builder(this);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cl_Filial = new CL_Filial();
        final CTL_Filial ctl_Filial = new CTL_Filial(getApplicationContext(), cl_Filial);

        if(cl_Filial.getTotalFiliais() > 1){
            if(cl_Filial.getAutorizaTrocaFilial().equals("N")){
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
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/
            }

            if(ctl_Filial.fuBuscarFiliais()){

                final Cursor cursor = ctl_Filial.rs_Filial;

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

                        //define o titulo
                        builder.setTitle("ATENÇÃO!");
                        //define a mensagem
                        builder.setMessage("Deseja mesmo selecionar esta filial?");

                        //define um botão como positivo
                        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                                cl_Filial = new CL_Filial();
                                CTL_Filial vf_Ctl_Filial = new CTL_Filial(vc_Context, cl_Filial);

                                if(vf_Ctl_Filial.fuBuscarFiliais()){
                                    Cursor rs_Filial = vf_Ctl_Filial.rs_Filial;

                                    //vf_Ctl_Filial.cl_Filial.setId(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.ID)));
                                    vf_Ctl_Filial.cl_Filial.setId(codigo);

                                    if(vf_Ctl_Filial.fuSelecionarFilial()){
                                        LoadingAsyncPrecoFilial async_PrecoProdutoFilial = new LoadingAsyncPrecoFilial();
                                        async_PrecoProdutoFilial.execute();
                                    }else{
                                        MensagemUtil.addMsg(Filial.this, "Não foi possível selecionar a filial");
                                    }
                                }

                            }
                        });

                        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        //cria o AlertDialog
                        alerta = builder.create();
                        //Exibe
                        alerta.show();

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
                                //cria o AlertDialog
                                alerta = builder.create();
                                //Exibe
                                alerta.show();
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
            }


        }else{
            if(ctl_Filial.fuBuscarFiliais()){
                Cursor rs_Filial = ctl_Filial.rs_Filial;

                ctl_Filial.cl_Filial.setId(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.ID)));

                if(ctl_Filial.fuSelecionarFilial()){
                    Intent intent = new Intent(Filial.this, Pedidos.class);
                    startActivity(intent);
                }else{
                    MensagemUtil.addMsg(Filial.this, "Não foi possível selecionar a filial");
                }
            }
        }
    }

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



    private class LoadingAsyncPrecoFilial extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Filial.this);

        String sincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (FU_SincronizarPrecoFilial()) {
                sincronizou = "S";

            } else {
                sincronizou = "N";
            }
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


    private class LoadingAsyncSincronizar extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog = new ProgressDialog(Filial.this);

        String fgSincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando as filiais e suas configurações...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (FU_Sincronizar()) {
                fgSincronizou = "S";
            } else {
                fgSincronizou = "N";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            if (fgSincronizou.equals("S")) {
                MensagemUtil.addMsg(Filial.this, "Filiais sincronizadas com sucesso!");
                suCarregaFiliais();
            } else {
                MensagemUtil.addMsg(Filial.this, "Não foi possivel realizar a sincronização das filiais. Favor verificar a conexão com a internet e o banco de dados!");
            }


        }
    }

    protected boolean FU_Sincronizar(){

        if(!sync_Filial.FU_SincronizarFilial()){
            return false;
        }

        if(!sync_Filial.FU_SincronizarAutorizacaoUsuario()){
            return false;
        }

        return true;
    }

    protected boolean FU_SincronizarPrecoFilial(){

        if(!sync_Clientes.FU_SincronizarConfiguracaoPrecoIndividualizado()){
            return false;
        }

        CL_Filial cl_Filial = new CL_Filial();
        CTL_Filial ctl_Filial = new CTL_Filial(vc_Context, cl_Filial);

        if(cl_Filial.getPrecoIndividualizado().equals("S")){
            if(!sync_Produtos.FU_SincronizarPrecosIndividualizadosProdutos()){
                return false;
            }
        }

        return true;
    }

}
