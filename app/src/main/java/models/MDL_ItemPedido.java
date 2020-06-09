package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import models.CriaBanco;

public class MDL_ItemPedido {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public String vc_Mensagem = "";

    public Cursor rs_ItemPedido;

    public String [] arr_CamposItemPedido = {banco.ID, banco.NUMPEDIDO, banco.CDPRODUTO, banco.DESCRICAO, banco.QTDE,
            banco.PERCDESCONTO, banco.VLMAXDESCPERMITIDO, banco.VLDESCONTO, banco.VLUNITARIO,
            banco.VLLIQUIDO, banco.VLTOTAL, banco.OBSERVACAOITEMPEDIDO};

    public MDL_ItemPedido(Context context)
    {
        banco = new CriaBanco(context);
    }

    public boolean fuInserirItemPedido(String numPedido, String cdProduto, String descricao, String qtde, String percDesconto,
                                      String vlDesconto, String vlMaxDescPermitido, String vlUnitario, String vlLiquido, String vlTotal, String observacao){

        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.NUMPEDIDO, numPedido);
        valores.put(CriaBanco.CDPRODUTO, cdProduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.QTDE, qtde);
        valores.put(CriaBanco.PERCDESCONTO, percDesconto);
        valores.put(CriaBanco.VLDESCONTO, vlDesconto);
        valores.put(CriaBanco.VLMAXDESCPERMITIDO, vlMaxDescPermitido);
        valores.put(CriaBanco.VLUNITARIO, vlUnitario);
        valores.put(CriaBanco.VLLIQUIDO, vlLiquido);
        valores.put(CriaBanco.VLTOTAL, vlTotal);
        valores.put(CriaBanco.OBSERVACAOITEMPEDIDO, observacao);

        resultado = db.insert(CriaBanco.TABELAITEMPEDIDO, null, valores);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    //---------------------------Função para alteração dos itens incluidos no pedido -----------------------------------------
    public boolean fuAlterarItemPedido(String numPedido, String cdProduto, String qtde, String percDesconto,
                                       String vlDesconto, String vlUnitario, String vlLiquido, String vlTotal, String observacao){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.NUMPEDIDO + "=" + numPedido + " AND " + CriaBanco.CDPRODUTO + "='" + cdProduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.QTDE, qtde);
        valores.put(CriaBanco.PERCDESCONTO, percDesconto);
        valores.put(CriaBanco.VLDESCONTO, vlDesconto);
        valores.put(CriaBanco.VLLIQUIDO, vlLiquido);
        valores.put(CriaBanco.VLTOTAL, vlTotal);
        valores.put(CriaBanco.VLUNITARIO, vlUnitario);
        valores.put(CriaBanco.OBSERVACAOITEMPEDIDO, observacao);

        resultado = db.update(CriaBanco.TABELAITEMPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean fuDeletarItemPedido(String numPedido, String cdProduto){

        String where = CriaBanco.NUMPEDIDO + "=" + numPedido + " AND " + CriaBanco.CDPRODUTO + "='" + cdProduto + "'";
        long resultado;

        db = banco.getReadableDatabase();
        resultado = db.delete(CriaBanco.TABELAITEMPEDIDO, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    //Função para duplicação dos itens de um pedido que foi duplicado
    public boolean fuDuplicarItensPedido(String numPedido, String cdProduto, String descricao, String qtde,
                                         String percDesconto, String vlDesconto, String vlMaxDescPermitido,
                                         String vlUnitario, String vlLiquido, String vlTotal, String observacao){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.NUMPEDIDO, numPedido);
        valores.put(CriaBanco.CDPRODUTO, cdProduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.QTDE, qtde);
        valores.put(CriaBanco.PERCDESCONTO, percDesconto);
        valores.put(CriaBanco.VLDESCONTO, vlDesconto);
        valores.put(CriaBanco.VLMAXDESCPERMITIDO, vlMaxDescPermitido);
        valores.put(CriaBanco.VLUNITARIO, vlUnitario);
        valores.put(CriaBanco.VLLIQUIDO, vlLiquido);
        valores.put(CriaBanco.VLTOTAL, vlTotal);
        valores.put(CriaBanco.OBSERVACAOITEMPEDIDO, observacao);

        resultado = db.insert(CriaBanco.TABELAITEMPEDIDO, null, valores);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }
    }

    //---------------------------Função para carregamento dos itens do pedido -----------------------------------------
    public Cursor fuCarregaItemPedido(String numPedido){
        Cursor cursor;
        String[] campos = arr_CamposItemPedido;
        String where = CriaBanco.NUMPEDIDO + " = " + numPedido;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //------------------------ Função para verificar se já foi incluido algum produto no pedido -------------------------
    public String fuVerificaItemPedido(String numPedido){

        Cursor cursor;
        String validou = "N";
        String[] campos = {CriaBanco.ID};
        String where = CriaBanco.NUMPEDIDO + "=" + numPedido;
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
    public  Cursor fuCarregaProdutoItemPedido(String numpedido, String cdproduto){
        Cursor cursor;
        String[] campos = arr_CamposItemPedido;
        String where = CriaBanco.NUMPEDIDO + "=" + numpedido + " AND " + CriaBanco.CDPRODUTO + "= '" + cdproduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //---------------------------- Função para carregar os dados do item do pedido para alteração -----------------------
    public Cursor fuCarregaItemPedidoAlteracao(String numpedido, String codigo){
        Cursor cursor;
        String[] campos = arr_CamposItemPedido;
        String where = CriaBanco.ID + "=" + codigo + " AND " + CriaBanco.NUMPEDIDO + "=" + numpedido;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAITEMPEDIDO, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }
}
