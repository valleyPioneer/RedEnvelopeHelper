package com.example.valleypioneer.redenvelopehelper;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.accessibility_service_switch)
    Switch accessibilitySwitch;
    @BindView(R.id.notification_listener_service_switch)
    Switch notificationSwicth;
    @BindView(R.id.delay_seek_bar)
    BubbleSeekBar delaySeekBar;
    @BindView(R.id.wechat_version_tv)
    TextView wechatVersion;
    @BindView(R.id.leave_message)
    EditText leaveMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        accessibilitySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessibilityService();
            }
        });

        notificationSwicth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationListenSettings();
            }
        });

        delaySeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                Constants.DELAY_TIME = progressFloat;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        wechatVersion.setText(Constants.WECHAT_VERSION);

        leaveMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Constants.LEAVE_MESSAGE_STRING = s.toString();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Utils.isAccessibilitySettingsOn("RedEnvelopeAccessibilityService", this)) {
            accessibilitySwitch.setChecked(true);
        } else {
            accessibilitySwitch.setChecked(false);
        }

        if (Utils.isNotificationListenerEnabled(this)) {
            notificationSwicth.setChecked(true);
        } else {
            notificationSwicth.setChecked(false);
        }

        delaySeekBar.setProgress(Constants.DELAY_TIME);

        leaveMessage.setText(Constants.LEAVE_MESSAGE_STRING);
    }

    private void openAccessibilityService() {
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
