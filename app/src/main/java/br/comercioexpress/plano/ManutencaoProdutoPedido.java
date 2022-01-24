package br.comercioexpress.plano;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

import classes.CL_Clientes;
import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import classes.CL_Produtos;
import controllers.CTL_Clientes;
import controllers.CTL_ItemPedido;
import controllers.CTL_Pedidos;
import controllers.CTL_Produtos;
import models.CriaBanco;

public class ManutencaoProdutoPedido extends AppCompatActivity {

    CL_Pedidos cl_Pedidos;
    CTL_Pedidos ctl_Pedidos;

    CL_ItemPedido cl_ItemPedido;
    CTL_ItemPedido ctl_ItemPedido;

    CL_Produtos cl_Produto;

    TextView lb_descricaoProduto, lb_produtoAdicionado, lb_codigoProdutoResultado, lb_valorLiquidoResultado,
            lb_valorSemDescontoResultado, lb_valorComDescontoResultado;
    EditText tb_quantidadeProduto, tb_descontoProduto, tb_valorUnitarioProduto, tb_observacaoItemPedido;

    String vc_CdProduto, vc_NumPedido, vc_Operacao, vc_Alteracao;

    double percdescmaxvendedor;
    double percDescontoClassificacao = 0; //19429
    double VL_valorBruto;

    private AlertDialog alerta;
    AlertDialog.Builder builder;
    boolean validou;

    FloatingActionButton fab_SalvarProdutosPedido;

    MenuItem me_Salvar, me_Excluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_produto_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Função que instancia os campos do xml para utilizar na classe
        suInstanciarCampos();

        //Verifica se o produto que está vindo foi chamado a partir da lista de itens do pedido(alteração) ou a partir da lista de produtos(inclusão)
        if (vc_Alteracao.equals("S")) {
            cl_ItemPedido.setId(vc_CdProduto);
            ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

            if(ctl_ItemPedido.fuCarregaItemPedidoAlteracaoId()){
                try {
                    lb_descricaoProduto.setText(cl_ItemPedido.getDescricao());
                } catch (Exception e) {
                    lb_descricaoProduto.setText("");
                }
                try {
                    lb_codigoProdutoResultado.setText(cl_ItemPedido.getCdProduto());
                } catch (Exception e) {
                    lb_codigoProdutoResultado.setText("0");
                }
                try {
                    tb_quantidadeProduto.setText(cl_ItemPedido.getQtde());
                } catch (Exception e) {
                    tb_quantidadeProduto.setText("0");
                }

                try {
                        tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario().replace(",", "."));
                        VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
                        String valorUnitario = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                        tb_valorUnitarioProduto.setText(valorUnitario);
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0");
                }

                /*try {
                        tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlLiquido().replace(",", "."));
                        String valorLiquido = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                        tb_valorUnitarioProduto.setText(valorLiquido);
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0");
                }*/

                try {
                    lb_valorLiquidoResultado.setText(cl_ItemPedido.getVlLiquido());
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorLiquidoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlSemDesconto = 0.0;
                    vf_VlSemDesconto = Double.parseDouble(cl_ItemPedido.getVlUnitario().replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde().replace(",", "."));

                    lb_valorSemDescontoResultado.setText("R$" + String.format("%.2f", vf_VlSemDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorSemDescontoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlComDesconto = 0.0;
                    vf_VlComDesconto = Double.parseDouble(cl_ItemPedido.getVlLiquido().replace("R$", "").replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde().replace(",", "."));

                    lb_valorComDescontoResultado.setText("R$" + String.format("%.2f", vf_VlComDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorComDescontoResultado.setText("R$0,00");
                }


                vc_Operacao = "A";

                try {
                    tb_descontoProduto.setText(cl_ItemPedido.getPercDesconto());
                } catch (Exception e) {
                    tb_descontoProduto.setText("0");
                }

                try {
                    tb_observacaoItemPedido.setText(cl_ItemPedido.getObservacao());
                } catch (Exception e) {
                    tb_observacaoItemPedido.setText("");
                }

                lb_produtoAdicionado.setVisibility(View.VISIBLE);

                suBuscaPrecosCliente("S");

                cl_Produto = new CL_Produtos();
                cl_Produto.setCdProduto(lb_codigoProdutoResultado.getText().toString());

                CTL_Produtos ctl_Produtos = new CTL_Produtos(getApplicationContext(), cl_Produto);

                if(ctl_Produtos.fuCarregaProdutoCdProduto()){
                    vc_CdProduto = cl_Produto.getId();
                }


            }

        } else {
            cl_Produto = new CL_Produtos();
            cl_Produto.setId(vc_CdProduto);

            CTL_Produtos ctl_Produtos = new CTL_Produtos(getApplicationContext(), cl_Produto);
            if(ctl_Produtos.fuCarregaProduto()){
                cl_ItemPedido = new CL_ItemPedido();

                try {
                    lb_descricaoProduto.setText(cl_Produto.getDescricao().toUpperCase());
                    cl_ItemPedido.setDescricao(cl_Produto.getDescricao().toUpperCase());
                } catch (Exception e) {
                    lb_descricaoProduto.setText("");
                    cl_ItemPedido.setDescricao("");
                }
                try {
                    lb_codigoProdutoResultado.setText(cl_Produto.getCdProduto());
                    cl_ItemPedido.setCdProduto(lb_codigoProdutoResultado.getText().toString());
                } catch (Exception e) {
                    lb_codigoProdutoResultado.setText("0");
                    cl_ItemPedido.setCdProduto("0");
                }

                tb_quantidadeProduto.setText("1");
                cl_ItemPedido.setQtde("1");

                try {
                    tb_valorUnitarioProduto.setText(cl_Produto.getVlUnitario());

                    String valorProduto = tb_valorUnitarioProduto.getText().toString();
                    String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                    tb_valorUnitarioProduto.setText(valor.replace(",", "."));
                    VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
                    cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                    cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0.00");
                    cl_ItemPedido.setVlUnitario("0.00");
                    cl_ItemPedido.setVlLiquido("0.00");
                }

                suBuscaPrecosCliente("N");
                if(percDescontoClassificacao > 0) {
                    tb_descontoProduto.setText(String.valueOf(percDescontoClassificacao));
                }else{
                    tb_descontoProduto.setText("0");
                }
                cl_ItemPedido.setVlDesconto(String.valueOf(percDescontoClassificacao));

                if(percDescontoClassificacao > 0){
                    double vf_PercDesconto = Double.parseDouble(tb_descontoProduto.getText().toString());
                    double vf_ValorBruto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", "."));
                    double vf_ValorLiquido = vf_ValorBruto - (vf_ValorBruto * (vf_PercDesconto / 100));

                    cl_ItemPedido.setVlLiquido(String.valueOf(vf_ValorLiquido));
                }
                suCalculaValorItemPedido();
                /*cl_ItemPedido.setVlTotal("0.00");

                try {
                    lb_valorLiquidoResultado.setText("R$" + String.format("%.2f", Double.parseDouble(cl_Produto.getVlUnitario().replace(",", "."))));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorLiquidoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlSemDesconto = 0.0;
                    vf_VlSemDesconto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", ".")) * 1;

                    lb_valorSemDescontoResultado.setText("R$" + String.format("%.2f", vf_VlSemDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorSemDescontoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlComDesconto = 0.0;
                    vf_VlComDesconto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", ".")) * 1;

                    lb_valorComDescontoResultado.setText("R$" + String.format("%.2f", vf_VlComDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorComDescontoResultado.setText("R$0,00");
                }*/

                suVerificaItemPedido();


            }

        }

        Locale mLocale = new Locale("pt", "BR");
        tb_valorUnitarioProduto.addTextChangedListener(new MoneyTextWatcher(tb_valorUnitarioProduto, mLocale));

        tb_valorUnitarioProduto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    try {
                        double VA_VlUnitarioNovo = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().trim().replace(",", "."));
                        if (VA_VlUnitarioNovo - VL_valorBruto < 0) {

                            try {

                                CL_Produtos cl_Produto = new CL_Produtos();
                                cl_Produto.setId(vc_CdProduto);

                                CTL_Produtos ctl_Produto = new CTL_Produtos(getApplicationContext(), cl_Produto);

                                if(ctl_Produto.fuCarregaProduto()){
                                    double valorBruto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", "."));
                                    double valorLiquido = valorBruto - VA_VlUnitarioNovo;

                                    if(valorLiquido == 0.0){

                                        tb_descontoProduto.setText(String.format("%.5f", 0.0).replace(",", "."));
                                        tb_valorUnitarioProduto.setText(String.format("%.2f", valorBruto).replace(".", "").replace(",", "."));

                                        tb_descontoProduto.setEnabled(true);

                                        cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                                        cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                                        cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                                        suCalculaValorItemPedido();

                                    }else {

                                        double porcentagem = ((valorBruto - valorLiquido) / valorBruto) * 100;
                                        porcentagem = 100 - porcentagem;
                                        if (porcentagem > percdescmaxvendedor) {
                                            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                                            tb_valorUnitarioProduto.setText(String.format("%.2f", valorBruto));
                                            //tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario());
                                            tb_descontoProduto.setText("0.00000");
                                            tb_valorUnitarioProduto.setText(String.format("%.2f", valorBruto));

                                            cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                                            cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                                            suCalculaValorItemPedido();

                                        } else {
                                            tb_descontoProduto.setText(String.format("%.5f", porcentagem).replace(",", "."));
                                            valorLiquido = VL_valorBruto - Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
                                            tb_valorUnitarioProduto.setText(String.format("%.2f", VL_valorBruto).replace(".", "").replace(",", "."));

                                            tb_descontoProduto.setEnabled(true);

                                            cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                                            cl_ItemPedido.setVlLiquido(String.format("%.2f", VA_VlUnitarioNovo));
                                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                                            suCalculaValorItemPedido();
                                        }
                                    }
                                }


                            } catch (Exception e) {

                            }



                        } else if (VA_VlUnitarioNovo - VL_valorBruto > 0) {
                            tb_descontoProduto.setEnabled(false);
                            tb_descontoProduto.setText("0.00000");

                            tb_valorUnitarioProduto.setText(String.format("%.2f", VA_VlUnitarioNovo));
                            cl_ItemPedido.setVlUnitario(String.valueOf(VA_VlUnitarioNovo));
                            cl_ItemPedido.setVlLiquido(String.valueOf(VA_VlUnitarioNovo));
                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                            suCalculaValorItemPedido();
                        } else {
                            tb_descontoProduto.setEnabled(true);

                            cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                            cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                            suCalculaValorItemPedido();

                            //tb_descontoProduto.setText("0");
                        }
                        VA_VlUnitarioNovo = VL_valorBruto; //Realizado esse tratamento para que o sistema mantenha o valor incial do produto

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tb_quantidadeProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suCalculaValorItemPedido();
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

        tb_descontoProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suConsisteDescontos();
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

    protected void suCalculaValorItemPedido(){
        if (tb_quantidadeProduto.getText().toString().trim().equals("")) {
            cl_ItemPedido.setQtde("0");
        } else {
            cl_ItemPedido.setQtde(tb_quantidadeProduto.getText().toString());

            try {
                double vf_vlTotalItemPedido = Double.parseDouble(cl_ItemPedido.getVlLiquido().replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde().replace(",", "."));
                cl_ItemPedido.setVlTotal(String.format("%.2f",vf_vlTotalItemPedido));

                try {
                    lb_valorLiquidoResultado.setText("R$" + String.format("%.2f", Double.parseDouble(cl_ItemPedido.getVlLiquido().replace(",", "."))));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorLiquidoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlSemDesconto = 0.0;
                    vf_VlSemDesconto = Double.parseDouble(cl_ItemPedido.getVlUnitario().replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde());

                    lb_valorSemDescontoResultado.setText("R$" + String.format("%.2f", vf_VlSemDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorSemDescontoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlComDesconto = 0.0;
                    vf_VlComDesconto = Double.parseDouble(cl_ItemPedido.getVlLiquido().replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde());

                    lb_valorComDescontoResultado.setText("R$" + String.format("%.2f", vf_VlComDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorComDescontoResultado.setText("R$0,00");
                }
            }catch (Exception e){
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não foi possível realizar o calculo do valor total do item do pedido");
            }

        }
    }

    protected void suConsisteDescontos() {

        try {
            if(tb_descontoProduto.getText().toString().trim().equals("")){
                tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario());
                //tb_descontoProduto.setText("0.00000");

                cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                suCalculaValorItemPedido();
            }else {
                double vf_PercDesconto = Double.parseDouble(tb_descontoProduto.getText().toString());


                if (vf_PercDesconto > percdescmaxvendedor) {
                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                    tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario());
                    tb_descontoProduto.setText("0.00000");

                    cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                    cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                    cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                    suCalculaValorItemPedido();
                } else {
                    if (vf_PercDesconto > 0.0) {
                        CL_Produtos cl_Produto = new CL_Produtos();
                        cl_Produto.setId(vc_CdProduto);

                        CTL_Produtos ctl_Produto = new CTL_Produtos(getApplicationContext(), cl_Produto);

                        if (ctl_Produto.fuCarregaProduto()) {
                            double vf_ValorBruto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", "."));
                            double vf_ValorLiquido = vf_ValorBruto - (vf_ValorBruto * (vf_PercDesconto / 100));

                            DecimalFormat fmt = new DecimalFormat("0.00");   //limita o número de casas decimais
                            String vf_VlLiquidoFormatado = fmt.format(vf_ValorLiquido);
                            cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                            //cl_ItemPedido.setVlLiquido(String.format("%.2f", testeVlLiquido));
                            cl_ItemPedido.setVlLiquido(vf_VlLiquidoFormatado);
                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                            suCalculaValorItemPedido();
                        } else {

                            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não foi possível carregar os valores do produto para aplicar o desconto!");

                            tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario());
                            tb_descontoProduto.setText("0.00000");

                            cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                            cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                            cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                            suCalculaValorItemPedido();
                        }
                    } else {
                        tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlLiquido());
                        //tb_descontoProduto.setText("0.00000");

                        cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                        cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                        cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                        suCalculaValorItemPedido();
                    }
                }
            }


        } catch (Exception e) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manutencaoproduto, menu);

        me_Salvar = menu.findItem(R.id.menu_salvarprodutopedido);
        me_Excluir = menu.findItem(R.id.menu_excluirprodutopedido);

        if(vc_Operacao.equals("A")){
            me_Excluir.setVisible(true);
        }else{
            me_Excluir.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){

            if(vc_Alteracao.equals("N")){
                Intent intent = new Intent();
                intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                if (vc_Alteracao.equals("S")) {
                    setResult(2, intent);
                } else {
                    setResult(1, intent);
                }
                finish();
            }else {
                Intent intent2 = new Intent();
                intent2.putExtra("numpedido", cl_Pedidos.getNumPedido());
                setResult(1, intent2);
                finish();
            }

            return true;
        }

        if(id == R.id.menu_salvarprodutopedido) {
            suSalvarProduto();
            return true;
        }

        if(id == R.id.menu_excluirprodutopedido){
            suExcluirProduto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void suInstanciarCampos(){

        cl_Pedidos = new CL_Pedidos();
        cl_ItemPedido = new CL_ItemPedido();

        vc_CdProduto = this.getIntent().getStringExtra("codigo");
        vc_NumPedido = this.getIntent().getStringExtra("numpedido");
        vc_Alteracao = this.getIntent().getStringExtra("alteracao");

        cl_Pedidos.setNumPedido(vc_NumPedido);
        cl_ItemPedido.setNumPedido(vc_NumPedido);

        fab_SalvarProdutosPedido = (FloatingActionButton) findViewById(R.id.fab_SalvarManutencaoProdutoPedido);
        fab_SalvarProdutosPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suSalvarProduto();
            }
        });

        lb_descricaoProduto = (TextView) findViewById(R.id.lb_descricaoProdutoManutencao);
        lb_codigoProdutoResultado = (TextView) findViewById(R.id.lb_codigoProdutoResultado);
        tb_quantidadeProduto = (EditText) findViewById(R.id.tb_qtdeProdutoManutencao);
        tb_descontoProduto = (EditText) findViewById(R.id.tb_descontoProdutoManutencao);
        tb_valorUnitarioProduto = (EditText) findViewById(R.id.tb_valorUnitarioManutencao);

        //Novos campos adicionados:
        tb_observacaoItemPedido = (EditText)findViewById(R.id.tb_obsItemPedido);
        lb_valorLiquidoResultado = (TextView)findViewById(R.id.lb_vlTotalLiquidoResultado);
        lb_valorSemDescontoResultado = (TextView)findViewById(R.id.lb_vlTotalSemDescontoResultado);
        lb_valorComDescontoResultado = (TextView)findViewById(R.id.lb_vlTotalComDescontoResultado);


        lb_produtoAdicionado = (TextView) findViewById(R.id.lb_produtoAdicionado);
    }

    protected void suSalvarProduto(){
        if(fuConsisteItem()) {
            if(vc_Operacao.equals("I")) {
                if (fuIncluirItemPedido()) {
                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto incluido com sucesso!");
                    Intent intent = new Intent();
                    intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                    setResult(1, intent);
                    finish();
                }
            }else{

                if(fuAlteraItemPedido()){
                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto alterado com sucesso!");
                    Intent intent = new Intent();
                    intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                    setResult(1, intent);
                    finish();
                }
            }
        }
    }

    protected void suExcluirProduto() {
        builder = new AlertDialog.Builder(this);


        //define o titulo
        builder.setTitle("Excluir Produto");
        //define a mensagem
        //builder.setMessage("Deseja mesmo excluir o produto " + lb_descricaoProduto.getText().toString() + "?");
        builder.setMessage("Deseja mesmo excluir o produto do pedido?");

        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                try {

                    cl_ItemPedido.setCdProduto(lb_codigoProdutoResultado.getText().toString());
                    ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

                    if(ctl_ItemPedido.fuDeletarItemPedido()){
                        MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto excluido com sucesso!");
                        Intent intent = new Intent();
                        intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
                        if (vc_Alteracao.equals("S")) {
                            setResult(2, intent);
                        } else {
                            setResult(1, intent);
                        }
                        //intent.putExtra("codigo", codigo);
                        //startActivity(intent);
                        finish();
                    }else{
                        MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não foi possivel excluir o produto do pedido devido à seguinte situação:" + ctl_ItemPedido.vc_Mensagem);
                    }

                } catch (Exception e) {
                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não foi possivel excluir o produto do pedido devido à seguinte situação:" + e.getMessage().toString());
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("numpedido", cl_Pedidos.getNumPedido());
        setResult(0, intent);
        finish();
    }

    protected boolean fuIntanciarItemPedido(){

        try {
            double VA_vlUnitarioProduto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
            double vltotal = Double.parseDouble(tb_quantidadeProduto.getText().toString()) * Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
            double vldesconto = 0;

            String vlmaxdescpermitido = "0";
            String valor = String.format("%.2f", vltotal);
            String valorTotalDesconto = String.format("%.2f", vldesconto);

            String valorPercDesconto = "0";
            if (VA_vlUnitarioProduto < VL_valorBruto) {
                if (percdescmaxvendedor > 0) {
                    double VA_vlDesconto = percdescmaxvendedor;
                    double porcentagem = VA_vlDesconto / 100;
                    double resultado = vltotal * porcentagem;
                    vlmaxdescpermitido = String.format("%.2f", resultado);
                }

                valorTotalDesconto = String.format("%.2f", VL_valorBruto - VA_vlUnitarioProduto);

                if (!tb_descontoProduto.getText().toString().trim().equals("")) {
                    valorPercDesconto = tb_descontoProduto.getText().toString();
                }

            } else {

                if (VA_vlUnitarioProduto > VL_valorBruto) {

                }else{
                    if (percdescmaxvendedor > 0) {
                        double VA_vlDesconto = percdescmaxvendedor;
                        double porcentagem = VA_vlDesconto / 100;
                        double resultado = vltotal * porcentagem;
                        vlmaxdescpermitido = String.format("%.2f", resultado);
                    }

                    if (!tb_descontoProduto.getText().toString().trim().equals("")) {

                        double vf_ValorBruto = Double.parseDouble(cl_Produto.getVlUnitario().replace(",", "."));
                        double VA_vlDesconto = Double.parseDouble(tb_descontoProduto.getText().toString());
                        double vf_ValorLiquido =  vf_ValorBruto - (vf_ValorBruto * (VA_vlDesconto / 100));

                        DecimalFormat fmt = new DecimalFormat("0.00");   //limita o número de casas decimais
                        String vf_VlLiquidoFormatado = fmt.format(vf_ValorLiquido);
                        double vlTotalLiquido = Double.parseDouble(tb_quantidadeProduto.getText().toString()) * Double.parseDouble(vf_VlLiquidoFormatado.replace(",", "."));
                        valor = String.format("%.2f", vlTotalLiquido);;


                    /*double porcentagem = VA_vlDesconto / 100;
                    vldesconto = vltotal * porcentagem;
                    double resultadoTotal = vltotal - vldesconto;
                    vltotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
                    DecimalFormat fmt = new DecimalFormat("0.00");   //limita o número de casas decimais
                    String vf_VlTotalFormatado = fmt.format(vltotal);
                    valor = vf_VlTotalFormatado;*/
                        //valor = String.format("%.2f", vltotal);
                    }

                    if (!tb_descontoProduto.getText().toString().trim().equals("")) {
                        valorPercDesconto = tb_descontoProduto.getText().toString();
                    }

                    valorTotalDesconto = String.format("%.2f", vldesconto);
                }

            }

            String valorunitarioteste = String.format("%.2f", VA_vlUnitarioProduto);

            cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());
            cl_ItemPedido.setCdProduto(lb_codigoProdutoResultado.getText().toString());
            cl_ItemPedido.setDescricao(lb_descricaoProduto.getText().toString());
            cl_ItemPedido.setQtde(tb_quantidadeProduto.getText().toString());
            cl_ItemPedido.setPercDesconto(valorPercDesconto);
            cl_ItemPedido.setVlDesconto(valorTotalDesconto.replace(",", "."));
            cl_ItemPedido.setVlMaxDescPermitido(vlmaxdescpermitido.replace(",", "."));
            cl_ItemPedido.setVlUnitario(valorunitarioteste);
            cl_ItemPedido.setVlLiquido(lb_valorLiquidoResultado.getText().toString().replace("R$", ""));



            cl_ItemPedido.setVlTotal(valor);
            cl_ItemPedido.setObservacao(tb_observacaoItemPedido.getText().toString().toUpperCase());

            ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);

            return true;

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuIncluirItemPedido(){

        try {

            if(fuIntanciarItemPedido()){
                if(ctl_ItemPedido.fuInserirItemPedido()){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    public boolean fuAlteraItemPedido(){
        try {

            if(fuIntanciarItemPedido()){
                if(ctl_ItemPedido.fuAlterarItemPedido()){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuConsisteItem() {

        try {
            double vf_QuantidadeProduto;
            double vf_DescontoProduto, vf_VlUnitarioProduto;

            if (tb_quantidadeProduto.getText().toString().trim().equals("")) {
                vf_QuantidadeProduto = 0;
            } else {
                vf_QuantidadeProduto = Double.parseDouble(tb_quantidadeProduto.getText().toString());
            }
            if (tb_descontoProduto.getText().toString().trim().equals("")) {
                vf_DescontoProduto = 0;
            } else {
                vf_DescontoProduto = Double.parseDouble(tb_descontoProduto.getText().toString());
            }
            if (tb_valorUnitarioProduto.getText().toString().trim().equals("")) {
                vf_VlUnitarioProduto = 0;
            } else {
                vf_VlUnitarioProduto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
            }

            if (vf_QuantidadeProduto == 0) {
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com a quantidade igual a 0!");
                return false;
            }

            if (tb_valorUnitarioProduto.getText().toString().trim().equals("")) {
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o valor unitário em branco!");
                return false;
            }

            if (vf_VlUnitarioProduto == 0) {
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o valor unitário igual a 0!");
                return false;
            }

            try {
                BancoController crud = new BancoController(getBaseContext());
                Cursor cursorVlUnitario = crud.carregaDadosProdutosByCdProduto(lb_codigoProdutoResultado.getText().toString());

                VL_valorBruto = Double.parseDouble(cursorVlUnitario.getString(cursorVlUnitario.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", "."));
                if (VL_valorBruto != 0) {
                    DecimalFormat fmt = new DecimalFormat("0.00");   //limita o número de casas decimais
                    String string = fmt.format(VL_valorBruto);
                    //String[] part = string.split("[,]");
                    //String preco = part[0]+"."+part[1];
                    VL_valorBruto = Double.parseDouble(string.replace(",", "."));
                }
                if (vf_VlUnitarioProduto < VL_valorBruto) {

                    try {
                        double VA_VlUnitarioNovo = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().trim().replace(",", "."));
                        if (VA_VlUnitarioNovo - VL_valorBruto < 0) {

                            try {
                                Cursor cursor = crud.carregaProdutosById(Integer.parseInt(vc_CdProduto));

                                double valorBruto = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", "."));
                                double valorLiquido = valorBruto - VA_VlUnitarioNovo;

                                double porcentagem = ((valorBruto - valorLiquido) / valorBruto) * 100;
                                porcentagem = 100 - porcentagem;

                                if (porcentagem > percdescmaxvendedor) {
                                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                                    tb_valorUnitarioProduto.setText(String.format("%.2f", valorBruto));
                                    tb_descontoProduto.setText("0.00000");
                                    tb_valorUnitarioProduto.setText(String.format("%.2f", valorBruto));

                                    cl_ItemPedido.setVlUnitario(tb_valorUnitarioProduto.getText().toString());
                                    cl_ItemPedido.setVlLiquido(tb_valorUnitarioProduto.getText().toString());
                                    cl_ItemPedido.setVlDesconto(tb_descontoProduto.getText().toString());
                                    suCalculaValorItemPedido();
                                    return false;
                                } else {
                                    tb_descontoProduto.setText(String.format("%.5f", porcentagem).replace(",", "."));
                                }

                            } catch (Exception e) {

                            }

                        } else {
                            tb_descontoProduto.setText("");
                        }

                        double valorLiquido = VL_valorBruto - Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));

                        double porcentagem = ((VL_valorBruto - valorLiquido) / VL_valorBruto) * 100;
                        tb_descontoProduto.setText(String.format("%.5f", 100 - porcentagem).replace(",", "."));
                        vf_DescontoProduto = 100 - porcentagem;
                        tb_valorUnitarioProduto.setText(String.format("%.2f", VL_valorBruto).replace(".", "").replace(",", "."));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (vf_VlUnitarioProduto > VL_valorBruto) {
                    //tb_descontoProduto.setText("0");
                    vf_DescontoProduto = 0;
                } else {
                    //tb_descontoProduto.setText("0");
                }
            } catch (Exception e) {
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, e.getMessage());
            }

        /*if (VA_descontoProduto > 99) {
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o desconto superior a 99%!");
            return false;
        }*/

            if (vf_DescontoProduto > percdescmaxvendedor) {
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    protected void suBuscaPrecosCliente(String item){

        ctl_Pedidos = new CTL_Pedidos(getApplicationContext(), cl_Pedidos);

        String vf_CdCliente = ctl_Pedidos.fuBuscarCdClientePedido();
        int indexPonto = vf_CdCliente.indexOf(".");
        String vf_CdClienteInt = "";
        if(indexPonto == -1){
            vf_CdClienteInt = vf_CdCliente;
        }else{
            vf_CdClienteInt = vf_CdCliente.substring(0, indexPonto).replace(".", "");
        }

        CL_Clientes cl_Cliente = new CL_Clientes();
        cl_Cliente.setCdCliente(vf_CdCliente);

        CTL_Clientes ctl_Clientes = new CTL_Clientes(getApplicationContext(), cl_Cliente);
        if(ctl_Clientes.fuSelecionarCliente()){
            String vf_Classificacao = "";
            if (cl_Cliente.getClassificacao().trim().equals("")) {
                vf_Classificacao = "N";
            } else {
                vf_Classificacao = cl_Cliente.getClassificacao();
            }

            cl_Produto = new CL_Produtos();
            cl_Produto.setCdProduto(lb_codigoProdutoResultado.getText().toString());

            CTL_Produtos ctl_Produtos = new CTL_Produtos(getApplicationContext(), cl_Produto);

            if(ctl_Produtos.fuCarregaProdutoCdProduto()) {

                if (cl_Cliente.getFidelidade().equals("S")) {

                    try {
                        percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoFidelidade());
                    } catch (Exception e) {
                        percdescmaxvendedor = 0;
                    }
                } else if (vf_Classificacao.equals("N")) {
                    try {
                        percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitido());
                    } catch (Exception e) {
                        percdescmaxvendedor = 0;
                    }
                } else {
                    try {
                        if (vf_Classificacao.equals("A")) {

                            percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoA());
                            percDescontoClassificacao = Double.parseDouble(cl_Produto.getDescMaxPermitidoA());

                        } else if (vf_Classificacao.equals("B")) {

                            percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoB());
                            percDescontoClassificacao = Double.parseDouble(cl_Produto.getDescMaxPermitidoB());

                        } else if (vf_Classificacao.equals("C")) {

                            percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoC());
                            percDescontoClassificacao = Double.parseDouble(cl_Produto.getDescMaxPermitidoC());

                        } else if (vf_Classificacao.equals("D")) {

                            percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoD());
                            percDescontoClassificacao = Double.parseDouble(cl_Produto.getDescMaxPermitidoD());

                        } else if (vf_Classificacao.equals("E")) {

                            percdescmaxvendedor = Double.parseDouble(cl_Produto.getDescMaxPermitidoE());
                            percDescontoClassificacao = Double.parseDouble(cl_Produto.getDescMaxPermitidoE());

                        }
                    } catch (Exception e) {
                        percdescmaxvendedor = 0;
                        percDescontoClassificacao = 0;
                    }
                }
            }
        }

        if(item.equals("N")){

            if (ctl_Clientes.fuBuscaTipoPrecoCliente().equals("A")) {
                CL_Produtos vf_Cl_Produto = new CL_Produtos();
                vf_Cl_Produto.setCdProduto(lb_codigoProdutoResultado.getText().toString());

                CTL_Produtos ctl_Produtos = new CTL_Produtos(getApplicationContext(), cl_Produto);

                String vf_VlAtacado = ctl_Produtos.fuBuscaValorAtacado();

                try {
                    if (!vf_VlAtacado.equals("null")) {
                        tb_valorUnitarioProduto.setText(vf_VlAtacado);

                        String valorProduto = tb_valorUnitarioProduto.getText().toString();
                        String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                        tb_valorUnitarioProduto.setText(valor.replace(",", "."));
                        VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
                    }
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0.00");
                }
            }
        }
    }

    protected void suVerificaItemPedido(){

        cl_ItemPedido.setNumPedido(vc_NumPedido);
        cl_ItemPedido.setCdProduto(cl_Produto.getCdProduto());

        ctl_ItemPedido = new CTL_ItemPedido(getApplicationContext(), cl_ItemPedido);


        if(ctl_ItemPedido.fuCarregaItemPedidoCdProduto()){

            Cursor rs_ItemPedido = ctl_ItemPedido.rs_ItemPedido;

            while (!rs_ItemPedido.isAfterLast()) {

                try {
                    tb_quantidadeProduto.setText(cl_ItemPedido.getQtde());
                } catch (Exception e) {
                    tb_quantidadeProduto.setText("0");
                }
                try {
                   tb_descontoProduto.setText(cl_ItemPedido.getPercDesconto());
                } catch (Exception e) {
                    tb_descontoProduto.setText("0");
                }
                try {
                    tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlUnitario());

                    String valorProduto = tb_valorUnitarioProduto.getText().toString();
                    String valor = String.format("%.2f", Double.parseDouble(valorProduto.replace(",", ".")));
                    tb_valorUnitarioProduto.setText(valor);
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0,00");
                }

                /*try {
                    tb_valorUnitarioProduto.setText(cl_ItemPedido.getVlLiquido());
                    String valorLiquido = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                    tb_valorUnitarioProduto.setText(valorLiquido.replace(",", "."));
                } catch (Exception e) {
                    tb_valorUnitarioProduto.setText("0");
                }*/

                try {
                    lb_valorLiquidoResultado.setText(cl_ItemPedido.getVlLiquido());
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorLiquidoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlSemDesconto = 0.0;
                    vf_VlSemDesconto = Double.parseDouble(cl_ItemPedido.getVlUnitario().replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde().replace(",", "."));

                    lb_valorSemDescontoResultado.setText("R$" + String.format("%.2f", vf_VlSemDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorSemDescontoResultado.setText("R$0,00");
                }

                try {
                    double vf_VlComDesconto = 0.0;
                    vf_VlComDesconto = Double.parseDouble(cl_ItemPedido.getVlLiquido().replace("R$", "").replace(",", ".")) * Double.parseDouble(cl_ItemPedido.getQtde().replace(",", "."));

                    lb_valorComDescontoResultado.setText("R$" + String.format("%.2f", vf_VlComDesconto));
                    //lb_valorLiquidoResultado.setText(valorLiquido);
                } catch (Exception e) {
                    lb_valorComDescontoResultado.setText("R$0,00");
                }

                try{
                    tb_observacaoItemPedido.setText(cl_ItemPedido.getObservacao());
                }catch (Exception e){
                    tb_observacaoItemPedido.setText("");
                }

                vc_Operacao = "A";
                lb_produtoAdicionado.setVisibility(View.VISIBLE);

                rs_ItemPedido.moveToNext();
            }

        } else {
            vc_Operacao = "I";
        }
    }

}
