package controllers;

import android.content.Context;
import android.database.Cursor;

import br.comercioexpress.plano.Funcoes;
import classes.CL_Configuracao;
import models.CriaBanco;
import models.MDL_Configuracao;

public class CTL_Configuracao {

    Funcoes funcoes;

    CL_Configuracao cl_Configuracao;
    MDL_Configuracao mdl_Configuracao;

    Context cxt_Pedidos;

    public String vc_Mensagem;

    public Cursor rs_Configuracao;

    public CTL_Configuracao(Context context, CL_Configuracao cl_Configuracao){
        this.cl_Configuracao = cl_Configuracao;
        this.mdl_Configuracao = new MDL_Configuracao(context);
        this.cxt_Pedidos = context;

        funcoes = new Funcoes();
    }

    public boolean fuAtualizarConfiguracoes(){
        if(mdl_Configuracao.fuAtualizarConfiguracoes(cl_Configuracao.getFgControlaEstoquePedido(), cl_Configuracao.getFgPrecoIndividualizado())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuCarregarConfiguracoes(){

        try{
            rs_Configuracao = mdl_Configuracao.fuCarregarConfiguracoes();
            if(rs_Configuracao.getCount() > 0){
                while (!rs_Configuracao.isAfterLast()){

                    try {
                        if (!rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)).equals("null") && !rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)).trim().equals("")) {
                            cl_Configuracao.setFgControlaEstoquePedido(rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)));
                        }
                    } catch (Exception e) {
                        cl_Configuracao.setFgControlaEstoquePedido("N");
                    }

                    try {
                        if (!rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGPRECOINDIVIDUALIZADO)).equals("null") && !rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGPRECOINDIVIDUALIZADO)).trim().equals("")) {
                            cl_Configuracao.setFgPrecoIndividualizado(rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGPRECOINDIVIDUALIZADO)));
                        }
                    } catch (Exception e) {
                        cl_Configuracao.setFgPrecoIndividualizado("N");
                    }

                    rs_Configuracao.moveToNext();
                }
            }
            return true;
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public boolean fuAtualizarFgControlaEstoquePedido(){
        if(mdl_Configuracao.fuAtualizarFgControlaEstoquePedido(cl_Configuracao.getFgControlaEstoquePedido())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuCarregarFgControlaEstoquePedido(){

        try{
            rs_Configuracao = mdl_Configuracao.fuCarregarFgControlaEstoquePedido();
            if(rs_Configuracao.getCount() > 0){
                while (!rs_Configuracao.isAfterLast()){

                    try {
                        if (!rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)).equals("null") && !rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)).trim().equals("")) {
                            cl_Configuracao.setFgControlaEstoquePedido(rs_Configuracao.getString(rs_Configuracao.getColumnIndexOrThrow(CriaBanco.FGCONTROLAESTOQUEPEDIDO)));
                        }
                    } catch (Exception e) {
                        cl_Configuracao.setFgControlaEstoquePedido("N");
                    }

                    rs_Configuracao.moveToNext();
                }
            }
            return true;
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }
}
