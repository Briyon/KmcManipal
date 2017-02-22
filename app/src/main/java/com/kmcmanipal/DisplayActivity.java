package com.kmcmanipal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NAGS on 6/10/2016.
 */
public class DisplayActivity extends AppCompatActivity {
    Button btnCreateReport, btnviewreport;
    TableHelper mydb;

    String rdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mydb = new TableHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rdate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        String username = mydb.getUsername();
        String first_name = mydb.getFirstname(username);


        TextView tv = (TextView) findViewById(R.id.textView24);
        tv.setText(first_name);
        btnCreateReport = (Button) findViewById(R.id.btnNewReport);
        btnviewreport = (Button) findViewById(R.id.buttonView);

        if (mydb.isReportPending()) {
/*
            Toast.makeText(DisplayActivity.this, "Report Pending ", Toast.LENGTH_LONG).show();
*/
            Intent i = new Intent(DisplayActivity.this, Rounds.class);
            startActivity(i);
            finish();

        }


        btnCreateReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  String username = mydb.getUsername();
                //  mydb.insertReport(rdate, username, false);
              /*  Intent i = new Intent(DisplayActivity.this,Rounds.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                */


                Cursor res = mydb.getReport(rdate, mydb.getUsername());
                if (res.getCount() > 0) {
                    res.moveToFirst();

                    String status = res.getString(3);

                    if (status.equals("Submitted")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                        builder.setTitle("Report Exist");
                        builder.setMessage("\n" + "Report for Date " + rdate + " has already created and Submitted. \n\nTap on View Reports to View it. \n").setCancelable(false);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                        builder.setTitle("Report Exist");
                        builder.setMessage("\n" + "Report for Date " + rdate + " has already created. Submission is Pending.! \n\nTap on View Reports to Submit it. \n").setCancelable(false);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                } else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                    builder.setTitle("New Report");
                    builder.setMessage("\n" + "Create Report for " + rdate + "\n").setCancelable(false);

                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things

                            if (mydb.insertReport(rdate, mydb.getUserID(), "Pending", null)) {
                                Intent i = new Intent(DisplayActivity.this, Rounds.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                                builder.setTitle("Report Exist");
                                builder.setMessage("\n" + "Report for Date  " + rdate + "  has already created by another User. \n\n").setCancelable(false);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        });

        btnviewreport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayActivity.this, ViewReport.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }

        });

    }


    /*public void onIcu1(View v)
    {
        Intent i = new Intent(DisplayActivity.this,Rounds.class);
        startActivity(i);
        finish();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
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


        if (id == R.id.actn_changePassword) {
            Intent j = new Intent(DisplayActivity.this, CHANGE_PASSWORD.class);
            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
            return true;
        }

        if (id == R.id.actn_Logout) {
            if (isInternetOn()) {
                String currentdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                serverLogout(mydb.getUserID(), replaceSpace(currentdate));
                return true;
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Check your network and Retry");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    /*@Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }*/

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            //Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;

    }

    private void serverLogout(String userid, String logOut) {
        String urlSuffix = "?userid=" + userid + "&logout=" + logOut;
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            private static final String REGISTER_URL = "http://teaminnovators.esy.es/app_files/applogout.php";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DisplayActivity.this, "Please Wait..", "Logging Out", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                try {
                    if (s.contains("success")) {
                        if (mydb.logout()) {
                            Intent k = new Intent(DisplayActivity.this, Login.class);
                            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(k);
                            finish();
                        }
                    }
                }
                catch (NullPointerException e)
                {
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
                if (str.charAt(i) == ' ') {
                    strBuffer.append("%20");
                }else {
                    strBuffer.append(str.charAt(i));
                }
            }
            return strBuffer.toString();
        } else return str;

    }
}


