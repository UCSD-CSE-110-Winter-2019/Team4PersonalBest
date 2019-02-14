package edu.ucsd.cse110.googlefitapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.renderscript.Element;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.Timer;
import java.util.TimerTask;

import edu.ucsd.cse110.googlefitapp.fitness.FitnessService;
import edu.ucsd.cse110.googlefitapp.fitness.FitnessServiceFactory;
import edu.ucsd.cse110.googlefitapp.fitness.GoogleFitAdapter;

public class StepCountActivity extends AppCompatActivity {

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";
    private static final int RESULT_CODE = 1000;

    private TextView textSteps;
    private TextView textDist;
    private TextView textSpeed;
    private FitnessService fitnessService;
    private long steps = 0;
    private double distance = 0.0;
    private int time = 0;
    private double speed = 0.0;
    private float strideLen;
    private boolean recordInitialStep = true;
    private long initialSteps;

    @Override
    protected void onRestart() {
        super.onRestart();
        fitnessService.startAsync();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        SharedPreferences sharedPref = getSharedPreferences("stepCountData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("recordInitialStep", true);
        editor.apply();

        Timer t = new Timer();
        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.updateStepCount();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView)findViewById(R.id.timer_text);
                        int min = time / 60;
                        int sec = time - min*60;

                        if(sec < 10) {
                            tv.setText(String.format("Time: %d:0%d", min, sec));
                        } else {
                            tv.setText(String.format("Time: %d:%d", min, sec));
                        }

                        time += 1;
                    }
                });
            }
        }, 0, 1000);

        textSteps = findViewById(R.id.textSteps);
        textDist = findViewById(R.id.textDistance);
        textDist.setText(String.format("Distance: %.1f miles", distance));
        textSpeed = findViewById(R.id.textSpeed);
        textSpeed.setText(String.format("Speed: %.1f MPH", speed));

        strideLen = getIntent().getFloatExtra("stride", 0);

        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessService.updateStepCount();
            }
        });

        Button btnEndRecord = findViewById(R.id.btnEndRecord);
        btnEndRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fitnessService.stopAsync();
                Intent homescreen = new Intent(getApplicationContext(), MainActivity.class);
                homescreen.putExtra("speed", speed);
                homescreen.putExtra("steps", steps);
                homescreen.putExtra("time", time);
                homescreen.putExtra("distance", distance);
                setResult(RESULT_CODE, homescreen);
                finish();
            }
        });

        fitnessService.setup();

    }

    @Override
    protected void onStop() {
        super.onStop();

        fitnessService.stopAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();
            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        SharedPreferences sharedPref = getSharedPreferences("stepCountData", MODE_PRIVATE);
        recordInitialStep = sharedPref.getBoolean("recordInitialStep", true);

        if(recordInitialStep) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("initialSteps", stepCount);
            editor.putBoolean("recordInitialStep", false);
            editor.apply();
        }

        initialSteps = sharedPref.getLong("initialSteps", stepCount);
        steps = stepCount - initialSteps;
        textSteps.setText(String.format("Steps: %d", steps));
    }

    public void setDistance() {
        distance = (double)(steps * strideLen) / 63360.0;
        textDist.setText(String.format("Distance: %.1f miles", distance));
    }

    public void setSpeed() {
        if(time == 0) {
            speed = 0.0;
        } else {
            speed = distance/(double)time*3600.0;
        }
        textSpeed.setText(String.format("Speed: %.1f MPH", speed));
    }

    public void updateAll(long stepCount) {
        setStepCount(stepCount);
        setDistance();
        setSpeed();
    }
    
    public void showEncouragement(long stepCount){

        Context context = getApplicationContext();
        CharSequence text = String.format("Good job! You're already at %d percent of the daily recommended number of steps.", (int) stepCount / 100);

        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
