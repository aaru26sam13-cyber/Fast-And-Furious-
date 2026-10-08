package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {

    private long lastClick = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        if (System.currentTimeMillis() - lastClick < 500) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        findAndClick(root);
    }

    private void findAndClick(AccessibilityNodeInfo node) {
        if (node == null) return;
        try {
            String text = node.getText() != null ? node.getText().toString().toLowerCase() : "";
            String desc = node.getContentDescription() != null ? node.getContentDescription().toString().toLowerCase() : "";

            // ===== RAPIDO SATHI - JUNA CODE TASACH - KAHI BADAL NAHI =====
            boolean isRapidoAccept = text.contains("accept") || desc.contains("accept");
            
            // ===== OLA + UBER SATHI - NAVIN ADD - RAPIDO LA DHIKKA NAHI =====
            boolean isUberMatch = text.contains("match") || desc.contains("match");

            boolean isTarget = isRapidoAccept || isUberMatch;

            if (isTarget) {
                AccessibilityNodeInfo parent = node;
                for (int i = 0; i < 8; i++) {
                    if (parent == null) break;
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        lastClick = System.currentTimeMillis();
                        return;
                    }
                    parent = parent.getParent();
                }
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                lastClick = System.currentTimeMillis();
                return;
            }
        } catch (Exception e) {}

        for (int i = 0; i < node.getChildCount(); i++) {
            findAndClick(node.getChild(i));
            if (System.currentTimeMillis() - lastClick < 500) return;
        }
    }

    @Override
    public void onInterrupt() {}
}
