package com.example.valleypioneer.redenvelopehelper;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class RedEnvelopeService extends AccessibilityService {
    private static final String TAG = "RedEnvelopeService";
    private boolean isProcessing = false; /** 记录是否处于一轮抢红包的状态中 */

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                if(!isProcessing){
                    List<CharSequence> texts = event.getText();
                    Log.i(TAG,"notification texts is " + texts.toString());
                    if(!texts.isEmpty()){
                        for(CharSequence text : texts){
                            String content = text.toString();
                            if(content.contains("[微信红包]")){
                                if(event.getParcelableData() != null && event.getParcelableData() instanceof Notification){
                                    isProcessing = true; /** 开始处理本次通知 */
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
                }

                break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    String className = event.getClassName().toString();
                    Log.i(TAG,"className is " + className);

                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        if(isProcessing){
                            Log.i(TAG,"开始领取红包！");
                            getLastPacket(Constants.GET_RED_ENVELOPE);
                        }

                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                        Log.i(TAG,"开始抢红包！");
                        checkMoneyLeft(Constants.GRAB);
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                        if(isProcessing){
                            Log.i(TAG,"退出红包详情界面！");
                            /** 退出红包详情界面，返回聊天界面 */
                            performGlobalAction(GLOBAL_ACTION_BACK);
                            performGlobalAction(GLOBAL_ACTION_HOME);
                        }
                        isProcessing = false; /** 完成此次通知处理,准备下次通知处理*/
                    }
                    break;
                default:
        }
    }

    private void checkMoneyLeft(String widgetID){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        for(int i = 0;i < root.getChildCount();i++){
            Log.i(TAG,"child is " + root.getChild(i));
        }
        Log.i(TAG,"rootNode2 id is" + root.getClassName());
        if(root != null){
            List<AccessibilityNodeInfo> nodeslist = root.findAccessibilityNodeInfosByViewId(widgetID);
            Log.i(TAG,"nodesList.size() is" + nodeslist.size());
            if(nodeslist.size() != 0) {
                for(AccessibilityNodeInfo node : nodeslist)
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        }
        isProcessing = false;
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    private boolean inputNormalClick(String widgetID){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        boolean res = true;
        if(root != null){
            List<AccessibilityNodeInfo> nodeslist = root.findAccessibilityNodeInfosByViewId(widgetID);
            Log.i(TAG,"nodesList.size() is" + nodeslist.size());
            if (nodeslist.size() == 0)
                res = false;
            else
                for(AccessibilityNodeInfo node : nodeslist)
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return res;
    }

    private void getLastPacket(String widgetID){
        /** 建议使用控件id法效率更高 */
        AccessibilityNodeInfo root = getRootInActiveWindow();
        for(int i = 0;i < root.getChildCount();i++){
            Log.i(TAG,"1.....child is " + root.getChild(i));
        }
        Log.i(TAG,"rootNode1 id is" + root.getClassName());
        if(root != null){
            List<AccessibilityNodeInfo> nodesList = root.findAccessibilityNodeInfosByViewId(widgetID);
            Log.i(TAG,"getpacket is size" + nodesList.size());
            /** 对最后一个"领取红包"进行点击 */
            nodesList.get(nodesList.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }


    @Override
    public void onInterrupt() {

    }


}
