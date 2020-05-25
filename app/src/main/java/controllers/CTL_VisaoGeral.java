package controllers;

import android.content.Context;
import android.database.Cursor;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

import java.util.ArrayList;
import java.util.List;

import classes.CL_VisaoGeral;
import models.MDL_Clientes;
import models.MDL_Pedidos;
import models.MDL_Produtos;

public class CTL_VisaoGeral {

    CL_VisaoGeral cl_VisaoGeral;

    MDL_Produtos mdl_Produtos;
    MDL_Pedidos mdl_Pedidos;

    Context cxt_VisaoGeral;

    public String vc_Mensagem;


    public CTL_VisaoGeral(Context contexto, CL_VisaoGeral cl_VisaoGeral){
        this.cxt_VisaoGeral = contexto;
        this.cl_VisaoGeral = cl_VisaoGeral;
        this.mdl_Pedidos = new MDL_Pedidos(cxt_VisaoGeral);
        this.mdl_Produtos = new MDL_Produtos(cxt_VisaoGeral);
    }

    public boolean fu_ValidarVendaCadastrada(){

        try {
            if (mdl_Pedidos.fu_ValidarPossuiPedido()) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    public boolean fu_CarregarMovimentacaoDiaria(String dtMovimentacao){

        double vf_VlVendaBruto = 0.0;
        double vf_VlVendaDesconto = 0.0;
        double vf_VlVendaLiquido = 0.0;
        double vf_vlLucroMedio = 0.0;

        try {
            if (mdl_Pedidos.fu_BuscarMovimentacaoDiaria(dtMovimentacao)) {

                Cursor rs_Movimentacao = mdl_Pedidos.rs_Pedido;

                if (rs_Movimentacao != null) {
                    if (rs_Movimentacao.getCount() > 0) {
                        int countCursor = 0;
                        rs_Movimentacao.moveToFirst();

                        while (countCursor < rs_Movimentacao.getCount()) {
                            String vf_VlDescontoString = rs_Movimentacao.getString(rs_Movimentacao.getColumnIndex(CriaBanco.VLDESCONTO));
                            double vf_VlDescontoVenda = 0.0;
                            if(vf_VlDescontoString.trim().equals("")){
                                vf_VlDescontoVenda += 0.0;
                            }else{
                                vf_VlDescontoVenda += Double.parseDouble(rs_Movimentacao.getString(rs_Movimentacao.getColumnIndex(CriaBanco.VLDESCONTO)).replace(",", "."));
                            }
                            String vf_VlTotalString = rs_Movimentacao.getString(rs_Movimentacao.getColumnIndex(CriaBanco.VLTOTAL)).replace(",", ".");
                            double vf_VlTotal = 0.0;
                            if(vf_VlTotalString.trim().equals("")){
                                vf_VlTotal += 0.0;
                            }else{
                                vf_VlTotal += Double.parseDouble(vf_VlTotalString.replace(".", "").replace(",", "."));
                            }

                            vf_VlVendaBruto += vf_VlTotal + vf_VlDescontoVenda;
                            vf_VlVendaDesconto += vf_VlDescontoVenda;
                            vf_VlVendaLiquido += vf_VlTotal;

                            countCursor ++;
                            rs_Movimentacao.moveToNext();
                        }

                        cl_VisaoGeral.setVlVendaBruto(vf_VlVendaBruto);
                        cl_VisaoGeral.setVlVendaDesconto(vf_VlVendaDesconto);
                        cl_VisaoGeral.setVlVendaLiquido(vf_VlVendaLiquido);
                    } else {
                        vc_Mensagem = mdl_Pedidos.vc_Mensagem;
                        return false;
                    }
                } else {
                    vc_Mensagem = mdl_Pedidos.vc_Mensagem;
                    return false;
                }
            } else {
                vc_Mensagem = mdl_Pedidos.vc_Mensagem;
                return false;
            }
        }catch (Exception e){
            vc_Mensagem = mdl_Pedidos.vc_Mensagem;
            return false;
        }

        return true;
    }

    public boolean fu_CarregarDadosTipoVenda(String dataInicial, String dataFinal){

        int vf_CountTipoVenda = 0;
        int vf_CountCanceladosTipoVenda = 0;
        double vf_VlDescontoTipoVenda = 0.0;
        double vf_VlTotalTipoVenda = 0.0;

        Cursor rs_Venda = mdl_Pedidos.fu_SelecionarTodosPedidos();


        if(!cl_VisaoGeral.getDataInicial().trim().equals("")){
            String vf_Dia = cl_VisaoGeral.getDataInicial().substring(0, 2);
            String vf_Mes = cl_VisaoGeral.getDataInicial().substring(3, 5);
            String vf_Ano = cl_VisaoGeral.getDataInicial().substring(6, 10);
            String vf_DataInicial = vf_Ano + "-" + vf_Mes + "-" + vf_Dia;

            vf_Dia = cl_VisaoGeral.getDataFinal().substring(0, 2);
            vf_Mes = cl_VisaoGeral.getDataFinal().substring(3, 5);
            vf_Ano = cl_VisaoGeral.getDataFinal().substring(6, 10);

            String vf_DataFinal = vf_Ano + "-" + vf_Mes + "-" + vf_Dia;

            if(mdl_Pedidos.fu_BuscarPedidosData(dataInicial, dataFinal)) {
                rs_Venda = mdl_Pedidos.rs_Pedido;
            }else{
                return false;
            }

        }

        if(rs_Venda != null){
            if(rs_Venda.getCount() > 0) {
                int count = 0;
                while (count < rs_Venda.getCount()) {
                    try {
                        if (rs_Venda.getString(rs_Venda.getColumnIndex(CriaBanco.FGSITUACAO)).equals("ABERTO")) {
                            vf_CountTipoVenda += 1;

                            if (rs_Venda.getString(rs_Venda.getColumnIndex(CriaBanco.VLDESCONTO)).trim().equals("")) {
                                vf_VlDescontoTipoVenda += 0.0;
                            } else {
                                vf_VlDescontoTipoVenda += Double.parseDouble(rs_Venda.getString(rs_Venda.getColumnIndex(CriaBanco.VLDESCONTO)));
                            }
                            String vf_VlTotal = rs_Venda.getString(rs_Venda.getColumnIndex(CriaBanco.VLTOTAL)).replace("Total Liquido do Pedido: R$", "");
                            if (vf_VlTotal.trim().equals("")) {
                                vf_VlTotalTipoVenda += 0.0;
                            } else {
                                vf_VlTotalTipoVenda += Double.parseDouble(vf_VlTotal.replace(".", "").replace(",", "."));
                            }
                        } else if (rs_Venda.getString(rs_Venda.getColumnIndex(CriaBanco.FGSITUACAO)).equals("CANCELADO")) {
                            vf_CountCanceladosTipoVenda += 1;
                        }
                    }catch (Exception e){

                    }
                    count += 1;

                    rs_Venda.moveToNext();
                }

                cl_VisaoGeral.setCountTipoVenda(vf_CountTipoVenda);
                cl_VisaoGeral.setCountCanceladosTipoVenda(vf_CountCanceladosTipoVenda);
                cl_VisaoGeral.setVlDescontoTipoVenda(vf_VlDescontoTipoVenda);
                cl_VisaoGeral.setVlTotalTipoVenda(vf_VlTotalTipoVenda);

            }else{
                cl_VisaoGeral.setCountTipoVenda(0);
                cl_VisaoGeral.setCountCanceladosTipoVenda(0);
                cl_VisaoGeral.setVlDescontoTipoVenda(0.00);
                cl_VisaoGeral.setVlTotalTipoVenda(0.00);
            }
        }

        return true;
    }

    public int fu_BuscarProdutosAtencao(){

        vc_Mensagem = "";
        int vf_CountProdutosAtencao = 0;

        try {

            vf_CountProdutosAtencao = mdl_Produtos.fu_BuscarProdutosAtencao();
            if(vf_CountProdutosAtencao == 0){
                if(!mdl_Produtos.vc_Mensagem.trim().equals("")){
                    vc_Mensagem = mdl_Produtos.vc_Mensagem;
                }
            }

        }catch (Exception e){
            vc_Mensagem = e.getMessage();
            return 0;
        }

        return vf_CountProdutosAtencao;
    }

}
