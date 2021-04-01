package bluetooth.maze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener, SensorEventListener {
    //Preferences
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";

    public static final String ACCELEROMETER_SENSITIVITY_PREFERENCE = "ACCELEROMETER_SENSITIVITY";
    public static final String BEST_TIME_PREFERENCE = "BEST_TIME";

    public int bestTime = 0;
    public int accelerometerSensitivity = 5;

    //Request codes
    public final int ENABLE_BLUETOOTH_REQUEST = 0;
    public final int GO_TO_SETTINGS_ACTIVITY = 1;

    //Flags
    private boolean isArduinoAvaible;
    private boolean isAccelerometerMode = false;
    private boolean hintIsHidden = false;
    private boolean isGameStarted = false;

    //android.bluetooth
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;

    //Motion sensors
    private float lastX, lastY, lastZ;
    private float deltaX = 0;
    private float deltaY = 0;
    private float lastDeltaX, lastDeltaY;

    float startTime;
    int timerTimeMS;
    boolean isTimerBeingUsed = false;

    //Thread
    Handler timerHandler;

    //Views
    TextView statusView;
    TextView statusIndicator;
    Joystick joystick;
    TextView timerView;
    TextView bestTimeView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQUEST) { // Enabling Bluetooth
            if (resultCode != RESULT_OK) {
               String text = "Приложение остановлено. Пользователь отклонил запрос на" +
                       " подключение Bluetooth";
               Toast.makeText(this, text, Toast.LENGTH_LONG).show();

               this.finish();
            }
            else{
               connectToModule();
            }
        }
    }
    void enableBluetoothConnection(){
        statusIndicator.setTextColor(getResources().getColor(R.color.orange));
        if(!bluetoothAdapter.isEnabled()){
            super.onPause();
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, ENABLE_BLUETOOTH_REQUEST);
        }
        else connectToModule();
    } //Enables bluetooth if it's not enabled. If user refuses to turn the Bluetooth on, shuts down the app

    boolean findArduinoModule(){
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(!bondedDevices.isEmpty()){

            for (BluetoothDevice iterator : bondedDevices){
                Log.d("CustomMessage",iterator.getAddress());
                if(iterator.getAddress().equals("98:D3:71:F9:69:EB")){
                    bluetoothDevice = iterator;
                    return true;
                }
            }
            return false;
        }
        else return false;
    }//Searches for the Arduino Bluetooth module in the bonded devices list. Returns false if it haven't found

    void connectToModule(){
        statusIndicator.setTextColor(getResources().getColor(R.color.orange));
        if(findArduinoModule()){
            statusIndicator.setTextColor(getResources().getColor(R.color.orange));
            try {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.randomUUID());
                bluetoothDevice = bluetoothAdapter.getRemoteDevice("98:D3:71:F9:69:EB");
            } catch (IOException e) {
                statusIndicator.setTextColor(getResources().getColor(R.color.red));
                e.printStackTrace();
            }
            try{
                bluetoothSocket.connect();
            } catch (IOException el){
                el.printStackTrace();
                try{
                bluetoothSocket =(BluetoothSocket) bluetoothDevice.getClass().
                        getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                bluetoothSocket.connect();
                    statusIndicator.setTextColor(getResources().getColor(R.color.green));
                try{
                    BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
                    bluetoothReceiver.start();
                }
                catch (Exception receiverException){
                    receiverException.printStackTrace();
                    statusIndicator.setTextColor(getResources().getColor(R.color.orange));
                }
                isArduinoAvaible = true;
                    sendCommand(-9, 19);
                }
                catch (Exception e2){
                    statusIndicator.setTextColor(getResources().getColor(R.color.red));
                    e2.printStackTrace();
                }
            }
        }
        else{
            statusIndicator.setTextColor(getResources().getColor(R.color.red));
        }
    } //Main function of the app, that connects to Arduino Bluetooth module. Gets called if phone's Bluetooth module is enabled

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                statusIndicator.setTextColor(getResources().getColor(R.color.orange));
                connectToModule();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                statusIndicator.setTextColor(getResources().getColor(R.color.orange));
                connectToModule();
            }
        }
    };

    private void runTimer(){
        startTime = SystemClock.uptimeMillis();
        isTimerBeingUsed = true;
        timerHandler.postDelayed(customTimer,0);
    }

    public Runnable customTimer = new Runnable() {

        private int minutes;
        private int seconds;
        private int milliseconds;

        public void run() {

            timerTimeMS = (int)(SystemClock.uptimeMillis() - startTime);

            milliseconds = timerTimeMS%1000;
            seconds = (timerTimeMS/1000)%60;
            minutes = (timerTimeMS/1000)/60;

            timerView.setText(
                    String.valueOf( String.format("%02d",minutes)+":"+String.format("%02d",seconds)+":"+String.format("%03d",milliseconds))
            );
            if(isTimerBeingUsed)timerHandler.postDelayed(this, 0);
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    } //Handling Accelerometer menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.turnOnAccelerometer) {
            if(!isAccelerometerMode) {
                isAccelerometerMode = true;
                joystick.setGrey();
                joystick.isClickable = false;
                joystick.invalidate();

                Toast.makeText(this, "Акселерометр включен", Toast.LENGTH_LONG).show();
            }
            else{
                isAccelerometerMode = false;
                joystick.setDefaultColor();
                joystick.isClickable = true;
                joystick.setPosition(0,0);
                joystick.invalidate();
                Toast.makeText(this, "Акселерометр выключен", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (item.getItemId() == R.id.enterSettings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent,GO_TO_SETTINGS_ACTIVITY);
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //Accelerometer menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Main variables and objects of app
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        statusView = (TextView)findViewById(R.id.statusView);
        joystick = (Joystick)findViewById(R.id.joystick);
        isArduinoAvaible = false;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        statusIndicator = (TextView)findViewById(R.id.statusIndicator);
        timerView = (TextView)findViewById(R.id.timer);
        timerHandler = new Handler();
        bestTimeView = (TextView)findViewById(R.id.timer_best);

        //Listening if Arduino Bluetooth module is about to disconnect
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        //Turning on the accelerometer
        if(mSettings.contains(ACCELEROMETER_SENSITIVITY_PREFERENCE)) {
            accelerometerSensitivity = mSettings.getInt(ACCELEROMETER_SENSITIVITY_PREFERENCE,5);
            accelerometerSensitivity += 5;
        }

        if(mSettings.contains(BEST_TIME_PREFERENCE)){
            bestTime = mSettings.getInt(BEST_TIME_PREFERENCE,0);
            bestTimeView.setText(convertMS(bestTime));
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this,"Устройство не поддерживает режим акселерометра",Toast.LENGTH_LONG).show();
        }

        enableBluetoothConnection(); // Makes connection between module and app
    } //Main method of the program

    @Override
    public void onResume(){
        super.onResume();
        if(mSettings.contains(ACCELEROMETER_SENSITIVITY_PREFERENCE)) {
            accelerometerSensitivity = mSettings.getInt(ACCELEROMETER_SENSITIVITY_PREFERENCE,5);
            accelerometerSensitivity += 5;
        }

        if(mSettings.contains(BEST_TIME_PREFERENCE)){
            bestTime = mSettings.getInt(BEST_TIME_PREFERENCE,0);
            bestTimeView.setText(convertMS(bestTime));
        }

    }

    String convertMS(int ms){
        return (ms/1000)/60+":"+(ms/1000)%60+":"+ms%1000;
    }

    private class BluetoothReceiver extends Thread{
        @Override
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = bluetoothSocket.getInputStream().read(buffer);
                    String readMessage = new String(buffer,0,bytes);
                    if(readMessage.charAt(0) == '1'){ //Finish
                        isTimerBeingUsed = false;
                        isGameStarted = false;
                        if(timerTimeMS < bestTime || bestTime == 0){
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putInt(BEST_TIME_PREFERENCE, timerTimeMS);
                            bestTime = timerTimeMS;
                            bestTimeView.setText(convertMS(bestTime));
                            editor.apply();
                        }
                    }
                    else if(readMessage.charAt(0) == '0'){ //Start
                        isGameStarted = true;
                        isTimerBeingUsed = false;
                        runTimer();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }

    public void sendCommand(int x, int y){

        if(isArduinoAvaible){
            try {
                String ln;
                outputStream = bluetoothSocket.getOutputStream();
                ln = "$";
                outputStream.write(ln.getBytes());
                ln = String.valueOf(x);
                outputStream.write(ln.getBytes());
                ln = " ";
                outputStream.write(ln.getBytes());
                ln = String.valueOf(y);
                outputStream.write(ln.getBytes());
                ln = ";";
                outputStream.write(ln.getBytes());
            } catch (Exception e){
                e.printStackTrace();
                statusIndicator.setTextColor(getResources().getColor(R.color.orange));
            }

        }
    } //Sends command to Arduino Bluetooth module

    @Override
    public void onJoystickMoved(int x, int y) {
        if(!isAccelerometerMode) {
            sendCommand(x,y);
        }
    } //Joysticks' click listener

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(isAccelerometerMode) {
            // get the change of the x,y,z values of the accelerometer
            deltaX = (accelerometerSensitivity*(lastX - event.values[0]));
            deltaY = (accelerometerSensitivity*(lastY - event.values[1]));

            if(deltaX > 60)deltaX = 60;
            if(deltaX < -60)deltaX = -60;
            if(deltaY > 60)deltaY = 60;
            if(deltaY < -60)deltaY = -60;

            joystick.setPosition(deltaX, deltaY);

            deltaX = (int)deltaX;
            deltaY = (int)deltaY;

            sendCommand((int) deltaX, (int) deltaY);

            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {} //Has empty body
}