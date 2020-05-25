package classes;

public class CL_Pedidos {

    protected String numPedido;
    protected String condPgto;
    protected String vlTotal;
    protected String percDesconto;
    protected String vlDesconto;
    protected String cdCliente;
    protected String nomeRzSocial;
    protected String obsPedido;
    protected String dtEmissao;
    protected String fgSituacao;
    protected String cdVendedor;

    protected String qtdeItens;
    protected String vlTotalItens;

    protected String vlFrete;

    protected String numPedidoServidor;

    public CL_Pedidos(){
        this.numPedido = "";
        this.condPgto = "";
        this.vlTotal = "0.0";
        this.percDesconto = "";
        this.vlDesconto = "0.0";
        this.cdCliente = "";
        this.nomeRzSocial = "";
        this.obsPedido = "";
        this.dtEmissao = "";
        this.fgSituacao = "ABERTO";
        this.cdVendedor = "";

        this.qtdeItens = "0";
        this.vlTotalItens = "0.0";

        this.vlFrete = "0.00";

        this.numPedidoServidor = "";
    }

    public String getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(String numPedido) {
        this.numPedido = numPedido;
    }

    public String getCondPgto() {
        return condPgto;
    }

    public void setCondPgto(String condPgto) {
        this.condPgto = condPgto;
    }

    public String getVlTotal() {
        return vlTotal;
    }

    public void setVlTotal(String vlTotal) {
        this.vlTotal = vlTotal;
    }

    public String getPercDesconto() {
        return percDesconto;
    }

    public void setPercDesconto(String percDesconto) {
        this.percDesconto = percDesconto;
    }

    public String getVlDesconto() {
        return vlDesconto;
    }

    public void setVlDesconto(String vlDesconto) {
        this.vlDesconto = vlDesconto;
    }

    public String getCdCliente() {
        return cdCliente;
    }

    public void setCdCliente(String cdCliente) {
        this.cdCliente = cdCliente;
    }

    public String getNomeRzSocial() {
        return nomeRzSocial;
    }

    public void setNomeRzSocial(String nomeRzSocial) {
        this.nomeRzSocial = nomeRzSocial;
    }

    public String getObsPedido() {
        return obsPedido;
    }

    public void setObsPedido(String obsPedido) {
        this.obsPedido = obsPedido;
    }

    public String getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(String dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getFgSituacao() {
        return fgSituacao;
    }

    public void setFgSituacao(String fgSituacao) {
        this.fgSituacao = fgSituacao;
    }

    public String getCdVendedor() {
        return cdVendedor;
    }

    public void setCdVendedor(String cdVendedor) {
        this.cdVendedor = cdVendedor;
    }

    public String getQtdeItens() {
        return qtdeItens;
    }

    public void setQtdeItens(String qtdeItens) {
        this.qtdeItens = qtdeItens;
    }

    public String getVlTotalItens() {
        return vlTotalItens;
    }

    public void setVlTotalItens(String vlTotalItens) {
        this.vlTotalItens = vlTotalItens;
    }

    public String getVlFrete() {
        return vlFrete;
    }

    public void setVlFrete(String vlFrete) {
        this.vlFrete = vlFrete;
    }

    public String getNumPedidoServidor() {
        return numPedidoServidor;
    }

    public void setNumPedidoServidor(String numPedidoServidor) {
        this.numPedidoServidor = numPedidoServidor;
    }
}
