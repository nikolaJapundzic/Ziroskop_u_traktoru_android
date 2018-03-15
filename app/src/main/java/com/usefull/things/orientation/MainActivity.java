package com.usefull.things.orientation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usefull.things.orientation.AttitudeIndicator;
import com.usefull.things.orientation.Orientation;
import com.usefull.things.orientation.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.CALL_PHONE;

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

  private SharedPreferences prefs;

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
    button1 = (Button) findViewById(R.id.button1);
    button2 = (Button) findViewById(R.id.button2);
    checkBox = (CheckBox) findViewById(R.id.checkBox);
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = pref.edit();
    editText1.setText(pref.getString("kljuc1","50"));
    editText2.setText(pref.getString("kljuc2","50"));
    setBok = Float.parseFloat(pref.getString("kljuc1","50"));
    setUzduz = Float.parseFloat(pref.getString("kljuc2","50"));
    editor.commit();

    button1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(flagKalib){
          if(xAngleNewSTATIC > 0){
            xAngleKALIBRACIONI = xAngleNewSTATIC;
          }
          if(yAngleNewSTATIC > 0){
            yAngleKALIBRACIONI = yAngleNewSTATIC;
          }
          if(xAngleNewSTATIC < 0){
            xAngleKALIBRACIONI = xAngleNewSTATIC;
          }
          if(yAngleNewSTATIC < 0){
            yAngleKALIBRACIONI = yAngleNewSTATIC;
          }
          flagKalib = false;
          button1.setTextSize(11);
          button1.setText("Restore");
        }else{
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
        try{
          if(Float.parseFloat(String.valueOf(editText1.getText())) < 179.99){
            if(Float.parseFloat(String.valueOf(editText2.getText())) < 179.99){
              AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
              dialog.setCancelable(false);
              dialog.setTitle("Save");
              dialog.setMessage("Are you sure you want to save and set?" );
              dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                  //Action for "Ok".
                  SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                  SharedPreferences.Editor editor = pref.edit();
                  editor.putString("kljuc1", String.valueOf(editText1.getText()));
                  editor.putString("kljuc2", String.valueOf(editText2.getText()));
                  setBok = Float.parseFloat(String.valueOf(editText1.getText()));
                  setUzduz = Float.parseFloat(String.valueOf(editText2.getText()));
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
            }else{
              errorDialog();
            }
          }else{
            errorDialog();
          }
        }catch (Exception e){
          errorDialog();
        }
      }
    });


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


    if(checkBox.isChecked()){
      if(xAngleNewSTATIC > setBok || xAngleNewSTATIC < (0-setBok) || yAngleNewSTATIC > setUzduz || yAngleNewSTATIC < (0-setUzduz)){
        getWindow().getDecorView().setBackgroundColor(Color.RED);
        if(flagROT){
          //uradi
          //errorDialog();

        }
        flagROT  = false;

      }else{
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        flagROT = true;
      }
    }else{
      getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }


  }
  protected void errorDialog(){
    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
    dialog.setCancelable(false);
    dialog.setTitle("Error");
    dialog.setMessage("Only value from 0 to -179.99!" );
    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        //Action for "Ok".
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editText1.setText(pref.getString("kljuc1",""));
        editText2.setText(pref.getString("kljuc2",""));
        editor.commit();

      }
    });

    final AlertDialog alert = dialog.create();
    alert.show();
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0) {

          boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
          boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
          boolean callAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

          if (locationAccepted && cameraAccepted && callAccepted)
            Snackbar.make(view, "Permission Granted, Now you can access location data and camera and call.", Snackbar.LENGTH_LONG).show();
          else {

            Snackbar.make(view, "Permission Denied, You cannot access location data and camera and call.", Snackbar.LENGTH_LONG).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("You need to allow access to three permissions",
                        new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                              requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, CALL_PHONE},
                                      PERMISSION_REQUEST_CODE);
                            }
                          }
                        });
                return;
              }
            }

          }
        }


        break;
    }
  }


  private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(MainActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
  }
  private boolean checkPermission() {
    int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
    int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
    int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

    return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermission() {

    ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, CALL_PHONE}, PERMISSION_REQUEST_CODE);

  }

}
