package com.kmcmanipal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CHANGE_PASSWORD extends AppCompatActivity
{
    TableHelper mydb;

    EditText editoldpas, editnewpas, editconpas;
    Button btnchange;
    EditText editusnm;

    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);

        editusnm =(EditText)findViewById(R.id.editusername);
        editoldpas = (EditText) findViewById(R.id.editoldpass);
        editnewpas = (EditText) findViewById(R.id.editnewpass);
        editconpas = (EditText) findViewById(R.id.editconfirmpass);
        btnchange = (Button) findViewById(R.id.btnchange);

        String us=mydb.getUsername();
        editusnm.setText(us) ;
        editusnm.setVisibility(View.VISIBLE);

        editusnm.setEnabled(false);

        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

               // View view = arg0;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if (editoldpas.getText().toString().isEmpty())
                {
                    editoldpas.setError("Enter old password");
                    editoldpas.requestFocus();
                    editoldpas.setText("");
                    return;
                }
                else
                {


                    String user_pass = mydb.getpassword(editusnm.getText().toString());

                    if (!user_pass.equals(editoldpas.getText().toString()))
                    {
                        editoldpas.setError("Incorrect Password");
                        editoldpas.requestFocus();
                        editoldpas.setText("");
                        editnewpas.setText("");
                        editconpas.setText("");
                        return;
                    }
                }

                if(editnewpas.getText().toString().trim().contains(" "))
                {
                    Toast pass = Toast.makeText(CHANGE_PASSWORD.this, "Password cannot contain space", Toast.LENGTH_SHORT);
                    pass.show();
                    editnewpas.requestFocus();
                    editnewpas.setText("");
                    return;
                }
                if(editnewpas.getText().toString().trim().length() < 8)
                {
                    Toast.makeText(CHANGE_PASSWORD.this, "Password length should be more than 8 characters", Toast.LENGTH_LONG).show();
                    editnewpas.requestFocus();
                    return;
                }

                if (editnewpas.getText().toString().isEmpty())
                {
                    Toast pass = Toast.makeText(CHANGE_PASSWORD.this, "Re-enter new password", Toast.LENGTH_SHORT);
                    pass.show();
                    editnewpas.requestFocus();
                    editnewpas.setText("");
                    return;
                }
                if (editconpas.getText().toString().isEmpty())
                {
                    Toast pass = Toast.makeText(CHANGE_PASSWORD.this, "Enter confirm Password", Toast.LENGTH_SHORT);
                    pass.show();
                    editconpas.requestFocus();
                    editconpas.setText("");
                    return;
                }
                if(editnewpas.getText().toString().trim().equals(editoldpas.getText().toString().trim()))
                {
                    Toast pass = Toast.makeText(CHANGE_PASSWORD.this, "Old and new password cannot be same", Toast.LENGTH_SHORT);
                    pass.show();
                    editnewpas.requestFocus();
                    editnewpas.setText("");
                    return;

                }
                if(!(editnewpas.getText().toString().trim().equals(editconpas.getText().toString().trim())))
                {
                    Toast pass = Toast.makeText(CHANGE_PASSWORD.this, "Re-enter confirm password", Toast.LENGTH_SHORT);
                    pass.show();
                    editconpas.requestFocus();
                    editconpas.setText("");
                    return;
                }


                serverChangePassword(editusnm.getText().toString().trim(),editnewpas.getText().toString().trim());


            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();



    }


    private void serverChangePassword(final String username, final String newpassword) {
        String urlSuffix = "?uname=" + username + "&password=" + replaceSpace(newpassword);
        class RegisterUser extends AsyncTask<String, Void, String> {


            private static final String REGISTER_URL = "http://teaminnovators.esy.es/app_files/appChangePassword.php";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CHANGE_PASSWORD.this, "Please Wait..", "Updating", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // loading.dismiss();
                try {
                    if (s.contains("updated")) {
                        // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        mydb.updatepass(username, newpassword);
                        new AlertDialog.Builder(CHANGE_PASSWORD.this)
                        /*.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Activity")*/
                                .setMessage("Password Changed")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                        loading.dismiss();

                    } else {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                }
                catch (NullPointerException e)
                {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL + s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result;
                    result = bufferedReader.readLine();

                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(urlSuffix);
    }

    public static String replaceSpace(String str) {
        StringBuffer strBuffer = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '%') {
                    strBuffer.append("~");
                } else if (str.charAt(i) == '+') {
                    strBuffer.append("_p_");
                } else if (str.charAt(i) == '&') {
                    strBuffer.append("_a_");
                } else if (str.charAt(i) == ' ') {
                    strBuffer.append("%20");
                } else if (str.charAt(i) == '#') {
                    strBuffer.append("_ash_");
                } else if (str.charAt(i) == '\n') {
                    strBuffer.append("%0A");
                } else {
                    strBuffer.append(str.charAt(i));
                }
            }
            return strBuffer.toString();
        } else return str;
    }

}

