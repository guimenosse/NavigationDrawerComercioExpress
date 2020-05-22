package models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

public class MDL_Produtos {


    private SQLiteDatabase db;
    private CriaBanco banco;

    protected String vc_CdClienteBanco = "";

    public String vc_Mensagem = "";

    public Cursor rs_Produto;

    /*sql = "CREATE TABLE " + TABELAPRODUTOS + "(" +
                ID + " integer primary key autoincrement, " +
                CDPRODUTO + " text, " +
                DESCRICAO + " text, " +
                ESTOQUEATUAL + " integer, " +
                VALORUNITARIO + " real, " +
                DESCMAXPERMITIDO + " real, " +
                DESCMAXPERMITIDOA + " real, " +
                DESCMAXPERMITIDOB + " real, " +
                DESCMAXPERMITIDOC + " real, " +
                DESCMAXPERMITIDOD + " real, " +
                DESCMAXPERMITIDOE + " real, " +
                DESCMAXPERMITIDOFIDELIDADE + " real, " +
                DTULTALTERACAO + " datetime)";

        db.execSQL(sql);*/

    public String [] arr_CamposProduto = {banco.ID, banco.CDPRODUTO, banco.DESCRICAO, banco.ESTOQUEATUAL, banco.VALORUNITARIO,
            banco.VALORATACADO,
            banco.DESCMAXPERMITIDO, banco.DESCMAXPERMITIDOA, banco.DESCMAXPERMITIDOB, banco.DESCMAXPERMITIDOC,
            banco.DESCMAXPERMITIDOD, banco.DESCMAXPERMITIDOE, banco.DESCMAXPERMITIDOFIDELIDADE,
            banco.DTULTALTERACAO};

    public MDL_Produtos(Context context)
    {
        banco = new CriaBanco(context);
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

    public Cursor buscaDescontoFidelidade(String cdproduto){
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
}
