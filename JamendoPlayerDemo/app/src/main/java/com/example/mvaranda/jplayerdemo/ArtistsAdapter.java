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
 *
 * */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by mvaranda on 2018-02-22.
 */


class ArtistsAdapter extends ArrayAdapter {
    ArtistFetch.Artist artist = null;
    ArtistFetch.Artists parsed_artists;
    Boolean show_track = false;
    Context ctx;

    public ArtistsAdapter( Context context) {
        super(context, 0);
        ctx = context;
    }

    @Override
    public int getCount() {
        if (parsed_artists == null) return 0;

        if (show_track == false) {
            return parsed_artists.artist_list.size();
        }
        else {
            return artist.track_list.size();
        }
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((JPlayerDemoMain) ctx).getLayoutInflater();
        View row_view;

        if (show_track == false) {
            row_view = inflater.inflate(R.layout.row_layout_artist, null);

            TextView txt = row_view.findViewById(R.id.id_name);
            txt.setText("Artist: " + parsed_artists.artist_list.get(position).name);
            TextView txt2 = row_view.findViewById(R.id.id_num_tracks);
            txt2.setText("Num tracks: " + parsed_artists.artist_list.get(position).track_list.size());

        }
        else {
            row_view = inflater.inflate(R.layout.row_layout_track, null);
            TextView txt = row_view.findViewById(R.id.id_track_name);
            txt.setText("Track: " + artist.track_list.get(position).track_name);
            TextView txt2 = row_view.findViewById(R.id.id_album);
            txt2.setText("Album: " + artist.track_list.get(position).album_name);
        }

        return row_view;

    }
    void setArtist(int i) {
        artist = parsed_artists.artist_list.get(i);
    }

    void setShowTrack(Boolean b) {
        show_track = b;
    }
    void setArtists( ArtistFetch.Artists a) {
        parsed_artists = a;
    }

    String getAudio(int i){
        return artist.track_list.get(i).audio;
    }
    String getPicture(int i){
        return artist.track_list.get(i).album_image;
    }
    String getArtist(int i){
        return artist.name;
    }
    String getTrackName(int i){
        return artist.track_list.get(i).track_name;
    }
}
