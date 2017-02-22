package com.kmcmanipal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by NAGS on 6/10/2016.
 */
public class SignupActivity extends AppCompatActivity
{
     EditText editUsername;
    int flag=0;
    //DatabaseHelper helper = new DatabaseHelper(this);
    TableHelper myHelper;
    Cursor cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myHelper = new TableHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editUsername=(EditText)findViewById(R.id.EUname);
    }
    public void onSignUpClick(View v) {

        if (v.getId() == R.id.Bsiup) {
            EditText fname = (EditText) findViewById(R.id.Efirstname);
            EditText lname = (EditText) findViewById(R.id.Elastname);
            EditText email = (EditText) findViewById(R.id.Email);
            EditText uname = (EditText) findViewById(R.id.EUname);
            EditText desi = (EditText) findViewById(R.id.Edes);
            EditText affil = (EditText) findViewById(R.id.affiliation);
            EditText pass1 = (EditText) findViewById(R.id.Epass);
            EditText pass2 = (EditText) findViewById(R.id.Epass1);

            String fnamestr = fname.getText().toString().trim();
            String lnamestr = lname.getText().toString().trim();
            String emailstr = email.getText().toString().trim();
            String unamestr = uname.getText().toString().trim();
            String desistr = desi.getText().toString().trim();
            String affilstr= affil.getText().toString().trim();
            String pass1str = pass1.getText().toString().trim();
            String pass2str = pass2.getText().toString().trim();

            cur=myHelper.getalluser();

            final String emailcheck = email.getText().toString();
            final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if(fnamestr.equals("")||lnamestr.equals("")||emailstr.equals("")||affilstr.equals("")||unamestr.equals("")||desistr.equals("")||pass1str.equals("")||pass2str.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Field Vaccant", Toast.LENGTH_LONG).show();
                return;
            }
            if (cur.getCount() > 0)
            {
                cur.moveToFirst();
                do
                {
                    if(unamestr.equals(cur.getString(0))) {
                        uname.setError("User Name already exits");
                        uname.requestFocus();
                        return;
                    }

                }while(cur.moveToNext());
            }
            if (!pass1str.equals(pass2str))
            {
                //popup msg
                Toast pass = Toast.makeText(SignupActivity.this, "Passwords doesn't match!", Toast.LENGTH_SHORT);
                pass2.requestFocus();
                pass2.setText("");
                pass.show();
                return;
            }
            /*if(uname.equals(cur))
            {
                Toast new_user = Toast.makeText(SignupActivity.this, "User already exist!", Toast.LENGTH_SHORT);
                new_user.show();
                uname.requestFocus();
                uname.setText("");
                return;
            }*/
            else
            {


                if (!(email.getText().toString().matches(emailPattern))) {
                    // Toast.makeText(getApplicationContext(),emailValid.getText().toString(), Toast.LENGTH_SHORT).show();
                   // Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                    return;
                }
                myHelper.insertUSER_DETAILS("",fnamestr,lnamestr,desistr,emailstr,unamestr,pass1str,true,affilstr);

                new AlertDialog.Builder(SignupActivity.this)

                        .setMessage("New User Account Created")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(SignupActivity.this, admin1.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        })
                        .show();
            }

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
                .setMessage("Exit Signup?")
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
