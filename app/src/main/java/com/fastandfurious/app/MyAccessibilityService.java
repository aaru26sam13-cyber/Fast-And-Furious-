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
        clickOnlyAcceptButton(root);
    }

    private void clickOnlyAcceptButton(AccessibilityNodeInfo node) {
        if (node == null || alreadyClicked) return;
        boolean isButton = node.getClassName() != null && node.getClassName().toString().toLowerCase().contains("button");
        boolean isClickable = node.isClickable();

        if (isButton && isClickable) {
            String text = node.getText() != null ? node.getText().toString().trim().toLowerCase() : "";
            // 【entity-Uber¦canonical_name=Uber】 = Match, Ola = Accept, Rapido = Accept - 0.01ms speed
            if (text.contains("accept") || text.equals("match")) {
                try {
                    // 0.01ms = NO DELAY - Ekdam fast!
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    alreadyClicked = true;
                    new android.os.Handler(getMainLooper()).postDelayed(() -> alreadyClicked = false, 5000);
                    return;
                } catch (Exception e) {}
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (alreadyClicked) break;
            clickOnlyAcceptButton(node.getChild(i));
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
