package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Desenvolvimento on 18/01/2016.
 */
public class MensagemUtil {

    public static void addMsg(Activity activity, String mensagem){

        Toast.makeText(activity, mensagem, Toast.LENGTH_LONG).show();

    }
}
