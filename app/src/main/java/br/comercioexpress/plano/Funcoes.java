package br.comercioexpress.plano;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import classes.CL_Clientes;
import classes.CL_Filial;
import classes.CL_TiposCliente;
import classes.CL_Usuario;
import controllers.CTL_Clientes;
import controllers.CTL_Filial;
import controllers.CTL_Usuario;
import models.MDL_Clientes;

/**
 * Created by Desenvolvimento on 12/05/2016.
 */
public class Funcoes {

    int countCli;

    CL_Usuario cl_Usuario;
    CTL_Usuario ctl_Usuario;

    Context vc_Context;

    int TIMEOUT_MILLISEC = 10000;

    String bodyString = "";

    public Funcoes(){

    }

    public Funcoes(Context context){

        this.vc_Context = context;

        cl_Usuario= new CL_Usuario();
        ctl_Usuario = new CTL_Usuario(vc_Context, cl_Usuario);
        cl_Usuario = ctl_Usuario.fuSelecionarUsuarioAPI();
        bodyString = ctl_Usuario.buildRequestBodyString(cl_Usuario);

    }

    public boolean FU_VerificaAutorizacaoAPI(String cdsistema, String cdfuncao){

        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody bodyUsuario = RequestBody.create(mediaType, bodyString);

            Request request = new Request.Builder()
                    .url("http://35.247.249.209:70/VerificarAutorizacao/" + cdsistema + "/" + cdfuncao)
                    .method("POST", bodyUsuario)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

            int vf_Response = response.code();

            if(vf_Response == 200){
                String jsonResponse = response.body().string();

                if (!jsonResponse.equals("null") && !jsonResponse.equals("") && jsonResponse.equals("true")) {
                    return true;
                } else {
                    return false;
                }

            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }
    }

    public boolean verificaAutorizacao(String cdsistema, String cdfuncao, String usuario, String cdclientebanco) {


        int TIMEOUT_MILLISEC = 10000;
        try {
            HttpParams p = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(p, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(p, TIMEOUT_MILLISEC);

            HttpClient httpclient = new DefaultHttpClient(p);
            String url = "http://www.planosistemas.com.br/" +
                    "WebService2.php?user=" + cdclientebanco + "&format=json&num=10&method=autorizacao&cdsistema=" + cdsistema + "&cdfuncao=" + cdfuncao + "&nmusuario=" + usuario + "";
            HttpPost httppost = new HttpPost(url);

            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                // Parse
                JSONObject json = new JSONObject(responseBody);
                JSONArray jArray = json.getJSONArray("posts");
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                    String s = e.getString("post");

                    if (!s.equals("null") && !s.equals("") && s.equals("true")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String gerarDataFinal(int mes, int ano){

        String _mesString = String.valueOf(mes);
        if(mes < 10){
            _mesString = "0" + _mesString;
        }

        if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
            return "" + "31/" + _mesString + "/" + String.valueOf(ano);
        }else if(mes == 2){
            return "" + "29/" + _mesString + "/" + String.valueOf(ano);
            //return "" + String.valueOf(ano) + "-29-" + String.valueOf(mes) + " 00:00:00";
        }else{
            return "" + "30/" + _mesString + "/" + String.valueOf(ano);
            //return "" + String.valueOf(ano) + "-30-" + String.valueOf(mes) + " 00:00:00";
        }
    }

    public static String gerarDataFinalComparacao(int mes, int ano){

        String _mesString = String.valueOf(mes);
        if(mes < 10){
            _mesString = "0" + _mesString;
        }

        if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
            return "" + "31" + _mesString + "" + String.valueOf(ano);
        }else if(mes == 2){
            return "" + "29" + _mesString + "" + String.valueOf(ano);
            //return "" + String.valueOf(ano) + "-29-" + String.valueOf(mes) + " 00:00:00";
        }else{
            return "" + "30" + _mesString + "" + String.valueOf(ano);
            //return "" + String.valueOf(ano) + "-30-" + String.valueOf(mes) + " 00:00:00";
        }
    }

    public static String gerarNomeDia(int dia){

        String nomeDia = "";

        switch(dia) {
            case Calendar.SUNDAY:
                nomeDia = "Domingo";
                break;
            case Calendar.MONDAY:
                nomeDia = "Segunda-feira";
                break;
            case Calendar.TUESDAY:
                nomeDia = "Terça-feira";
                break;
            case Calendar.WEDNESDAY:
                nomeDia = "Quarta-feira";
                break;
            case Calendar.THURSDAY:
                nomeDia = "Quinta-feira";
                break;
            case Calendar.FRIDAY:
                nomeDia = "Sexta-feira";
                break;
            case Calendar.SATURDAY:
                nomeDia = "Sábado";
                break;
        }

        return nomeDia;

    }

    public static String gerarNomeMes(int mes){

        String nomeMes = "";

        switch (mes){
            case 1:
                nomeMes = "Janeiro";
                break;
            case 2:
                nomeMes = "Fevereiro";
                break;
            case 3:
                nomeMes = "Março";
                break;
            case 4:
                nomeMes = "Abril";
                break;
            case 5:
                nomeMes = "Maio";
                break;
            case 6:
                nomeMes = "Junho";
                break;
            case 7:
                nomeMes = "Julho";
                break;
            case 8:
                nomeMes = "Agosto";
                break;
            case 9:
                nomeMes = "Setembro";
                break;
            case 10:
                nomeMes = "Outubro";
                break;
            case 11:
                nomeMes = "Novembro";
                break;
            case 12:
                nomeMes = "Dezembro";
                break;
        }

        return nomeMes;

    }
    public static String gerarSiglaMes(int mes){
        String siglaMes = "";

        switch (mes){
            case 1:
                siglaMes = "JAN";
                break;
            case 2:
                siglaMes = "FEV";
                break;
            case 3:
                siglaMes = "MAR";
                break;
            case 4:
                siglaMes = "ABR";
                break;
            case 5:
                siglaMes = "MAI";
                break;
            case 6:
                siglaMes = "JUN";
                break;
            case 7:
                siglaMes = "JUL";
                break;
            case 8:
                siglaMes = "AGO";
                break;
            case 9:
                siglaMes = "SET";
                break;
            case 10:
                siglaMes = "OUT";
                break;
            case 11:
                siglaMes = "NOV";
                break;
            case 12:
                siglaMes = "DEZ";
                break;
        }

        return siglaMes;
    }

    public static String gerarNumeroMes(String mes){
        String numeroMes = "";


        switch (mes.toUpperCase()){
            case "JAN":
                numeroMes = "01";
                break;
            case "FEV":
                numeroMes = "02";
                break;
            case "FEB":
                numeroMes = "02";
                break;
            case "MAR":
                numeroMes = "03";
                break;
            case "ABR":
                numeroMes = "04";
            case "APR":
                numeroMes = "04";
                break;
            case "MAI":
                numeroMes = "05";
            case "MAY":
                numeroMes = "05";
                break;
            case "JUN":
                numeroMes = "06";
                break;
            case "JUL":
                numeroMes = "07";
                break;
            case "AGO":
                numeroMes = "08";
            case "AUG":
                numeroMes = "08";
                break;
            case "SET":
                numeroMes = "09";
            case "SEP":
                numeroMes = "09";
                break;
            case "OUT":
                numeroMes = "10";
            case "OCT":
                numeroMes = "10";
                break;
            case "NOV":
                numeroMes = "11";
                break;
            case "DEZ":
                numeroMes = "12";
                break;
            case "DEC":
                numeroMes = "12";
                break;
        }
        return numeroMes;
    }

}
