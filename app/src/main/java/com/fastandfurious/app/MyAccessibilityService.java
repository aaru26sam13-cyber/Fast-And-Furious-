package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {
    SharedPreferences prefs;
    boolean alreadyClicked = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED 
            && event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;

        prefs = getSharedPreferences("fast_furious", MODE_PRIVATE);
        if (!prefs.getBoolean("auto_service", false)) return;
        if (alreadyClicked) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        findAndClick(root);
    }

    private void findAndClick(AccessibilityNodeInfo node) {
        if (node == null || alreadyClicked) return;

        String text = node.getText() != null ? node.getText().toString().trim().toLowerCase() : "";
        String desc = node.getContentDescription() != null ? node.getContentDescription().toString().trim().toLowerCase() : "";

        // FINAL: Ola=Accept, Rapido=Accept/Accept Ride, Uber=Match - 0.01ms speed
        boolean isOlaRapido = text.contains("accept") || desc.contains("accept");
        boolean isUber = text.equals("match") || desc.equals("match") || text.contains("match") || desc.contains("match");

        if (isOlaRapido || isUber) {
            // Rapido/Ola cha button TextView asto - tyacha clickable parent var click karaycha
            AccessibilityNodeInfo clickNode = node;
            int tries = 0;
            while (clickNode != null && !clickNode.isClickable() && tries < 5) {
                clickNode = clickNode.getParent();
                tries++;
            }
            if (clickNode != null) {
                try {
                    // 0.01ms = NO DELAY - SUPER FAST CLICK
                    clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    alreadyClicked = true;
                    // 4 sec lock - Map open nahi honar
                    new android.os.Handler(getMainLooper()).postDelayed(() -> alreadyClicked = false, 4000);
                    return;
                } catch (Exception e) {}
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            if (alreadyClicked) break;
            findAndClick(node.getChild(i));
        }
    }

    @Override
    public void onInterrupt() { alreadyClicked = false; }

    @Override
    public void onServiceConnected() {
        alreadyClicked = false;
        super.onServiceConnected();
    }
}
