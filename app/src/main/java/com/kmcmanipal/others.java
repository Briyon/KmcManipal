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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class others extends AppCompatActivity {

    EditText editissue;
    Button btnsave;
    TableHelper mydb;
    private String cdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mydb = new TableHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdate = mydb.getPendingReportDate();

        editissue = (EditText) findViewById(R.id.editTextIssue);
        btnsave = (Button) findViewById(R.id.btn_other_save);

        Cursor rows = mydb.getExistingothers(cdate, mydb.getUsername());

        if (rows.getCount() > 0) {
            rows.moveToFirst();
            if(rows.getString(0)!=null)
            {
                editissue.setText(rows.getString(0));
                btnsave.setText("UPDATE");
            }
        }


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (editissue.getText().toString().isEmpty()) {
                    editissue.setError("Enter any Isuues");
                    editissue.requestFocus();
                    return;
                }

                if (btnsave.getText().equals("SAVE"))
                {



                     mydb.updateOthers(cdate, mydb.getUsername(), editissue.getText().toString().trim());

                    new AlertDialog.Builder(others.this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                            .setMessage("Saved Successfully")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();

                }
                else
                {

                    mydb.updateOthers(cdate,mydb.getUsername(),editissue.getText().toString().trim());

                    new AlertDialog.Builder(others.this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                            .setMessage("Updated Successfully")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }

                btnsave.setText("UPDATE");
            }
        });

    }


    public void showMessage(boolean result, String title, String message) {
        if (result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(others.this);
            builder.setTitle(title);
            builder.setMessage("\n" + message + "\n").setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    // Toast.makeText(Casul_icu.this, "DATA INSERTED", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(others.this, Rounds.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(others.this);
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
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                /*.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")*/
                .setMessage("Exit others?")
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
