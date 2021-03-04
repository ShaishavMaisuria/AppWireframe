package com.example.appwireframe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.concurrent.Executors;

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
    Button buttonThread;

    ExecutorService threadPool;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewComplexity = findViewById(R.id.textViewComplexity);
        progressBar = findViewById(R.id.progressBar);
        seekBarSimpleComplexity = findViewById(R.id.seekBarComplexity);
        textViewprogress = findViewById(R.id.textViewProgress);
        textAverage = findViewById(R.id.textViewAverage);
        listView = findViewById(R.id.listView);
        buttonAsyc = findViewById(R.id.buttonAsyncTask);
        buttonThread = findViewById(R.id.buttonGenerateUsingThread);

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

        threadPool = Executors.newFixedThreadPool(2);
        adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter((ListAdapter) adapter);
        buttonAsyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seekBarComplexityValue = seekBarSimpleComplexity.getProgress();


                new MyTasks().execute(seekBarComplexityValue);

                // threadPool.execute(new DoWork(seekBarComplexityValue));


            }
        });


        buttonThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seekBarComplexityValue = seekBarSimpleComplexity.getProgress();
                if(!adapter.isEmpty()) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }

                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        Log.d("demo", "Message " + msg.what);

                        switch (msg.what) {
                            case DoWork.STATUS_PROGRESS:
                                progressBar.setProgress(msg.getData().getInt(DoWork.PROGRESS_KEY));
                                textViewprogress.setText(msg.getData().getInt(DoWork.PROGRESS_KEY) + " /" + msg.getData().getInt(DoWork.PROGRESS_MAX_LENGTH));
                                Log.d("demoav",""+msg.getData().getDouble(DoWork.PROGRESS_AVERAGE));
                                textAverage.setText( String.valueOf(msg.getData().getDouble(DoWork.PROGRESS_AVERAGE)));
                                adapter.notifyDataSetChanged();

                                break;
                            case DoWork.STATUS_START:

                                progressBar.setMax(msg.getData().getInt(DoWork.PROGRESS_MAX_LENGTH));
                                buttonAsyc.setEnabled(false);
                                buttonThread.setEnabled(false);
                                break;
                            case DoWork.STATUS_END:
                                buttonAsyc.setEnabled(true);
                                buttonThread.setEnabled(true);
                                break;

                        }


                        return false;
                    }

                });


                // threadPool.execute(new DoWork(seekBarComplexityValue));
                new Thread(new DoWork(seekBarComplexityValue)).start();


                Log.d("demo","end");

            }
        });


    }

    private class MyTasks extends AsyncTask<Integer, Integer, ArrayList<Double>> {

        double total = 0;
        double average = 0;
        int limit;

        @Override
        protected void onPreExecute() {
            Log.d("demo", "onPreExecute ");
            adapter.clear();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
            buttonAsyc.setEnabled(false);
            buttonThread.setEnabled(false);
            textViewprogress.setText("Hello");
        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            buttonAsyc.setEnabled(true);
            buttonThread.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("demo", "onProgressUpdate " + values[0]);

            progressBar.setMax(values[1]);
            progressBar.setProgress(values[0]);
            textViewprogress.setText(values[0].toString() + " /" + limit);
            textAverage.setText("Average " + average);
            adapter.notifyDataSetChanged();

        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... params) {
            Log.d("demo", "doInBackground " + params[0]);


            limit = params[0];
            progressBar.setMax((params[0])); //wrong .. can't access ui in childthread
            double count = 0;
            for (int i = 0; i < params[0]; i++) {
                list.add(HeavyWork.getNumber());
                total = total + list.get(i);
                average = total / (i + 1);
                publishProgress(i + 1, params[0]);
                progressBar.setProgress(i + 1); //wrong .. can't access ui in childthread

            }

            textViewprogress.setText("Hello00000"); //wrong .. can't access ui in childthread
            return list;
        }
    }


    class DoWork implements Runnable {
        int params;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_START = 0x00;
        static final int STATUS_END = 0x10;
        static final String PROGRESS_KEY = "Progress";
        static final String PROGRESS_MAX_LENGTH = "PROGRESS_MAX_LENGTH";
        static final String PROGRESS_AVERAGE = "PROGRESS_AVERAGE";

        public DoWork(int count) {
           params=count;

            Log.d("demo", "startconstructor ");

        }

        @Override
        public void run() {
            double total = 0;
            double average;

            Log.d("demo", "startrun ");
            Message startMessage = new Message();
            startMessage.what = STATUS_START;

            Bundle startbundle = new Bundle();
            startbundle.putInt(PROGRESS_MAX_LENGTH, (Integer) params);
            startMessage.setData(startbundle);
            handler.sendMessage(startMessage);


            Log.d("demo", "start ");
            progressBar.setMax(params);
            for (int i = 0; i < params; i++) {
                Log.d("demo", "progress ");
                list.add(HeavyWork.getNumber());
                total = (double) (total + list.get(i));
                average = total / (i + 1);
                Message message = new Message();
                message.what = STATUS_PROGRESS;

                Bundle bundle = new Bundle();
                bundle.putDouble(PROGRESS_AVERAGE, (Double) average);
                bundle.putInt(PROGRESS_KEY, (Integer) i + 1);
                bundle.putInt(PROGRESS_MAX_LENGTH, (Integer) params);

                message.setData(bundle);
                handler.sendMessage(message);
                // progressBar.setProgress(i+1);
                // textViewprogress.setText((i+1)+" /"+count);
                // textAverage.setText("Average "+ average);
                // adapter.notifyDataSetChanged();

                //progressBar.setProgress(i+1); //wrong .. can't access ui in childthread


            }


            Log.d("demo", "end run ");
            Message endMessage = new Message();
            startMessage.what = STATUS_END;

            handler.sendMessage(endMessage);


        }
    }
}
