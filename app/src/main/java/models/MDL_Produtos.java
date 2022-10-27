package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import models.CriaBanco;

public class MDL_Produtos {


    private SQLiteDatabase db;
    private CriaBanco banco;

    protected String vc_CdClienteBanco = "";

    public String vc_Mensagem = "";

    public Cursor rs_Produto;

    public String [] arr_CamposProduto = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.COMPLEMENTODESCRICAO, banco.ESTOQUEATUAL, banco.VALORUNITARIO,
            banco.VALORATACADO,
            banco.DESCMAXPERMITIDO, banco.DESCMAXPERMITIDOA, banco.DESCMAXPERMITIDOB, banco.DESCMAXPERMITIDOC,
            banco.DESCMAXPERMITIDOD, banco.DESCMAXPERMITIDOE, banco.DESCMAXPERMITIDOFIDELIDADE, banco.QTDEDISPONIVEL,
            banco.CDREFESTOQUE,
            banco.DTULTALTERACAO};

    public MDL_Produtos(Context context)
    {
        banco = new CriaBanco(context);
    }


    public boolean fuInserirProdutoFilial(String cdproduto, String descricao, String complementodescricao, String estoqueatual, String valorunitario,
                                       String valoratacado,
                                       String dtultalteracao, String maxdescpermitido, String maxdescpermitidoa,
                                       String maxdescpermitidob, String maxdescpermitidoc, String maxdescpermitidod,
                                       String maxdescpermitidoe, String maxdescpermitidofidelidade, String qtdeDisponivel, String cdRefEstoque){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.CDPRODUTO, cdproduto);
        valores.put(CriaBanco.DESCRICAO, descricao);
        valores.put(CriaBanco.COMPLEMENTODESCRICAO, complementodescricao);
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
        valores.put(CriaBanco.QTDEDISPONIVEL, qtdeDisponivel);
        valores.put(CriaBanco.CDREFESTOQUE, cdRefEstoque);

        resultado = db.insert(CriaBanco.TABELAPRODUTOS, null, valores);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean fuAtualizarValorUnitarioFilial(String cdProduto, String valorUnitario){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdProduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.VALORUNITARIO, valorUnitario);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean fuAtualizarQtdeDisponivel(String cdProduto, String qtdeDisponivel){

        ContentValues valores;
        String where;
        long resultado;
        db = banco.getWritableDatabase();

        where = CriaBanco.CDPRODUTO + "='" + cdProduto + "'";

        valores = new ContentValues();
        valores.put(CriaBanco.QTDEDISPONIVEL, qtdeDisponivel);

        resultado = db.update(CriaBanco.TABELAPRODUTOS, valores, where, null);
        db.close();

        if(resultado == -1){
            return false;
        }else{
            return true;
        }

    }

    //------------------------------ Função que limpa todos os produtos para a sincronização -------------------------------------
    public boolean fuDeletarTodosProdutos(){

        try {

            db = banco.getReadableDatabase();
            db.delete(CriaBanco.TABELAPRODUTOS, null, null);
            db.close();

            return true;
        }catch (Exception e){
            return false;
        }

    }

    //FUNÇÃO PARA A CLASSE DE ESTOQUE
    public int fu_BuscarProdutosAtencao(){
        int countProdutos = 0;
        vc_Mensagem = "";

        try {
            Cursor cursor;
            String[] campos = {banco.ESTOQUEATUAL};
            db = banco.getReadableDatabase();

            cursor = db.query(banco.TABELAPRODUTOS, campos, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        String estoqueAtual = cursor.getString(cursor.getColumnIndex(CriaBanco.ESTOQUEATUAL));
                        if(estoqueAtual.trim().equals("")){
                            countProdutos += 1;
                        }else {
                            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(CriaBanco.ESTOQUEATUAL))) <= 0) {
                                countProdutos += 1;
                            }
                        }
                        cursor.moveToNext();
                    }

                }else{
                    vc_Mensagem = "";
                }
            }else{
                vc_Mensagem = "";
            }

            db.close();

        }catch (Exception e){
            vc_Mensagem = "Não foi possível buscar os produtos que necessitam de atenção devido à seguinte situação: " + e.getMessage() + "";
        }

        vc_Mensagem = "";

        return countProdutos;
    }

    //-------------------- Função para carregamento do produto de acordo com seu id ----------------------------------------
    public Cursor fuCarregaProdutosById(String id){
        Cursor cursor;
        String[] campos = arr_CamposProduto;
        String where = CriaBanco.ID + " = " + id;
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    //-------------------- Função para carregamento do produto de acordo com seu id ----------------------------------------
    public Cursor fuCarregaProdutosCdProduto(String cdProduto){
        Cursor cursor;
        String[] campos = arr_CamposProduto;
        String where = CriaBanco.CDPRODUTO + "= '" + cdProduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor fuBuscaValorAtacado(String cdProduto){
        Cursor cursor;
        String[] campos = {banco.VALORATACADO};
        String where = CriaBanco.CDPRODUTO + "='" + cdProduto + "'";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();

        return cursor;
    }

    public Cursor fuBuscaDescontoFidelidade(String cdproduto){
        Cursor cursor;
        String[] campos = {banco.ID, banco.DESCMAXPERMITIDOFIDELIDADE};
        String where = CriaBanco.CDPRODUTO + "='" + cdproduto + "'";
        String cdcliente = "";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor fuBuscaCdRefEstoque(String cdProduto){
        Cursor cursor;
        String[] campos = {banco.ID, banco.CDREFESTOQUE};
        String where = CriaBanco.CDPRODUTO + "='" + cdProduto + "'";
        String cdcliente = "";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, where, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Boolean adicionarColunaComplementoDescricao(){
        try{
            String sql = "ALTER TABLE " + banco.TABELAPRODUTOS + " ADD COLUMN " + banco.COMPLEMENTODESCRICAO + " text ";
            db = banco.getReadableDatabase();
            db.execSQL(sql);

            return true;
        }catch (Exception e){
            Cursor cursor;
            String[] campos = {banco.COMPLEMENTODESCRICAO};
            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELAPRODUTOS, campos, null, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
                return true;
            }else{
                return false;
            }
        }



    }
}
