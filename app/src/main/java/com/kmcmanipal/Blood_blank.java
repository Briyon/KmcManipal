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

public class Blood_blank extends AppCompatActivity {

    CheckBox chkavail;
    RadioButton radioEquipyes, radioEquipno, radioShortyes, radioShortno;
    RadioGroup rgEquip,rgShort;

    EditText comments;


    TableHelper mydb;

    private static final String table_id = "T10";
    private static final String table_name = "Blood-Bank";


    private String timeStamp;
    Button btnClick;

    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_blank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);
        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        btnClick = (Button)findViewById(R.id.btnbloodsave);
        chkavail = (CheckBox) findViewById(R.id.blood_avil_check);
        rgEquip=(RadioGroup)findViewById(R.id.blood_radioGroup_equip);
        rgShort=(RadioGroup)findViewById(R.id.blood_radio_group);
        radioEquipyes=(RadioButton)findViewById(R.id.blood_equip_yes);
        radioEquipno=(RadioButton)findViewById(R.id.blood_equip_no);
        radioShortyes=(RadioButton)findViewById(R.id.blood_short_yes);
        radioShortno=(RadioButton)findViewById(R.id.blood_short_no);
        comments=(EditText)findViewById(R.id.blood_comments);

        Cursor rows = mydb.getExistingBloodBank(ID);

        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                chkavail.setChecked(strTOboolean(flagsrow.getString(9)));

                comments.setText(flagsrow.getString(12));
                boolean equip,shortage;
                equip = strTOboolean(flagsrow.getString(11));
                if (equip)
                    radioEquipyes.setChecked(true);
                else
                    radioEquipno.setChecked(true);
                shortage = strTOboolean(rows.getString(1));
                if (shortage)
                    radioShortyes.setChecked(true);
                else
                    radioShortno.setChecked(true);
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
                        boolean flagequip,flagshort;
                        Button btnequip,btnshort;

                        if(!radioEquipyes.isChecked()&&!radioEquipno.isChecked())
                        {
                            Toast myToast=Toast.makeText(Blood_blank.this, "Check value for Equipment Breakdown", Toast.LENGTH_LONG);
                            myToast.setGravity(Gravity.CENTER,0,0);
                            myToast.show();
                            return;
                        }
                        if(!radioShortyes.isChecked()&&!radioShortno.isChecked())
                        {
                            Toast myToast=Toast.makeText(Blood_blank.this, "Check value for Shoratge of drugs", Toast.LENGTH_LONG);
                            myToast.setGravity(Gravity.CENTER,0,0);
                            myToast.show();
                            return;
                        }

                        int select_id = rgEquip.getCheckedRadioButtonId();
                        btnequip=(RadioButton)findViewById(select_id);
                        if (btnequip.getText().toString().equalsIgnoreCase("yes"))
                            flagequip = true;
                        else
                            flagequip = false;

                        int select = rgShort.getCheckedRadioButtonId();
                        btnshort=(RadioButton)findViewById(select);
                        if (btnshort.getText().toString().equalsIgnoreCase("yes"))
                            flagshort = true;
                        else
                            flagshort = false;

                        if (btnClick.getText().equals("SAVE")) {
                            boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);

                            if (res) {
                                ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                                boolean res1 = mydb.insertBloodBank(ID, flagshort);

                                boolean res2 = mydb.insert_FLAGS(ID, null, null, false, false, false, null,timeStamp,
                                        false, chkavail.isChecked(), false, flagequip, comments.getText().toString().trim());


                                if (res1 == res2)
                                    showMessage(true, "Message", "Saved Successfully");
                                else
                                    showMessage(false, "ERROR", "Record Not Saved ");

                            }
                        } else {
                            boolean res1 = mydb.updateBloodBank(ID, flagshort);

                            boolean res2 = mydb.updateFLAGS(ID, null, null, false, false, false,  null,
                                    timeStamp, false,chkavail.isChecked(), false,flagequip, comments.getText().toString().trim());

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
            AlertDialog.Builder builder = new AlertDialog.Builder(Blood_blank.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    mydb.close();
                    Intent i = new Intent(Blood_blank.this, Rounds.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Blood_blank.this);
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

    public boolean strTOboolean(String str) {
        if (str.equals("0"))
            return false;
        else
            return true;
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
                .setMessage("Exit Blood Bank?")
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
