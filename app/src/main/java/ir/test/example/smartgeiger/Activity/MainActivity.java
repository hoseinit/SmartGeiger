package ir.test.example.smartgeiger.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import ir.test.example.smartgeiger.MyDialogFragment;
import ir.test.example.smartgeiger.R;
import ir.test.example.smartgeiger.utility.MyPreferences;
import kr.ftlab.lib.SmartSensor;
import kr.ftlab.lib.SmartSensorEventListener;
import kr.ftlab.lib.SmartSensorResultGE;

public class MainActivity extends FragmentActivity implements SmartSensorEventListener, MyDialogFragment.OnPrefChangeListener {
    private SmartSensor mMI ;
    private SmartSensorResultGE mResultGE;

    private Button btnStart;
//    private TextView txtResultCPM;
//    private TextView txtResultSV;

    int mProcess_Status = 0;
    int Process_Stop = 0;
    int Process_Start = 1;

    private ArcProgress arcProgressSv, arcProgressCpm;

    MyPreferences preferences;

    BroadcastReceiver mHeadSetConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        stopSensing();
                        btnStart.setEnabled(false);
                        Toast.makeText(MainActivity.this, R.string.sensor_not_attached, Toast.LENGTH_SHORT).show();
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        Toast.makeText(MainActivity.this, R.string.sensor_attached, Toast.LENGTH_SHORT).show();
                        btnStart.setEnabled(true);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnStart = (Button) findViewById(R.id.button_on);
//        txtResultCPM = (TextView) findViewById(R.id.textresultcpm);
//        txtResultSV = (TextView) findViewById(R.id.textresultsv);

        arcProgressSv = (ArcProgress) findViewById(R.id.arc_progress_Sv);
        arcProgressCpm = (ArcProgress) findViewById(R.id.arc_progress_cpm);

        mMI = new SmartSensor(MainActivity.this, this);
        mMI.selectDevice(SmartSensor.GE);

        initPrefs();
        updateArcs();

    }

    private void updateArcs() {
        arcProgressCpm.setMax(preferences.getCPM());
        arcProgressSv.setMax(preferences.getSv());
    }

    private void initPrefs() {
        preferences = new MyPreferences(getApplicationContext());
        if (preferences.isFirstTime() ){
            preferences.restoreDefualts();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if (mProcess_Status == Process_Start) {
                        stopSensing();
                    }
                    else {
                        startSensing();
                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to connect to your External device", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void mOnClick(View v) {
        if(v.getId() == R.id.button_on) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
            } else {
                if (mProcess_Status == Process_Start) {
                    stopSensing();
                } else {
                    startSensing();
                }
            }
        } else if (v.getId() == R.id.button_pref) {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            MyDialogFragment editNameDialog = new MyDialogFragment();
            editNameDialog.show(manager, "");
        }
    }

    public void startSensing()
    {
        btnStart.setText(R.string.stop);
        mProcess_Status = Process_Start;

        mMI.start();

    }

    public void stopSensing()
    {
        btnStart.setText(R.string.start);
        mProcess_Status = Process_Stop;
        mMI.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_calibration) {
            mMI.registerSelfConfiguration();
            mMI.start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intflt = new IntentFilter();
        intflt.addAction(Intent.ACTION_HEADSET_PLUG);
        this.registerReceiver(mHeadSetConnectReceiver, intflt);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mHeadSetConnectReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMI.quit();
        finish();
        System.exit(0);
        super.onDestroy();
    }

    @Override
    public void onMeasured() {
        String str ="";
        mResultGE = mMI.getResultGE();

        str = String.format("%1.2f", mResultGE.GE_CPM);
//        txtResultCPM.setText(str);
//        DecimalFormat twoDForm = new DecimalFormat("#.##");

        arcProgressCpm.setProgress(mResultGE.GE_CPM);
        arcProgressCpm.setBottomText(str);

        str = String.format("%1.2f", mResultGE.GE_uSv);
//        txtResultSV.setText(str);
        arcProgressSv.setProgress((mResultGE.GE_uSv));
        arcProgressSv.setBottomText(str);

    }

    @Override
    public void onSelfConfigurated() {
        mProcess_Status = 0;
        btnStart.setText(getString(R.string.start));
    }

    @Override
    public void onPrefChange() {
        updateArcs();
    }
}