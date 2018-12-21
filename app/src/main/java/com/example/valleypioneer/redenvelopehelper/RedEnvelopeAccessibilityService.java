package com.example.valleypioneer.redenvelopehelper;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class RedEnvelopeAccessibilityService extends AccessibilityService {
    private static final String TAG = "AccessibilityService";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.i(TAG, "进入event类型判断！" + "eventType is " + Integer.toHexString(eventType));
        // 由于notificationListenerService只能在api>=18的系统上使用，因此，以下逻辑并不会触发
        switch (eventType) {
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                    List<CharSequence> texts = event.getText();
//                    Log.i(TAG, "notification texts is " + texts.toString());
//                    if (!texts.isEmpty()) {
//                        for (CharSequence text : texts) {
//                            String content = text.toString();
//                            Log.i(TAG, "content is " + content);
//                            if (content.contains("[微信红包]")) {
//                                if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
//                                    // isProcessing = true; /** 开始处理本次通知 */
//                                    Notification notification = (Notification) event.getParcelableData();
//                                    PendingIntent pi = notification.contentIntent;
//                                    try {
//                                        pi.send();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.i(TAG, "className is " + className);

                if (Constants.IS_NOTIFICATION_SHOWN) {
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                        Log.i(TAG, "领取红包！");
                        openLastRedEnvelope(Constants.GET_RED_ENVELOPE);
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                        Log.i(TAG, "抢红包！");
                        if (!inputClick(Constants.GRAB)) {
                            inputClick(Constants.CANCEL);
                        }
                    } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                        Log.i(TAG, "退出红包详情界面！");
                        // 群聊红包留言功能
                        if (inputClick(Constants.LEAVE_MESSAGE)) {
                            leaveMessage(Constants.EDIT_BOARD, Constants.LEAVE_MESSAGE_STRING);
                            inputClick(Constants.SEND);
                        }

                        inputClick(Constants.LUCKY_MONEY_DETAIL_UI_BACK);
                        performGlobalAction(GLOBAL_ACTION_HOME);
                        Constants.IS_NOTIFICATION_SHOWN = false;
                    }
                }

                break;
            default:
        }
    }

    @Override
    public void onInterrupt() {

    }

    private boolean inputClick(String widgetID) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        boolean res = true;
        if (root != null) {
            List<AccessibilityNodeInfo> nodeslist = root.findAccessibilityNodeInfosByViewId(widgetID);
            Log.i(TAG, "nodesList.size() is" + nodeslist.size());
            if (nodeslist.size() == 0)
                res = false;
            else
                for (AccessibilityNodeInfo node : nodeslist)
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return res;
    }

    private void openLastRedEnvelope(String widgetID) {
        AccessibilityNodeInfo root = getRootInActiveWindow();

        if (root != null) {
            List<AccessibilityNodeInfo> nodesList = root.findAccessibilityNodeInfosByViewId(widgetID);
            if (nodesList.size() == 0) {
                return;
            }
            /** 对最后一个"领取红包"进行点击 */
            nodesList.get(nodesList.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    public void leaveMessage(String widgetID, String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

            final List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByViewId(widgetID);
            if (list != null && !list.isEmpty()) {
                AccessibilityNodeInfo info = list.get(0);
                //粘贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);

                CharSequence txt = info.getText();
                if (txt == null) txt = "";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, txt.length());
                    info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }
            }
    }


}
