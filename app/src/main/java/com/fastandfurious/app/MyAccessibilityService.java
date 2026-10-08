package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        String pkg = event.getPackageName() != null ? event.getPackageName().toString().toLowerCase() : "";
        if (!pkg.contains("rapido")) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // 0.00000001 ms - NO DELAY - DIRECT LOOP
        List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText("Accept");
        if (list != null) {
            for (AccessibilityNodeInfo n : list) {
                AccessibilityNodeInfo p = n;
                for (int i = 0; i < 12; i++) {
                    if (p == null) break;
                    if (p.isClickable()) {
                        // 0.00000001 ms - 3 vela ekach click
                        p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                    p = p.getParent();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {}
}
