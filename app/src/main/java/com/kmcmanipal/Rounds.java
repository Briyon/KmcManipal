package com.kmcmanipal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rounds extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static Context cntxOfParent;


    TableHelper myHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rounds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myHelper = new TableHelper(this);

        Toast.makeText(Rounds.this, "Swipe Right For Menu ", Toast.LENGTH_SHORT).show();

        View view_keyboard = this.getCurrentFocus();
        if (view_keyboard != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view_keyboard.getWindowToken(), 0);
        }

        cntxOfParent = Rounds.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtUser = (TextView) v.findViewById(R.id.labelUsername);
        txtUser.setText(myHelper.getUsername().toUpperCase());


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            //for dialog box before closing application
          /*  AlertDialog.Builder builder = new AlertDialog.Builder(Rounds.this);
            builder.setMessage("Exit Application");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

            */

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rounds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_changepassword) {
            Intent j = new Intent(Rounds.this, CHANGE_PASSWORD.class);
            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
            return true;
        }

        if (id == R.id.action_logout) {

            if(isInternetOn())
            {
                String currentdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                serverLogout(myHelper.getUserID(), replaceSpace(currentdate));
                return true;
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Rounds.this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trauma) {
            Intent i = new Intent(Rounds.this, Trauma.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            //finish();
            // Handle the camera action
        } else if (id == R.id.nav_emergency)
        {
            Intent j = new Intent(Rounds.this, Emergency.class);
            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(j);
            // finish();


        } else if (id == R.id.nav_casualty) {
            Intent g = new Intent(Rounds.this, Casul_icu.class);
            g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(g);
            //finish();

        } else if (id == R.id.nav_icu1) {
            Intent g = new Intent(Rounds.this, Icu1.class);
            g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(g);
            //finish();

        } else if (id == R.id.nav_icu2) {
            Intent g = new Intent(Rounds.this, Icu2.class);
            g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(g);
            //finish();

        } else if (id == R.id.nav_icu3) {
            Intent g = new Intent(Rounds.this, Icu3.class);
            g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(g);
            //finish();
        } else if (id == R.id.nav_icu4) {
            Intent g = new Intent(Rounds.this, Icu4.class);
            g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(g);
            //finish();

        } else if (id == R.id.nav_wardGeneral) {
            Intent k = new Intent(Rounds.this, general_ward_main.class);
            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);
            // finish();

        } else if (id == R.id.nav_wardSpecial) {
            Intent k = new Intent(Rounds.this, special_wrad_main.class);
            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);
            // finish();

        } else if (id == R.id.nav_other) {
            Intent k = new Intent(Rounds.this, others.class);
            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);

        }else if (id == R.id.nav_view) {
            Intent i = new Intent(Rounds.this, ViewReport.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.nav_CLINIC) {
            Intent i = new Intent(Rounds.this, Clinical_lab.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.nav_BLOOD) {
            Intent i = new Intent(Rounds.this, Blood_blank.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
               else if (id == R.id.nav_RADIO) {
            Intent i = new Intent(Rounds.this, Radiology.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.nav_DIALY) {
            Intent i = new Intent(Rounds.this, Dialysis.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.nav_CSSD) {
            Intent i = new Intent(Rounds.this, CSSD.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.nav_pharm) {
            Intent i = new Intent(Rounds.this, PharmacyActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }





        /*else if (id== R.id.nav_changepass) {
            Intent i = new Intent(Rounds.this, CHANGE_PASSWORD.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        else if (id==R.id.nav_logout)
        {
            TableHelper myHelper = new TableHelper(this);

            if(myHelper.logout())
            {
                Intent k = new Intent(Rounds.this, Login.class);
                k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(k);
                finish();
            }

        }*/
        else if (id == R.id.nav_about) {
            Intent i = new Intent(Rounds.this, ABOUT_US.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }/* else if (id == R.id.nav_contact) {
            Intent i = new Intent(Rounds.this, contact_us.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
*/

  /*  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        return false;

    }

    private void serverLogout(String userid,String logOut)
    {
        String urlSuffix ="?userid="+userid+"&logout="+logOut;
        class RegisterUser extends AsyncTask<String, Void, String>
        {

            ProgressDialog loading;
            private static final String REGISTER_URL = "http://teaminnovators.esy.es/app_files/applogout.php";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Rounds.this, "Please Wait..","Logging Out", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                try {
                    if (s.contains("success")) {
                        if (myHelper.logout()) {
                            Intent k = new Intent(Rounds.this, Login.class);
                            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(k);
                            finish();
                        }
                    }
                }  catch (NullPointerException e)
                {
                    Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
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