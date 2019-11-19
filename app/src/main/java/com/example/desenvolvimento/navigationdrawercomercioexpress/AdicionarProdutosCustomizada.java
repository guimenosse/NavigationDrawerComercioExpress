package com.example.desenvolvimento.navigationdrawercomercioexpress;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdicionarProdutosCustomizada extends AppCompatActivity {

    int VA_ContProdutos;

    String numpedido;

    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produtos_customizada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numpedido = this.getIntent().getStringExtra("numpedido");

        String VA_ValorProduto = "";
        String VA_ValorAtacado = "";
        VA_ContProdutos = 0;
        BancoController crud = new BancoController(getBaseContext());
        final Cursor cursor = crud.carregaProdutosCompleto();

        List<String> codigo = new ArrayList<>();
        List<String> descricao = new ArrayList<>();
        List<String> itensRestantes = new ArrayList<>();
        List<String> valorProduto = new ArrayList<>();
        List<String> valorAtacado = new ArrayList<>();

        if (cursor != null) {
            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
            descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
            itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
            VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
            VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

            valorProduto.add(VA_ValorProduto);
            valorAtacado.add(VA_ValorAtacado);
            VA_ContProdutos = VA_ContProdutos + 1;
            while(cursor.moveToNext()) {
                codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
                descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                valorProduto.add(VA_ValorProduto);
                valorAtacado.add(VA_ValorAtacado);
                VA_ContProdutos = VA_ContProdutos + 1;
            }
        }

        List<Integer> icones = new ArrayList<>();


        for(int i = 0; i < VA_ContProdutos; i++) {
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

        lista = (ListView) findViewById(R.id.lista);

        ListaProdutosCustomizadaAdapter adapter = new ListaProdutosCustomizadaAdapter(this, icones, codigo, descricao, itensRestantes, valorProduto, valorAtacado);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codigo;
                try {
                    cursor.moveToPosition(position);
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                    Intent secondActivity;
                    Intent intent = new Intent(AdicionarProdutosCustomizada.this, ManutencaoProdutoPedido.class);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("numpedido", numpedido);
                    intent.putExtra("alteracao", "N");
                    //startActivity(intent);
                    startActivityForResult(intent, 1);
                }catch (Exception e){
                    cursor.moveToPosition(position + 1);
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                    Intent secondActivity;
                    Intent intent = new Intent(AdicionarProdutosCustomizada.this, ManutencaoProdutoPedido.class);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("numpedido", numpedido);
                    intent.putExtra("alteracao", "N");
                    //startActivity(intent);
                    startActivityForResult(intent, 1);
                }
            }
        });



        final EditText tb_buscarprodutoPedido = (EditText)findViewById(R.id.tb_buscarprodutoPedido);

        tb_buscarprodutoPedido.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BancoController crud = new BancoController(getBaseContext());
                final Cursor cursor = crud.carregaProdutosDescricaoPedido(tb_buscarprodutoPedido.getText().toString());

                String VA_ValorProduto = "";
                String VA_ValorAtacado = "";
                VA_ContProdutos = 0;

                List<String> codigo = new ArrayList<>();
                List<String> descricao = new ArrayList<>();
                List<String> itensRestantes = new ArrayList<>();
                List<String> valorProduto = new ArrayList<>();
                List<String> valorAtacado = new ArrayList<>();

                if (cursor != null) {
                    try {
                        codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
                        descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                        itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                        VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                        VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                        valorProduto.add(VA_ValorProduto);
                        valorAtacado.add(VA_ValorAtacado);
                        VA_ContProdutos = VA_ContProdutos + 1;
                        while (cursor.moveToNext()) {
                            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
                            descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                            itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                            VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                            VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                            valorProduto.add(VA_ValorProduto);
                            valorAtacado.add(VA_ValorAtacado);
                            VA_ContProdutos = VA_ContProdutos + 1;
                        }
                    }catch (Exception e){

                    }
                }

                List<Integer> icones = new ArrayList<>();


                for(int i = 0; i < VA_ContProdutos; i++) {
                    icones.add(R.drawable.sem_foto);
                }
                lista = (ListView) findViewById(R.id.lista);

                ListaProdutosCustomizadaAdapter adapter = new ListaProdutosCustomizadaAdapter(getBaseContext(), icones, codigo, descricao, itensRestantes, valorProduto, valorAtacado);
                lista.setAdapter(adapter);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String codigo;
                        try {
                            ListAdapter adapter2 = lista.getAdapter();
                            int lenght = adapter2.getCount();
                            if(position == 0){
                                cursor.moveToPosition(position);
                            }else if(position < lenght - 1) {
                                cursor.moveToPosition(position);
                            }else{
                                cursor.moveToPosition(position + 1);
                            }
                            //cursor.moveToPosition(position + 1);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                            Intent secondActivity;
                            Intent intent = new Intent(AdicionarProdutosCustomizada.this, ManutencaoProdutoPedido.class);
                            intent.putExtra("codigo", codigo);
                            intent.putExtra("numpedido", numpedido);
                            intent.putExtra("alteracao", "N");
                            //startActivity(intent);
                            startActivityForResult(intent, 1);
                        }catch (Exception e){
                            cursor.moveToPosition(position);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                            Intent secondActivity;
                            Intent intent = new Intent(AdicionarProdutosCustomizada.this, ManutencaoProdutoPedido.class);
                            intent.putExtra("codigo", codigo);
                            intent.putExtra("numpedido", numpedido);
                            intent.putExtra("alteracao", "N");
                            //startActivity(intent);
                            startActivityForResult(intent, 1);
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adicionarproduto, menu);
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

                NavUtils.navigateUpFromSameTask(this);


                return true;

            case R.id.action_settings:
                Intent intent = new Intent();
                intent.putExtra("numpedido", numpedido);
                setResult(2, intent);
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        //Seu código aqui dentro
        MensagemUtil.addMsg(AdicionarProdutosCustomizada.this, "Para voltar a tela anterior clique em concluir.");
    }


}
