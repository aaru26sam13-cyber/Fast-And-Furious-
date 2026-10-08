package com.fastandfurious.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;
import java.util.Random;

public class MyAccessibilityService extends AccessibilityService {
    private long lastClick = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isTapping = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        String pkg = event.getPackageName()!=null?event.getPackageName().toString().toLowerCase():"";
        if(!pkg.contains("rapido") && !pkg.contains("uber") && !pkg.contains("ubercab")) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if(root==null) return;

        // 1. RAPIDO PAGE OPEN - PAHILA WALA - 0.00001ms - NO CHANGE
        if(pkg.contains("rapido")){
            if(System.currentTimeMillis()-lastClick < 200) return;
            List<AccessibilityNodeInfo> list=root.findAccessibilityNodeInfosByText("Accept");
            if(list!=null){
                for(AccessibilityNodeInfo n:list){
                    AccessibilityNodeInfo p=n;
                    for(int i=0;i<8;i++){ 
                        if(p==null) break; 
                        if(p.isClickable()){ 
                            p.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
                            lastClick=System.currentTimeMillis(); 
                            return; 
                        } 
                        p=p.getParent(); 
                    }
                }
            }
            return;
        }

        // 2. UBER PAGE - RAPIDO BG = 1 BOX, UBER = 2 BOX
        if(pkg.contains("uber") || pkg.contains("ubercab")){
            if(isTapping) return;
            if(containsText(root,"Accept")) startRapidoBackground_1Box();
            if(containsText(root,"Match") || containsText(root,"₹")) startUber_2Box(root);
        }
    }

    private boolean containsText(AccessibilityNodeInfo n, String s){
        if(n==null) return false;
        try{ String t=n.getText()!=null?n.getText().toString():""; if(t.toLowerCase().contains(s.toLowerCase())) return true; }catch(Exception e){}
        for(int i=0;i<n.getChildCount();i++){ if(containsText(n.getChild(i),s)) return true; }
        return false;
    }

    // RAPIDO BACKGROUND - 1 BOX - tuza ₹133/₹488 photo - Pivla Accept
    private void startRapidoBackground_1Box(){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int W=dm.widthPixels, H=dm.heightPixels;
        Rect box = new Rect((int)(W*0.12), (int)(H*0.80), (int)(W*0.93), (int)(H*0.97)); // 1 Box
        isTapping=true; lastClick=System.currentTimeMillis();
        for(int i=0;i<60;i++){
            int x=box.left+random.nextInt(Math.max(1,box.width()));
            int y=box.top+random.nextInt(Math.max(1,box.height()));
            handler.postDelayed(() -> tapAt(x,y), i*5); // 0.00001ms - 5ms speed - 60 point
        }
        handler.postDelayed(() -> isTapping=false, 2500);
    }

    // UBER - 2 BOX - Center + Right
    private void startUber_2Box(AccessibilityNodeInfo root){
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int W=dm.widthPixels, H=dm.heightPixels;
        Rect exact=findExact(root,"Match");
        Rect b1,b2;
        if(exact!=null){ b1=exact; b2=exact; }
        else{
            b1=new Rect((int)(W*0.10),(int)(H*0.84),(int)(W*0.90),(int)(H*0.96)); // Center
            b2=new Rect((int)(W*0.30),(int)(H*0.77),(int)(W*0.92),(int)(H*0.94)); // Right
        }
        isTapping=true; lastClick=System.currentTimeMillis();
        for(int i=0;i<30;i++){
            int x1=b1.left+random.nextInt(Math.max(1,b1.width()));
            int y1=b1.top+random.nextInt(Math.max(1,b1.height()));
            handler.postDelayed(() -> tapAt(x1,y1), i*5);
        }
        for(int i=0;i<30;i++){
            int x2=b2.left+random.nextInt(Math.max(1,b2.width()));
            int y2=b2.top+random.nextInt(Math.max(1,b2.height()));
            handler.postDelayed(() -> tapAt(x2,y2), i*5+2);
        }
        handler.postDelayed(() -> isTapping=false, 3000);
    }

    private Rect findExact(AccessibilityNodeInfo root,String txt){
        try{ List<AccessibilityNodeInfo> list=root.findAccessibilityNodeInfosByText(txt); if(list!=null){ for(AccessibilityNodeInfo n:list){ Rect r=new Rect(); n.getBoundsInScreen(r); if(r.width()>50) return r; } } }catch(Exception e){} return null;
    }
    private void tapAt(int x,int y){
        try{ Path p=new Path(); p.moveTo(x,y); GestureDescription.Builder b=new GestureDescription.Builder(); b.addStroke(new GestureDescription.StrokeDescription(p,0,30)); dispatchGesture(b.build(),null,null); }catch(Exception e){}
    }
    @Override public void onInterrupt() {}
}
