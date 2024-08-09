package br.comercioexpress.plano;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import classes.CL_Clientes;
import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import classes.CL_Usuario;
import controllers.CTL_Clientes;
import controllers.CTL_ItemPedido;
import controllers.CTL_Pedidos;
import controllers.CTL_Usuario;
import models.CriaBanco;
import sync.SYNC_Clientes;
import sync.SYNC_Pedidos;

public class ManutencaoPedidos extends AppCompatActivity {

    Funcoes funcoes = new Funcoes();

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    TextView lb_SelecionarClienteResultado, lb_SelecionarProdutoResultado, lb_ocultarProdutos, lb_valorTotalProdutos,
            lb_qtdeTotalProdutos, lb_dtEmissao, lb_dtEmissaoResultado, lb_vlDesconto, lb_percDesconto, lb_condPgto,
            lb_obsPedido, lb_valorTotal, lb_vlTotalResultado, lb_numPedidoServidor, lb_numPedidoServidorResultado;

    FloatingActionButton fab_SelecionarEmitente, fab_AdicionarProduto;

    ListView lv_ItensPedido;

    EditText tb_vlDesconto, tb_percDesconto, tb_vlFrete, tb_condPgto, tb_obsPedido;

    String vc_Operacao;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    FloatingActionButton fab_SalvarPedidos;
    SYNC_Pedidos sync_Pedidos;

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.NORMAL);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);

    private static Font negritoMenor = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static Font negritoDescricao = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_SalvarPedidos = (FloatingActionButton) findViewById(R.id.fab_SalvarPedido);
        fab_SalvarPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suSalvarPedido("N");
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        suInstanciarCampos();

        if (vc_Operacao.equals("A")) {
            suCarregarPedido();
            suCarregaItemPedido();
            suCalcularValorPedido();
        }

        if (cl_Pedidos.getFgSituacao().equals("E") || cl_Pedidos.getFgSituacao().equals("C")) {
            suBloqueiaCampos(false);
        }

        tb_vlDesconto.addTextChangedListener(new TextWatcher() {
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
                    if (tb_vlDesconto.getText().toString().trim().equals("")) {
                        tb_percDesconto.setText("");
                    } else {

                        if (!cl_Pedidos.getQtdeItens().trim().equals("") || !cl_Pedidos.getQtdeItens().equals("0")) {
                            double vf_VlBruto = Double.parseDouble(cl_Pedidos.getVlTotalItens());
                            double vf_VlLiquido = vf_VlBruto - Double.parseDouble(tb_vlDesconto.getText().toString().replace(",", "."));

                            double vf_Porcentagem = ((vf_VlBruto - vf_VlLiquido) / vf_VlBruto) * 100;
                            tb_percDesconto.setText(String.format("%.5f", vf_Porcentagem).replace(",", "."));

                            suCalcularValorPedido();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tb_vlFrete.addTextChangedListener(new TextWatcher() {
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
                    /*if (tb_vlFrete.getText().toString().trim().equals("")) {
                        cl_Pedidos.setVlFrete("0.00");
                    } else {
                        /*cl_Pedidos.setVlFrete(String.valueOf(Double.parseDouble(tb_vlFrete.getText().toString().replace(".", "").replace(",", "."))));

                        double vf_VlBruto = Double.parseDouble(cl_Pedidos.getVlTotalItens());
                        double vf_VlLiquido = vf_VlBruto - Double.parseDouble(tb_vlDesconto.getText().toString().replace(",", "."));

                        double vf_VltTotalPedido = vf_VlLiquido + Double.parseDouble(cl_Pedidos.getVlFrete());


                    }*/
                    suCalcularValorPedido();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manutencaopedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }else if (id == R.id.action_salvarpedido) {
            suSalvarPedido("N");
            return true;
        }else if(id == R.id.action_enviaronline){
            suEnviarOnline();
            return true;
        }else if(id == R.id.action_cancelarpedido){
            suCancelarPedido();
            return true;
        }else if(id == R.id.action_duplicarpedido) {
            suDuplicarPedido();
            return true;
        }else if(id == R.id.action_gerarpdf){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // Versão do Android é menor que API nível 14 (Ice Cream Sandwich)
                // Faça alguma coisa aqui, como chamar sua função específica
                fuVerificaPermissaoStorage();
            } else {
                suCriarPDF();
                // Versão do Android é maior ou igual a API nível 14
                // Outra lógica, se necessário
            }

            return true;
        }else if(id == R.id.action_excluirpedido){
            suExcluirPedido();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int codigo, int resultado, Intent intent) {
        BancoController crud = new BancoController(getBaseContext());
        if(codigo == 1) {
            if(resultado == 0) {

            }else if(resultado == 2){
                try {
                    suCarregaItemPedido();
                    suCalcularValorPedido();
                }catch (Exception e) {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, e.getMessage().toString());
                }
            }else {

                String vf_CdEmitente = intent.getStringExtra("codigo");

                CL_Clientes cl_Cliente = new CL_Clientes();
                cl_Cliente.setId(vf_CdEmitente);

                CTL_Clientes ctl_Clientes = new CTL_Clientes(getApplicationContext(), cl_Cliente);

                if(ctl_Clientes.fuCarregarClienteId()) {
                    try {
                        cl_Pedidos.setNomeRzSocial(cl_Cliente.getNomeRzSocial().toUpperCase());
                        cl_Pedidos.setCdCliente(cl_Cliente.getCdCliente());

                        lb_SelecionarClienteResultado.setText(cl_Pedidos.getNomeRzSocial());

                        if (cl_Pedidos.getDtEmissao().trim().equals("")) {

                            cl_Pedidos.setDtEmissao(funcoes.getDateTime().substring(0, 16));
                            lb_dtEmissaoResultado.setText(cl_Pedidos.getDtEmissao());

                            suBloqueiaCampos(true);

                            cl_Pedidos.setCdVendedor(cl_Usuario.getCdVendedorDefault());
                            cl_Pedidos.setFgSituacao("A");

                            ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                            if(ctl_Pedidos.fuAbrirPedido())
                            {
                                cl_Pedidos.setNumPedido(ctl_Pedidos.fuCarregaNumPedido());
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido aberto com sucesso");
                            }else{
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível abrir o pedido");
                            }
                        }
                    } catch (Exception e) {
                        cl_Pedidos.setCdCliente("");
                        cl_Pedidos.setNomeRzSocial("");
                        cl_Pedidos.setDtEmissao("");
                    }
                }
            }
        }else{
            try {
                suCarregaItemPedido();
                suCalcularValorPedido();
            }catch (Exception e) {
                MensagemUtil.addMsg(ManutencaoPedidos.this, e.getMessage().toString());
            }
        }
    }

    @SuppressLint("RestrictedApi")
    protected void suInstanciarCampos() {


        cl_Pedidos = new CL_Pedidos();
        cl_Usuario = new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(getApplicationContext(), cl_Usuario);

        vc_Operacao = this.getIntent().getStringExtra("operacao");

        if (vc_Operacao.equals("A")) {
            cl_Pedidos.setNumPedido(this.getIntent().getStringExtra("codigo"));
        } else {
            cl_Pedidos.setNumPedido("0");
            cl_Pedidos.setFgSituacao("A");
        }

        lb_SelecionarClienteResultado = (TextView) findViewById(R.id.lb_SelecionarClienteResultado);
        lb_SelecionarProdutoResultado = (TextView) findViewById(R.id.lb_SelecionarProdutoResultado);
        lb_ocultarProdutos = (TextView) findViewById(R.id.lb_ocultarProdutos);
        lb_valorTotalProdutos = (TextView) findViewById(R.id.lb_valorTotalProdutos);
        lb_qtdeTotalProdutos = (TextView) findViewById(R.id.lb_qtdeTotalProdutos);
        lb_dtEmissao = (TextView) findViewById(R.id.lb_dtEmissao);
        lb_dtEmissaoResultado = (TextView) findViewById(R.id.lb_dtEmissaoResultado);
        lb_vlDesconto = (TextView) findViewById(R.id.lb_vlDesconto);
        lb_percDesconto = (TextView) findViewById(R.id.lb_percDesconto);
        lb_condPgto = (TextView) findViewById(R.id.lb_condPgto);
        lb_obsPedido = (TextView) findViewById(R.id.lb_obsPedido);
        lb_valorTotal = (TextView) findViewById(R.id.lb_valorTotal);
        lb_vlTotalResultado = (TextView) findViewById(R.id.lb_vlTotalResultado);
        lb_numPedidoServidor = (TextView)findViewById(R.id.lb_numPedidoServidor);
        lb_numPedidoServidorResultado = (TextView)findViewById(R.id.lb_numPedidoServidorResultado);

        fab_SelecionarEmitente = (FloatingActionButton) findViewById(R.id.fab_AddClienteVenda);
        fab_SelecionarEmitente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManutencaoPedidos.this, SelecaoCliente.class);
                startActivityForResult(intent, 1);
            }
        });

        fab_AdicionarProduto = (FloatingActionButton) findViewById(R.id.fab_AddProdutoVenda);
        fab_AdicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManutencaoPedidos.this, AdicionarProdutosCustomizada.class);
                intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                intent.putExtra("selecaoProdutos", "S");
                startActivityForResult(intent, 1);

            }
        });

        lv_ItensPedido = (ListView) findViewById(R.id.listViewItemPedidos);

        lb_ocultarProdutos.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                  if (lb_ocultarProdutos.getText().toString().equals("Mostrar Produtos")) {
                      lv_ItensPedido.setVisibility(View.VISIBLE);
                      lb_ocultarProdutos.setText("Ocultar Produtos");
                      calculeHeightListView();
                  } else {
                      lv_ItensPedido.setVisibility(View.GONE);
                      lb_ocultarProdutos.setText("Mostrar Produtos");
                      calculeHeigthListViewReverse();
                  }
              }
          }
        );

        tb_vlDesconto = (EditText) findViewById(R.id.tb_vlDesconto);

        Locale mLocale = new Locale("pt", "BR");
        tb_vlDesconto.addTextChangedListener(new MoneyTextWatcher(tb_vlDesconto, mLocale));

        tb_percDesconto = (EditText) findViewById(R.id.tb_percDesconto);
        tb_vlFrete = (EditText)findViewById(R.id.tb_vlFrete);
        tb_vlFrete.addTextChangedListener(new MoneyTextWatcher(tb_vlFrete, mLocale));

        tb_condPgto = (EditText) findViewById(R.id.tb_condPgto);
        tb_obsPedido = (EditText) findViewById(R.id.tb_obsPedido);

        if(vc_Operacao.equals("I")){
            suBloqueiaCampos(false);
            fab_SelecionarEmitente.setVisibility(View.VISIBLE);
        }

        sync_Pedidos = new SYNC_Pedidos(getApplicationContext());
    }

    protected boolean fuInstanciarPedido() {

        try {
            cl_Pedidos.setQtdeItens(lb_qtdeTotalProdutos.getText().toString().replace("Qtde: ", ""));
            cl_Pedidos.setCondPgto(tb_condPgto.getText().toString().toUpperCase());
            //String.format("%.5f", vf_Porcentagem)
            double vf_VlDesconto = 0.0;
            if(!tb_vlDesconto.getText().toString().trim().equals("")){
                vf_VlDesconto = Double.parseDouble(tb_vlDesconto.getText().toString().replace(".", "").replace(",", "."));
            }
            cl_Pedidos.setVlDesconto(String.format("%.2f", vf_VlDesconto).replace(",", "."));
            if(tb_percDesconto.getText().toString().trim().equals("")){
                cl_Pedidos.setPercDesconto("0");
            }else {
                cl_Pedidos.setPercDesconto(tb_percDesconto.getText().toString());
            }
            double vf_VlFrete = 0.0;
            if(!tb_vlFrete.getText().toString().trim().equals("")){
                vf_VlFrete = Double.parseDouble(tb_vlFrete.getText().toString().replace(".", "").replace(",", "."));
            }
            cl_Pedidos.setVlFrete(String.format("%.2f", vf_VlFrete).replace(",", "."));
            cl_Pedidos.setVlTotal(lb_vlTotalResultado.getText().toString().replace("R$", "").replace(",", "."));
            cl_Pedidos.setObsPedido(tb_obsPedido.getText().toString().toUpperCase());

            ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("RestrictedApi")
    public void suBloqueiaCampos(boolean habilitar){


        lb_SelecionarClienteResultado.setEnabled(habilitar);
        lb_SelecionarProdutoResultado.setEnabled(habilitar);
        //lb_ocultarProdutos.setEnabled(habilitar);
        lb_valorTotalProdutos.setEnabled(habilitar);
        lb_qtdeTotalProdutos.setEnabled(habilitar);
        lb_dtEmissao.setEnabled(habilitar);
        lb_dtEmissaoResultado.setEnabled(habilitar);
        lb_vlDesconto.setEnabled(habilitar);
        lb_percDesconto.setEnabled(habilitar);
        lb_vlDesconto.setEnabled(habilitar);
        lb_percDesconto.setEnabled(habilitar);
        lb_condPgto.setEnabled(habilitar);
        lb_obsPedido.setEnabled(habilitar);
        lb_valorTotal.setEnabled(habilitar);
        lb_vlTotalResultado.setEnabled(habilitar);

        if(habilitar) {
            fab_SelecionarEmitente.setVisibility(View.VISIBLE);
            fab_AdicionarProduto.setVisibility(View.VISIBLE);
        }else{
            fab_SelecionarEmitente.setVisibility(View.INVISIBLE);
            fab_AdicionarProduto.setVisibility(View.INVISIBLE);
        }

        lv_ItensPedido.setEnabled(habilitar);

        tb_vlDesconto.setEnabled(habilitar);
        tb_percDesconto.setEnabled(habilitar);
        tb_vlFrete.setEnabled(habilitar);
        tb_condPgto.setEnabled(habilitar);
        tb_obsPedido.setEnabled(habilitar);

    }

    public boolean fuConsistePedido() {
        if (cl_Pedidos.getCdCliente().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Favor selecionar um cliente para abertura do pedido!");
            return false;
        }

        if (!cl_Pedidos.getPercDesconto().trim().equals("") && Double.parseDouble(cl_Pedidos.getPercDesconto()) > 99) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não é permitido um desconto superior a 99%!");
            return false;
        }

        if (!cl_Pedidos.getQtdeItens().trim().equals("")) {
            if (!cl_Pedidos.getVlDesconto().trim().equals("") && Double.parseDouble(cl_Pedidos.getVlDesconto()) > 0) {


                CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

                CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);


                try {
                    double vf_VlMaxDescPermitido = 0;
                    double vf_VlDescontoItens = 0;

                    if (ctl_ItemPedido.fuCarregaTodosItensPedido()) {

                        Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;
                        while (!rs_ItemPedido.isAfterLast()) {
                            vf_VlMaxDescPermitido += Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                            vf_VlDescontoItens += Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));

                            rs_ItemPedido.moveToNext();
                        }
                    }


                    double VA_vlDesconto = Double.parseDouble(tb_percDesconto.getText().toString());
                    double porcentagem = VA_vlDesconto / 100;
                    double resultado = Double.parseDouble(cl_Pedidos.getVlTotal()) * porcentagem;
                    if ((resultado + vf_VlDescontoItens) > vf_VlMaxDescPermitido) {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Desconto informado é maior que o permitido!");
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            if (!cl_Pedidos.getVlDesconto().equals("") && Double.parseDouble(cl_Pedidos.getVlDesconto()) > 0) {
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi adicionado nenhum produto no pedido. Não é permitido informar nenhum desconto para este pedido!");
                return false;
            }
        }

        return true;
    }

    public void suSalvarPedido(String fgEnviarOnline) {
        if (cl_Pedidos.getFgSituacao().equals("E")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido já foi enviado para o sistema online.");
        } else if (cl_Pedidos.getFgSituacao().equals("C")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser salvo, pois o pedido foi cancelado!");
        } else {
            if (fuInstanciarPedido()) {
                if (fuConsistePedido()) {
                    try {

                        if (vc_Operacao.equals("I")) {
                            cl_Pedidos.setNumPedido(ctl_Pedidos.fuCarregaNumPedido());
                        }
                        if (ctl_Pedidos.fuAlterarPedido()) {
                            if(fgEnviarOnline.equals("E")){

                            }else{
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido salvo com sucesso!");
                                Intent intent = new Intent();
                                setResult(1, intent);
                                finish();
                            }
                        } else {
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi ´possível salvar o pedido");
                        }

                    } catch (Exception e) {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel salvar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
                    }
                }
            }
        }
    }

    public void suEnviarOnline() {

        try {
            suSalvarPedido("E");

            if (fuConsisteEnviarOnline()) {
                if (verificaConexao()) {
                    LoadingAsyncEnviarPedido async_EnviarPedido = new LoadingAsyncEnviarPedido();
                    async_EnviarPedido.execute();

                } else {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "É necessário ter conexão com a internet para o envio do pedido!");
                }
            }

        } catch (Exception e) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel enviar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
        }
    }

    protected void suCancelarPedido(){
        if(cl_Pedidos.getCdCliente().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não foi aberto. Não será possivel realizar o cancelamento sem que o pedido seja aberto!");
        }else {
            if (cl_Pedidos.getFgSituacao().equals("E")) {
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não pode ser cancelado, pois já foi enviado para o sistema online.");
            } else {
                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Cancelar Pedido");
                //define a mensagem
                builder.setMessage("Deseja mesmo cancelar o pedido " + cl_Pedidos.getNumPedido() + "?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        cl_Pedidos.setFgSituacao("C");
                        ctl_Pedidos =  new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                        try {
                            if(ctl_Pedidos.fuAlterarSituacaoPedido()){
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido cancelado com sucesso!");
                                Intent intent = new Intent();
                                setResult(1, intent);
                                finish();
                            }else{
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
                            }

                        } catch (Exception e) {
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel cancelar o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
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
    }

    protected void suDuplicarPedido(){
        AlertDialog al_DuplicarPedido;
        AlertDialog.Builder bu_DuplicarPedido;

        bu_DuplicarPedido = new AlertDialog.Builder(ManutencaoPedidos.this);

        //define o titulo
        bu_DuplicarPedido.setTitle("Duplicar pedido " + cl_Pedidos.getNumPedido());
        //define a mensagem
        bu_DuplicarPedido.setMessage("Deseja mesmo duplicar o pedido " + cl_Pedidos.getNumPedido() + "?");

        //define um botão como positivo
        bu_DuplicarPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                if(ctl_Pedidos.fuCarregarPedido()){
                    try {
                        String vf_NumPedidoOriginal = cl_Pedidos.getNumPedido();
                        if(ctl_Pedidos.fuDuplicarPedido()){

                            CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                            cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

                            CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

                            if(ctl_ItemPedido.fuDuplicarItensPedidoDuplicado(vf_NumPedidoOriginal)) {
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido duplicado com sucesso!");
                                finish();
                            }else{
                                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível duplicar os itens do pedido!");
                            }
                        }else{
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível duplicar o pedido!");
                        }
                    }catch (Exception e){
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel duplicar o pedido devido à seguinte situação: " + e.getMessage().toString() + ".");
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
    }

    protected void suExcluirPedido(){
        if(cl_Pedidos.getCdCliente().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido não foi aberto. Não será possivel realizar a exclusão sem que o pedido seja aberto!");
        }else {
            builder = new AlertDialog.Builder(this);

            //define o titulo
            builder.setTitle("Excluir Pedido");
            //define a mensagem
            builder.setMessage("Deseja mesmo excluir o pedido " + cl_Pedidos.getNumPedido() + "?");

            //define um botão como positivo
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                    ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

                    try {
                        if(ctl_Pedidos.fuDeletarPedido()){
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido excluido com sucesso!");
                            finish();
                        }else{
                            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
                        }

                    } catch (Exception e) {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel excluir o pedido devido à seguinte situação: " + ctl_Pedidos.vc_Mensagem + ".");
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

    public boolean fuConsisteEnviarOnline(){

        if(cl_Pedidos.getCdCliente().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Favor selecionar um cliente no pedido!");
            return false;
        }
        if(cl_Pedidos.getQtdeItens().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Deve ser informado ao menos um produto no pedido.");
            return  false;
        }
        if(cl_Pedidos.getCondPgto().trim().equals("")){
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Deve ser informada a forma de pagamento do pedido.");
            return false;
        }
        return true;
    }

    protected void suCalcularValorPedido(){

        double vf_vlDesconto = 0;
        if(tb_percDesconto.getText().toString().trim().equals("") || tb_percDesconto.getText().toString().equals("0")){
            if(tb_vlFrete.getText().toString().trim().equals("")){
                cl_Pedidos.setVlFrete("0.00");
            }else{
                cl_Pedidos.setVlFrete(String.valueOf(Double.parseDouble(tb_vlFrete.getText().toString().replace(".", "").replace(",", "."))));
            }
            double vf_VlTotalFrete = Double.parseDouble(cl_Pedidos.getVlTotalItens()) + Double.parseDouble(cl_Pedidos.getVlFrete());
            String vf_Valor = String.format("%.2f", vf_VlTotalFrete);
            cl_Pedidos.setVlTotal(vf_Valor.replace(",", "."));
            lb_vlTotalResultado.setText("R$" + vf_Valor);
        }else{
            vf_vlDesconto = Double.parseDouble(tb_vlDesconto.getText().toString().replace(".", "").replace(",", "."));
            if(tb_vlFrete.getText().toString().trim().equals("")){
                cl_Pedidos.setVlFrete("0.00");
            }else{
                cl_Pedidos.setVlFrete(String.valueOf(Double.parseDouble(tb_vlFrete.getText().toString().replace(".", "").replace(",", "."))));
            }
            double resultadoTotal = (Double.parseDouble(cl_Pedidos.getVlTotalItens()) - vf_vlDesconto) + Double.parseDouble(cl_Pedidos.getVlFrete());
            resultadoTotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
            String valor = String.format("%.2f", resultadoTotal);
            cl_Pedidos.setVlTotal(valor.replace(",", "."));
            lb_vlTotalResultado.setText("R$" + valor);
        }


    }

    protected void suCarregarPedido(){

        ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

        if(ctl_Pedidos.fuCarregarPedido()){
            try {
                lb_SelecionarClienteResultado.setText(cl_Pedidos.getNomeRzSocial().toUpperCase());
            }catch (Exception e){
                lb_SelecionarClienteResultado.setText("");
            }

            try {
                tb_condPgto.setText(cl_Pedidos.getCondPgto().toUpperCase());
            }catch (Exception e){
                tb_condPgto.setText("");
            }

            try {
                tb_percDesconto.setText(cl_Pedidos.getPercDesconto());
            }catch (Exception e){
                tb_percDesconto.setText("");
            }

            try {
                tb_vlDesconto.setText(cl_Pedidos.getVlDesconto());
            }catch (Exception e){
                tb_vlDesconto.setText("");
            }

            try {
                tb_vlFrete.setText(cl_Pedidos.getVlFrete());
            }catch (Exception e){
                tb_vlFrete.setText("");
            }

            try {
                tb_obsPedido.setText(cl_Pedidos.getObsPedido().toUpperCase());
            }catch (Exception e){
                tb_obsPedido.setText("");
            }

            try {
                lb_dtEmissaoResultado.setText(cl_Pedidos.getDtEmissao());
            }catch (Exception e){
                lb_dtEmissaoResultado.setText("");
            }

            try {
                lb_vlTotalResultado.setText("R$" + cl_Pedidos.getVlTotal().replace(".", ","));
            }catch (Exception e){
                lb_vlTotalResultado.setText("R$0,00");
            }

            if(cl_Pedidos.getFgSituacao().equals("A")){
                suBloqueiaCampos(true);
            }else if(cl_Pedidos.getFgSituacao().equals("C")){
                suBloqueiaCampos(false);
            }else if(cl_Pedidos.getFgSituacao().equals("E")){

                try {
                    lb_numPedidoServidor.setVisibility(View.VISIBLE);
                    lb_numPedidoServidorResultado.setVisibility(View.VISIBLE);
                    lb_numPedidoServidorResultado.setText(cl_Pedidos.getNumPedidoServidor());
                }catch (Exception e){
                    lb_numPedidoServidor.setVisibility(View.GONE);
                    lb_numPedidoServidorResultado.setVisibility(View.GONE);
                    lb_numPedidoServidorResultado.setText("");
                }

                suBloqueiaCampos(false);
            }
        }
    }

    protected void suCarregaItemPedido(){

        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
        cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

        CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);
        View vw_ListaProdutos = findViewById(R.id.la_ListaProdutos);

        if(ctl_ItemPedido.fuCarregaTodosItensPedido()){

            //la_ListaProdutos
            lv_ItensPedido.setAdapter(null);

            vw_ListaProdutos.setVisibility(View.VISIBLE);

            final Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;

            List<String> descricaoPedidos = new ArrayList<>();
            List<String> itensRestantesPedidos = new ArrayList<>();
            List<String> valorProdutos = new ArrayList<>();
            List<String> valorAtacado = new ArrayList<>();

            double vf_VlTotalItens = 0.0;
            int vf_QtdeItens = 0;

            while (!rs_ItemPedido.isAfterLast()){

                descricaoPedidos.add(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                itensRestantesPedidos.add(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                valorProdutos.add(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                vf_QtdeItens += Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                vf_VlTotalItens += Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));

                rs_ItemPedido.moveToNext();
            }

            lb_valorTotalProdutos.setText("Total: R$" +  String.format("%.2f", vf_VlTotalItens));
            lb_qtdeTotalProdutos.setText("Qtde: " + String.valueOf(vf_QtdeItens));

            cl_Pedidos.setQtdeItens(String.valueOf(vf_QtdeItens));
            cl_Pedidos.setVlTotalItens(String.valueOf(vf_VlTotalItens));

            ListaProdutosPedido adapter = new ListaProdutosPedido(this, descricaoPedidos, itensRestantesPedidos, valorProdutos);
            lv_ItensPedido.setAdapter(adapter);

            lv_ItensPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String codigo;
                    try {
                        ListAdapter adapter2 = lv_ItensPedido.getAdapter();
                        int lenght = adapter2.getCount();
                        if(position == 0){
                            rs_ItemPedido.moveToPosition(position);
                        }else if(position < lenght - 1) {
                            rs_ItemPedido.moveToPosition(position);
                        }else{
                            rs_ItemPedido.moveToPosition(position + 1);
                        }

                        codigo = rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent(ManutencaoPedidos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                        intent.putExtra("alteracao", "S");
                        //startActivity(intent);
                        startActivityForResult(intent, 2);
                    } catch (Exception e) {
                        rs_ItemPedido.moveToPosition(position);
                        codigo = rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent(ManutencaoPedidos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                        intent.putExtra("alteracao", "S");
                        //startActivity(intent);
                        startActivityForResult(intent, 2);
                    }
                }
            });

            calculeHeightListView();

        }else{
            vw_ListaProdutos.setVisibility(View.GONE);

            cl_Pedidos.setQtdeItens("0");
            cl_Pedidos.setVlTotalItens("0.0");

            lb_valorTotalProdutos.setText("Total: R$0,00");
            lb_qtdeTotalProdutos.setText("Qtde: 0");

        }
    }

    private void calculeHeightListView() {

        int totalHeight = 0;
        ListAdapter adapter2 = lv_ItensPedido.getAdapter();
        int lenght = adapter2.getCount();
        for (int i = 0; i < lenght; i++) {
            View listItem = adapter2.getView(i, null, lv_ItensPedido);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv_ItensPedido.getLayoutParams();

        params.height = totalHeight
                + (lv_ItensPedido.getDividerHeight() * (adapter2.getCount() - 1));
        lv_ItensPedido.setLayoutParams(params);
        lv_ItensPedido.requestLayout();
    }

    private void calculeHeigthListViewReverse(){
        int totalHeight = 0;
        ListAdapter adapter2 = lv_ItensPedido.getAdapter();
        int lenght = adapter2.getCount();
        for (int i = 0; i < lenght; i++) {
            View listItem = adapter2.getView(i, null, lv_ItensPedido);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv_ItensPedido.getLayoutParams();

        params.height = lv_ItensPedido.getDividerHeight() * (adapter2.getCount() - 1);
        lv_ItensPedido.setLayoutParams(params);
        lv_ItensPedido.requestLayout();
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

    private class LoadingAsyncEnviarPedido extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd_Pedidos;
        String validou = "N";

        @Override
        protected void onPreExecute() {
            pd_Pedidos = ProgressDialog.show(ManutencaoPedidos.this, "Enviando o pedido","Enviando o pedido online para o servidor...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(fuEnviarPedido()) {
                validou = "S";
            }else{
                validou = "N";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {
                pd_Pedidos.dismiss();

                if (validou.equals("S")) {
                    cl_Pedidos.setFgSituacao("E");
                    if (ctl_Pedidos.fuAlterarSituacaoPedido()) {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Pedido enviado com sucesso!");
                        finish();
                    } else {
                        MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.");
                    }
                } else {
                    MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possivel realizar a sincronização do pedido. Favor verificar a conexão com a internet.");
                }
            }catch (Exception e){
                String teste = e.getMessage();
                String teste2 = teste;
            }
        }
    }

    public boolean fuEnviarPedido() {

        CL_Clientes cl_Clientes = new CL_Clientes();
        cl_Clientes.setCdCliente(cl_Pedidos.getCdCliente());

        CTL_Clientes ctl_Clientes = new CTL_Clientes(getApplicationContext(), cl_Clientes);

        if(ctl_Clientes.fuSelecionarCliente()){
            if(cl_Clientes.getFgSincronizado().equals("N")){
                SYNC_Clientes sync_Clientes = new SYNC_Clientes(getApplicationContext());
                if(sync_Clientes.FU_SincronizarClientesPedidoAPI(cl_Clientes)){
                    cl_Pedidos.setCdCliente(cl_Clientes.getCdCliente());
                }
            }
        }

        if(sync_Pedidos.FU_EnviarPedidoAPI(cl_Pedidos)){
            return true;
        }else{
            return false;
        }
    }

    private void suCriarPDF() {

        boolean vf_Gerou = true;
        Document document = new Document();
        String filename = "pedido_" + cl_Pedidos.getNumPedido() + ".pdf";

        try {
            document = new Document(PageSize.A4);

            // Use MediaStore to save the PDF
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/PedidosPDF");

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new IOException("Failed to get output stream.");
            }

            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Adicione seu conteúdo ao documento
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            Paragraph pa_Titulo = new Paragraph();
            pa_Titulo.setAlignment(Element.ALIGN_CENTER);
            pa_Titulo.setFont(catFont);
            pa_Titulo.add("PEDIDO Nº" + cl_Pedidos.getNumPedido());
            preface.add(pa_Titulo);

            addEmptyLine(preface, 2);
            preface.setFont(normalFont);
            preface.add("Data de emissão: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getDtEmissao());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Cliente: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getNomeRzSocial());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Quantidade de produtos: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getQtdeItens());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor dos produtos: ");
            preface.setFont(subFont);
            preface.add("R$" + String.format("%.2f", Double.parseDouble(cl_Pedidos.getVlTotalItens())));
            addEmptyLine(preface, 1);

            CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
            cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

            CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

            if (ctl_ItemPedido.fuCarregaTodosItensPedido()) {
                addEmptyLine(preface, 1);
                preface.setFont(subFont);
                preface.add("PRODUTOS DO PEDIDO:");
                addEmptyLine(preface, 2);

                createTable(preface, ctl_ItemPedido.rs_ItemPedido);
                addEmptyLine(preface, 2);
            }

            preface.setFont(normalFont);
            preface.add("Valor do desconto: ");
            preface.setFont(subFont);
            preface.add("R$" + cl_Pedidos.getVlDesconto());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor do frete: ");
            preface.setFont(subFont);
            preface.add("R$" + String.format("%.2f", Double.parseDouble(cl_Pedidos.getVlFrete())));
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Condição do pagamento: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getCondPgto());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Observação do pedido: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getObsPedido());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor total do pedido: ");
            preface.setFont(subFont);
            preface.add("R$" + cl_Pedidos.getVlTotal().replace(".", ","));

            document.add(preface);

            document.close();
            outputStream.close();
            vf_Gerou = true;
            MensagemUtil.addMsg(ManutencaoPedidos.this, "PDF do pedido gerado com sucesso na pasta " + Environment.DIRECTORY_DOCUMENTS + "/PedidosPDF");

            // Share the PDF
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("application/pdf");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share PDF using"));

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            vf_Gerou = false;
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível gerar o PDF");
        }
    }

    //Função antiga do su_CriaPDF

    /*private void suCriarPDF() {

        boolean vf_Gerou = true;
        Document document = new Document();
        String filename = "";
        String pathFile = "";

        try {

            filename = "pedido_" + cl_Pedidos.getNumPedido() + ".pdf";

            document = new Document(PageSize.A4);


            //Comando para buscar no armazenamento interno:
            //String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFs/" ;


            pathFile = Environment.getExternalStorageDirectory() + "/PedidosPDF/";

            String backupDirectoryPath = getApplicationContext().getExternalFilesDir(null) +
                    "/" + "PedidosPDF/";

            //pathFile = getFilesDir()  .getAbsolutePath();
            pathFile = getFilesDir().getCanonicalPath();
            pathFile = backupDirectoryPath;
            //Comando original
            //pathFile = Environment.getExternalStorageDirectory() + "/PedidosPDF/";

            File dir = new File(pathFile, filename);
            if (!dir.exists()) {
                dir.getParentFile().mkdirs();
            }

                Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", dir);

            FileOutputStream fOut = new FileOutputStream(dir);
            fOut.flush();


            PdfWriter.getInstance(document, fOut);
            document.open();

            Paragraph preface = new Paragraph();
            // We add one empty line
            addEmptyLine(preface, 1);
            // Lets write a big header
            Paragraph pa_Titulo = new Paragraph();
            pa_Titulo.setAlignment(Element.ALIGN_CENTER);
            pa_Titulo.setFont(catFont);
            pa_Titulo.add("PEDIDO Nº" + cl_Pedidos.getNumPedido());
            preface.add(pa_Titulo);
            //preface.add(new Paragraph("PEDIDO Nº" + cl_Pedidos.getNumPedido(), catFont));

            addEmptyLine(preface, 2);
            // Will create: Report generated by: _name, _date
            preface.setFont(normalFont);
            preface.add("Data de emissão: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getDtEmissao());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Cliente: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getNomeRzSocial());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Quantidade de produtos: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getQtdeItens());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor dos produtos: ");
            preface.setFont(subFont);
            preface.add("R$" + String.format("%.2f", Double.parseDouble(cl_Pedidos.getVlTotalItens())));
            addEmptyLine(preface, 1);

            CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
            cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

            CTL_ItemPedido ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

            if(ctl_ItemPedido.fuCarregaTodosItensPedido()) {

                addEmptyLine(preface, 1);
                preface.setFont(subFont);
                preface.add("PRODUTOS DO PEDIDO:");
                addEmptyLine(preface, 2);

                createTable(preface, ctl_ItemPedido.rs_ItemPedido);

                addEmptyLine(preface, 2);
            }


            preface.setFont(normalFont);
            preface.add("Valor do desconto: ");
            preface.setFont(subFont);
            preface.add("R$" + cl_Pedidos.getVlDesconto());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor do frete: ");
            preface.setFont(subFont);
            //double resultadoTotal = (Double.parseDouble(cl_Pedidos.getVlTotalItens()) - vf_vlDesconto) + Double.parseDouble(cl_Pedidos.getVlFrete());
            //            resultadoTotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
            //            String valor = String.format("%.2f", resultadoTotal);
            preface.add("R$" + String.format("%.2f", Double.parseDouble(cl_Pedidos.getVlFrete())));
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Condição do pagamento: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getCondPgto());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Observação do pedido: ");
            preface.setFont(subFont);
            preface.add(cl_Pedidos.getObsPedido());
            addEmptyLine(preface, 1);
            preface.setFont(normalFont);
            preface.add("Valor total do pedido: ");
            preface.setFont(subFont);
            preface.add("R$" + cl_Pedidos.getVlTotal().replace(".", ","));

            //createTable(preface);

            document.add(preface);




        } catch (DocumentException e) {
            e.printStackTrace();
            vf_Gerou = false;
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível gerar o PDF");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            vf_Gerou = false;
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível gerar o PDF");
        } catch (IOException e) {
            e.printStackTrace();
            vf_Gerou = false;
            MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível gerar o PDF");
        } finally {
            document.close();
            if(vf_Gerou){
                MensagemUtil.addMsg(ManutencaoPedidos.this, "PDF do pedido gerado com sucesso na pasta " + pathFile);
            }
        }

        if(vf_Gerou){

            File dir = new File(pathFile, filename);
            Uri uri_Arquivo = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", dir);
            //Uri uri_Arquivo = Uri.parse(pathFile + filename);

            try {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                //REMOVER ESSE ESPAÇO ENTRE A BARRA E OS *
                sharingIntent.setType("* / *");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri_Arquivo);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));

            }catch (Exception e){
                MensagemUtil.addMsg(ManutencaoPedidos.this, "Não foi possível abrir o PDF do pedido");
            }

        }

    }*/

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static void createTable(Paragraph paragraph, Cursor rs_ItemPedido)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        while (!rs_ItemPedido.isAfterLast()) {

            Phrase ph_Descricao = new Phrase();
            ph_Descricao.setFont(negritoMenor);
            //ph_Descricao.setFont(negritoDescricao);
            ph_Descricao.add(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));


            PdfPCell c1 = new PdfPCell(ph_Descricao);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            ///c1.setPaddingTop(5);
            c1.setPadding(10);
            table.addCell(c1);

            Phrase ph_QtdeProdutos = new Phrase();
            ph_QtdeProdutos.setFont(negritoMenor);
            //ph_QtdeProdutos.setFont(negritoDescricao);
            ph_QtdeProdutos.add("Quantidade: " + rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));

            PdfPCell c2 = new PdfPCell(ph_QtdeProdutos);
            c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            ///c1.setPaddingTop(5);
            c2.setPadding(10);
            table.addCell(c2);

            Phrase ph_VlTotal = new Phrase();
            ph_VlTotal.setFont(negritoMenor);
            //ph_VlTotal.setFont(negritoDescricao);
            ph_VlTotal.add("Valor total: " + "R$" + String.format("%.2f", Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."))));

            PdfPCell c3 = new PdfPCell(ph_VlTotal);
            c3.setHorizontalAlignment(Element.ALIGN_LEFT);
            ///c1.setPaddingTop(5);
            c3.setPadding(10);
            table.addCell(c3);

            //table.addCell("Quantidade: " + rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
            //table.addCell("Valor total: " + "R$" + String.format("%.2f", Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."))));

            rs_ItemPedido.moveToNext();
        }


        /*PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 2"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Table Header 3"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell("1.0");
        table.addCell("1.1");
        table.addCell("1.2");
        table.addCell("2.1");
        table.addCell("2.2");
        table.addCell("2.3");*/

        paragraph.add(table);

    }


    //Função para verificar se a permissão de armazenamento no celular já foi setada
    protected void fuVerificaPermissaoStorage(){

        if (ContextCompat.checkSelfPermission(ManutencaoPedidos.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ManutencaoPedidos.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                //suCriarPDF();
                builder = new AlertDialog.Builder(this);

                //define o titulo
                builder.setTitle("Permissão");
                //define a mensagem
                builder.setMessage("Para que o Comércio Express Mobile possa gerar o PDF do pedido, a permissão para salvar o documento em seus arquivos precisa ser concedida. Por favor conceda a permissão para que seja possivel realizar a geração do PDF do pedido clicando em 'PERMITIR' na mensagem a seguir.");

                builder.setNeutralButton("ESTÁ BEM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //fuVerificaPermissaoStorage();
                        ActivityCompat.requestPermissions(ManutencaoPedidos.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }
                });

                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();



                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            suCriarPDF();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    suCriarPDF();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    //Funcoes.addMsg(this, "Poxa você não deu permissao, vai ficar sem algumas funcionalidades");
                }
                return;
            }
        }
    }


}

