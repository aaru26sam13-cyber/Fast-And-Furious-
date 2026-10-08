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

        // ===== 1. RAPIDO PAGE OPEN - PAHILA WALA - 0.00001ms =====
        if(pkg.contains("rapido")){
            if(fastClickAccept(root)) return;
            return;
        }

        // ===== 2. UBER PAGE - RAPIDO BACKGROUND + UBER =====
        if(pkg.contains("uber") || pkg.contains("ubercab")){
            if(System.currentTimeMillis()-lastClick < 150) return;
            // Rapido background - pahila direct Accept click - 0.00001ms
            if(fastClickAccept(root)){
                startRapidoBackground_1Box_Fast();
                return;
            }
            // Uber - Match
            if(containsTextQuick(root,"Match") || containsTextQuick(root,"₹")){
                startUber_2Box(root);
            }
        }
    }

    // PAHILA WALA ORIGINAL - 0.00001ms - Rapido Open + Background donhi sathi
    private boolean fastClickAccept(AccessibilityNodeInfo root){
        if(System.currentTimeMillis()-lastClick < 100) return false;
        try{
            List<AccessibilityNodeInfo> list=root.findAccessibilityNodeInfosByText("Accept");
            if(list!=null && !list.isEmpty()){
                for(AccessibilityNodeInfo n:list){
                    AccessibilityNodeInfo p=n;
                    for(int i=0;i<8;i++){ 
                        if(p==null) break; 
                        if(p.isClickable()){ 
                            p.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
                            lastClick=System.currentTimeMillis(); 
                            return true; 
                        } 
                        p=p.getParent(); 
                    }
                }
            }
        }catch(Exception e){}
        return false;
    }

    private boolean containsTextQuick(AccessibilityNodeInfo n, String s){
        if(n==null) return false;
        try{
            List<AccessibilityNodeInfo> list=n.findAccessibilityNodeInfosByText(s);
            if(list!=null && !list.isEmpty()) return true;
        }catch(Exception e){}
        return false;
    }

    private DisplayMetrics getMetrics(){
        DisplayMetrics dm=new DisplayMetrics();
        try{
            WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
            if(wm!=null) wm.getDefaultDisplay().getMetrics(dm);
        }catch(Exception e){ dm.widthPixels=1080; dm.heightPixels=1920; }
        if(dm.widthPixels==0){ dm.widthPixels=1080; dm.heightPixels=1920; }
        return dm;
    }

    // RAPIDO BACKGROUND - 1 BOX - Direct click nantar backup
    private void startRapidoBackground_1Box_Fast(){
        if(isTapping) return;
        DisplayMetrics dm=getMetrics();
        int W=dm.widthPixels, H=dm.heightPixels;
        Rect box = new Rect((int)(W*0.12), (int)(H*0.80), (int)(W*0.93), (int)(H*0.97));
        isTapping=true;
        for(int i=0;i<60;i++){
            int x=box.left+random.nextInt(Math.max(1,box.width()));
            int y=box.top+random.nextInt(Math.max(1,box.height()));
            handler.postDelayed(() -> tapAt(x,y), i*3); // 3ms - ajun fast
        }
        handler.postDelayed(() -> isTapping=false, 1500);
    }

    private void startUber_2Box(AccessibilityNodeInfo root){
        if(isTapping) return;
        DisplayMetrics dm=getMetrics();
        int W=dm.widthPixels, H=dm.heightPixels;
        Rect exact=findExact(root,"Match");
        Rect b1,b2;
        if(exact!=null){ b1=exact; b2=exact; }
        else{
            b1=new Rect((int)(W*0.10),(int)(H*0.84),(int)(W*0.90),(int)(H*0.96));
            b2=new Rect((int)(W*0.30),(int)(H*0.77),(int)(W*0.92),(int)(H*0.94));
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
        handler.postDelayed(() -> isTapping=false, 2500);
    }

    private Rect findExact(AccessibilityNodeInfo root,String txt){
        try{ List<AccessibilityNodeInfo> list=root.findAccessibilityNodeInfosByText(txt); if(list!=null){ for(AccessibilityNodeInfo n:list){ Rect r=new Rect(); n.getBoundsInScreen(r); if(r.width()>50) return r; } } }catch(Exception e){} return null;
    }
    private void tapAt(int x,int y){
        try{ Path p=new Path(); p.moveTo(x,y); GestureDescription.Builder b=new GestureDescription.Builder(); b.addStroke(new GestureDescription.StrokeDescription(p,0,30)); dispatchGesture(b.build(),null,null); }catch(Exception e){}
    }
    @Override public void onInterrupt() {}
}
