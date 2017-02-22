package com.kmcmanipal;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.net.ConnectivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//From PDF


public class ViewReport extends AppCompatActivity {
    TableHelper mydb;
    TextView noreports;
    ListView listReports;


    File myFile;
    ProgressDialog waitDialog;
    Activity parentActivity;
    String date;

    //for uploading file to server
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    HttpURLConnection conn = null;
    String upLoadServerUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mydb = new TableHelper(this);
        parentActivity = (Activity) Rounds.cntxOfParent;

        noreports = (TextView) findViewById(R.id.textviewNoReport);
        listReports = (ListView) findViewById(R.id.listViewReports);

        Cursor allReports = mydb.getAllReport(mydb.getUsername());

        if (allReports.getCount() <= 0)
            noreports.setVisibility(View.VISIBLE);
        else
            noreports.setVisibility(View.INVISIBLE);

        String[] from = new String[]{"rdate", "submitted"};
        int[] to = new int[]{R.id.reportDate, R.id.reportStatus};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.listview_item, allReports, from, to, 0);
        listReports.setAdapter(cursorAdapter);

        registerForContextMenu(listReports);





        /*
        String  values[] = new String[10];

        allReports.moveToFirst();
        if  (allReports.moveToFirst())
        {
            int i = 0;
            do {
                String firstName = allReports.getString(0);
                String sub = allReports.getString(2);

                String Submitted;
                if(sub.equals("0"))
                    Submitted = "Pending";
                else
                    Submitted ="Submitted";

                values[i++] = firstName + "       " + Submitted;

            }while (allReports.moveToNext());
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,R.layout.z_spinner_item,values);
        listReports.setAdapter(adp);
        registerForContextMenu(listReports);


*/

        listReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                date = ((TextView) view.findViewById(R.id.reportDate)).getText().toString();
                String status = ((TextView) view.findViewById(R.id.reportStatus)).getText().toString();

               /* Toast toast = Toast.makeText(getApplicationContext(), date + " " + status, Toast.LENGTH_SHORT);
                toast.show();*/

                if (status.equals("Pending"))
                    promptForPending(date);

                if (status.equals("Submitted"))
                    promptForSubmitted(date);


            }
        });

    }


   /* protected boolean onLongListItemClick(View v, int pos, long id) {

        String date = ((TextView) v.findViewById(R.id.reportDate)).getText().toString();
        String status = ((TextView) v.findViewById(R.id.reportStatus)).getText().toString();

        Toast toast = Toast.makeText(getApplicationContext(), date + " " + status, Toast.LENGTH_SHORT);
        toast.show();

        if(status.equals("Pending"))
           promptForPending(date);

        if(status.equals("Submitted"))
            promptForSubmitted(date);
        return true;
    }*/


    private void promptForPending(final String rdate) {
        final String[] options = {getString(R.string.label_edit),getString(R.string.label_discard), getString(R.string.label_perview_report), getString(R.string.label_submit)};         //getString(R.string.label_delete),
        final String reportdate = rdate;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);   //getActivity()
        builder.setTitle("Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_edit))) {
                    Intent i = new Intent(ViewReport.this, Rounds.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
                else if (options[which].equals(getString(R.string.label_discard))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewReport.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Do you want to discard this report..?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /*Toast toast = Toast.makeText(getApplicationContext(), rdate, Toast.LENGTH_SHORT);
                            toast.show();*/
                            //call delete record function
                            //get pending report date and call a method to delete that date's record from records table
                            //do things
                            mydb.deleteLocalRecords();
                            Toast.makeText(ViewReport.this, "Outside Delete:"+mydb.getUserID(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(ViewReport.this, "Funtion return value:"+mydb.deleteReport(rdate,mydb.getUserID()), Toast.LENGTH_LONG).show();
                            if (mydb.deleteReport(rdate,mydb.getUserID())) {
                                Toast.makeText(ViewReport.this, "Inside Delete", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ViewReport.this, DisplayActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                parentActivity.finish();
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

                else if (options[which].equals(getString(R.string.label_perview_report))) {

                    dialog.dismiss();
                    if (checkDevicePermission()) {
                        waitDialog = ProgressDialog.show(ViewReport.this, "Creating PDF", "Please wait...", true);

                        new Thread(new Runnable()     //thread to display waitDialog box
                        {
                            @Override
                            public void run() {
                                // do the thing that takes a long time

                                try {
                                    createPdf(rdate, 1);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitDialog.dismiss();
                                        //Toast.makeText(ViewReport.this, "back to ui", Toast.LENGTH_LONG).show();
//                                    viewPdf();
                                    }
                                });
                            }
                        }).start();
                    } else
                        Toast.makeText(ViewReport.this, "Allow Storage Permission to continue", Toast.LENGTH_LONG).show();

                } else if (options[which].equals(getString(R.string.label_submit))) {
                    //dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewReport.this);
                    builder.setTitle("Confirm");
                    builder.setMessage("Do you want to Submit..?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things

                            if (isInternetOn()) {
                                waitDialog = ProgressDialog.show(ViewReport.this, "Preparing to Submit", "Please wait...", true);
                                new Thread(new Runnable()     //thread to display waitDialog box
                                {
                                    @Override
                                    public void run() {
                                        // do the thing that takes a long time
                                        try {
                                            createPdf(rdate, 2);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (DocumentException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // waitDialog.dismiss();
                                                serverReportCheck(rdate);
                                                //  fileuploadThread();


                                                //call after file upload
                                               /* String status = "Submitted";
                                               if (mydb.updateReport(rdate, mydb.getUserID(), status, null)) {
                                                    //Toast.makeText(ViewReport.this, "Report Submitted", Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(ViewReport.this, DisplayActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(i);
                                                    finish();
                                                    parentActivity.finish();

                                                    emailNote(rdate);
                                                } else {
                                                    Toast.makeText(ViewReport.this, "Report ERROR", Toast.LENGTH_LONG).show();
                                                }*/

                                            }
                                        });
                                    }
                                }).start();
                            } //dialog.dismiss(); //to cancle
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewReport.this);
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
        builder.show();

    }


    private void promptForSubmitted(final String ReportDate) {
        final String[] options = {getString(R.string.label_view),getString(R.string.label_share)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);   //getActivity()
        builder.setTitle("Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_view))) {
                    // emailNote();
                    String username = mydb.getUsername();
                    // File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MOD_Reports/" + username);
                    File pdfFolder = new File(Environment.getExternalStorageDirectory() + "/MOD_Reports/" + username);
                    myFile = new File(pdfFolder, "MOD_Reports" + ReportDate + ".pdf");

                    if (myFile.exists())
                        viewPdf();
                    else
                        Toast.makeText(ViewReport.this, "Report Copy Do no Exist", Toast.LENGTH_LONG).show();

                }
                else if(options[which].equals(getString(R.string.label_share))){
                    String username = mydb.getUsername();
                    File pdfFolder = new File(Environment.getExternalStorageDirectory() + "/MOD_Reports/" + username);
                    myFile = new File(pdfFolder, "MOD_Reports" + ReportDate + ".pdf");

                    if (myFile.exists())
                        emailNote(ReportDate);
                    else
                        Toast.makeText(ViewReport.this, "Report Copy Do no Exist", Toast.LENGTH_LONG).show();

                }
                //dialog.dismiss(); //to cancle
            }
        });
        builder.show();

    }


    public void createPdf(final String ReportDate, int checkValue) throws DocumentException, FileNotFoundException, IOException {
        //
       /* waitDialog = ProgressDialog.show(ViewReport.this, "Creating PDF", "Please wait...", true);

        new Thread(new Runnable()     //thread to display waitDialog box
        {
            @Override
            public void run()
            {
                // do the thing that takes a long time

                try
                {*/


        String username = mydb.getUsername();


        //The first thing we need to do is to create a folder where we will save
        //the PDF files we will be creating and we do this with the code below:


        //File pdfFolder = new File(Environment.getExternalStoragePublicDirectory("Documents"), "MOD_Reports/" + username);
        //File pdfFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/MOD_Reports/" + username);
        File pdfFolder = new File(Environment.getExternalStorageDirectory() + "/MOD_Reports/" + username);

        //To save under Android/data/package name
        // File pdfFolder = new File(getExternalFilesDir(null), "MOD_Reports/"+ username);

        if (!pdfFolder.exists()) {
            boolean res = pdfFolder.mkdirs();
            if (!res) {
                if (!checkDevicePermission())
                    return;
                else
                    pdfFolder.mkdirs();
            } else {
                Log.i("LOG_TAG", "Pdf Directory created");
            }

        } else
            checkDevicePermission();


        // Next we need to create the name(s) of the Pdf file we are going to create.
        // We do this my appending the current date and time to the string “SelfNote” and then append “.pdf” like this:
        // Date date = new Date();
        //  String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(date);
        // String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(date);

        myFile = new File(pdfFolder, "MOD_Reports" + ReportDate + ".pdf");


        //create an output stream where the iText library will write the PDF to,
        // and point the output stream to the myfile we created.
        OutputStream output = new FileOutputStream(myFile);


        //Step 1
        Document document;

        //Rectangle pagesize = new Rectangle(216f, 720f);
        //document = new Document(pagesize, 36f, 72f, 108f, 180f);

        //the above document created a standard European sized document,
        // to create an American standard sized document you can create the document instead like this
        //Document
        document = new Document(PageSize.A4);


        //Step 2
        //The PdfWriter class is responsible for writing the PDF file.
        // And in this step, you associate the blank document we created above with the PdfWriter class
        // and point it to the output stream where the PDF will be written to like so:

        PdfWriter.getInstance(document, output);

        //above method will throw DocumentException
        // So add DocumentException to the signature of this method so it looks like this with the content removed for brevity.
        // i.e   private void createPdf() throws FileNotFoundException, DocumentException  {}

        //Step 3
        document.open();

        //Step 4 Add content


        Drawable d = getResources().getDrawable(R.drawable.manipal_logo);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
        Image img1 = Image.getInstance(stream.toByteArray());
        img1.scaleAbsolute(50, 50);


 /*       d = getResources().getDrawable(R.drawable.logo2);
        bitDw = ((BitmapDrawable) d);
        bmp = bitDw.getBitmap();
        stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image img2 = Image.getInstance(stream.toByteArray());
        img2.scaleAbsolute(50, 50);


        d = getResources().getDrawable(R.drawable.logo3);
        bitDw = ((BitmapDrawable) d);
        bmp = bitDw.getBitmap();
        stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image img3 = Image.getInstance(stream.toByteArray());
        img3.scaleAbsolute(50, 55);
*/
        PdfPTable logosTable = new PdfPTable(1);        // num of columns as argument.
        logosTable.setWidthPercentage(90);

        PdfPCell imgCel1 = new PdfPCell(img1);          //Inserting image inside cell
        //    PdfPCell imgCel2 = new PdfPCell(img2);
        //    PdfPCell imgCel3 = new PdfPCell(img3);

        imgCel1.setHorizontalAlignment(Element.ALIGN_RIGHT);   //align cells
        //    imgCel2.setHorizontalAlignment(Element.ALIGN_CENTER);
        //    imgCel3.setHorizontalAlignment(Element.ALIGN_RIGHT);

        //   setNoBorder(imgCel1, imgCel2, imgCel3);          //Removing cell Border
        setNoBorder(imgCel1);

        //adding 3 logos to table
        logosTable.addCell(imgCel1);
        //    logosTable.addCell(imgCel2);
        //    logosTable.addCell(imgCel3);

        //    insertRowNoBORDER(logosTable, 1);
        document.add(logosTable);


        PdfPTable basicTable = new PdfPTable(3);        // num of columns as argument.
        basicTable.setWidthPercentage(90);
        float[] colwidth = new float[]{10f, 20f, 20f};
        basicTable.setWidths(colwidth);                 //setting column Width

        Font fontHeading = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        PdfPCell heading_cell = new PdfPCell(new Paragraph("MOD Report", fontHeading));
        heading_cell.setColspan(3);
        setNoBorder(heading_cell);
        heading_cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        basicTable.addCell(heading_cell);

        insertRowNoBORDER(basicTable, 3);

        PdfPCell cell1;
        PdfPCell cell2;
        PdfPCell cell3;
        PdfPCell cell4;

        String value1 = "", value2 = "", value3 = "", value4 = "", value5 = "";
        Cursor cursor = mydb.db.rawQuery("Select fname,lname,designation from user_details where id = '" + mydb.getUserID() + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = cursor.getString(2);

            value1 = value1 + " " + value2;
        }
        cursor.close();

        cell1 = new PdfPCell(new Paragraph("Date                :"));
        cell2 = new PdfPCell(new Paragraph(ReportDate));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);

        cell1 = new PdfPCell(new Paragraph("Name              :"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);

        cell1 = new PdfPCell(new Paragraph("Designation     :"));
        cell2 = new PdfPCell(new Paragraph(value3));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);

        insertRowNoBORDER(basicTable, 3);

        Font font14 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("Areas monitored", font14));
        cell1.setColspan(3);
        setNoBorder(cell1);
        insertRow(basicTable, cell1);


        value1 = "";
        value2 = "";
        value3 = "";
        value4 = "";
        cursor = mydb.db.rawQuery("select table_name from table_list where table_id in (select table_id from records where rdate = '" + ReportDate + "' and table_id in('T3','T4','T5','T6','T7')) ORDER BY table_name ASC", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (!value1.equals(""))
                    value1 += ", ";

                String str = cursor.getString(0);
              /*  if (str.equals("icu001"))
                    str = "CAS-ICU";
                else if (str.equals("icu002"))
                    str = "ICU-1";
                else if (str.equals("icu003"))
                    str = "ICU-2";
                else if (str.equals("icu004"))
                    str = "ICU-3";
                else if (str.equals("icu005"))
                    str = "ICU-4";*/

                value1 += str;

            } while (cursor.moveToNext());
        }
        cursor.close();


        cell1 = new PdfPCell(new Paragraph("ICUs               :"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);


        value1 = "";
        cursor = mydb.db.rawQuery("select w_name from wards where record_id = (select record_id from records where table_id = 'T8' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (!value1.equals(""))
                    value1 += ", ";

                value1 += cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();

        cell1 = new PdfPCell(new Paragraph("General Ward :"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);


        value1 = "";
        cursor = mydb.db.rawQuery("select w_name from wards where record_id = (select record_id from records where table_id = 'T9' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (!value1.equals(""))
                    value1 += ", ";

                value1 += cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        cell1 = new PdfPCell(new Paragraph("Special Ward  :"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell2.setColspan(2);
        setNoBorder(cell1, cell2);
        insertRow(basicTable, cell1, cell2);
        insertRowNoBORDER(basicTable, 3);
        document.add(basicTable);


        //Table Beds
        PdfPTable bedsTable = new PdfPTable(3);        // num of columns as argument.
        bedsTable.setWidthPercentage(90);

        cell1 = new PdfPCell(new Paragraph("ICUs", font14));
        cell1.setColspan(3);
        setNoBorder(cell1);
        bedsTable.addCell(cell1);

        cell1 = new PdfPCell(new Paragraph("Beds:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(3);
        setNoBorder(cell1);
        bedsTable.addCell(cell1);

        Font font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("In Use", font12));
        cell3 = new PdfPCell(new Paragraph("Available", font12));
        BaseColor HeadingBlue = new BaseColor(68, 113, 194);
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select beds_use,beds_avl from icu where record_id = (select record_id from records where table_id = 'T3'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
        }
        cursor.close();


        BaseColor RowBlue = new BaseColor(218, 224, 249);
        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("Casualty ICU"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell3 = new PdfPCell(new Paragraph(value2));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select beds_use,beds_avl from icu where record_id = (select record_id from records where table_id = 'T4'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("ICU 1"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell3 = new PdfPCell(new Paragraph(value2));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        //setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select beds_use,beds_avl from icu where record_id = (select record_id from records where table_id = 'T5'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
        }
        cursor.close();
        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("ICU 2"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell3 = new PdfPCell(new Paragraph(value2));
        setRowHeight(20, cell1);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select beds_use,beds_avl from icu where record_id = (select record_id from records where table_id = 'T6'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("ICU 3"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell3 = new PdfPCell(new Paragraph(value2));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        //setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select beds_use,beds_avl from icu where record_id = (select record_id from records where table_id = 'T7'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell1 = new PdfPCell(new Paragraph("ICU 4"));
        cell2 = new PdfPCell(new Paragraph(value1));
        cell3 = new PdfPCell(new Paragraph(value2));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(bedsTable, cell1, cell2, cell3);
        document.add(bedsTable);

        document.add(new Paragraph(" "));   //Empty Space after table


        //Table Ventilators
        PdfPTable ventilatorsTable = new PdfPTable(4);        // num of columns as argument.
        ventilatorsTable.setWidthPercentage(90);

        cell1 = new PdfPCell(new Paragraph("Ventilators:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(4);
        setNoBorder(cell1);
        ventilatorsTable.addCell(cell1);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("In Use", font12));
        cell3 = new PdfPCell(new Paragraph("Available", font12));
        cell4 = new PdfPCell(new Paragraph("Breakdown", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(ventilatorsTable, cell1, cell2, cell3, cell4);


        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select vent_use,vent_avl,vent_repair from icu where record_id = (select record_id from records where table_id = 'T3'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = cursor.getString(2);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Casualty ICU", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(ventilatorsTable, cell1, cell2, cell3, cell4);

        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select vent_use,vent_avl,vent_repair from icu where record_id = (select record_id from records where table_id = 'T4'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = cursor.getString(2);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 1", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(ventilatorsTable, cell1, cell2, cell3, cell4);


        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select vent_use,vent_avl,vent_repair from icu where record_id = (select record_id from records where table_id = 'T5'  AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = cursor.getString(2);
        }
        cursor.close();


        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 2", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(ventilatorsTable, cell1, cell2, cell3, cell4);

        document.add(ventilatorsTable);
        document.add(new Paragraph(" "));


        //Table MLCCases:
        PdfPTable MlcCasesTable = new PdfPTable(2);        // num of columns as argument.
        MlcCasesTable.setWidthPercentage(90);
        MlcCasesTable.setWidths(new float[]{25f, 90f});           //setting column Width

        cell1 = new PdfPCell(new Paragraph("MLC Cases:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(2);
        setNoBorder(cell1);
        MlcCasesTable.addCell(cell1);

        insertRowNoBORDER(MlcCasesTable, 2);


        value1 = "";
        int tot = 0;
        int sent = 0;
        cursor = mydb.db.rawQuery("select total_mlc,sent_mlc from tr_er where record_id in(select record_id from records where table_id in('T1','T2') and rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                tot += Integer.parseInt(cursor.getString(0));
                sent +=  Integer.parseInt(cursor.getString(1));
            } while (cursor.moveToNext());

            value1 = Integer.toString(tot);
        }
        cursor.close();


        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Total:", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        setRowHeight(20, cell1);
        setNoBorder(cell1, cell2);
        insertRow(MlcCasesTable, cell1, cell2);

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Police intimation:", font12));
        cell2 = new PdfPCell(new Paragraph(" ", font12));
        setRowHeight(20, cell1);
        setNoBorder(cell1, cell2);
        insertRow(MlcCasesTable, cell1, cell2);

/*
        value1 = "";
        int sent = 0;
        int pending = 0;
        cursor = mydb.db.rawQuery("select mlc,police_flag from flags where record_id in(select record_id from records where table_id in('T1','T2') and rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                value1 = cursor.getString(0);
                value2 = cursor.getString(1);

                if (value2.equals("0")) {
                    pending += Integer.parseInt(value1);
                } else if (value2.equals("1")) {
                    sent += Integer.parseInt(value1);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

*/


/*
                    value1 ="";
                    int sent = 0;
                    int pending = 0;
                    cursor = mydb.db.rawQuery("select police_flag from tr_er where cdate = '"+ ReportDate +"'",null);
                    if(cursor.getCount()>0)
                    {
                        cursor.moveToFirst();
                        do {
                            value1 = cursor.getString(0);
                            value2 = cursor.getString(1);

                            if(value2.equals("0"))
                            {
                                pending += Integer.parseInt(value1);
                            }
                            else  if(value2.equals("1"))
                            {
                                sent += Integer.parseInt(value1);
                            }
                        }while(cursor.moveToNext());
                    }
                    cursor.close();

                    String sent ="";
                    String pending = "";
                    if(value1.equals("00"))
                    {
                        sent ="";
                        pending = "YES";
                    }
                    else if(value1.equals("01") || value1.equals("10")|| value1.equals("11"))
                    {
                        sent ="YES";
                        pending = "";
                    }*/


        String strSent = Integer.toString(sent);
        String strPending = Integer.toString(tot - sent);


        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Sent:", font12));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2 = new PdfPCell(new Paragraph(strSent, font12));
        setRowHeight(20, cell1);
        setNoBorder(cell1, cell2);
        insertRow(MlcCasesTable, cell1, cell2);


        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Pending:", font12));
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2 = new PdfPCell(new Paragraph(strPending, font12));
        setRowHeight(20, cell1);
        setNoBorder(cell1, cell2);
        insertRow(MlcCasesTable, cell1, cell2);

        document.add(MlcCasesTable);
        document.add(new Paragraph(" "));


        //Table Crash cart status:
        PdfPTable CrashcartTable = new PdfPTable(4);        // num of columns as argument.
        CrashcartTable.setWidthPercentage(90);

        cell1 = new PdfPCell(new Paragraph("Crash cart status:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(4);
        setNoBorder(cell1);
        CrashcartTable.addCell(cell1);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("Drug", font12));
        cell3 = new PdfPCell(new Paragraph("Available Stock", font12));
        cell4 = new PdfPCell(new Paragraph("Documentation", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CrashcartTable, cell1, cell2, cell3, cell4);


        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select drug,stock,docu_flag from flags where record_id =(select record_id from records where table_id = 'T1' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = toCORRECTorINCORRECT(cursor.getString(2));
        }
        cursor.close();


        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Trauma triage", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CrashcartTable, cell1, cell2, cell3, cell4);


        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select drug,stock,docu_flag from flags where record_id =(select record_id from records where table_id = 'T2' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
            value2 = cursor.getString(1);
            value3 = toCORRECTorINCORRECT(cursor.getString(2));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Emergency triage", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CrashcartTable, cell1, cell2, cell3, cell4);


        /*font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("(others)", font12));
        cell2 = new PdfPCell(new Paragraph(" ", font12));
        cell3 = new PdfPCell(new Paragraph(" ", font12));
        cell4 = new PdfPCell(new Paragraph(" ", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CrashcartTable, cell1, cell2, cell3, cell4);*/

        document.add(CrashcartTable);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));


        //Table  CleanTable:
        PdfPTable CleanTable = new PdfPTable(4);        // num of columns as argument.
        CleanTable.setWidthPercentage(90);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("Availability of doctors", font12));
        cell3 = new PdfPCell(new Paragraph("Cleanliness", font12));
        cell4 = new PdfPCell(new Paragraph("House Keeping Availability", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CleanTable, cell1, cell2, cell3, cell4);


        value1 = "";
        cursor = mydb.db.rawQuery("select doct_avl from tr_er where record_id = (select record_id from records where table_id = 'T1' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T1' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toYESorNO(cursor.getString(0));
            value3 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Trauma triage", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CleanTable, cell1, cell2, cell3, cell4);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select sur_flag,med_flag from tr_er where record_id = (select record_id from records where table_id = 'T2' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            String avl_sur = toYESorNO(cursor.getString(0));
            String avl_med = toYESorNO(cursor.getString(1));

            if (avl_sur.equals("YES") || avl_med.equals("YES"))
                value1 = "YES";
            else
                value1 = "NO";
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T2' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toYESorNO(cursor.getString(0));
            value3 = toYESorNO(cursor.getString(1));

        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Emergency triage", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));

        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CleanTable, cell1, cell2, cell3, cell4);

        /*font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("(others)", font12));
        cell2 = new PdfPCell(new Paragraph(" ", font12));
        cell3 = new PdfPCell(new Paragraph(" ", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(CleanTable, cell1, cell2, cell3);*/

        document.add(CleanTable);
        document.add(new Paragraph(" "));


        //other details of tr_er and icu section

        PdfPTable ICU_tr_er_OtherTable = new PdfPTable(3);        // num of columns as argument.
        ICU_tr_er_OtherTable.setWidthPercentage(90);
        colwidth = new float[]{17f, 15f, 20f};
        ICU_tr_er_OtherTable.setWidths(colwidth);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("Cleanliness", font12));
        cell3 = new PdfPCell(new Paragraph("House Keeping Availability", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);

        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T3' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Casualty ICU", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T4' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 1", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T5' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 2", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T6' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 3", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select clean_flag,hk_flag from flags where record_id = (select record_id from records where table_id = 'T7' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 4", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3);
        setRowBgColor(RowBlue, cell1, cell2, cell3);
        insertRow(ICU_tr_er_OtherTable, cell1, cell2, cell3);

        document.add(ICU_tr_er_OtherTable);
        document.add(new Paragraph(" "));


        //Table Case sheet documentation:
        PdfPTable CasesheetTable = new PdfPTable(4);        // num of columns as argument.
        CasesheetTable.setWidthPercentage(90);
        colwidth = new float[]{10f, 10f, 12f, 25f};
        CasesheetTable.setWidths(colwidth);                 //setting column Width

        cell1 = new PdfPCell(new Paragraph("Case sheet documentation:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(4);
        setNoBorder(cell1);
        CasesheetTable.addCell(cell1);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("IP no.", font12));
        cell3 = new PdfPCell(new Paragraph("Documentation", font12));
        cell4 = new PdfPCell(new Paragraph("Comments", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);


        value1 = "";
        cursor = mydb.db.rawQuery("select ipno from icu where record_id = (select record_id from records where table_id = 'T3' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select docu_flag,comments from flags where record_id = (select record_id from records where table_id = 'T3' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toCORRECTorINCORRECT(cursor.getString(0));
            value3 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("Casualty ICU", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);


        value1 = "";
        cursor = mydb.db.rawQuery("select ipno from icu where record_id = (select record_id from records where table_id = 'T4' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select docu_flag,comments from flags where record_id = (select record_id from records where table_id = 'T4' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toCORRECTorINCORRECT(cursor.getString(0));
            value3 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 1", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);


        value1 = "";
        cursor = mydb.db.rawQuery("select ipno from icu where record_id = (select record_id from records where table_id = 'T5' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select docu_flag,comments from flags where record_id = (select record_id from records where table_id = 'T5' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toCORRECTorINCORRECT(cursor.getString(0));
            value3 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 2", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);


        value1 = "";
        cursor = mydb.db.rawQuery("select ipno from icu where record_id = (select record_id from records where table_id = 'T6' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select docu_flag,comments from flags where record_id = (select record_id from records where table_id = 'T6' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toCORRECTorINCORRECT(cursor.getString(0));
            value3 = cursor.getString(1);
        }
        cursor.close();
        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 3", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);

        value1 = "";
        cursor = mydb.db.rawQuery("select ipno from icu where record_id = (select record_id from records where table_id = 'T7' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value1 = cursor.getString(0);
        }
        cursor.close();

        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select docu_flag,comments from flags where record_id = (select record_id from records where table_id = 'T7' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            value2 = toCORRECTorINCORRECT(cursor.getString(0));
            value3 = cursor.getString(1);
        }
        cursor.close();

        font12 = new Font(Font.FontFamily.HELVETICA, 12);
        cell1 = new PdfPCell(new Paragraph("ICU 4", font12));
        cell2 = new PdfPCell(new Paragraph(value1, font12));
        cell3 = new PdfPCell(new Paragraph(value2, font12));
        cell4 = new PdfPCell(new Paragraph(value3, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
        setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
        insertRow(CasesheetTable, cell1, cell2, cell3, cell4);

        document.add(CasesheetTable);
        document.add(new Paragraph(" "));


        Paragraph para;                                               // Used for comments
        BaseFont bf = BaseFont.createFont();
        float indentation = bf.getWidthPoint("Comments :  ", 17);


        //wards
        cursor = mydb.db.rawQuery("select record_id from records where table_id in ('T8','T9') AND rdate = '" + ReportDate + "'", null);
        if (cursor.getCount() > 0) {
            PdfPTable wardsTable = new PdfPTable(4);        // num of columns as argument.
            wardsTable.setWidthPercentage(90);
            //colwidth = new float[]{10f, 10f, 12f, 25f};
            //wardsTable.setWidths(colwidth);                 //setting column Width

            cell1 = new PdfPCell(new Paragraph("Wards:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(4);
            setNoBorder(cell1);
            wardsTable.addCell(cell1);

            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("Area", font12));
            cell2 = new PdfPCell(new Paragraph("Cleanliness", font12));
            cell3 = new PdfPCell(new Paragraph("HouseKeeping Register Updated", font12));
            cell4 = new PdfPCell(new Paragraph("Pharmacy Indents Received", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
            setRowBgColor(HeadingBlue, cell1, cell2, cell3, cell4);
            insertRow(wardsTable, cell1, cell2, cell3, cell4);
            cursor.close();

            value1 = "";
            cursor = mydb.db.rawQuery("select w_name from wards where record_id = (select record_id from records where table_id = 'T8' AND rdate = '" + ReportDate + "')", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                value1 = cursor.getString(0);
                cursor.close();

                value2 = "";
                value3 = "";
                value4 = "";
                cursor = mydb.db.rawQuery("select clean_flag,hk_register_flag,ph_flag from flags where record_id = (select record_id from records where table_id = 'T8' AND rdate = '" + ReportDate + "')", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    value2 = toYESorNO(cursor.getString(0));
                    value3 = toYESorNO(cursor.getString(1));
                    value4 = toYESorNO(cursor.getString(2));
                }
                cursor.close();
                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                cell3 = new PdfPCell(new Paragraph(value3, font12));
                cell4 = new PdfPCell(new Paragraph(value4, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
                setRowBgColor(RowBlue, cell1, cell2, cell3, cell4);
                insertRow(wardsTable, cell1, cell2, cell3, cell4);
            }
            cursor.close();

            value1 = "";
            cursor = mydb.db.rawQuery("select w_name from wards where record_id = (select record_id from records where table_id = 'T9' AND rdate = '" + ReportDate + "')", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                value1 = cursor.getString(0);

                cursor.close();
                value2 = "";
                value3 = "";
                value4 = "";
                cursor = mydb.db.rawQuery("select clean_flag,hk_register_flag,ph_flag from flags where record_id = (select record_id from records where table_id = 'T9' AND rdate = '" + ReportDate + "')", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    value2 = toYESorNO(cursor.getString(0));
                    value3 = toYESorNO(cursor.getString(1));
                    value4 = toYESorNO(cursor.getString(2));
                }
                cursor.close();
                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                cell3 = new PdfPCell(new Paragraph(value3, font12));
                cell4 = new PdfPCell(new Paragraph(value4, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2, cell3, cell4);
                insertRow(wardsTable, cell1, cell2, cell3, cell4);
            }
            cursor.close();

            document.add(wardsTable);
            document.add(new Paragraph(" "));
        }
        cursor.close();


        //Blood Bank Table
        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select avail_staff,equip_break from flags where record_id = (select record_id from records where table_id = 'T10' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));
            value3 = toYESorNO(cursor.getString(1));  // equip_break


            PdfPTable BloodBankTable = new PdfPTable(2);        // num of columns as argument.
            BloodBankTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("Blood Bank :", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            BloodBankTable.addCell(cell1);


            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(BloodBankTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(value1, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(BloodBankTable, cell1, cell2);

            cursor = mydb.db.rawQuery("select blood_shortage from blood_bank where record_id = (select record_id from records where table_id = 'T10' AND rdate = '" + ReportDate + "')", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                value2 = toYESorNO(cursor.getString(0));
            }
            cursor.close();
            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Shortage of blood and blood components", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(BloodBankTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Equipment breakdown", font12));
            cell2 = new PdfPCell(new Paragraph(value3, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(BloodBankTable, cell1, cell2);

            document.add(BloodBankTable);
            document.add(new Paragraph(" "));
        }
        cursor.close();


        //Clinical lab Table
        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select avail_staff,equip_break from flags where record_id = (select record_id from records where table_id = 'T11' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));  // equip_break

            PdfPTable ClinicalTable = new PdfPTable(2);        // num of columns as argument.
            ClinicalTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("Clinical lab :", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            ClinicalTable.addCell(cell1);


            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(ClinicalTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(value1, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(ClinicalTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Equipment breakdown", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(ClinicalTable, cell1, cell2);

            document.add(ClinicalTable);
            document.add(new Paragraph(" "));
        }
        cursor.close();


        //CSSD Table
        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select avail_staff,equip_break from flags where record_id = (select record_id from records where table_id = 'T12' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));  // equip_break


            PdfPTable CSSDTable = new PdfPTable(2);        // num of columns as argument.
            CSSDTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("CSSD :", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            CSSDTable.addCell(cell1);

            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(CSSDTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(value1, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(CSSDTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Equipment breakdown", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(CSSDTable, cell1, cell2);

            document.add(CSSDTable);
            document.add(new Paragraph(" "));           //for blank line after comments
        }
        cursor.close();


        //Dialysis
        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select avail_staff,equip_break from flags where record_id = (select record_id from records where table_id = 'T13' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));
            value2 = toYESorNO(cursor.getString(1));  // equip_break

            PdfPTable DialysisTable = new PdfPTable(2);        // num of columns as argument.
            DialysisTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("Dialysis:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            DialysisTable.addCell(cell1);


            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(DialysisTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(value1, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(DialysisTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Equipment breakdown", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(DialysisTable, cell1, cell2);

            document.add(DialysisTable);

            document.add(new Paragraph(" "));

        }
        cursor.close();


        //Pharmacy
        value1 = "";
        value2 = "";
        value3 = "";
        cursor = mydb.db.rawQuery("select avail_staff from flags where record_id = (select record_id from records where table_id = 'T14' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = toYESorNO(cursor.getString(0));


            PdfPTable PharmacyTable = new PdfPTable(2);        // num of columns as argument.
            PharmacyTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("Pharmacy:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            PharmacyTable.addCell(cell1);


            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(PharmacyTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(value1, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(PharmacyTable, cell1, cell2);


            cursor = mydb.db.rawQuery("select stock_out_drugs,indents_sent from pharmacy where record_id = (select record_id from records where table_id = 'T14' AND rdate = '" + ReportDate + "')", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                value2 = toYESorNO(cursor.getString(0));
                value3 = toYESorNO(cursor.getString(1));
            }
            cursor.close();
            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Drugs out of stock", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(PharmacyTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Indents sent on time", font12));
            cell2 = new PdfPCell(new Paragraph(value3, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(PharmacyTable, cell1, cell2);

            document.add(PharmacyTable);
            document.add(new Paragraph(" "));

        }
        cursor.close();


        //Radiology
        value1 = "";
        value2 = "";
        value3 = "";
        value4 = "";
        cursor = mydb.db.rawQuery("select equip_break from flags where record_id = (select record_id from records where table_id = 'T15' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //value1 = toYESorNO(cursor.getString(0));
            value4 = toYESorNO(cursor.getString(0));

            PdfPTable RadiologyTable = new PdfPTable(2);        // num of columns as argument.
            RadiologyTable.setWidthPercentage(90);

            cell1 = new PdfPCell(new Paragraph("Radiology:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell1.setColspan(2);
            setNoBorder(cell1);
            RadiologyTable.addCell(cell1);


            font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            font12.setColor(BaseColor.WHITE);
            cell1 = new PdfPCell(new Paragraph("", font12));
            cell2 = new PdfPCell(new Paragraph("Status", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(HeadingBlue, cell1, cell2);
            insertRow(RadiologyTable, cell1, cell2);

/*
            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of staff", font12));
            cell2 = new PdfPCell(new Paragraph(" ", font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(RadiologyTable, cell1, cell2);*/


            cursor = mydb.db.rawQuery("select support_staff,doctors from radiology where record_id = (select record_id from records where table_id = 'T15' AND rdate = '" + ReportDate + "')", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                value2 = toYESorNO(cursor.getString(0));
                value3 = toYESorNO(cursor.getString(1));
            }
            cursor.close();
            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of Support staff", font12));
            cell2 = new PdfPCell(new Paragraph(value2, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(RadiologyTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Availability of Doctors", font12));
            cell2 = new PdfPCell(new Paragraph(value3, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            setRowBgColor(RowBlue, cell1, cell2);
            insertRow(RadiologyTable, cell1, cell2);


            font12 = new Font(Font.FontFamily.HELVETICA, 12);
            cell1 = new PdfPCell(new Paragraph("Equipment breakdown", font12));
            cell2 = new PdfPCell(new Paragraph(value4, font12));
            setRowHeight(20, cell1);
            setRowBorderColor(HeadingBlue, cell1, cell2);
            //setRowBgColor(RowBlue, cell1, cell2);
            insertRow(RadiologyTable, cell1, cell2);

            document.add(RadiologyTable);
            document.add(new Paragraph(" "));

        }
        cursor.close();


        // IssuesTable

        Cursor curIssues = mydb.db.rawQuery("select record_id,issues from flags where record_id in(Select record_id from records where table_id in ('T1','T2','T3','T4','T5','T6','T7')and rdate = '" + ReportDate + "')  and issues IS NOT NULL", null);
        Cursor curComments = mydb.db.rawQuery("select record_id,comments from flags where record_id in(Select record_id from records where table_id in ('T10','T11','T12','T13','T14','T15')and rdate = '" + ReportDate + "')  and comments IS NOT NULL", null);

        if (curIssues.getCount() > 0 || curComments.getCount() > 0) {

            PdfPTable IssuesTable = new PdfPTable(2);        // num of columns as argument.
            IssuesTable.setWidthPercentage(90);
            IssuesTable.setWidths(new float[]{25f, 90f});           //setting column Width

            int flag = 0;


            int i = 1;
            if (curIssues.getCount() > 0) {
                curIssues.moveToFirst();
                Cursor cur;
                do {
                    if (curIssues.getString(1).equals(""))
                        continue;
                    if (flag <= 0) {
                        insertOthersTableHeader(IssuesTable, HeadingBlue);
                        flag++;
                    }
                    cur = mydb.db.rawQuery("select table_name from table_list where table_id = (select table_id from records where record_id = '" + curIssues.getString(0) + "')", null);
                    value1 = "";
                    cur.moveToFirst();
                    value1 = cur.getString(0);
                    cur.close();


                    BaseColor rowcolor;
                    if (i % 2 == 0)
                        rowcolor = BaseColor.WHITE;
                    else
                        rowcolor = new BaseColor(218, 224, 249);

                    if (curIssues.getString(1).equals("") || curIssues.getString(1).isEmpty())
                        continue;
                    insertComments(value1, curIssues.getString(1), rowcolor, IssuesTable);
                    i++;
                } while (curIssues.moveToNext());
            }

            if (curComments.getCount() > 0) {
                curComments.moveToFirst();
                Cursor cur;
                do {
                    if (curComments.getString(1).equals(""))
                        continue;
                    if (flag <= 0) {
                        insertOthersTableHeader(IssuesTable, HeadingBlue);
                        flag++;
                    }
                    cur = mydb.db.rawQuery("select table_name from table_list where table_id = (select table_id from records where record_id = '" + curComments.getString(0) + "')", null);
                    value1 = "";
                    cur.moveToFirst();
                    value1 = cur.getString(0);
                    cur.close();

                    BaseColor rowcolor;
                    if (i % 2 == 0)
                        rowcolor = BaseColor.WHITE;
                    else
                        rowcolor = new BaseColor(218, 224, 249);
                    if (curComments.getString(1).equals("") || curComments.getString(1).isEmpty())
                        continue;
                    insertComments(value1, curComments.getString(1), rowcolor, IssuesTable);
                    i++;
                } while (curComments.moveToNext());

            }

            document.add(IssuesTable);
            document.add(new Paragraph(" "));

        }

       /* PdfPTable IssuesTable = new PdfPTable(2);        // num of columns as argument.
        IssuesTable.setWidthPercentage(90);
        IssuesTable.setWidths(new float[]{25f, 90f});           //setting column Width


        cell1 = new PdfPCell(new Paragraph("Issues / Comments :", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(2);
        setNoBorder(cell1);
        IssuesTable.addCell(cell1);

        font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        cell2 = new PdfPCell(new Paragraph("Comments", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2);
        setRowBgColor(HeadingBlue, cell1, cell2);
        insertRow(IssuesTable, cell1, cell2);



        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T1' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "Trauma triage";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        // insertEmptyRow(OthersTable,HeadingBlue,2);


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T2' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "Emergency triage";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                //setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T3' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "Casualty ICU";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T4' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "ICU 1";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                //setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T5' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "ICU 2";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T6' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "ICU 3";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {
                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                //setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();


        value1 = "";
        value2 = "";
        cursor = mydb.db.rawQuery("select issues from flags where record_id =(select record_id from records where table_id =  'T7' AND rdate = '" + ReportDate + "')", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = "ICU 4";
            value2 = cursor.getString(0);

            if (!value2.isEmpty()) {

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                cell2 = new PdfPCell(new Paragraph(value2, font12));
                setRowHeight(20, cell1);
                setRowBorderColor(HeadingBlue, cell1, cell2);
                setRowBgColor(RowBlue, cell1, cell2);
                insertRow(IssuesTable, cell1, cell2);
            }
        }
        cursor.close();

        document.add(IssuesTable);
        document.add(new Paragraph(" "));

*/
        //OthersTable
        value1 = "";
        cursor = mydb.db.rawQuery("select others from reports where rdate = '" + ReportDate + "' and others IS NOT NULL", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            value1 = cursor.getString(0);

            if (!cursor.getString(0).equals("") || !cursor.getString(0).isEmpty()) {

                PdfPTable OthersTable = new PdfPTable(1);        // num of columns as argument.
                OthersTable.setWidthPercentage(90);

                cell1 = new PdfPCell(new Paragraph("Others:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                setNoBorder(cell1);
                OthersTable.addCell(cell1);

                font12 = new Font(Font.FontFamily.HELVETICA, 12);
                cell1 = new PdfPCell(new Paragraph(value1, font12));
                setNoBorder(cell1);
                insertRow(OthersTable, cell1);

                document.add(OthersTable);
            }
        }
        cursor.close();

        document.close();

        if (checkValue == 1)
            viewPdf();



/*
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitDialog.dismiss();

                       // viewPdf();
                        //popup after creating pdf
                       // promptForNextAction();
                    }
                });
            }
        }).start();
*/

    }

    public void insertComments(String value1, String value2, BaseColor rowcolor, PdfPTable table) {
        BaseColor HeadingBlue = new BaseColor(68, 113, 194);
        Font font12 = new Font(Font.FontFamily.HELVETICA, 12);
        PdfPCell cell1 = new PdfPCell(new Paragraph(value1, font12));
        PdfPCell cell2 = new PdfPCell(new Paragraph(value2, font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2);
        setRowBgColor(rowcolor, cell1, cell2);
        insertRow(table, cell1, cell2);

    }

    public String toYESorNO(String str) {
        if (str.equals("1"))
            return "YES";
        else
            return "NO";
    }

    public String toCORRECTorINCORRECT(String str) {
        if (str.equals("1"))
            return "CORRECT";
        else
            return "INCORRECT";
    }


    void setRowHeight(float height, PdfPCell... cell) {
        for (PdfPCell x : cell) {
            x.setFixedHeight(height);
        }
    }

    void setNoBorder(PdfPCell... cell) {
        for (PdfPCell x : cell) {
            x.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        }
    }

    void setRowBorderColor(BaseColor color, PdfPCell... cell) {
        for (PdfPCell x : cell) {
            x.setBorderColor(color);
        }
    }

    void setRowBgColor(BaseColor color, PdfPCell... cell) {
        for (PdfPCell x : cell) {
            x.setBackgroundColor(color);
        }
    }

    void insertRowNoBORDER(PdfPTable table, int colNum) {
        for (int i = 0; i < colNum; i++) {
            PdfPCell cell1 = new PdfPCell(new Paragraph("  "));
            cell1.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            table.addCell(cell1);
        }

    }

    void insertRow(PdfPTable table, PdfPCell... cell) {
        for (PdfPCell x : cell) {
            table.addCell(x);
        }
    }

    void insertEmptySpanedRow(PdfPTable table, int spanValue) {
        PdfPCell cell = new PdfPCell(new Paragraph("  "));
        cell.setColspan(spanValue);
        table.addCell(cell);

    }


    public void viewPdf() {

        try {
            // you email code here
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // show message to user
            Looper.prepare();
            waitDialog.dismiss();

            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
            downloadDialog.setTitle("No PDF Viewer Found");
            downloadDialog.setMessage("Do u want to Download");
            downloadDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String packageName = "com.google.android.apps.pdfviewer";
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException anfe) {
                        // Hmm, market is not installed
                        // Log.w(TAG, "Google Play is not installed; cannot install " + packageName);
                        Toast.makeText(ViewReport.this, "Google Play is not installed", Toast.LENGTH_LONG).show();
                    }

                }
            });
            downloadDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            downloadDialog.show();
            Looper.loop();
        }


    }


    public void emailNote(String date) {
        String[] toArr = {"dayananda@manipalhealth.com", "sudhakar@manipalhospitals.com", "jibu.thomas@manipalhealth.com", "somu.g@manipal.edu", "operations.kh@manipal.edu"};
        //"dayananda@manipalhealth.com","sudhakar@manipalhospitals.com","jibu.thomas@manipalhealth.com","somu.g@manipal.edu","dha.kmc@manipal.edu","operations.kh@manipal.edu"

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, toArr);
        email.putExtra(Intent.EXTRA_SUBJECT, "MOD Report");
        Cursor cur = mydb.db.rawQuery("select fname,lname,designation,affiliation from user_details where uname ='" + mydb.getUsername() + "'", null);
        String name = "", desig = "", affil = "";
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(0) + " " + cur.getString(1);
            desig = cur.getString(2);
            affil = cur.getString(3);
        }
        email.putExtra(Intent.EXTRA_TEXT, "Respected Sir,\n\nPlease find the attached MOD report for " + date + ".\n\nThanking You,\n\n" + name + ",\n" + desig + ",\n" + affil + ".");
        //Uri uri = Uri.parse(myFile.getAbsolutePath());
        Uri uri = Uri.fromFile(myFile);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);


        // email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void fileuploadThread() {

        // dialog = ProgressDialog.show(ViewReport.this, "", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
                uploadFile(myFile.getPath());

            }
        }).start();
    }


    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;
        //HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            waitDialog.dismiss();
            //dialog.dismiss();

            //  Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ViewReport.this, "Source File not exist", Toast.LENGTH_SHORT).show();
                    // messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                upLoadServerUri = "http://www.teaminnovators.esy.es/app_files/pdfupload.php";
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            // waitDialog.dismiss();
                            // Toast.makeText(ViewReport.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                            saveReport();


                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                // dialog.dismiss();
                waitDialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        // messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(ViewReport.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                waitDialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        // messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();
                    }
                });
                // Log.e("Upload file to server Exception","Exception : " + e.getMessage(), e);
            }
            waitDialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    /*public void promptForNextAction() {
        final String[] options = {getString(R.string.label_email), getString(R.string.label_preview), getString(R.string.label_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);   //getActivity()
        builder.setTitle("PDF Created, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_email))) {
                    //emailNote();
                } else if (options[which].equals(getString(R.string.label_preview))) {
                    viewPdf();
                } else if (options[which].equals(getString(R.string.label_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }*/




    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId()==R.id.listViewReports)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

            //menu.setHeaderTitle(Countries[info.position]);
            menu.setHeaderTitle("Options");


            String selected = ((TextView) v.findViewById(R.id.reportDate)).getText().toString();
            Toast toast = Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT);
            toast.show();

            String[] menuItems = getResources().getStringArray(R.array.submit_menu);
            for (int i = 0; i<menuItems.length; i++)
            {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }


    /*OK, I found it. You can get ID or POSITION of the list item which was longclicked like this:
        ((AdapterView.AdapterContextMenuInfo)menuInfo).id
        ((AdapterView.AdapterContextMenuInfo)menuInfo).position*/

    /*

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.pending_menu);
        String menuItemName = menuItems[menuItemIndex];
       // String listItemName = Countries[info.position];

        /*
        TextView text = (TextView)findViewById(R.id.footer);
        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        */
    //  return true;
    // }

    public boolean localDbSubmit(String rdate) {
        String status = "Submitted";
        if (mydb.updateReport(rdate, mydb.getUserID(), status, null)) {
            //Toast.makeText(ViewReport.this, "Report Submitted", Toast.LENGTH_LONG).show();
            Intent i = new Intent(ViewReport.this, DisplayActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            parentActivity.finish();
            return true;
        } else
            return false;
    }

    public String booltostr(Boolean b) {
        if (b)
            return "NO";
        else
            return "YES";
    }

    private void saveReport() {
        String rdate = mydb.getPendingReportDate();
        String user_id = mydb.getUserID();
        String submitted = "YES";
        Cursor csr = mydb.getExistingothers(rdate, user_id);
        csr.moveToFirst();
        String others = replaceSpace(csr.getString(0));
        csr.close();
        String path = "http://teaminnovators.esy.es/Reports/MOD_Reports" + date + ".pdf";


        Cursor cur_flg, cur_tr_er, cur_icu, cur_ward, cur_blood_bank, cur_pharmacy, cur_radiology, cur_stable;
        String urlSuffix = "?table_name=reports&rdate=" + rdate + "&user_id=" + user_id + "&submitted=" + submitted + "&others=" + others + "&path=" + path;
        save(urlSuffix);
        cur_stable = mydb.db.rawQuery("select * from stable_details where rdate='" + rdate + "'", null);
        if (cur_stable.getCount() > 0) {
            cur_stable.moveToFirst();
            do {
                urlSuffix = "?table_name=stable&table_id=" + cur_stable.getString(0) + "&rdate=" + cur_stable.getString(1) + "&st_ip=" + cur_stable.getString(2) + "&st_det=" + replaceSpace(cur_stable.getString(3));
                save(urlSuffix);
            } while (cur_stable.moveToNext());
        }
        csr = mydb.db.rawQuery("select table_id,rdate,record_id from records where rdate = '" + mydb.getPendingReportDate() + "'", null);

        if (csr.getCount() > 0) {
            csr.moveToFirst();
            do {
                cur_flg = mydb.db.rawQuery("select * from flags where record_id = '" + csr.getString(2) + "'", null);
                cur_flg.moveToFirst();
                if (csr.getString(0).equals("T1") || csr.getString(0).equals("T2")) {
                    cur_tr_er = mydb.db.rawQuery("select * from tr_er where record_id='" + csr.getString(2) + "'", null);
                    cur_tr_er.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + replaceSpace(cur_flg.getString(1)) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) + "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&total_mlc=" + cur_tr_er.getString(1) + "&sent_mlc=" + cur_tr_er.getString(2) + "&doct_avil=" + cur_tr_er.getString(3) + "&sur_flag=" + cur_tr_er.getString(4) + "&med_flag=" + cur_tr_er.getString(5);
                    save(urlSuf);
                } else if (csr.getString(0).equals("T3") || csr.getString(0).equals("T4") || csr.getString(0).equals("T5") || csr.getString(0).equals("T6") || csr.getString(0).equals("T7")) {
                    cur_icu = mydb.db.rawQuery("select * from icu where record_id='" + csr.getString(2) + "'", null);
                    cur_icu.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&beds_use=" + cur_icu.getString(1) + "&beds_avl=" + cur_icu.getString(2) + "&vent_use=" + cur_icu.getString(3) + "&vent_avl=" + cur_icu.getString(4) + "&vent_repair=" + cur_icu.getString(5) + "&ipno=" + cur_icu.getString(6);
                    save(urlSuf);
                } else if (csr.getString(0).equals("T8") || csr.getString(0).equals("T9")) {
                    cur_ward = mydb.db.rawQuery("select * from wards where record_id='" + csr.getString(2) + "'", null);
                    cur_ward.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&w_name=" + cur_ward.getString(1);
                    save(urlSuf);
                } else if (csr.getString(0).equals("T10")) {
                    cur_blood_bank = mydb.db.rawQuery("select * from blood_bank where record_id='" + csr.getString(2) + "'", null);
                    cur_blood_bank.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&blood_shortage=" + cur_blood_bank.getString(1);
                    save(urlSuf);
                } else if (csr.getString(0).equals("T11") || csr.getString(0).equals("T12") || csr.getString(0).equals("T13")) {
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12));
                    save(urlSuf);
                } else if (csr.getString(0).equals("T14")) {
                    cur_pharmacy = mydb.db.rawQuery("select * from pharmacy where record_id='" + csr.getString(2) + "'", null);
                    cur_pharmacy.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&stock_out_drugs=" + cur_pharmacy.getString(1) + "&indents_sent=" + cur_pharmacy.getString(2);
                    save(urlSuf);
                } else if (csr.getString(0).equals("T15")) {
                    cur_radiology = mydb.db.rawQuery("select * from radiology where record_id='" + csr.getString(2) + "'", null);
                    cur_radiology.moveToFirst();
                    String urlSuf = "?table_name=records&table_id=" + csr.getString(0) + "&rdate=" + csr.getString(1) + "&drug=" + cur_flg.getString(1) + "&stock=" + cur_flg.getString(2) + "&docu_flag=" + cur_flg.getString(3) +  "&clean_flag=" + cur_flg.getString(4) + "&hk_flag=" + cur_flg.getString(5) + "&issues=" + replaceSpace(cur_flg.getString(6)) + "&timestamp=" + cur_flg.getString(7) + "&ph_flag=" + cur_flg.getString(8) + "&avail_staff=" + cur_flg.getString(9) + "&hk_register_flag=" + cur_flg.getString(10) + "&equip_break=" + cur_flg.getString(11) + "&comments=" + replaceSpace(cur_flg.getString(12)) + "&support_staff=" + cur_radiology.getString(1) + "&doctors=" + cur_radiology.getString(2);
                    save(urlSuf);
                }

            } while (csr.moveToNext());
        }
        waitDialog.dismiss();
        if (localDbSubmit(date)) {
            emailNote(date);
            mydb.deleteLocalRecords();
        }
    }

    private void save(String urlSuffix) {
        class SaveReport extends AsyncTask<String, Void, String> {

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loading = ProgressDialog.show(ViewReport.this, "Saving",null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    // loading.dismiss();
                    // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    // loading.dismiss();
                    Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    String REGISTER_URL = "http://teaminnovators.esy.es/app_files/inserting.php";
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
        SaveReport sr = new SaveReport();
        sr.execute(urlSuffix);
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


    private void serverReportCheck(final String reportdate) {
        String urlSuffix = "?rdate=" + reportdate;
        //String urlSuffix = "?rdate=23-07-2016";

        class RegisterUser extends AsyncTask<String, Void, String> {


            private static final String REGISTER_URL = "http://teaminnovators.esy.es/app_files/reportRecordCheck.php";

            //ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // loading.dismiss();
                try {
                    if (s.contains("doesnotexist")) {
                        fileuploadThread();
                        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    } else {
                        waitDialog.dismiss();
                        new AlertDialog.Builder(ViewReport.this)
                                .setTitle("Warning")
                                .setMessage("Report for the Date " + reportdate + " has already been submitted. Hence this Report cannot be submitted")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (localDbSubmit(reportdate))
                                            mydb.deleteLocalRecords();
                                    }
                                })
                                .show();
                        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    waitDialog.dismiss();
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

    public int permissionCheck() {
       /* int permission = ContextCompat.checkSelfPermission(ViewReport.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return  permission;*/
        return ContextCompat.checkSelfPermission(ViewReport.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public boolean checkDevicePermission() {
        //int permissionCheck = ContextCompat.checkSelfPermission(ViewReport.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission = permissionCheck();
        android.util.Log.i("LOG_TAG", "Message" + permission);
        if (permission < 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewReport.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //pdfFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/MOD_Reports/" + username);
                if (permissionCheck() < 0)
                    ActivityCompat.requestPermissions(ViewReport.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {
                ActivityCompat.requestPermissions(ViewReport.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        if (permissionCheck() > -1) {

            android.util.Log.i("LOG_TAG", "Message" + permissionCheck());
            return true;

        } else {
            android.util.Log.i("LOG_TAG", "Message" + permissionCheck());
            return false;

        }
    }

    public void insertOthersTableHeader(PdfPTable IssuesTable, BaseColor HeadingBlue) {

        PdfPCell cell1 = new PdfPCell(new Paragraph("Issues / Comments :", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell1.setColspan(2);
        setNoBorder(cell1);
        IssuesTable.addCell(cell1);

        Font font12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        font12.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Paragraph("Area", font12));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Comments", font12));
        setRowHeight(20, cell1);
        setRowBorderColor(HeadingBlue, cell1, cell2);
        setRowBgColor(HeadingBlue, cell1, cell2);
        insertRow(IssuesTable, cell1, cell2);
    }

}
