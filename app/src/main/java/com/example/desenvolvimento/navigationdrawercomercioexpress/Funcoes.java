package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Context;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Desenvolvimento on 12/05/2016.
 */
public class Funcoes {

    int countCli;

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


}
