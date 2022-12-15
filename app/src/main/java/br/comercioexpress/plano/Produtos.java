package br.comercioexpress.plano;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import classes.CL_Configuracao;
import classes.CL_Filial;
import classes.CL_Usuario;
import controllers.CTL_Configuracao;
import controllers.CTL_Filial;
import controllers.CTL_Usuario;
import models.CriaBanco;
import models.MDL_Usuario;

public class Produtos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CL_Filial cl_Filial;
    CTL_Filial ctl_Filial;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    CL_Configuracao cl_Configuracao;
    CTL_Configuracao ctl_Configuracao;

    int VA_ContProdutos;

    String numpedido, selecaoprodutos;

    ListView lista;


    MaterialSearchView sv_Produtos;

    MenuItem me_BuscarProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cl_Configuracao = new CL_Configuracao();
        ctl_Configuracao = new CTL_Configuracao(getApplicationContext(), cl_Configuracao);
        ctl_Configuracao.fuCarregarFgControlaEstoquePedido();

        sv_Produtos = (MaterialSearchView) findViewById(R.id.sv_Produtos);
        sv_Produtos.setVoiceSearch(true); //or false

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

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{
            selecaoprodutos = this.getIntent().getStringExtra("selecaoProdutos");
            if(selecaoprodutos.equals("null")){
                selecaoprodutos = "N";
            }
        }catch (Exception e){
            selecaoprodutos = "N";
        }

        if(selecaoprodutos.equals("S")) {
            numpedido = this.getIntent().getStringExtra("numpedido");
        }

        String VA_ValorProduto = "";
        String VA_ValorAtacado = "";
        VA_ContProdutos = 0;
        BancoController crud = new BancoController(getBaseContext());
        final Cursor cursor = crud.carregaProdutosCompleto();

        final List<String> codigo = new ArrayList<>();
        List<String> descricao = new ArrayList<>();
        List<String> itensRestantes = new ArrayList<>();
        List<String> valorProduto = new ArrayList<>();
        List<String> valorAtacado = new ArrayList<>();

        if (cursor != null) {
            while(!cursor.isAfterLast()) {
                codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                String descricaoCompleta = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO));
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO)).trim().equals((""))){
                    descricaoCompleta += " - " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO));
                }
                descricao.add(descricaoCompleta);

                if(cl_Configuracao.getFgControlaEstoquePedido().equals("S")){
                    itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)) + " / Quantidade disponível: " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)));
                }else{
                    itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                }
                VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)) != null) {
                    VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));
                }
                else {
                    VA_ValorAtacado = "0";
                }

                valorProduto.add(VA_ValorProduto);
                valorAtacado.add(VA_ValorAtacado);
                VA_ContProdutos = VA_ContProdutos + 1;

                cursor.moveToNext();
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
                    if (selecaoprodutos.equals("S")) {
                        Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);

                        intent.putExtra("numpedido", numpedido);
                        intent.putExtra("alteracao", "N");
                        startActivityForResult(intent, 1);
                    }else{
                        Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                        intent.putExtra("codigo", codigo);
                        startActivityForResult(intent, 1);
                    }
                    //startActivity(intent);
                    //startActivityForResult(intent, 1);
                } catch (Exception e) {
                    cursor.moveToPosition(position + 1);
                    codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                    Intent secondActivity;
                    if (selecaoprodutos.equals("S")) {
                        Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                        intent.putExtra("codigo", codigo);
                        intent.putExtra("numpedido", numpedido);
                        intent.putExtra("alteracao", "N");
                        //startActivity(intent);
                        startActivityForResult(intent, 1);
                    }else{
                        Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                        intent.putExtra("codigo", codigo);
                        startActivityForResult(intent, 1);
                    }
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
                        while (!cursor.isAfterLast()) {
                            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                            //descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                            String descricaoCompleta = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO));
                            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO)).trim().equals((""))){
                                descricaoCompleta += " - " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO));
                            }
                            descricao.add(descricaoCompleta);
                            if(cl_Configuracao.getFgControlaEstoquePedido().equals("S")){
                                itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)) + " / Quantidade disponível: " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)));
                            }else{
                                itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                            }
                            VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                            VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                            valorProduto.add(VA_ValorProduto);
                            valorAtacado.add(VA_ValorAtacado);
                            VA_ContProdutos = VA_ContProdutos + 1;

                            cursor.moveToNext();
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
                            if (selecaoprodutos.equals("S")) {
                                Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                //startActivity(intent);
                                startActivityForResult(intent, 1);
                            }else{
                                Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                                intent.putExtra("codigo", codigo);

                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                startActivityForResult(intent, 1);
                            }
                        }catch (Exception e){
                            cursor.moveToPosition(position);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                            Intent secondActivity;
                            if (selecaoprodutos.equals("S")) {
                                Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                //startActivity(intent);
                                startActivityForResult(intent, 1);
                            }else{
                                Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                                intent.putExtra("codigo", codigo);

                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                startActivityForResult(intent, 1);
                            }
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


        final TextView lb_TituloProdutos = (TextView) findViewById(R.id.lb_TituloProdutos);

        sv_Produtos.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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
                        while (!cursor.isAfterLast()) {
                            codigo.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                            //descricao.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                            String descricaoCompleta = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DESCRICAO));
                            if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO)).trim().equals((""))){
                                descricaoCompleta += " - " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTODESCRICAO));
                            }
                            descricao.add(descricaoCompleta);
                            if(cl_Configuracao.getFgControlaEstoquePedido().equals("S")){
                                itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)) + " / Quantidade disponível: " + cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)));
                            }else{
                                itensRestantes.add(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                            }
                            VA_ValorProduto = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).replace(",", ".")));
                            VA_ValorAtacado = String.format("%.2f", Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).replace(",", ".")));

                            valorProduto.add(VA_ValorProduto);
                            valorAtacado.add(VA_ValorAtacado);
                            VA_ContProdutos = VA_ContProdutos + 1;

                            cursor.moveToNext();
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
                            if (selecaoprodutos.equals("S")) {
                                Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                //startActivity(intent);
                                startActivityForResult(intent, 1);
                            }else{
                                Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                                intent.putExtra("codigo", codigo);

                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                startActivityForResult(intent, 1);
                            }
                        }catch (Exception e){
                            cursor.moveToPosition(position);
                            codigo = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID));
                            Intent secondActivity;
                            if (selecaoprodutos.equals("S")) {
                                Intent intent = new Intent(Produtos.this, ManutencaoProdutoPedido.class);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                //startActivity(intent);
                                startActivityForResult(intent, 1);
                            }else{
                                Intent intent = new Intent(Produtos.this, CadastroProdutos.class);
                                intent.putExtra("codigo", codigo);

                                intent.putExtra("numpedido", numpedido);
                                intent.putExtra("alteracao", "N");
                                startActivityForResult(intent, 1);
                            }
                        }
                    }
                });

                return false;
            }
        });

        sv_Produtos.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.produtos, menu);
        MenuItem item = menu.findItem(R.id.buscar_produto);

        sv_Produtos.setMenuItem(item);

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
        if (id == R.id.action_settings) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, Opcoes.class);
            startActivity(secondActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, HomeActivity.class);
            startActivity(secondActivity);
            // Handle the camera action
        } else if (id == R.id.nav_pedidos) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, Pedidos.class);
            startActivity(secondActivity);


        } else if (id == R.id.nav_produtos) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, Produtos.class);
            secondActivity.putExtra("selecaoProdutos", "N");
            startActivity(secondActivity);

        } else if (id == R.id.nav_opcoes) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, Opcoes.class);
            startActivity(secondActivity);

        } else if (id == R.id.nav_visaogeral) {
            Intent secondActivity;
            secondActivity = new Intent(Produtos.this, VisaoGeralNova.class);
            startActivity(secondActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
