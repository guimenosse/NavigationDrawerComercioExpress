package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import models.CriaBanco;

public class MDL_Filial {

    private SQLiteDatabase db;
    private CriaBanco banco;

    //
    public String [] arr_CamposFilial = {banco.ID, banco.CDFILIAL, banco.FILIAL, banco.FGSELECIONADA, banco.FGTROCAFILIAL, banco.FGPRECOINDIVIDUALIZADO};

    public MDL_Filial(Context context)
    {
        banco = new CriaBanco(context);
    }


    //--------------------------------Função para inserir uma nova Filial na sincronização ----------------------------
    public boolean fuInserirFilial(String cdFilial, String nmFilial, String fgSelecionada, String fgTrocaFilial){

        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.CDFILIAL, cdFilial);
            valores.put(CriaBanco.FILIAL, nmFilial);
            valores.put(CriaBanco.FGSELECIONADA, fgSelecionada);
            valores.put(CriaBanco.FGTROCAFILIAL, fgTrocaFilial);

            long resultado = db.insert(CriaBanco.TABELAFILIAL, null, valores);
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


    //---------------------------- Função para limpar a tabela de filial para sincronização das filiais ------------------
    public boolean fuDeletarFilial(){

        try{

            db = banco.getReadableDatabase();
            db.delete(CriaBanco.TABELAFILIAL, null, null);
            db.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    //----------------------------------- Função para seleção da filial na listview da tela de filial ------------------
    public boolean fuSelecionarFilial(String id){

        try {
            ContentValues valores;
            db = banco.getWritableDatabase();

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

            return true;

        }catch (Exception e){
            return false;
        }

    }


    //----------------------- Função para buscar o código da filial selecionada ----------------------------------------------------
    public Cursor fuBuscaCdFilialSelecionada(){
        Cursor cursor;
        String[] campos = arr_CamposFilial;
        String where = CriaBanco.FGSELECIONADA + "= 'S'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, where, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public Cursor fuBuscarFiliais(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FILIAL, banco.FGSELECIONADA};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, null, null, null, null, null);
        if(cursor!=null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    //------------------------------- Função para verificar se o vendedor possui autorização para troca de filial ------------------
    public Cursor fuVerificaPermissaoTrocaFilial(){
        Cursor cursor;
        String[] campos = arr_CamposFilial;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    //----------------------------- Função para verificar quantas filiais foram sincronizadas, caso só tenha uma
    //----------------------------então essa filial será selecionada automaticamente -------------------------------------------
    public Cursor fuCountFiliais(){
        Cursor cursor;
        String[] campos = arr_CamposFilial;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public Cursor fuBuscarConfiguracaoPrecoIndividualizado(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FGPRECOINDIVIDUALIZADO};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public boolean fuAtualizarPrecoIndividualizado(String fgPrecoIndividualizado){
        try{
            ContentValues valores;
            String where;
            long resultado;
            db = banco.getWritableDatabase();


            valores = new ContentValues();
            valores.put(CriaBanco.FGPRECOINDIVIDUALIZADO, fgPrecoIndividualizado);

            resultado = db.update(CriaBanco.TABELAFILIAL, valores, null, null);
            db.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    //-------------------- Função para atualizar se o vendedor possui ou não autorização para a troca de filial -----------------------
    public boolean fuAtualizarAutorizaTrocaFilial(String fgTrocaFilial){

        try{

            ContentValues valores;
            long resultado;
            db = banco.getWritableDatabase();

            valores = new ContentValues();
            valores.put(CriaBanco.FGTROCAFILIAL, fgTrocaFilial);

            resultado = db.update(CriaBanco.TABELAFILIAL, valores, null, null);
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
}
