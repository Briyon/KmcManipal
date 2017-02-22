package com.kmcmanipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sony on 6/13/2016.
 */
public class Trauma  extends AppCompatActivity  implements AdapterView.OnItemSelectedListener
{
    TableHelper mydb;

    EditText txtStock,txtTotMlc, totMlcSent,txtTotMlcPending,txtIssues,editdrug;
    CheckBox chkDocument,chkHK,chkDoctor,chkClean;
    Spinner spinner;
    Intent intent_refresh;
    private static final String table_id = "T1";

    private static final String table_name ="Trauma";
    // Date date = new Date();
    private String cdate,timeStamp;

    Button btnNext;


    String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trauma1);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);

        ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
        //cdate = mydb.getPendingReportDate(mydb.getUsername());
        timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        /*View view_keyboard = this.getCurrentFocus();
        if (view_keyboard != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view_keyboard.getWindowToken(), 0);
        }*/

        spinner = (Spinner) findViewById(R.id.spinner2);

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
        spinner.setAdapter(dataAdapter);


        editdrug = (EditText) findViewById(R.id.editTextdrug);
        txtStock = (EditText) findViewById(R.id.editTextStock);
        chkDocument = (CheckBox) findViewById(R.id.checkBoxDocument);
        chkHK = (CheckBox) findViewById(R.id.checkBoxHK);
        txtTotMlc = (EditText) findViewById(R.id.editTextTotMlc);
        totMlcSent =(EditText)findViewById(R.id.editTextTotMlcSent);
        txtTotMlcPending=(EditText)findViewById(R.id.editTextMlcPending);
        /*chkPloice = (CheckBox) findViewById(R.id.checkBoxTrumaPolice);*/
        chkDoctor = (CheckBox) findViewById(R.id.checkBoxDoctor);
        chkClean= (CheckBox) findViewById(R.id.checkBoxClean);
        txtIssues = (EditText) findViewById(R.id.editTextIssue);
        btnNext = (Button)findViewById(R.id.buttonTraumaNext);


 intent_refresh=getIntent();
     //   editdrug=(EditText)findViewById(R.id.editTextdrug);


        Cursor rows = mydb.getExistingTR_ET(ID);
        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            Cursor flagsrow = mydb.getExistingFLAGS(ID);
            if(flagsrow.getCount() > 0)
            {
                flagsrow.moveToFirst();
                editdrug.setText(flagsrow.getString(1));
                String[] str_spinner = new String[3];
                str_spinner[0] = "Inj.Adrenaline";
                str_spinner[1] = "Inj.Atropine";
                str_spinner[2] = "Others";
                for (int i = 0; i < str_spinner.length; i++) {
                /*Toast.makeText(Trauma.this, "saved item:"+rows.getString(2), Toast.LENGTH_LONG).show();
                Toast.makeText(Trauma.this, "one", Toast.LENGTH_LONG).show();*/

                    if (str_spinner[i].equalsIgnoreCase(flagsrow.getString(1))) {
                        spinner.setSelection(i, true);
                        break;
                    }
                    if (i == 2) {
                        spinner.setSelection(i, true);
                        editdrug.setVisibility(View.VISIBLE);

                        ViewGroup.LayoutParams params = spinner.getLayoutParams();
                        params.width = 300;
                        spinner.setLayoutParams(params);
                    }
                }
                txtStock.setText(flagsrow.getString(2));
                chkDocument.setChecked(strTOboolean(flagsrow.getString(3)));
                chkClean.setChecked(strTOboolean(flagsrow.getString(4)));
                chkHK.setChecked(strTOboolean(flagsrow.getString(5)));
                txtIssues.setText(flagsrow.getString(6));
                /*chkPloice.setChecked(strTOboolean(flagsrow.getString(4)));*/
                txtTotMlc.setText(rows.getString(1));
                totMlcSent.setText(rows.getString(2));
                txtTotMlcPending.setText(Integer.toString(Integer.parseInt(rows.getString(1))-Integer.parseInt(rows.getString(2))));
                chkDoctor.setChecked(strTOboolean(rows.getString(3)));          // doc is from tr_er so use row cursor





                btnNext.setText("UPDATE");
                //    finish();
                // startActivity(getIntent());
            }
        }

        AddData();

        txtTotMlc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //txtMlcSent.setText(Integer.toString(0));
                if(!(txtTotMlc.getText().toString().isEmpty() && totMlcSent.getText().toString().isEmpty())) {
                    if(!totMlcSent.getText().toString().isEmpty())
                    {
                        if(!txtTotMlc.getText().toString().isEmpty())
                        {
                            txtTotMlcPending.setText(Integer.toString(Integer.parseInt(txtTotMlc.getText().toString()) - Integer.parseInt(totMlcSent.getText().toString())));
                        }
                    }


                    //Toast.makeText(Emergency.this, "on-text change", Toast.LENGTH_SHORT).show();

                }
                else
                    txtTotMlcPending.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        totMlcSent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txtTotMlc.getText().toString().isEmpty()) {

                    txtTotMlc.setError("Enter Total MLC Cases");
                    //txtMlcSent.setText("");
                    txtTotMlc.requestFocus();

                    //Toast.makeText(Emergency.this, "before text change", Toast.LENGTH_SHORT).show();
                    return;
                } else if (totMlcSent.getText().toString().isEmpty()) {
                    //Toast.makeText(Emergency.this, "on text change", Toast.LENGTH_SHORT).show();
                    totMlcSent.setError("Enter Value");
                    totMlcSent.requestFocus();
                    txtTotMlcPending.setText("");

                    return;
                }

                if (!(txtTotMlc.getText().toString().isEmpty() && totMlcSent.getText().toString().isEmpty())) {
                    if (Integer.parseInt(totMlcSent.getText().toString()) <= Integer.parseInt(txtTotMlc.getText().toString())) {
                        txtTotMlcPending.setText(Integer.toString(Integer.parseInt(txtTotMlc.getText().toString()) - Integer.parseInt(totMlcSent.getText().toString())));
                    } else {
                        Toast.makeText(Trauma.this, "Police intimation sent value should be less than total value", Toast.LENGTH_LONG).show();
                        txtTotMlcPending.setText("");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtTotMlc.getText().toString().isEmpty()) {
                    //txtMlcSent.setText("");
                }
            }
        });





        /*    String sp;
        sp="select editdrug  from tr_er"*/
    //    editdrug=(EditText)findViewById(R.id.editip);

//        editdrug.setVisibility(View.GONE);
        // Spinner element

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("Others"))
        {
            editdrug.setText("");
            editdrug.setVisibility(View.VISIBLE);
            editdrug.requestFocus();
            ViewGroup.LayoutParams params= spinner.getLayoutParams();
            params.width=300;
            spinner.setLayoutParams(params);
        }
        else {

            editdrug.setVisibility(View.INVISIBLE);
            editdrug.setText(item);

            ViewGroup.LayoutParams params= spinner.getLayoutParams();
            params.width=600;
            spinner.setLayoutParams(params);
        }

         //Showing selected spinner item
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
       //getMenuInflater().inflate(R.menu.rounds, menu);
        return true;

        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }




    public void AddData() {
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editdrug.getText().toString().isEmpty()) {
                            editdrug.setError("Enter Drug Name");
                            editdrug.requestFocus();
                            return;
                        }
                        if (txtStock.getText().toString().isEmpty()) {
                            txtStock.setError("Enter Value");
                            txtStock.requestFocus();
                            return;
                        }
                        if (txtTotMlc.getText().toString().isEmpty()) {
                            txtTotMlc.setError("Enter Total MLC cases");
                            txtTotMlc.requestFocus();
                            return;
                        }
                        if (totMlcSent.getText().toString().isEmpty()) {
                            totMlcSent.setError("Enter Value");
                            totMlcSent.requestFocus();
                            return;
                        }
                        if(txtTotMlcPending.getText().toString().isEmpty()){
                            totMlcSent.setError("Enter Valid Input");
                            totMlcSent.requestFocus();
                            return;
                        }


                        if (btnNext.getText().equals("SAVE"))
                        {
                            boolean res = mydb.insertRecord(mydb.getPendingReportDate(), table_id);
                          //  Toast.makeText(Trauma.this, mydb.getUserID(), Toast.LENGTH_LONG).show();
                            if (res)
                            {
                                ID = mydb.getRecordID(mydb.getPendingReportDate(), table_id);
                                boolean res1 = mydb.insertTR_ER(ID, txtTotMlc.getText().toString(), totMlcSent.getText().toString(),chkDoctor.isChecked(), false, false);

                                boolean res2 = mydb.insert_FLAGS(ID, editdrug.getText().toString(), txtStock.getText().toString(), chkDocument.isChecked(),

                                        chkClean.isChecked(), chkHK.isChecked(), txtIssues.getText().toString().trim(),
                                        timeStamp.toString(), false, false, false, false, null);

                                if (res1 == res2)
                                    showMessage(true, "Message", "Saved Successfully");
                                else
                                    showMessage(false, "ERROR", "Record Not Saved ");

                            }
                        }
                        else
                            {
                                boolean res1 = mydb.updateTR_ER(ID, txtTotMlc.getText().toString(), totMlcSent.getText().toString(), chkDoctor.isChecked(), false, false);

                                boolean res2 = mydb.updateFLAGS(ID, editdrug.getText().toString(), txtStock.getText().toString(), chkDocument.isChecked(),
                                                chkClean.isChecked(), chkHK.isChecked(), txtIssues.getText().toString().trim(),
                                        timeStamp.toString(), false, false, false, false, null);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(Trauma.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    mydb.close();
                    Intent i = new Intent(Trauma.this, Rounds.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(Trauma.this);
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
                .setMessage("Exit Trauma Triage?")
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
