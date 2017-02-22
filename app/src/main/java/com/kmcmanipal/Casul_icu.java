package com.kmcmanipal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;

/**
 * Created by Sony on 6/13/2016.
 */
public class Casul_icu extends AppCompatActivity implements View.OnClickListener
{

    public Button scanBtn, scanbtn1;
    TableHelper mydb;
    int viewid;
    View scan_view;
    EditText editbedsuse, editbedsavail, editventuse, editventavail, editventrepair, editipno, editcomm,  editiss;
    Button btnbck, btnnext;
    CheckBox chkdoc, chkcln, chkhk;

    //related to stable patient
    EditText textIn1,textIn2;
    Button buttonAdd,buttonDelete;
    LinearLayout container;
    TextView tvIP,tvDetails;
    int count=0;

    private static final int  total_beds=14;
    private static final String table_id = "T3";
    private static final String table_name ="CAS-ICU";
    String ID;


    String cdate,timeStamp;

    Button btnext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casual__icu);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);
        ID= mydb.getRecordID(mydb.getPendingReportDate(), table_id);

        cdate = mydb.getPendingReportDate();
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        scanBtn = (Button) findViewById(R.id.btn_scan1);
        scanbtn1 = (Button) findViewById(R.id.btn_scan2);
        editbedsuse = (EditText) findViewById(R.id.beds_in_use);
        editbedsavail = (EditText) findViewById(R.id.beds_avail);

        scanBtn.setOnClickListener(this);
        scanbtn1.setOnClickListener(this);
        editventuse = (EditText) findViewById(R.id.vent_inuse);
        editventavail = (EditText) findViewById(R.id.vent_avail);
        editventrepair = (EditText) findViewById(R.id.vent_repair);
        editipno = (EditText) findViewById(R.id.ip_no);
        chkdoc = (CheckBox) findViewById(R.id.checkBox1);
        chkcln = (CheckBox) findViewById(R.id.checkBox2);
        chkhk = (CheckBox) findViewById(R.id.checkBox3);
        editcomm = (EditText) findViewById(R.id.comments);
        editiss = (EditText) findViewById(R.id.issues);


        btnnext = (Button) findViewById(R.id.button2_next);

        editbedsavail.setEnabled(false);

        textIn1 = (EditText) findViewById(R.id.stableIP);
        textIn2 = (EditText) findViewById(R.id.stable_details);
        buttonAdd = (Button) findViewById(R.id.add);
        buttonDelete=(Button) findViewById(R.id.btnDelete);
        container = (LinearLayout) findViewById(R.id.container);


        buttonDelete.setVisibility(View.INVISIBLE);

        textIn2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textIn2.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editbedsuse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editbedsuse.getText().toString().isEmpty()||Integer.parseInt(editbedsuse.getText().toString())>total_beds)
                {
                    editbedsuse.setError("Not a valid number");
                    editbedsuse.requestFocus();
                    editbedsavail.setText("");
                    return;
                }
                editbedsavail.setText(Integer.toString(total_beds-Integer.parseInt(editbedsuse.getText().toString())));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        Cursor rows = mydb.getExistingICU(ID);
        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                editbedsuse.setText(rows.getString(1));
                editbedsavail.setText(rows.getString(2));
                editventuse.setText(rows.getString(3));
                editventavail.setText(rows.getString(4));
                editventrepair.setText(rows.getString(5));
                editipno.setText(rows.getString(6));

                chkdoc.setChecked(strTOboolean(flagsrow.getString(3)));
                chkcln.setChecked(strTOboolean(flagsrow.getString(4)));
                chkhk.setChecked(strTOboolean(flagsrow.getString(5)));
                editcomm.setText(flagsrow.getString(12));
                editiss.setText(flagsrow.getString(6));
                btnnext.setText("UPDATE");


                Cursor stable_rows = mydb.getDataSTABLE_DETAILS(table_id, cdate);
                if (stable_rows.getCount() > 0) {
                    stable_rows.moveToFirst();
                    do {

                        LayoutInflater layoutInflaterStable =
                                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addViewDisp = layoutInflaterStable.inflate(R.layout.row, null);
                        final TextView textOut1 = (TextView) addViewDisp.findViewById(R.id.textout1);
                        final TextView textOut2 = (TextView) addViewDisp.findViewById(R.id.textout2);
                        Button buttonEdit = (Button) addViewDisp.findViewById(R.id.edit);
                        tvIP = (TextView) findViewById(R.id.sampleLabel1);
                        tvDetails = (TextView) findViewById(R.id.sampleLabel2);
                        tvIP.setText("IP No");
                        tvDetails.setText("Patient Details");
                        tvIP.setVisibility(View.VISIBLE);
                        tvDetails.setVisibility(View.VISIBLE);
                        textOut1.setText(stable_rows.getString(2));
                        textOut2.setText(stable_rows.getString(3));
                        textOut2.setEnabled(false);
                        textOut1.setEnabled(false);
                        container.addView(addViewDisp);
                        buttonEdit.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                textIn1.setEnabled(false);
                                buttonAdd.setText("UPDATE");
                                buttonDelete.setVisibility(View.VISIBLE);
                                if (!textIn1.getText().toString().isEmpty() || !textIn2.getText().toString().isEmpty()) {
                                    Toast.makeText(Casul_icu.this, "Update above fields and edit", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                count -= 1;
                                textIn1.setText(textOut1.getText().toString());
                                textIn2.setText(textOut2.getText().toString());
                                ((LinearLayout) addViewDisp.getParent()).removeView(addViewDisp);
                                if (count <= 0) {
                                    tvIP.setVisibility(View.GONE);
                                    tvDetails.setVisibility(View.GONE);
                                }
                            }
                        });

                        buttonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean ins = mydb.delete_stable(table_id,
                                        cdate,
                                        textIn1.getText().toString());
                                if (ins == true) {
                                    Toast.makeText(Casul_icu.this, "STABLE PATIENT DATA DELETED", Toast.LENGTH_LONG).show();
                                    buttonAdd.setText("ADD");
                                    textIn1.setEnabled(true);
                                    buttonDelete.setVisibility(View.INVISIBLE);
                                    textIn1.setText("");
                                    textIn2.setText("");
                                } else {
                                    Toast.makeText(Casul_icu.this, "DELETING FAILED", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    } while (stable_rows.moveToNext());
                /*activeEdit();*/

                }
            }

        }

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {

                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                final TextView textOut1 = (TextView) addView.findViewById(R.id.textout1);
                final TextView textOut2 = (TextView) addView.findViewById(R.id.textout2);
                tvIP = (TextView) findViewById(R.id.sampleLabel1);
                tvDetails = (TextView) findViewById(R.id.sampleLabel2);
                tvIP.setText("IP No");
                tvDetails.setText("Patient Details");
                textOut1.setText(textIn1.getText().toString());
                textOut2.setText(textIn2.getText().toString());
                Button buttonEdit = (Button) addView.findViewById(R.id.edit);

                if (textIn1.getText().toString().isEmpty()) {
                    textIn1.setError("Invalid IP Number");
                    textIn1.requestFocus();
                    return;
                }
                if (textIn2.getText().toString().isEmpty()) {
                    textIn2.setError("Invalid Patient Details");
                    textIn2.requestFocus();
                    return;
                }

                count += 1;



                buttonEdit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        textIn1.setEnabled(false);

                        buttonAdd.setText("UPDATE");
                        buttonDelete.setVisibility(View.VISIBLE);
                        if(!textIn1.getText().toString().isEmpty()||!textIn2.getText().toString().isEmpty())
                        {
                            Toast.makeText(Casul_icu.this, "Update above fields and edit", Toast.LENGTH_LONG).show();
                            return;
                        }
                        count -= 1;
                        textIn1.setText(textOut1.getText().toString());
                        textIn2.setText(textOut2.getText().toString());
                        ((LinearLayout) addView.getParent()).removeView(addView);
                        if (count <= 0) {
                            tvIP.setVisibility(View.GONE);
                            tvDetails.setVisibility(View.GONE);
                        }
                    }
                });


                buttonDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        boolean ins = mydb.delete_stable(table_id,
                                cdate,
                                textIn1.getText().toString());
                        if (ins == true)
                        {
                            Toast.makeText(Casul_icu.this, "DELETED", Toast.LENGTH_SHORT).show();
                            buttonAdd.setText("ADD");
                            textIn1.setEnabled(true);
                            buttonDelete.setVisibility(View.INVISIBLE);
                            textIn1.setText("");
                            textIn2.setText("");
                        }
                        else
                        {
                            Toast.makeText(Casul_icu.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (count >= 1) {
                    tvIP.setVisibility(View.VISIBLE);
                    tvDetails.setVisibility(View.VISIBLE);
                }
                container.addView(addView);



                if(buttonAdd.getText().equals("UPDATE"))
                {
                    boolean ins = mydb.update_stable(table_id,
                            cdate,
                            textIn1.getText().toString(),
                            textIn2.getText().toString());
                    if (ins == true)
                    {
                        Toast.makeText(Casul_icu.this, "UPDATED", Toast.LENGTH_LONG).show();
                        buttonAdd.setText("ADD");
                        textIn1.setEnabled(true);
                        textOut2.setEnabled(false);
                        textOut1.setEnabled(false);
                        buttonDelete.setVisibility(View.INVISIBLE);
                        tvIP.setVisibility(View.VISIBLE);
                        tvDetails.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(Casul_icu.this, "UPDATING FAILED", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    boolean ins = mydb.insert_stable(table_id,
                            cdate,
                            textIn1.getText().toString(),
                            textIn2.getText().toString());
                    if (ins)
                    {
                        Toast.makeText(Casul_icu.this, "SAVED", Toast.LENGTH_LONG).show();
                        textOut2.setEnabled(false);
                        textOut1.setEnabled(false);
                    }
                    else
                    {
                        Toast.makeText(Casul_icu.this, "FAILED", Toast.LENGTH_LONG).show();
                    }
                }
                textIn1.setText("");
                textIn2.setText("");

            }
        });

        nextclick();

    }

    public boolean strTOboolean(String str)
    {
        if(str.equals("0"))
            return  false;
        else
            return true;
    }

    public void nextclick()
    {
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editbedsuse.getText().toString().isEmpty()) {
                    editbedsuse.setError("Enter Value");
                    editbedsuse.requestFocus();
                    return;
                }
                if (Integer.parseInt(editbedsuse.getText().toString())>total_beds) {
                    editbedsuse.setError("Invalid Input");
                    editbedsuse.requestFocus();
                    return;
                }
                if (editventuse.getText().toString().isEmpty()) {
                    editventuse.setError("Enter Value");
                    editventuse.requestFocus();
                    return;
                }

                if (editventavail.getText().toString().isEmpty()) {
                    editventavail.setError("Enter Value");
                    editventavail.requestFocus();
                    return;
                }

                if (editventrepair.getText().toString().isEmpty()) {
                    editventrepair.setError("Enter Value");
                    editventrepair.requestFocus();
                    return;
                }
                if (editipno.getText().toString().isEmpty()) {
                    editipno.setError("Enter Value");
                    editipno.requestFocus();
                    return;
                }


                if (btnnext.getText().equals("SAVE")) {
                    //icu_id and date are declared as final variables
                    boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);
                    if (res) {

                        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                        boolean res1 = mydb.insert_icu(ID,
                                editbedsuse.getText().toString(),
                                editbedsavail.getText().toString(),
                                editventuse.getText().toString(),
                                editventavail.getText().toString(),
                                editventrepair.getText().toString(),
                                editipno.getText().toString());
                        boolean res2 = mydb.insert_FLAGS(ID, null, null, chkdoc.isChecked(),  chkcln.isChecked(), chkhk.isChecked(), editiss.getText().toString().trim(),
                                timeStamp.toString(), false, false, false, false, editcomm.getText().toString().trim());


                        if (res1 == res2)
                            showMessage(true, "Message", "Saved Successfully");
                        else
                            showMessage(false, "ERROR", "Record Not Saved ");
                    }
                }
                else
                {
                    boolean res1 = mydb.update_icu(ID,
                            editbedsuse.getText().toString(),
                            editbedsavail.getText().toString(),
                            editventuse.getText().toString(),
                            editventavail.getText().toString(),
                            editventrepair.getText().toString(),
                            editipno.getText().toString());
                    boolean res2 = mydb.updateFLAGS(ID, null, null, chkdoc.isChecked(),  chkcln.isChecked(), chkhk.isChecked(), editiss.getText().toString().trim(),
                            timeStamp.toString(), false, false, false, false, editcomm.getText().toString().trim());



                    if (res1==res2)
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Casul_icu.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Casul_icu.this, Rounds.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Casul_icu.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    public void onClick(View v) {
        if (v.getId() == R.id.btn_scan1 || v.getId() == R.id.btn_scan2) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            scan_view = v;
            viewid = v.getId();
            Toast toast = Toast.makeText(getApplicationContext(), "" + viewid, Toast.LENGTH_LONG);
            toast.show();

            Toast toast2 = Toast.makeText(getApplicationContext(), "" + viewid, Toast.LENGTH_LONG);

            toast2.show();


        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            //    String scanFormat = scanningResult.getFormatName();
            //   formatTxt.setText("FORMAT: " + scanFormat);
           /* if (viewid == 2131558557)
            {
                textIn1.setText(scanContent);

            }
            else if (viewid == 2131558540)
            {
                editipno.setText(scanContent);
            }
            else
            {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }*/


            if(scan_view.getId() == R.id.btn_scan1)
                textIn1.setText(scanContent);

            if( scan_view.getId() == R.id.btn_scan2)
                editipno.setText(scanContent);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
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
                .setMessage("Exit Casualty ICU?")
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