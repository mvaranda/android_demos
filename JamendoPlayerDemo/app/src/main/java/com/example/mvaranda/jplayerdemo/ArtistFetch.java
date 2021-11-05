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
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelovaranda on 2018-02-21.
 */

public class ArtistFetch extends AsyncTask<String, String, ArtistFetch.Artists> {

    private enum Processing_enum { NONE, ARTIST, TRACK};
    private Processing_enum proc = Processing_enum.NONE;
    private String url_txt;
    final String TAG = "PLAYER_DEMO";
    ArtistFetchDone context;

    public interface ArtistFetchDone {
        void onArtistFetchDone(Artists artists);
    }

    public class Artists {
        List<Artist> artist_list;

        public Artists() {
            artist_list = new ArrayList<Artist>();
        }
    }

        public class Artist {
        String name;
        List<Track> track_list;

            public Artist() {
                track_list = new ArrayList<Track>();
            }
        }

    public class Track {
        String album_name;      // album_name
        String track_name;      // name
        String album_image;     // album_image
        String audio;           // audio
    }

    void parseArtists(InputStream DataInputStream, Artists a){
        //    private Artists parseArtists(InputStream is, String content) {
        String tagname=null, text=null;
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        Artist artist = null;
        Track track = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();


            parser.setInput(DataInputStream, null);


            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        tagname = parser.getName();
                        Log.d(TAG, "Event: START_TAG: " + tagname);
                        if (tagname.equals("artist")) {
                            artist = new Artist();
                            proc = Processing_enum.ARTIST;
                        }
                        if (tagname.equals("track")) {
                            track = new Track();
                            proc = Processing_enum.TRACK;
                        }
                        break;


                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        Log.d(TAG, "Event: TEXT: " + text);
                        switch (proc) {
                            case ARTIST:
                                if (tagname.equals("name"))
                                    artist.name = text;
                                break;
                            case TRACK:
                                if (tagname.equals("name"))
                                    track.track_name = text;
                                if (tagname.equals("album_name"))
                                    track.album_name = text;
                                if (tagname.equals("album_image"))
                                    track.album_image = text;
                                if (tagname.equals("audio"))
                                    track.audio = text;
                                break;
                            default:
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagname = parser.getName();
                        Log.d(TAG, "Event: END_TAG: /" + tagname);
                        if (tagname.equals("artist")) {
                            a.artist_list.add(artist);
                            artist = null;
                            proc = Processing_enum.NONE;
                        }
                        if (tagname.equals("track")) {
                            artist.track_list.add(track);
                            track = null;
                            proc = Processing_enum.ARTIST;
                        }

                        break;

                    default:
                        Log.d(TAG, "Event: unhandled");
                        break;


                }

                eventType = parser.next();
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected Artists doInBackground(String... strings) {
        InputStream DataInputStream = null;
        String ret_string = "ok";
        try {

            URL url = new URL(url_txt);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(15000);
            // auto-ridirection
            cc.setInstanceFollowRedirects(true);
            // set timeout for connection
            cc.setConnectTimeout(15000);
            //set HTTP method to GET
            cc.setRequestMethod("GET");
            //set it to true as we are connecting for input
            cc.setDoInput(true);

            //reading HTTP response code
            int response = cc.getResponseCode(); // where connection really happen
            // wireshark filter: http && ip.addr==162.243.53.59

            //if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
                //String s = convertStreamToString(DataInputStream);
                //DataInputStream.reset();
                //return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in GetData", e);
            return null;
        }

        Artists artists = new Artists();

        parseArtists(DataInputStream, artists);

        return artists;
    }

    public ArtistFetch(String url, Context ctx) {
        super();
        url_txt = url;
        context = (ArtistFetchDone) ctx;
    }

    @Override
    protected void onPostExecute(Artists artists) {
        //super.onPostExecute(artists);
        context.onArtistFetchDone(artists);

    }
}
