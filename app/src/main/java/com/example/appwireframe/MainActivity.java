package com.example.appwireframe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBarSimpleComplexity;
    TextView textViewComplexity;
    ProgressBar progressBar;
    ArrayList<Double> list;
    ArrayAdapter adapter;
    TextView textViewprogress;
    TextView textAverage;
    ListView listView;
    Button buttonAsyc;

    ExecutorService threadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewComplexity=findViewById(R.id.textViewComplexity);
        progressBar = findViewById(R.id.progressBar);
       seekBarSimpleComplexity = findViewById(R.id.seekBarComplexity);
       textViewprogress=findViewById(R.id.textViewProgress);
        textAverage=findViewById(R.id.textViewAverage);
        listView= findViewById(R.id.listView);
        buttonAsyc=findViewById(R.id.buttonAsyncTask);

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

adapter= new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1,android.R.id.text1 ,list);
        listView.setAdapter((ListAdapter) adapter);

        buttonAsyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seekBarComplexityValue =seekBarSimpleComplexity.getProgress();



                new MyTasks().execute(seekBarComplexityValue);

               // threadPool.execute(new DoWork(seekBarComplexityValue));


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
            adapter.clear();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
            buttonAsyc.setEnabled(false);
            textViewprogress.setText("Hello");
        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            buttonAsyc.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("demo","onProgressUpdate "+values[0]);

            progressBar.setMax(values[1]);
            progressBar.setProgress(values[0]);
            textViewprogress.setText(values[0].toString()+" /"+limit);
            textAverage.setText("Average "+ average);
            adapter.notifyDataSetChanged();

        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... params) {
            Log.d("demo","doInBackground "+params[0]);



            limit=params[0];
            progressBar.setMax((params[0])); //wrong .. can't access ui in childthread
            double count=0;
            for(int i=0;i<params[0];i++){
                list.add(HeavyWork.getNumber());
                total=total+list.get(i);
                average=total/(i+1);
                publishProgress(i+1, params[0]);
                progressBar.setProgress(i+1); //wrong .. can't access ui in childthread

            }

            textViewprogress.setText("Hello00000"); //wrong .. can't access ui in childthread
            return list;
        }
    }


    class DoWork implements Runnable{
        public DoWork(int count){

        }
        @Override
        public void run() {


        }
    }
}
