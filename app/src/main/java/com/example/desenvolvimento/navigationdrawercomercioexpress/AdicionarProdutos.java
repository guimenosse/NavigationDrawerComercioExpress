package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.Toast;

public class AdicionarProdutos extends AppCompatActivity {

    private ListView lista;
    private Toast toast;

    String selecaoProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        BancoController crud = new BancoController(getBaseContext());
        final Cursor cursor = crud.carregaProdutos();

        String[] nomeCampos = new String[] {CriaBanco.ID, CriaBanco.DESCRICAO};
        int[] idViews = new int[] {R.id.cdProduto, R.id.descricaoProduto};

        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listview_produtos, cursor, nomeCampos, idViews, 0);
        lista = (ListView)findViewById(R.id.listViewProdutos);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codigo;
                cursor.moveToPosition(position);
                codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));

                Intent intent = new Intent(AdicionarProdutos.this, CadastroProdutos.class);
                intent.putExtra("codigo", codigo);
                startActivity(intent);

            }
        });

    }

}
