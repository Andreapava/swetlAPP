package com.example.testcognito;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.testcognito.Connector;

import java.util.ArrayList;
import java.util.List;

public class ConnectorActivity extends AppCompatActivity {

    private ArrayList<Connector> mConnectors = new ArrayList<>();

    private ArrayList<Connector> mActiveConnectors = new ArrayList<>();

    private RecyclerView mRecyclerViewAvailable;

    private RecyclerView mRecyclerViewActive;

    private ActiveConnectorRecycleViewAdapter mActiveConnectorAdapter =
            new ActiveConnectorRecycleViewAdapter(mActiveConnectors, this);

    private AvailableConnectorRecycleViewAdapter mAvailableConnectorAdapter =
            new AvailableConnectorRecycleViewAdapter(mConnectors, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connector);

        //RecyclerView setup - available connectors
        mRecyclerViewAvailable = findViewById(R.id.availableConnectorsRecycle);
        mRecyclerViewAvailable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewAvailable.setAdapter(mAvailableConnectorAdapter);

        //RecyclerView setup - active connectors
        mRecyclerViewActive = findViewById(R.id.activeConnectorsRecycle);
        mRecyclerViewActive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewActive.setAdapter(mActiveConnectorAdapter);

        setConnectors();
    }

    public void setConnectors() {
        // 1. CONNECTOR - Feed RSS
        Connector feedRSS = new Connector.ConnectorBuilder()
                .name("Feed RSS")
                .addField("URL")
                .addField("URL2")
                .build();
        mConnectors.add(feedRSS);
        mAvailableConnectorAdapter.notifyItemInserted(mConnectors.indexOf(feedRSS));

        // 2. CONNECTOR - More coming later...
        Connector message = new Connector.ConnectorBuilder()
                .name("Message")
                .build();
        mConnectors.add(message);
        mAvailableConnectorAdapter.notifyItemInserted(mConnectors.indexOf(feedRSS));

        //Set the "SAVE EDITS" button as not visible
        findViewById(R.id.buttonSaveConnectors).setVisibility(View.GONE);
    }

    public void addConnectorToActive(Connector cn) {
        mActiveConnectors.add(cn);
        mActiveConnectorAdapter.notifyItemInserted(mActiveConnectors.indexOf(cn));
    }

    public void removeConnectorFromActive(Connector cn) {
        mActiveConnectors.remove(cn);
        mActiveConnectorAdapter.notifyItemRemoved(mActiveConnectors.indexOf(cn));
    }

    public void setActiveConnector(Connector cn) {
        Intent addWorkflowIntent = new Intent(ConnectorActivity.this, SetConnectorActivity.class);
        addWorkflowIntent.putExtra("connector",cn);
        ConnectorActivity.this.startActivity(addWorkflowIntent);
    }

}