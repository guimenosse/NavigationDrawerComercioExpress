package models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Desenvolvimento on 26/11/2015.
 */
public class CriaBanco extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "db_express_mobile.db";
    public static final String TABELA = "tb_cadcli";
    public static final String ID = "_id";
    public static final String CDCLIENTE = "cdcliente";
    public static final String RZSOCIAL = "rzsocial";
    public static final String NMFANTASIA = "nmfantasia";
    public static final String CEP = "cep";
    public static final String ENDERECO = "endereco";
    public static final String NUMERO = "numero";
    public static final String COMPLEMENTO = "complemento";
    public static final String BAIRRO = "bairro";
    public static final String UF = "uf";
    public static final String CIDADE = "cidade";
    public static final String PAIS = "pais";
    public static final String TIPOPESSOA = "tipopessoa";
    public static final String CNPJ = "cgc";
    public static final String INSCESTADUAL = "inscestadual";
    public static final String TELEFONE = "telefone";
    public static final String TELEFONEADICIONAL = "telefoneadicional";
    public static final String FAX = "fax";
    public static final String CONTATO = "nmcontatocliente";
    public static final String EMAIL = "email";
    public static final String TIPCLIENTE = "tipcliente";
    public static final String VENDEDOR = "vendedor";
    public static final String DTULTALTERACAO = "dtultalteracao";
    public static final String FGSINCRONIZADO = "fgsincronizado";
    public static final String DTCADASTRO = "dtcadastro";
    public static final String OBSCLIENTE = "obscliente";
    public static final String CLASSIFICACAO = "classificacao";
    public static final String FIDELIDADE = "fidelidade";
    public static final String TIPOPRECO = "tipopreco";
    public static final String FGBLOQUEIO = "fgbloqueio";
    public static final int VERSAO = 1;

    public static final String TABELALOGIN = "login";
    public static final String USUARIOLOGIN = "usuario";
    public static final String SENHALOGIN = "senha";
    public static final String CDVENDEDORDEFAULT = "cdvendedor";
    public static final String NMUSUARIOSISTEMA = "nmusuariosistema";
    public static final String CDCLIENTEBANCO = "cdclientebanco";

    public static final String TABELASINCRONIZACAO = "sincronizacao";
    public static final String DTULTSINCRONIZACAO = "ultdtsincronizacao";
    public static final String ULTCDCLIENTE = "ultcdcliente";
    public static final String ULTDTATUALIZACAO = "ultdtatualizacao";

    public static final String TABELACONFIGURACAO = "configuracao";
    public static final String FGCONTROLAESTOQUEPEDIDO = "fgcontrolaestoquepedido";

    public static  final String TABELAPRODUTOS = "cadpro";
    public static final String CDPRODUTO = "cdproduto";
    public static final String DESCRICAO = "descricao";
    public static final String COMPLEMENTODESCRICAO = "complementodescricao";
    public static final String ESTOQUEATUAL = "estoqueatual";
    public static final String VALORUNITARIO = "valorunitario";
    public static final String VALORATACADO = "valoratacado";
    public static final String DESCMAXPERMITIDO = "descmaxpermitido";
    public static final String DESCMAXPERMITIDOA = "descmaxpermitidoa";
    public static final String DESCMAXPERMITIDOB = "descmaxpermitidob";
    public static final String DESCMAXPERMITIDOC = "descmaxpermitidoc";
    public static final String DESCMAXPERMITIDOD = "descmaxpermitidod";
    public static final String DESCMAXPERMITIDOE = "descmaxpermitidoe";
    public static final String DESCMAXPERMITIDOFIDELIDADE = "descmaxpermitidofidelidade";
    public static final String QTDEDISPONIVEL = "qtdedisponivel";
    public static final String CDREFESTOQUE = "cdrefestoque";

    public static final String TABELAMESTREPEDIDO = "mestrepedido";
    public static final String NUMPEDIDO = "numpedido";
    public static final String CONDPGTO = "condpgto";
    public static final String VLTOTAL = "vltotal";
    public static final String VLDESCONTO = "vldesconto";
    public static final String VLFRETE = "vlfrete";
    public static final String CDEMITENTE = "cdemitente";
    public static final String OBS = "obs";
    public static final String DTEMISSAO = "dtemissao";
    public static final String CDVENDEDOR = "cdvendedor";
    public static final String FGSITUACAO = "fgsituacao";
    public static final String NUMPEDIDOSERVIDOR = "numpedidoservidor";

    public static final String TABELAITEMPEDIDO = "itempedido";
    public static final String QTDE = "qtde";
    public static final String PERCDESCONTO = "percdesconto";
    public static final String VLUNITARIO = "vlunitario";
    public static final String VLLIQUIDO = "vlliquido";
    public static final String VLMAXDESCPERMITIDO = "vlmaxdescpermitido";
    public static final String OBSERVACAOITEMPEDIDO = "observacaoitempedido";

    public static final String TABELABANCODADOS = "bancodados";
    public static final String IP = "ip";
    public static final String USUARIOBANCO = "usuario";
    public static final String SENHABANCO = "senha";
    public static final String NMBANCO = "nmbanco";

    public static final String TABELATIPCLIENTE = "tipcli";
    public static final String CDTIPO = "cdtipo";
    public static final String NMTIPO = "nmtipo";

    public static final String TABELAFILIAL = "filial";
    public static final String CDFILIAL = "cdfilial";
    public static final String FILIAL = "filial";
    public static final String FGSELECIONADA = "fgselecionada";
    public static final String FGTROCAFILIAL = "fgtrocafilial";
    public static final String PRECOINDIVIDUALIZADO = "precoindividualizado";

    public CriaBanco(Context context){

        super(context, NOME_BANCO, null, VERSAO);
        //context.deleteDatabase("db_express_mobile.db");
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String sql = "CREATE TABLE " + TABELALOGIN + "("
                + ID + " integer primary key autoincrement, "
                + USUARIOLOGIN + " text, "
                + SENHALOGIN + " text, "
                + CDVENDEDORDEFAULT + " text, "
                + CDCLIENTEBANCO + " text, "
                + NMUSUARIOSISTEMA + " text "
                + ")";

        db.execSQL(sql);


        sql = "CREATE TABLE " + TABELASINCRONIZACAO + "("
                + DTULTSINCRONIZACAO + " datetime primary key, "
                + ULTCDCLIENTE + " integer, "
                + ULTDTATUALIZACAO + " datetime "
                + ")";

        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + TABELACONFIGURACAO + "("
                + FGCONTROLAESTOQUEPEDIDO + " text"
                + ")";

        db.execSQL(sql);

        sql = "INSERT INTO " + TABELACONFIGURACAO + " VALUES ('N')";
        db.execSQL(sql);

        sql = "CREATE TABLE " + TABELATIPCLIENTE + "("
                + ID + " integer primary key autoincrement, "
                + CDTIPO + " text, "
                + NMTIPO + " text )";

        db.execSQL(sql);

        sql = "CREATE TABLE cidadesibge(cdcidadeibge integer primary key, cdufibge integer, nmcidade text)";

        db.execSQL(sql);

        sql = "CREATE TABLE cadest(uf text, nmestado text, cdibge integer primary key)";

        db.execSQL(sql);

        sql = "CREATE TABLE "+TABELA+"("
                + ID + " integer primary key autoincrement, "
                + CDCLIENTE + " text, "
                + RZSOCIAL + " text, "
                + NMFANTASIA + " text, "
                + CEP + " text, "
                + ENDERECO + " text, "
                + NUMERO + " text, "
                + COMPLEMENTO + " text, "
                + BAIRRO + " text, "
                + UF + " text, "
                + CIDADE + " text, "
                + TIPOPESSOA + " text, "
                + CNPJ + " text, "
                + INSCESTADUAL + " text, "
                + TELEFONE + " text, "
                + TELEFONEADICIONAL + " text, "
                + FAX + " text, "
                + CONTATO + " text, "
                + EMAIL + " text, "
                + TIPCLIENTE + " text, "
                + VENDEDOR + " text, "
                + DTULTALTERACAO + " datetime, "
                + DTCADASTRO + " datetime, "
                + OBSCLIENTE + " text, "
                + CLASSIFICACAO + " text, "
                + FIDELIDADE + " text, "
                + TIPOPRECO + " text, "
                + FGBLOQUEIO + " text DEFAULT 'N', "
                + FGSINCRONIZADO + " text )";

        db.execSQL(sql);

        sql = "CREATE TABLE " + TABELAPRODUTOS + "(" +
                ID + " integer primary key autoincrement, " +
                CDPRODUTO + " text, " +
                DESCRICAO + " text, " +
                ESTOQUEATUAL + " integer, " +
                VALORUNITARIO + " real, " +
                VALORATACADO + " real, " +
                DESCMAXPERMITIDO + " real, " +
                DESCMAXPERMITIDOA + " real, " +
                DESCMAXPERMITIDOB + " real, " +
                DESCMAXPERMITIDOC + " real, " +
                DESCMAXPERMITIDOD + " real, " +
                DESCMAXPERMITIDOE + " real, " +
                DESCMAXPERMITIDOFIDELIDADE + " real, " +
                QTDEDISPONIVEL +  " real, " +
                CDREFESTOQUE + " text, " +
                DTULTALTERACAO + " datetime)";

        db.execSQL(sql);


        sql = "CREATE TABLE " + TABELAMESTREPEDIDO + "(" +
                ID + " integer primary key autoincrement, " +
                CONDPGTO + " text, " +
                VLTOTAL + " real, " +
                PERCDESCONTO + " real, " +
                VLDESCONTO + " real, " +
                VLFRETE + " real, " +
                CDEMITENTE + " text, " +
                RZSOCIAL + " text, " +
                OBS + " text, " +
                DTEMISSAO + " datetime, " +
                FGSITUACAO + " text, " +
                NUMPEDIDOSERVIDOR + " text, " +
                CDVENDEDOR + " integer)";

        db.execSQL(sql);

        sql = "CREATE TABLE " + TABELAITEMPEDIDO + "(" +
                ID + " integer primary key, " +
                NUMPEDIDO + " integer, " +
                CDPRODUTO + " text, " +
                DESCRICAO + " text, " +
                QTDE + " real, " +
                PERCDESCONTO + " real, " +
                VLMAXDESCPERMITIDO + " real, " +
                VLDESCONTO + " real, " +
                VLUNITARIO + " real, " +
                VLLIQUIDO + " real, " +
                VLTOTAL + " real, " +
                OBSERVACAOITEMPEDIDO + " text, " +
                "FOREIGN KEY(" + NUMPEDIDO + ") REFERENCES mestrepedido(" + ID + "), " +
                "FOREIGN KEY(" + CDPRODUTO + ") REFERENCES cadpro(" + ID + "))";

        db.execSQL(sql);


        sql = "CREATE TABLE IF NOT EXISTS " + TABELAFILIAL + "(" +
                ID + " integer primary key, " +
                CDFILIAL + " integer, " +
                FILIAL + " text, " +
                FGSELECIONADA + " text, " +
                FGTROCAFILIAL + " text, " +
                PRECOINDIVIDUALIZADO + " text DEFAULT 'N')";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion){

        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);

    }
}
