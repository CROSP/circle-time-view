package net.crosp.android.circletimeviewexample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.crosp.libs.android.circletimeview.CircleTimeView;


public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private CircleTimeView circleTimeView;
    private Button buttonManualSetup;
    private Button buttonStartTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleTimeView = (CircleTimeView) findViewById(R.id.circle_timer_view);
        buttonManualSetup = (Button) findViewById(R.id.button_manual_setup);
        buttonStartTimer = (Button) findViewById(R.id.button_start_time);
        buttonStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleTimeView.startTimer();
            }
        });
        buttonManualSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleTimeView.stopTimer();
                circleTimeView.setCurrentTimeMode(CircleTimeView.MODE_MANUAL_SETUP);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setClockViewCallbacks();
            }
        }, 2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeAppearance();
            }
        }, 8000);


    }

    private void changeAppearance() {
        circleTimeView.setHighlightMarkLineColor(Color.GREEN);
        circleTimeView.setCircleButtonColor(Color.GREEN);
        circleTimeView.setCircleButtonPressedColor(Color.GREEN);
        circleTimeView.setCircleColor(Color.CYAN);
        circleTimeView.setCircleStrokeWidth(10);
        circleTimeView.setCircleButtonRadius(20);
        circleTimeView.setInnerRadius(370);
        circleTimeView.setLabelText(R.string.app_name);
        circleTimeView.setLapBackgroundColor(Color.CYAN);
        circleTimeView.setLabelTextSize(28);
        circleTimeView.setLapLabelTextColor(Color.BLACK);
        circleTimeView.setLapLabelMarginTop(12);
        circleTimeView.setLapLabelTextSize(20);
        circleTimeView.setMarkLineColor(Color.CYAN);
        circleTimeView.setMarginTopLabel(10);
        circleTimeView.setMinuteMarkCount(60);
        circleTimeView.setMarkLineWidth(3);
        circleTimeView.setOuterRadius(400);
        circleTimeView.setMarkSize(40);
        circleTimeView.setPaddingInnerRadius(20);
        circleTimeView.setQuarterMarkSize(80);
        circleTimeView.setPaddingQuarterNumber(85);
        circleTimeView.setQuarterNumberColor(Color.GREEN);
        circleTimeView.setLabelTextColor(Color.GREEN);
        circleTimeView.setTimeNumberColor(Color.CYAN);
        circleTimeView.setQuarterNumbersTextSize(35);
        circleTimeView.setTimeNumbersTextSize(120);
    }

    private void setClockViewCallbacks() {
        circleTimeView.setLapLabelDataProvider(new CircleTimeView.LapDataProvider() {
            @Override
            public String getLapLabelText(long currentTimeInSeconds) {
                return String.valueOf(currentTimeInSeconds % 60);
            }
        });
        circleTimeView.setCircleTimeListener(new CircleTimeView.CircleTimeListener() {
            @Override
            public void onTimeManuallySet(long time) {
                Log.d("TIME LISTENER", "onTimeManuallySet " + time);
            }

            @Override
            public void onTimeManuallyChanged(long time) {
                Log.d("TIME LISTENER", "onTimeManuallyChanged " + time);
            }

            @Override
            public void onTimeUpdated(long time) {
                Log.d("TIME LISTENER", "onTimeUpdated " + time);
            }
        });
        circleTimeView.setCircleTimerListener(new CircleTimeView.CircleTimerListener() {
            @Override
            public void onTimerStop() {
                Log.d("TIMER LISTENER", "onTimerStop ");

            }

            @Override
            public void onTimerStart(long time) {
                Log.d("TIMER LISTENER", "onTimerStart " + time);
            }

            @Override
            public void onTimerTimeValueChanged(long time) {
                Log.d("TIMER LISTENER", "onTimerTimeValueChanged " + time);
            }
        });
        circleTimeView.startTimer();
    }
}
