package com.example.marcelovaranda.newview;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.IOException;
import java.io.InputStream;

public class NewViewActivity extends AppCompatActivity implements View.OnClickListener {
    Button c1, c2, c3;
    ImageButton undo_b;
    EditText txt;
    NewView my_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_view);

        c1 = findViewById(R.id.id_b1);
        c2 = findViewById(R.id.id_b2);
        c3 = findViewById(R.id.id_b3);
        undo_b = findViewById(R.id.id_undo);
        txt = findViewById(R.id.id_editText);
        my_view = findViewById(R.id.id_my_view);

        c1.setOnClickListener(this);
        c2.setOnClickListener(this);
        c3.setOnClickListener(this);
        undo_b.setOnClickListener(this);

        my_view.setBitmap(getBitmap());
    }

    @Override
    public void onClick(View v) {
        if (v == c1) {
            my_view.setLineColor(0xffff0000);
        }
        else if (v == c2) {
            my_view.setLineColor(0xff00ff00);
        }
        else if (v == c3) {
            my_view.setLineColor(0xff0000ff);
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Choose color")
                    .initialColor(Color.BLUE)
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
                            Toast.makeText(NewViewActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            //changeBackgroundColor(selectedColor);
                            Toast.makeText(NewViewActivity.this, "onColorSelected: 0x" + selectedColor, Toast.LENGTH_SHORT).show();
                            my_view.setLineColor(selectedColor);
                            c3.setBackgroundColor(selectedColor);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();
        }
        else if (v == undo_b) {
            my_view.undo();
        }
        my_view.setStrokeWidth(Integer.parseInt(txt.getText().toString() )	);
    }

    private Bitmap getBitmap() {
        AssetManager assetManager = getAssets();
        InputStream fileStream = null;
        Bitmap bitmap = null;
        try {
            fileStream = assetManager.open("logo.jpg");
            if (fileStream != null) {
                bitmap = BitmapFactory.decodeStream(fileStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}
