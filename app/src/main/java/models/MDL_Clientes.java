package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

public class MDL_Clientes {

    private SQLiteDatabase db;
    private CriaBanco banco;

    //
    public String [] arr_CamposCliente = {banco.ID, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO,
            banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ,
            banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE,
            banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE,
            banco.CLASSIFICACAO, banco.INSCESTADUAL, banco.TIPOPRECO, banco.FIDELIDADE};

    public MDL_Clientes(Context context)
    {
        banco = new CriaBanco(context);
    }


    //---------------Função para carregar os dados do cliente pelo seu codigo de cliente---------------------------------
    public Cursor fuCarregaCliente(String cdCliente){
        Cursor cursor;
        String[] campos = arr_CamposCliente;
        String where = CriaBanco.CDCLIENTE + " = " + cdCliente + "";
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
}
