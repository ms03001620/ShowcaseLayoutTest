package org.mark.showcaselayouttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "ddddd");
                ((ShowcaseLayout)findViewById(R.id.layout)).setDisplay(true);
            }
        });

        changePostion();
    }

    private void changePostion() {
        final Button btn = (Button) findViewById(R.id.btn);
        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        btn.postDelayed(new Runnable() {
            @Override
            public void run() {
                lp.setMargins(0,0,0,0);
                btn.setLayoutParams(lp);
            }
        }, 5000);
    }

}
