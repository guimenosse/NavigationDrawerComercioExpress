package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

public class MDL_Pedidos {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public String vc_Mensagem = "";

    public Cursor rs_Pedido;


    public String [] arr_CamposPedido = {banco.ID, banco.CONDPGTO, banco.VLTOTAL, banco.PERCDESCONTO, banco.VLDESCONTO,
            banco.VLFRETE, banco.CDEMITENTE, banco.RZSOCIAL, banco.OBS, banco.DTEMISSAO,
            banco.FGSITUACAO, banco.CDVENDEDOR};

    public MDL_Pedidos(Context context)
    {
        banco = new CriaBanco(context);
    }

    public Cursor fuCarregaNumPedido(){
        Cursor cursor;
        String[] campos = {banco.ID};
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAMESTREPEDIDO, campos, null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public boolean fuAbrirPedido(String cdCliente, String nomeRzSocial, String dtEmissao, String cdVendedor, String fgSituacao){

        try {
            ContentValues valores;
            long resultado;

            db = banco.getWritableDatabase();
            valores = new ContentValues();
            valores.put(CriaBanco.CDEMITENTE, cdCliente);
            valores.put(CriaBanco.RZSOCIAL, nomeRzSocial);
            valores.put(CriaBanco.DTEMISSAO, dtEmissao);
            valores.put(CriaBanco.CDVENDEDOR, cdVendedor);
            valores.put(CriaBanco.FGSITUACAO, fgSituacao);
            valores.put(CriaBanco.CONDPGTO, "");
            valores.put(CriaBanco.VLTOTAL, "0.00");
            valores.put(CriaBanco.PERCDESCONTO, "0.00");
            valores.put(CriaBanco.VLDESCONTO, "0.00");
            valores.put(CriaBanco.VLFRETE, "0.00");
            valores.put(CriaBanco.OBS, "");

            resultado = db.insertOrThrow(CriaBanco.TABELAMESTREPEDIDO, null, valores);
            db.close();

            if (resultado == -1) {
                return false;
            } else {
                return true;
            }
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }

    }


    public boolean fuAlterarPedido(String numPedido, String cdCliente, String nomeRzSocial, String condPgto, String vlTotal,
                                 String percDesconto, String vlDesconto, String vlFrete, String obsPedido, String fgSituacao){
        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.ID + " = " + numPedido;

        valores = new ContentValues();
        valores.put(CriaBanco.CDEMITENTE, cdCliente);
        valores.put(CriaBanco.RZSOCIAL, nomeRzSocial);
        valores.put(CriaBanco.CONDPGTO, condPgto);
        valores.put(CriaBanco.VLTOTAL, vlTotal);
        valores.put(CriaBanco.PERCDESCONTO, percDesconto);
        valores.put(CriaBanco.VLDESCONTO, vlDesconto);
        valores.put(CriaBanco.VLFRETE, vlFrete);
        valores.put(CriaBanco.OBS, obsPedido);
        valores.put(CriaBanco.FGSITUACAO, fgSituacao);


        resultado = db.update(CriaBanco.TABELAMESTREPEDIDO, valores, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean fuDuplicarPedido(String cdCliente, String nomeRzSocial, String dtEmissao, String cdVendedor,
                                    String condPgto, String vlTotal,
                                    String percDesconto, String vlDesconto, String vlFrete, String obsPedido, String fgSituacao){

        try {
            ContentValues valores;
            long resultado;

            db = banco.getWritableDatabase();
            valores = new ContentValues();
            valores.put(CriaBanco.CDEMITENTE, cdCliente);
            valores.put(CriaBanco.RZSOCIAL, nomeRzSocial);
            valores.put(CriaBanco.DTEMISSAO, dtEmissao);
            valores.put(CriaBanco.CDVENDEDOR, cdVendedor);
            valores.put(CriaBanco.CONDPGTO, condPgto);
            valores.put(CriaBanco.VLTOTAL, vlTotal);
            valores.put(CriaBanco.PERCDESCONTO, percDesconto);
            valores.put(CriaBanco.VLDESCONTO, vlDesconto);
            valores.put(CriaBanco.VLFRETE, vlFrete);
            valores.put(CriaBanco.OBS, obsPedido);
            valores.put(CriaBanco.FGSITUACAO, "ABERTO");

            resultado = db.insertOrThrow(CriaBanco.TABELAMESTREPEDIDO, null, valores);
            db.close();

            if (resultado == -1) {
                return false;
            } else {
                return true;
            }
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }

    }

    public boolean fuAlterarSituacaoPedido(String numpedido, String fgsituacao){
        try {
            ContentValues valores;
            String where;
            long resultado;
            db = banco.getWritableDatabase();

            where = CriaBanco.ID + "=" + numpedido;

            valores = new ContentValues();
            valores.put(CriaBanco.FGSITUACAO, fgsituacao);

            resultado = db.update(CriaBanco.TABELAMESTREPEDIDO, valores, where, null);
            db.close();

            if (resultado == -1) {
                return false;
            } else {
                return true;
            }
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public boolean fuDeletarPedido(String id){
        try {
            String where = CriaBanco.ID + "=" + id;
            db = banco.getReadableDatabase();
            db.delete(CriaBanco.TABELAMESTREPEDIDO, where, null);
            db.close();

            where = CriaBanco.NUMPEDIDO + "=" + id;
            db = banco.getReadableDatabase();
            db.delete(CriaBanco.TABELAITEMPEDIDO, where, null);
            db.close();

            return true;
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public Cursor fuCarregarTodosPedidos(String nomeRazaoSocial){
        Cursor cursor;
        String [] vf_Campos = arr_CamposPedido;

        String where = "";
        if (!nomeRazaoSocial.trim().equals("")){
            where += CriaBanco.RZSOCIAL + " LIKE '%" + nomeRazaoSocial + "%'";
        }

        String orderBy = banco.ID;

        db = banco.getReadableDatabase();
        if(where.trim().equals("")) {
            cursor = db.query(banco.TABELAMESTREPEDIDO, vf_Campos, null, null, null, null, orderBy);
        }else{
            cursor = db.query(banco.TABELAMESTREPEDIDO, vf_Campos, where, null, null, null, orderBy);
        }

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

    public Cursor fuCarregaTodosPedidosEnviados(){
        Cursor cursor;
        String[] campos = {banco.ID, banco.FGSITUACAO, banco.RZSOCIAL};
        String where = CriaBanco.FGSITUACAO + " = 'ENVIADO'";
        String orderBy = CriaBanco.ID;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor fuCarregaPedido(String numPedido){
        Cursor cursor;
        String[] campos = arr_CamposPedido;
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.ID + " = " + numPedido;
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();

        return cursor;

    }

    public Cursor fuCarregaPedidosAbertos() {

        Cursor cursor;
        String[] campos = arr_CamposPedido;
        String orderBy = CriaBanco.ID;
        String where = CriaBanco.FGSITUACAO + " = 'ABERTO'";
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, orderBy, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

    public boolean fu_ValidarPossuiPedido(){
        boolean fgEncontrou = false;
        Cursor cursor;
        String [] campos = {banco.ID};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, campos, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount() > 0) {
                fgEncontrou = true;
            }
        }
        db.close();

        return fgEncontrou;
    }

    public boolean fu_BuscarMovimentacaoDiaria(String data){

        boolean validou = false;
        try {
            String[] campos = {banco.ID, banco.DTEMISSAO, banco.VLDESCONTO, banco.VLTOTAL};
            db = banco.getReadableDatabase();
            String where = "substr(" + banco.DTEMISSAO + ", 1, 10)" + " = '" + data + "'";
            rs_Pedido = db.query(banco.TABELAMESTREPEDIDO, campos, where, null, null, null, null);

            if(rs_Pedido != null) {
                if (rs_Pedido.getCount() > 0) {
                    validou = true;
                }else{
                    vc_Mensagem = "Não foram encontradas movimentações no dia " + data + "";
                }
            }else{
                vc_Mensagem = "Não foram encontradas movimentações no dia " + data + "";
            }

            db.close();

        }catch (Exception e){
            vc_Mensagem = "Não foi possível carregar os dados da movimentação do dia " + data + " devido à seguinte situação: " + e.getMessage();
            return false;
        }

        return validou;
        //return String.valueOf(valor);
    }

    public Cursor fu_SelecionarTodosPedidos(){
        Cursor cursor;
        String [] vf_Campos = arr_CamposPedido;
        String orderBy = banco.ID;

        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELAMESTREPEDIDO, vf_Campos, null, null, null, null, orderBy);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

    public Cursor fuCarregarClientePedido(String numPedido){

        Cursor cursor;
        String[] campos = {banco.ID, banco.CDEMITENTE};
        String where = CriaBanco.ID + " = " + numPedido;

        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAMESTREPEDIDO, campos, where, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;

    }

}
