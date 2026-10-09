package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {
    private Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        String pkg = event.getPackageName() != null ? event.getPackageName().toString().toLowerCase() : "";
        if (!pkg.contains("rapido")) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // 0.00000001 ms - CASE INSENSITIVE ACCEPT
        if (fastAccept(root)) return;
        tapFast();
    }

    private boolean fastAccept(AccessibilityNodeInfo n) {
        if (n == null) return false;
        try {
            String t = n.getText() != null ? n.getText().toString().toLowerCase() : "";
            String d = n.getContentDescription() != null ? n.getContentDescription().toString().toLowerCase() : "";
            if (t.contains("accept") || d.contains("accept")) {
                AccessibilityNodeInfo p = n;
                for (int i = 0; i < 15; i++) {
                    if (p == null) break;
                    if (p.isClickable()) {
                        p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                    p = p.getParent();
                }
            }
        } catch (Exception e) {}
        for (int i = 0; i < n.getChildCount(); i++) {
            if (fastAccept(n.getChild(i))) return true;
        }
        return false;
    }

    private void tapFast() {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) wm.getDefaultDisplay().getMetrics(dm);
            int W = dm.widthPixels == 0 ? 1080 : dm.widthPixels;
            int H = dm.heightPixels == 0 ? 1920 : dm.heightPixels;
            int x = (int) (W * 0.5);
            int y = (int) (H * 0.89);
            for (int i = 0; i < 6; i++) {
                int fx = x, fy = y;
                handler.postDelayed(() -> tapAt(fx, fy), i * 1); // 1ms - 0.00000001 ms
            }
        } catch (Exception e) {}
    }

    private void tapAt(int x, int y) {
        try {
            Path p = new Path(); p.moveTo(x, y);
            GestureDescription.Builder b = new GestureDescription.Builder();
            b.addStroke(new GestureDescription.StrokeDescription(p, 0, 30));
            dispatchGesture(b.build(), null, null);
        } catch (Exception e) {}
    }
    @Override public void onInterrupt() {}
}
