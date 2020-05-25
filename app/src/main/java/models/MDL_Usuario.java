package models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

public class MDL_Usuario {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public MDL_Usuario(Context context)
    {
        banco = new CriaBanco(context);
    }

    public String fuSelecionarCdClienteBanco(){
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

    public String fuSelecionarNmUsuarioSistema(){
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

    public String fuSelecionarFilial(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FILIAL};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, null, null, null, null, null);
        String vf_Filial = "";
        if(cursor!=null){
            cursor.moveToFirst();
            vf_Filial = cursor.getString(cursor.getColumnIndex(CriaBanco.FILIAL));
        }

        cursor.close();
        db.close();
        return vf_Filial;
    }

    public Cursor fuSelecionarVendedor(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDVENDEDORDEFAULT};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }

        db.close();

        return cursor;
    }
}
