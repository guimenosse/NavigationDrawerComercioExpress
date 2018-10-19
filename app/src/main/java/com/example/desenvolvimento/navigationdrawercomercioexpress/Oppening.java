package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.Log;

public class Oppening extends AppCompatActivity {

    private Timer timerAtual = new Timer();
    private TimerTask task;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppening);

        BancoController crud = new BancoController(getBaseContext());
        try {
            try{
                Cursor cursor = crud.carregaObsCliente();
                //String obsCliente = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.OBSCLIENTE));
            }catch (Exception e) {
                crud.alteraTabelaCliente();
            }

            ativaTimer();
        }catch (Exception e){
            MensagemUtil.addMsg(Oppening.this, e.getMessage());
        }
    }

    private void ativaTimer(){
        task = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            BancoController crud = new BancoController(getBaseContext());
                            String validou = crud.validaLogin();



                            if (validou.equals("N")) {
                                Intent secondActivity;
                                secondActivity = new Intent(Oppening.this, LoginActivity.class);
                                startActivity(secondActivity);
                                timerAtual.cancel();
                            } else {
                                try{
                                    //crud.possuiFilialSincronizada();
                                    Intent secondActivity;
                                    secondActivity = new Intent(Oppening.this, Pedidos.class);
                                    startActivity(secondActivity);

                                }catch (Exception e){
                                    Intent secondActivity;
                                    secondActivity = new Intent(Oppening.this, Filial.class);
                                    secondActivity.putExtra("operacao", "L");
                                    startActivity(secondActivity);
                                }
                                timerAtual.cancel();
                            }

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            timerAtual.cancel();
                        }
                    }
                });
            }};

        timerAtual.schedule(task, 1000, 1000);
    }
}
