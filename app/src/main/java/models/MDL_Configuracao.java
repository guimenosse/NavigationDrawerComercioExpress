package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MDL_Configuracao {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public String vc_Mensagem = "";
    //
    public String [] arr_CamposConfiguracao = {banco.FGCONTROLAESTOQUEPEDIDO, banco.FGPRECOINDIVIDUALIZADO};

    public MDL_Configuracao(Context context)
    {
        banco = new CriaBanco(context);
    }

    public boolean fuAtualizarConfiguracoes(String fgControlaEstoquePedido, String fgPrecoIndividualizado){
        try {
            ContentValues valores;
            long resultado;

            db = banco.getWritableDatabase();
            valores = new ContentValues();
            valores.put(CriaBanco.FGCONTROLAESTOQUEPEDIDO, fgControlaEstoquePedido);
            valores.put(CriaBanco.FGPRECOINDIVIDUALIZADO, fgPrecoIndividualizado);

            resultado = db.update(CriaBanco.TABELACONFIGURACAO, valores, null, null);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public Cursor fuCarregarConfiguracoes(){
        Cursor cursor;
        String[] campos = arr_CamposConfiguracao;

        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELACONFIGURACAO, campos, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public boolean fuAtualizarFgControlaEstoquePedido(String fgControlaEstoquePedido){

        try {
            ContentValues valores;
            long resultado;

            db = banco.getWritableDatabase();
            valores = new ContentValues();
            valores.put(CriaBanco.FGCONTROLAESTOQUEPEDIDO, fgControlaEstoquePedido);

            resultado = db.update(CriaBanco.TABELACONFIGURACAO, valores, null, null);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }

    }

    public Cursor fuCarregarFgControlaEstoquePedido(){
        Cursor cursor;
        String[] campos = arr_CamposConfiguracao;

        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELACONFIGURACAO, campos, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

}
