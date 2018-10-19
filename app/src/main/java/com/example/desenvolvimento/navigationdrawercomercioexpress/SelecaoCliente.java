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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SelecaoCliente extends AppCompatActivity {

    private ListView lista;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BancoController crud = new BancoController(getBaseContext());
        final Cursor cursor = crud.carregaClientes();

        String[] nomeCampos = new String[] {CriaBanco.ID, CriaBanco.RZSOCIAL};
        int[] idViews = new int[] {R.id.cdCliente, R.id.rzsociallist};

        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewteste, cursor, nomeCampos, idViews, 0);
        lista = (ListView)findViewById(R.id.listView);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codigo;
                cursor.moveToPosition(position);
                codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                Intent intent = new Intent();
                setResult(Integer.parseInt(codigo), intent);
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();
            }
        });


        final EditText tb_buscarcliente = (EditText)findViewById(R.id.tb_buscarclienteSelecaoClientes);

        tb_buscarcliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BancoController crud = new BancoController(getBaseContext());
                final Cursor cursor = crud.carregaClientesNome(tb_buscarcliente.getText().toString());

                String[] nomeCampos = new String[]{CriaBanco.RZSOCIAL};
                int[] idViews = new int[]{R.id.rzsociallist};

                SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.listviewteste, cursor, nomeCampos, idViews, 0);
                lista = (ListView) findViewById(R.id.listView);
                lista.setAdapter(adaptador);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String codigo;
                        cursor.moveToPosition(position);
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                        Intent intent = new Intent();
                        setResult(Integer.parseInt(codigo), intent);
                        //intent.putExtra("codigo", codigo);
                        //startActivity(intent);
                        finish();
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
        getMenuInflater().inflate(R.menu.selecaocliente, menu);
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

            case R.id.action_settings:
                Intent intent = new Intent();
                setResult(0, intent);
                //intent.putExtra("codigo", codigo);
                //startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
