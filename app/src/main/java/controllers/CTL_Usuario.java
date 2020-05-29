package controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import models.CriaBanco;

import classes.CL_Usuario;
import models.MDL_Usuario;

public class CTL_Usuario {

    public CL_Usuario cl_Usuario;
    MDL_Usuario mdl_Usuario;

    Context cxt_Usuario;

    public Cursor rs_Usuario;

    public CTL_Usuario(Context cxt_Usuario, CL_Usuario cl_Usuario){

        this.cl_Usuario = cl_Usuario;
        this.cxt_Usuario = cxt_Usuario;
        this.mdl_Usuario = new MDL_Usuario(this.cxt_Usuario);

        fuSelecionarUsuario();
        fuSelecionarCdClienteBanco();
        fuSelecionarNmUsuarioSistema();
        fuSelecionarVendedor();
        fuSelecionarDtSincronizacao();
    }


    public boolean fuSelecionarUsuario(){

        try {
            rs_Usuario = mdl_Usuario.fuSelecionarUsuario();

            if (rs_Usuario != null) {
                rs_Usuario.moveToFirst();
                cl_Usuario.setUsuario(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.USUARIOLOGIN)));
            }else{
                cl_Usuario.setUsuario("");
            }

            rs_Usuario.close();

            return true;
        }catch (Exception e){
            cl_Usuario.setUsuario("");
            return false;
        }
    }

    public boolean fuSelecionarCdClienteBanco(){

        try {
            rs_Usuario = mdl_Usuario.fuSelecionarCdClienteBanco();

            if (rs_Usuario != null) {
                rs_Usuario.moveToFirst();
                cl_Usuario.setCdClienteBanco(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDCLIENTEBANCO)));
            }else{
                cl_Usuario.setCdClienteBanco("");
            }

            rs_Usuario.close();

            return true;
        }catch (Exception e){
            cl_Usuario.setCdClienteBanco("");
            return false;
        }

    }

    public boolean fuSelecionarNmUsuarioSistema(){

        try {
            rs_Usuario = mdl_Usuario.fuSelecionarNmUsuarioSistema();

            if (rs_Usuario != null) {
                rs_Usuario.moveToFirst();
                cl_Usuario.setNmUsuarioSistema(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.NMUSUARIOSISTEMA)));
            }else{
                cl_Usuario.setNmUsuarioSistema("");
            }

            rs_Usuario.close();

            return true;
        }catch (Exception e){
            cl_Usuario.setNmUsuarioSistema("");
            return false;
        }
    }

    public boolean fuSelecionarVendedor(){

        try {
            rs_Usuario = mdl_Usuario.fuSelecionarVendedor();

            if (rs_Usuario.getCount() > 0) {
                cl_Usuario.setCdVendedorDefault(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDVENDEDORDEFAULT)));
            } else {
                cl_Usuario.setCdVendedorDefault("");
            }
            rs_Usuario.close();

            return true;

        }catch (Exception e){
            cl_Usuario.setCdVendedorDefault("");
            return false;
        }

    }

    public boolean fuSelecionarDtSincronizacao(){

        try{
            rs_Usuario = mdl_Usuario.fuSelecionarDtSincronizacao();

            if (rs_Usuario.getCount() > 0) {
                cl_Usuario.setDtUltimaSincronizacao(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.DTULTSINCRONIZACAO)));
            } else {
                cl_Usuario.setDtUltimaSincronizacao("");
            }
            rs_Usuario.close();

            return true;

        }catch (Exception e){
            cl_Usuario.setDtUltimaSincronizacao("");
            return false;
        }
    }

    public boolean fuAtualizarSincronizacao(){
        try{

            if(mdl_Usuario.fuAtualizarSincronizacao(cl_Usuario.getDtUltimaSincronizacao())){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }
}
