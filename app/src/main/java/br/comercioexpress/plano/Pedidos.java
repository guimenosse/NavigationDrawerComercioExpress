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
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import classes.CL_Clientes;
import classes.CL_Filial;
import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import classes.CL_Usuario;
import controllers.CTL_Clientes;
import controllers.CTL_Filial;
import controllers.CTL_ItemPedido;
import controllers.CTL_Pedidos;
import controllers.CTL_Usuario;
import models.CriaBanco;
import models.MDL_Usuario;
import sync.SYNC_Clientes;
import sync.SYNC_Filial;
import sync.SYNC_Pedidos;
import sync.SYNC_Produtos;

public class Pedidos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Funcoes funcoes = new Funcoes();

    Context vc_Context;

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;

    private ListView lista;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    int countCli;

    private long lastBackPressTime = 0;

    String codigo_lista, vc_Mensagem = "";

    MaterialSearchView sv_Pedidos;

    MenuItem me_ExcluirPedidos;
    MenuItem me_Sincronizar;
    MenuItem me_BuscarPedido;

    SYNC_Pedidos sync_Pedidos;
    SYNC_Clientes sync_Clientes;
    SYNC_Produtos sync_Produtos;
    SYNC_Filial sync_Filial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sv_Pedidos = (MaterialSearchView) findViewById(R.id.sv_Pedidos);
        sv_Pedidos.setVoiceSearch(true); //or false

        sync_Pedidos = new SYNC_Pedidos(getApplicationContext());
        sync_Clientes = new SYNC_Clientes(getApplicationContext());
        sync_Produtos = new SYNC_Produtos(getApplicationContext());
        sync_Filial = new SYNC_Filial(getApplicationContext());

        vc_Context = getApplicationContext();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent secondActivity;
                secondActivity = new Intent(Pedidos.this, ManutencaoPedidos.class);
                secondActivity.putExtra("operacao", "I");
                startActivityForResult(secondActivity, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            View view = navigationView.getHeaderView(0);

            TextView lb_NomeVendedor = (TextView) view.findViewById(R.id.lb_NomeVendedor);
            TextView lb_EmailVendedor = (TextView) view.findViewById(R.id.lb_EmailVendedor);

            cl_Usuario = new CL_Usuario();
            ctl_Usuario = new CTL_Usuario(getApplicationContext(), cl_Usuario);

            String vf_NmUsuario = cl_Usuario.getNmUsuarioSistema();

            cl_Filial = new CL_Filial();
            ctl_Filial = new CTL_Filial(getApplicationContext(), cl_Filial);

            String vf_Filial = cl_Filial.getNomeFilial();

            try {
                lb_NomeVendedor.setText(vf_NmUsuario);
                lb_EmailVendedor.setText(vf_Filial);
            } catch (Exception e) {
                lb_NomeVendedor.setText("");
                lb_EmailVendedor.setText("");
            }
        }catch (Exception e){
            Log.d("CARREGARVENDEDOR", "Não foi possivel carregar o vendedor por causa dessa situação: " + e.getMessage());
        }

        cl_Pedidos = new CL_Pedidos();
        ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

        suCalculaPrazoSincronizacao();
        suCarregarPedidos("");

        final TextView lb_TituloPedidos = (TextView) findViewById(R.id.lb_TituloPedidos);

        sv_Pedidos.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String vf_Nome) {

                suCarregarPedidos(vf_Nome);

                return false;
            }
        });

        sv_Pedidos.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                me_ExcluirPedidos.setVisible(false);
                me_Sincronizar.setVisible(false);
                me_BuscarPedido.setVisible(false);

                lb_TituloPedidos.setWidth(0);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                //vc_RazaoSocialNome = "";
                //su_AtualizarListaFiltros();

                me_ExcluirPedidos.setVisible(true);
                me_Sincronizar.setVisible(true);
                me_BuscarPedido.setVisible(true);

                lb_TituloPedidos.setWidth(550);
            }
        });

    }

    protected void suCarregarPedidos(String nomeRazaoSocial){

        String vf_NomeRazaoSocial = nomeRazaoSocial;

        try {
            if(ctl_Pedidos.fuCarregaTodosPedidos(vf_NomeRazaoSocial)) {

                final Cursor cursor = ctl_Pedidos.rs_Pedido;

                String[] nomeCamposPedidos = new String[]{CriaBanco.ID, CriaBanco.FGSITUACAO, CriaBanco.RZSOCIAL};
                int[] idViewsPedidos = new int[]{R.id.numPedido, R.id.situacaoPedido, R.id.clientepedido};

                SimpleCursorAdapter adaptadorPedidos = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewpedidos, cursor, nomeCamposPedidos, idViewsPedidos, 0);
                lista = (ListView) findViewById(R.id.listViewPedidos);
                lista.setAdapter(adaptadorPedidos);

                lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                     @Override
                                                     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                         final String codigo, situacao;
                                                         cursor.moveToPosition(position);
                                                         codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                                                         situacao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FGSITUACAO));

                                                         suCliqueLongoLista(codigo, situacao);

                                                         return true;
                                                     }
                                                 }

                );

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                 @Override
                                                 public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                         long id) {
                                                     String codigo;
                                                     cursor.moveToPosition(position);
                                                     codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                                                     Intent intent = new Intent(Pedidos.this, ManutencaoPedidos.class);
                                                     intent.putExtra("operacao", "A");
                                                     intent.putExtra("codigo", codigo);
                                                     //startActivity(intent);
                                                     startActivityForResult(intent, 1);
                                                 }
                                             }

                );
            }

        } catch (Exception e) {
            MensagemUtil.addMsg(Pedidos.this, e.getMessage().toString());
        }
    }

    public boolean fuConsisteEnviarOnline(String numpedido){

            String VA_possuiItemPedido = "N";
            BancoController crud = new BancoController(getBaseContext());
            Cursor cursor = crud.carregaDadosPedido(numpedido);
            VA_possuiItemPedido = crud.verificaItemPedido(numpedido);

            if(!VA_possuiItemPedido.equals("S")) {
                MensagemUtil.addMsg(Pedidos.this, "Deve ser informado ao menos um produto no pedido.");
                return  false;
            }
            if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") || cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")){
                MensagemUtil.addMsg(Pedidos.this, "Deve ser informada a forma de pagamento do pedido.");
                return false;
            }

        return true;
    }

    public boolean FU_EnviarPedido(String numPedido) {

        cl_Pedidos = new CL_Pedidos();
        cl_Pedidos.setNumPedido(numPedido);

        ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);

        if (ctl_Pedidos.fuCarregarPedido()) {

            CL_Clientes cl_Clientes = new CL_Clientes();
            cl_Clientes.setCdCliente(cl_Pedidos.getCdCliente());

            CTL_Clientes ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);

            if(ctl_Clientes.fuSelecionarCliente()){
                if(cl_Clientes.getFgSincronizado().equals("N")){
                    if(sync_Clientes.FU_SincronizarClientePedido(cl_Clientes)){
                        cl_Pedidos.setCdCliente(cl_Clientes.getCdCliente());
                    }
                }
            }

            if (sync_Pedidos.FU_EnviarPedido(cl_Pedidos)) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------Funções de menu - Excluir todos os Pedidos ---------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pedidos, menu);
        MenuItem item = menu.findItem(R.id.buscar_pedido);

        sv_Pedidos.setMenuItem(item);

        me_ExcluirPedidos = menu.findItem(R.id.action_excluirtodospedidos);
        me_Sincronizar = menu.findItem(R.id.sincronizar_pedidos);
        me_BuscarPedido = menu.findItem(R.id.buscar_pedido);

        return true;
    }

    @Override
    protected void onActivityResult(int codigo, int resultado, Intent intent) {
        suCarregarPedidos("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_excluirtodospedidos) {

                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Excluir Pedidos Enviados");
                //define a mensagem
                builder.setMessage("Deseja mesmo excluir todos os pedidos enviados?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        try {

                            CL_Pedidos cl_Pedidos = new CL_Pedidos();
                            CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                            if(ctl_Pedidos.fuCarregaTodosPedidosEnviados()){
                                Cursor rs_Pedidos = ctl_Pedidos.rs_Pedido;
                                while (!rs_Pedidos.isAfterLast()){

                                    cl_Pedidos.setNumPedido(rs_Pedidos.getString(rs_Pedidos.getColumnIndexOrThrow(CriaBanco.ID)));
                                    ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                                    ctl_Pedidos.fuDeletarPedido();

                                    rs_Pedidos.moveToNext();
                                }

                                MensagemUtil.addMsg(Pedidos.this, "Pedidos enviados excluídos com sucesso!");
                                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                                startActivity(intent);
                            }else{
                                MensagemUtil.addMsg(Pedidos.this, "Não foi encontrado nenhum pedido enviado para ser excluído.");
                            }

                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel excluir os pedidos enviados devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

                return true;

         //Atendimento 19441
        }else if(id == R.id.sincronizar_pedidos){
            if(verificaConexao()){

                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Sincronizar todos os pedidos abertos");
                //define a mensagem
                builder.setMessage("Deseja mesmo enviar online todos os seus pedidos em aberto?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            LoadingAsyncTodosPedidos async_TodosPedidos = new LoadingAsyncTodosPedidos();
                            async_TodosPedidos.execute();
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possível realizar a sincronização de todos os seus pedidos em aberto");
                        }
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }else{
                MensagemUtil.addMsg(Pedidos.this, "Por favor se conecte à internet para sincronizar todos os seus pedidos em aberto");
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------------------------------------------------------------------------

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

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            Toast.makeText(getApplicationContext(), "Pressione o botão Voltar novamente para fechar o aplicativo.", Toast.LENGTH_LONG).show();
            /*toast = Toast.makeText(getApplicationContext(), "Pressione o Botão Voltar novamente para fechar o Aplicativo.", Toast.LENGTH_LONG);
            toast.show();*/
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //super.onBackPressed();
        }
    }

     @SuppressWarnings("StatementWithEmptyBody")
     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         // Handle navigation view item clicks here.
         int id = item.getItemId();

         if (id == R.id.nav_clientes) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, HomeActivity.class);
             startActivity(secondActivity);
             // Handle the camera action
         } else if (id == R.id.nav_pedidos) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Pedidos.class);
             startActivity(secondActivity);


         } else if (id == R.id.nav_produtos) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Produtos.class);
             secondActivity.putExtra("selecaoProdutos", "N");
             startActivity(secondActivity);

         } else if (id == R.id.nav_opcoes) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, Opcoes.class);
             startActivity(secondActivity);

         } else if (id == R.id.nav_visaogeral) {
             Intent secondActivity;
             secondActivity = new Intent(Pedidos.this, VisaoGeralNova.class);
             startActivity(secondActivity);

         }
         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         drawer.closeDrawer(GravityCompat.START);
         return true;
     }


    public void suCalculaPrazoSincronizacao(){

        cl_Usuario = new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(getApplicationContext(), cl_Usuario);

        String dateStart = cl_Usuario.getDtUltimaSincronizacao();
        String dateStop = funcoes.getDateTime();

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {

            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diferencaHoras = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*Date dateCadguia = format.parse(dateStart);
            Calendar dataInicial = Calendar.getInstance();
            dataInicial.setTime(dateCadguia);


            long diferenca = System.currentTimeMillis() - dataInicial.getTimeInMillis();
            long diferencaSeg = diferenca / 1000;    //DIFERENCA EM SEGUNDOS
            long diferencaMin = diferenca / (60 * 1000);    //DIFERENCA EM MINUTOS
            long diferencaHoras = diferenca / (60 * 60 * 1000);    // DIFERENCA EM HORAS*/

            builder = new AlertDialog.Builder(this);

            /*if(diffDays > 2 && diffDays < 3){
                MensagemUtil.addMsg(Pedidos.this, "Você não fez nenhuma sincronização nas últimas 48 horas, necessária a realização da sincronização.");
            }else if(diffDays >= 3){*/
            if(diffDays >= 1){
                //define o titulo
                builder.setTitle("ATENÇÃO!");
                //define a mensagem
                builder.setMessage("Você não fez nenhuma sincronização nas últimas 24 horas, obrigatória a realização da sincronização. É necessária conexão com a internet para a realização da sincronização.");

                //define um botão como positivo
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        try {
                            if(verificaConexao()) {
                                LoadingAsyncSincronizarClientesProdutos async_Sincronizar = new LoadingAsyncSincronizarClientesProdutos();
                                async_Sincronizar.execute();
                            }else {
                                Intent secondActivity;
                                secondActivity = new Intent(Pedidos.this, Pedidos.class);
                                startActivity(secondActivity);
                            }
                        }catch (Exception e){
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização devido à seguinte situação:" + e.getMessage().toString());
                        }
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
               /* MensagemUtil.addMsg(HomeActivity.this, "Você não fez nenhuma sincronização nas últimas 72 horas, obrigatória a realização da sincronização.");
                FU_Sincronizar();*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class LoadingAsyncPedido extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Pedidos.this);
        BancoController crud = new BancoController(getBaseContext());
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Enviando o pedido online...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(FU_EnviarPedido(codigo_lista)) {
                validou = "S";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(validou.equals("S")){
                BancoController crud = new BancoController(getBaseContext());
                crud = new BancoController(getBaseContext());
                crud.alteraSituacaoPedido(codigo_lista, "ENVIADO");
                MensagemUtil.addMsg(Pedidos.this, "Pedido enviado com sucesso!");
                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                startActivity(intent);
            }else {
                MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.");
            }
        }
    }


    private class LoadingAsyncSincronizarClientesProdutos extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(Pedidos.this);

        String sincronizou = "N";
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sincronizando clientes e produtos...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (FU_Sincronizar()) {
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
                MensagemUtil.addMsg(Pedidos.this, "Clientes e produtos sincronizados com sucesso!");
            } else {
                MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar a sincronização de clientes e produtos. Favor verificar a conexão com a internet e o banco de dados!");
            }


        }
    }

    protected boolean FU_Sincronizar(){

        CL_Clientes cl_Clientes = new CL_Clientes();
        CTL_Clientes ctl_Clientes = new CTL_Clientes(vc_Context, cl_Clientes);

        if(ctl_Clientes.fuSelecionarClientesNaoSincronizados()){
            if(!sync_Clientes.FU_SincronizarClientes(ctl_Clientes.rs_Cliente)){
                return false;
            }
        }

        if(!sync_Clientes.FU_SincronizarTodosClientesServidor()){
            return false;
        }

        if(!sync_Produtos.FU_SincronizarTodosProdutosServidor()){
            return false;
        }

        if(!sync_Clientes.FU_SincronizarConfiguracaoPrecoIndividualizado()){
            return false;
        }


        if(cl_Filial.getPrecoIndividualizado().equals("S")){
            if(!sync_Produtos.FU_SincronizarPrecosIndividualizadosProdutos()){
                return false;
            }
        }

        if(!sync_Clientes.FU_SincronizarTipoCliente()){
            return false;
        }

        ctl_Usuario.cl_Usuario.setDtUltimaSincronizacao(funcoes.getDateTime());

        if(!ctl_Usuario.fuAtualizarSincronizacao()){
            return false;
        }

        return true;
    }

    /*Funções referentes ao atendimento 19441
    A primeira função ira mostrar uma mensagem na tela informando que a sincronização de todos os pedidos esta sendo feita
    e chamará a função que realizará o envio dos pedidos.
    A segunda função irá fazer uma varredura no banco de dados e verificar se existem atendimentos em aberto
    caso existam então o aplicativo irá realizar o envio de todos os pedidos
    */

    private class LoadingAsyncTodosPedidos extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialogPedidos;
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialogPedidos = ProgressDialog.show(Pedidos.this, "Sincronizando todos os pedidos", "Sincronizando todos pedidos em aberto...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (fuSincronizarTodosPedidosAbertos()) {
                validou = "S";

            } else {
                validou = "N";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialogPedidos.dismiss();

            if (validou.equals("N")) {
                if(vc_Mensagem.trim().equals("")) {
                    MensagemUtil.addMsg(Pedidos.this, "Não foi encontrado nenhum pedido em aberto para sincronização");
                }else{
                    MensagemUtil.addMsg(Pedidos.this, vc_Mensagem);
                }
            }else{
                MensagemUtil.addMsg(Pedidos.this, "Todos os seus pedidos que estavam em aberto foram sincronizados com o servidor");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    protected boolean fuSincronizarTodosPedidosAbertos(){

        if(ctl_Pedidos.fuPossuiPedidosAbertos()){
            Cursor rs_Pedidos = ctl_Pedidos.rs_Pedido;

            if(fuConsisteEnvioTodosPedidosAbertos()) {
                if (sync_Pedidos.FU_EnviarTodosPedidos(rs_Pedidos)) {
                    vc_Mensagem = "";
                    return true;
                } else {
                    vc_Mensagem = "";
                    return false;
                }
            }else{
                return false;
            }

        }else{
            return false;
        }

    }

    protected  boolean fuConsisteEnvioTodosPedidosAbertos(){

        CL_Pedidos vf_cl_Pedidos = new CL_Pedidos();
        CTL_Pedidos vf_ctl_Pedidos = new CTL_Pedidos(vc_Context, vf_cl_Pedidos);

        if(vf_ctl_Pedidos.fuPossuiPedidosAbertos()){
            Cursor vf_rs_Pedidos = vf_ctl_Pedidos.rs_Pedido;

            while (!vf_rs_Pedidos.isAfterLast()){

                String vf_NumPedido = "";
                try {
                    if (!vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                        vf_NumPedido = vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.ID));
                    }
                } catch (Exception e) {
                    vf_NumPedido = "";
                }

                String vf_CdEmitente = vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE));

                try {
                    if (!vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                        vf_CdEmitente = vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CDEMITENTE));
                    }
                } catch (Exception e) {
                    vf_CdEmitente = "";
                }

                double vf_VlTotal = 0.0, vf_VlFrete = 0.0;

                try {
                    if (!vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        vf_VlTotal = Double.parseDouble(vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                    }
                } catch (Exception e) {
                    vf_VlTotal = 0.0;
                }

                try {
                    if (!vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLFRETE)).equals("null") && !vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLFRETE)).trim().equals("")) {
                        vf_VlFrete = Double.parseDouble(vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.VLFRETE)));
                    }
                } catch (Exception e) {
                    vf_VlFrete = 0.0;
                }

                double vf_VlLiquido = vf_VlTotal - vf_VlFrete;

                String vf_CondPgto = "";

                try {
                    if (!vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") && !vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")) {
                        vf_CondPgto = vf_rs_Pedidos.getString(vf_rs_Pedidos.getColumnIndexOrThrow(CriaBanco.CONDPGTO));
                    }
                } catch (Exception e) {
                    vf_CondPgto = "";
                }


                if(vf_CdEmitente.trim().equals("")) {
                    vc_Mensagem = "Favor selecionar um cliente no pedido " + vf_NumPedido + "!";
                    return false;
                }

                if(vf_VlLiquido <= 0) {
                    vc_Mensagem = "Deve ser informado ao menos um produto no pedido " + vf_NumPedido + ".";
                    return  false;
                }
                if(vf_CondPgto.trim().equals("")){
                    vc_Mensagem = "Deve ser informada a forma de pagamento do pedido " + vf_NumPedido + "!";
                    return false;
                }

                vf_rs_Pedidos.moveToNext();
            }
        }



        return true;
    }

    //Função referente ao atendimento 19449 de duplicação do pedido
    protected void suCliqueLongoLista(String numPedido, String fgSituacao){

        final String vf_NumPedido = numPedido;
        final String vf_FgSituacao = fgSituacao;
        //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();

        itens.add("Consultar");
        itens.add("Enviar Online");
        itens.add("Cancelar Pedido");
        itens.add("Duplicar Pedido");
        itens.add("Excluir");
        itens.add("Sair");

        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), R.layout.item_alerta, itens);

        AlertDialog.Builder builder = new AlertDialog.Builder(Pedidos.this);
        builder.setTitle("O que você deseja fazer com o pedido nº" + vf_NumPedido + "? ");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0) {
                    Intent intent = new Intent(Pedidos.this, ManutencaoPedidos.class);
                    intent.putExtra("operacao", "A");
                    intent.putExtra("codigo", vf_NumPedido);
                    startActivityForResult(intent, 1);
                } else if (arg1 == 1) {

                    if(vf_FgSituacao.equals("ENVIADO")) {
                        MensagemUtil.addMsg(Pedidos.this, "Pedido já foi enviado para o sistema online.");
                    }else if(vf_FgSituacao.equals("CANCELADO")){
                        MensagemUtil.addMsg(Pedidos.this, "Pedido não pode ser enviado, pois o pedido foi cancelado!");
                    }else {
                        try {
                            if (fuConsisteEnviarOnline(vf_NumPedido)) {
                                alerta.dismiss();
                                codigo_lista = vf_NumPedido;

                                LoadingAsyncPedido async_SincronizarPedidos = new LoadingAsyncPedido();
                                async_SincronizarPedidos.execute();

                            }
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel realizar o envio do pedido");
                        }
                    }
                } else if (arg1 == 2) {

                    if(vf_FgSituacao.equals("ENVIADO")) {
                        MensagemUtil.addMsg(Pedidos.this, "Pedido não pode ser cancelado, pois já foi enviado para o sistema online.");
                    }else {

                        try {
                            cl_Pedidos = new CL_Pedidos();
                            cl_Pedidos.setNumPedido(vf_NumPedido);
                            cl_Pedidos.setFgSituacao("C");
                            ctl_Pedidos =  new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                            if(ctl_Pedidos.fuAlterarSituacaoPedido()) {
                                MensagemUtil.addMsg(Pedidos.this, "Pedido cancelado com sucesso!");
                                Intent intent = new Intent(Pedidos.this, Pedidos.class);
                                startActivity(intent);
                            }else{
                                MensagemUtil.addMsg(Pedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
                            }
                        } catch (Exception e) {
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }
                    }
                } else if (arg1 == 3) {

                    AlertDialog al_DuplicarPedido;
                    AlertDialog.Builder bu_DuplicarPedido;

                    bu_DuplicarPedido = new AlertDialog.Builder(Pedidos.this);

                    //define o titulo
                    bu_DuplicarPedido.setTitle("Duplicar pedido " + vf_NumPedido);
                    //define a mensagem
                    bu_DuplicarPedido.setMessage("Deseja mesmo duplicar o pedido " + vf_NumPedido + "?");

                    //define um botão como positivo
                    bu_DuplicarPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            CL_Pedidos cl_Pedido = new CL_Pedidos();
                            cl_Pedido.setNumPedido(vf_NumPedido);

                            CTL_Pedidos ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedido);

                            if(ctl_Pedidos.fuCarregarPedido()){
                                try {
                                    String vf_NumPedidoOriginal = cl_Pedido.getNumPedido();
                                    if(ctl_Pedidos.fuDuplicarPedido()){

                                        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                                        cl_ItemPedido.setNumPedido(cl_Pedido.getNumPedido());

                                        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

                                        if(ctl_ItemPedido.fuDuplicarItensPedidoDuplicado(vf_NumPedidoOriginal)) {
                                            MensagemUtil.addMsg(Pedidos.this, "Pedido duplicado com sucesso!");
                                            Intent intent = new Intent(Pedidos.this, Pedidos.class);
                                            startActivity(intent);
                                        }else{
                                            MensagemUtil.addMsg(Pedidos.this, "Não foi possível duplicar os itens do pedido!");
                                        }
                                    }else{
                                        MensagemUtil.addMsg(Pedidos.this, "Não foi possível duplicar o pedido!");
                                    }
                                }catch (Exception e){
                                    MensagemUtil.addMsg(Pedidos.this, "Não foi possivel duplicar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                                }

                            }
                        }
                    });
                    //define um botão como negativo.
                    bu_DuplicarPedido.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    //cria o AlertDialog
                    al_DuplicarPedido = bu_DuplicarPedido.create();
                    //Exibe
                    al_DuplicarPedido.show();

                } else if (arg1 == 4) {

                    try {
                        cl_Pedidos = new CL_Pedidos();
                        cl_Pedidos.setNumPedido(vf_NumPedido);

                        ctl_Pedidos = new CTL_Pedidos(vc_Context, cl_Pedidos);

                        if(ctl_Pedidos.fuDeletarPedido()){
                            MensagemUtil.addMsg(Pedidos.this, "Pedido excluido com sucesso!");
                            Intent intent = new Intent(Pedidos.this, Pedidos.class);
                            startActivity(intent);
                        }else{
                            MensagemUtil.addMsg(Pedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
                        }

                    } catch (Exception e) {
                        MensagemUtil.addMsg(Pedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                    }
                } else if (arg1 == 5) {
                    alerta.dismiss();
                }

            }
        });

        alerta = builder.create();
        alerta.show();

    }

}
