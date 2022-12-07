package com.example.button;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;



public class SuccessScreen extends AppCompatActivity {
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_screen);
        mediaPlayer= MediaPlayer.create(SuccessScreen.this,R.raw.sound);
        mediaPlayer.start();
        }
    }