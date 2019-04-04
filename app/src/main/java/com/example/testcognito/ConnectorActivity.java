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
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.UpdateUserMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.example.testcognito.Connector;

import java.util.ArrayList;
import java.util.List;

import type.UpdateUserInput;
import type.WorkflowInput;

public class ConnectorActivity extends AppCompatActivity {

    private ArrayList<Connector> mConnectors = new ArrayList<>();

    private ArrayList<Connector> mActiveConnectors = new ArrayList<>();

    public static List<String> inputUpdateWf = new ArrayList<>();

    private RecyclerView mRecyclerViewAvailable;

    private RecyclerView mRecyclerViewActive;

    private ActiveConnectorRecycleViewAdapter mActiveConnectorAdapter =
            new ActiveConnectorRecycleViewAdapter(mActiveConnectors, this);

    private AvailableConnectorRecycleViewAdapter mAvailableConnectorAdapter =
            new AvailableConnectorRecycleViewAdapter(mConnectors, this);


    private int currentWfPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connector);
        Log.i("ANDREA oncreate", inputUpdateWf.toString());
        //RecyclerView setup - available connectors
        mRecyclerViewAvailable = findViewById(R.id.availableConnectorsRecycle);
        mRecyclerViewAvailable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewAvailable.setAdapter(mAvailableConnectorAdapter);

        //RecyclerView setup - active connectors
        mRecyclerViewActive = findViewById(R.id.activeConnectorsRecycle);
        mRecyclerViewActive.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewActive.setAdapter(mActiveConnectorAdapter);

        currentWfPos = getIntent().getIntExtra("currentWfpos",0);
        Log.i("Current WF: ", String.valueOf(currentWfPos));
        setConnectors();
    }

    public void setConnectors() {
        // 1. CONNECTOR - Feed RSS
        Connector feedRSS = new Connector.ConnectorBuilder()
                .name("Feed RSS")
                .action("read_feed")
                .addField("URL")
                .addField("URL2")
                .build();
        mConnectors.add(feedRSS);
        mAvailableConnectorAdapter.notifyItemInserted(mConnectors.indexOf(feedRSS));

        // 2. CONNECTOR - Custom message
        Connector message = new Connector.ConnectorBuilder()
                .name("Message")
                .action("custom_message")
                .addField("Message body")
                .build();
        mConnectors.add(message);
        mAvailableConnectorAdapter.notifyItemInserted(mConnectors.indexOf(feedRSS));

        //Set the "SAVE EDITS" button as not visible
        findViewById(R.id.buttonSaveConnectors).setVisibility(View.GONE);
    }

    public void addConnectorToActive(Connector cn) {
        mActiveConnectors.add(cn);
        mActiveConnectorAdapter.notifyItemInserted(mActiveConnectors.indexOf(cn));
        //salvo nel connettore la sua posizione nella lista di connettori attivi
        cn.setPosition(mActiveConnectors.indexOf(cn));
        //aggiungo stringa vuota per non creare null ref quando la sostituirò
        //con l' effettiva stringa
        inputUpdateWf.add("");
    }

    //TODO: rimozione connettori
    public void removeConnectorFromActive(Connector cn) {
        inputUpdateWf.remove(mActiveConnectors.indexOf(cn));
        mActiveConnectors.remove(cn);
        mActiveConnectorAdapter.notifyItemRemoved(mActiveConnectors.indexOf(cn));
    }

    public void setActiveConnector(Connector cn) {
        Intent intent = new Intent(ConnectorActivity.this, SetConnectorActivity.class);
        intent.putExtra("connector",cn);
        ConnectorActivity.this.startActivity(intent);
    }

    //query di update per wf corrente
    //TODO: callback
    public void saveEdit(View view) {
        UpdateUserInput input = UpdateUserInput.builder()
                .id(AWSMobileClient.getInstance().getUsername())
                .workflow(updateWfDefinition())
                .build();
        UpdateUserMutation updateUserMutation = UpdateUserMutation.builder()
                .input(input)
                .build();
        com.example.testcognito.ClientFactory.appSyncClient().mutate(updateUserMutation).enqueue(null);
    }

    //aggiorna il workflow corrente con i connettori settati e restituisce la lista di workflow
    //è più un "copia modifica e sostituisci" wf
    public List<WorkflowInput> updateWfDefinition() {
        WorkflowInput aux = MainActivity.wfList.get(currentWfPos);
        WorkflowInput toUpdate = WorkflowInput.builder()
                .idwf(aux.idwf())
                .def(inputUpdateWf.toString())
                .name(aux.name())
                .build();

        MainActivity.wfList.add(currentWfPos,toUpdate);

        if(MainActivity.wfList.get(currentWfPos+1)!=null) {
            MainActivity.wfList.remove(currentWfPos+1);
        }
        Log.i("ANDREA UPDATE", inputUpdateWf.toString());
        return MainActivity.wfList;
    }
}