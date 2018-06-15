package com.developers.yacc.taxis;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.sourceforge.jtds.jdbc.*;

import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity {
    private static final int INTERVALO = 2000; //2 segundos para salir
    EditText user, pass;
    Button ouy, cl;
    Dialog popup;
    TextView close;
    // Declaring connection variables
    Connection con;
    String un, passw, db, ip;
    String usr, psw;
    private long tiempoPrimerClick;

    //End Declaring connection variables
    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ouy = (Button) findViewById(R.id.login);
        user = (EditText) findViewById(R.id.User);
        pass = (EditText) findViewById(R.id.password);
        popup = new Dialog(this);
        // Declaring Server ip, username, database name and password
        ip = "208.118.63.49";
        db = "DB_A3B963_login";
        un = "DB_A3B963_login_admin";
        passw = "Thekingof02";
        // Declaring Server ip, username, database name and password
        ouy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usr = user.getText().toString();
                psw = pass.getText().toString();
                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute("");
            }
        });
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            ConnectionURL = "jdbc:jtds:sqlserver://"+server+";database=" + database + ";user=" + user + ";password=" + password + ";";
            //ConnectionURL = "jdbc:jtds:sqlserver://192.168.1.9;database=msss;instance=SQLEXPRESS;Network Protocol=NamedPipes" ;


            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        SpotsDialog waitdialog = new SpotsDialog(Login.this);
        @Override
        protected void onPreExecute() {
            waitdialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Login.this, r, Toast.LENGTH_SHORT).show();
            waitdialog.dismiss();
            if (r.equals("Login successful")){
                Intent desk = new Intent(Login.this, Map.class);
                startActivityForResult(desk, 0);
                finish();
            }
            else if(r.equals("Please enter Username and Password")){
                if (usr.trim().equalsIgnoreCase("")) {
                    user.setError("Este campo no puede estar vacio");
                } else if (psw.trim().equalsIgnoreCase("")) {
                    pass.setError("Este campo no puede estar vacio");
                }
            }
            else if(r.equals("Invalid Credentials!")) {
                popup.setContentView(R.layout.activity_pop_up);
                close = (TextView) popup.findViewById(R.id.close);
                cl = (Button) popup.findViewById(R.id.Ok);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });
                cl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();

                    }
                });
                popup.show();
            }
            if (isSuccess) {
                z = "Login successful";
                Toast.makeText(Login.this , "Login successful" , Toast.LENGTH_LONG).show();
                //finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            if (usr.trim().equals("") || psw.trim().equals(""))
                z = "Please enter Username and Password";
            else {
                try {
                    con = connectionclass(un, passw, db, ip);        // Connect to database
                    if (con == null) {
                        z = "Check Your Internet Access!";
                    } else {
                        String query = "select * from Login where usr= '" + usr + "' and psw= '" + psw + "' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            z = "Login successful";
                            isSuccess = true;
                            con.close();
                        } else {
                            z = "Invalid Credentials!";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }
}
