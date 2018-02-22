package com.example.mvaranda.jplayerdemo;
/***************************************************************************************************

 Copyright 2018, Marcelo Varanda

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ***************************************************************************************************
 *
 *
 * Ref: https://developer.android.com/reference/android/media/MediaPlayer.html
 *      https://developer.android.com/guide/topics/media/mediaplayer.html
 *
 *
 * */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PlayActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener , FetchImage.Listener {

    String url_song, url_picture;
    TextView txt_artist, txt_track, txt_status;
    ImageView img;
    Button bt;
    ProgressBar bar;
    MediaPlayer mm;
    Handler timerHandler;
    Boolean timer_enable = true;
    Boolean anim_enable = true;
    final int anim_size = 6;
    int anim_idx = 0;
    String [] anim_strings = {"Fetching.", "Fetching..","Fetching.","Fetching...","Fetching....","Fetching....."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        txt_artist = findViewById(R.id.id_play_artist);
        txt_track = findViewById(R.id.id_play_track);
        txt_status = findViewById(R.id.id_play_status);
        img = findViewById(R.id.id_play_image);
        bt = findViewById(R.id.id_play_back);
        bar = findViewById(R.id.id_play_bar);
        bar.setMax(100);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        url_song = b.getString("url_song", "Unknown");
        url_picture = b.getString("url_picture", "");
        //b.getString("artist", "");
        b.getString("track", "");
        b.getInt("duration", 0);

        txt_artist.setText("Artist: " + b.getString("artist", ""));
        txt_track.setText("Track: " + b.getString("track", ""));
        txt_status.setText("fetching.");

        //------ start mm ------
        String url = "http://........"; // your URL here
        mm = new MediaPlayer();
        mm.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mm.setDataSource(url_song);
        }
        catch (java.io.IOException e)
        {
            System.out.print("file not found!") ;
            txt_status.setText("Error");
            return;
        }

        /**** lets set callbacks for getting results from prepareAsync: either onPrepared or onError will be called */
        mm.setOnErrorListener(this);
        mm.setOnPreparedListener( this);
        mm.prepareAsync();
        //------------------------

        // If back button event then clean up and finish this Activity.
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanup();
                finish();
            }
        });

        //----- start animation -------
        timerHandler = new Handler();
        timerHandler.postDelayed(anim, 300);
    }

    //--- if Android Back button event then just clean up as it will be finished by the framework.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cleanup();
    }

    private void cleanup(){
        if (mm != null) {
            mm.stop();
            mm.release();
            mm = null;
        }
        timer_enable = false;

    }

    //----- Either animate or update time and progress bar ------
    private Runnable anim = new Runnable() {
        @Override
        public void run() {
            if (timer_enable == false)
                return;
            if (anim_enable == true) {
                anim_idx += 1;
                if (anim_idx >= anim_size)
                    anim_idx = 0;
                txt_status.setText(anim_strings[anim_idx]);

            }

            if (mm.isPlaying()) {
                int current = mm.getCurrentPosition();
                int p = (current * 100) / mm.getDuration();
                txt_status.setText("playing: " + current/1000 + " sec");
                bar.setProgress(p);
            }
            timerHandler.postDelayed(this, 300);
        }
    };


    //------------------ Callbacks (events) from Multimedia or FetchImage ----------

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        anim_enable = false;
        mm.start();
        txt_status.setText("playing");

        // --- so now lets load the Album picture
        if (url_picture != "") {
            new FetchImage(this).execute(url_picture);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mm.release();
        mm = null;
        timer_enable = false;
        txt_status.setText("Error");
        return false;
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        img.setImageBitmap(bitmap);
    }

    @Override
    public void onError() {

    }
}
