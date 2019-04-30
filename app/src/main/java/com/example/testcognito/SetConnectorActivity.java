package com.example.testcognito;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetConnectorActivity extends AppCompatActivity {
    
    private List<String> fieldList = new ArrayList<>();
    private List<String> paramList = new ArrayList<>();
    private Connector connector;
    private RecyclerView mRecyclerView ;
    private ConnParametersRecycleViewAdapter mAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean cnSetted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        connector = (Connector) getIntent().getSerializableExtra("connector");
        if(!checkWeatherConn(connector)) {
            setContentView(R.layout.activity_set_connector);

            fieldList = connector.getFields();
            mAdapter = new ConnParametersRecycleViewAdapter(fieldList, this);

            //RecyclerView setup - available connectors
            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setAdapter(mAdapter);

            //Mostra le view (edittext) in cui digitare i valori per i campi parametri dei connettori
            setFields();
        }
    }
     //TODO: sistema questa oscenità
    public boolean checkWeatherConn(Connector c) {
        if(c.getAction().equals("weather")) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("ANDREA LOC:",String.valueOf(location.getLatitude())
                                    +" "+String.valueOf(location.getLongitude()));
                            List<String> coordinates = new ArrayList<>();
                            coordinates.add(String.valueOf(location.getLatitude()));
                            coordinates.add(String.valueOf(location.getLongitude()));
                            ConnectorActivity.inputUpdateWf.add(buildJsonConn(c,coordinates).toString());

                            Toast.makeText(SetConnectorActivity.this, "Weather info set on your coordinates!",
                                    Toast.LENGTH_LONG).show();
                            SetConnectorActivity.this.finish();
                        }
                    });
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.i("ANDREA LOC:",String.valueOf(location.getLatitude())
                                    +" "+String.valueOf(location.getLongitude()));
                            List<String> coordinates = new ArrayList<>();
                            coordinates.add(String.valueOf(location.getLatitude()));
                            coordinates.add(String.valueOf(location.getLongitude()));
                            ConnectorActivity.inputUpdateWf.add(buildJsonConn(c,coordinates).toString());

                            Toast.makeText(SetConnectorActivity.this, "Weather info set on your coordinates!",
                                    Toast.LENGTH_LONG).show();
                            SetConnectorActivity.this.finish();
                        }
                    });
                }
            } else {
                // Permission has already been granted
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("ANDREA LOC:",String.valueOf(location.getLatitude())
                                +" "+String.valueOf(location.getLongitude()));
                        List<String> coordinates = new ArrayList<>();
                        coordinates.add(String.valueOf(location.getLatitude()));
                        coordinates.add(String.valueOf(location.getLongitude()));
                        ConnectorActivity.inputUpdateWf.add(buildJsonConn(c,coordinates).toString());

                        Toast.makeText(SetConnectorActivity.this, "Weather info set on your coordinates!",
                                Toast.LENGTH_LONG).show();
                        SetConnectorActivity.this.finish();
                    }
                });
            }

            return true;
        }
        return false;
    }

    public void setFields() {
        Log.i("ANDREANUMERO FIELDS",String.valueOf(fieldList.size()));

        for(String s: fieldList) {
            mAdapter.notifyItemInserted(0);
        }
    }

    //aggiunge o modifica parametri connettore e li salva in una lista
    // utilizzata da connectorActivity
    public void saveAllParameters(View view) {
        List <String> pList = mAdapter.retrieveParameters();
        if (!pList.isEmpty()) {
            try {
                //creo JSON del connettore
                JSONObject jsonObject = buildJsonConn(connector,pList);
                Log.i("ANDREA JSON", jsonObject.toString(1));

                connector.setBeenSet(true);
                cnSetted=true;
                //lo salvo nella lista inputUpdateWf
                ConnectorActivity.inputUpdateWf.add(jsonObject.toString());
                Log.i("ANDREA UPDATE", ConnectorActivity.inputUpdateWf.toString());

                //sostituisco i parametri del connettore vecchio se presente
                //TODO: aggiornamento connettori
            /*if(ConnectorActivity.inputUpdateWf.get(connector.getPosition()+1)!=null)
                ConnectorActivity.inputUpdateWf.remove(connector.getPosition()+1);*/
            } catch (JSONException e) {
                Log.i("ANDREA JSON", e.getMessage());
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cnSetted", cnSetted);
            resultIntent.putExtra("cn",connector);
            setResult(Activity.RESULT_OK, resultIntent);
            SetConnectorActivity.this.finish();
        }
        else
            Toast.makeText(SetConnectorActivity.this,
                    "Please provide the fields with some input",Toast.LENGTH_LONG).show();
    }

    public JSONObject buildJsonConn(Connector c, List<String> pL) {
        Map<String, Object> connMap = new HashMap<>();
        connMap.put("action",c.getAction());
        connMap.put("params",pL);
        JSONObject jsonObject = new JSONObject(connMap);
        return jsonObject;
    }
    //esempio formato json parametri
    //{\"actions_records\":[{\"action\":\"tv_schedule\",\"params\":[\"cielo\",\"19:00\"]}]}
}
