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
 * */

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class JPlayerDemoMain extends AppCompatActivity implements ArtistFetch.ArtistFetchDone {

    ArtistFetch artist_fetcher;
    ArtistFetch.Artists parsed_artists;

    final String jamendo_base_url = "http://api.jamendo.com/v3.0/artists/tracks?client_id=";
    final String jamendo_user_id = "xxxxxxx"; // please use your own ID. Get it free from: https://devportal.jamendo.com/
    final String jamendo_options = "&format=xml&limit=20&offset=";
    int offset = 0;
    int offset_increment = 20;
    Boolean show_track = false;
    final int anim_size = 7;
    int anim_idx = 0;
    Handler timerHandler;

    Boolean anim_enable = true;
    String [] anim_strings = {  "-      ",
                                " -     ",
                                "  -    ",
                                "   -   ",
                                "    -  ",
                                "     - ",
                                "      -"};


    ListView lv;
    Button bt_left, bt_right;
    ArtistsAdapter adapter = null;
    TextView anim_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jplayer_demo_main);

        lv = findViewById(R.id.id_list);
        bt_left = findViewById(R.id.id_bt_left);
        bt_right = findViewById(R.id.id_bt_right);
        anim_txt = findViewById(R.id.id_main_anim_txt);
        bt_right.setEnabled(false);
        bt_left.setEnabled(false);

        String query = jamendo_base_url + jamendo_user_id + jamendo_options + offset;
        artist_fetcher = new ArtistFetch(query, this);
        artist_fetcher.execute();

        adapter = new ArtistsAdapter(this);
        lv.setAdapter(adapter);

        bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_track) // right button does nothing for track display... ignore it
                    return;

                // disable the buttons to avoid multiple fetches
                bt_right.setEnabled(false);
                bt_left.setEnabled(false);

                offset += offset_increment;
                String query = jamendo_base_url + jamendo_user_id + jamendo_options + offset;
                artist_fetcher = new ArtistFetch(query, JPlayerDemoMain.this);
                artist_fetcher.execute(); // will get

                //----- start animation -------
                timerHandler = new Handler();
                timerHandler.postDelayed(anim, 300);
                anim_enable = true;

            }
        });

        //----------- Left Button event ------------
        // if displaying list with artist the we fetch past data
        // if displaying list with tracks then this button "goes" back to artist list display
        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_track == false) {
                    // fetch past page if we are not already in the first page
                    if (offset > 0) {
                        // disable buttons to avoid multiple fetches
                        bt_right.setEnabled(false);
                        bt_left.setEnabled(false);
                        offset -= offset_increment;
                        String query = jamendo_base_url + jamendo_user_id + jamendo_options + offset;
                        artist_fetcher = new ArtistFetch(query, JPlayerDemoMain.this);
                        artist_fetcher.execute(); // onArtistFetchDone will be called with result

                        //----- start animation -------
                        timerHandler = new Handler();
                        timerHandler.postDelayed(anim, 300);
                        anim_enable = true;

                    }
                }
                else {
                    // back to artist list display
                    show_track = false;
                    adapter.setShowTrack(show_track);
                    bt_right.setEnabled(true);
                    if (offset <= 0)
                        bt_left.setEnabled(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //----------- User click in a List  row -----------
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // if displaying artists then we go to show tracks
                if (show_track == false) {
                    adapter.setArtist(i);

                    show_track = true;
                    adapter.setShowTrack(show_track);

                    bt_left.setEnabled(true);
                    bt_right.setEnabled(false);
                    adapter.notifyDataSetChanged();
                }
                else {
                    // if displaying tracks then we call activity to play the song
                    Bundle b = new Bundle();
                    b.putString("url_song", adapter.getAudio(i));
                    b.putString("url_picture", adapter.getPicture(i));
                    b.putString("artist", adapter.getArtist(i));
                    b.putString("track", adapter.getTrackName(i));
                    b.putInt("duration", 100);
                    Intent in = new Intent(JPlayerDemoMain.this, PlayActivity.class);
                    in.putExtras(b);
                    startActivityForResult(in, 1);
                }
            }
        });

        //----- start animation -------
        timerHandler = new Handler();
        timerHandler.postDelayed(anim, 300);
        anim_enable = true;


    }

    // ------- perform the animation while fetching data -------
    private Runnable anim = new Runnable() {
        @Override
        public void run() {
            if (anim_enable == true) {
                anim_idx += 1;
                if (anim_idx >= anim_size)
                    anim_idx = 0;
                anim_txt.setText(anim_strings[anim_idx]);
                timerHandler.postDelayed(this, 300);
            }
        }
    };

    //--------- callback when data has been arrived ---------
    @Override
    public void onArtistFetchDone(ArtistFetch.Artists artists) {
        if (artists == null) {
            Toast.makeText(this, "Network Error", Toast.LENGTH_LONG);
            anim_enable = false;
            anim_txt.setText("ERROR");
            bt_right.setEnabled(true);
            return;
        }
        anim_enable = false;
        anim_txt.setText(offset + "~" + (offset + offset_increment - 1));
        adapter.setArtists(artists);
        //parsed_artists = artists;
        adapter.notifyDataSetChanged();
        bt_right.setEnabled(true);
        if (offset >= offset_increment)
            bt_left.setEnabled(true);
    }
}
