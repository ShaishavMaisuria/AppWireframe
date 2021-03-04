package com.example.appwireframe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBarSimpleComplexity;
    TextView textViewComplexity;
    ProgressBar progressBar;
    ArrayList<Double> list;
    Adapter adapter;
    TextView textViewprogress;
    TextView textAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewComplexity=findViewById(R.id.textViewComplexity);
        progressBar = findViewById(R.id.progressBar);
       seekBarSimpleComplexity = findViewById(R.id.seekBarComplexity);
       textViewprogress=findViewById(R.id.textViewProgress);
        textAverage=findViewById(R.id.textViewAverage);

       list = new ArrayList<>();

        seekBarSimpleComplexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewComplexity.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        findViewById((R.id.buttonAsyncTask)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seekBarComplexityValue =seekBarSimpleComplexity.getProgress();
                new MyTasks().execute(seekBarComplexityValue);
            }
        });


    }

    private class MyTasks extends AsyncTask<Integer, Integer,ArrayList<Double>> {

        double total=0;
        double average=0;
        int limit;
        @Override
        protected void onPreExecute() {
            Log.d("demo","onPreExecute ");
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("demo","onProgressUpdate "+values[0]);
            progressBar.setProgress(values[0]);
            textViewprogress.setText(values[0].toString()+" /"+limit);
            textAverage.setText("Average "+ average);

        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... params) {
            Log.d("demo","doInBackground "+params[0]);

            limit=params[0];
            progressBar.setMax((params[0]));
            double count=0;
            for(int i=0;i<params[0];i++){
                list.add(HeavyWork.getNumber());
                total=total+list.get(i);
                average=total/(i+1);
                publishProgress(i+1);
            }

            return list;
        }
    }

}
