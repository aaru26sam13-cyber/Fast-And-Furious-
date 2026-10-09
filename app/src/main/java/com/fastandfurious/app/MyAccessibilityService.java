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

        // Donhi page - Main + Orders - donhi var Accept shodh - 0.00000001 ms
        if (clickAccept(root)) return;
    }

    private boolean clickAccept(AccessibilityNodeInfo node) {
        if (node == null) return false;

        try {
            String text = node.getText() != null ? node.getText().toString().toLowerCase() : "";
            String desc = node.getContentDescription() != null ? node.getContentDescription().toString().toLowerCase() : "";
            
            // Accept / ACCEPT / accept - sagla
            if (text.contains("accept") || desc.contains("accept")) {
                AccessibilityNodeInfo p = node;
                for (int i = 0; i < 25; i++) { // 25 parent - Order page + Main page donhi
                    if (p == null) break;
                    if (p.isClickable()) {
                        // Donhi page var - 10 vela toofan click
                        for(int k=0;k<10;k++) {
                            p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        return true;
                    }
                    p = p.getParent();
                }
            }
        } catch (Exception e) {}

        // Full screen scan - Home + Orders donhi
        for (int i = 0; i < node.getChildCount(); i++) {
            if (clickAccept(node.getChild(i))) return true;
        }
        return false;
    }

    @Override
    public void onInterrupt() {}
}
