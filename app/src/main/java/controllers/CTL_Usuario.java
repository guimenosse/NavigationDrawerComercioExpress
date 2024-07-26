package controllers;

import android.annotation.SuppressLint;
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

    /*this.cdClienteBanco = "";
        this.nmUsuarioSistema = "";
        this.cdVendedorDefault = "";
        this.dtUltimaSincronizacao = "";

        this.ip = "";
        this.usuarioSQL = "";
        this.senhaSQL = "";
        this.nmBanco = "";*/

    @SuppressLint("Range")
    public CL_Usuario fuSelecionarUsuarioAPI(){
        CL_Usuario cl_UsuarioAPI = new CL_Usuario();
        try {

            rs_Usuario = mdl_Usuario.fuSelecionarUsuarioAPI();

            if (rs_Usuario != null) {
                rs_Usuario.moveToFirst();
                cl_UsuarioAPI.setCdUsuario(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDUSUARIO)));
                cl_UsuarioAPI.setUsuario(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.USUARIOLOGIN)));
                cl_UsuarioAPI.setSenha(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.SENHALOGIN)));
                cl_UsuarioAPI.setCdClienteBanco(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDCLIENTEBANCO)));
                cl_UsuarioAPI.setNmUsuarioSistema(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.NMUSUARIOSISTEMA)));
                cl_UsuarioAPI.setCdVendedorDefault(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.CDVENDEDORDEFAULT)));
                cl_UsuarioAPI.setIp(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.IP)));
                cl_UsuarioAPI.setUsuarioSQL(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.USUARIOSQL)));
                cl_UsuarioAPI.setSenhaSQL(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.SENHASQL)));
                cl_UsuarioAPI.setNmBanco(rs_Usuario.getString(rs_Usuario.getColumnIndex(CriaBanco.NMBANCO)));
            }else{
                cl_UsuarioAPI.setCdUsuario("");
            }

            rs_Usuario.close();


        }catch (Exception e){
            cl_UsuarioAPI.setCdUsuario("");
        }

        return cl_UsuarioAPI;
    }

    public String buildRequestBodyString(CL_Usuario cl_UsuarioAPI) {
        return "cdUsuario=" + cl_UsuarioAPI.getCdUsuario()
                + "&usuario=" + cl_UsuarioAPI.getUsuario()
                + "&senha=" + cl_UsuarioAPI.getSenha()
                + "&cdCliente=" + cl_UsuarioAPI.getCdClienteBanco()
                + "&nmUsuarioSistema=" + cl_UsuarioAPI.getNmUsuarioSistema()
                + "&ip=" + cl_UsuarioAPI.getIp()
                + "&usuarioSQL=" + cl_UsuarioAPI.getUsuarioSQL()
                + "&senhaSQL=" + cl_UsuarioAPI.getSenhaSQL()
                + "&nmBanco=" + cl_UsuarioAPI.getNmBanco()
                + "&cdVendedorDefault=" + cl_UsuarioAPI.getCdVendedorDefault();
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
