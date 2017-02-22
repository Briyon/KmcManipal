package com.kmcmanipal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Login extends AppCompatActivity {
    Button Blogin, Btnup, BNTTABLE;
    TableHelper myHelper;
    //DatabaseHelper helper = new DatabaseHelper(this);
    // DataBaseTime db= new DataBaseTime(this);
    Cursor c;
    public String currentdate;

    EditText a;
    EditText b;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        checkDevicePermission();
        myHelper = new TableHelper(this);
        if (myHelper.isLoggedIn()) {
            Intent i = new Intent(Login.this, DisplayActivity.class);
            startActivity(i);
            finish();
        }

         a = (EditText) findViewById(R.id.user);
         b = (EditText) findViewById(R.id.pass);

        a.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                a.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        b.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setError(null);

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void onButtonClick(View v) {
        if (v.getId() == R.id.blog) {

            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String str = a.getText().toString().trim();
            String passs = b.getText().toString().trim();
            currentdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            if(str.isEmpty())
            {
                a.setError("Enter Username");
                a.requestFocus();
                return;
            }
            if(passs.isEmpty())
            {
                b.setError("Enter Password");
                b.requestFocus();
                return;
            }

            if (isInternetOn()) {
                serverLogin(str, passs, replaceSpace(currentdate));


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            //Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            return true;
        }
        /*else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  )
        {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }*/
        return false;

    }


    private void serverLogin(final String username, final String password, String logDate) {
        String urlSuffix = "?username=" + username + "&password=" + password + "&logdate=" + logDate;
        class RegisterUser extends AsyncTask<String, Void, String> {


            private static final String REGISTER_URL = "http://teaminnovators.esy.es/app_files/applogin.php";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Please Wait..", "Logging in", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
               // loading.dismiss();
                try {
                    if (s.contains("success")) {
                        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        getJSON(username, password);

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


    private void getJSON(final String username,final String password) {
       final  String JSON_URL = "http://teaminnovators.esy.es/app_files/getLoginDetails.php";
        String urlSuffix = "?uname=" + username;
        class GetJSON extends AsyncTask<String, Void, String> {
           // ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // loading = ProgressDialog.show(Login.this, "Please Wait...","Getting user details",false, false);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(JSON_URL + uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                // textViewJSON.setText(s);

                try {
                    JSONArray user = null;
                    JSONObject jsonObj = new JSONObject(s);
                    user = jsonObj.getJSONArray("Result");

                   // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                    for (int i = 0; i < user.length(); i++)
                    {
                        JSONObject c = user.getJSONObject(i);
                        String user_id = c.getString("user_id");
                        String fname = c.getString("fname");
                        String lname = c.getString("lname");
                        String designation = c.getString("designation");
                        String email = c.getString("email");
                        String uname = c.getString("uname");
                        String password = c.getString("password");
                        String active = c.getString("active");
                        String affiliation = c.getString("affiliation");


                        boolean act,res;
                        if(active.equals("1"))
                            act = true;
                        else
                            act= false;
                        myHelper = new TableHelper(Login.this);
                        Cursor cur = myHelper.db.rawQuery("select *  from user_details where id = '"+user_id+"'",null);
                        if(cur.getCount() > 0)
                        {
                            res = myHelper.updateUSER_DETAILS(user_id,fname,lname,designation,email,uname,password,act,affiliation);
                           /* if(res)
                                Toast.makeText(getApplicationContext(), "user details updated", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "user details update failed", Toast.LENGTH_LONG).show();*/
                        }
                        else
                        {
                            res = myHelper.insertUSER_DETAILS(user_id,fname,lname,designation,email,uname,password,act,affiliation);
                           /* if(res)
                                Toast.makeText(getApplicationContext(), "user details saved", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "user details save failed", Toast.LENGTH_LONG).show();*/
                        }
                        cur.close();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                catch (NullPointerException e)
                {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                }


                locallogin(username, password);
                loading.dismiss();
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(urlSuffix);
    }




    private void locallogin(String str, String passs) {
           /*if(!myHelper.isValidUser(str))
            {
                a.setError("Inavlid User Name");
                b.setText("");
                a.requestFocus();
                return;
            }*/

        String password = myHelper.searchPASSWORD(str);
        if (passs.equals(password)) {
            //currentdate = DateFormat.getDateTimeInstance().format(new Date());


               /* TextView ed1 = (TextView) findViewById(R.id.textView16);
                ed1.setText(currentdate);*/


            EditText q = (EditText) findViewById(R.id.user);
            String un = q.getText().toString();
            // String m = ed1.getText().toString();


            //logout field


            boolean res = myHelper.insertLOG(un, currentdate);

            if (res == true) {
                new AlertDialog.Builder(Login.this)
                        .setMessage("Logged In Successfully")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Login.this, DisplayActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }

                        })
                        .show();
            } else {
                return;
            }
        } else if (str.equals("admin") && passs.equals("admin")) {
            new AlertDialog.Builder(Login.this)
                    .setMessage(" Admin Logged In Successfully")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Login.this, admin1.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }

                    })

                    .show();
        } else {
            EditText b = (EditText) findViewById(R.id.user);
            b.setError("Invalid Username");
            b.requestFocus();
           // b.setText("");
            return;
        }
    }


    public void checkDevicePermission()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        android.util.Log.i("LOG_TAG", "Message" + permissionCheck);
        if (permissionCheck < 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //pdfFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/MOD_Reports/" + username);
            }
            else {
                ActivityCompat.requestPermissions(Login.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
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
