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
        
        String text = "";
        String desc = "";
        String id = "";
        try {
            if(node.getText() != null) text = node.getText().toString().toLowerCase();
            if(node.getContentDescription() != null) desc = node.getContentDescription().toString().toLowerCase();
            if(node.getViewIdResourceName() != null) id = node.getViewIdResourceName().toLowerCase();
        } catch (Exception e) {}

        // Rapido + Ola + 【entity-Uber¦canonical_name=Uber】 saglyansathi
        boolean isAcceptBtn = text.contains("accept") || desc.contains("accept") || 
                              text.contains("match") || desc.contains("match") ||
                              id.contains("accept") ||
                              id.contains("match");

        if (isAcceptBtn) {
            AccessibilityNodeInfo p = node;
            for (int i = 0; i < 6; i++) {
                if (p == null) break;
                if (p.isClickable()) {
                    isLocked = true;
                    p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    // 1.5 sec nantar parat chalu
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

    @Override
    public void onInterrupt() {}
    
    @Override
    public void onServiceConnected() {}
}
