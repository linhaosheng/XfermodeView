package com.linhao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.linhao.view.XfermodeView;

public class MainActivity extends AppCompatActivity {


    private XfermodeView xmode_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmode_view = (XfermodeView) findViewById(R.id.xmode_view);
        xmode_view.setActivity(this);
        xmode_view.clearCanvas();
        xmode_view.setBitmap(XfermodeView.TEMP_PATH);
        xmode_view.setOnTouchCutListener(new XfermodeView.TouchCutListener() {
            @Override
            public void onEvent(int event) {

            }

            @Override
            public void touchBackUrl(String filePath) {

            }

            @Override
            public void touchCutError(String error) {

            }
        });
    }
}
