package com.developers.yacc.taxis;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Map extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Marker mCurrent;
    LatLng Lantlong;
    Connection con;
    String Lat, Long;
    String un, passw, db, ip, value, valuet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ip = "208.118.63.49";
        db = "DB_A3B963_login";
        un = "DB_A3B963_login_admin";
        passw = "Thekingof02";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFra  gment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
       /* mMap.moveCamera(CameraUpdateFactory.newLatLng(Lantlong));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mCurrent = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                .position(Lantlong)
                .title("Tu"));*/

    }

    protected synchronized void buildGoogleApiClient() {
        mGogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGogleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Lantlong = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Lantlong));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        /*mCurrent = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                .position(Lantlong)
                .title("Tu"));*/
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Lat = Double.toString(lat);
        Long = Double.toString(lon);
        Update update = new Update();
        update.execute("");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGogleApiClient, this);
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
    public class Update extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(String r){
        }
        @Override
        protected String doInBackground(String... params) {
                try {
                    con = connectionclass(un, passw, db, ip);
                    if (con == null) {
                        z = "Check Your Internet Access!";
                    } else {
                        String query = "INSERT INTO ubicacion VALUES("+Lat+", "+Long+");";
                        Statement stmt = con.createStatement();
                        stmt.executeQuery(query);
                        con.close();
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            return z;
        }
    }
}
