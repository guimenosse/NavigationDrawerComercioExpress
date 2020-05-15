package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;

public class CadastroClientes extends AppCompatActivity {

    String codigo;

    Spinner tipopessoa;
    EditText rzsocial;
    EditText nmfantasia;
    EditText cep;
    EditText endereco;
    EditText classificacao;
    EditText numero;
    EditText complemento;
    EditText bairro;
    Spinner estado;
    EditText cidade;
    EditText cnpj;
    EditText telefone;
    EditText telefoneadicional;
    EditText fax;
    EditText email;
    EditText contato;
    EditText obscliente;
    Spinner tipocliente;
    EditText inscestadual;

    BancoController crud;
    Cursor cursor;

    MaskEditTextChangedListener maskCPF;
    MaskEditTextChangedListener maskCNPJ;

    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    String rzsocialString, nmfantasiaString;
    String cepString;
    String enderecoString;
    String classificacaoString;
    String numeroString;
    String complementoString;
    String bairroString;
    String estadoString;
    String cidadeString;
    String cnpjString;
    String telefoneString;
    String telefoneAdicionalString;
    String faxString;
    String nmcontatoString;
    String emailString;
    String tipopessoaString;
    String tipoclienteString;
    String obsclienteString;
    String dtultalteracao;
    String resultado;
    String inscestadualString;

    private AlertDialog alerta;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /*Spinner spinnerEstado = (Spinner) findViewById(R.id.tb_estado);
        try {
            ArrayList<String> estados = crud.selecionarEstado();
            ArrayAdapter<String> arrayEstado = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, estados);
            spinnerEstado.setAdapter(arrayEstado);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        tipopessoa = (Spinner)findViewById(R.id.cb_tipopessoa);
        rzsocial = (EditText)findViewById(R.id.tb_rzsocial);
        nmfantasia = (EditText)findViewById(R.id.tb_nmfantasia);
        cep = (EditText)findViewById(R.id.tb_cep);
        endereco = (EditText)findViewById(R.id.tb_endereco);
        classificacao = (EditText)findViewById(R.id.tb_Classificacao);
        numero = (EditText)findViewById(R.id.tb_numero);
        complemento = (EditText)findViewById(R.id.tb_complemento);
        bairro = (EditText)findViewById(R.id.tb_bairro);
        estado = (Spinner)findViewById(R.id.tb_estado);
        cidade = (EditText)findViewById(R.id.tb_cidade);
        cnpj = (EditText)findViewById(R.id.tb_cnpj);
        telefone = (EditText)findViewById(R.id.tb_telefone);
        telefoneadicional = (EditText)findViewById(R.id.tb_telefoneadicional);
        fax = (EditText)findViewById(R.id.tb_fax);
        contato = (EditText)findViewById(R.id.tb_nmcontato);
        email = (EditText)findViewById(R.id.tb_email);
        obscliente = (EditText)findViewById(R.id.tb_obsCliente);
        tipocliente = (Spinner)findViewById(R.id.cb_tipcliente);
        inscestadual = (EditText)findViewById(R.id.tb_inscestadual);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estados_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        estado.setAdapter(adapter);

         // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTipoPessoa = ArrayAdapter.createFromResource(this, R.array.tipopessoa_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTipoPessoa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tipopessoa.setAdapter(adapterTipoPessoa);

        /*ArrayAdapter<CharSequence> adapterTipoCliente = ArrayAdapter.createFromResource(this, R.array.tipocliente_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTipoCliente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tipocliente.setAdapter(adapterTipoCliente);*/
        try {
            crud = new BancoController(getBaseContext());
            Cursor listaTipoCliente = crud.selecionarTipoClienteCursor();
            ArrayList<String> suaListaDeConsultaCursor = new ArrayList<String>();
            while (!listaTipoCliente.isAfterLast()) {
                String nmTipo = listaTipoCliente.getString(listaTipoCliente.getColumnIndex(CriaBanco.NMTIPO));
                suaListaDeConsultaCursor.add(listaTipoCliente.getString(listaTipoCliente.getColumnIndex(CriaBanco.NMTIPO)));
                listaTipoCliente.moveToNext();
            }
            ArrayAdapter<String> array;
            array = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, suaListaDeConsultaCursor);
            tipocliente.setAdapter(array);
        }catch (Exception e2){
            e2.printStackTrace();
        }

       /* ArrayList<String> suaListaDeConsulta = crud.selecionarTipoCliente();
        ArrayAdapter<String> array;

        array = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, suaListaDeConsulta);
        tipocliente.setAdapter(array);*/

        maskCPF = new MaskEditTextChangedListener("###.###.###-##", cnpj);
        maskCNPJ = new MaskEditTextChangedListener("##.###.###/####-##", cnpj);


        MaskEditTextChangedListener maskTelefone = new MaskEditTextChangedListener("(##)#####-####", telefone);
        MaskEditTextChangedListener maskTelefoneAdicional = new MaskEditTextChangedListener("(##)#####-####", telefoneadicional);
        MaskEditTextChangedListener maskFax = new MaskEditTextChangedListener("(##)####-####", fax);
        MaskEditTextChangedListener maskCep = new MaskEditTextChangedListener("##.###-###", cep);

        telefone.addTextChangedListener(maskTelefone);
        telefoneadicional.addTextChangedListener(maskTelefoneAdicional);
        fax.addTextChangedListener(maskFax);
        cep.addTextChangedListener(maskCep);

        tipopessoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (tipopessoa.getSelectedItem().toString().equals("Física")) {
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
        Button botaoAlterar = (Button)findViewById(R.id.sc_alterar);
        Button botaoCancelar = (Button)findViewById(R.id.sc_cancelar);
        Button botaoExcluir = (Button)findViewById(R.id.sc_excluir);

        codigo = this.getIntent().getStringExtra("codigo");

        if(codigo.equals("0")) {
            botaoAlterar.setVisibility(View.INVISIBLE);
            botaoExcluir.setVisibility(View.INVISIBLE);
            TextView lb_sccancelar = (TextView)findViewById(R.id.lb_sccancelar);
            RelativeLayout.LayoutParams p_botaocancelar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_botaocancelar.addRule(RelativeLayout.BELOW, R.id.lb_sccadastrar);
            p_botaocancelar.setMargins(0, 90, 0, 0);
            lb_sccancelar.setLayoutParams(p_botaocancelar);

            RelativeLayout.LayoutParams p_botaoalterar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_botaoalterar.addRule(RelativeLayout.BELOW, R.id.tb_fax);
            botaoAlterar.setLayoutParams(p_botaoalterar);

            RelativeLayout.LayoutParams p_botaoexcluir = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p_botaoexcluir.addRule(RelativeLayout.BELOW, R.id.tb_fax);
            botaoExcluir.setLayoutParams(p_botaoexcluir);


        }else{
            crud = new BancoController(getBaseContext());
            cursor = crud.carregaClienteById(Integer.parseInt(codigo));

            try{
                String compareValueTipoPessoa = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TIPOPESSOA));
                ArrayAdapter<CharSequence> adaptadorTipoPessoa = ArrayAdapter.createFromResource(this, R.array.tipopessoa_array, android.R.layout.simple_spinner_item);
                adaptadorTipoPessoa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tipopessoa.setAdapter(adaptadorTipoPessoa);
                if (!compareValueTipoPessoa.equals(null)) {
                    if(compareValueTipoPessoa.equals("F")){
                        compareValueTipoPessoa = "Física";
                    }else{
                        compareValueTipoPessoa = "Jurídica";
                    }
                    int spinnerPositionTipoPessoa = adaptadorTipoPessoa.getPosition(compareValueTipoPessoa);
                    tipopessoa.setSelection(spinnerPositionTipoPessoa);
                }
                if(compareValueTipoPessoa.equals("Física")){
                    FU_TipoPessoa("Física");
                }else{
                    FU_TipoPessoa("Jurídica");
                }
            }catch (Exception e){

            }
            try {
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null")) {
                    rzsocial.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
                }
            }catch (Exception e){
                rzsocial.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NMFANTASIA)).equals("null")) {
                    nmfantasia.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NMFANTASIA)));
                }
            }catch (Exception e){
                nmfantasia.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CEP)).equals("null")) {
                    cep.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CEP)));
                }
            }catch (Exception e){
                cep.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ENDERECO)).equals("null")) {
                    endereco.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ENDERECO)));
                }
            }catch (Exception e){
                endereco.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CLASSIFICACAO)).equals("null")) {
                    classificacao.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CLASSIFICACAO)));
                }
            }catch (Exception e){
                classificacao.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NUMERO)).equals("null")) {
                    numero.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NUMERO)));
                }
            }catch (Exception e){
                numero.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTO)).equals("null")) {
                    complemento.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COMPLEMENTO)));
                }
            }catch (Exception e){
                complemento.setText("");
            }

            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.BAIRRO)).equals("null")) {
                    bairro.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.BAIRRO)));
                }
            }catch (Exception e){
                bairro.setText("");
            }
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
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CIDADE)).equals("null")) {
                    cidade.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CIDADE)));
                }
            }catch (Exception e){
                cidade.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CNPJ)).equals("null")) {
                    cnpj.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CNPJ)));
                }
            }catch (Exception e){
                cnpj.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.INSCESTADUAL)).equals("null")) {
                    inscestadual.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.INSCESTADUAL)));
                }
            }catch (Exception e){
                inscestadual.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONE)).equals("null")) {
                    telefone.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONE)));
                }
            }catch (Exception e){
                telefone.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONEADICIONAL)).equals("null")) {
                    telefoneadicional.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TELEFONEADICIONAL)));
                }
            }catch (Exception e){
                telefoneadicional.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FAX)).equals("null")) {
                    fax.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FAX)));
                }
            }catch (Exception e){
                fax.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EMAIL)).equals("null")) {
                    email.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EMAIL)));
                }
            }catch (Exception e){
                email.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBSCLIENTE)).equals("null")) {
                    obscliente.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBSCLIENTE)));
                }
            }catch (Exception e){
                obscliente.setText("");
            }
            try{
                if(!cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONTATO)).equals("null")) {
                    contato.setText(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CONTATO)));
                }
            }catch (Exception e){
                contato.setText("");
            }
            try{
                String compareValueTipoCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.TIPCLIENTE));
                crud = new BancoController(getBaseContext());
                Cursor listaTipoCliente = crud.selecionarTipoClienteCursor();
                ArrayList<String> suaListaDeConsultaCursor = new ArrayList<String>();
                int VA_CountRows = 0;
                while (!listaTipoCliente.isAfterLast()) {
                    String nmTipo = listaTipoCliente.getString(listaTipoCliente.getColumnIndex(CriaBanco.NMTIPO));
                    suaListaDeConsultaCursor.add(listaTipoCliente.getString(listaTipoCliente.getColumnIndex(CriaBanco.NMTIPO)));
                    if(nmTipo.equals(compareValueTipoCliente)){
                        int spinnerPosition = Integer.parseInt(listaTipoCliente.getString(listaTipoCliente.getColumnIndex(CriaBanco.ID)));
                        spinnerPosition -= 1;
                        tipocliente.setSelection(VA_CountRows);
                    }
                    VA_CountRows += 1;
                    listaTipoCliente.moveToNext();
                }
                /*ArrayAdapter<CharSequence> adaptadorTipoCliente = ArrayAdapter.createFromResource(this, R.array.tipocliente_array, android.R.layout.simple_spinner_item);
                adaptadorTipoCliente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tipocliente.setAdapter(adaptadorTipoCliente);
                if (!compareValueTipoCliente.equals(null)) {
                    int spinnerPosition = adaptadorTipoCliente.getPosition(compareValueTipoCliente);
                    tipocliente.setSelection(spinnerPosition);
                }*/

            }catch (Exception e){
                e.printStackTrace();;
            }

            if(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FGSINCRONIZADO)).equals("N")) {
                TextView lb_scalterar = (TextView)findViewById(R.id.lb_scalterar);
                TextView lb_sccancelar = (TextView)findViewById(R.id.lb_sccancelar);
                TextView lb_scexcluir = (TextView)findViewById(R.id.lb_scexcluir);
                botao.setVisibility(View.INVISIBLE);
                //botaoExcluir.setVisibility(View.INVISIBLE);
                cnpj.setEnabled(false);
                RelativeLayout.LayoutParams p_botaoalterar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaoalterar.addRule(RelativeLayout.BELOW, R.id.lb_tipcliente);
                lb_scalterar.setLayoutParams(p_botaoalterar);

                RelativeLayout.LayoutParams p_botaoexcluir = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaoexcluir.addRule(RelativeLayout.BELOW, R.id.lb_scalterar);
                p_botaoexcluir.setMargins(0, 90, 0, 0);
                lb_scexcluir.setLayoutParams(p_botaoexcluir);


                RelativeLayout.LayoutParams p_botaocancelar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaocancelar.addRule(RelativeLayout.BELOW, R.id.lb_scexcluir);
                p_botaocancelar.setMargins(0, 90, 0, 0);
                lb_sccancelar.setLayoutParams(p_botaocancelar);

            }else{
                RelativeLayout.LayoutParams p_botaocadastrar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaocadastrar.addRule(RelativeLayout.BELOW, R.id.tb_fax);
                botao.setLayoutParams(p_botaocadastrar);

                RelativeLayout.LayoutParams p_botaoalterar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaoalterar.addRule(RelativeLayout.BELOW, R.id.tb_fax);
                botaoAlterar.setLayoutParams(p_botaoalterar);

                RelativeLayout.LayoutParams p_botaoexcluir = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p_botaoexcluir.addRule(RelativeLayout.BELOW, R.id.tb_fax);
                botaoExcluir.setLayoutParams(p_botaoexcluir);


                botao.setVisibility(View.INVISIBLE);
                botaoCancelar.setVisibility(View.INVISIBLE);
                botaoAlterar.setVisibility(View.INVISIBLE);
                botaoExcluir.setVisibility(View.INVISIBLE);

                tipopessoa.setEnabled(false);
                rzsocial.setEnabled(false);
                nmfantasia.setEnabled(false);
                cep.setEnabled(false);
                endereco.setEnabled(false);
                classificacao.setEnabled(false);
                numero.setEnabled(false);
                complemento.setEnabled(false);
                bairro.setEnabled(false);
                estado.setEnabled(false);
                cidade.setEnabled(false);
                cnpj.setEnabled(false);
                telefone.setEnabled(false);
                telefoneadicional.setEnabled(false);
                fax.setEnabled(false);
                contato.setEnabled(false);
                email.setEnabled(false);
                tipocliente.setEnabled(false);
                obscliente.setEnabled(false);
                inscestadual.setEnabled(false);

                TextView lb_clienteSincronizado = (TextView)findViewById(R.id.lb_clientesincronizado);
                lb_clienteSincronizado.setVisibility(View.VISIBLE);
            }

        }



        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button botao = (Button) findViewById(R.id.button);
                BancoController crud = new BancoController(getBaseContext());

                /*EditText rzsocial = (EditText) findViewById(R.id.tb_rzsocial);
                EditText nmfantasia = (EditText) findViewById(R.id.tb_nmfantasia);
                EditText cnpj = (EditText) findViewById(R.id.tb_cnpj);

                EditText email = (EditText) findViewById(R.id.tb_email);
                EditText telefone = (EditText) findViewById(R.id.tb_telefone);
                EditText celular = (EditText) findViewById(R.id.tb_telefoneadicional);

                EditText endereco = (EditText) findViewById(R.id.tb_endereco);
                EditText complemento = (EditText) findViewById(R.id.tb_complemento);
                EditText cep = (EditText) findViewById(R.id.tb_cep);
                EditText bairro = (EditText) findViewById(R.id.tb_bairro);
                EditText cidade = (EditText) findViewById(R.id.tb_cidade);
                Spinner estado = (Spinner) findViewById(R.id.tb_estado);
                Spinner tipopessoa = (Spinner)findViewById(R.id.cb_tipopessoa);*/

                rzsocialString = rzsocial.getText().toString().toUpperCase();
                nmfantasiaString = nmfantasia.getText().toString().toUpperCase();
                cepString = cep.getText().toString();
                enderecoString = endereco.getText().toString().toUpperCase();
                classificacaoString = classificacao.getText().toString().toUpperCase();
                numeroString = numero.getText().toString();
                complementoString = complemento.getText().toString().toUpperCase();
                bairroString = bairro.getText().toString().toUpperCase();
                estadoString = estado.getSelectedItem().toString();
                cidadeString = cidade.getText().toString().toUpperCase();
                cnpjString = cnpj.getText().toString().replace(".", "").replace("-", "").replace("/", "");
                inscestadualString = inscestadual.getText().toString();
                telefoneString = telefone.getText().toString();
                telefoneAdicionalString = telefoneadicional.getText().toString();
                faxString = fax.getText().toString();
                nmcontatoString = contato.getText().toString().toUpperCase();
                emailString = email.getText().toString().toUpperCase();
                tipopessoaString = tipopessoa.getSelectedItem().toString().substring(0, 1);
                tipoclienteString = tipocliente.getSelectedItem().toString();
                obsclienteString = obscliente.getText().toString().toUpperCase();
                dtultalteracao = getDateTime();
                String dtcadastro = getDateTime();
                resultado = "";
                String vendedor = crud.selecionaVendedor();

                if(botao.getText().toString().equals("Cadastrar")) {
                    if (FU_Consiste("incluir")){

                            try {
                                resultado = crud.inserirCliente(cnpjString, rzsocialString, nmfantasiaString, cepString, enderecoString, numeroString, complementoString, bairroString, estadoString, cidadeString, cnpjString, inscestadualString, telefoneString, telefoneAdicionalString, faxString, nmcontatoString, emailString, tipoclienteString, vendedor, tipopessoaString, dtultalteracao, dtcadastro, "N", obsclienteString, classificacaoString, "", "");
                                Toast.makeText(getApplicationContext(), "Cliente cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                                Intent secondActivity;
                                secondActivity = new Intent(CadastroClientes.this, HomeActivity.class);
                                startActivity(secondActivity);
                            }catch (Exception e){
                                //Toast.makeText(getApplicationContext(), "Não foi possivel realizar o cadastro do cliente.", Toast.LENGTH_LONG).show();
                                MensagemUtil.addMsg(CadastroClientes.this, "Não foi possivel realizar o cadastro do cliente.");

                            }
                    }
                }else{
                    try {
                        resultado = crud.alterarCliente(Integer.parseInt(codigo), rzsocialString, nmfantasiaString, cepString, enderecoString, numeroString, complementoString, bairroString, estadoString, cidadeString, cnpjString, inscestadualString, telefoneString, telefoneAdicionalString, faxString, nmcontatoString, emailString, tipoclienteString, tipopessoaString, dtultalteracao, obsclienteString, classificacaoString);
                        Toast.makeText(getApplicationContext(), "Cliente alterado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent secondActivity;
                        secondActivity = new Intent(CadastroClientes.this, HomeActivity.class);
                        startActivity(secondActivity);
                        //resultado = crud.alteraRegistro(Integer.parseInt(codigo), rzsocial.getText().toString(),nmfantasia.getText().toString(), cnpj.getText().toString(), email.getText().toString(), telefone.getText().toString(), celular.getText().toString(), endereco.getText().toString(), complemento.getText().toString(), cep.getText().toString(), bairro.getText().toString(), cidade.getText().toString(), estado.getSelectedItem().toString(), tipopessoaString);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Não foi possivel realizar a alteração do cadastro do cliente.", Toast.LENGTH_LONG).show();

                    }
                }

            }
        });

        botaoAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button botao = (Button) findViewById(R.id.sc_alterar);
                BancoController crud = new BancoController(getBaseContext());

                /*EditText rzsocial = (EditText) findViewById(R.id.tb_rzsocial);
                EditText nmfantasia = (EditText) findViewById(R.id.tb_nmfantasia);
                EditText cnpj = (EditText) findViewById(R.id.tb_cnpj);

                EditText email = (EditText) findViewById(R.id.tb_email);
                EditText telefone = (EditText) findViewById(R.id.tb_telefone);
                EditText celular = (EditText) findViewById(R.id.tb_telefoneadicional);

                EditText endereco = (EditText) findViewById(R.id.tb_endereco);
                EditText complemento = (EditText) findViewById(R.id.tb_complemento);
                EditText cep = (EditText) findViewById(R.id.tb_cep);
                EditText bairro = (EditText) findViewById(R.id.tb_bairro);
                EditText cidade = (EditText) findViewById(R.id.tb_cidade);
                Spinner estado = (Spinner) findViewById(R.id.tb_estado);
                Spinner tipopessoa = (Spinner)findViewById(R.id.cb_tipopessoa);*/

                rzsocialString = rzsocial.getText().toString().toUpperCase();
                nmfantasiaString = nmfantasia.getText().toString().toUpperCase();
                cepString = cep.getText().toString();
                enderecoString = endereco.getText().toString().toUpperCase();
                classificacaoString = classificacao.getText().toString().toUpperCase();
                numeroString = numero.getText().toString();
                complementoString = complemento.getText().toString().toUpperCase();
                bairroString = bairro.getText().toString().toUpperCase();
                estadoString = estado.getSelectedItem().toString();
                cidadeString = cidade.getText().toString().toUpperCase();
                cnpjString = cnpj.getText().toString().replace(".", "").replace("-", "").replace("/", "");
                inscestadualString = inscestadual.getText().toString();
                telefoneString = telefone.getText().toString();
                telefoneAdicionalString = telefoneadicional.getText().toString();
                faxString = fax.getText().toString();
                nmcontatoString = contato.getText().toString().toUpperCase();
                emailString = email.getText().toString().toUpperCase();
                tipopessoaString = tipopessoa.getSelectedItem().toString().substring(0, 1);
                tipoclienteString = tipocliente.getSelectedItem().toString();
                obsclienteString = obscliente.getText().toString().toUpperCase();
                dtultalteracao = getDateTime();
                resultado = "";

                if (FU_Consiste("alterar")){
                    try {
                        resultado = crud.alterarCliente(Integer.parseInt(codigo), rzsocialString, nmfantasiaString, cepString, enderecoString, numeroString, complementoString, bairroString, estadoString, cidadeString, cnpjString, inscestadualString, telefoneString, telefoneAdicionalString, faxString, nmcontatoString, emailString, tipoclienteString, tipopessoaString, dtultalteracao, obsclienteString, classificacaoString);
                        Toast.makeText(getApplicationContext(), "Cliente alterado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent secondActivity;
                        secondActivity = new Intent(CadastroClientes.this, HomeActivity.class);
                        startActivity(secondActivity);
                        //resultado = crud.alteraRegistro(Integer.parseInt(codigo), rzsocial.getText().toString(),nmfantasia.getText().toString(), cnpj.getText().toString(), email.getText().toString(), telefone.getText().toString(), celular.getText().toString(), endereco.getText().toString(), complemento.getText().toString(), cep.getText().toString(), bairro.getText().toString(), cidade.getText().toString(), estado.getSelectedItem().toString(), tipopessoaString);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Não foi possivel realizar a alteração do cadastro do cliente.", Toast.LENGTH_LONG).show();

                    }
                }

            }
        });

        builder = new AlertDialog.Builder(this);

        botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tb_rzsocial = (EditText) findViewById(R.id.tb_rzsocial);

                crud = new BancoController(getBaseContext());
                //cursor = crud.carregaDadosById(Integer.parseInt(codigo));

                if (crud.verificaPedidoIdCliente(Integer.parseInt(codigo)).equals("S")) {
                    MensagemUtil.addMsg(CadastroClientes.this, "Cliente se encontra associado a um pedido, não será possivel excluir o cliente!");
                } else {


                    //define o titulo

                    builder.setTitle("Excluir Cliente");
                    //define a mensagem
                    builder.setMessage("Deseja mesmo excluir o cliente " + tb_rzsocial.getText().toString() + "?")
                    ;

                    //define um botão como positivo
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(ManutencaoProdutoPedido.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                            try {
                                BancoController crud = new BancoController(getBaseContext());
                                crud.deletaCliente(Integer.parseInt(codigo));
                                MensagemUtil.addMsg(CadastroClientes.this, "Cliente excluido com sucesso!");
                                Intent secondActivity;
                                secondActivity = new Intent(CadastroClientes.this, HomeActivity.class);
                                startActivity(secondActivity);
                            } catch (Exception e) {
                                MensagemUtil.addMsg(CadastroClientes.this, "Não foi possivel excluir o cliente do pedido devido à seguinte situação:" + e.getMessage().toString());
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
            }
        });

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondActivity;
                secondActivity = new Intent(CadastroClientes.this, HomeActivity.class);
                startActivity(secondActivity);
            }
        });
    }

    public boolean FU_Consiste(String comando){
        BancoController crud = new BancoController(getBaseContext());

        if (rzsocialString.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Favor informar a razão social do cliente!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (cidadeString.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Favor informar a cidade do cliente!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(estadoString.trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Favor informar o estado do cliente!", Toast.LENGTH_LONG).show();
            return  false;
        }


        if(tipopessoaString.equals("F")) {
            if (cnpjString.trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Favor informar o cpf do cliente!", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!validateCPF(cnpjString.replace(".", "").replace("-", ""))) {
                Toast.makeText(getApplicationContext(), "Favor informar um cnpj/cpf válido do cliente!", Toast.LENGTH_LONG).show();
                return false;
            }
            if(comando.equals("incluir")) {
                if (crud.carregaClienteByCGC(cnpjString).equals("S")) {
                    Toast.makeText(getApplicationContext(), "Cpf já existente, não é possivel cadastrar um cliente com cpf repetido!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }else{
            if (cnpjString.trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Favor informar o cnpj do cliente!", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!isValidCNPJ(cnpjString.replace(".", "").replace("-", "").replace("/", ""))) {
                Toast.makeText(getApplicationContext(), "Favor informar um cnpj/cpf válido do cliente!", Toast.LENGTH_LONG).show();
                return false;
            }
            if(comando.equals("incluir")) {
               if (crud.carregaClienteByCGC(cnpjString).equals("S")) {
                    Toast.makeText(getApplicationContext(), "Cnpj já existente, não é possivel cadastrar um cliente com cnpj repetido!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        if(!emailString.trim().equals("")){
            if(!validateEmail(emailString)){
                Toast.makeText(getApplicationContext(), "Favor informar um email válido do cliente!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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

            //tb_cpf.setText("");
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

            //tb_cnpj.setText("");
            tb_cnpj.setHint("##.###.###/####-##");
            cnpj.removeTextChangedListener(maskCPF);
            cnpj.addTextChangedListener(maskCNPJ);

        }
    }

    public static boolean validateCPF(String CPF) {
        if (CPF.equals("00000000000") || CPF.equals("11111111111")
                || CPF.equals("22222222222") || CPF.equals("33333333333")
                || CPF.equals("44444444444") || CPF.equals("55555555555")
                || CPF.equals("66666666666") || CPF.equals("77777777777")
                || CPF.equals("88888888888") || CPF.equals("99999999999")) {
            return false;
        }
        char dig10, dig11;
        int sm, i, r, num, peso;
        try {
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48);
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return true;
            else
                return false;
        } catch (Exception erro) {
            return false;
        }
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    public static boolean isValidCNPJ(String cnpj) {
        if ((cnpj==null)||(cnpj.length()!=14)) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }


    public final static boolean validateEmail(String txtEmail) {
        if (TextUtils.isEmpty(txtEmail)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches();
        }
    }

}
