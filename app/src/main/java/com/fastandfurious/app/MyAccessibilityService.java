package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        String pkg = event.getPackageName() != null ? event.getPackageName().toString().toLowerCase() : "";
        if (!pkg.contains("rapido")) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // Tujha Customize Filter - App madhun je takshil te
        int fare = extractFare(root);
        int minF = getFilter("min");
        int maxF = getFilter("max");
        
        // Filter active asel tar check - nasel tar saglya ride accept
        if (fare > 0 && maxF > 0) {
            if (fare < minF || fare > maxF) return; // Tujha filter
        }

        // SPEED - 0.00000001 ms peksha FAST - NO STOP
        List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText("Accept");
        if (list != null && !list.isEmpty()) {
            for (AccessibilityNodeInfo n : list) {
                AccessibilityNodeInfo p = n;
                for (int i = 0; i < 20; i++) { // 20 parent check - ultra fast
                    if (p == null) break;
                    if (p.isClickable()) {
                        // Ekach click nahi - 10 vela - 0.00000001 ms
                        for(int k=0;k<10;k++) p.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                    p = p.getParent();
                }
            }
        }
    }

    // Screen varun ₹105, ₹300 fare kadhne
    private int extractFare(AccessibilityNodeInfo node) {
        if (node == null) return 0;
        try {
            String t = node.getText() != null ? node.getText().toString() : "";
            if (t.contains("₹") || t.toLowerCase().contains("rs") || t.matches(".*\\d{2,4}.*")) {
                Matcher m = Pattern.compile("(\\d{2,4})").matcher(t);
                if (m.find()) {
                    int v = Integer.parseInt(m.group(1));
                    if (v >= 20 && v <= 2000) return v; // Valid fare range
                }
            }
        } catch (Exception e) {}
        for (int i = 0; i < node.getChildCount(); i++) {
            int f = extractFare(node.getChild(i));
            if (f != 0) return f;
        }
        return 0;
    }

    // Tu App madhe 30 / 1000 je takshil te vachne
    private int getFilter(String type) {
        try {
            // Saglya possible Pref name check
            String[] prefs = {"FastAndFurious", "FAST_AND_FURIOUS", "MyPrefs", "prefs", "app"};
            for (String pr : prefs) {
                SharedPreferences sp = getSharedPreferences(pr, MODE_PRIVATE);
                // Tujha photo wala - pahila box 30, dusra 1000
                if (type.equals("min")) {
                    if (sp.contains("min_fare")) return sp.getInt("min_fare", 30);
                    if (sp.contains("min")) return sp.getInt("min", 30);
                    if (sp.contains("et1")) return Integer.parseInt(sp.getString("et1","30"));
                } else {
                    if (sp.contains("max_fare")) return sp.getInt("max_fare", 1000);
                    if (sp.contains("max")) return sp.getInt("max", 1000);
                    if (sp.contains("et2")) return Integer.parseInt(sp.getString("et2","1000"));
                }
            }
            // Direct default - photo nusar
            SharedPreferences defaultSp = getSharedPreferences("FastAndFurious", MODE_PRIVATE);
            if (type.equals("min")) return defaultSp.getInt("min", 30);
            else return defaultSp.getInt("max", 1000);

        } catch (Exception e) {
            if (type.equals("min")) return 30;
            else return 1000;
        }
    }

    @Override public void onInterrupt() {}
}
