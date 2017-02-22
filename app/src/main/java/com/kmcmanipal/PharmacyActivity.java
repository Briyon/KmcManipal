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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PharmacyActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    CheckBox chkavail, chkindents_ontime;
    RadioButton radioyes, radiono;
    RadioGroup radiostock;

    EditText comments;


    TableHelper mydb;

    private static final String table_id = "T14";
    private static final String table_name = "Pharmacy";


    private String timeStamp;
    Button btnNext;
    Intent intent_refresh;

    String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mydb = new TableHelper(this);
        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        btnNext = (Button)findViewById(R.id.btnsavephar);
        chkavail = (CheckBox) findViewById(R.id.checkBox2);
        chkindents_ontime = (CheckBox) findViewById(R.id.checkBox3);
        radiostock=(RadioGroup)findViewById(R.id.radiogrp);
        radioyes=(RadioButton)findViewById(R.id.radioyes);
        radiono=(RadioButton)findViewById(R.id.radiono);
        comments=(EditText)findViewById(R.id.comments);
        // radiostock = (RadioGroup) findViewById(R.id.radiogpoption);


        intent_refresh = getIntent();
        Cursor rows = mydb.getExistingpharmacy(ID);

        if (rows.getCount() > 0) {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                chkavail.setChecked(strTOboolean(flagsrow.getString(9)));
                chkindents_ontime.setChecked(strTOboolean(rows.getString(2)));
                comments.setText(flagsrow.getString(12));
                boolean b;
                b = strTOboolean(rows.getString(1));
                if (b)
                    radioyes.setChecked(true);
                else
                    radiono.setChecked(true);

                btnNext.setText("UPDATE");

            }


        }
        AddData();
    }

    public void AddData() {
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean br;
                        Button btn;
                        if(!radioyes.isChecked()&&!radiono.isChecked())
                        {
                            Toast myToast=Toast.makeText(PharmacyActivity.this, "Check value for Stock out of drugs", Toast.LENGTH_LONG);
                            myToast.setGravity(Gravity.CENTER,0,0);
                            myToast.show();
                            return;
                        }


                        int select_id = radiostock.getCheckedRadioButtonId();
                        btn=(RadioButton)findViewById(select_id);
                        if (btn.getText().toString().equalsIgnoreCase("yes"))
                            br = true;
                         else
                            br = false;

                        if (btnNext.getText().equals("SAVE")) {
                            boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);
                            //Toast.makeText(PharmacyActivity.this, mydb.getUserID(), Toast.LENGTH_LONG).show();
                            if (res) {
                                ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                                boolean res1 = mydb.insert_pharmacy(ID, br, chkindents_ontime.isChecked());

                                boolean res2 = mydb.insert_FLAGS(ID, null, null, false, false,  false, null,timeStamp,
                                        false, chkavail.isChecked(), false, false, comments.getText().toString().trim());


                                if (res1 == res2)
                                    showMessage(true, "Message", "Saved Successfully");
                                else
                                    showMessage(false, "ERROR", "Record Not Saved ");

                            }
                        } else {
                            boolean res1 = mydb.updatePHARMACY(ID, br, chkindents_ontime.isChecked());

                            boolean res2 = mydb.updateFLAGS(ID, null, null, false, false, false,  null,
                                    timeStamp, false,chkavail.isChecked(), false,false, comments.getText().toString().trim());

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PharmacyActivity.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    mydb.close();
                    Intent i = new Intent(PharmacyActivity.this, Rounds.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PharmacyActivity.this);
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
    public boolean onSupportNavigateUp ()
    {
        onBackPressed();
        return true;
    }


    //Exit Condition
    @Override
    public void onBackPressed () {
        new AlertDialog.Builder(this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                .setMessage("Exit Pharmacy?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public boolean strTOboolean(String str) {
        if (str.equals("0"))
            return false;
        else
            return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



