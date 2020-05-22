package controllers;

import android.content.Context;
import android.database.Cursor;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

import classes.CL_Usuario;
import models.MDL_Usuario;

public class CTL_Usuario {

    CL_Usuario cl_Usuario;
    MDL_Usuario mdl_Usuario;

    Context cxt_Usuario;

    public Cursor rs_Usuario;

    public CTL_Usuario(Context cxt_Usuario, CL_Usuario cl_Usuario){

        this.cl_Usuario = cl_Usuario;
        this.cxt_Usuario = cxt_Usuario;
        this.mdl_Usuario = new MDL_Usuario(this.cxt_Usuario);

    }

    public String fuSelecionarVendedor(){

        String vf_CdVendedor = "";

        rs_Usuario = mdl_Usuario.fuSelecionarVendedor();

        if(rs_Usuario.getCount() > 0){
            vf_CdVendedor = rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDVENDEDORDEFAULT));
        }else{
            vf_CdVendedor = "";
        }
        rs_Usuario.close();

        return vf_CdVendedor;
    }
}
