package org.mark.showcaselayouttest;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import static org.mark.showcaselayouttest.HintShowcaseDrawer.BELOW_SHOWCASE;

public class MainActivity extends AppCompatActivity {
    ShowcaseLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "ddddd");
                layout.setDisplay(true);
            }
        });


        layout = (ShowcaseLayout)findViewById(R.id.layout);

        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        ViewGroup group = (ViewGroup) parent.getParent().getParent();
        int count = group.getChildCount();

        layout.setShowcaseDrawer(new HintShowcaseDrawer(MainActivity.this,
                R.string.content_2,
                BELOW_SHOWCASE,
                R.dimen.hint_bg_width,
                R.dimen.hint_text_size,
                R.dimen.btn_width,
                R.dimen.btn_width),
                R.id.btn);

        layout.setParent(group, count);


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
