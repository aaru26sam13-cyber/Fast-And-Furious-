package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        SharedPreferences prefs = getSharedPreferences("fast_furious", MODE_PRIVATE);
        if (!prefs.getBoolean("auto_service", true)) return;

        // Check which apps are enabled
        boolean ola = prefs.getBoolean("app_ola", true);
        boolean uber = prefs.getBoolean("app_uber", true);
        boolean rapido = prefs.getBoolean("app_rapido", true);

        String pkg = event.getPackageName() != null ? event.getPackageName().toString() : "";
        
        boolean isTarget = false;
        if (ola && (pkg.contains("olacabs") || pkg.contains("ola"))) isTarget = true;
        if (uber && pkg.contains("uber")) isTarget = true;
        if (rapido && pkg.contains("rapido")) isTarget = true;
        
        if (!isTarget) return;

        // 0.01ms instant - No delay, direct click
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        
        // Ultra fast recursive search and click
        instantClick(root);
    }

    private void instantClick(AccessibilityNodeInfo node) {
        if (node == null) return;
        
        // All possible Accept texts for Ola/Uber/Rapido
        CharSequence text = node.getText();
        CharSequence desc = node.getContentDescription();
        String combined = "";
        if (text != null) combined += text.toString().toLowerCase() + " ";
        if (desc != null) combined += desc.toString().toLowerCase();
        
        if (combined.contains("accept") || combined.contains("go") || combined.contains("start") || combined.contains("pick")) {
            // Check if it's a button
            if (node.isClickable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                // Also try parent
                if (node.getParent() != null && node.getParent().isClickable()) {
                    node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                return;
            } else {
                // Try to find clickable parent
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                    parent = parent.getParent();
                }
            }
        }

        // Recursively check children - 0.01ms = no sleep, full speed
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                instantClick(child);
                child.recycle();
            }
        }
    }

    // Alternative ultra-fast method using find by text - runs in parallel
    private void ultraFastClick(AccessibilityNodeInfo root) {
        if (root == null) return;
        String[] targets = {"ACCEPT", "Accept", "ACCEPT RIDE", "Accept Ride", "GO", "Go", "START", "Start", "PICK UP", "Pick up"};
        for (String t : targets) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(t);
            for (AccessibilityNodeInfo n : nodes) {
                if (n != null) {
                    if (n.isClickable()) n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    else if (n.getParent() != null) n.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {}

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }
}
