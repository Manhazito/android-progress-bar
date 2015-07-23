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

    private ProgressBar progressBar;
    private TextView progressLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.deliveryProgressBar);
        progressBar.canSetManually(true);
        Button btnBack = (Button) findViewById(R.id.btnBack);
        Button btnForward = (Button) findViewById(R.id.btnForward);
        Button btnReset = (Button) findViewById(R.id.btnReset);
        progressLabel = (TextView) findViewById(R.id.progressLabel);
        progressLabel.setText("Stage: " + progressBar.getProgressStage());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.goBackward();
            }
        });
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.goForward();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setProgressStage(0);
            }
        });
    }

    @Override
    public void progressChanged() {
        progressLabel.setText("Stage: " + progressBar.getProgressStage());
    }
}
