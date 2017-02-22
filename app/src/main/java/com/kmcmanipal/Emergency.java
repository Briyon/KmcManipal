package com.kmcmanipal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sony on 6/13/2016.
 */
public class Emergency  extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    TableHelper mydb;
    Button btnNext;
    EditText editDrug,txtStock,txtIssues,txtMlcTot,txtMlcSent,txtTotMlcPending;
    CheckBox chkDocument,chkHK,chkDocSurgical,chkDocMedicen,chkClean;
    Spinner spinner1;

    private static final String table_id = "T2";

    private static final String table_name ="Emergency";
    String cdate,timeStamp;

    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency1);
        mydb = new TableHelper(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdate = mydb.getPendingReportDate();
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        btnNext = (Button)findViewById(R.id.buttonTraumaNext);

        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);

        spinner1 = (Spinner) findViewById(R.id.spinner);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Inj.Adrenaline");
        categories.add("Inj.Atropine");
        categories.add("Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner1.setAdapter(dataAdapter);


        editDrug = (EditText) findViewById(R.id.editText_drug);
        txtStock = (EditText) findViewById(R.id.editTextStock);
        chkDocument = (CheckBox) findViewById(R.id.checkBoxDocument);
        chkHK = (CheckBox) findViewById(R.id.checkBoxHK);
        txtMlcTot = (EditText) findViewById(R.id.editTextTotMlc);
        txtMlcSent = (EditText) findViewById(R.id.editTextTotMlcSent);
        txtTotMlcPending=(EditText)findViewById(R.id.editTextMlcPending);

        /*chkPloice = (CheckBox) findViewById(R.id.checkBoxPolice);*/
        chkDocSurgical= (CheckBox) findViewById(R.id.checkBoxDocSurgical);
        chkDocMedicen = (CheckBox) findViewById(R.id.checkBoxDocMedical);
        chkClean= (CheckBox) findViewById(R.id.checkBoxClean);
        txtIssues = (EditText) findViewById(R.id.editTextIssue);



        Cursor rows = mydb.getExistingTR_ET(ID);
        if (rows.getCount() > 0) {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if (flagsrow.getCount() > 0) {
                flagsrow.moveToFirst();
                editDrug.setText(flagsrow.getString(1));
                String[] str_spinner = new String[3];
                str_spinner[0] = "Inj.Adrenaline";
                str_spinner[1] = "Inj.Atropine";
                str_spinner[2] = "Others";
                for (int i = 0; i < str_spinner.length; i++) {
                /*Toast.makeText(Trauma.this, "saved item:"+rows.getString(2), Toast.LENGTH_LONG).show();
                Toast.makeText(Trauma.this, "one", Toast.LENGTH_LONG).show();*/

                    if (str_spinner[i].equalsIgnoreCase(flagsrow.getString(1))) {
                        spinner1.setSelection(i, true);
                        break;
                    }
                    if (i == 2) {
                        spinner1.setSelection(i, true);
                        editDrug.setVisibility(View.VISIBLE);

                        ViewGroup.LayoutParams params = spinner1.getLayoutParams();
                        params.width = 300;
                        spinner1.setLayoutParams(params);
                    }
                }
                txtStock.setText(flagsrow.getString(2));
                chkDocument.setChecked(strTOboolean(flagsrow.getString(3)));
                chkClean.setChecked(strTOboolean(flagsrow.getString(4)));
                chkHK.setChecked(strTOboolean(flagsrow.getString(5)));
                txtIssues.setText(flagsrow.getString(6));

                /*chkPloice.setChecked(strTOboolean(flagsrow.getString(4)));*/
                txtMlcTot.setText(rows.getString(1));
                txtMlcSent.setText(rows.getString(2));
                txtTotMlcPending.setText(Integer.toString(Integer.parseInt(rows.getString(1)) - Integer.parseInt(rows.getString(2))));
                chkDocSurgical.setChecked(strTOboolean(rows.getString(4)));
                chkDocMedicen.setChecked(strTOboolean(rows.getString(5)));


                btnNext.setText("UPDATE");
            }
        }

        AddData();
       txtMlcTot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


               //txtMlcSent.setText(Integer.toString(0));
                if(!(txtMlcTot.getText().toString().isEmpty() && txtMlcSent.getText().toString().isEmpty())) {
                    if(!txtMlcSent.getText().toString().isEmpty())
                    {
                        if(!txtMlcTot.getText().toString().isEmpty())
                        {
                            txtTotMlcPending.setText(Integer.toString(Integer.parseInt(txtMlcTot.getText().toString()) - Integer.parseInt(txtMlcSent.getText().toString())));
                        }
                    }


                    //Toast.makeText(Emergency.this, "on-text change", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtMlcSent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txtMlcTot.getText().toString().isEmpty()) {

                    txtMlcTot.setError("Enter Total MLC Cases");
                    //txtMlcSent.setText("");
                    txtMlcTot.requestFocus();

                    //Toast.makeText(Emergency.this, "before text change", Toast.LENGTH_SHORT).show();
                    return;
                }

               else if (txtMlcSent.getText().toString().isEmpty()) {
                    //Toast.makeText(Emergency.this, "on text change", Toast.LENGTH_SHORT).show();
                    txtMlcSent.setError("Enter Value");
                    txtMlcSent.requestFocus();
                    txtTotMlcPending.setText("");

                    return;
                }

                if(!(txtMlcTot.getText().toString().isEmpty() && txtMlcSent.getText().toString().isEmpty())) {
                    if(Integer.parseInt(txtMlcSent.getText().toString()) <= Integer.parseInt(txtMlcTot.getText().toString())) {
                        txtTotMlcPending.setText(Integer.toString(Integer.parseInt(txtMlcTot.getText().toString()) - Integer.parseInt(txtMlcSent.getText().toString())));
                    }
                    else {
                        Toast.makeText(Emergency.this, "Police intimation sent value should be less than total value", Toast.LENGTH_LONG).show();
                        txtTotMlcPending.setText("");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtMlcTot.getText().toString().isEmpty()) {
                    //txtMlcSent.setText("");
                }
            }
        });



        spinner1.setOnItemSelectedListener(this);
        //spinner
        //  editDrug=(EditText)findViewById(R.id.editText_drug);


      /*  // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Inj.Adrenaline");
        categories.add("Inj.Atropine");
        categories.add("Others");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);*/
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("Others"))
        {
            editDrug.setText("");
            editDrug.setVisibility(View.VISIBLE);
            editDrug.requestFocus();
            ViewGroup.LayoutParams params= spinner1.getLayoutParams();
            params.width=300;
            spinner1.setLayoutParams(params);
        }
        else {

            editDrug.setVisibility(View.INVISIBLE);
            editDrug.setText(item);
            ViewGroup.LayoutParams params= spinner1.getLayoutParams();
            params.width=600;
            spinner1.setLayoutParams(params);
        }

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public boolean strTOboolean(String str)
    {
        if(str.equals("0"))
            return  false;
        else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    public void AddData()
    {
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editDrug.getText().toString().isEmpty())
                        {
                            editDrug.setError("Enter Drug Name");
                            editDrug.requestFocus();
                            return;
                        }
                        if (txtStock.getText().toString().isEmpty())
                        {
                            txtStock.setError("Enter Value");
                            txtStock.requestFocus();
                            return;
                        }
                        if (txtMlcTot.getText().toString().isEmpty())
                        {
                            txtMlcTot.setError("Enter Value");
                            txtMlcTot.requestFocus();
                            return;
                        }
                        if (txtMlcSent.getText().toString().isEmpty())
                        {
                            txtMlcSent.setError("Enter Value");
                            txtMlcSent.requestFocus();
                            return;
                        }
                        if(txtTotMlcPending.getText().toString().isEmpty()){
                            txtMlcSent.setError("Enter Valid Input");
                            txtMlcSent.requestFocus();
                            return;
                        }

                        if (btnNext.getText().equals("SAVE")) {
                            boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);
                            //Toast.makeText(Emergency.this, mydb.getUserID(), Toast.LENGTH_LONG).show();
                            if (res) {
                                ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                                boolean res1 = mydb.insertTR_ER(ID,txtMlcTot.getText().toString(),txtMlcSent.getText().toString(), false, chkDocSurgical.isChecked(),chkDocMedicen.isChecked());

                                boolean res2 = mydb.insert_FLAGS(ID, editDrug.getText().toString(), txtStock.getText().toString(), chkDocument.isChecked(),

                                        chkClean.isChecked(), chkHK.isChecked(), txtIssues.getText().toString().trim(),
                                        timeStamp.toString(), false, false, false, false, null);


                                if (res1 == res2)
                                    showMessage(res, "Message", "Saved Successfully");
                                else
                                    showMessage(false, "ERROR", "Record Not Saved ");

                            }
                        }
                        else
                        {
                            boolean res1 = mydb.updateTR_ER(ID,txtMlcTot.getText().toString(),txtMlcSent.getText().toString(),false, chkDocSurgical.isChecked(),chkDocMedicen.isChecked());

                            boolean res2 = mydb.updateFLAGS(ID, editDrug.getText().toString(), txtStock.getText().toString(), chkDocument.isChecked(),
                                    chkClean.isChecked(), chkHK.isChecked(), txtIssues.getText().toString().trim(),
                                    timeStamp.toString(), false, false, false, false, null);

                            if (res1 == res2)
                                showMessage(res1, "Message", "Updated Successfully");
                            else
                                showMessage(false, "ERROR", "Updating Failed ");
                        }

                    }
                });
    }


    public void showMessage(boolean result,String title,String message)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Emergency.this);
        builder.setTitle(title);
        builder.setMessage("\n" + message + "\n").setCancelable(false);

        if (result)
        {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    mydb.close();
                    Intent i = new Intent(Emergency.this, Rounds.class);
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
                .setMessage("Exit Emergency Triage?")
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
