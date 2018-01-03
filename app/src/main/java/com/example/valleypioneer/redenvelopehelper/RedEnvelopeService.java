package com.example.valleypioneer.redenvelopehelper;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class RedEnvelopeService extends AccessibilityService {
    private List<AccessibilityNodeInfo> parents;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        parents = new ArrayList<>();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if(!texts.isEmpty()){
                    for(CharSequence text : texts){
                        String content = text.toString();
                        if(content.contains("[微信红包 ]")){
                            if(event.getParcelableData() != null && event.getParcelableData() instanceof Notification){
                                Notification notification = (Notification)event.getParcelableData();
                                PendingIntent pi = notification.contentIntent;
                                try{
                                    pi.send();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    String className = event.getClassName().toString();
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        //点击最后一个红包
                        getLastPacket();
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                        //开红包
                        inputClick("com.tencent.mm:id/bg7");
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                        //退出红包
                        inputClick("com.tencent.mm:id/gd");
                    }
                    break;
        }
    }

    private void inputClick(String widgetID){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root != null){
            List<AccessibilityNodeInfo> nodeslist = root.findAccessibilityNodeInfosByViewId(widgetID);
            for(AccessibilityNodeInfo node : nodeslist)
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void getLastPacket(){
        //文字匹配法？控件id法？
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root != null){
           // List<AccessibilityService> nodesList = root.findAccessibilityNodeInfosByText()
        }
    }


    @Override
    public void onInterrupt() {

    }
}
