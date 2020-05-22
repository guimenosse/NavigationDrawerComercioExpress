package models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

public class MDL_Filial {

    private SQLiteDatabase db;
    private CriaBanco banco;

    //
    public String [] arr_CamposFilial = {banco.ID, banco.CDFILIAL, banco.FILIAL};

    public MDL_Filial(Context context)
    {
        banco = new CriaBanco(context);
    }


    //----------------------- Função para buscar o código da filial selecionada ----------------------------------------------------
    public String fuBuscaCdFilialSelecionada(){
        Cursor cursor;
        String[] campos = arr_CamposFilial;
        String where = CriaBanco.FGSELECIONADA + "= 'S'";
        String vf_CdFilial = "N";
        db = banco.getReadableDatabase();
        cursor = db.query(CriaBanco.TABELAFILIAL, campos, where, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            vf_CdFilial = cursor.getString(cursor.getColumnIndex(CriaBanco.CDFILIAL));
        }else{
            vf_CdFilial = "";
        }
        db.close();

        return vf_CdFilial;
    }
}
