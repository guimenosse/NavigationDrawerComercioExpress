package controllers;

import android.content.Context;
import android.database.Cursor;

import com.example.desenvolvimento.navigationdrawercomercioexpress.CriaBanco;

import classes.CL_Clientes;
import models.MDL_Clientes;

public class CTL_Clientes {

    CL_Clientes cl_Cliente;
    MDL_Clientes mdl_Cliente;

    Context cxt_Cliente;

    public Cursor rs_Cliente;

    public CTL_Clientes(Context cxt_Cliente, CL_Clientes cl_Cliente){

        this.cl_Cliente = cl_Cliente;
        this.cxt_Cliente = cxt_Cliente;
        this.mdl_Cliente = new MDL_Clientes(this.cxt_Cliente);

    }

    public boolean fuSelecionarCliente(){

        rs_Cliente = mdl_Cliente.fuCarregaCliente(cl_Cliente.getCdCliente());

        if(rs_Cliente.getCount() > 0){

            while(!rs_Cliente.isAfterLast()) {

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                    cl_Cliente.setCdCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)));
                }else{
                    cl_Cliente.setCdCliente("0");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).trim().equals("")) {
                    cl_Cliente.setNomeRzSocial(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeRzSocial("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).trim().equals("")) {
                    cl_Cliente.setNomeFantasia(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeFantasia("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).trim().equals("")) {
                    cl_Cliente.setCep(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).replace(".", "").replace("-", ""));
                } else {
                    cl_Cliente.setCep("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).trim().equals("")) {
                    cl_Cliente.setEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).replace("'", ""));
                } else {
                    cl_Cliente.setEndereco("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).trim().equals("")) {
                    cl_Cliente.setClassificacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).replace("'", ""));
                } else {
                    cl_Cliente.setClassificacao("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).trim().equals("")) {
                    cl_Cliente.setNumEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)));
                } else {
                    cl_Cliente.setNumEndereco("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).trim().equals("")) {
                    cl_Cliente.setComplemento(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).replace("'", ""));
                } else {
                    cl_Cliente.setComplemento("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).trim().equals("")) {
                    cl_Cliente.setBairro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).replace("'", ""));
                } else {
                    cl_Cliente.setBairro("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).trim().equals("")) {
                    cl_Cliente.setUf(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)));
                } else {
                    cl_Cliente.setUf("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).trim().equals("")) {
                    cl_Cliente.setCidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).replace("'", ""));
                } else {
                    cl_Cliente.setCidade("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).trim().equals("")) {
                    cl_Cliente.setCpfCnpj(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).replace(".", "").replace("-", "").replace("/", ""));
                } else {
                    cl_Cliente.setCpfCnpj("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).trim().equals("")) {
                    cl_Cliente.setTelefone(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefone("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).trim().equals("")) {
                    cl_Cliente.setTelefoneAdicional(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefoneAdicional("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).trim().equals("")) {
                    cl_Cliente.setFax(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).replace("(", "").replace("-", "").replace(")", ""));
                    ;
                } else {
                    cl_Cliente.setFax("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).trim().equals("")) {
                    cl_Cliente.setNomeContato(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeContato("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).trim().equals("")) {
                    cl_Cliente.setEmail(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).replace("'", ""));
                } else {
                    cl_Cliente.setEmail("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).trim().equals("")) {
                    cl_Cliente.setTipoPessoa(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)));
                } else {
                    cl_Cliente.setTipoPessoa("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).trim().equals("")) {
                    cl_Cliente.setTipoCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)));
                } else {
                    cl_Cliente.setTipoCliente("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).trim().equals("")) {
                    cl_Cliente.setObservacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).replace("'", ""));
                } else {
                    cl_Cliente.setObservacao("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).trim().equals("")) {
                    cl_Cliente.setDtUltimaAlteracao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)));
                } else {
                    cl_Cliente.setDtUltimaAlteracao("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).trim().equals("")) {
                    cl_Cliente.setDtCadastro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)));
                } else {
                    cl_Cliente.setDtCadastro("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).trim().equals("")) {
                    cl_Cliente.setVendedor(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)));
                } else {
                    cl_Cliente.setVendedor("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).trim().equals("")) {
                    cl_Cliente.setInscEstadual(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)));
                } else {
                    cl_Cliente.setInscEstadual("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).trim().equals("")) {
                    cl_Cliente.setFidelidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)));
                } else {
                    cl_Cliente.setFidelidade("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).trim().equals("")) {
                    cl_Cliente.setTipoPreco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)));
                } else {
                    cl_Cliente.setTipoPreco("");
                }
            }

            return true;
        }else{
            return false;
        }

    }

    public boolean fuCarregarClienteId(){
        rs_Cliente = mdl_Cliente.fuCarregaClienteId(cl_Cliente.getId());

        if(rs_Cliente.getCount() > 0){

            while(!rs_Cliente.isAfterLast()) {

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                    cl_Cliente.setCdCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)));
                }else{
                    cl_Cliente.setCdCliente("0");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).trim().equals("")) {
                    cl_Cliente.setNomeRzSocial(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeRzSocial("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).trim().equals("")) {
                    cl_Cliente.setNomeFantasia(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeFantasia("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).trim().equals("")) {
                    cl_Cliente.setCep(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).replace(".", "").replace("-", ""));
                } else {
                    cl_Cliente.setCep("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).trim().equals("")) {
                    cl_Cliente.setEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).replace("'", ""));
                } else {
                    cl_Cliente.setEndereco("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).trim().equals("")) {
                    cl_Cliente.setClassificacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).replace("'", ""));
                } else {
                    cl_Cliente.setClassificacao("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).trim().equals("")) {
                    cl_Cliente.setNumEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)));
                } else {
                    cl_Cliente.setNumEndereco("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).trim().equals("")) {
                    cl_Cliente.setComplemento(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).replace("'", ""));
                } else {
                    cl_Cliente.setComplemento("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).trim().equals("")) {
                    cl_Cliente.setBairro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).replace("'", ""));
                } else {
                    cl_Cliente.setBairro("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).trim().equals("")) {
                    cl_Cliente.setUf(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)));
                } else {
                    cl_Cliente.setUf("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).trim().equals("")) {
                    cl_Cliente.setCidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).replace("'", ""));
                } else {
                    cl_Cliente.setCidade("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).trim().equals("")) {
                    cl_Cliente.setCpfCnpj(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).replace(".", "").replace("-", "").replace("/", ""));
                } else {
                    cl_Cliente.setCpfCnpj("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).trim().equals("")) {
                    cl_Cliente.setTelefone(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefone("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).trim().equals("")) {
                    cl_Cliente.setTelefoneAdicional(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefoneAdicional("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).trim().equals("")) {
                    cl_Cliente.setFax(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).replace("(", "").replace("-", "").replace(")", ""));
                    ;
                } else {
                    cl_Cliente.setFax("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).trim().equals("")) {
                    cl_Cliente.setNomeContato(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeContato("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).trim().equals("")) {
                    cl_Cliente.setEmail(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).replace("'", ""));
                } else {
                    cl_Cliente.setEmail("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).trim().equals("")) {
                    cl_Cliente.setTipoPessoa(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)));
                } else {
                    cl_Cliente.setTipoPessoa("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).trim().equals("")) {
                    cl_Cliente.setTipoCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)));
                } else {
                    cl_Cliente.setTipoCliente("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).trim().equals("")) {
                    cl_Cliente.setObservacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).replace("'", ""));
                } else {
                    cl_Cliente.setObservacao("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).trim().equals("")) {
                    cl_Cliente.setDtUltimaAlteracao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)));
                } else {
                    cl_Cliente.setDtUltimaAlteracao("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).trim().equals("")) {
                    cl_Cliente.setDtCadastro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)));
                } else {
                    cl_Cliente.setDtCadastro("");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).trim().equals("")) {
                    cl_Cliente.setVendedor(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)));
                } else {
                    cl_Cliente.setVendedor("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).trim().equals("")) {
                    cl_Cliente.setInscEstadual(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)));
                } else {
                    cl_Cliente.setInscEstadual("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).trim().equals("")) {
                    cl_Cliente.setFidelidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)));
                } else {
                    cl_Cliente.setFidelidade("");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).trim().equals("")) {
                    cl_Cliente.setTipoPreco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)));
                } else {
                    cl_Cliente.setTipoPreco("");
                }

                rs_Cliente.moveToNext();
            }

            return true;
        }else{
            return false;
        }
    }

    public boolean fuSelecionarClienteSincronizacao(String condicao){

        if(condicao.equals("cdCliente")) {
            rs_Cliente = mdl_Cliente.fuCarregaCliente(cl_Cliente.getCdCliente());
        }else{
            rs_Cliente = mdl_Cliente.fuCarregaClienteCPFCNPJ(cl_Cliente.getCdCliente());
        }

        if(rs_Cliente.getCount() > 0){

            while(!rs_Cliente.isAfterLast()) {

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)).trim().equals("")) {
                    cl_Cliente.setId(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ID)));
                }else{
                    cl_Cliente.setId("0");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)).trim().equals("")) {
                    cl_Cliente.setCdCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CDCLIENTE)));
                }else{
                    cl_Cliente.setCdCliente("0");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).trim().equals("")) {
                    cl_Cliente.setNomeRzSocial(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.RZSOCIAL)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeRzSocial("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).trim().equals("")) {
                    cl_Cliente.setNomeFantasia(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NMFANTASIA)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeFantasia("espaco");
                }
                //nmfantasiaString = "Guilherme";
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).trim().equals("")) {
                    cl_Cliente.setCep(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CEP)).replace(".", "").replace("-", ""));
                } else {
                    cl_Cliente.setCep("0");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).trim().equals("")) {
                    cl_Cliente.setEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.ENDERECO)).replace("'", ""));
                } else {
                    cl_Cliente.setEndereco("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).trim().equals("")) {
                    cl_Cliente.setClassificacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CLASSIFICACAO)).replace("'", ""));
                } else {
                    cl_Cliente.setClassificacao("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)).trim().equals("")) {
                    cl_Cliente.setNumEndereco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.NUMERO)));
                } else {
                    cl_Cliente.setNumEndereco("0");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).trim().equals("")) {
                    cl_Cliente.setComplemento(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.COMPLEMENTO)).replace("'", ""));
                } else {
                    cl_Cliente.setComplemento("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).trim().equals("")) {
                    cl_Cliente.setBairro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.BAIRRO)).replace("'", ""));
                } else {
                    cl_Cliente.setBairro("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)).trim().equals("")) {
                    cl_Cliente.setUf(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.UF)));
                } else {
                    cl_Cliente.setUf("PR");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).trim().equals("")) {
                    cl_Cliente.setCidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CIDADE)).replace("'", ""));
                } else {
                    cl_Cliente.setCidade("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).trim().equals("")) {
                    cl_Cliente.setCpfCnpj(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CNPJ)).replace(".", "").replace("-", "").replace("/", ""));
                } else {
                    cl_Cliente.setCpfCnpj("0");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).trim().equals("")) {
                    cl_Cliente.setTelefone(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONE)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefone("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).trim().equals("")) {
                    cl_Cliente.setTelefoneAdicional(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TELEFONEADICIONAL)).replace("(", "").replace("-", "").replace(")", ""));
                } else {
                    cl_Cliente.setTelefoneAdicional("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).trim().equals("")) {
                    cl_Cliente.setFax(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FAX)).replace("(", "").replace("-", "").replace(")", ""));
                    ;
                } else {
                    cl_Cliente.setFax("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).trim().equals("")) {
                    cl_Cliente.setNomeContato(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.CONTATO)).replace("'", ""));
                } else {
                    cl_Cliente.setNomeContato("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).trim().equals("")) {
                    cl_Cliente.setEmail(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.EMAIL)).replace("'", ""));
                } else {
                    cl_Cliente.setEmail("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)).trim().equals("")) {
                    cl_Cliente.setTipoPessoa(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPESSOA)));
                } else {
                    cl_Cliente.setTipoPessoa("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)).trim().equals("")) {
                    cl_Cliente.setTipoCliente(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPCLIENTE)));
                } else {
                    cl_Cliente.setTipoCliente("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).trim().equals("")) {
                    cl_Cliente.setObservacao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.OBSCLIENTE)).replace("'", ""));
                } else {
                    cl_Cliente.setObservacao("espaco");
                }
                //tipoclienteString = "EXPRESSPLANO";
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)).trim().equals("")) {
                    cl_Cliente.setDtUltimaAlteracao(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTULTALTERACAO)));
                } else {
                    cl_Cliente.setDtUltimaAlteracao("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)).trim().equals("")) {
                    cl_Cliente.setDtCadastro(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.DTCADASTRO)));
                } else {
                    cl_Cliente.setDtCadastro("espaco");
                }
                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)).trim().equals("")) {
                    cl_Cliente.setVendedor(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.VENDEDOR)));
                } else {
                    cl_Cliente.setVendedor("espaco");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)).trim().equals("")) {
                    cl_Cliente.setInscEstadual(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.INSCESTADUAL)));
                } else {
                    cl_Cliente.setInscEstadual("espaco");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)).trim().equals("")) {
                    cl_Cliente.setFidelidade(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.FIDELIDADE)));
                } else {
                    cl_Cliente.setFidelidade("espaco");
                }

                if (!rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).equals("null") && !rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)).trim().equals("")) {
                    cl_Cliente.setTipoPreco(rs_Cliente.getString(rs_Cliente.getColumnIndex(CriaBanco.TIPOPRECO)));
                } else {
                    cl_Cliente.setTipoPreco("espaco");
                }

            }

            return true;
        }else{
            return false;
        }

    }

    public boolean fuAlteraClientePedido(){

        if(mdl_Cliente.fuAlteraClientePedido(cl_Cliente.getId(), cl_Cliente.getCdCliente())){
            return true;
        }else{
            return false;
        }

    }

    public String fuBuscaTipoPrecoCliente(){

        String vf_TipoPreco = "N";

        try{
            Cursor rs_TipoPreco = mdl_Cliente.fuBuscaTipoPrecoCliente(cl_Cliente.getCdCliente());
            if(rs_TipoPreco.getCount() > 0){
                vf_TipoPreco = rs_TipoPreco.getString(rs_TipoPreco.getColumnIndex(CriaBanco.TIPOPRECO));
            }else{
                vf_TipoPreco = "N";
            }

        }catch (Exception e){
            vf_TipoPreco = "N";
        }

        return vf_TipoPreco;
    }
}
