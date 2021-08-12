package com.junglist963.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private Button btnNext, btnPrevious, btnPause;
    private TextView txtSongName, txtProgress, txtTotalTime;
    private SeekBar vol_seekBar, music_seekBar;

    String title, filepath;
    int position;
    ArrayList<String>list;

    private MediaPlayer mediaPlayer;

    Runnable runnable;
    Handler handler;
    int totalTime;

    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initViews();
    }

    private void initViews() {
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPause = findViewById(R.id.btnPause);
        txtSongName = findViewById(R.id.txtSongName);
        txtProgress = findViewById(R.id.txtProgress);
        txtTotalTime = findViewById(R.id.txtTotalTime);
        vol_seekBar = findViewById(R.id.vol_SeekBar);
        music_seekBar = findViewById(R.id.music_SeekBar);

        animation = AnimationUtils.loadAnimation(MusicActivity.this,R.anim.translate_animation);
        txtSongName.setAnimation(animation);

        title = getIntent().getStringExtra("title");
        filepath = getIntent().getStringExtra("filepath");
        position = getIntent().getIntExtra("position", -1);
        list = getIntent().getStringArrayListExtra("list");

        mediaPlayer = new MediaPlayer();

        txtSongName.setText(title);

        try {
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.reset();
                if (position == 0){
                    position = list.size()-1;
                }else {
                    position--;
                }
                String newFilePath = list.get(position);
                try {
                    mediaPlayer.setDataSource(newFilePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    btnPause.setBackgroundResource(R.drawable.pause);

                  String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                    txtSongName.setText(newTitle);

                    txtSongName.clearAnimation();
                    txtSongName.startAnimation(animation);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.reset();
                if (position == list.size()-1){
                    position = 0;
                }else {
                    position++;
                }
                String newFilePath = list.get(position);
                try {
                    mediaPlayer.setDataSource(newFilePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    btnPause.setBackgroundResource(R.drawable.pause);

                    String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                    txtSongName.setText(newTitle);

                    txtSongName.clearAnimation();
                    txtSongName.startAnimation(animation);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    btnPause.setBackgroundResource(R.drawable.play);
                }else {
                    mediaPlayer.start();
                    btnPause.setBackgroundResource(R.drawable.pause);
                }
            }
        });
        vol_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    vol_seekBar.setProgress(progress);
                    float volumeLevel = progress/100f;
                    mediaPlayer.setVolume(volumeLevel,volumeLevel);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        music_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    music_seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                totalTime = mediaPlayer.getDuration();
                music_seekBar.setMax(totalTime);
                int currentPosition = mediaPlayer.getCurrentPosition();
                music_seekBar.setProgress(currentPosition);
                handler.postDelayed(runnable, 1000);

                String elapsedTime = createTimeLabel(currentPosition);
                String lastTime = createTimeLabel(totalTime);

                txtProgress.setText(elapsedTime);
                txtTotalTime.setText(lastTime);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.reset();
                        if (position == list.size()-1){
                            position = 0;
                        }else {
                            position++;
                        }
                        String newFilePath = list.get(position);
                        try {
                            mediaPlayer.setDataSource(newFilePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            btnPause.setBackgroundResource(R.drawable.pause);

                            String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/")+1);
                            txtSongName.setText(newTitle);

                            txtSongName.clearAnimation();
                            txtSongName.startAnimation(animation);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        handler.post(runnable);
    }
    public String createTimeLabel(int currentPosition){
        // 1min = 60sec
        //1 sec = 1000millis

        String timeLabel;
        int minute, second;

        minute = currentPosition / 1000 / 60;
        second = currentPosition / 1000 % 60;

        if (second < 10){
            timeLabel = minute +":0"+second;
        }else {
            timeLabel = minute+":"+second;
        }
        return timeLabel;
    }
}