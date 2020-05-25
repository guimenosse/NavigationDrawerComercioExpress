package controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;
import com.example.desenvolvimento.navigationdrawercomercioexpress.Funcoes;

import classes.CL_ItemPedido;
import classes.CL_Pedidos;
import models.MDL_Pedidos;

public class CTL_Pedidos {

    Funcoes funcoes;

    CL_Pedidos cl_Pedidos;
    MDL_Pedidos mdl_Pedidos;

    Context cxt_Pedidos;

    public String vc_Mensagem;

    public Cursor rs_Pedido;

    public CTL_Pedidos(Context context, CL_Pedidos cl_Pedidos) {
        this.cl_Pedidos = cl_Pedidos;
        this.mdl_Pedidos = new MDL_Pedidos(context);
        this.cxt_Pedidos = context;

        funcoes = new Funcoes();
    }

    public String fuCarregaNumPedido(){
        String vf_NumPedido = "";
        try{

            rs_Pedido = mdl_Pedidos.fuCarregaNumPedido();
            if (rs_Pedido.getCount() > 0){
                while(!rs_Pedido.isAfterLast()) {
                    vf_NumPedido = rs_Pedido.getString(rs_Pedido.getColumnIndex(CriaBanco.ID));
                    rs_Pedido.moveToNext();
                }
            }else{
                vf_NumPedido = "";
            }

        }catch (Exception e){
            vf_NumPedido = "";
        }

        return vf_NumPedido;
    }

    public boolean fuAbrirPedido(){

        if(mdl_Pedidos.fuAbrirPedido(cl_Pedidos.getCdCliente(), cl_Pedidos.getNomeRzSocial(), cl_Pedidos.getDtEmissao(), cl_Pedidos.getCdVendedor(), suDescricaoSituacao())){
            return true;
        }else{
            return false;
        }

    }

    public boolean fuAlterarPedido(){
        if(mdl_Pedidos.fuAlterarPedido(cl_Pedidos.getNumPedido(), cl_Pedidos.getCdCliente(), cl_Pedidos.getNomeRzSocial(), cl_Pedidos.getCondPgto(),
                cl_Pedidos.getVlTotal(), cl_Pedidos.getPercDesconto(), cl_Pedidos.getVlDesconto(), cl_Pedidos.getVlFrete(), cl_Pedidos.getObsPedido(), suDescricaoSituacao())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuAlterarSituacaoPedido(){
        if(mdl_Pedidos.fuAlterarSituacaoPedido(cl_Pedidos.getNumPedido(), suDescricaoSituacao())){
            return true;
        }else{
            vc_Mensagem = mdl_Pedidos.vc_Mensagem;
            return false;
        }
    }

    public boolean fuAlterarNumPedidoServidor(){
        if(mdl_Pedidos.fuAlterarNumPedidoServidor(cl_Pedidos.getNumPedido(), cl_Pedidos.getNumPedidoServidor())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuDeletarPedido(){
        if(mdl_Pedidos.fuDeletarPedido(cl_Pedidos.getNumPedido())){
            return true;
        }else{
            vc_Mensagem = mdl_Pedidos.vc_Mensagem;
            return false;
        }
    }

    protected String suDescricaoSituacao(){

        String vf_Situacao = "";

        if(cl_Pedidos.getFgSituacao().equals("A")){
            vf_Situacao = "ABERTO";
        }else if(cl_Pedidos.getFgSituacao().equals("E")){
            vf_Situacao = "ENVIADO";
        }else if(cl_Pedidos.getFgSituacao().equals("C")){
            vf_Situacao = "CANCELADO";
        }

        return vf_Situacao;
    }

    public boolean fuPossuiPedidosAbertos() {

        try {
            rs_Pedido = mdl_Pedidos.fuCarregaPedidosAbertos();

            if (rs_Pedido.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    public boolean fuCarregaTodosPedidos(String nomeRzSocial){

        try {
            rs_Pedido = mdl_Pedidos.fuCarregarTodosPedidos(nomeRzSocial);

            if(rs_Pedido.getCount() > 0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public boolean fuCarregaTodosPedidosEnviados(){
        try
        {
            rs_Pedido = mdl_Pedidos.fuCarregaTodosPedidosEnviados();
            if(rs_Pedido.getCount() > 0){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    public boolean fuCarregarPedido() {

        rs_Pedido = mdl_Pedidos.fuCarregaPedido(cl_Pedidos.getNumPedido());

        if (rs_Pedido.getCount() > 0) {

            while (!rs_Pedido.isAfterLast()) {
                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                        cl_Pedidos.setNumPedido(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.ID)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setNumPedido("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {

                        String vf_VlTotal = "";

                        CL_ItemPedido cl_ItemPedido = new CL_ItemPedido();
                        cl_ItemPedido.setNumPedido(cl_Pedidos.getNumPedido());

                        CTL_ItemPedido ctl_ItensPedido = new CTL_ItemPedido(cxt_Pedidos, cl_ItemPedido);

                        double vf_VlTotalDouble = 0.0;

                        if(ctl_ItensPedido.fuCarregaTodosItensPedido()) {

                            Cursor rs_ItemPedido = ctl_ItensPedido.rs_ItemPedido;

                            while (!rs_ItemPedido.isAfterLast()) {

                                vf_VlTotalDouble = vf_VlTotalDouble + Double.parseDouble(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).replace(",", "."));
                                rs_ItemPedido.moveToNext();

                            }
                        }

                        if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                            vf_VlTotalDouble = vf_VlTotalDouble - Double.parseDouble(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).replace(",", "."));
                        }

                        vf_VlTotal = String.format("%.2f", vf_VlTotalDouble);
                        cl_Pedidos.setVlTotal(vf_VlTotal.replace(".", "").replace(",", "."));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlTotal("0.00");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)).trim().equals("")) {
                        cl_Pedidos.setDtEmissao(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.DTEMISSAO)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setDtEmissao("");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)).trim().equals("")) {
                        cl_Pedidos.setCdVendedor(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDVENDEDOR)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCdVendedor("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)).trim().equals("")) {
                        cl_Pedidos.setCdCliente(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CDEMITENTE)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCdCliente("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)).trim().equals("")) {
                        cl_Pedidos.setNomeRzSocial(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.RZSOCIAL)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setNomeRzSocial("");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        cl_Pedidos.setPercDesconto(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                    } else {
                        cl_Pedidos.setPercDesconto("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setPercDesconto("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        cl_Pedidos.setVlDesconto(String.format("%.2f", Double.parseDouble(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)))));
                    } else {
                        cl_Pedidos.setVlDesconto("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlDesconto("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)).trim().equals("")) {
                        cl_Pedidos.setVlFrete(String.format("%.2f", Double.parseDouble(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.VLFRETE)))));
                    } else {
                        cl_Pedidos.setVlFrete("0");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setVlFrete("0");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)).trim().equals("")) {
                        cl_Pedidos.setCondPgto(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.CONDPGTO)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setCondPgto("DINHEIRO");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.FGSITUACAO)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.FGSITUACAO)).trim().equals("")) {
                        cl_Pedidos.setFgSituacao(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.FGSITUACAO)).substring(0, 1));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setFgSituacao("A");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)).trim().equals("")) {
                        cl_Pedidos.setObsPedido(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.OBS)));
                    } else {
                        cl_Pedidos.setObsPedido("");
                    }
                } catch (Exception e) {
                    cl_Pedidos.setObsPedido("");
                }

                try {
                    if (!rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDOSERVIDOR)).equals("null") && !rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDOSERVIDOR)).trim().equals("")) {
                        cl_Pedidos.setNumPedidoServidor(rs_Pedido.getString(rs_Pedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDOSERVIDOR)));
                    }
                } catch (Exception e) {
                    cl_Pedidos.setNumPedidoServidor("A");
                }

                rs_Pedido.moveToNext();

            }
            return true;
        } else {
            return false;
        }
    }

    //Função para duplicar o pedido
    public boolean fuDuplicarPedido() {

        cl_Pedidos.setDtEmissao(funcoes.getDateTime());

        if (mdl_Pedidos.fuDuplicarPedido(cl_Pedidos.getCdCliente(), cl_Pedidos.getNomeRzSocial(), cl_Pedidos.getDtEmissao(),
                cl_Pedidos.getCdVendedor(), cl_Pedidos.getCondPgto(), cl_Pedidos.getVlTotal(), cl_Pedidos.getPercDesconto(),
                cl_Pedidos.getVlDesconto().replace(",", "."), cl_Pedidos.getVlFrete().replace(",", "."), cl_Pedidos.getObsPedido(), cl_Pedidos.getFgSituacao())) {
            cl_Pedidos.setNumPedido(fuCarregaNumPedido());
            return true;
        } else {
            vc_Mensagem = mdl_Pedidos.vc_Mensagem;
            return false;
        }
    }

    public String fuBuscarCdClientePedido(){

        String vf_CdCliente = "";

        try {
            Cursor rs_Cliente = mdl_Pedidos.fuCarregarClientePedido(cl_Pedidos.getNumPedido());
            if(rs_Cliente.getCount() > 0){
                vf_CdCliente = rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDEMITENTE));
            }else{
                vf_CdCliente = "";
            }
        }catch (Exception e){

        }

        return vf_CdCliente;
    }

    public boolean fuBuscarPedidosData(String dataInicial, String dataFinal){

        String vf_Dia = dataInicial.substring(0, 2);
        String vf_Mes = dataInicial.substring(3, 5);
        String vf_Ano = dataInicial.substring(6, 10);
        String vf_DataInicial = vf_Ano + "-" + vf_Mes + "-" + vf_Dia;

        vf_Dia = dataFinal.substring(0, 2);
        vf_Mes = dataFinal.substring(3, 5);
        vf_Ano = dataFinal.substring(6, 10);

        String vf_DataFinal = vf_Ano + "-" + vf_Mes + "-" + vf_Dia;

        if(mdl_Pedidos.fu_BuscarPedidosData(vf_DataInicial, vf_DataFinal)){
            rs_Pedido = mdl_Pedidos.rs_Pedido;
            if (rs_Pedido.getCount() > 0){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

}
