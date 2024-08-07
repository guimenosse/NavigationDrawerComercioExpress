package br.comercioexpress.plano;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import models.CriaBanco;

/**
 * Created by Desenvolvimento on 26/11/2015.
 */
public class BancoController {

    private SQLiteDatabase db;
    private CriaBanco banco;


    public BancoController(Context context){
        banco = new CriaBanco(context);
    }

    /*-------------------------------------------------Funções para clientes -------------------------------
    --------------------------------------------------------------------------------------------------------
     */

            /*------------------------ Funções de CRUD básicas ------------------------------
            ---------------------------------------------------------------------------------
            */

    public String fu_CarregarCdCliente() {
        String cdEmitente = "1";
        Cursor cursor;
        String[] campos = {CriaBanco.ID};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA, campos, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ID))) > Integer.parseInt(cdEmitente)){
                    break;
                }
                cdEmitente = String.valueOf(Integer.parseInt(cdEmitente) + 1);
                cursor.moveToNext();
            }
        }

        cursor.close();
        //db.close();

        return cdEmitente;
    }

    //----------------------- Função para inclusão de um novo cliente --------------------------------------
    public String inserirCliente(String cdcliente, String rzsocial, String nmfantasia, String cep, String endereco,
                                 String numero, String complemento, String bairro, String uf, String cidade,
                                 String cgc, String inscestadual, String telefone, String telefoneadicional,
                                 String fax, String contato, String email, String tipcliente, String vendedor,
                                 String tipopessoa, String dtultalteracao, String dtcadastro, String fgsincronizado,
                                 String obscliente, String classificacao, String fidelidade, String tipopreco)
    {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDCLIENTE, fu_CarregarCdCliente());
        valores.put(CriaBanco.RZSOCIAL, rzsocial);
        valores.put(CriaBanco.NMFANTASIA, nmfantasia);
        valores.put(CriaBanco.CEP, cep);
        valores.put(CriaBanco.ENDERECO, endereco);
        valores.put(CriaBanco.NUMERO, numero);
        valores.put(CriaBanco.COMPLEMENTO, complemento);
        valores.put(CriaBanco.BAIRRO, bairro);
        valores.put(CriaBanco.UF, uf);
        valores.put(CriaBanco.CIDADE, cidade);
        valores.put(CriaBanco.TIPOPESSOA, tipopessoa);
        valores.put(CriaBanco.CNPJ, cgc);
        valores.put(CriaBanco.INSCESTADUAL, inscestadual);
        valores.put(CriaBanco.TELEFONE, telefone);
        valores.put(CriaBanco.TELEFONEADICIONAL, telefoneadicional);
        valores.put(CriaBanco.FAX, fax);
        valores.put(CriaBanco.CONTATO, contato);
        valores.put(CriaBanco.EMAIL, email);
        valores.put(CriaBanco.TIPCLIENTE, tipcliente);
        valores.put(CriaBanco.VENDEDOR, vendedor);
        valores.put(CriaBanco.DTULTALTERACAO, dtultalteracao);
        valores.put(CriaBanco.DTCADASTRO, dtcadastro);
        valores.put(CriaBanco.OBSCLIENTE, obscliente);
        valores.put(CriaBanco.FGSINCRONIZADO, fgsincronizado);
        valores.put(CriaBanco.CLASSIFICACAO, classificacao);
        valores.put(CriaBanco.FIDELIDADE, fidelidade);
        valores.put(CriaBanco.TIPOPRECO, tipopreco);
        valores.put(CriaBanco.FGBLOQUEIO, "S");

        resultado = db.insert(CriaBanco.TABELA, null, valores);
        db.close();

        if(resultado == -1){
            return "Erro ao inserir o cliente!";
        }else{
            return "Cliente inserido com sucesso!";
        }

    }

    //----------------------- Função para alteração de um cliente que ainda não foi sincronizado --------------------------------------
    public String alterarCliente(int id, String rzsocial, String nmfantasia, String cep, String endereco, String numero, String complemento, String bairro, String uf,
                                 String cidade, String cnpj, String inscestadual, String telefone, String telefoneadicional, String fax, String nmcontato, String email,
                                 String tipocliente, String tipopessoa, String dtultalteracao, String obscliente, String classificacao){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + id;

        valores = new ContentValues();
        valores.put(CriaBanco.TIPOPESSOA, tipopessoa);
        valores.put(CriaBanco.RZSOCIAL, rzsocial);
        valores.put(CriaBanco.NMFANTASIA, nmfantasia);
        valores.put(CriaBanco.CEP, cep);
        valores.put(CriaBanco.ENDERECO, endereco);
        valores.put(CriaBanco.NUMERO, numero);
        valores.put(CriaBanco.BAIRRO, bairro);
        valores.put(CriaBanco.UF, uf);
        valores.put(CriaBanco.CIDADE, cidade);
        valores.put(CriaBanco.CNPJ, cnpj);
        valores.put(CriaBanco.INSCESTADUAL, inscestadual);
        valores.put(CriaBanco.TELEFONE, telefone);
        valores.put(CriaBanco.TELEFONEADICIONAL, telefoneadicional);
        valores.put(CriaBanco.FAX, fax);
        valores.put(CriaBanco.CONTATO, nmcontato);
        valores.put(CriaBanco.EMAIL, email);
        valores.put(CriaBanco.TIPCLIENTE, tipocliente);
        valores.put(CriaBanco.OBSCLIENTE, obscliente);
        valores.put(CriaBanco.DTULTALTERACAO, dtultalteracao);
        valores.put(CriaBanco.CLASSIFICACAO, classificacao);

        resultado = db.update(CriaBanco.TABELA,valores,where,null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o cliente!";
        }else{
            return "Cliente alterado com sucesso!";
        }
    }

    //--------------------- Função para exclusão de um cliente que ainda não foi sincronizadoe um novo cliente ------------------------
    public void deletaCliente(int id){
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA,where,null);
        db.close();
    }

    //----------------------- Função para exclusão de todos os clientes --------------------------------------
    public void deletaTodosClientes(){
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELA, null, null);
        db.close();
    }


    /*---------------------------------------Funções para seleção de Clientes--------------------
    ---------------------------------------------------------------------------------------------
     */

    //----------------------- Função para carregar todos os clientes para alimentar o listview de clientes--------------------------------
    public Cursor carregaClientes(){

        Cursor cursor;
        String[] campos = {banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.TELEFONE, banco.EMAIL, banco.FGBLOQUEIO};
        String orderBy = banco.RZSOCIAL;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA, campos, null, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;

    }

    //------------------------Função para carregamento da listview de clientes de acordo com o nome digitado no campo de busca------------
    public Cursor carregaClientesNome(String nome){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.TELEFONE, banco.EMAIL, banco.FGBLOQUEIO};
        String orderBy = banco.RZSOCIAL;
        String where = CriaBanco.RZSOCIAL + " LIKE '%" + nome + "%'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, orderBy, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //----------------------- Função que verifica se o CNPJ/CPF que está sendo incluso já se encontra cadastrado em algum outro cliente------
    public String carregaClienteByCGC(String CGC){
        Cursor cursor;
        String validou = "N";
        String[] campos = {CriaBanco.RZSOCIAL, CriaBanco.CNPJ};
        String where = CriaBanco.CNPJ + " = '" + CGC + ".0000'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                validou = "S";
            }
        }
        db.close();

        return validou;
    }

    //----------------------Função para buscar todos os dados de um cliente para a tela de Cadastro de Clientes----------------------------
    public Cursor carregaClienteById(int id){
        Cursor cursor;
        String[] campos = {
                banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP,
                banco.ENDERECO, banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF,
                banco.CIDADE, banco.CNPJ, banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX,
                banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE, banco.VENDEDOR, banco.DTULTALTERACAO,
            banco.TIPOPESSOA, banco.DTCADASTRO, banco.FGSINCRONIZADO, banco.OBSCLIENTE, banco.INSCESTADUAL, banco.CLASSIFICACAO
        };
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //---------------Função para carregar os dados do cliente pelo seu codigo de cliente---------------------------------
    public Cursor carregaClienteByCdCliente(String cdcliente){
        Cursor cursor;
        String[] campos = {banco.ID, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO, banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ, banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE, banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE, banco.CLASSIFICACAO};
        String where = CriaBanco.ID + "=" + cdcliente + "";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //---------------Função para carregar os dados do cliente pelo seu cgc para novos clientes serem sincronizados---------------------------------
    public Cursor carregaClienteByCGCNovoCliente(String cdcliente){
        Cursor cursor;
        String[] campos = {banco.ID, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO, banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ, banco.INSCESTADUAL, banco.INSCESTADUAL, banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE, banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE, banco.CLASSIFICACAO};
        String where = CriaBanco.CDCLIENTE + "='" + cdcliente + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-----------------Função para carregar os dados do cliente de acordo com seu código de cliente-----------------------
    public Cursor carregaClienteByCdClienteNovoCliente(String cdcliente){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO, banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ, banco.INSCESTADUAL, banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE, banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE, banco.CLASSIFICACAO};
        String where = CriaBanco.CDCLIENTE + "='" + cdcliente + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-----------------Função para carregar os dados dos clientes nao sincronizados--------------------------------
    public Cursor carregaClientesNaoSincronizados(String fgsincronizado){
        Cursor cursor;
        String[] campos = {
                banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP,
                banco.ENDERECO, banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF,
                banco.CIDADE, banco.TIPOPESSOA, banco.CNPJ, banco.INSCESTADUAL, banco.TELEFONE,
                banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE,
                banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.OBSCLIENTE,
                banco.INSCESTADUAL, banco.CLASSIFICACAO
        };
        String where = CriaBanco.FGSINCRONIZADO + " = '" + fgsincronizado + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //--------------------------Função para carregar a observação do cliente -----------------------------------------
    public Cursor carregaObsCliente(){
        Cursor cursor;
        String[] campos = {banco.OBSCLIENTE};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }


    /*---------------------------------------------------Fim dos comandos de clientes ---------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */


    /*-------------------------------------------Comandos para a parte de Produtos ------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */

            /*------------------------ Funções de CRUD básicas ------------------------------
            ---------------------------------------------------------------------------------
            */
    //---------------------------Função para inclusão dos produtos na sincronização-----------------------------------------
    public String inserirProduto(String cdproduto, String descricao, String estoqueatual, String valorunitario, String dtultalteracao, String maxdescpermitido){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDPRODUTO, cdproduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.ESTOQUEATUAL, estoqueatual);
        valores.put(CriaBanco.VALORUNITARIO, valorunitario);
        valores.put(CriaBanco.DTULTALTERACAO, dtultalteracao);
        valores.put(CriaBanco.DESCMAXPERMITIDO, maxdescpermitido);

        resultado = db.insert(CriaBanco.TABELAPRODUTOS, null, valores);
        db.close();

        if(resultado == -1){
            return "Erro ao inserir o produto!";
        }else{
            return "Produto inserido com sucesso!";
        }

    }

    public String inserirProdutoFilial(String cdproduto, String descricao, String estoqueatual, String valorunitario,
                                       String valoratacado,
                                       String dtultalteracao, String maxdescpermitido, String maxdescpermitidoa,
                                       String maxdescpermitidob, String maxdescpermitidoc, String maxdescpermitidod,
                                       String maxdescpermitidoe, String maxdescpermitidofidelidade){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDPRODUTO, cdproduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.ESTOQUEATUAL, estoqueatual);
        valores.put(CriaBanco.VALORUNITARIO, valorunitario);
        valores.put(CriaBanco.VALORATACADO, valoratacado);
        valores.put(CriaBanco.DTULTALTERACAO, dtultalteracao);
        valores.put(CriaBanco.DESCMAXPERMITIDO, maxdescpermitido);
        valores.put(CriaBanco.DESCMAXPERMITIDOA, maxdescpermitidoa);
        valores.put(CriaBanco.DESCMAXPERMITIDOB, maxdescpermitidob);
        valores.put(CriaBanco.DESCMAXPERMITIDOC, maxdescpermitidoc);
        valores.put(CriaBanco.DESCMAXPERMITIDOD, maxdescpermitidod);
        valores.put(CriaBanco.DESCMAXPERMITIDOE, maxdescpermitidoe);
        valores.put(CriaBanco.DESCMAXPERMITIDOFIDELIDADE, maxdescpermitidofidelidade);

        resultado = db.insert(CriaBanco.TABELAPRODUTOS, null, valores);
        db.close();

        if(resultado == -1){
            return "Erro ao inserir o produto!";
        }else{
            return "Produto inserido com sucesso!";
        }

    }

    public String atualizarDescontos(String cdproduto, String valorunitario, String descontoa, String descontob, String descontoc, String descontod, String descontoe, String descontofidelidade){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.VLUNITARIO, valorunitario);
        valores.put(CriaBanco.DESCMAXPERMITIDOA, descontoa);
        valores.put(CriaBanco.DESCMAXPERMITIDOB, descontob);
        valores.put(CriaBanco.DESCMAXPERMITIDOC, descontoc);
        valores.put(CriaBanco.DESCMAXPERMITIDOD, descontod);
        valores.put(CriaBanco.DESCMAXPERMITIDOE, descontoe);
        valores.put(CriaBanco.DESCMAXPERMITIDOFIDELIDADE, descontofidelidade);


        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    /*--------------------------------------------------Inicio da configuração de Descontos da tabela de PrecoFilial
    ----------------------------------------------------------------------------------------------------------------
     */

    public String atualizarValorUnitarioFilial(String cdproduto, String valorunitario){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.VALORUNITARIO, valorunitario);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoA(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOA, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoB(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOB, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoC(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOC, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoD(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOD, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoE(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOE, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    public String atualizarDescontoFidelidade(String cdproduto, String desconto){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.DESCMAXPERMITIDOFIDELIDADE, desconto);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o produto!";
        }else{
            return "Produto alterado com sucesso!";
        }

    }

    //------------------------------ Função que limpa todos os produtos para a sincronização -------------------------------------
    public void deletaTodosProdutos(){
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELAPRODUTOS, null, null);
        db.close();
    }

    /*---------------------------------------Funções para seleção de Produtos--------------------
    ---------------------------------------------------------------------------------------------
     */

    //----------------------- Função para carregar os dados dos produtos para a listview da tela de Produtos ----------------------
    public Cursor carregaProdutos(){

        Cursor cursor;
        String[] campos = {banco.ID, banco.DESCRICAO};
        String orderBy = CriaBanco.DESCRICAO;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, null, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;

    }

    //------------------ Função que busca todos os valores do produto -----------------------------------------
    public Cursor carregaProdutosCompleto(){

        Cursor cursor;
        String[] campos = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.COMPLEMENTODESCRICAO, banco.ESTOQUEATUAL, banco.QTDEDISPONIVEL, banco.VALORUNITARIO, banco.VALORATACADO};
        String orderBy = CriaBanco.DESCRICAO;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, null, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;

    }

    //--------------------------- Função para atualização da listview da tela de produtos de acordo com a descricao digitada--------------
    public Cursor carregaProdutosDescricao(String descricao){
        Cursor cursor;
        String[] campos = {banco.ID, banco.DESCRICAO};
        String where = CriaBanco.DESCRICAO + " LIKE '%" + descricao + "%'";
        String orderBy = CriaBanco.DESCRICAO;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, orderBy, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-------------------- Função para carregamento do produto de acordo com seu id ----------------------------------------
    public Cursor carregaProdutosById(int id){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.COMPLEMENTODESCRICAO, banco.ESTOQUEATUAL, banco.VALORUNITARIO, banco.VALORATACADO, banco.DESCMAXPERMITIDO};
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-------------------- Função para carregamento do produto de acordo com seu código de produto ----------------------------------------
    public Cursor carregaDadosProdutosByCdProduto(String cdproduto){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.COMPLEMENTODESCRICAO, banco.ESTOQUEATUAL, banco.VALORUNITARIO, banco.DESCMAXPERMITIDO, banco.VALORATACADO};
        String where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }


    /*---------------------------------------------------Fim dos comandos de produtos ---------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */

    /*------------------------------------------Inicio dos comandos de Pedidos ----------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */

    public String abrirPedido(String cdcliente, String rzsocial, String dtemissao, String cdvendedor, String fgsituacao){

        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDEMITENTE, cdcliente);
        valores.put(CriaBanco.RZSOCIAL, rzsocial);
        valores.put(CriaBanco.DTEMISSAO, dtemissao);
        valores.put(CriaBanco.CDVENDEDOR, cdvendedor);
        valores.put(CriaBanco.FGSITUACAO, fgsituacao);

        resultado = db.insert(CriaBanco.TABELAMESTREPEDIDO, null, valores);
        db.close();

        if(resultado == -1){
            return "Erro ao abrir o novo pedido!";
        }else{
            return "Pedido aberto com sucesso!";
        }

    }


    public String alteraPedido(String numpedido, String cdcliente, String rzsocial, String condpgto, String vltotal, String percdesconto, String vldesconto, String obs, String fgsituacao){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + numpedido;

        valores = new ContentValues();
        valores.put(CriaBanco.CDEMITENTE, cdcliente);
        valores.put(CriaBanco.RZSOCIAL, rzsocial);
        valores.put(CriaBanco.CONDPGTO, condpgto);
        valores.put(CriaBanco.VLTOTAL, vltotal);
        valores.put(CriaBanco.PERCDESCONTO, percdesconto);
        valores.put(CriaBanco.VLDESCONTO, vldesconto);
        valores.put(CriaBanco.OBS, obs);
        valores.put(CriaBanco.FGSITUACAO, fgsituacao);


        resultado = db.update(CriaBanco.TABELAMESTREPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o pedido!";
        }else{
            return "Pedido alterado com sucesso!";
        }

    }

    public String alteraSituacaoPedido(String numpedido, String fgsituacao){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + numpedido;

        valores = new ContentValues();
        valores.put(CriaBanco.FGSITUACAO, fgsituacao);

        resultado = db.update(CriaBanco.TABELAMESTREPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o pedido!";
        }else{
            return "Pedido alterado com sucesso!";
        }
    }

    public Cursor carregaTodosPedidos(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FGSITUACAO, banco.RZSOCIAL};
        String orderBy = CriaBanco.ID;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, null, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor carregaTodosPedidosByCliente(String cliente){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FGSITUACAO, banco.RZSOCIAL};
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.RZSOCIAL + " LIKE '%" + cliente + "%'";
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public String carregaNumPedido(){
        Cursor cursor;
        String[] campos = {banco.ID};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAMESTREPEDIDO, campos, null, null, null, null, null);
        String numpedido = "";
        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                numpedido = cursor.getString(cursor.getColumnIndex(CriaBanco.ID));
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return numpedido;
    }

    public Cursor carregaDadosPedido(String numpedido){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CONDPGTO, banco.VLTOTAL, banco.PERCDESCONTO, banco.VLDESCONTO, banco.CDEMITENTE, banco.RZSOCIAL, banco.OBS, banco.DTEMISSAO, banco.FGSITUACAO, banco.CDVENDEDOR};
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.ID + "=" + numpedido;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public void deletaPedido(int id){
        String where = CriaBanco.ID + "=" + id;
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELAMESTREPEDIDO, where, null);
        db.close();

        where = CriaBanco.NUMPEDIDO + "=" + id;
        db = banco.getReadableDatabase();
        db.delete(CriaBanco.TABELAITEMPEDIDO, where, null);
        db.close();
    }

    public String verificaPedidoIdCliente(int id){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDEMITENTE};
        String where = CriaBanco.CDEMITENTE + "=" + id;
        String validou = "N";
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, null, null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                validou = "S";
            }
        }
        db.close();
        cursor.close();
        return validou;
    }

    public Cursor carregaPedidoCliente(String cdcliente){
        Cursor cursor;
        String[] campos = {banco.ID};
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.CDEMITENTE + "=" + cdcliente;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public String carregaPedidoAbertos(){
        Cursor cursor;
        String[] campos = {banco.ID};
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.FGSITUACAO + " = 'ABERTO'";
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        String validou = "";
        if(cursor != null){
            if(cursor.moveToFirst()){
                validou = "S";
            }else{
                validou = "N";
            }
        }else{
            validou = "N";
        }

        db.close();
        return validou;
    }

    public String alteraClientePedido(String cdemitente, int id){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + id;

        valores = new ContentValues();
        valores.put(CriaBanco.CDCLIENTE, cdemitente);
        valores.put(CriaBanco.FGSINCRONIZADO, "S");

        resultado = db.update(CriaBanco.TABELA, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o cliente!";
        }else{
            return "Cliente alterado com sucesso!";
        }
    }

    public String alteraEmitentePedido(String cdemitente, int id){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + id;

        valores = new ContentValues();
        valores.put(CriaBanco.CDEMITENTE, cdemitente);

        resultado = db.update(CriaBanco.TABELAMESTREPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o cliente!";
        }else{
            return "Cliente alterado com sucesso!";
        }
    }

    public Cursor carregaProdutosDescricaoPedido(String descricao){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.COMPLEMENTODESCRICAO, banco.ESTOQUEATUAL, banco.QTDEDISPONIVEL, banco.VALORUNITARIO, banco.VALORATACADO};
        String where = "(" + CriaBanco.DESCRICAO + " LIKE '%" + descricao + "%'";
        where += " OR " + CriaBanco.COMPLEMENTODESCRICAO + " LIKE '%" + descricao + "%'";
        where += " OR " + CriaBanco.CDPRODUTO + " LIKE '%" + descricao + "%')";

        String orderBy = CriaBanco.DESCRICAO;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, orderBy, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }




    /*------------------------------------------Fim dos comandos de Pedidos--------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */


    /*------------------------------------------Inicio dos comandos de Item Pedidos -----------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */

            /*------------------------ Funções de CRUD básicas ------------------------------
            ---------------------------------------------------------------------------------
            */

    //--------------------------- Função para inclusão de itens no pedido ---------------------------------------------------
    public String inserirItemPedido(String numpedido, String cdproduto, String descricao, String qtde, String percdesconto, String vltotaldesconto, String vlmaxdescpermitido, String vlunitario, String vlliquido, String vltotal){

        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.NUMPEDIDO, numpedido);
        valores.put(CriaBanco.CDPRODUTO, cdproduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.QTDE, qtde);
        valores.put(CriaBanco.PERCDESCONTO, percdesconto);
        valores.put(CriaBanco.VLDESCONTO, vltotaldesconto);
        valores.put(CriaBanco.VLMAXDESCPERMITIDO, vlmaxdescpermitido);
        valores.put(CriaBanco.VLUNITARIO, vlunitario);
        valores.put(CriaBanco.VLLIQUIDO, vlliquido);
        valores.put(CriaBanco.VLTOTAL, vltotal);

        resultado = db.insert(CriaBanco.TABELAITEMPEDIDO, null, valores);
        db.close();

        if(resultado == -1){
            return "Erro ao inserir o item do pedido!";
        }else{
            return "Item do pedido inserido com sucesso!";
        }

    }

    //---------------------------Função para alteração dos itens incluidos no pedido -----------------------------------------
    public String alteraItemPedido(String numpedido, String cdproduto, String qtde, String percdesconto, String vltotaldesconto, String vlliquido, String vltotal, String vlunitario){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.NUMPEDIDO + "=" + numpedido + " AND " + CriaBanco.CDPRODUTO + "='" + cdproduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.QTDE, qtde);
        valores.put(CriaBanco.PERCDESCONTO, percdesconto);
        valores.put(CriaBanco.VLDESCONTO, vltotaldesconto);
        valores.put(CriaBanco.VLLIQUIDO, vlliquido);
        valores.put(CriaBanco.VLTOTAL, vltotal);
        valores.put(CriaBanco.VLUNITARIO, vlunitario);

        resultado = db.update(CriaBanco.TABELAITEMPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao alterar o item do pedido!";
        }else{
            return "Item do Pedido alterado com sucesso!";
        }
    }

    //---------------------------Função para exclusão dos itens incluidos no pedido-----------------------------------------
    public String deletaItemPedido(String numpedido, String cdproduto){
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido + " AND " + CriaBanco.CDPRODUTO + "='" + cdproduto + "'";
        long resultado;

        db = banco.getReadableDatabase();
        resultado = db.delete(CriaBanco.TABELAITEMPEDIDO, where, null);
        db.close();

        if(resultado == -1){
            return "Erro ao excluir o item do pedido!";
        }else{
            return "Item do Pedido excluido com sucesso!";
        }
    }

        /*---------------------------------------Funções para seleção de Itens do Pedido--------------------
            ---------------------------------------------------------------------------------------------
        */

    //---------------------------Função para carregamento dos itens do pedido -----------------------------------------
    public Cursor carregaItemPedido(String numpedido){
        Cursor cursor;
        String[] campos = {banco.ID, banco.DESCRICAO, banco.QTDE, banco.VLTOTAL, banco.PERCDESCONTO, banco.VLDESCONTO, banco.CDPRODUTO, banco.VLUNITARIO, banco.VLMAXDESCPERMITIDO};
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //---------------------------- Função para carregar os dados do item do pedido para alteração -----------------------
    public Cursor carregaItemPedidoAlteracao(String numpedido, String codigo){
        Cursor cursor;
        String[] campos = {banco.ID, banco.DESCRICAO, banco.QTDE, banco.VLTOTAL, banco.PERCDESCONTO, banco.CDPRODUTO, banco.VLUNITARIO, banco.VLLIQUIDO};
        String where = CriaBanco.ID + "=" + codigo + " AND " + CriaBanco.NUMPEDIDO + "=" + numpedido;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-------------------------- Função para verificar se o produto selecionado na tela de seleção de produtos do pedido já se encontra
    // -----------------------incluso no pedido que está sofrendo alteração-------------------------------------------------------------
    public String verificaProdutoItemPedido(String numpedido, String cdproduto){

        Cursor cursor;
        String validou = "N";
        String[] campos = {CriaBanco.ID, CriaBanco.CDPRODUTO};
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido + " AND " + CriaBanco.CDPRODUTO + " = '" + cdproduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                validou = "S";
            }
        }
        db.close();

        return validou;
    }

    //----------------------------------- Função para carregar os dados do produto -------------------------------------
    public  Cursor carregaProdutoItemPedido(String numpedido, String cdproduto){
        Cursor cursor;
        String[] campos = {banco.ID, banco.PERCDESCONTO, banco.QTDE, banco.VLUNITARIO, banco.VLLIQUIDO};
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido + " AND " + CriaBanco.CDPRODUTO + "= '" + cdproduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //------------------------ Função para verificar se já foi incluido algum produto no pedido -------------------------
    public String verificaItemPedido(String numpedido){

        Cursor cursor;
        String validou = "N";
        String[] campos = {CriaBanco.ID};
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                validou = "S";
            }
        }
        db.close();

        return validou;
    }


    /*------------------------------------------Fim dos comandos de Item Pedidos---------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    */

     /*------------------------------------------Inicio dos comandos de Login -----------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
    -------------------------------------------------------------------------------------------------------------------------
     */

    public void insereLogin(String usuario, String senha){

        ContentValues valores;
        db = banco.getWritableDatabase();

        db.delete("login",null,null);
        db.close();

        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("usuario", usuario);
        valores.put("senha", senha);

        db.insert("login", null, valores);
        db.close();
    }

    public Cursor carregaDadosTabelaLogin(){

        Cursor cursor;
        String[] campos = {banco.ID, banco.USUARIOLOGIN, banco.SENHALOGIN, banco.CDCLIENTEBANCO, banco.CDVENDEDORDEFAULT, banco.NMUSUARIOSISTEMA};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;

    }

    public void insereNmUsuarioSistema(String usuarioSistema){
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.NMUSUARIOSISTEMA, usuarioSistema);

        resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
        db.close();
    }

    public String selecionarNmUsuarioSistema(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.NMUSUARIOSISTEMA};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);
        String nmusuarioSistema = "";
        if(cursor!=null){
            cursor.moveToFirst();
            nmusuarioSistema = cursor.getString(cursor.getColumnIndex(CriaBanco.NMUSUARIOSISTEMA));
        }

        cursor.close();
        db.close();
        return nmusuarioSistema;
    }



    public String validaLogin() {

        Cursor cursor;
        String validou = "N";
        String[] campos = {"usuario","senha"};
        db = banco.getReadableDatabase();
        cursor = db.query("login", campos, null, null, null, null, null, null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                validou = "S";
            }
        }
        db.close();

        return validou;
    }


    public void insereVendedor(String cdvendedor){
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.CDVENDEDORDEFAULT, cdvendedor);

        resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
        db.close();

    }

    public void insereCdClienteBanco(String cdcliente){
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put(CriaBanco.CDCLIENTEBANCO, cdcliente);

        resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
        db.close();
    }

    public String selecionarCdClienteBanco(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDCLIENTEBANCO};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);
        String cdclienteBanco = "";
        if(cursor!=null){
            cursor.moveToFirst();
            cdclienteBanco = cursor.getString(cursor.getColumnIndex(CriaBanco.CDCLIENTEBANCO));
        }

        cursor.close();
        db.close();
        return cdclienteBanco;
    }

    public String selecionaVendedor(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDVENDEDORDEFAULT};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);
        String vendedor = "";
        if(cursor!=null){
            cursor.moveToFirst();
            vendedor = cursor.getString(cursor.getColumnIndex(CriaBanco.CDVENDEDORDEFAULT));
        }

        cursor.close();
        db.close();
        return vendedor;
    }

    public Cursor selecionarTipoClienteCursor(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDTIPO, banco.NMTIPO};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELATIPCLIENTE, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }


    public ArrayList<String> selecionarEstado(){
        Cursor cursor;
        String[] campos = {"uf"};
        db = banco.getReadableDatabase();
        cursor = db.query("cadest", campos, null, null, null, null, null);
        ArrayList<String> estados = new ArrayList<String>();
        if(cursor!=null){
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                estados.add(cursor.getString(cursor.getColumnIndex("uf")));
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
        return estados;
    }

    //----------------------------------- Função para seleção da filial na listview da tela de filial ------------------
    public void selecionaFilial(int id){
        ContentValues valores;
        db = banco.getWritableDatabase();

        //where = CriaBanco.ID + "=" + id;

        valores = new ContentValues();
        valores.put(CriaBanco.FGSELECIONADA, "N");

        db.update(CriaBanco.TABELAFILIAL, valores, null, null);
        db.close();

        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + "=" + id;

        valores = new ContentValues();
        valores.put(CriaBanco.FGSELECIONADA, "S");

        resultado = db.update(CriaBanco.TABELAFILIAL, valores, where, null);
        db.close();


    }

    //----------------------- Função para atualizar a listview de filial de acordo com o nome da filial digitado no campo de busca--------
    public Cursor buscaFilialNome(String nome){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDFILIAL, banco.FILIAL};
        String where = CriaBanco.FILIAL + " LIKE '%" + nome + "%'";
        String orderBy = CriaBanco.FILIAL;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, where, null, null, null, orderBy, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

}