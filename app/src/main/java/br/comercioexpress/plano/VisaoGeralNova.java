package br.comercioexpress.plano;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import classes.CL_Filial;
import classes.CL_Pedidos;
import classes.CL_Usuario;
import classes.CL_VisaoGeral;
import controllers.CTL_Filial;
import controllers.CTL_Pedidos;
import controllers.CTL_Usuario;
import controllers.CTL_VisaoGeral;
import models.MDL_Usuario;

public class VisaoGeralNova extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    Funcoes funcoes = new Funcoes();

    CL_VisaoGeral cl_VisaoGeral;
    CTL_VisaoGeral ctl_VisaoGeral;

    private AlertDialog ad_VisaoGeral;
    AlertDialog.Builder bu_VisaoGeral;

    //Instancia da imagem para seleção da data da movimentação
    ImageView img_DtEmissaoPicker, img_dtSelecionarDatas, img_dtSelecionarDatasFechar;

    //Função de calendário para seleção da data de emissão da venda
    final Calendar vc_MyCalendar = Calendar.getInstance();

    //Instancia das labels que apresentarão os resultados da movimentação diária
    TextView lb_DtMovimentacaoDiaria, lb_VlVendaBrutaResultado, lb_VlDescontosVendaResultado, lb_VlVendaLiquidaResultado,
            lb_VlLucroMedioResultado, lb_DatasSelecionadas;

    //Instancia dos botões da seção de tipos de venda
    Button btn_Orcamentos, btn_Pedidos, btn_Vendas;

    //Instancia das labels que apresentarão os resultados do tipo de venda
    TextView lb_QuantidadeTipoVendaAbertos, lb_QuantidadeTipoVendaCancelados, lb_VlTotalTipoVenda;
    TextView lb_QuantidadeTipoVendaAbertosResultado, lb_QuantidadeTipoVendaCanceladosResultado, lb_VlDescontoTipoVendaResultado,
            lb_VlTotalTipoVendaResultado, lb_quantidadeTipoVendaEnviadosResultado,
            lb_vlTotalTipoVendaCanceladosResultado, lb_vlTotalTipoVendaEnviadosResultado;

    //Instancia da label do estoque.
    TextView lb_ProdutosAtencao;

    //Variáveis para quando o usuário selecionar o periodo de datas na seção de total de tipos de venda
    String vc_DtInicial = "", vc_DtFinal = "";

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

        fu_InstanciarCampos();

        fu_AtualizarData("S");
        //fu_CarregarProdutosZerados();
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
        img_dtSelecionarDatas = (ImageView)findViewById(R.id.img_dtSelecionarDatasPicker);
        img_dtSelecionarDatasFechar = (ImageView)findViewById(R.id.img_dtSelecionarDatasFechar);

        lb_DatasSelecionadas = (TextView)findViewById(R.id.lb_DatasSelecionadas);

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

                MensagemUtil.addMsg(VisaoGeralNova.this, "Por favor selecione a data inicial");

                new DatePickerDialog(VisaoGeralNova.this, date, vc_MyCalendar
                        .get(Calendar.YEAR), vc_MyCalendar.get(Calendar.MONTH),
                        vc_MyCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }

        });

        final DatePickerDialog.OnDateSetListener dateInicial = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                vc_MyCalendar.set(Calendar.YEAR, year);
                vc_MyCalendar.set(Calendar.MONTH, monthOfYear);
                vc_MyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fu_AtualizaDataInicial("N");
            }

        };

        img_dtSelecionarDatas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(VisaoGeralNova.this, dateInicial, vc_MyCalendar
                        .get(Calendar.YEAR), vc_MyCalendar.get(Calendar.MONTH),
                        vc_MyCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        img_dtSelecionarDatasFechar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cl_VisaoGeral.setDataInicial("");
                cl_VisaoGeral.setDataFinal("");

                fu_CalcularResultadosTipoVenda();
                lb_DatasSelecionadas.setVisibility(View.GONE);
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
        lb_quantidadeTipoVendaEnviadosResultado = (TextView)findViewById(R.id.lb_quantidadeTipoVendaEnviadosResultado);
        lb_VlDescontoTipoVendaResultado = (TextView)findViewById(R.id.lb_vlDescontoTipoVendaResultado);
        lb_VlTotalTipoVendaResultado = (TextView)findViewById(R.id.lb_vlTotalTipoVendaResultado);

        lb_vlTotalTipoVendaCanceladosResultado = (TextView)findViewById(R.id.lb_vlTotalTipoVendaCanceladosResultado);
        lb_vlTotalTipoVendaEnviadosResultado = (TextView)findViewById(R.id.lb_vlTotalTipoVendaEnviadosResultado);

        //----------------------------------------------------------------------------------------

        //Campos da seção de produtos-------------------------------------------------------------
        //lb_ProdutosAtencao = (TextView)findViewById(R.id.lb_produtosAtencao);
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

    private void fu_AtualizaDataInicial(String fgDataInicial){

        String data = "";
        String myFormat = "dd/MM/yyyy"; //In which you need put
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        cl_VisaoGeral.setDataInicial(sdf.format(vc_MyCalendar.getTime()));

        MensagemUtil.addMsg(VisaoGeralNova.this, "Por favor selecione a data final");

        final DatePickerDialog.OnDateSetListener dateFinal = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                vc_MyCalendar.set(Calendar.YEAR, year);
                vc_MyCalendar.set(Calendar.MONTH, monthOfYear);
                vc_MyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fu_AtualizaDataFinal("N");
            }

        };

        new DatePickerDialog(VisaoGeralNova.this, dateFinal, vc_MyCalendar
                .get(Calendar.YEAR), vc_MyCalendar.get(Calendar.MONTH),
                vc_MyCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void fu_AtualizaDataFinal(String fgDataFinal){
        String data = "";
        String myFormat = "dd/MM/yyyy"; //In which you need put
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        cl_VisaoGeral.setDataFinal(sdf.format(vc_MyCalendar.getTime()));

        lb_DatasSelecionadas.setVisibility(View.VISIBLE);
        lb_DatasSelecionadas.setText("Período: " + cl_VisaoGeral.getDataInicial() +  " até " + cl_VisaoGeral.getDataFinal() + "");


        Calendar c = vc_MyCalendar;
        c.add(Calendar.DATE, 1);

        cl_VisaoGeral.setDataFinal(sdf.format(c.getTime()));

        fu_CalcularResultadosTipoVenda();
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

            ctl_VisaoGeral = new CTL_VisaoGeral(getApplicationContext(), cl_VisaoGeral);

            if (ctl_VisaoGeral.fu_CarregarDadosTipoVenda(cl_VisaoGeral.getDataInicial(), cl_VisaoGeral.getDataFinal())) {

                lb_QuantidadeTipoVendaAbertosResultado.setText(String.valueOf(cl_VisaoGeral.getCountTipoVenda()) + " pedido");

                if (cl_VisaoGeral.getCountTipoVenda() != 1) {
                    lb_QuantidadeTipoVendaAbertosResultado.setText(lb_QuantidadeTipoVendaAbertosResultado.getText() + "s");
                }

                lb_QuantidadeTipoVendaCanceladosResultado.setText(cl_VisaoGeral.getCountCanceladosTipoVenda() + " pedido");

                if (cl_VisaoGeral.getCountCanceladosTipoVenda() != 1) {
                    lb_QuantidadeTipoVendaCanceladosResultado.setText(lb_QuantidadeTipoVendaCanceladosResultado.getText() + "s");
                }

                lb_quantidadeTipoVendaEnviadosResultado.setText(cl_VisaoGeral.getCountEnviadosTipoVenda() + " pedido");

                if (cl_VisaoGeral.getCountEnviadosTipoVenda() != 1) {
                    lb_quantidadeTipoVendaEnviadosResultado.setText(lb_quantidadeTipoVendaEnviadosResultado.getText() + "s");
                }

                lb_VlDescontoTipoVendaResultado.setText("R$ " + String.format("%.2f", cl_VisaoGeral.getVlDescontoTipoVenda()) + "");
                String vf_VlTotalTipoVenda = String.format("%.2f", cl_VisaoGeral.getVlTotalTipoVenda());
                lb_VlTotalTipoVendaResultado.setText("R$ " + vf_VlTotalTipoVenda + "");

                //lb_vlTotalTipoVendaCanceladosResultado, lb_vlTotalTipoVendaEnviadosResultado
                String vl_VlTotalCancelados = String.format("%.2f", cl_VisaoGeral.getVlTotalCancelados());
                lb_vlTotalTipoVendaCanceladosResultado.setText("R$ " + vl_VlTotalCancelados + "");
                String vf_VlTotalEnviados = String.format("%.2f", cl_VisaoGeral.getVlTotalEnviados());
                lb_vlTotalTipoVendaEnviadosResultado.setText("R$ " + vf_VlTotalEnviados + "");



            } else {
                lb_QuantidadeTipoVendaAbertosResultado.setText("0 pedidos");
                lb_QuantidadeTipoVendaCanceladosResultado.setText("0 pedidos");
                lb_quantidadeTipoVendaEnviadosResultado.setText("0 pedidos");
                lb_VlDescontoTipoVendaResultado.setText("R$ 0,00");
                lb_VlTotalTipoVendaResultado.setText("R$ 0,00");
                lb_vlTotalTipoVendaCanceladosResultado.setText("R$ 0,00");
                lb_vlTotalTipoVendaEnviadosResultado.setText("R$ 0,00");
                MensagemUtil.addMsg(VisaoGeralNova.this, getString(R.string.mensagem_visaogeral_falhacarregamentopedidos) + vf_TipoVenda + "s");
            }

        }catch (Exception e){
            MensagemUtil.addMsg(VisaoGeralNova.this, "Não foi possível carregar os dados devido à seguinte situação: " + e.getMessage());
        }
    }

}
