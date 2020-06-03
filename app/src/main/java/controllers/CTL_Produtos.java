package controllers;

import android.content.Context;
import android.database.Cursor;

import java.security.interfaces.RSAKey;

import models.CriaBanco;

import br.comercioexpress.plano.Funcoes;
import classes.CL_Produtos;
import models.MDL_Produtos;

public class CTL_Produtos {

    Funcoes funcoes;

    CL_Produtos cl_Produtos;
    MDL_Produtos mdl_Produtos;

    Context cxt_Produtos;

    public String vc_Mensagem;

    public Cursor rs_Produto;

    public CTL_Produtos(Context context, CL_Produtos cl_Produtos) {
        this.cl_Produtos = cl_Produtos;
        this.mdl_Produtos = new MDL_Produtos(context);
        this.cxt_Produtos = context;

        funcoes = new Funcoes();
    }

    public boolean fuInserirProdutoFilial(){

        if(mdl_Produtos.fuInserirProdutoFilial(cl_Produtos.getCdProduto(), cl_Produtos.getDescricao(), cl_Produtos.getEstoqueAtual(),
                cl_Produtos.getVlUnitario(), cl_Produtos.getVlAtacado(), cl_Produtos.getDtUltimaAlteracao(), cl_Produtos.getDescMaxPermitido(),
                cl_Produtos.getDescMaxPermitidoA(), cl_Produtos.getDescMaxPermitidoB(), cl_Produtos.getDescMaxPermitidoC(),
                cl_Produtos.getDescMaxPermitidoD(), cl_Produtos.getDescMaxPermitidoE(), cl_Produtos.getDescMaxPermitidoFidelidade(),
                cl_Produtos.getQtdeDisponivel(), cl_Produtos.getCdRefEstoque())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuCarregaProduto(){
        try {
            rs_Produto = mdl_Produtos.fuCarregaProdutosById(cl_Produtos.getId());

            if(rs_Produto.getCount() > 0){

                while (!rs_Produto.isAfterLast()){

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null")) {
                            cl_Produtos.setCdProduto(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setCdProduto("0");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null")) {
                            cl_Produtos.setDescricao(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescricao("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)).equals("null")) {
                            cl_Produtos.setEstoqueAtual(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setEstoqueAtual("0");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).equals("null")) {
                            cl_Produtos.setVlUnitario(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setVlUnitario("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).equals("null")) {
                            cl_Produtos.setVlAtacado(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORATACADO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setVlAtacado("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)).equals("null")) {
                            cl_Produtos.setDescMaxPermitido(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitido("0.00");
                    }
                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoA(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoA("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoB(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoB("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoC(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoC("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoD(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoD("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoE(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoE("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoFidelidade(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoFidelidade("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)).equals("null")) {
                            cl_Produtos.setQtdeDisponivel(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setQtdeDisponivel("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDREFESTOQUE)).equals("null")) {
                            cl_Produtos.setCdRefEstoque(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDREFESTOQUE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setCdRefEstoque("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DTULTALTERACAO)).equals("null")) {
                            cl_Produtos.setDtUltimaAlteracao(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DTULTALTERACAO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDtUltimaAlteracao("");
                    }

                    rs_Produto.moveToNext();
                }
            }else{
                return false;
            }

            return true;
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public boolean fuCarregaProdutoCdProduto(){
        try {
            rs_Produto = mdl_Produtos.fuCarregaProdutosCdProduto(cl_Produtos.getCdProduto());

            if(rs_Produto.getCount() > 0){

                while (!rs_Produto.isAfterLast()){

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null")) {
                            cl_Produtos.setCdProduto(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setCdProduto("0");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null")) {
                            cl_Produtos.setDescricao(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescricao("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)).equals("null")) {
                            cl_Produtos.setEstoqueAtual(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.ESTOQUEATUAL)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setEstoqueAtual("0");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)).equals("null")) {
                            cl_Produtos.setVlUnitario(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORUNITARIO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setVlUnitario("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORATACADO)).equals("null")) {
                            cl_Produtos.setVlAtacado(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.VALORATACADO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setVlAtacado("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)).equals("null")) {
                            cl_Produtos.setDescMaxPermitido(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitido("0.00");
                    }
                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoA(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOA)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoA("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoB(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOB)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoB("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoC(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOC)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoC("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoD(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOD)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoD("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoE(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoE("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)).equals("null")) {
                            cl_Produtos.setDescMaxPermitidoFidelidade(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DESCMAXPERMITIDOFIDELIDADE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDescMaxPermitidoFidelidade("0.00");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)).equals("null")) {
                            cl_Produtos.setQtdeDisponivel(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.QTDEDISPONIVEL)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setQtdeDisponivel("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDREFESTOQUE)).equals("null")) {
                            cl_Produtos.setCdRefEstoque(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.CDREFESTOQUE)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setCdRefEstoque("");
                    }

                    try {
                        if (!rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DTULTALTERACAO)).equals("null")) {
                            cl_Produtos.setDtUltimaAlteracao(rs_Produto.getString(rs_Produto.getColumnIndexOrThrow(CriaBanco.DTULTALTERACAO)));
                        }
                    } catch (Exception e) {
                        cl_Produtos.setDtUltimaAlteracao("");
                    }

                    rs_Produto.moveToNext();
                }
            }else{
                return false;
            }

            return true;
        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return false;
        }
    }

    public String fuBuscaValorAtacado(){
        String vf_VlAtacado = "";
        try {
            Cursor rs_VlAtacado = mdl_Produtos.fuBuscaValorAtacado(cl_Produtos.getCdProduto());
            if(rs_VlAtacado.getCount() > 0){
                vf_VlAtacado = rs_VlAtacado.getString(rs_VlAtacado.getColumnIndex(CriaBanco.VALORATACADO));
            }else{
                vf_VlAtacado = "0.00";
            }
        }catch (Exception e){
            vf_VlAtacado = "0.00";
        }

        return vf_VlAtacado;
    }

    public String fuBuscarCdRefEstoque(){
        String vf_CdRefEstoque = "";
        try {
            rs_Produto = mdl_Produtos.fuBuscaCdRefEstoque(cl_Produtos.getCdProduto());
            if(rs_Produto.getCount() > 0){
                vf_CdRefEstoque = rs_Produto.getString(rs_Produto.getColumnIndex(CriaBanco.CDREFESTOQUE));
            }
        }catch (Exception e){
            vf_CdRefEstoque = "";
        }

        return vf_CdRefEstoque;
    }
}
