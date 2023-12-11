package com.example.camilodesarrollo.abicap;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Miguel Bustamante on 04/07/2023.
 */

public class Beeper {

    private MediaPlayer reproductor;

    public void activar(Context con){
        try {
            if(reproductor == null){
                reproductor = MediaPlayer.create(con, R.raw.beep4);
            }
            reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusic();
                }
            });

            reproductor.setLooping(false);
            reproductor.start();
        }catch(Exception e){
            Log.e(TAG, "The exception caught while executing the process. ("+e+")");
        }
    }

    public void activarIncorrecto(Context con){
        try {
            if(reproductor == null){
                reproductor = MediaPlayer.create(con, R.raw.beep03);
            }

            reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusic();
                }
            });

            reproductor.setLooping(false);
            reproductor.start();
        }catch(Exception e){
            Log.e(TAG, "The exception caught while executing the process. ("+e+")");
        }
    }

    private void stopMusic(){
        try {
            if(reproductor != null){
                reproductor.release();
                reproductor = null;
            }
        }catch(Exception e){
            Log.e(TAG, "The exception caught while executing the process. ("+e+")");
        }
    }













}
