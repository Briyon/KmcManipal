package com.kmcmanipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class general_ward_main extends AppCompatActivity
{
    TableHelper mydb;
    EditText ward_name,general_issues;
    CheckBox g_clean, g_house, g_pharm;
    TextView  a;

    private static Button button_next;
    private static final String table_id = "T8";
    private static final String table_name ="General";
    String ID;
    String cdate,timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_ward_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mydb = new TableHelper(this);
        // cdate = mydb.getPendingReportDate(mydb.getUsername());
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());


        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);


        ward_name = (EditText) findViewById((R.id.editTextWardName));
        g_clean = (CheckBox) findViewById((R.id.chkbx_cleanliness));
        g_house = (CheckBox) findViewById((R.id.chkbx_housekeep));
        g_pharm = (CheckBox) findViewById((R.id.chkbx_pharmacy));


        button_next = (Button) findViewById(R.id.button_next);


        Cursor rows = mydb.getDataWARDS(ID);
        if (rows.getCount() > 0) {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                ward_name.setText(rows.getString(1));
                g_clean.setChecked(strTOboolean(flagsrow.getString(4)));
                g_house.setChecked(strTOboolean(flagsrow.getString(5)));
                g_pharm.setChecked(strTOboolean(flagsrow.getString(8)));

                button_next.setText("UPDATE");
            }
        }

        AddData();

    }


    public boolean strTOboolean(String str)
    {
        if(str.equals("0"))
            return  false;
        else
            return true;
    }


    public void AddData()
    {

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ward_name.getText().toString().isEmpty()) {
                    ward_name.setError("Enter Ward Name");
                    ward_name.requestFocus();
                    return;
                }


                if (button_next.getText().equals("SAVE")) {

                    boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);
                    if (res) {
                        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);

                        boolean res1 = mydb.insert_wards(ID, ward_name.getText().toString());


                        boolean res2 = mydb.insert_FLAGS(ID, null, null, false,  g_clean.isChecked(), false, null,
                                timeStamp, g_pharm.isChecked(), false, g_house.isChecked(), false, null);


                        if (res1 == res2)
                            showMessage(true, "Message", "Saved Successfully");
                        else
                            showMessage(false, "ERROR", "Record Not Saved ");
                    }

                } else {

                    //   String ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                    boolean res1 = mydb.update_wards(ID, ward_name.getText().toString());

                    boolean res2 = mydb.updateFLAGS(ID, null, null, false,  g_clean.isChecked(), false, null,
                            timeStamp.toString(), g_pharm.isChecked(), false, g_house.isChecked(), false, null);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(general_ward_main.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(general_ward_main.this, Rounds.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(general_ward_main.this);
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


    //Exit Condition
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                .setMessage("Exit General Ward?")
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





