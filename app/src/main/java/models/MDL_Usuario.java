package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import classes.CL_Usuario;
import models.CriaBanco;

public class MDL_Usuario {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public String [] arr_CamposUsuario = {banco.ID, banco.USUARIOLOGIN, banco.SENHALOGIN, banco.CDVENDEDORDEFAULT,
            banco.NMUSUARIOSISTEMA, banco.CDCLIENTEBANCO};

    public MDL_Usuario(Context context)
    {
        banco = new CriaBanco(context);
    }

    public boolean fuInserirLogin(String usuario, String senha){

        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

            db.delete(banco.TABELALOGIN, null, null);
            db.close();

            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(banco.USUARIOLOGIN, usuario);
            valores.put(banco.SENHALOGIN, senha);

            long resultado = db.insert(banco.TABELALOGIN, null, valores);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuSalvarUsuarioAPI(CL_Usuario usuario){

        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

            db.delete(banco.TABELALOGIN, null, null);
            db.close();

            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(banco.CDUSUARIO, usuario.getCdUsuario());
            valores.put(banco.USUARIOLOGIN, usuario.getUsuario());
            valores.put(banco.SENHALOGIN, usuario.getSenha());
            valores.put(banco.CDVENDEDORDEFAULT, usuario.getCdVendedorDefault());
            valores.put(banco.NMUSUARIOSISTEMA, usuario.getNmUsuarioSistema());
            valores.put(banco.CDCLIENTEBANCO, usuario.getCdClienteBanco());
            valores.put(banco.IP, usuario.getIp());
            valores.put(banco.USUARIOSQL, usuario.getUsuarioSQL());
            valores.put(banco.SENHASQL, usuario.getSenhaSQL());
            valores.put(banco.NMBANCO, usuario.getNmBanco());

            long resultado = db.insert(banco.TABELALOGIN, null, valores);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            return false;
        }
    }

    public Cursor fuSelecionarUsuarioAPI() {
        Cursor cursor;
        String[] campos = {banco.CDUSUARIO, banco.USUARIOLOGIN, banco.SENHALOGIN, banco.CDVENDEDORDEFAULT, banco.NMUSUARIOSISTEMA, banco.CDCLIENTEBANCO, banco.IP, banco.USUARIOSQL, banco.SENHASQL, banco.NMBANCO};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELALOGIN, campos, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

    public boolean fuInserirCdClienteBanco(String cdCliente){
        try{
            ContentValues valores;
            long resultado;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.CDCLIENTEBANCO, cdCliente);

            resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            return false;
        }

    }

    public boolean fuInserirNmUsuarioSistema(String usuarioSistema){
        try {
            ContentValues valores;
            long resultado;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.NMUSUARIOSISTEMA, usuarioSistema);

            resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public boolean fuInserirCdVendedorDefault(String cdvendedor){

        try {
            ContentValues valores;
            long resultado;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.CDVENDEDORDEFAULT, cdvendedor);

            resultado = db.update(CriaBanco.TABELALOGIN, valores, null, null);
            db.close();

            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            return false;
        }

    }

    public Cursor fuSelecionarUsuario() {
        Cursor cursor;
        String[] campos = {banco.USUARIOLOGIN};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELALOGIN, campos, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }
    public Cursor fuSelecionarCdClienteBanco(){

        Cursor cursor;
        String[] campos = {banco.ID, banco.CDCLIENTEBANCO};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);

        if(cursor!=null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public Cursor fuSelecionarNmUsuarioSistema(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.NMUSUARIOSISTEMA};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELALOGIN, campos, null, null, null, null, null);

        if(cursor!=null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
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

    public Cursor fuSelecionarDtSincronizacao(){
        Cursor cursor;
        String[] campos = {banco.DTULTSINCRONIZACAO};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELASINCRONIZACAO, campos, null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }

        db.close();

        return cursor;
    }

    public boolean fuAtualizarSincronizacao(String data){
        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

            db.delete(banco.TABELASINCRONIZACAO, null, null);

            valores = new ContentValues();
            valores.put(banco.DTULTSINCRONIZACAO, data);
            valores.put(banco.ULTDTATUALIZACAO, data);

            db.insert(banco.TABELASINCRONIZACAO, null, valores);
            db.close();

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
