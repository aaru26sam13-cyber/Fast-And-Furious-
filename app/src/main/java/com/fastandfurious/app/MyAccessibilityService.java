package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;
import java.util.Set;

public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        SharedPreferences prefs = getSharedPreferences("fast_furious", MODE_PRIVATE);
        if (!prefs.getBoolean("auto_service", true)) return;
        String allText = getAllText(root).toLowerCase();

        Set<String> avoidSet = prefs.getStringSet("avoid_locations", null);
        if (avoidSet!= null) {
            for (String avoid : avoidSet) {
                if (!avoid.isEmpty() && allText.contains(avoid.toLowerCase())) return;
            }
        }
        Set<String> goSet = prefs.getStringSet("goto_locations", null);
        if (goSet!= null &&!goSet.isEmpty()) {
            boolean found = false;
            for (String go : goSet) {
                if (!go.isEmpty() && allText.contains(go.toLowerCase())) { found = true; break; }
            }
            if (!found) return;
        }
        int minFare = prefs.getInt("min_fare", 0);
        int maxFare = prefs.getInt("max_fare", 10000);
        int fare = extractFare(root);
        if (fare!= -1 && (fare < minFare || fare > maxFare)) return;

        ultraFastClick(root);
        instantClick(root);
    }
    private String getAllText(AccessibilityNodeInfo node){
        if(node==null) return "";
        StringBuilder sb = new StringBuilder();
        if(node.getText()!=null) sb.append(node.getText().toString()).append(" ");
        if(node.getContentDescription()!=null) sb.append(node.getContentDescription().toString()).append(" ");
        for(int i=0;i<node.getChildCount();i++){
            AccessibilityNodeInfo c=node.getChild(i);
            if(c!=null){ sb.append(getAllText(c)).append(" "); c.recycle(); }
        }
        return sb.toString();
    }
    private int extractFare(AccessibilityNodeInfo node){
        try{
            String t=getAllText(node);
            if(t.contains("₹")){
                String[] parts=t.split("₹");
                for(int i=1;i<parts.length;i++){
                    String num=parts[i].replaceAll("[^0-9]", " ").trim().split(" ")[0];
                    if(!num.isEmpty()) return Integer.parseInt(num);
                }
            }
        }catch(Exception e){}
        return -1;
    }
    private void instantClick(AccessibilityNodeInfo node) {
        if (node == null) return;
        String combined=getAllText(node).toLowerCase();
        if (combined.contains("accept") || combined.contains("match") || combined.contains("go") || combined.contains("start")) {
            if (node.isClickable()) { node.performAction(AccessibilityNodeInfo.ACTION_CLICK); return; }
            AccessibilityNodeInfo p=node.getParent();
            while(p!=null){ if(p.isClickable()){p.performAction(AccessibilityNodeInfo.ACTION_CLICK); return;} p=p.getParent(); }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child!= null) { instantClick(child); child.recycle(); }
        }
    }
    private void ultraFastClick(AccessibilityNodeInfo root) {
        if (root == null) return;
        String[] targets = {"Match", "MATCH", "Accept", "ACCEPT", "ACCEPT RIDE", "GO", "START"};
        for (String t : targets) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(t);
            for (AccessibilityNodeInfo n : nodes) {
                if (n!= null) {
                    if (n.isClickable()) n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    else if (n.getParent()!= null) n.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }
    @Override public void onInterrupt() {}
}
