
package com.fastandfurious.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {
    LinearLayout root; LinearLayout content; SharedPreferences prefs;
    final int BG = Color.rgb(246,248,253); final int WHITE = Color.WHITE;
    final int DARK = Color.rgb(25,32,48); final int MUTED = Color.rgb(105,114,132);
    final int BLUE = Color.rgb(50,105,220); final int PURPLE = Color.rgb(112,76,190);
    final int GREEN = Color.rgb(25,145,92); final int ORANGE = Color.rgb(220,125,35); final int RED = Color.rgb(205,55,65);
    final int LIGHT_BLUE = Color.rgb(238,245,255); final int LIGHT_PURPLE = Color.rgb(246,240,255);
    final int LIGHT_GREEN = Color.rgb(237,250,243); final int LIGHT_ORANGE = Color.rgb(255,246,235);
    TextView accessibilityStatus; TextView notificationStatus; TextView batteryStatus; TextView shizukuStatus;
    static final int SHIZUKU_PERMISSION_CODE = 4001;
    @Override protected void onCreate(Bundle s){ super.onCreate(s); prefs=getSharedPreferences("fast_furious",MODE_PRIVATE); showHome(); }
    @Override protected void onResume(){ super.onResume(); }
    int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    TextView text(String v,float sz,boolean b){TextView t=new TextView(this);t.setText(v);t.setTextSize(sz);t.setTextColor(DARK);t.setTypeface(Typeface.DEFAULT,b?Typeface.BOLD:Typeface.NORMAL);return t;}
    TextView muted(String v){TextView t=text(v,13,false);t.setTextColor(MUTED);return t;}
    void base(String t,String sub,int c){ root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(5),dp(5),dp(5),0); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(14),dp(5),dp(14),dp(14)); ScrollView sc=new ScrollView(this); sc.addView(content); root.addView(sc,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); TextView h=text(t,27,true); content.addView(h); if(sub!=null) content.addView(muted(sub)); }
    void showHome(){ base("Fast And Furious","Your smart ride dashboard",BLUE); content.addView(text("Welcome - Service Ready 0.01ms Ola/Uber/Rapido",18,true)); Button b=new Button(this); b.setText("Go to Settings"); b.setOnClickListener(v->showSettings()); content.addView(b); }
    void showSettings(){ base("Settings","Permissions",PURPLE); content.addView(text("Accessibility ON kara",16,true)); }
    void showSetupPage1(){ showHome(); } void showSetupPage2(){ showHome(); } void showSetupPage3(){ showHome(); } void showSetupPage4(){ showHome(); } void showSetupPage5(){ showHome(); }
    void showAutoService(){ showSettings(); } void showProfile(){ showSettings(); } void showHistory(){ showSettings(); }
}
