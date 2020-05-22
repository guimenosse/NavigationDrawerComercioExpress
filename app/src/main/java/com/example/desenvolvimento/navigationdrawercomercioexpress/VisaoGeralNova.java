package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import classes.CL_VisaoGeral;
import controllers.CTL_VisaoGeral;

public class VisaoGeralNova extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Funcoes funcoes = new Funcoes();

    CL_VisaoGeral cl_VisaoGeral;
    CTL_VisaoGeral ctl_VisaoGeral;

    private AlertDialog ad_VisaoGeral;
    AlertDialog.Builder bu_VisaoGeral;

    //Instancia da imagem para seleção da data da movimentação
    ImageView img_DtEmissaoPicker;

    //Função de calendário para seleção da data de emissão da venda
    final Calendar vc_MyCalendar = Calendar.getInstance();

    //Instancia das labels que apresentarão os resultados da movimentação diária
    TextView lb_DtMovimentacaoDiaria, lb_VlVendaBrutaResultado, lb_VlDescontosVendaResultado, lb_VlVendaLiquidaResultado, lb_VlLucroMedioResultado;

    //Instancia dos botões da seção de tipos de venda
    Button btn_Orcamentos, btn_Pedidos, btn_Vendas;

    //Instancia das labels que apresentarão os resultados do tipo de venda
    TextView lb_QuantidadeTipoVendaAbertos, lb_QuantidadeTipoVendaCancelados, lb_VlTotalTipoVenda;
    TextView lb_QuantidadeTipoVendaAbertosResultado, lb_QuantidadeTipoVendaCanceladosResultado, lb_VlDescontoTipoVendaResultado, lb_VlTotalTipoVendaResultado;

    //Instancia da label do estoque.
    TextView lb_ProdutosAtencao;

    int vc_CountProdutosAtencao = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_geral_nova);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        com.github.clans.fab.FloatingActionButton fab_Emitente = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_ClienteVisao);
        fab_Emitente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("tipo", "C");
                startActivity(intent);
                finish();
            }
        });

        com.github.clans.fab.FloatingActionButton fab_Produto = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_ProdutoVisao);
        fab_Produto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Produtos.class);
                startActivity(intent);
                finish();
            }
        });

        com.github.clans.fab.FloatingActionButton fab_Venda = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_PedidoVisao);
        fab_Venda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Pedidos.class);
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fu_InstanciarCampos();

        fu_AtualizarData("S");
        fu_CarregarProdutosZerados();
        fu_CalcularResultadosTipoVenda();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.visao_geral_nova, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Intent secondActivity;
            secondActivity = new Intent(VisaoGeralNova.this, HomeActivity.class);
            startActivity(secondActivity);
            // Handle the camera action
        } else if (id == R.id.nav_pedidos) {
            Intent secondActivity;
            secondActivity = new Intent(VisaoGeralNova.this, Pedidos.class);
            startActivity(secondActivity);


        } else if (id == R.id.nav_produtos) {
            Intent secondActivity;
            secondActivity = new Intent(VisaoGeralNova.this, Produtos.class);
            secondActivity.putExtra("selecaoProdutos", "N");
            startActivity(secondActivity);

        } else if (id == R.id.nav_opcoes) {
            Intent secondActivity;
            secondActivity = new Intent(VisaoGeralNova.this, Opcoes.class);
            startActivity(secondActivity);

        } else if (id == R.id.nav_visaogeral) {
            Intent secondActivity;
            secondActivity = new Intent(VisaoGeralNova.this, VisaoGeralNova.class);
            startActivity(secondActivity);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fu_InstanciarCampos(){

        //Campos da seção de movimentação diária--------------------------------------------------
        img_DtEmissaoPicker = (ImageView)findViewById(R.id.img_dtEmissaoPicker);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                vc_MyCalendar.set(Calendar.YEAR, year);
                vc_MyCalendar.set(Calendar.MONTH, monthOfYear);
                vc_MyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fu_AtualizarData("N");
            }

        };

        img_DtEmissaoPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(VisaoGeralNova.this, date, vc_MyCalendar
                        .get(Calendar.YEAR), vc_MyCalendar.get(Calendar.MONTH),
                        vc_MyCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        lb_DtMovimentacaoDiaria = (TextView)findViewById(R.id.lb_dataMovimentacaoDiaria);
        lb_VlVendaBrutaResultado = (TextView)findViewById(R.id.lb_vlVendaBrutaResultado);
        lb_VlDescontosVendaResultado = (TextView)findViewById(R.id.lb_vlDescontosVendaResultado);
        lb_VlVendaLiquidaResultado = (TextView)findViewById(R.id.lb_vlVendaLiquidaResultado);
        lb_VlLucroMedioResultado = (TextView)findViewById(R.id.lb_vlLucroMedioResultado);
        //----------------------------------------------------------------------------------------

        lb_QuantidadeTipoVendaAbertos = (TextView)findViewById(R.id.lb_quantidadeTipoVendaAbertos);
        lb_QuantidadeTipoVendaCancelados = (TextView)findViewById(R.id.lb_quantidadeTipoVendaCancelados);
        lb_VlTotalTipoVenda = (TextView)findViewById(R.id.lb_vlTotalTipoVenda);

        lb_QuantidadeTipoVendaAbertosResultado = (TextView)findViewById(R.id.lb_quantidadeTipoVendaAbertosResultado);
        lb_QuantidadeTipoVendaCanceladosResultado = (TextView)findViewById(R.id.lb_quantidadeTipoVendaCanceladosResultado);
        lb_VlDescontoTipoVendaResultado = (TextView)findViewById(R.id.lb_vlDescontoTipoVendaResultado);
        lb_VlTotalTipoVendaResultado = (TextView)findViewById(R.id.lb_vlTotalTipoVendaResultado);
        //----------------------------------------------------------------------------------------

        //Campos da seção de produtos-------------------------------------------------------------
        lb_ProdutosAtencao = (TextView)findViewById(R.id.lb_produtosAtencao);
        //----------------------------------------------------------------------------------------

        //----------------------------------------------------------------------------------------

        //Instancia da classe controle de VisaoGeral
        cl_VisaoGeral = new CL_VisaoGeral();
        ctl_VisaoGeral = new CTL_VisaoGeral(getApplicationContext(), cl_VisaoGeral);

    }

    //Função para atualização da data no label de dtEmissao
    private void fu_AtualizarData(String fgDataAtual) {

        String data = "";
        String myFormat = "dd/MM/yyyy"; //In which you need put
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        if (fgDataAtual.equals("S")) {
            data = funcoes.getDateTime().substring(0, 10);
        } else {
            data = sdf.format(vc_MyCalendar.getTime());
        }

        String dataInformadaFormatada =  data.replace("/", "");
        String diaInformada = dataInformadaFormatada.substring(0, 2);
        int diaInformadaInt = Integer.parseInt(diaInformada);
        diaInformadaInt += 10;
        String mesInformada = dataInformadaFormatada.substring(2, 4);
        int mesInformadaInt = Integer.parseInt(mesInformada);
        mesInformadaInt += 10;
        String anoInformada = dataInformadaFormatada.substring(4, 8);
        int anoInformadaInt = Integer.parseInt(anoInformada);

        String dataAtual = funcoes.getDateTime().substring(0, 10).replace("/", "").replace("-", "");

        String diaAtual = dataAtual.substring(0, 2);
        int diaAtualInt = Integer.parseInt(diaAtual);
        diaAtualInt += 10;
        String mesAtual = dataAtual.substring(2, 4);
        int mesAtualInt = Integer.parseInt(mesAtual);
        mesAtualInt += 10;
        String anoAtual = dataAtual.substring(4, 8);
        int anoAtualInt = Integer.parseInt(anoAtual);

        //int dataInformada = Integer.parseInt(data.replace("/", ""));
        //int dataAtual = Integer.parseInt(funcoes.getDateTime().substring(0, 10).replace("/", "").replace("-", ""));

        boolean dataInvalida = false;
        if(anoInformadaInt > anoAtualInt){
            dataInvalida = true;
        }else{
            if(anoInformadaInt == anoAtualInt){
                if(mesInformadaInt > mesAtualInt){
                    dataInvalida = true;
                }else{
                    if(mesInformadaInt == mesAtualInt){
                        if(diaInformadaInt > diaAtualInt){
                            dataInvalida = true;
                        }
                    }
                }
            }
        }

        if(dataInvalida){

        /*int dataInformada = Integer.parseInt(data.replace("/", ""));
        int dataAtual = Integer.parseInt(funcoes.getDateTime().substring(0, 10).replace("/", "").replace("-", ""));

        if (dataInformada > dataAtual) {*/
            MensagemUtil.addMsg(VisaoGeralNova.this, "Não é possivel informar uma data maior que a data atual");

            //lb_dtEmissaoResultado.setText(dtEmissao);
        } else {
            String dataMovimentacao = data;
            String dataFormatada = "";

            int dia = vc_MyCalendar.get(vc_MyCalendar.DAY_OF_WEEK);

            String nomeDia = funcoes.gerarNomeDia(dia);
            dataFormatada = nomeDia + ", ";

            dataFormatada += dataMovimentacao.substring(0, 2) + " de ";

            int mes = Integer.parseInt(dataMovimentacao.substring(3, 5));
            String nomeMes = funcoes.gerarNomeMes(mes);
            dataFormatada += nomeMes + " de ";

            dataFormatada += dataMovimentacao.substring(6, 10);

            lb_DtMovimentacaoDiaria.setText(dataFormatada);
            fu_CalcularMovimentacaoDiaria(fgDataAtual, dataMovimentacao);
        }
    }

    private void fu_CalcularMovimentacaoDiaria(String fgDataAtual, String data){

        if(ctl_VisaoGeral.fu_CarregarMovimentacaoDiaria(data)){
            lb_VlVendaBrutaResultado.setText("R$ " + String.format("%.2f", cl_VisaoGeral.getVlVendaBruto()));
            lb_VlDescontosVendaResultado.setText("R$ " + String.format("%.2f", cl_VisaoGeral.getVlVendaDesconto()));
            lb_VlVendaLiquidaResultado.setText("R$ " + String.format("%.2f", cl_VisaoGeral.getVlVendaLiquido()));
            //lb_vlVendaBrutaResultado.setText("R$ " + String.valueOf(Double.parseDouble(ctl_VisaoGeral.vlVendaBruto)));
        }else{
            if(fgDataAtual.equals("N")) {
                MensagemUtil.addMsg(VisaoGeralNova.this, ctl_VisaoGeral.vc_Mensagem);
            }
            lb_VlVendaBrutaResultado.setText("R$ " + String.format("%.2f", 0.0));
            lb_VlDescontosVendaResultado.setText("R$ " + String.format("%.2f", 0.0));
            lb_VlVendaLiquidaResultado.setText("R$ " + String.format("%.2f", 0.0));
        }
    }

    private void fu_CalcularResultadosTipoVenda(){

        try {
            String vf_TipoVenda = "";


            lb_QuantidadeTipoVendaAbertos.setText(getString(R.string.label_quantidade_tipovenda_pedido));
            lb_QuantidadeTipoVendaCancelados.setText(getString(R.string.label_quantidade_tipovenda_cancelados_pedidos));
            lb_VlTotalTipoVenda.setText(getString(R.string.label_valortotal_tipovenda_pedidos));

            if (ctl_VisaoGeral.fu_CarregarDadosTipoVenda()) {

                lb_QuantidadeTipoVendaAbertosResultado.setText(String.valueOf(cl_VisaoGeral.getCountTipoVenda()) + " pedido");

                if (cl_VisaoGeral.getCountTipoVenda() != 1) {
                    lb_QuantidadeTipoVendaAbertosResultado.setText(lb_QuantidadeTipoVendaAbertosResultado.getText() + " pedidos");
                }

                lb_QuantidadeTipoVendaCanceladosResultado.setText(cl_VisaoGeral.getCountCanceladosTipoVenda() + " pedido");

                if (cl_VisaoGeral.getCountTipoVenda() != 1) {
                    lb_QuantidadeTipoVendaCanceladosResultado.setText(lb_QuantidadeTipoVendaCanceladosResultado.getText() + " pedidos");
                }

                lb_VlDescontoTipoVendaResultado.setText("R$ " + String.format("%.2f", cl_VisaoGeral.getVlDescontoTipoVenda()) + "");
                String vf_VlTotalTipoVenda = String.format("%.2f", cl_VisaoGeral.getVlTotalTipoVenda());
                lb_VlTotalTipoVendaResultado.setText("R$ " + vf_VlTotalTipoVenda + "");

            } else {
                MensagemUtil.addMsg(VisaoGeralNova.this, getString(R.string.mensagem_visaogeral_falhacarregamentopedidos) + vf_TipoVenda + "s");
            }

        }catch (Exception e){
            MensagemUtil.addMsg(VisaoGeralNova.this, "Não foi possível carregar os dados devido à seguinte situação: " + e.getMessage());
        }
    }

    private boolean fu_CarregarProdutosZerados(){

        vc_CountProdutosAtencao = ctl_VisaoGeral.fu_BuscarProdutosAtencao();
        //countProdutosAtencao = ctl_VisaoGeral.buscaProdutosAtencao();

        if(vc_CountProdutosAtencao > 0){
            if(vc_CountProdutosAtencao == 1){
                lb_ProdutosAtencao.setText("Existe " + String.valueOf(vc_CountProdutosAtencao) + " " + getString(R.string.label_produtoatencaounico));
            }else{
                lb_ProdutosAtencao.setText("Existem " + String.valueOf(vc_CountProdutosAtencao) + " " + getString(R.string.label_produtosatencao));
            }
        }else{
            lb_ProdutosAtencao.setText(getString(R.string.label_semprodutosatencao));
            if(!ctl_VisaoGeral.vc_Mensagem.trim().equals("")){
                MensagemUtil.addMsg(VisaoGeralNova.this, ctl_VisaoGeral.vc_Mensagem);
                return false;
            }
        }

        return true;
    }
}
