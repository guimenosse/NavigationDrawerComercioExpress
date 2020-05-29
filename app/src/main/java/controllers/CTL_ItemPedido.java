package controllers;

import android.content.Context;
import android.database.Cursor;

import models.CriaBanco;
import br.comercioexpress.plano.Funcoes;

import classes.CL_ItemPedido;
import models.MDL_ItemPedido;

public class CTL_ItemPedido {

    Funcoes funcoes = new Funcoes();

    CL_ItemPedido cl_ItemPedido;
    MDL_ItemPedido mdl_ItemPedido;

    Context cxt_ItemPedido;

    public String vc_Mensagem;

    public Cursor rs_ItemPedido;

    public CTL_ItemPedido(Context context, CL_ItemPedido cl_ItemPedido){
        this.cl_ItemPedido = cl_ItemPedido;
        this.cxt_ItemPedido = context;
        this.mdl_ItemPedido = new MDL_ItemPedido(context);

    }

    public boolean fuInserirItemPedido(){

        if(mdl_ItemPedido.fuInserirItemPedido(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getCdProduto(), cl_ItemPedido.getDescricao(),
                cl_ItemPedido.getQtde(), cl_ItemPedido.getPercDesconto(), cl_ItemPedido.getVlDesconto(), cl_ItemPedido.getVlMaxDescPermitido(),
                cl_ItemPedido.getVlUnitario(), cl_ItemPedido.getVlLiquido(), cl_ItemPedido.getVlTotal())){
            return true;
        }else{
            return false;
        }

    }

    public boolean fuAlterarItemPedido(){

        if(mdl_ItemPedido.fuAlterarItemPedido(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getCdProduto(),
                cl_ItemPedido.getQtde(), cl_ItemPedido.getPercDesconto(), cl_ItemPedido.getVlDesconto(),
                cl_ItemPedido.getVlUnitario(), cl_ItemPedido.getVlLiquido(), cl_ItemPedido.getVlTotal())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuDeletarItemPedido(){
        if(mdl_ItemPedido.fuDeletarItemPedido(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getCdProduto())){
            return true;
        }else{
            return false;
        }
    }

    public boolean fuCarregaTodosItensPedido() {
        rs_ItemPedido = mdl_ItemPedido.fuCarregaItemPedido(cl_ItemPedido.getNumPedido());

        if (rs_ItemPedido.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean fuCarregaItemPedidoCdItemPedido() {

        rs_ItemPedido = mdl_ItemPedido.fuCarregaItemPedido(cl_ItemPedido.getNumPedido());

        if (rs_ItemPedido.getCount() > 0) {

            cl_ItemPedido = new CL_ItemPedido();

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                    cl_ItemPedido.setId(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                }
            } catch (Exception e) {
                cl_ItemPedido.setId("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).trim().equals("")) {
                    cl_ItemPedido.setNumPedido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setNumPedido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                    cl_ItemPedido.setCdProduto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setCdProduto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                    cl_ItemPedido.setDescricao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setDescricao(" ");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                    cl_ItemPedido.setQtde(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setQtde("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                    cl_ItemPedido.setPercDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setPercDesconto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).trim().equals("")) {
                    cl_ItemPedido.setVlMaxDescPermitido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlMaxDescPermitido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                    cl_ItemPedido.setVlDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlDesconto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                    cl_ItemPedido.setVlUnitario(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlUnitario("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).trim().equals("")) {
                    cl_ItemPedido.setVlLiquido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlLiquido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                    cl_ItemPedido.setVlTotal(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlTotal("0");
            }

            return true;
        } else {
            return false;
        }

    }

    public boolean fuCarregaItemPedidoCdProduto() {

        rs_ItemPedido = mdl_ItemPedido.fuCarregaProdutoItemPedido(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getCdProduto());

        if (rs_ItemPedido.getCount() > 0) {

            cl_ItemPedido = new CL_ItemPedido();

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                    cl_ItemPedido.setId(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                }
            } catch (Exception e) {
                cl_ItemPedido.setId("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).trim().equals("")) {
                    cl_ItemPedido.setNumPedido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setNumPedido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                    cl_ItemPedido.setCdProduto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setCdProduto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                    cl_ItemPedido.setDescricao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setDescricao(" ");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                    cl_ItemPedido.setQtde(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setQtde("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                    cl_ItemPedido.setPercDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setPercDesconto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).trim().equals("")) {
                    cl_ItemPedido.setVlMaxDescPermitido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlMaxDescPermitido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                    cl_ItemPedido.setVlDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlDesconto("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                    cl_ItemPedido.setVlUnitario(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlUnitario("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).trim().equals("")) {
                    cl_ItemPedido.setVlLiquido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlLiquido("0");
            }

            try {
                if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                    cl_ItemPedido.setVlTotal(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                }
            } catch (Exception e) {
                cl_ItemPedido.setVlTotal("0");
            }

            return true;
        } else {
            return false;
        }

    }

    //Função para duplicar o pedido
    public boolean fuDuplicarItensPedidoDuplicado(String numPedidoOriginal) {

        String vf_NumPedidoNovo = cl_ItemPedido.getNumPedido();
        rs_ItemPedido = mdl_ItemPedido.fuCarregaItemPedido(numPedidoOriginal);

        if (rs_ItemPedido.getCount() > 0) {
            while (!rs_ItemPedido.isAfterLast()) {

                cl_ItemPedido = new CL_ItemPedido();

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                        cl_ItemPedido.setId(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setId("0");
                }

                try {
                    cl_ItemPedido.setNumPedido(vf_NumPedidoNovo);
                } catch (Exception e) {
                    cl_ItemPedido.setNumPedido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                        cl_ItemPedido.setCdProduto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setCdProduto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                        cl_ItemPedido.setDescricao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setDescricao(" ");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                        cl_ItemPedido.setQtde(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setQtde("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setPercDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setPercDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlMaxDescPermitido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlMaxDescPermitido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setVlDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                        cl_ItemPedido.setVlUnitario(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlUnitario("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlLiquido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlLiquido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        cl_ItemPedido.setVlTotal(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlTotal("0");
                }

                if (!mdl_ItemPedido.fuDuplicarItensPedido(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getCdProduto(), cl_ItemPedido.getDescricao(),
                        cl_ItemPedido.getQtde(), cl_ItemPedido.getPercDesconto(), cl_ItemPedido.getVlDesconto(), cl_ItemPedido.getVlMaxDescPermitido(),
                        cl_ItemPedido.getVlUnitario(), cl_ItemPedido.getVlLiquido(), cl_ItemPedido.getVlTotal())) {
                    vc_Mensagem = mdl_ItemPedido.vc_Mensagem;
                    return false;
                }

                rs_ItemPedido.moveToNext();
            }
        }

        return true;
    }

    public boolean fuCarregaItemPedidoAlteracaoId(){
        rs_ItemPedido = mdl_ItemPedido.fuCarregaItemPedidoAlteracao(cl_ItemPedido.getNumPedido(), cl_ItemPedido.getId());

        if (rs_ItemPedido.getCount() > 0) {
            while (!rs_ItemPedido.isAfterLast()) {
                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).trim().equals("")) {
                        cl_ItemPedido.setId(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.ID)).replace(" ", "espaco"));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setId("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)).trim().equals("")) {
                        cl_ItemPedido.setNumPedido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.NUMPEDIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setNumPedido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)).trim().equals("")) {
                        cl_ItemPedido.setCdProduto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.CDPRODUTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setCdProduto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)).trim().equals("")) {
                        cl_ItemPedido.setDescricao(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.DESCRICAO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setDescricao(" ");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)).trim().equals("")) {
                        cl_ItemPedido.setQtde(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.QTDE)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setQtde("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setPercDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.PERCDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setPercDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlMaxDescPermitido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLMAXDESCPERMITIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlMaxDescPermitido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)).trim().equals("")) {
                        cl_ItemPedido.setVlDesconto(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLDESCONTO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlDesconto("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)).trim().equals("")) {
                        cl_ItemPedido.setVlUnitario(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLUNITARIO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlUnitario("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)).trim().equals("")) {
                        cl_ItemPedido.setVlLiquido(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLLIQUIDO)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlLiquido("0");
                }

                try {
                    if (!rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).equals("null") && !rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)).trim().equals("")) {
                        cl_ItemPedido.setVlTotal(rs_ItemPedido.getString(rs_ItemPedido.getColumnIndexOrThrow(CriaBanco.VLTOTAL)));
                    }
                } catch (Exception e) {
                    cl_ItemPedido.setVlTotal("0");
                }

                rs_ItemPedido.moveToNext();
            }
        }

        return true;
    }

}
