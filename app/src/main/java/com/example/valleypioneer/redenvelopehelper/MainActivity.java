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
    @BindView(R.id.accessibility_service_button)
    ImageButton accessibilityButton;
    @BindView(R.id.notification_service_button)
    ImageButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews(){
        accessibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开无障碍服务列表，需要用户手动开启抢红包功能
                openAccessibilityService();
            }
        });
        notificationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openNotificationListenSettings();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Utils.isAccessibilitySettingsOn("RedEnvelopeAccessibilityService",this)){
            accessibilityButton.setImageDrawable(getResources().getDrawable(R.drawable.opened));
        }
        else
            accessibilityButton.setImageDrawable(getResources().getDrawable(R.drawable.closed));
        if (Utils.isNotificationListenerEnabled(this)) {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.opened));
        }
        else {
            notificationButton.setImageDrawable(getResources().getDrawable(R.drawable.closed));
        }
    }

    private void openAccessibilityService(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
