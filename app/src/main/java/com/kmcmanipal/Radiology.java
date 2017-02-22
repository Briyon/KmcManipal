package com.kmcmanipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Radiology extends AppCompatActivity {

    RadioButton radioyes, radiono;
    RadioGroup radiostock;
    CheckBox chkDoctors,chkSupport;


    EditText comments;
    TableHelper mydb;

    private static final String table_id = "T15";
    private static final String table_name = "Radiology";


    private String timeStamp;
    Button btnClick;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);
        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        btnClick = (Button)findViewById(R.id.radiologysave);
        chkDoctors = (CheckBox) findViewById(R.id.checkDoc);
        chkSupport = (CheckBox) findViewById(R.id.checkSupport);
        radiostock=(RadioGroup)findViewById(R.id.radiologygroup);
        radioyes=(RadioButton)findViewById(R.id.radiologyes);
        radiono=(RadioButton)findViewById(R.id.radiologyno);
        comments=(EditText)findViewById(R.id.radiologycomments);

        Cursor rows = mydb.getExistingRadiology(ID);

        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                chkDoctors.setChecked(strTOboolean(rows.getString(2)));
                chkSupport.setChecked(strTOboolean(rows.getString(1)));
                comments.setText(flagsrow.getString(12));
                boolean bool;
                bool = strTOboolean(flagsrow.getString(11));
                if (bool)
                    radioyes.setChecked(true);
                else
                    radiono.setChecked(true);

                btnClick.setText("UPDATE");

            }


        }
        AddData();
    }


    public void AddData() {
        btnClick.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag;
                        Button btn;
                        if(!radioyes.isChecked()&&!radiono.isChecked())
                        {
                            Toast myToast=Toast.makeText(Radiology.this, "Check value for Equipment Breakdown", Toast.LENGTH_LONG);
                            myToast.setGravity(Gravity.CENTER,0,0);
                            myToast.show();
                            return;
                        }

                        int select_id = radiostock.getCheckedRadioButtonId();
                        btn=(RadioButton)findViewById(select_id);
                        if (btn.getText().toString().equalsIgnoreCase("yes"))
                            flag = true;
                        else
                            flag = false;

                        if (btnClick.getText().equals("SAVE")) {
                            boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);

                            if (res) {
                                ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                                boolean res1 = mydb.insertRadiology(ID, chkSupport.isChecked(), chkDoctors.isChecked());

                                boolean res2 = mydb.insert_FLAGS(ID, null, null, false,  false, false,  null,timeStamp,
                                        false, false, false, flag, comments.getText().toString().trim());


                                if (res1 == res2)
                                    showMessage(true, "Message", "Saved Successfully");
                                else
                                    showMessage(false, "ERROR", "Record Not Saved ");

                            }
                        } else {
                            boolean res1 = mydb.updateRadiology(ID, chkSupport.isChecked(), chkDoctors.isChecked());

                            boolean res2 = mydb.updateFLAGS(ID, null, null, false,  false, false,  null,
                                    timeStamp, false,false, false,flag, comments.getText().toString().trim());

                            if (res1 == res2)
                                showMessage(true, "Message", "Updated Successfully");
                            else
                                showMessage(false, "ERROR", "Updating Failed ");


                        }
                    }


                });
    }

    public void showMessage(boolean result,String title,String message)
    {
        if (result)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Radiology.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    mydb.close();
                    Intent i = new Intent(Radiology.this, Rounds.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Radiology.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    //Toast.makeText(Casul_icu.this, "DATA  NOT INSERTED", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }





    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    public boolean strTOboolean(String str) {
        if (str.equals("0"))
            return false;
        else
            return true;
    }


    //Exit Condition
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                .setMessage("Exit Radiology?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
