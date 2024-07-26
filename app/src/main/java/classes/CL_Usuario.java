package classes;

public class CL_Usuario {

    protected String id;
    protected String cdUsuario;
    protected String usuario;
    protected String senha;
    protected String cdClienteBanco;
    protected String nmUsuarioSistema;
    protected String cdVendedorDefault;
    protected String dtUltimaSincronizacao;


    protected String ip;
    protected String usuarioSQL;
    protected String senhaSQL;
    protected String nmBanco;

    public CL_Usuario(){
        this.id = "";
        this.cdUsuario = "";
        this.usuario = "";
        this.senha = "";
        this.cdClienteBanco = "";
        this.nmUsuarioSistema = "";
        this.cdVendedorDefault = "";
        this.dtUltimaSincronizacao = "";

        this.ip = "";
        this.usuarioSQL = "";
        this.senhaSQL = "";
        this.nmBanco = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdUsuario() {
        return id;
    }

    public void setCdUsuario(String cdUsuario) {
        this.cdUsuario = cdUsuario;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsuarioSQL() {
        return usuarioSQL;
    }

    public void setUsuarioSQL(String usuarioSQL) {
        this.usuarioSQL = usuarioSQL;
    }

    public String getSenhaSQL() {
        return senhaSQL;
    }

    public void setSenhaSQL(String senhaSQL) {
        this.senhaSQL = senhaSQL;
    }

    public String getNmBanco() {
        return nmBanco;
    }

    public void setNmBanco(String nmBanco) {
        this.nmBanco = nmBanco;
    }

}
