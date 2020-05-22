package controllers;

import android.content.Context;
import android.database.Cursor;

import classes.CL_Filial;
import models.MDL_Filial;

public class CTL_Filial {

    CL_Filial cl_Filial;
    MDL_Filial mdl_Filial;

    Context cxt_Filial;

    public Cursor rs_Filial;

    public CTL_Filial(Context cxt_Filial, CL_Filial cl_Filial){

        this.cl_Filial = cl_Filial;
        this.cxt_Filial = cxt_Filial;
        this.mdl_Filial = new MDL_Filial(this.cxt_Filial);

    }

    public String fuBuscaCdFilialSelecionada(){
        try{

            cl_Filial.setCdFilial(mdl_Filial.fuBuscaCdFilialSelecionada());

            return cl_Filial.getCdFilial();

        }catch (Exception e){
            return "";
        }
    }
}
