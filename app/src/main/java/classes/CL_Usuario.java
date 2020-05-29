package classes;

public class CL_Usuario {

    protected String id;
    protected String usuario;
    protected String senha;
    protected String cdClienteBanco;
    protected String nmUsuarioSistema;
    protected String cdVendedorDefault;
    protected String dtUltimaSincronizacao;


    public CL_Usuario(){
        this.id = "";
        this.usuario = "";
        this.senha = "";
        this.cdClienteBanco = "";
        this.nmUsuarioSistema = "";
        this.cdVendedorDefault = "";
        this.dtUltimaSincronizacao = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCdClienteBanco() {
        return cdClienteBanco;
    }

    public void setCdClienteBanco(String cdClienteBanco) {
        this.cdClienteBanco = cdClienteBanco;
    }

    public String getNmUsuarioSistema() {
        return nmUsuarioSistema;
    }

    public void setNmUsuarioSistema(String nmUsuarioSistema) {
        this.nmUsuarioSistema = nmUsuarioSistema;
    }

    public String getCdVendedorDefault() {
        return cdVendedorDefault;
    }

    public void setCdVendedorDefault(String cdVendedorDefault) {
        this.cdVendedorDefault = cdVendedorDefault;
    }

    public String getDtUltimaSincronizacao() {
        return dtUltimaSincronizacao;
    }

    public void setDtUltimaSincronizacao(String dtUltimaSincronizacao) {
        this.dtUltimaSincronizacao = dtUltimaSincronizacao;
    }
}
