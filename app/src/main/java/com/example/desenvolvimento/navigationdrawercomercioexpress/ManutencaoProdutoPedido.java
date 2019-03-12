package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class ManutencaoProdutoPedido extends AppCompatActivity {

    TextView lb_descricaoProduto;
    EditText tb_codigoProduto, tb_quantidadeProduto, tb_descontoProduto, tb_valorUnitarioProduto;

    String codigo, numpedido, operacao, alteracao;

    double percdescmaxvendedor;

    double VL_valorBruto;

    private AlertDialog alerta;
    AlertDialog.Builder builder;
    boolean validou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_produto_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        codigo = this.getIntent().getStringExtra("codigo");
        numpedido = this.getIntent().getStringExtra("numpedido");
        alteracao = this.getIntent().getStringExtra("alteracao");

        if(alteracao.equals("N")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lb_descricaoProduto = (TextView)findViewById(R.id.lb_descricaoProdutoManutencao);
        tb_codigoProduto = (EditText)findViewById(R.id.tb_cdProdutoManutencao);
        tb_quantidadeProduto = (EditText)findViewById(R.id.tb_qtdeProdutoManutencao);
        tb_descontoProduto = (EditText)findViewById(R.id.tb_descontoProdutoManutencao);
        tb_valorUnitarioProduto = (EditText)findViewById(R.id.tb_valorUnitarioManutencao);

        Button sc_excluirProduto = (Button)findViewById(R.id.sc_excluirProduto);
        Button sc_cancelar = (Button)findViewById(R.id.sc_cancelar);



        BancoController crud = new BancoController(getBaseContext());
        Cursor cursor;
        if(alteracao.equals("S")){
            cursor = crud.carregaItemPedidoAlteracao(numpedido, codigo);
            //cursor.moveToPosition(Integer.parseInt(codigo));

            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null")) {
                    lb_descricaoProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                }
            }catch (Exception e){
                lb_descricaoProduto.setText("");
            }
            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null")) {
                    tb_codigoProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                }
            }catch (Exception e){
                tb_codigoProduto.setText("0");
            }
            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null")) {
                    tb_quantidadeProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDE)));
                }
            } catch (Exception e) {
                tb_quantidadeProduto.setText("0");
            }

            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null")) {
                    tb_valorUnitarioProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).replace(",", "."));
                    VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
                    String valorUnitario = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                    tb_valorUnitarioProduto.setText(valorUnitario.replace(",", "."));
                }

            }catch (Exception e){
                tb_valorUnitarioProduto.setText("0");
            }

            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null")) {
                    tb_valorUnitarioProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).replace(",", "."));
                    String valorLiquido = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                    tb_valorUnitarioProduto.setText(valorLiquido.replace(",", "."));
                }

            }catch (Exception e){
                tb_valorUnitarioProduto.setText("0");
            }



            try {
                if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null")) {
                    tb_descontoProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                }
            } catch (Exception e) {
                tb_descontoProduto.setText("0");
            }

            sc_excluirProduto.setVisibility(View.VISIBLE);
            sc_cancelar.setVisibility(View.VISIBLE);
            operacao = "A";

            TextView lb_produtoAdicionado = (TextView)findViewById(R.id.lb_produtoAdicionado);
            lb_produtoAdicionado.setVisibility(View.VISIBLE);

            String VA_CdCliente = crud.buscaClientePedido(numpedido);
            String VA_ClassificacaoCliente = crud.buscaClassificacaoCliente(VA_CdCliente);
            String VA_Fidelidade = crud.buscaFidelidadeCliente(VA_CdCliente);

            if(VA_Fidelidade.equals("S")){
                cursor = crud.buscaDescontoFidelidade(tb_codigoProduto.getText().toString());
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)).equals("null")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)));
                    }
                } catch (Exception e) {
                    percdescmaxvendedor = 0;
                }
            }else if(VA_ClassificacaoCliente.equals("N")){
                cursor = crud.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)).equals("null")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)));
                    }
                } catch (Exception e) {
                    percdescmaxvendedor = 0;
                }
            }else{
                cursor = crud.buscaDescontos(tb_codigoProduto.getText().toString());
                if(VA_ClassificacaoCliente.equals("A")){
                    percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)));
                }else if(VA_ClassificacaoCliente.equals("B")){
                    percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)));
                }else if(VA_ClassificacaoCliente.equals("C")){
                    percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)));
                }else if(VA_ClassificacaoCliente.equals("D")){
                    percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)));
                }else if(VA_ClassificacaoCliente.equals("E")){
                    percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)));
                }
            }

            try {
                BancoController crud2 = new BancoController(getBaseContext());

                Cursor cursor_temp;
                cursor_temp = crud2.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());

                String valorUnitario = cursor_temp.getString(cursor_temp.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO));
                if (Double.parseDouble(valorUnitario) == 0) {
                    tb_valorUnitarioProduto.setEnabled(true);
                } else {
                    tb_valorUnitarioProduto.setEnabled(true);
                }
                cursor_temp.close();

            }catch (Exception e){
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, e.getMessage());
            }

        }else {
            cursor = crud.carregaProdutosById(Integer.parseInt(codigo));

            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null")) {
                    lb_descricaoProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                }
            }catch (Exception e){
                lb_descricaoProduto.setText("");
            }
            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null")) {
                    tb_codigoProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                }
            }catch (Exception e){
                tb_codigoProduto.setText("0");
            }
            tb_quantidadeProduto.setText("1");
            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).equals("null")) {
                    tb_valorUnitarioProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));

                    String valorProduto = tb_valorUnitarioProduto.getText().toString();
                    String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                    tb_valorUnitarioProduto.setText(valor.replace(",", "."));
                    VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());


                }
            }catch (Exception e){
                tb_valorUnitarioProduto.setText("0.00");
            }



            if(Double.parseDouble(tb_valorUnitarioProduto.getText().toString()) == 0) {
                tb_valorUnitarioProduto.setEnabled(true);
            }else{
                tb_valorUnitarioProduto.setEnabled(true);
            }

            String VA_CdCliente = crud.buscaClientePedido(numpedido);
            String VA_ClassificacaoCliente = crud.buscaClassificacaoCliente(VA_CdCliente);
            String VA_Fidelidade = crud.buscaFidelidadeCliente(VA_CdCliente);

            if(VA_Fidelidade.equals("S")){
                cursor = crud.buscaDescontoFidelidade(tb_codigoProduto.getText().toString());
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)).equals("null")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)));
                    }
                } catch (Exception e) {
                    percdescmaxvendedor = 0;
                }
            }else if(VA_ClassificacaoCliente.equals("N")){
                cursor = crud.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());
                try {
                    if (!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)).equals("null")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)));
                    }
                } catch (Exception e) {
                    percdescmaxvendedor = 0;
                }
            }else{
                try {
                    String VA_CdProduto = tb_codigoProduto.getText().toString();
                    cursor = crud.buscaDescontos(tb_codigoProduto.getText().toString());
                    VA_CdProduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO));
                    if (VA_ClassificacaoCliente.equals("A")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)));
                    } else if (VA_ClassificacaoCliente.equals("B")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)));
                    } else if (VA_ClassificacaoCliente.equals("C")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)));
                    } else if (VA_ClassificacaoCliente.equals("D")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)));
                    } else if (VA_ClassificacaoCliente.equals("E")) {
                        percdescmaxvendedor = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)));
                    }
                }catch (Exception e){
                    percdescmaxvendedor = 0;
                }
            }

            if(crud.buscaTipoPrecoCliente(VA_CdCliente).equals("A")){
                String vlAtacado = crud.buscaValorAtacado(tb_codigoProduto.getText().toString());

                try {
                    if(!vlAtacado.equals("null")) {
                        tb_valorUnitarioProduto.setText(vlAtacado);

                        String valorProduto = tb_valorUnitarioProduto.getText().toString();
                        String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                        tb_valorUnitarioProduto.setText(valor.replace(",", "."));
                        VL_valorBruto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
                    }
                }catch (Exception e){
                    tb_valorUnitarioProduto.setText("0.00");
                }
            }

            String codigoProduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO));
            String VA_CdProduto = "N";
            try {
                VA_CdProduto = crud.verificaProdutoItemPedido(numpedido, codigoProduto);
            }catch (Exception e){
                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, e.getMessage().toString());
            }
            if(VA_CdProduto.equals("S")){
                cursor.close();
                Cursor cursor_itempedido;
                try {
                    cursor_itempedido = crud.carregaProdutoItemPedido(numpedido, codigoProduto);

                    try {
                        if (!cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null")) {
                            tb_quantidadeProduto.setText(cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                        }
                    } catch (Exception e) {
                        tb_quantidadeProduto.setText("0");
                    }
                    try {
                        if (!cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null")) {
                            tb_descontoProduto.setText(cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                        }
                    } catch (Exception e) {
                        tb_descontoProduto.setText("0");
                    }
                    try {
                        if (!cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null")) {
                            tb_valorUnitarioProduto.setText(cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));

                            String valorProduto = tb_valorUnitarioProduto.getText().toString();
                            String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                            tb_valorUnitarioProduto.setText(valor.replace(",", "."));
                        }
                    } catch (Exception e) {
                        tb_valorUnitarioProduto.setText("0.00");
                    }

                    try {
                        if(!cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null")) {
                            tb_valorUnitarioProduto.setText(cursor_itempedido.getString(cursor_itempedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).replace(",", "."));
                            String valorLiquido = String.format("%.2f", Double.parseDouble(tb_valorUnitarioProduto.getText().toString()));
                            tb_valorUnitarioProduto.setText(valorLiquido.replace(",", "."));
                        }

                    }catch (Exception e){
                        tb_valorUnitarioProduto.setText("0");
                    }

                    sc_excluirProduto.setVisibility(View.VISIBLE);
                    sc_cancelar.setVisibility(View.VISIBLE);
                    operacao = "A";

                    TextView lb_produtoAdicionado = (TextView)findViewById(R.id.lb_produtoAdicionado);
                    lb_produtoAdicionado.setVisibility(View.VISIBLE);

                }catch (Exception e){
                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, e.getMessage().toString());
                }
            }else{
                sc_excluirProduto.setVisibility(View.INVISIBLE);
                sc_cancelar.setVisibility(View.INVISIBLE);
                operacao = "I";
            }
        }

        builder = new AlertDialog.Builder(this);


        sc_excluirProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //define o titulo
                builder.setTitle("Excluir Produto");
                //define a mensagem
                //builder.setMessage("Deseja mesmo excluir o produto " + lb_descricaoProduto.getText().toString() + "?");
                builder.setMessage("Deseja mesmo excluir o produto ?");

                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                        try {
                            tb_codigoProduto = (EditText) findViewById(R.id.tb_cdProdutoManutencao);
                            BancoController crud = new BancoController(getBaseContext());
                            crud.deletaItemPedido(numpedido, tb_codigoProduto.getText().toString());
                            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto excluido com sucesso!");
                            Intent intent = new Intent();
                            intent.putExtra("numpedido", numpedido);
                            if (alteracao.equals("S")) {
                                setResult(2, intent);
                            } else {
                                setResult(1, intent);
                            }
                            //intent.putExtra("codigo", codigo);
                            //startActivity(intent);
                            finish();
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
        });

        sc_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("numpedido", numpedido);
                if (alteracao.equals("S")) {
                    setResult(2, intent);
                } else {
                    setResult(1, intent);
                }
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();


            }
        });

        tb_valorUnitarioProduto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    try {
                        double VA_VlUnitarioNovo = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().trim());
                        if (VA_VlUnitarioNovo - VL_valorBruto < 0) {

                            try {
                                BancoController crud = new BancoController(getBaseContext());
                                Cursor cursor = crud.carregaProdutosById(Integer.parseInt(codigo));


                                double valorBruto = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", "."));
                                double valorLiquido = valorBruto - VA_VlUnitarioNovo;


                                double porcentagem = ((valorBruto - valorLiquido) / valorBruto) * 100;
                                porcentagem = 100 - porcentagem;
                                if (porcentagem > percdescmaxvendedor) {
                                    MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                                    tb_valorUnitarioProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));
                                } else {
                                    tb_descontoProduto.setText(String.format("%.5f", 100 - porcentagem).replace(",", "."));
                                }


                            } catch (Exception e) {

                            }


                            double valorLiquido = VL_valorBruto - Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
                            tb_valorUnitarioProduto.setText(String.format("%.2f", VL_valorBruto).replace(".", "").replace(",", "."));
                            double porcentagem = ((VL_valorBruto - valorLiquido) / VL_valorBruto) * 100;
                            tb_descontoProduto.setText(String.format("%.5f", 100 - porcentagem).replace(",", "."));
                            tb_descontoProduto.setEnabled(true);
                        } else if (VA_VlUnitarioNovo - VL_valorBruto > 0) {
                            tb_descontoProduto.setEnabled(false);
                            tb_descontoProduto.setText("0.00000");
                        }else {
                            tb_descontoProduto.setEnabled(true);
                            //tb_descontoProduto.setText("0");
                        }
                        VA_VlUnitarioNovo = VL_valorBruto; //Realizado esse tratamento para que o sistema mantenha o valor incial do produto

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    });

        tb_valorUnitarioProduto.addTextChangedListener(new TextWatcher() {
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

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manutencaoproduto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {

            // Id correspondente ao botão Up/Home da actionbar
            case android.R.id.home:

                Intent intent2 = new Intent();
                intent2.putExtra("numpedido", numpedido);
                setResult(1, intent2);
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();

               //NavUtils.navigateUpFromSameTask(this);
               return true;
            case R.id.action_settings:
                if(FU_ConsisteItem()) {
                    if(operacao.equals("I")) {
                        if (FU_IncluirItemPedido()) {
                            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto incluido com sucesso!");
                            Intent intent = new Intent();
                            intent.putExtra("numpedido", numpedido);
                            setResult(1, intent);
                            //intent.putExtra("codigo", codigo);
                            //startActivity(intent);
                            finish();
                            //Intent intent = new Intent(ManutencaoProdutoPedido.this, AdicionarProdutosCustomizada.class);
                            //intent.putExtra("numpedido", numpedido);
                            //startActivity(intent);
                        }
                    }else{

                        if(FU_AlteraItemPedido()){
                            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Produto alterado com sucesso!");
                            Intent intent = new Intent();
                            intent.putExtra("numpedido", numpedido);
                            setResult(1, intent);
                            //intent.putExtra("codigo", codigo);
                            //startActivity(intent);
                            finish();
                        }
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        //Seu código aqui dentro
        //MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Foi clicado para voltar");
        Intent intent = new Intent();
        intent.putExtra("numpedido", numpedido);
        setResult(0, intent);
        //intent.putExtra("codigo", codigo);
        //startActivity(intent);
        finish();
    }

    public boolean FU_IncluirItemPedido(){

        BancoController crud = new BancoController(getBaseContext());

        try {
            lb_descricaoProduto = (TextView)findViewById(R.id.lb_descricaoProdutoManutencao);
            tb_codigoProduto = (EditText) findViewById(R.id.tb_cdProdutoManutencao);
            tb_quantidadeProduto = (EditText) findViewById(R.id.tb_qtdeProdutoManutencao);
            tb_descontoProduto = (EditText) findViewById(R.id.tb_descontoProdutoManutencao);
            tb_valorUnitarioProduto = (EditText) findViewById(R.id.tb_valorUnitarioManutencao);

            Cursor cursorVlUnitario = crud.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());
            double vlunitariobruto = Double.parseDouble(cursorVlUnitario.getString(cursorVlUnitario.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));

            double VA_vlUnitarioProduto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));
            double vltotal = Double.parseDouble(tb_quantidadeProduto.getText().toString()) * Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
            double vldesconto = 0;

            String vlmaxdescpermitido = "0";
            String valor = String.format("%.2f", vltotal);
            String valorTotalDesconto = String.format("%.2f", vldesconto);

            String valorPercDesconto = "0";
            if(VA_vlUnitarioProduto < VL_valorBruto) {
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

            }else {

                if (percdescmaxvendedor > 0) {
                    double VA_vlDesconto = percdescmaxvendedor;
                    double porcentagem = VA_vlDesconto / 100;
                    double resultado = vltotal * porcentagem;
                    vlmaxdescpermitido = String.format("%.2f", resultado);
                }

                if (!tb_descontoProduto.getText().toString().trim().equals("")) {
                    double VA_vlDesconto = Double.parseDouble(tb_descontoProduto.getText().toString());
                    double porcentagem = VA_vlDesconto / 100;
                    vldesconto = vltotal * porcentagem;
                    double resultadoTotal = vltotal - vldesconto;
                    vltotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
                    valor = String.format("%.2f", vltotal);
                }

                if (!tb_descontoProduto.getText().toString().trim().equals("")) {
                    valorPercDesconto = tb_descontoProduto.getText().toString();
                }

                valorTotalDesconto = String.format("%.2f", vldesconto);
            }

            String valorunitarioteste = String.format("%.2f", VA_vlUnitarioProduto);
             crud.inserirItemPedido(numpedido, tb_codigoProduto.getText().toString(), lb_descricaoProduto.getText().toString(), tb_quantidadeProduto.getText().toString(), valorPercDesconto, valorTotalDesconto.replace(",", "."), vlmaxdescpermitido.replace(",", "."), valorunitarioteste, tb_valorUnitarioProduto.getText().toString(), valor);

            return true;
        }catch (Exception e){
            return false;
        }

    }

    public boolean FU_AlteraItemPedido(){
        BancoController crud = new BancoController(getBaseContext());
        try {
            lb_descricaoProduto = (TextView)findViewById(R.id.lb_descricaoProdutoManutencao);
            tb_codigoProduto = (EditText) findViewById(R.id.tb_cdProdutoManutencao);
            tb_quantidadeProduto = (EditText) findViewById(R.id.tb_qtdeProdutoManutencao);
            tb_descontoProduto = (EditText) findViewById(R.id.tb_descontoProdutoManutencao);
            tb_valorUnitarioProduto = (EditText) findViewById(R.id.tb_valorUnitarioManutencao);

            double VA_vlUnitarioProduto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());

            Cursor cursorVlUnitario = crud.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());
            double vlunitariobruto = Double.parseDouble(cursorVlUnitario.getString(cursorVlUnitario.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));
            double vltotal = Double.parseDouble(tb_quantidadeProduto.getText().toString()) * Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
            double vldesconto = 0;
            double percdesconto = 0;

            if(vlunitariobruto - VA_vlUnitarioProduto > 0 || vlunitariobruto - VA_vlUnitarioProduto == 0){
                if (!tb_descontoProduto.getText().toString().trim().equals("") && !tb_descontoProduto.getText().toString().trim().equals("0")) {

                    double VA_vlDesconto = Double.parseDouble(tb_descontoProduto.getText().toString());
                    double porcentagem = VA_vlDesconto / 100;
                    vldesconto = (VL_valorBruto * Double.parseDouble(tb_quantidadeProduto.getText().toString())) * porcentagem;
                    double resultadoTotal = (VL_valorBruto * Double.parseDouble(tb_quantidadeProduto.getText().toString())) - vldesconto;
                    vltotal = Double.valueOf(String.format(Locale.US, "%.2f", resultadoTotal));
                    percdesconto = Double.parseDouble(tb_descontoProduto.getText().toString());
                }
            }else{

            }

            String valor = String.format("%.2f", vltotal);
            String valorTotalDesconto = String.format("%.2f", vldesconto);
            String valorPercDesconto = String.format("%.5f", percdesconto);

            String valorunitarioteste = String.format("%.2f", VA_vlUnitarioProduto);
            crud.alteraItemPedido(numpedido, tb_codigoProduto.getText().toString(), tb_quantidadeProduto.getText().toString(), valorPercDesconto.replace(",", "."), valorTotalDesconto.replace(",", "."), valorunitarioteste, valor, valorunitarioteste);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean FU_ConsisteItem() {
        tb_quantidadeProduto = (EditText) findViewById(R.id.tb_qtdeProdutoManutencao);
        tb_descontoProduto = (EditText) findViewById(R.id.tb_descontoProdutoManutencao);
        tb_valorUnitarioProduto = (EditText) findViewById(R.id.tb_valorUnitarioManutencao);
        tb_codigoProduto = (EditText) findViewById(R.id.tb_cdProdutoManutencao);

        double VA_quantidadeProduto;
        double VA_descontoProduto, VA_vlUnitarioProduto;

        if (tb_quantidadeProduto.getText().toString().trim().equals("")) {
            VA_quantidadeProduto = 0;
        } else {
            VA_quantidadeProduto = Double.parseDouble(tb_quantidadeProduto.getText().toString());
        }
        if (tb_descontoProduto.getText().toString().trim().equals("")) {
            VA_descontoProduto = 0;
        } else {
            VA_descontoProduto = Double.parseDouble(tb_descontoProduto.getText().toString());
        }
        if (tb_valorUnitarioProduto.getText().toString().trim().equals("")) {
            VA_vlUnitarioProduto = 0;
        } else {
            VA_vlUnitarioProduto = Double.parseDouble(tb_valorUnitarioProduto.getText().toString());
        }

        if (VA_quantidadeProduto == 0) {
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com a quantidade igual a 0!");
            return false;
        }

        if (tb_valorUnitarioProduto.getText().toString().trim().equals("")) {
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o valor unitário em branco!");
            return false;
        }

        if (VA_vlUnitarioProduto == 0) {
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o valor unitário igual a 0!");
            return false;
        }

        try {
            BancoController crud = new BancoController(getBaseContext());
            Cursor cursorVlUnitario = crud.carregaDadosProdutosByCdProduto(tb_codigoProduto.getText().toString());

            VL_valorBruto = Double.parseDouble(cursorVlUnitario.getString(cursorVlUnitario.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", "."));

            if (VA_vlUnitarioProduto < VL_valorBruto) {

                try {
                    double VA_VlUnitarioNovo = Double.parseDouble(tb_valorUnitarioProduto.getText().toString().trim());
                    if (VA_VlUnitarioNovo - VL_valorBruto < 0) {

                        try {
                            Cursor cursor = crud.carregaProdutosById(Integer.parseInt(codigo));

                            double valorBruto = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", "."));
                            double valorLiquido = valorBruto - VA_VlUnitarioNovo;

                            double porcentagem = ((valorBruto - valorLiquido) / valorBruto) * 100;
                            porcentagem = 100 - porcentagem;

                            if (porcentagem > percdescmaxvendedor) {
                                MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
                                tb_valorUnitarioProduto.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));
                                return false;
                            } else {
                                tb_descontoProduto.setText(String.format("%.5f", 100 - porcentagem).replace(",", "."));
                            }

                        } catch (Exception e) {

                        }

                    } else {
                        tb_descontoProduto.setText("");
                    }

                    double valorLiquido = VL_valorBruto - Double.parseDouble(tb_valorUnitarioProduto.getText().toString().replace(",", "."));

                    double porcentagem = ((VL_valorBruto - valorLiquido) / VL_valorBruto) * 100;
                    tb_descontoProduto.setText(String.format("%.5f", 100 - porcentagem).replace(",", "."));
                    VA_descontoProduto = 100 - porcentagem;
                    tb_valorUnitarioProduto.setText(String.format("%.2f", VL_valorBruto).replace(".", "").replace(",", "."));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(VA_vlUnitarioProduto > VL_valorBruto){
                tb_descontoProduto.setText("0");
                VA_descontoProduto = 0;
            }else{
                //tb_descontoProduto.setText("0");
            }
        }catch (Exception e){
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, e.getMessage());
        }

        /*if (VA_descontoProduto > 99) {
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Não é permitido incluir um produto com o desconto superior a 99%!");
            return false;
        }*/

        if(VA_descontoProduto > percdescmaxvendedor){
            MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Desconto informado é maior que o permitido!");
            return false;
        }
        return true;
    }


}
