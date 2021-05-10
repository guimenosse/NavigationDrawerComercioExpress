package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import models.CriaBanco;

public class MDL_Clientes {

    private SQLiteDatabase db;
    private CriaBanco banco;

    //
    public String [] arr_CamposCliente = {banco.ID, banco.CDCLIENTE, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO,
            banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ,
            banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE,
            banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE,
            banco.CLASSIFICACAO, banco.INSCESTADUAL, banco.TIPOPRECO, banco.FIDELIDADE, banco.FGBLOQUEIO};

    public MDL_Clientes(Context context)
    {
        banco = new CriaBanco(context);
    }


    //----------------------- Função para inclusão de um novo cliente --------------------------------------
    public boolean fuInserirCliente(String cdcliente, String rzsocial, String nmfantasia, String cep, String endereco,
                                 String numero, String complemento, String bairro, String uf, String cidade,
                                 String cgc, String inscestadual, String telefone, String telefoneadicional,
                                 String fax, String contato, String email, String tipcliente, String vendedor,
                                 String tipopessoa, String dtultalteracao, String dtcadastro, String fgsincronizado,
                                 String obscliente, String classificacao, String fidelidade, String tipopreco, String fgBloqueio)
    {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDCLIENTE, cdcliente);
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
        valores.put(CriaBanco.FGBLOQUEIO, fgBloqueio);

        resultado = db.insert(CriaBanco.TABELA, null, valores);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean fuInserirTipoCliente(String cdTipo, String nmTipo){

        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.CDTIPO, cdTipo);
            valores.put(CriaBanco.NMTIPO, nmTipo);

            db.insert(CriaBanco.TABELATIPCLIENTE, null, valores);
            db.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    //----------------------- Função para exclusão de todos os clientes --------------------------------------
    public boolean fuDeletarTodosClientes(){

        try {
            db = banco.getReadableDatabase();
            db.delete(CriaBanco.TABELA, null, null);
            db.close();

            return true;

        }catch (Exception e){
            return false;
        }

    }

    public boolean fuDeletarTodosTiposCliente(){

        try{
            db = banco.getWritableDatabase();

            db.delete(CriaBanco.TABELATIPCLIENTE, null, null);
            db.close();

            return true;
        }catch (Exception e){
            return false;
        }

    }

    //---------------Função para carregar os dados do cliente pelo seu codigo de cliente---------------------------------
    public Cursor fuCarregaCliente(String cdCliente){
        Cursor cursor;
        String[] campos = arr_CamposCliente;
        String where = CriaBanco.CDCLIENTE + " = '" + cdCliente + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor fuCarregaClienteId(String id){
        Cursor cursor;
        String[] campos = arr_CamposCliente;
        String where = CriaBanco.ID + " = " + id + "";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //---------------Função para carregar os dados do cliente pelo seu cpf ou cnpj ---------------------------------
    public Cursor fuCarregaClienteCPFCNPJ(String cpfCnpj){
        Cursor cursor;
        String[] campos = arr_CamposCliente;
        String where = CriaBanco.CDCLIENTE + " = '" + cpfCnpj + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public boolean fuAlteraClientePedido(String idCliente, String cdCliente){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + " = " + idCliente;

        valores = new ContentValues();
        valores.put(CriaBanco.CDCLIENTE, cdCliente);
        valores.put(CriaBanco.FGSINCRONIZADO, "S");

        resultado = db.update(CriaBanco.TABELA, valores, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor fuBuscaTipoPrecoCliente(String cdCliente){

        Cursor cursor;
        String[] campos = {banco.TIPOPRECO};
        String where = CriaBanco.CDCLIENTE + " = '" + cdCliente + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    //-----------------Função para carregar os dados dos clientes nao sincronizados--------------------------------
    public Cursor fuCarregarClientesNaoSincronizados() {

        Cursor cursor;
        String[] campos = arr_CamposCliente;

        String where = CriaBanco.FGSINCRONIZADO + " = 'N'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELA, campos, where, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

    public void fuIncluirColunaFgBloqueio(){
        String sql = "ALTER TABLE " + CriaBanco.TABELA + " ADD " + CriaBanco.FGBLOQUEIO + " text DEFAULT 'N'";
        db = banco.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

}
