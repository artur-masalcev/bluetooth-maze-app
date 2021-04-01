package bluetooth.maze;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import static bluetooth.maze.MainActivity.ACCELEROMETER_SENSITIVITY_PREFERENCE;
import static bluetooth.maze.MainActivity.APP_PREFERENCES;

public class SettingsActivity extends AppCompatActivity {

    SeekBar ASSB; //ASSB - accelerometer settings seek bar
    TextView ASSBview;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ASSB = (SeekBar)findViewById(R.id.accelerometerSettingsSeekBar);
        ASSBview = (TextView)findViewById(R.id.ASSBview);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mSettings.contains(ACCELEROMETER_SENSITIVITY_PREFERENCE)) {
            int accelerometer_sensitivity = mSettings.getInt(ACCELEROMETER_SENSITIVITY_PREFERENCE,5);
            ASSBview.setText(String.valueOf(accelerometer_sensitivity));
            ASSB.setProgress(accelerometer_sensitivity);
        }
        else{
            ASSB.setProgress(5);
            ASSBview.setText(String.valueOf(5));
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(ACCELEROMETER_SENSITIVITY_PREFERENCE, ASSB.getProgress());
            editor.apply();
        }

        ASSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ASSBview.setText(String.valueOf(ASSB.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ASSBview.setText(String.valueOf(ASSB.getProgress()));
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(ACCELEROMETER_SENSITIVITY_PREFERENCE, ASSB.getProgress());
                editor.apply();
            }
        });

        ASSB.setMax(50);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mSettings.contains(ACCELEROMETER_SENSITIVITY_PREFERENCE)) {
            int accelerometer_sensitivity = mSettings.getInt(ACCELEROMETER_SENSITIVITY_PREFERENCE,5);
            ASSBview.setText(String.valueOf(accelerometer_sensitivity));
            ASSB.setProgress(accelerometer_sensitivity);
        }
        else{
            ASSB.setProgress(5);
            ASSBview.setText(String.valueOf(5));
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(ACCELEROMETER_SENSITIVITY_PREFERENCE, ASSB.getProgress());
            editor.apply();
        }

    }
}
