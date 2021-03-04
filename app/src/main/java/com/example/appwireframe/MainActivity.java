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
import android.widget.Toast;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "okay";
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
        progressBar.setVisibility(View.GONE);
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
                if (!adapter.isEmpty()) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }


                Log.d(TAG, "onClick: Async button clicked");
                int seekBarComplexityValue = seekBarSimpleComplexity.getProgress();

                if (seekBarComplexityValue != 0) {
                    new MyTasks().execute(seekBarComplexityValue);
                } else {
                    Toast.makeText(MainActivity.this, "Simple Complexity cant be Zero", Toast.LENGTH_SHORT).show();
                }
                // threadPool.execute(new DoWork(seekBarComplexityValue));


            }
        });


        buttonThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: thread button clicked");
                int seekBarComplexityValue = seekBarSimpleComplexity.getProgress();
                if (!adapter.isEmpty()) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }

                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        Log.d("demo", "Message " + msg.what);

                        switch (msg.what) {
                            case DoWork.STATUS_PROGRESS:
                                Log.d(TAG, "on progress");
                                progressBar.setProgress(msg.getData().getInt(DoWork.PROGRESS_KEY));
                                textViewprogress.setText(msg.getData().getInt(DoWork.PROGRESS_KEY) + " /" + seekBarComplexityValue);
                                textAverage.setText("Average: "+msg.getData().getDouble(DoWork.PROGRESS_AVERAGE));
                                adapter.notifyDataSetChanged();

                                break;
                            case DoWork.STATUS_START:
                                    Log.d(TAG,"on start");
                                progressBar.setVisibility(View.VISIBLE);
                                progressBar.setMax(seekBarComplexityValue);
                                buttonAsyc.setEnabled(false);
                                buttonThread.setEnabled(false);
                                break;
                            case DoWork.STATUS_END:
                                Log.d(TAG, "on end ");
                                buttonAsyc.setEnabled(true);
                                buttonThread.setEnabled(true);
                                break;
                        }
                        return false;
                    }

                });


                if (seekBarComplexityValue != 0) {

                    threadPool.execute(new DoWork(seekBarComplexityValue));


            } else {
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Simple Complexity cant be Zero", Toast.LENGTH_SHORT).show();
            }

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
            double count = 0;
            for (int i = 0; i < params[0]; i++) {
                list.add(HeavyWork.getNumber());
                total = total + list.get(i);
                average = total / (i + 1);
                publishProgress(i + 1, params[0]);
           }


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
            params = count;

            Log.d("demo", "startconstructor ");

        }

        @Override
        public void run() {
            double total = 0;
            double average;

            Log.d("demo", "startrun ");
            Message startMessage = new Message();
            startMessage.what = STATUS_START;

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
                message.setData(bundle);
                handler.sendMessage(message);
            }

            Log.d("demo", "end run ");
            Message endMessage = new Message();
            endMessage.what = STATUS_END;
            handler.sendMessage(endMessage);
        }
    }
}
