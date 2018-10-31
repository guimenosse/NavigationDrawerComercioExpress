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
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ManutencaoPedidos extends AppCompatActivity {

    ImageView imagemProdutos;
    TextView lb_descricaoProdutoPedidos, lb_descricaoProdutoPedidosTeste, lb_ocultarProdutos, lb_valorTotal;
    EditText tb_percDesconto, tb_condPgto, tb_obsPedido, tb_vldesconto;

    String operacao, numpedido, fgcancelado, fgenviado;
    int VA_ContProdutos;
    double VL_valorTotal;
    public String nmcliente;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(savedInstanceState != null){
            EstadoPedido estadoPedido = (EstadoPedido) savedInstanceState.getSerializable(EstadoPedido.KEY);
            TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
            tb_rzsocial.setText(estadoPedido.cliente);
        }

        FU_VisibilidadeProdutos(false);

        operacao = this.getIntent().getStringExtra("operacao");

        if(operacao.equals("A")){
            numpedido = this.getIntent().getStringExtra("codigo");
            FU_CarregaPedido();
            FU_CarregaItemPedido();
            FU_CalcularValorPedido();
        }else{
            fgcancelado = "N";
            fgenviado = "N";
        }
        BancoController crud = new BancoController(getBaseContext());

        //----------------------------- Comandos para o botao de clientes, para seleção ou alteração do cliente do pedido---------
        Button sc_selecionarCliente = (Button) findViewById(R.id.sc_selecionarcliente);
        TextView lb_rzsocialClientePedidos = (TextView)findViewById(R.id.lb_rzsocialClientePedidos);

        lb_rzsocialClientePedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondActivity;
                secondActivity = new Intent(ManutencaoPedidos.this, SelecaoCliente.class);
                //secondActivity.putExtra("codigo", "0");
                startActivityForResult(secondActivity, 1);
            }
        });

        lb_ocultarProdutos = (TextView)findViewById(R.id.lb_ocultarProdutos);

        TextView sc_selecionarProduto = (TextView)findViewById(R.id.lb_descricaoProdutoPedidosTeste);

        sc_selecionarProduto.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        BancoController crud = new BancoController(getBaseContext());
                                                        TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
                                                        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                                                        TextView tb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);
                                                        EditText tb_condpgto = (EditText) findViewById(R.id.tb_condPgto);
                                                        EditText tb_percdesconto = (EditText)findViewById(R.id.tb_percdescontoPedido);
                                                        EditText tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
                                                        EditText tb_obs = (EditText) findViewById(R.id.tb_obsPedido);
                                                        if(operacao.equals("I")) {
                                                            numpedido = crud.carregaNumPedido();

                                                        }
                                                        FU_CalcularValorPedido();
                                                        crud.alteraPedido(numpedido, tb_cdcliente.getText().toString(), tb_rzsocial.getText().toString().toUpperCase(), tb_condpgto.getText().toString().toUpperCase(), lb_valorTotal.getText().toString().substring(0, 27), tb_percdesconto.getText().toString(), tb_vldesconto.getText().toString(), tb_obs.getText().toString().toUpperCase(), "ABERTO");

                                                        if(lb_ocultarProdutos.getText().toString().equals("Ocultar Produtos")) {
                                                        /*---------------- É necessário ocultar a listview quando clica em adicionar produtos ------------------*/
                                                            ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);
                                                            lista.setVisibility(View.INVISIBLE);
                                                            lb_ocultarProdutos.setText("Mostrar Produtos");

                                                            TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissao);
                                                            RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_fundoOcultarProdutos);
                                                            p_endereco.setMargins(0, 25, 0, 0);
                                                            lb_dtemissao.setLayoutParams(p_endereco);
                                                        }

                                                        Intent secondActivity;
                                                        secondActivity = new Intent(ManutencaoPedidos.this, AdicionarProdutosCustomizada.class);
                                                        secondActivity.putExtra("numpedido", numpedido);
                                                        secondActivity.putExtra("selecaoProdutos", "S");
                                                        startActivityForResult(secondActivity, 2);
            }
        }
        );



        lb_ocultarProdutos.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                    ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);
                    if(lb_ocultarProdutos.getText().toString().equals("Mostrar Produtos")) {
                        lista.setVisibility(View.VISIBLE);
                        lb_ocultarProdutos.setText("Ocultar Produtos");

                        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissao);
                        RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        p_endereco.addRule(RelativeLayout.BELOW, R.id.listViewItemPedidos);
                        p_endereco.setMargins(0, 25, 0, 0);
                        lb_dtemissao.setLayoutParams(p_endereco);

                        calculeHeightListView();
                    }else{
                        lista.setVisibility(View.INVISIBLE);
                        lb_ocultarProdutos.setText("Mostrar Produtos");

                        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissao);
                        RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_fundoOcultarProdutos);
                        p_endereco.setMargins(0, 25, 0, 0);
                        lb_dtemissao.setLayoutParams(p_endereco);

                        calculeHeigthListViewReverse();
                    }
                }
            }
        );

        //Fazer a mudança do valor do percentual de desconto também.

        tb_vldesconto = (EditText)findViewById(R.id.tb_vldescontoPedido);
        tb_percDesconto = (EditText)findViewById(R.id.tb_percdescontoPedido);
        tb_vldesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if(tb_vldesconto.getText().toString().trim().equals("")){
                        tb_percDesconto.setText("");
                    }else {
                        BancoController crud = new BancoController(getBaseContext());


                        if (crud.verificaItemPedido(numpedido).equals("S")) {
                            TextView lb_valorTotalProdutos = (TextView) findViewById(R.id.lb_valorTotalProdutos);
                            //String.format("%.2f", vltotal);

                            //lb_valorTotalProdutos.setText("Total: R$" + String.format("%.2f", VL_valorTotal));
                            double valorBruto = Double.parseDouble(lb_valorTotalProdutos.getText().toString().replace("Total: R$", "").replace(",", "."));
                            double valorLiquido = valorBruto - Double.parseDouble(tb_vldesconto.getText().toString().replace(",", "."));

                            double porcentagem = ((valorBruto - valorLiquido) / valorBruto) * 100;
                            tb_percDesconto.setText(String.format("%.5f", porcentagem).replace(",", "."));

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        /*tb_percDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if(tb_percDesconto.getText().toString().trim().equals("")){
                        tb_vldesconto.setText("");
                    }else {
                        BancoController crud = new BancoController(getBaseContext());


                        if (crud.verificaItemPedido(numpedido).equals("S")) {
                            TextView lb_valorTotalProdutos = (TextView) findViewById(R.id.lb_valorTotalProdutos);
                            //String.format("%.2f", vltotal);

                            //lb_valorTotalProdutos.setText("Total: R$" + String.format("%.2f", VL_valorTotal));
                            double valorBruto = Double.parseDouble(lb_valorTotalProdutos.getText().toString().replace("Total: R$", "").replace(",", "."));
                            double valorPercDesconto = Double.parseDouble(tb_percDesconto.getText().toString().replace(",", "."));

                            double porcentagem = valorPercDesconto / 100;
                            double resultado = VL_valorTotal * porcentagem;
                            double resultadoTotal = VL_valorTotal - resultado;
                            resultado = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
                            String valor = String.format("%.2f", resultadoTotal);

                            tb_vldesconto.setText(String.format("%.2f", resultado));

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/

        if(fgenviado.equals("S") || fgcancelado.equals("S")){
            FU_BloqueiaCampos(false);

            imagemProdutos = (ImageView) findViewById(R.id.imageViewProduto);

            lb_descricaoProdutoPedidos = (TextView) findViewById(R.id.lb_descricaoProdutoPedidos);
            lb_descricaoProdutoPedidosTeste = (TextView) findViewById(R.id.lb_descricaoProdutoPedidosTeste);

            tb_percDesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);
            tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
            tb_condPgto = (EditText) findViewById(R.id.tb_condPgto);
            tb_obsPedido = (EditText) findViewById(R.id.tb_obsPedido);

            imagemProdutos.setEnabled(false);

            lb_descricaoProdutoPedidos.setEnabled(false);
            lb_descricaoProdutoPedidosTeste.setEnabled(false);

            tb_percDesconto.setEnabled(false);
            tb_vldesconto.setEnabled(false);
            tb_condPgto.setEnabled(false);
            tb_obsPedido.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manutencaopedido, menu);
        return true;
    }

    public boolean FU_ConsistePedido(){
        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
        if(tb_rzsocial.getText().equals("Toque para selecionar um cliente...")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Favor selecionar um cliente para abertura do pedido!");
            return false;
        }

        EditText tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
        if (!tb_percDesconto.getText().toString().trim().equals("") && Double.parseDouble(tb_percDesconto.getText().toString()) > 99) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não é permitido um desconto superior a 99%!");
            return false;
        }

        BancoController crud = new BancoController(getBaseContext());
        String VA_possuiItemPedido = crud.verificaItemPedido(numpedido);

        if(VA_possuiItemPedido.equals("N")){
            if(!tb_vldesconto.getText().toString().trim().equals("") && Double.parseDouble(tb_vldesconto.getText().toString()) > 0){
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi adicionado nenhum produto no pedido. Não é permitido informar nenhum desconto para este pedido!");
                return  false;
            }
        }else {
            if (!tb_percDesconto.getText().toString().trim().equals("") && Double.parseDouble(tb_vldesconto.getText().toString()) > 0) {
                Cursor cursor = crud.carregaItemPedido(numpedido);
                double VA_VlMaxDescPermitido = 0;
                double VA_VlDescontoItens = 0;
                try {
                    VA_VlMaxDescPermitido += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                    VA_VlDescontoItens += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    while (cursor.moveToNext()) {
                        VA_VlMaxDescPermitido += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                        VA_VlDescontoItens += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double VA_vlDesconto = Double.parseDouble(tb_percDesconto.getText().toString());
                double porcentagem = VA_vlDesconto / 100;
                double resultado = VL_valorTotal * porcentagem;
                if ((resultado + VA_VlDescontoItens) > VA_VlMaxDescPermitido) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Desconto informado é maior que o permitido!");
                    return false;
                }
            }
            if (!tb_vldesconto.getText().toString().trim().equals("") && Double.parseDouble(tb_vldesconto.getText().toString()) > 0) {
                Cursor cursor = crud.carregaItemPedido(numpedido);
                double VA_VlMaxDescPermitido = 0;
                double VA_VlDescontoItens = 0;
                try {
                    VA_VlMaxDescPermitido += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                    VA_VlDescontoItens += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    while (cursor.moveToNext()) {
                        VA_VlMaxDescPermitido += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                        VA_VlDescontoItens += Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
                double resultado = Double.parseDouble(tb_vldesconto.getText().toString());

                if ((resultado + VA_VlDescontoItens) > VA_VlMaxDescPermitido) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Desconto informado é maior que o permitido!");
                    return false;
                }
            }

        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            // Id correspondente ao botão Up/Home da actionbar
            case R.id.action_salvarpedido:
                if (fgenviado.equals("S")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido já foi enviado para o sistema online.");
                } else if(fgcancelado.equals("S")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser salvo, pois o pedido foi cancelado!");
                }else {
                    if (FU_ConsistePedido()) {
                        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                        EditText tb_vldesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);

                        BancoController crud = new BancoController(getBaseContext());
                        try {
                            TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
                            tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                            TextView tb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);
                            EditText tb_condpgto = (EditText) findViewById(R.id.tb_condPgto);
                            tb_percDesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);
                            tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
                            EditText tb_obs = (EditText) findViewById(R.id.tb_obsPedido);
                            if (operacao.equals("I")) {
                                numpedido = crud.carregaNumPedido();
                            }
                            if (fgcancelado.equals("N") && fgenviado.equals("N")) {

                                if (crud.verificaItemPedido(numpedido).equals("S")) {

                                    crud.alteraPedido(numpedido, tb_cdcliente.getText().toString(), tb_rzsocial.getText().toString().toUpperCase(), tb_condpgto.getText().toString().toUpperCase(), lb_valorTotal.getText().toString().substring(0, 27), tb_percDesconto.getText().toString(), tb_vldesconto.getText().toString(), tb_obs.getText().toString().toUpperCase(), "ABERTO");
                                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido salvo com sucesso!");

                                } else {
                                    crud.alteraPedido(numpedido, tb_cdcliente.getText().toString(), tb_rzsocial.getText().toString().toUpperCase(), tb_condpgto.getText().toString().toUpperCase(), "", tb_percDesconto.getText().toString(), tb_vldesconto.getText().toString(), tb_obs.getText().toString().toUpperCase(), "ABERTO");
                                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido salvo com sucesso!");
                                }
                            } else {
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser alterado, pois o pedido foi cancelado!");
                            }
                            Intent intent = new Intent(ManutencaoPedidos.this, Pedidos.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel salvar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }
                    }

                }
                return true;
            case R.id.action_enviaronline:

                if (fgenviado.equals("S")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido já foi enviado para o sistema online.");
                } else if(fgcancelado.equals("S")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser enviado, pois o pedido foi cancelado!");
                }else {
                    if(FU_ConsistePedido()) {
                        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                        EditText tb_vldesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);

                        BancoController crud = new BancoController(getBaseContext());
                        try {
                            TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
                            tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                            TextView tb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);
                            EditText tb_condpgto = (EditText) findViewById(R.id.tb_condPgto);
                            tb_percDesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);
                            tb_vldesconto = (EditText)findViewById(R.id.tb_vldescontoPedido);
                            EditText tb_obs = (EditText) findViewById(R.id.tb_obsPedido);
                            if (operacao.equals("I")) {
                                numpedido = crud.carregaNumPedido();
                            }
                            crud.alteraPedido(numpedido, tb_cdcliente.getText().toString(), tb_rzsocial.getText().toString().toUpperCase(), tb_condpgto.getText().toString().toUpperCase(), lb_valorTotal.getText().toString().substring(0, 27), tb_percDesconto.getText().toString(), tb_vldesconto.getText().toString(), tb_obs.getText().toString().toUpperCase(), "ABERTO");

                            if (FU_ConsisteEnviarOnline()) {
                                if (verificaConexao()) {
                                    new LoadingAsyncOpcoes().execute();

                                } else {
                                    MensagemUtil.addMsg(ManutencaoPedidos.this, "É necessário ter conexão com a internet para o envio do pedido!");
                                }
                            }

                        } catch (Exception e) {
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel enviar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                        }
                    }

                }

                return true;
            case R.id.action_cancelarpedido:

                TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);

                if(tb_rzsocial.getText().toString().equals("Toque para selecionar um cliente...")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não foi aberto. Não será possivel realizar o cancelamento sem que o pedido seja aberto!");
                    return false;
                }else {

                    if (fgenviado.equals("S")) {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser cancelado, pois já foi enviado para o sistema online.");
                    } else {
                        builder = new AlertDialog.Builder(this);

                        //define o titulo
                        builder.setTitle("Cancelar Pedido");
                        //define a mensagem
                        builder.setMessage("Deseja mesmo cancelar o pedido " + numpedido + "?");

                        //define um botão como positivo
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                                BancoController crud = new BancoController(getBaseContext());
                                try {
                                    crud.alteraSituacaoPedido(numpedido, "CANCELADO");
                                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido cancelado com sucesso!");
                                    Intent intent = new Intent(ManutencaoPedidos.this, Pedidos.class);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
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
                    }
                }
                return true;
            case R.id.action_excluirpedido:
                tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);

                if(tb_rzsocial.getText().toString().equals("Toque para selecionar um cliente...")) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não foi aberto. Não será possivel realizar a exclusão sem que o pedido seja aberto!");
                    return false;
                }else {
                    builder = new AlertDialog.Builder(this);

                    //define o titulo
                    builder.setTitle("Excluir Pedido");
                    //define a mensagem
                    builder.setMessage("Deseja mesmo excluir o pedido " + numpedido + "?");

                    //define um botão como positivo
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                            BancoController crud = new BancoController(getBaseContext());
                            try {
                                crud.deletaPedido(Integer.parseInt(numpedido));
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido excluido com sucesso!");
                                Intent intent = new Intent(ManutencaoPedidos.this, Pedidos.class);
                                startActivity(intent);
                            } catch (Exception e) {
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
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
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean FU_ConsisteEnviarOnline(){
        tb_condPgto = (EditText)findViewById(R.id.tb_condPgto);
        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);

        String VA_possuiItemPedido = "N";
        BancoController crud = new BancoController(getBaseContext());
        VA_possuiItemPedido = crud.verificaItemPedido(numpedido);

        if(tb_rzsocial.getText().toString().equals("Toque para selecionar um cliente...")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Favor selecionar um cliente no pedido!");
            return false;
        }
        if(!VA_possuiItemPedido.equals("S")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Deve ser informado ao menos um produto no pedido.");
            return  false;
        }
        if(tb_condPgto.getText().toString().trim().equals("")){
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Deve ser informada a forma de pagamento do pedido.");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int codigo, int resultado, Intent intent) {
        BancoController crud = new BancoController(getBaseContext());
        if(codigo == 1) {
            if(resultado == 0){

            }else {
                Cursor cursor = crud.carregaClienteById(resultado);
                TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
                tb_cdcliente.setText(String.valueOf(resultado));
                TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null")) {
                        tb_rzsocial.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).toUpperCase());
                        nmcliente = tb_rzsocial.getText().toString();
                        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);
                        String cdcliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE));
                        tb_cdcliente.setText(cdcliente);
                        if (lb_dtemissao.getText().toString().trim().equals("")) {
                            lb_dtemissao.setText(getDateTime().substring(0, 16));

                            imagemProdutos = (ImageView) findViewById(R.id.imageViewProduto);

                            lb_descricaoProdutoPedidos = (TextView) findViewById(R.id.lb_descricaoProdutoPedidos);
                            lb_descricaoProdutoPedidosTeste = (TextView) findViewById(R.id.lb_descricaoProdutoPedidosTeste);

                            tb_percDesconto = (EditText) findViewById(R.id.tb_percdescontoPedido);
                            tb_vldesconto = (EditText) findViewById(R.id.tb_vldescontoPedido);
                            tb_condPgto = (EditText) findViewById(R.id.tb_condPgto);
                            tb_obsPedido = (EditText) findViewById(R.id.tb_obsPedido);

                            imagemProdutos.setEnabled(true);

                            lb_descricaoProdutoPedidos.setEnabled(true);
                            lb_descricaoProdutoPedidosTeste.setEnabled(true);

                            tb_percDesconto.setEnabled(true);
                            tb_vldesconto.setEnabled(true);
                            tb_condPgto.setEnabled(true);
                            tb_obsPedido.setEnabled(true);

                            String cdVendedor = crud.selecionaVendedor();
                            crud.abrirPedido(cdcliente, nmcliente, lb_dtemissao.getText().toString(), cdVendedor, "ABERTO");


                        }
                    }
                } catch (Exception e) {
                    tb_rzsocial.setText("");
                }
            }
        }else{
            try {
                FU_CarregaItemPedido();
                FU_CalcularValorPedido();
            }catch (Exception e) {
                MensagemUtil.addMsg(ManutencaoPedidos.this, e.getMessage().toString());
            }
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);

        bundle.putSerializable(EstadoPedido.KEY, new EstadoPedido(nmcliente));
    }

    protected void FU_VisibilidadeProdutos(Boolean visibilidade){
        TextView lb_fundoOcultarProdutos = (TextView)findViewById(R.id.lb_fundoOcultarProdutos);
        TextView lb_ocultarProdutos = (TextView)findViewById(R.id.lb_ocultarProdutos);
        TextView lb_valorTotalProdutos = (TextView)findViewById(R.id.lb_valorTotalProdutos);
        TextView lb_qtdeTotalProdutos = (TextView)findViewById(R.id.lb_qtdeTotalProdutos);

        TextView tb_dtemissao = (TextView)findViewById(R.id.tb_dtemissao);


        if(visibilidade == true){
            lb_fundoOcultarProdutos.setVisibility(View.VISIBLE);
            lb_ocultarProdutos.setVisibility(View.VISIBLE);
            lb_valorTotalProdutos.setVisibility(View.VISIBLE);
            lb_qtdeTotalProdutos.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_fundoOcultarProdutos);
            p_endereco.setMargins(0, 25, 0, 0);
            tb_dtemissao.setLayoutParams(p_endereco);
        }else{
            lb_fundoOcultarProdutos.setVisibility(View.INVISIBLE);
            lb_ocultarProdutos.setVisibility(View.INVISIBLE);
            lb_valorTotalProdutos.setVisibility(View.INVISIBLE);
            lb_qtdeTotalProdutos.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_descricaoProdutoPedidos);
            p_endereco.setMargins(0, 25, 0, 0);
            tb_dtemissao.setLayoutParams(p_endereco);
        }
    }

    protected void FU_CalcularValorPedido(){

        tb_percDesconto = (EditText)findViewById(R.id.tb_percdescontoPedido);
        lb_valorTotal = (TextView)findViewById(R.id.lb_valorTotal);
        double VA_vlDesconto = 0;
        if(tb_percDesconto.getText().toString().trim().equals("") || tb_percDesconto.getText().toString().equals("0")){
            String valor = String.format("%.2f", VL_valorTotal);
                lb_valorTotal.setText("Total Liquido do Pedido: R$" + valor);
            //lb_valorTotal.setText("Total Liquido do Pedido: R$" + String.valueOf(VL_valorTotal));
        }else{
            VA_vlDesconto = Double.parseDouble(tb_percDesconto.getText().toString());
            double porcentagem = VA_vlDesconto / 100;
            double resultado = VL_valorTotal * porcentagem;
            double resultadoTotal = VL_valorTotal - resultado;
            resultadoTotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
            String valor = String.format("%.2f", resultadoTotal);
            lb_valorTotal.setText("Total Liquido do Pedido: R$" + valor);
        }


    }

    protected void FU_CarregaPedido(){
        BancoController crud = new BancoController(getBaseContext());
        Cursor cursor = crud.carregaDadosPedido(numpedido);

        TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);

        imagemProdutos = (ImageView)findViewById(R.id.imageViewProduto);

        lb_descricaoProdutoPedidos = (TextView)findViewById(R.id.lb_descricaoProdutoPedidos);
        lb_descricaoProdutoPedidosTeste = (TextView)findViewById(R.id.lb_descricaoProdutoPedidosTeste);
        lb_valorTotal = (TextView)findViewById(R.id.lb_valorTotal);

        tb_percDesconto = (EditText)findViewById(R.id.tb_percdescontoPedido);
        tb_vldesconto = (EditText)findViewById(R.id.tb_vldescontoPedido);
        tb_condPgto = (EditText)findViewById(R.id.tb_condPgto);
        tb_obsPedido = (EditText)findViewById(R.id.tb_obsPedido);

        imagemProdutos.setEnabled(true);

        lb_descricaoProdutoPedidos.setEnabled(true);
        lb_descricaoProdutoPedidosTeste.setEnabled(true);

        tb_percDesconto.setEnabled(true);
        tb_vldesconto.setEnabled(true);
        tb_condPgto.setEnabled(true);
        tb_obsPedido.setEnabled(true);

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null")) {
                tb_cdcliente.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)));
            }
        }catch (Exception e){
            tb_cdcliente.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null")) {
                tb_rzsocial.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
            }
        }catch (Exception e){
            tb_rzsocial.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null")) {
                tb_condPgto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)));
            }
        }catch (Exception e){
            tb_condPgto.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null")) {
                tb_percDesconto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
            }
        }catch (Exception e){
            tb_percDesconto.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null")) {
                tb_vldesconto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
            }
        }catch (Exception e){
            tb_vldesconto.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).equals("null")) {
                tb_obsPedido.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)));
            }
        }catch (Exception e){
            tb_obsPedido.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).equals("null")) {
                lb_dtemissao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)));
            }
        }catch (Exception e){
            lb_dtemissao.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null")) {
                lb_valorTotal.setText("Total Liquido do Pedido: R$" + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                //lb_dtemissao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)));
            }
        }catch (Exception e){
            lb_valorTotal.setText("Total Liquido do Pedido: R$0,00");
        }

        if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FGSITUACAO)).equals("CANCELADO")) {
            FU_BloqueiaCampos(false);
            fgcancelado = "S";
            fgenviado = "N";
        }else if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FGSITUACAO)).equals("ENVIADO")) {
            fgenviado = "S";
            fgcancelado = "N";
        }else{
            fgcancelado = "N";
            fgenviado = "N";
        }



    }

    protected void FU_CarregaItemPedido(){
        BancoController crud = new BancoController(getBaseContext());
        String VA_possuiItemPedido = "N";
        VA_possuiItemPedido = crud.verificaItemPedido(numpedido);
        VL_valorTotal = 0;
        if(VA_possuiItemPedido.equals("S")) {
            final Cursor cursorItemPedido = crud.carregaItemPedido(numpedido);

            VL_valorTotal = 0;
            double VA_quantidadeTotal = 0;
            String VA_ValorProduto = "";
            String VA_ValorAtacado = "";
            VA_ContProdutos = 0;
            List<String> descricaoPedidos = new ArrayList<>();
            List<String> itensRestantesPedidos = new ArrayList<>();
            List<String> valorProdutos = new ArrayList<>();
            List<String> valorAtacado = new ArrayList<>();
            if (cursorItemPedido != null) {
                descricaoPedidos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                itensRestantesPedidos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                valorProdutos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                VA_quantidadeTotal = VA_quantidadeTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                VL_valorTotal = VL_valorTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                VA_ContProdutos = VA_ContProdutos + 1;
                while (cursorItemPedido.moveToNext()) {
                    descricaoPedidos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                    itensRestantesPedidos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                    valorProdutos.add(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                    VA_quantidadeTotal = VA_quantidadeTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                    VL_valorTotal = VL_valorTotal + Double.parseDouble(cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                    VA_ContProdutos = VA_ContProdutos + 1;

                }

            }

            FU_VisibilidadeProdutos(true);
                /*descricaoPedidos.add("CATAFLAM PARA HEMATOMAS");
                itensRestantesPedidos.add("10");
                VA_ContProdutos = VA_ContProdutos + 1;

                descricaoPedidos.add("PRODUTO PARA TESTE DE LISTVIEW NO ANDROID");
                itensRestantesPedidos.add("5");
                VA_ContProdutos = VA_ContProdutos + 1;


                descricaoPedidos.add("PRODUTO PARA TESTE DE LISTVIEW NO ANDROID PARA VER SE ELE COLOCA MAIS LINHAS");
                itensRestantesPedidos.add("5");
                VA_ContProdutos = VA_ContProdutos + 1;*/

            TextView lb_valorTotalProdutos = (TextView) findViewById(R.id.lb_valorTotalProdutos);
            TextView lb_qtdeTotalProdutos = (TextView) findViewById(R.id.lb_qtdeTotalProdutos);
            //String.format("%.2f", vltotal);

            lb_valorTotalProdutos.setText("Total: R$" +  String.format("%.2f", VL_valorTotal));
            lb_qtdeTotalProdutos.setText("Qtde: " + String.valueOf(VA_quantidadeTotal) + " item(ns)");

            List<Integer> icones = new ArrayList<>();


            for (int i = 0; i < VA_ContProdutos; i++) {
                icones.add(R.drawable.sem_foto);
            }

            View.OnClickListener myhandler = new View.OnClickListener() {
                public void onClick(View v) {
                    // MY QUESTION STARTS HERE!!!
                    // IF b1 do this
                    // IF b2 do this
                    // MY QUESTION ENDS HERE!!!
                }
            };

            ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);

            ListaProdutosPedido adapter = new ListaProdutosPedido(this, icones, descricaoPedidos, itensRestantesPedidos, valorProdutos);
            lista.setAdapter(adapter);





            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String codigo;
                    try {
                        ListView listaProdutos = (ListView) findViewById(R.id.listViewItemPedidos);
                        listaProdutos.setVisibility(View.   INVISIBLE);
                        lb_ocultarProdutos.setText("Mostrar Produtos");

                        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissao);
                        RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_fundoOcultarProdutos);
                        p_endereco.setMargins(0, 25, 0, 0);
                        lb_dtemissao.setLayoutParams(p_endereco);

                        calculeHeigthListViewReverse();
                        ListAdapter adapter2 = listaProdutos.getAdapter();
                        int lenght = adapter2.getCount();
                        if(position == 0){
                            cursorItemPedido.moveToPosition(position);
                        }else if(position < lenght - 1) {
                            cursorItemPedido.moveToPosition(position);
                        }else{
                            cursorItemPedido.moveToPosition(position + 1);
                        }

                        codigo = cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent(ManutencaoPedidos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("numpedido", numpedido);
                        intent.putExtra("alteracao", "S");
                        //startActivity(intent);
                        startActivityForResult(intent, 2);
                    } catch (Exception e) {
                        ListView listaProdutos = (ListView) findViewById(R.id.listViewItemPedidos);
                        listaProdutos.setVisibility(View.   INVISIBLE);
                        lb_ocultarProdutos.setText("Mostrar Produtos");

                        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissao);
                        RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        p_endereco.addRule(RelativeLayout.BELOW, R.id.lb_fundoOcultarProdutos);
                        p_endereco.setMargins(0, 25, 0, 0);
                        lb_dtemissao.setLayoutParams(p_endereco);

                        calculeHeigthListViewReverse();

                        cursorItemPedido.moveToPosition(position);
                        codigo = cursorItemPedido.getString(cursorItemPedido.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent(ManutencaoPedidos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("numpedido", numpedido);
                        intent.putExtra("alteracao", "S");
                        //startActivity(intent);
                        startActivityForResult(intent, 2);
                    }
                }
            });
        }else{
            TextView lb_valorTotalProdutos = (TextView) findViewById(R.id.lb_valorTotalProdutos);
            TextView lb_qtdeTotalProdutos = (TextView) findViewById(R.id.lb_qtdeTotalProdutos);


            lb_valorTotalProdutos.setText("Total: R$0,00");
            lb_qtdeTotalProdutos.setText("Qtde: 0 item(ns)");

            FU_VisibilidadeProdutos(false);
        }
    }

    private void calculeHeightListView() {
        ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);
        int totalHeight = 0;
        ListAdapter adapter2 = lista.getAdapter();
        int lenght = adapter2.getCount();
        for (int i = 0; i < lenght; i++) {
            View listItem = adapter2.getView(i, null, lista);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lista.getLayoutParams();

        params.height = totalHeight
                + (lista.getDividerHeight() * (adapter2.getCount() - 1)) + (120);
        lista.setLayoutParams(params);
        lista.requestLayout();
    }

    private void calculeHeigthListViewReverse(){
        ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);
        int totalHeight = 0;
        ListAdapter adapter2 = lista.getAdapter();
        int lenght = adapter2.getCount();
        for (int i = 0; i < lenght; i++) {
            View listItem = adapter2.getView(i, null, lista);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lista.getLayoutParams();

        params.height = lista.getDividerHeight() * (adapter2.getCount() - 1);
        lista.setLayoutParams(params);
        lista.requestLayout();
    }

    public void FU_BloqueiaCampos(boolean bloqueia){
        TextView tb_cdcliente = (TextView) findViewById(R.id.lb_rzsocialClientePedidos);
        TextView tb_rzsocial = (TextView) findViewById(R.id.lb_rzsocialClientePedidosTeste);
        TextView lb_dtemissao = (TextView) findViewById(R.id.tb_dtemissaoResultado);

        imagemProdutos = (ImageView)findViewById(R.id.imageViewProduto);

        lb_descricaoProdutoPedidos = (TextView)findViewById(R.id.lb_descricaoProdutoPedidos);
        lb_descricaoProdutoPedidosTeste = (TextView)findViewById(R.id.lb_descricaoProdutoPedidosTeste);
        lb_valorTotal = (TextView)findViewById(R.id.lb_valorTotal);

        tb_percDesconto = (EditText)findViewById(R.id.tb_percdescontoPedido);
        tb_vldesconto = (EditText)findViewById(R.id.tb_vldescontoPedido);
        tb_condPgto = (EditText)findViewById(R.id.tb_condPgto);
        tb_obsPedido = (EditText)findViewById(R.id.tb_obsPedido);

        imagemProdutos.setEnabled(bloqueia);

        lb_descricaoProdutoPedidos.setEnabled(bloqueia);
        lb_descricaoProdutoPedidosTeste.setEnabled(bloqueia);

        tb_percDesconto.setEnabled(bloqueia);
        tb_vldesconto.setEnabled(bloqueia);
        tb_condPgto.setEnabled(bloqueia);
        tb_obsPedido.setEnabled(bloqueia);
        tb_cdcliente.setEnabled(bloqueia);
        tb_rzsocial.setEnabled(bloqueia);

        ListView lista = (ListView) findViewById(R.id.listViewItemPedidos);
        lista.setEnabled(bloqueia);

    }

    public boolean FU_EnviarPedido() {

        BancoController crud = new BancoController(getBaseContext());
        Cursor cursor = crud.carregaDadosPedido(numpedido);

        String vltotal = "", dtemissao = "", cdvendedor = "", cdemitente = "", rzsocial = "", percdesconto = "", vldesconto = "", condpgto = "", obs = "";

        String numpedidoSistema = "";

        if (cursor != null) {
            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                    vltotal = lb_valorTotal.getText().toString().replace("Total Liquido do Pedido: R$", "");
                    //vltotal = "100,00";
                }
            } catch (Exception e) {
                vltotal = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).trim().equals("")) {
                    dtemissao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                dtemissao = getDateTime();
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).trim().equals("")) {
                    cdvendedor = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR));
                }
            } catch (Exception e) {
                cdvendedor = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                    cdemitente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDEMITENTE));
                }
            } catch (Exception e) {
                cdemitente = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).trim().equals("")) {
                    rzsocial = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                rzsocial = "";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                    percdesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO));
                }else{
                    percdesconto = "0";
                }
            } catch (Exception e) {
                percdesconto = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                    vldesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO));
                }else{
                    vldesconto = "0";
                }
            } catch (Exception e) {
                vldesconto = "0";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")) {
                    condpgto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).replace(" ", "espaco");
                }
            } catch (Exception e) {
                condpgto = "DINHEIRO";
            }

            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).trim().equals("")) {
                    obs = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBS)).replace(" ", "espaco");
                }else{
                    obs = "espaco";
                }
            } catch (Exception e) {
                obs = "espaco";
            }

            obs += "  CONDIÇÃO DE PAGAMENTO INFORMADA: " + condpgto + "";
            obs = obs.replace(" ", "espaco");

            try {
                Cursor cursor_cliente = crud.carregaClienteByCdClienteNovoCliente(cdemitente);
                if(cursor_cliente.getString(cursor_cliente.getColumnIndexOrThrow(CriaBanco.FGSINCRONIZADO)).equals("N")){
                    cdemitente = FU_SincronizarCliente(cdemitente);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
                        "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inserirpedido&vltotal=" + vltotal + "&dtemissao=" + dtemissao + ""
                        + "&cdvendedor=" + cdvendedor + "&cdemitente=" + cdemitente + "&rzsocial=" + rzsocial.replace("\t", "") + "&percdesconto=" + percdesconto + "&vldesconto=" + vldesconto + ""
                        + "&condpgto=" + condpgto + "&obs=" + obs.replace("\n", "pulalinha") + "&filial=" + crud.buscaFilialSelecionada() + "";
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
                            String NumPedido = jObject.getString("NumPedido");
                            if(FU_SincronizaItemPedido(NumPedido, dtemissao)){
                                FU_AlteraSituacaoPedido(NumPedido);

                            }else{
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
        }
        return true;
    }

    public boolean FU_SincronizaItemPedido(String NumPedido, String DtEmissao){
        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaItemPedido(numpedido);

        String cdproduto = "", qtde = "", vlunitario = "",  vltotal = "", descricao = "", percdesconto = "", vldesconto = "";
        int id = 0;

        if(cursor!=null) {

            while (!cursor.isAfterLast()) {
                id += 1;
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                        cdproduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    cdproduto = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                        qtde = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    qtde = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                        vlunitario = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vlunitario = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        vltotal = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vltotal = "0";
                }

                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                        descricao = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    descricao = "";
                }
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        percdesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    percdesconto = "0";
                }
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        vldesconto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).replace(",", ".");
                        //vltotal = "100,00";
                    }
                } catch (Exception e) {
                    vldesconto = "0";
                }

                if(percdesconto.trim().equals("")){
                    percdesconto = "0";
                }


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
                            "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inseriritempedido&numpedido=" + NumPedido + "&cdproduto="
                            + cdproduto.replace(" ", "espaco") + "&id=" + id + "&qtde=" + qtde + "&vlunitario=" + vlunitario + "&vltotal=" + vltotal + "&dtemissao=" + DtEmissao + "&descricao=" +
                            descricao.replace(" ", "espaco") + "&vldesconto=" + vldesconto + "&percdesconto=" + percdesconto + "&filial=" + crud.buscaFilialSelecionada() + "";
                    HttpPost httppost = new HttpPost(url);

                    // Instantiate a GET HTTP method
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

                cursor.moveToNext();

            }
        }
        return true;

    }

    public boolean FU_AlteraSituacaoPedido(String NumPedido){
        BancoController crud = new BancoController(getBaseContext());

        String dataRegistro = getDateTime().substring(0, 16);
        int TIMEOUT_MILLISEC = 10000;
        try {
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=alteraSituacaoPedidoNovo&numpedido=" + NumPedido + "&filial=" + crud.buscaFilialSelecionada() + "&dtregistro=" + dataRegistro.replace(" ", "espaco") + "&usuario=" + crud.selecionarNmUsuarioSistema().replace(" ", "espaco") + "";
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
        return true;
    }

    public boolean FU_CancelaPedido(String NumPedido) {

        BancoController crud = new BancoController(getBaseContext());

        int TIMEOUT_MILLISEC = 10000;
        try {
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);
            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=cancelarPedido&numpedido=" + NumPedido + "";
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
        return true;
    }

    public String FU_SincronizarCliente(String cdemitente){
        String VA_CdCliente = "";

        int TIMEOUT_MILLISEC = 10000;

        BancoController crud = new BancoController(getBaseContext());

                /*String ultdtsincronizacao = crud.carregaDtSincronizacao();
                ultdtsincronizacao += "";*/
        Cursor cursor = crud.carregaClienteByCdClienteNovoCliente(cdemitente);

        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {

                String idString = "";
                String rzsocialString = "";
                String nmfantasiaString = "";
                String cepString = "";
                String enderecoString = "";
                String numeroString = "";
                String complementoString = "";
                String bairroString = "";
                String estadoString = "";
                String cidadeString = "";
                String cnpjString = "";
                String telefoneString = "";
                String telefoneAdicionalString = "";
                String faxString = "";
                String nmcontatoString = "";
                String emailString = "";
                String tipopessoaString = "";
                String tipoclienteString = "";
                String obsclienteString = "";
                String vendedorString = "99";
                String dtultalteracao = "";
                String dtcadastro = "";
                String resultado = "";

                if(!cursor.getString(cursor.getColumnIndex(CriaBanco.ID)).equals("null") && !cursor.getString(cursor.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                    idString = cursor.getString(cursor.getColumnIndex(CriaBanco.ID));
                }
                if(!cursor.getString(cursor.getColumnIndex("rzsocial")).equals("null") && !cursor.getString(cursor.getColumnIndex("rzsocial")).trim().equals("")) {
                    rzsocialString = cursor.getString(cursor.getColumnIndex("rzsocial")).replace("'", "");
                }else{
                    rzsocialString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("nmfantasia")).equals("null") && !cursor.getString(cursor.getColumnIndex("nmfantasia")).trim().equals("")) {
                    nmfantasiaString = cursor.getString(cursor.getColumnIndex("nmfantasia")).replace("'", "");
                }else{
                    nmfantasiaString = "espaco";
                }
                //nmfantasiaString = "Guilherme";
                if(!cursor.getString(cursor.getColumnIndex("cep")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cep")).trim().equals("")) {
                    cepString = cursor.getString(cursor.getColumnIndex("cep")).replace(".", "").replace("-", "");
                }else{
                    cepString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("endereco")).equals("null")  && !cursor.getString(cursor.getColumnIndex("endereco")).trim().equals("")) {
                    enderecoString = cursor.getString(cursor.getColumnIndex("endereco")).replace("'", "");
                }else{
                    enderecoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("numero")).equals("null")  && !cursor.getString(cursor.getColumnIndex("numero")).trim().equals("")) {
                    numeroString = cursor.getString(cursor.getColumnIndex("numero"));
                }else{
                    numeroString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("complemento")).equals("null")  && !cursor.getString(cursor.getColumnIndex("complemento")).trim().equals("")) {
                    complementoString = cursor.getString(cursor.getColumnIndex("complemento")).replace("'", "");
                }else{
                    complementoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("bairro")).equals("null")  && !cursor.getString(cursor.getColumnIndex("bairro")).trim().equals("")) {
                    bairroString = cursor.getString(cursor.getColumnIndex("bairro")).replace("'", "");
                }else{
                    bairroString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("uf")).equals("null")  && !cursor.getString(cursor.getColumnIndex("uf")).trim().equals("")) {
                    estadoString = cursor.getString(cursor.getColumnIndex("uf"));
                }else{
                    estadoString = "PR";
                }
                if(!cursor.getString(cursor.getColumnIndex("cidade")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cidade")).trim().equals("")) {
                    cidadeString = cursor.getString(cursor.getColumnIndex("cidade")).replace("'", "");
                }else{
                    cidadeString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("cgc")).equals("null")  && !cursor.getString(cursor.getColumnIndex("cgc")).trim().equals("")) {
                    cnpjString = cursor.getString(cursor.getColumnIndex("cgc")).replace(".", "").replace("-", "").replace("/", "");
                }else{
                    cnpjString = "0";
                }
                if(!cursor.getString(cursor.getColumnIndex("telefone")).equals("null")  && !cursor.getString(cursor.getColumnIndex("telefone")).trim().equals("")) {
                    telefoneString = cursor.getString(cursor.getColumnIndex("telefone")).replace("(", "").replace("-", "").replace(")", "");
                }else{
                    telefoneString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("telefoneadicional")).equals("null")  && !cursor.getString(cursor.getColumnIndex("telefoneadicional")).trim().equals("")) {
                    telefoneAdicionalString = cursor.getString(cursor.getColumnIndex("telefoneadicional")).replace("(", "").replace("-", "").replace(")", "");;
                }else{
                    telefoneAdicionalString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("fax")).equals("null")  && !cursor.getString(cursor.getColumnIndex("fax")).trim().equals("")) {
                    faxString = cursor.getString(cursor.getColumnIndex("fax")).replace("(", "").replace("-", "").replace(")", "");;
                }else{
                    faxString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("nmcontatocliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("nmcontatocliente")).trim().equals("")) {
                    nmcontatoString = cursor.getString(cursor.getColumnIndex("nmcontatocliente")).replace("'", "");
                }else{
                    nmcontatoString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("email")).equals("null")  && !cursor.getString(cursor.getColumnIndex("email")).trim().equals("")) {
                    emailString = cursor.getString(cursor.getColumnIndex("email")).replace("'", "");
                }else{
                    emailString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("tipopessoa")).equals("null")  && !cursor.getString(cursor.getColumnIndex("tipopessoa")).trim().equals("")) {
                    tipopessoaString = cursor.getString(cursor.getColumnIndex("tipopessoa"));
                }else{
                    tipopessoaString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("tipcliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("tipcliente")).trim().equals("")) {
                    tipoclienteString = cursor.getString(cursor.getColumnIndex("tipcliente"));
                }else{
                    tipoclienteString = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("obscliente")).equals("null")  && !cursor.getString(cursor.getColumnIndex("obscliente")).trim().equals("")) {
                    obsclienteString = cursor.getString(cursor.getColumnIndex("obscliente")).replace("'", "");
                }else{
                    obsclienteString = "espaco";
                }
                //tipoclienteString = "EXPRESSPLANO";
                if(!cursor.getString(cursor.getColumnIndex("dtultalteracao")).equals("null")  && !cursor.getString(cursor.getColumnIndex("dtultalteracao")).trim().equals("")) {
                    dtultalteracao = cursor.getString(cursor.getColumnIndex("dtultalteracao"));
                }else{
                    dtultalteracao = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("dtcadastro")).equals("null")  && !cursor.getString(cursor.getColumnIndex("dtcadastro")).trim().equals("")) {
                    dtcadastro = cursor.getString(cursor.getColumnIndex("dtcadastro"));
                }else{
                    dtcadastro = "espaco";
                }
                if(!cursor.getString(cursor.getColumnIndex("vendedor")).equals("null")  && !cursor.getString(cursor.getColumnIndex("vendedor")).trim().equals("")) {
                    vendedorString = cursor.getString(cursor.getColumnIndex("vendedor"));
                }else{
                    vendedorString = "espaco";
                }


                try {
                    // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

                    // Log.i(getClass().getSimpleName(), "send  task - start");
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams,
                            TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    //
                    HttpParams p = new BasicHttpParams();
                    // p.setParameter("name", pvo.getName());
                    p.setParameter("user", "1");

                    // Instantiate an HttpClient
                    HttpClient httpclient = new DefaultHttpClient(p);

                    String url = "http://www.planosistemas.com.br/" +
                            "WebService2.php?user=" + crud.selecionarCdClienteBanco() + "&format=json&num=10&method=inserirClienteNovo&rzsocial=" + rzsocialString.replace(" ", "espaco") + "&nmfantasia=" + nmfantasiaString.replace(" ", "espaco") +
                            "&cep=" + cepString + "&endereco=" + enderecoString.replace(" ", "espaco") + "&numero=" + numeroString + "&complemento=" + complementoString.replace(" ", "espaco") +
                            "&bairro=" + bairroString.replace(" ", "espaco") + "&uf=" + estadoString + "&cidade=" + cidadeString.replace(" ", "espaco") + "&tipopessoa=" + tipopessoaString +
                            "&cgc=" + cnpjString + "&telefone=" + telefoneString + "&telefoneadicional=" + telefoneAdicionalString + "&fax=" + faxString +
                            "&contato=" + nmcontatoString.replace(" ", "espaco") + "&email=" + emailString + "&vendedor=" + vendedorString + "&tipocliente=" + tipoclienteString.replace(" ", "espaco") + "&dtcadastro=" + dtcadastro.replace(" ", "espaco") + "" +
                            "&obscliente=" + obsclienteString.replace(" ", "espaco") + "";
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

                            if (!jObject.getString("CdCliente").equals("null")) {

                                crud.alteraClientePedido(jObject.getString("CdCliente"), Integer.parseInt(idString));
                                cdemitente =  jObject.getString("CdCliente");

                            }
                        }


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Log.i(getClass().getSimpleName(), "send  task - end");

                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
                }

                cursor.moveToNext();
            }
        }

        return cdemitente;
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

    private class LoadingAsyncOpcoes extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(ManutencaoPedidos.this);
        BancoController crud = new BancoController(getBaseContext());
        String validou = "N";

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Enviando o pedido online...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(FU_EnviarPedido()) {

                validou = "S";

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(validou.equals("S")){
                crud = new BancoController(getBaseContext());
                crud.alteraSituacaoPedido(numpedido, "ENVIADO");
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido enviado com sucesso!");
                Intent intent = new Intent(ManutencaoPedidos.this, Pedidos.class);
                startActivity(intent);
            }else {
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.");
            }
        }
    }

}
