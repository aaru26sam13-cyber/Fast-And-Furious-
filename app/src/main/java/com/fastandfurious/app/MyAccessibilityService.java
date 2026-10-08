package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {
    boolean isLocked = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (isLocked) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        checkAndClick(root);
    }

    private void checkAndClick(AccessibilityNodeInfo node) {
        if (node == null || isLocked) return;
        String text = node.getText() != null ? node.getText().toString().toLowerCase() : "";
        String desc = node.getContentDescription() != null ? node.getContentDescription().toString().toLowerCase() : "";
        
        if (text.contains("accept") || desc.contains("accept") || text.contains("match") || desc.contains("match")) {
            AccessibilityNodeInfo p = node;
            for (int i = 0; i < 5; i++) {
                if (p == null) break;
                if (p.isClickable()) {
                    isLocked = true;
                    p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    new android.os.Handler(getMainLooper()).postDelayed(() -> isLocked = false, 1500);
                    return;
                }
                p = p.getParent();
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            checkAndClick(node.getChild(i));
            if (isLocked) return;
        }
    }
    @Override public void onInterrupt() {}
    @Override public void onServiceConnected() {}
}
