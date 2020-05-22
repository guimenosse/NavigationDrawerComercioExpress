package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.Log;

public class Oppening extends AppCompatActivity {

    private Timer timerAtual = new Timer();
    private TimerTask task;
    private final Handler handler = new Handler();

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppening);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ImageView demoImage = (ImageView) findViewById(R.id.img_LogoCentro);
        int imagesToShow[] = { R.drawable.logo_expres_mobile, R.drawable.logoplano};

        animate(demoImage, imagesToShow, 0,false);

        Timer();


    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 1000;
        int fadeOutDuration = 500;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1,forever); //Calls itself until it gets to the end of the array
                }
                else {
                    if (forever){
                        animate(imageView, images, 0,forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void Timer(){
        timer = new Timer();
        Task task = new Task();

        timer.schedule(task, 2600, 2600);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timer.cancel();
                    timer.purge();

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
                                //startActivity(secondActivity);
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fadeout, R.anim.fadeout);
                                ActivityCompat.startActivity(Oppening.this, secondActivity, activityOptionsCompat.toBundle());

                            }catch (Exception e){
                                Intent secondActivity;
                                secondActivity = new Intent(Oppening.this, Filial.class);
                                secondActivity.putExtra("operacao", "L");
                                //startActivity(secondActivity);
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fadeout, R.anim.fadeout);
                                ActivityCompat.startActivity(Oppening.this, secondActivity, activityOptionsCompat.toBundle());
                            }
                        }

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            });
        }
    }

    /*private void ativaTimer(){
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
    }*/
}
