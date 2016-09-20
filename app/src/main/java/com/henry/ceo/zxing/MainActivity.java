package com.henry.ceo.zxing;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.input.InputManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView = null;
    private EditText editText = null;
    private Button creatQr = null;
    private TextView textView = null;
    private Button decode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.textView);
        textView = (TextView) findViewById(R.id.textView2);
        decode = (Button) findViewById(R.id.button2);
        creatQr = (Button) findViewById(R.id.button);
        creatQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.domain_logo);
                if(editText != null && editText.getText() != null && editText.getText().toString() != null){
                    imageView.setImageBitmap(Utils.creatQr(1500, 1500, editText.getText().toString(), null, null));
                }
            }
        });

        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("解析为："+Utils.decodeQr(((BitmapDrawable) (imageView.getDrawable())).getBitmap()));
            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.henry.ceo.zxing.webview");
                startActivity(intent);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("sysout","Long click");
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Utils.shareImage( bitmap,MainActivity.this);
                return false;
            }
        });
    }
}
