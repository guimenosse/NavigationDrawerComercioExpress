package controllers;

import android.content.Context;
import android.database.Cursor;

import classes.CL_Filial;
import models.CriaBanco;
import models.MDL_Filial;

public class CTL_Filial {

    public CL_Filial cl_Filial;
    MDL_Filial mdl_Filial;

    Context cxt_Filial;

    public Cursor rs_Filial;

    public CTL_Filial(Context cxt_Filial, CL_Filial cl_Filial){

        this.cl_Filial = cl_Filial;
        this.cxt_Filial = cxt_Filial;
        this.mdl_Filial = new MDL_Filial(this.cxt_Filial);

        fuBuscaCdFilialSelecionada();
        fuBuscaNmFilialSelecionada();
        fuVerificaPermissaoTrocaFilial();
        fuCountFiliais();
        fuBuscarConfiguracaoPrecoIndividualizado();
    }

    public boolean fuSelecionarFilial(){
        if(mdl_Filial.fuSelecionarFilial(cl_Filial.getId())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuBuscarFiliais(){
        try{
            rs_Filial = mdl_Filial.fuBuscarFiliais();

            if(rs_Filial!=null){
                if(rs_Filial.getCount() > 0){
                    rs_Filial.moveToFirst();
                    return true;
                }else{
                    return false;
                }

            }

            rs_Filial.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuBuscaCdFilialSelecionada(){
        try{

            rs_Filial = mdl_Filial.fuBuscaCdFilialSelecionada();

            if(rs_Filial!=null){
                if(rs_Filial.getCount() > 0){
                    rs_Filial.moveToFirst();
                    if(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.FGSELECIONADA)).equals("S")) {
                        cl_Filial.setCdFilial(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.CDFILIAL)));
                    }
                }else{
                    cl_Filial.setCdFilial(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.CDFILIAL)));
                }

            }

            rs_Filial.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuBuscaNmFilialSelecionada(){
        try{
            rs_Filial = mdl_Filial.fuBuscarFiliais();

            if(rs_Filial!=null){
                if(rs_Filial.getCount() > 0){
                    rs_Filial.moveToFirst();
                    while (!rs_Filial.isAfterLast()) {
                        if (rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.FGSELECIONADA)).equals("S")) {
                            cl_Filial.setNomeFilial(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.FILIAL)));
                        }
                        rs_Filial.moveToNext();
                    }
                }else{
                    cl_Filial.setNomeFilial(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.FILIAL)));
                }

            }

            rs_Filial.close();

            return true;

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuVerificaPermissaoTrocaFilial(){

        try{
            rs_Filial = mdl_Filial.fuVerificaPermissaoTrocaFilial();
            if(rs_Filial!=null) {
                if(rs_Filial.getCount() > 0){
                    rs_Filial.moveToFirst();
                    if (rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.FGTROCAFILIAL)).equals("S")) {
                        cl_Filial.setAutorizaTrocaFilial("S");
                    } else {
                        cl_Filial.setAutorizaTrocaFilial("N");
                    }
                }else{
                    cl_Filial.setAutorizaTrocaFilial("N");
                }

            }else{
                cl_Filial.setAutorizaTrocaFilial("N");
            }
            return true;
        }catch (Exception e){
            cl_Filial.setAutorizaTrocaFilial("N");
            return false;
        }

    }

    public boolean fuCountFiliais(){

        try {
            rs_Filial = mdl_Filial.fuCountFiliais();

            if (rs_Filial != null) {
                if (rs_Filial.getCount() > 0) {
                    rs_Filial.moveToFirst();
                    while (!rs_Filial.isAfterLast()) {
                        cl_Filial.setTotalFiliais(cl_Filial.getTotalFiliais() + 1);
                        rs_Filial.moveToNext();
                    }
                } else {
                    cl_Filial.setTotalFiliais(0);
                }

            } else {
                cl_Filial.setTotalFiliais(0);
            }
            return true;
        }catch (Exception e){
            cl_Filial.setTotalFiliais(0);
            return false;
        }

    }

    public boolean fuBuscarConfiguracaoPrecoIndividualizado(){

        try{
            rs_Filial = mdl_Filial.fuBuscarConfiguracaoPrecoIndividualizado();

            if (rs_Filial != null) {
                if (rs_Filial.getCount() > 0) {
                    rs_Filial.moveToFirst();
                    while (!rs_Filial.isAfterLast()) {
                        cl_Filial.setPrecoIndividualizado(rs_Filial.getString(rs_Filial.getColumnIndex(CriaBanco.PRECOINDIVIDUALIZADO)));
                        rs_Filial.moveToNext();
                    }
                } else {
                    cl_Filial.setPrecoIndividualizado("N");
                }

            } else {
                cl_Filial.setPrecoIndividualizado("N");
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean fuAtualizarPrecoIndividualizado(String fgPrecoIndividualizado){
        try{

            if(mdl_Filial.fuAtualizarPrecoIndividualizado(fgPrecoIndividualizado)){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
}
