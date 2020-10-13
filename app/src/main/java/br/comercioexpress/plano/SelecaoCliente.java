package br.comercioexpress.plano;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import models.CriaBanco;

public class SelecaoCliente extends AppCompatActivity {

    private ListView lista;
    private Toast toast;

    MaterialSearchView sv_ClientesPedido;
    MenuItem me_BuscarCliente, me_Cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sv_ClientesPedido = (MaterialSearchView) findViewById(R.id.sv_ClientesPedidos);
        sv_ClientesPedido.setVoiceSearch(true); //or false

        carregaListaClientes("");

        /*BancoController crud = new BancoController(getBaseContext());
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
                intent.putExtra("codigo", codigo);
                setResult(1, intent);
                //startActivity(intent);
                finish();
            }
        });*/


        final TextView lb_TituloClientesPedidos = (TextView) findViewById(R.id.lb_TituloClientesPedidos);

        sv_ClientesPedido.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nomeRazaoSocial) {

                carregaListaClientes(nomeRazaoSocial);

                return false;
            }
        });

        sv_ClientesPedido.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                me_BuscarCliente.setVisible(false);
                lb_TituloClientesPedidos.setWidth(0);

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                me_BuscarCliente.setVisible(true);
                lb_TituloClientesPedidos.setWidth(550);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selecaocliente, menu);
        MenuItem item = menu.findItem(R.id.buscar_clientepedido);

        sv_ClientesPedido.setMenuItem(item);

        me_BuscarCliente = menu.findItem(R.id.buscar_clientepedido);
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

    public void carregaListaClientes(String parametroNomeRazaoSocial){
        BancoController crud = new BancoController(getBaseContext());
        Cursor cursorCliente = crud.carregaClientes();
        if(parametroNomeRazaoSocial.trim() != ""){
            cursorCliente = crud.carregaClientesNome(parametroNomeRazaoSocial);
        }
        final Cursor cursor = cursorCliente;

        final List<String> codigo = new ArrayList<>();
        List<String> nomeRazaoSocial = new ArrayList<>();
        List<String> nomeFantasia = new ArrayList<>();
        List<String> telefone = new ArrayList<>();
        List<String> email = new ArrayList<>();

        if (cursor != null) {
            while(!cursor.isAfterLast()) {

                String codigoCliente = "";
                try{
                    int indexPontoCdCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)).indexOf(".");
                    codigoCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)).substring(0, indexPontoCdCliente);
                    //int codigoCliente = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE)));
                }catch (Exception e){
                    codigoCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDCLIENTE));
                }


                codigo.add(codigoCliente);
                nomeRazaoSocial.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
                nomeFantasia.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NMFANTASIA)));
                telefone.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONE)));
                email.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EMAIL)));

                cursor.moveToNext();
            }
        }

        lista = (ListView)findViewById(R.id.listView);
        ListaClientesCustomizadaAdapter adapter = new ListaClientesCustomizadaAdapter(this, codigo, nomeRazaoSocial, nomeFantasia, telefone, email);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String codigo;
                cursor.moveToPosition(position);
                codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                Intent intent = new Intent();
                intent.putExtra("codigo", codigo);
                setResult(1, intent);
                //startActivity(intent);
                finish();
            }
        });
    }

}
