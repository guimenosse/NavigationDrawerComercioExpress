package classes;

public class CL_VisaoGeral {

    protected int countTipoVenda, countCanceladosTipoVenda;
    protected double vlVendaBruto, vlVendaDesconto, vlVendaLiquido, vlLucroMedio, vlDescontoTipoVenda, vlTotalTipoVenda;

    protected String dataInicial, dataFinal;

    public CL_VisaoGeral(){
        this.dataInicial = "";
        this.dataFinal = "";
    }

    public int getCountTipoVenda() {
        return countTipoVenda;
    }

    public void setCountTipoVenda(int countTipoVenda) {
        this.countTipoVenda = countTipoVenda;
    }

    public int getCountCanceladosTipoVenda() {
        return countCanceladosTipoVenda;
    }

    public void setCountCanceladosTipoVenda(int countCanceladosTipoVenda) {
        this.countCanceladosTipoVenda = countCanceladosTipoVenda;
    }

    public double getVlVendaBruto() {
        return vlVendaBruto;
    }

    public void setVlVendaBruto(double vlVendaBruto) {
        this.vlVendaBruto = vlVendaBruto;
    }

    public double getVlVendaDesconto() {
        return vlVendaDesconto;
    }

    public void setVlVendaDesconto(double vlVendaDesconto) {
        this.vlVendaDesconto = vlVendaDesconto;
    }

    public double getVlVendaLiquido() {
        return vlVendaLiquido;
    }

    public void setVlVendaLiquido(double vlVendaLiquido) {
        this.vlVendaLiquido = vlVendaLiquido;
    }

    public double getVlLucroMedio() {
        return vlLucroMedio;
    }

    public void setVlLucroMedio(double vlLucroMedio) {
        this.vlLucroMedio = vlLucroMedio;
    }

    public double getVlDescontoTipoVenda() {
        return vlDescontoTipoVenda;
    }

    public void setVlDescontoTipoVenda(double vlDescontoTipoVenda) {
        this.vlDescontoTipoVenda = vlDescontoTipoVenda;
    }

    public double getVlTotalTipoVenda() {
        return vlTotalTipoVenda;
    }

    public void setVlTotalTipoVenda(double vlTotalTipoVenda) {
        this.vlTotalTipoVenda = vlTotalTipoVenda;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }
}
