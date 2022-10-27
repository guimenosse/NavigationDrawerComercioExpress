package br.comercioexpress.plano;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import models.CriaBanco;

public class CadastroProdutos extends AppCompatActivity {

    String codigo;

    TextView lb_descricao;
    TextView lb_cdProduto;
    TextView lb_estoqueAtual;
    TextView lb_valorUnitario;
    TextView lb_valorAtacado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lb_descricao = (TextView)findViewById(R.id.lb_descricaoProduto);
        lb_cdProduto = (TextView)findViewById(R.id.lb_cdProduto);
        lb_estoqueAtual = (TextView)findViewById(R.id.lb_estoqueAtual);
        lb_valorUnitario = (TextView)findViewById(R.id.lb_valorUnitario);
        lb_valorAtacado = (TextView)findViewById(R.id.lb_valoratacado);

        codigo = this.getIntent().getStringExtra("codigo");

        BancoController crud = new BancoController(getBaseContext());
        Cursor cursor = crud.carregaProdutosById(Integer.parseInt(codigo));

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null")) {
                lb_descricao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
            }
        }catch (Exception e){
            lb_descricao.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO)).equals("null")) {
                String descricaoCompleta = lb_descricao.getText().toString() + " - " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO));
                lb_descricao.setText(descricaoCompleta);
            }
        }catch (Exception e){
            //lb_descricao.setText("");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null")) {
                lb_cdProduto.setText("Código: " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
            }
        }catch (Exception e){
            lb_cdProduto.setText("Código: 0");
        }
        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)).equals("null")) {
                lb_estoqueAtual.setText("Estoque Atual: " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
            }
        }catch (Exception e){
            lb_estoqueAtual.setText("Estoque Atual: 0");
        }
        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).equals("null")) {
                String valorProduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO));
                String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                lb_valorUnitario.setText("Valor Unitário: R$" + valor.replace(",", "."));
            }
        }catch (Exception e){
            lb_valorUnitario.setText("Valor Unitário: R$0.00");
        }

        try {
            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).equals("null")) {
                String valorProduto = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO));
                String valor = String.format("%.2f", Double.parseDouble(valorProduto));
                lb_valorAtacado.setText("Valor Atacado: R$" + valor.replace(",", "."));
            }
        }catch (Exception e){
            lb_valorAtacado.setText("Valor Atacado: R$0.00");
        }

        ImageView imagemProduto = (ImageView)findViewById(R.id.imageViewProduto);
        imagemProduto.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed()
    {
        //Seu código aqui dentro
        //MensagemUtil.addMsg(ManutencaoProdutoPedido.this, "Foi clicado para voltar");
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
