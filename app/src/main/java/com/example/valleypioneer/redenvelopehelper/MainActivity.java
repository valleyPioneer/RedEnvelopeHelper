package com.example.valleypioneer.redenvelopehelper;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.grab_button)
    ImageButton grabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews(){
        grabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开无障碍服务列表，需要用户手动开启抢红包功能
                openAccessibilityService();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Utils.isAccessibilitySettingsOn("RedEnvelopeService",this)){
            grabButton.setImageDrawable(getResources().getDrawable(R.drawable.opened));
        }
        else
            grabButton.setImageDrawable(getResources().getDrawable(R.drawable.closed));
    }

    private void openAccessibilityService(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
