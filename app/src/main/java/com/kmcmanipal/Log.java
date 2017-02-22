package com.kmcmanipal;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by NAGS on 6/12/2016.
 */
public class Log extends Activity
{
    DataBaseTime mydb;
    Button Btnview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        mydb = new DataBaseTime(this);
        Btnview = (Button)findViewById(R.id.button);

        viewall();

    }
    public void viewall() {

        Btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Cursor res=mydb.getAllData();
                if(res.getCount()== 0){
                    //message
                    showMessage("Error","No Data Found");
                    return;
                }
                StringBuffer buffer =new StringBuffer();
                while(res.moveToNext())
                {
                    buffer.append("USER :"+ res.getString(0)+"\n");
                    buffer.append("TIME :"+ res.getString(1)+"\n-----------------------\n");
                }
                showMessage("USER LOGS",buffer.toString());
            }

        });
    }

    public  void showMessage(String title,String Message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
