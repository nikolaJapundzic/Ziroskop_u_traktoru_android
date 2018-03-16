package com.usefull.things.orientation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Orientation.Listener {

  private Orientation mOrientation;
  private AttitudeIndicator mAttitudeIndicator;

  private TextView textView2;
  private TextView textView4;
  private EditText editText1;
  private EditText editText2;
  private Button button1;
  private Button button2;
  private CheckBox checkBox;
  private EditText editText3;
  private EditText editText4;

  private static float xAngleNewSTATIC;
  private static float yAngleNewSTATIC;
  private static float xAngleKALIBRACIONI = 0;
  private static float yAngleKALIBRACIONI = 0;
  private static boolean flagKalib = true;
  private static boolean flagROT = true;

  private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
  private static final int PERMISSION_REQUEST_CODE = 200;
  private View view;


  private float setBok;
  private float setUzduz;
  private float setTres;
  private String lokacija = ":D";

  private SharedPreferences prefs;

  private LocationManager locationManager;
  private LocationListener listener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Remove title bar
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
    setContentView(R.layout.main);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.main);

    getWindow().getDecorView().setBackgroundColor(Color.WHITE);

    mOrientation = new Orientation(this);
    mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);

    textView2 = (TextView) findViewById(R.id.textView2);
    textView4 = (TextView) findViewById(R.id.textView4);
    editText1 = (EditText) findViewById(R.id.editText1);
    editText2 = (EditText) findViewById(R.id.editText2);
    editText3 = (EditText) findViewById(R.id.editText3);
    editText4 = (EditText) findViewById(R.id.editText4);
    button1 = (Button) findViewById(R.id.button1);
    button2 = (Button) findViewById(R.id.button2);
    checkBox = (CheckBox) findViewById(R.id.checkBox);
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = pref.edit();
    editText1.setText(pref.getString("kljuc1", "50"));
    editText2.setText(pref.getString("kljuc2", "50"));
    editText3.setText(pref.getString("kljuc3", "5"));
    editText4.setText(pref.getString("kljuc4", "+3810123456789"));
    setBok = Float.parseFloat(pref.getString("kljuc1", "50"));
    setUzduz = Float.parseFloat(pref.getString("kljuc2", "50"));
    setTres = Float.parseFloat(pref.getString("kljuc3", "5"));
    editor.commit();

    button1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (flagKalib) {
          if (xAngleNewSTATIC > 0) {
            xAngleKALIBRACIONI = xAngleNewSTATIC;
          }
          if (yAngleNewSTATIC > 0) {
            yAngleKALIBRACIONI = yAngleNewSTATIC;
          }
          if (xAngleNewSTATIC < 0) {
            xAngleKALIBRACIONI = xAngleNewSTATIC;
          }
          if (yAngleNewSTATIC < 0) {
            yAngleKALIBRACIONI = yAngleNewSTATIC;
          }
          flagKalib = false;
          button1.setTextSize(11);
          button1.setText("Restore");
        } else {
          xAngleKALIBRACIONI = 0;
          yAngleKALIBRACIONI = 0;
          flagKalib = true;
          button1.setTextSize(14);
          button1.setText("Zero");
        }


      }
    });

    button2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
          if (Float.parseFloat(String.valueOf(editText1.getText())) < 180) {
            if (Float.parseFloat(String.valueOf(editText2.getText())) < 180) {
              if (Float.parseFloat(String.valueOf(editText3.getText())) < 10) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Save");
                dialog.setMessage("Are you sure you want to save and set?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int id) {
                    //Action for "Ok".
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("kljuc1", String.valueOf(editText1.getText()));
                    editor.putString("kljuc2", String.valueOf(editText2.getText()));
                    editor.putString("kljuc3", String.valueOf(editText3.getText()));
                    editor.putString("kljuc4", String.valueOf(editText4.getText()));
                    setBok = Float.parseFloat(String.valueOf(editText1.getText()));
                    setUzduz = Float.parseFloat(String.valueOf(editText2.getText()));
                    setTres = Float.parseFloat(String.valueOf(editText3.getText()));
                    editor.commit();
                  }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                          }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
              } else {
                errorDialog();
              }

            } else {
              errorDialog();
            }
          } else {
            errorDialog();
          }
        } catch (Exception e) {
          errorDialog();
        }
      }
    });
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    listener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        lokacija = String.valueOf(location.getLatitude()); // + " " + location.getLatitude()
        lokacija += " ";
        lokacija += String.valueOf(location.getLongitude());

      }

      @Override
      public void onStatusChanged(String s, int i, Bundle bundle) {

      }

      @Override
      public void onProviderEnabled(String s) {

      }

      @Override
      public void onProviderDisabled(String s) {

        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);
      }
    };
    // first check for permissions
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      return;
    }
    locationManager.requestLocationUpdates("gps", 5000, 0, listener);


  }

  @Override
  protected void onStart() {
    super.onStart();
    mOrientation.startListening(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    mOrientation.stopListening();
  }

  @Override
  public void onOrientationChanged(float pitch, float roll) {
    mAttitudeIndicator.setAttitude(pitch, roll);

    xAngleNewSTATIC = pitch - xAngleKALIBRACIONI;
    yAngleNewSTATIC = roll - yAngleKALIBRACIONI;
    mAttitudeIndicator.setAttitude(xAngleNewSTATIC, yAngleNewSTATIC);
    textView2.setText(String.format("%.2f", xAngleNewSTATIC));
    textView4.setText(String.format("%.2f", yAngleNewSTATIC));


    if (checkBox.isChecked()) {

      if (xAngleNewSTATIC > (setBok-setTres) || xAngleNewSTATIC < (0 - (setBok-setTres)) || yAngleNewSTATIC > (setUzduz-setTres) || yAngleNewSTATIC < (0 - (setUzduz-setTres))){
        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);

        if (xAngleNewSTATIC > setBok || xAngleNewSTATIC < (0 - setBok) || yAngleNewSTATIC > setUzduz || yAngleNewSTATIC < (0 - setUzduz)) {
          getWindow().getDecorView().setBackgroundColor(Color.RED);
          if (flagROT) {
            sendSMS(String.valueOf(editText4.getText()), "I flip over, please help! My coordinate are:" + lokacija);
            dialContactPhone(String.valueOf(editText4.getText()));
          }
          flagROT = false;

        } else {
          //getWindow().getDecorView().setBackgroundColor(Color.WHITE);
          flagROT = true;
        }
      }else{
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
      }


    } else {
      getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }


  }

  protected void errorDialog() {
    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
    dialog.setCancelable(false);
    dialog.setTitle("Error");
    dialog.setMessage("Only value from 0 to 179.99 for angle, and 0 to 9.99 for threshold");
    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        //Action for "Ok".
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editText1.setText(pref.getString("kljuc1", ""));
        editText2.setText(pref.getString("kljuc2", ""));
        editText3.setText(pref.getString("kljuc3", ""));
        editText4.setText(pref.getString("kljuc4", ""));
        editor.commit();

      }
    });

    final AlertDialog alert = dialog.create();
    alert.show();
  }



  private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(MainActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
  }

  private void dialContactPhone(final String phoneNumber) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
  }

  private void sendSMS(String phoneNumber, String message) {
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phoneNumber, null, message, null, null);
  }

}
