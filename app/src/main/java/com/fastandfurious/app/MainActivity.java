package com.fastandfurious.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText minFare, maxFare, gotoInput, avoidInput;
    Button addGoto, addAvoid, saveBtn;
    SharedPreferences prefs;
    Set<String> gotoSet = new HashSet<>();
    Set<String> avoidSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("fast_furious", MODE_PRIVATE);
        
        minFare = findViewById(R.id.minFare);
        maxFare = findViewById(R.id.maxFare);
        gotoInput = findViewById(R.id.gotoInput);
        avoidInput = findViewById(R.id.avoidInput);
        addGoto = findViewById(R.id.addGoto);
        addAvoid = findViewById(R.id.addAvoid);
        saveBtn = findViewById(R.id.saveBtn);

        gotoSet = new HashSet<>(prefs.getStringSet("goto_locations", new HashSet<>()));
        avoidSet = new HashSet<>(prefs.getStringSet("avoid_locations", new HashSet<>()));
        minFare.setText(String.valueOf(prefs.getInt("min_fare", 200)));
        maxFare.setText(String.valueOf(prefs.getInt("max_fare", 1000)));

        addGoto.setOnClickListener(v -> {
            if(gotoSet.size() >= 15){ Toast.makeText(this,"Max 15 GoTo",0).show(); return; }
            String s = gotoInput.getText().toString().trim();
            if(!s.isEmpty()){ gotoSet.add(s); gotoInput.setText(""); Toast.makeText(this,"Added: "+s+" ("+gotoSet.size()+"/15)",0).show(); }
        });
        addAvoid.setOnClickListener(v -> {
            if(avoidSet.size() >= 5){ Toast.makeText(this,"Max 5 Avoid",0).show(); return; }
            String s = avoidInput.getText().toString().trim();
            if(!s.isEmpty()){ avoidSet.add(s); avoidInput.setText(""); Toast.makeText(this,"Added: "+s+" ("+avoidSet.size()+"/5)",0).show(); }
        });
        saveBtn.setOnClickListener(v -> {
            try{
                int min = Integer.parseInt(minFare.getText().toString());
                int max = Integer.parseInt(maxFare.getText().toString());
                prefs.edit().putInt("min_fare",min).putInt("max_fare",max).putStringSet("goto_locations",gotoSet).putStringSet("avoid_locations",avoidSet).putBoolean("auto_service",true).apply();
                Toast.makeText(this,"SAVED! GoTo:"+gotoSet.size()+" Avoid:"+avoidSet.size(),1).show();
            }catch(Exception e){ Toast.makeText(this,"Fare tak!",0).show(); }
        });
    }
}
