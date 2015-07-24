package org.gamaworks.android_progress_bar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.gamaworks.progressbar.ProgressBar;

public class MainActivity extends AppCompatActivity implements ProgressBar.ProgressBarListener {
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private TextView progressLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar1 = (ProgressBar) findViewById(R.id.deliveryProgressBar1);
        Button btnBack1 = (Button) findViewById(R.id.btnBack1);
        Button btnForward1 = (Button) findViewById(R.id.btnForward1);
        Button btnReset1 = (Button) findViewById(R.id.btnReset1);

        btnBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.goBackward();
            }
        });
        btnForward1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.goForward();
            }
        });
        btnReset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.setProgressStage(0);
            }
        });

        progressBar2 = (ProgressBar) findViewById(R.id.deliveryProgressBar2);
        Button btnBack2 = (Button) findViewById(R.id.btnBack2);
        Button btnForward2 = (Button) findViewById(R.id.btnForward2);
        Button btnReset2 = (Button) findViewById(R.id.btnReset2);

        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.goBackward();
            }
        });
        btnForward2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.goForward();
            }
        });
        btnReset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.setProgressStage(5);
            }
        });

        progressBar3 = (ProgressBar) findViewById(R.id.deliveryProgressBar3);
        Button btnBack3 = (Button) findViewById(R.id.btnBack3);
        Button btnForward3 = (Button) findViewById(R.id.btnForward3);
        Button btnReset3 = (Button) findViewById(R.id.btnReset3);

        btnBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.goBackward();
            }
        });
        btnForward3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.goForward();
            }
        });
        btnReset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.setProgressStage(0);
            }
        });

        progressLabel = (TextView) findViewById(R.id.progressLabel);
        progressLabel.setText("All quiet!");
    }

    @Override
    public void progressChanged(ProgressBar progressBar) {
        progressLabel.setText("Moved to stage " + progressBar.getProgressStage());
    }
}
