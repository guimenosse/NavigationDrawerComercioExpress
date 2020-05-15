package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;

public class Cliente extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String codigo;

    EditText rzsocial;
    EditText nmfantasia;
    EditText cnpj;
    EditText email;
    EditText telefone;
    EditText celular;
    EditText endereco;
    EditText complemento;
    EditText cep;
    EditText bairro;
    EditText classificacao;
    Spinner cidade;
    Spinner estado;


    BancoController crud;
    Cursor cursor;

    MaskEditTextChangedListener maskCPF;
    MaskEditTextChangedListener maskCNPJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.tb_estado);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.estados_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        //spinner.setAdapter(adapter);

        Spinner spinnerTipoPessoa = (Spinner) findViewById(R.id.cb_tipopessoa);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTipoPessoa = ArrayAdapter.createFromResource(this,
                R.array.tipopessoa_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapterTipoPessoa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerTipoPessoa.setAdapter(adapterTipoPessoa);



        Spinner spinnerEstado = (Spinner) findViewById(R.id.tb_estado);
        try {
            ArrayList<String> estados = crud.selecionarEstado();
            ArrayAdapter<String> arrayEstado = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, estados);
            spinnerEstado.setAdapter(arrayEstado);
        }catch (Exception e){
            e.printStackTrace();
        }


        cnpj = (EditText)findViewById(R.id.tb_cnpj);
        telefone = (EditText)findViewById(R.id.tb_telefone);
        celular = (EditText)findViewById(R.id.tb_telefoneadicional);
        cep = (EditText)findViewById(R.id.tb_cep);
        rzsocial = (EditText)findViewById(R.id.tb_rzsocial);
        nmfantasia = (EditText)findViewById(R.id.tb_nmfantasia);
        email = (EditText)findViewById(R.id.tb_email);
        endereco = (EditText)findViewById(R.id.tb_endereco);
        classificacao = (EditText)findViewById(R.id.tb_Classificacao);
        complemento = (EditText)findViewById(R.id.tb_complemento);
        bairro = (EditText)findViewById(R.id.tb_bairro);
        //cidade = (EditText)findViewById(R.id.tb_cidade);
        estado = (Spinner)findViewById(R.id.tb_estado);

        maskCPF = new MaskEditTextChangedListener("###.###.###-##", cnpj);
        maskCNPJ = new MaskEditTextChangedListener("##.###.###/####-##", cnpj);


        MaskEditTextChangedListener maskTelefone = new MaskEditTextChangedListener("(##)####-####", telefone);
        MaskEditTextChangedListener maskCelular = new MaskEditTextChangedListener("(##)####-####", celular);
        MaskEditTextChangedListener maskCep = new MaskEditTextChangedListener("##.###-###", cep);

        telefone.addTextChangedListener(maskTelefone);
        celular.addTextChangedListener(maskCelular);
        cep.addTextChangedListener(maskCep);

        spinnerTipoPessoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Spinner spinnerTipoPessoa = (Spinner) findViewById(R.id.cb_tipopessoa);
                if (spinnerTipoPessoa.getSelectedItem().toString().equals("Física")) {
                    FU_TipoPessoa("Física");

                } else {
                    FU_TipoPessoa("Jurídica");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        /*spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Spinner spinnerEstado = (Spinner) findViewById(R.id.tb_estado);
                if(!spinnerEstado.getSelectedItem().toString().trim().equals("null") || !spinnerEstado.getSelectedItem().toString().trim().equals("")) {
                    Spinner spinnerCidade = (Spinner) findViewById(R.id.tb_cidade);
                    try {
                        ArrayList<String> cidades = crud.selecionarCidade(spinnerEstado.getSelectedItem().toString());
                        ArrayAdapter<String> arrayCidade = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, cidades);
                        spinnerCidade.setAdapter(arrayCidade);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });*/


        Button botao = (Button)findViewById(R.id.button);

        codigo = this.getIntent().getStringExtra("codigo");

        if(codigo.equals("0")) {
            botao.setText("Cadastrar");
        }else{
            crud = new BancoController(getBaseContext());
            cursor = crud.carregaClienteById(Integer.parseInt(codigo));

            try {
                rzsocial.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
            }catch (Exception e){
                rzsocial.setText("");
            }
            try{
                nmfantasia.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NMFANTASIA)));
            }catch (Exception e){
                nmfantasia.setText("");
            }
            try{
                String compareValue = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TIPOPESSOA));
                ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.tipopessoa_array, android.R.layout.simple_spinner_item);
                adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTipoPessoa.setAdapter(adaptador);
                if (!compareValue.equals(null)) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    spinnerTipoPessoa.setSelection(spinnerPosition);
                }
                if(compareValue.equals("Física")){
                    FU_TipoPessoa("Física");
                }else{
                    FU_TipoPessoa("Jurídica");
                }
            }catch (Exception e){

            }
            try{
                cnpj.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CNPJ)));
            }catch (Exception e){
                cnpj.setText("");
            }
            try{
                email.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EMAIL)));
            }catch (Exception e){
                email.setText("");
            }
            try{
                telefone.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONE)));
            }catch (Exception e){
                telefone.setText("");
            }
            try{
                celular.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FAX)));
            }catch (Exception e){
                celular.setText("");
            }
            try{
                endereco.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ENDERECO)));
            }catch (Exception e){
                endereco.setText("");
            }
            try{
                classificacao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CLASSIFICACAO)));
            }catch (Exception e){
                classificacao.setText("");
            }
            try{
                complemento.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTO)));
            }catch (Exception e){
                complemento.setText("");
            }
            try{
                cep.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CEP)));
            }catch (Exception e){
                cep.setText("");
            }
            try{
                bairro.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.BAIRRO)));
            }catch (Exception e){
                bairro.setText("");
            }
            /*try{
                cidade.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CIDADE)));
            }catch (Exception e){
                cidade.setText("");
            }*/
            try{
                String compareValue = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.UF));
                ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.estados_array, android.R.layout.simple_spinner_item);
                adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                estado.setAdapter(adaptador);
                if (!compareValue.equals(null)) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    estado.setSelection(spinnerPosition);
                }

            }catch (Exception e){

            }


            //botao.setText("Alterar");
            botao.setVisibility(View.INVISIBLE);
        }



            botao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button botao = (Button)findViewById(R.id.button);
                    BancoController crud = new BancoController(getBaseContext());

                    EditText rzsocial = (EditText) findViewById(R.id.tb_rzsocial);
                    EditText nmfantasia = (EditText) findViewById(R.id.tb_nmfantasia);
                    EditText cnpj = (EditText) findViewById(R.id.tb_cnpj);

                    EditText email = (EditText) findViewById(R.id.tb_email);
                    EditText telefone = (EditText) findViewById(R.id.tb_telefone);
                    EditText celular = (EditText) findViewById(R.id.tb_telefoneadicional);

                    EditText endereco = (EditText) findViewById(R.id.tb_endereco);
                    EditText classificacao = (EditText) findViewById(R.id.tb_Classificacao);
                    EditText complemento = (EditText) findViewById(R.id.tb_complemento);
                    EditText cep = (EditText) findViewById(R.id.tb_cep);
                    EditText bairro = (EditText) findViewById(R.id.tb_bairro);
                    //Spinner cidade = (Spinner) findViewById(R.id.tb_cidade);
                    Spinner estado = (Spinner) findViewById(R.id.tb_estado);
                    Spinner tipopessoa = (Spinner)findViewById(R.id.cb_tipopessoa);

                    String rzsocialString = rzsocial.getText().toString();
                    String nmfantasiaString = nmfantasia.getText().toString();
                    String cnpjString = cnpj.getText().toString();
                    String emailString = email.getText().toString();
                    String telefoneString = telefone.getText().toString();
                    String celularString = celular.getText().toString();
                    String enderecoString = endereco.getText().toString();
                    String complementoString = complemento.getText().toString();
                    String cepString = cep.getText().toString();
                    String bairroString = bairro.getText().toString();
                    String cidadeString = "";
                    //String cidadeString = cidade.getText().toString();
                    String estadoString = estado.getSelectedItem().toString();
                    String tipopessoaString = tipopessoa.getSelectedItem().toString();
                    String dtultalteracao = getDateTime();
                    String resultado = "";

                    if (rzsocialString.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Favor informar a razão social do cliente!", Toast.LENGTH_LONG).show();
                    } else if (cnpjString.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Favor informar o cnpj do cliente!", Toast.LENGTH_LONG).show();
                    } else if (cidadeString.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Favor informar a cidade do cliente!", Toast.LENGTH_LONG).show();
                    }else if(estadoString.trim().equals("")){
                        Toast.makeText(getApplicationContext(), "Favor informar o estado do cliente!", Toast.LENGTH_LONG).show();
                    } else {
                        if(botao.getText().toString().equals("Cadastrar")) {
                            //resultado = crud.insereDado(rzsocialString, nmfantasiaString, cnpjString, emailString, telefoneString, celularString, enderecoString, complementoString, cepString, bairroString, cidadeString, estadoString, tipopessoaString, dtultalteracao);
                        }/*else{
                            resultado = crud.alteraRegistro(Integer.parseInt(codigo), rzsocial.getText().toString(),nmfantasia.getText().toString(), cnpj.getText().toString(), email.getText().toString(), telefone.getText().toString(), celular.getText().toString(), endereco.getText().toString(), complemento.getText().toString(), cep.getText().toString(), bairro.getText().toString(), cidade.getText().toString(), estado.getSelectedItem().toString(), tipopessoaString);

                        }*/
                        Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                        Intent secondActivity;
                        secondActivity = new Intent(Cliente.this, HomeActivity.class);
                        startActivity(secondActivity);
                    }

                }
            });


        Button botaoCancelar = (Button)findViewById(R.id.sc_cancelar);

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondActivity;
                secondActivity = new Intent(Cliente.this, HomeActivity.class);
                startActivity(secondActivity);
            }
        });
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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
        getMenuInflater().inflate(R.menu.cliente, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            Intent secondActivity;
            secondActivity = new Intent(Cliente.this, HomeActivity.class);
            startActivity(secondActivity);
        } else if (id == R.id.nav_gallery) {
            Intent secondActivity;
            secondActivity = new Intent(Cliente.this, Pedidos.class);
            startActivity(secondActivity);

        }  else if (id == R.id.nav_share) {
            Intent secondActivity;
            secondActivity = new Intent(Cliente.this, Opcoes.class);
            startActivity(secondActivity);

        } /*else if (id == R.id.nav_send) {
            Intent secondActivity;
            secondActivity = new Intent(Cliente.this, Sincronizar.class);
            startActivity(secondActivity);

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void FU_TipoPessoa(String va_tipopessoa){
        if (va_tipopessoa.equals("Física")) {
            TextView lb_rzsocial = (TextView) findViewById(R.id.lb_rzsocial);
            TextView lb_nmfantasia = (TextView) findViewById(R.id.lb_nmfantasia);
            EditText tb_nmfantasia = (EditText) findViewById(R.id.tb_nmfantasia);
            TextView lb_cnpjcpf = (TextView) findViewById(R.id.lb_cnpj);
            EditText tb_cpf = (EditText)findViewById(R.id.tb_cnpj);
            TextView lb_cep = (TextView) findViewById(R.id.lb_cep);

            lb_rzsocial.setText("Nome");
            lb_nmfantasia.setVisibility(View.INVISIBLE);
            tb_nmfantasia.setVisibility(View.INVISIBLE);
            lb_cnpjcpf.setText("CPF");
            /*RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.tb_rzsocial);
            p.setMargins(0, 15, 0, 0);
            lb_cnpjcpf.setLayoutParams(p);*/

            RelativeLayout.LayoutParams p_endereco = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_endereco.addRule(RelativeLayout.BELOW, R.id.tb_rzsocial);
            p_endereco.setMargins(0, 15, 0, 0);
            lb_cep.setLayoutParams(p_endereco);

            tb_cpf.setText("");
            tb_cpf.setHint("###.###.###-##");
            cnpj.removeTextChangedListener(maskCNPJ);
            cnpj.addTextChangedListener(maskCPF);

        } else {
            TextView lb_rzsocial = (TextView) findViewById(R.id.lb_rzsocial);
            TextView lb_nmfantasia = (TextView) findViewById(R.id.lb_nmfantasia);
            EditText tb_nmfantasia = (EditText) findViewById(R.id.tb_nmfantasia);
            TextView lb_cnpjcpf = (TextView) findViewById(R.id.lb_cnpj);
            EditText tb_cnpj = (EditText)findViewById(R.id.tb_cnpj);
            TextView lb_cep = (TextView) findViewById(R.id.lb_cep);

            lb_rzsocial.setText("Razão Social");
            lb_nmfantasia.setVisibility(View.VISIBLE);
            tb_nmfantasia.setVisibility(View.VISIBLE);
            lb_cnpjcpf.setText("CNPJ");

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.tb_nmfantasia);
            p.setMargins(0, 15, 0, 0);
            lb_cep.setLayoutParams(p);

            /*RelativeLayout.LayoutParams p_cnpj = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_cnpj.addRule(RelativeLayout.BELOW, R.id.tb_nmfantasia);
            p_cnpj.setMargins(0, 15, 0, 0);
            lb_cnpjcpf.setLayoutParams(p_cnpj);*/

            tb_cnpj.setText("");
            tb_cnpj.setHint("##.###.###/####-##");
            cnpj.removeTextChangedListener(maskCPF);
            cnpj.addTextChangedListener(maskCNPJ);

        }
    }
}


