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
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class AdicionarProdutosCustomizada extends AppCompatActivity {

    int VA_ContProdutos;

    String numpedido;

    ListView lista;

    FloatingActionButton fab_SalvarProdutosPedido;

    //sv_ProdutosPedidos

    MaterialSearchView sv_ProdutosPedidos;


    MenuItem me_BuscarProduto, me_Concluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produtos_customizada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sv_ProdutosPedidos = (MaterialSearchView) findViewById(R.id.sv_ProdutosPedidos);
        sv_ProdutosPedidos.setVoiceSearch(true); //or false

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_SalvarProdutosPedido = (FloatingActionButton) findViewById(R.id.fab_SalvarProdutosPedido);
        fab_SalvarProdutosPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //suSalvarCliente();
                Intent intent = new Intent();
                intent.putExtra("numpedido", numpedido);
                setResult(2, intent);
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();
            }
        });

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
            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
            descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
            itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
            VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
            VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

            valorProduto.add(VA_ValorProduto);
            valorAtacado.add(VA_ValorAtacado);
            VA_ContProdutos = VA_ContProdutos + 1;
            while(cursor.moveToNext()) {
                codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
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

        final TextView lb_TituloProdutos = (TextView) findViewById(R.id.lb_TituloProdutosPedidos);

                sv_ProdutosPedidos.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String cdProdutoDescricao) {

                String vf_CdProdutoDescricao = cdProdutoDescricao;

                BancoController crud = new BancoController(getBaseContext());
                final Cursor cursor = crud.carregaProdutosDescricaoPedido(vf_CdProdutoDescricao);

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
                        codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                        descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                        itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                        VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                        VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                        valorProduto.add(VA_ValorProduto);
                        valorAtacado.add(VA_ValorAtacado);
                        VA_ContProdutos = VA_ContProdutos + 1;
                        while (cursor.moveToNext()) {
                            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
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

                return false;
            }
        });

        sv_ProdutosPedidos.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                me_BuscarProduto.setVisible(false);
                lb_TituloProdutos.setWidth(0);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                me_BuscarProduto.setVisible(true);
                lb_TituloProdutos.setWidth(550);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adicionarproduto, menu);
        MenuItem item = menu.findItem(R.id.buscar_produto);

        sv_ProdutosPedidos.setMenuItem(item);

        me_BuscarProduto = menu.findItem(R.id.buscar_produto);
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
