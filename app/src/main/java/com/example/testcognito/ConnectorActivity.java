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

import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.amplify.generated.graphql.UpdateUserMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.testcognito.Connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

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
        retrieveUserConnectors();
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
        cn.setPosition(mActiveConnectorAdapter.getItemCount());

        //cn.setPosition(mActiveConnectors.indexOf(cn));
        Log.i("ANDREA POS",String.valueOf(cn.getPosition()));

        Intent intent = new Intent(ConnectorActivity.this, SetConnectorActivity.class);
        intent.putExtra("connector",cn);
        ConnectorActivity.this.startActivity(intent);

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
                .def(createWFdef(inputUpdateWf).toString())
                .name(aux.name())
                .build();

        MainActivity.wfList.add(currentWfPos,toUpdate);

        if(MainActivity.wfList.get(currentWfPos+1)!=null) {
            MainActivity.wfList.remove(currentWfPos+1);
        }
        Log.i("ANDREA UPDATE", inputUpdateWf.toString());
        return MainActivity.wfList;
    }

    public JSONObject createWFdef(List<String> list) {
        Map<String, Object> m = new HashMap<>();
        m.put("actions_records",list);
        JSONObject j = new JSONObject(m);
        return j;
    }

    public void retrieveUserConnectors () {
        com.example.testcognito.ClientFactory.appSyncClient()
                .query(GetUserQuery.builder().id(AWSMobileClient.getInstance().getUsername()).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(getConnListCallback);
    }

    private GraphQLCall.Callback<GetUserQuery.Data> getConnListCallback = new GraphQLCall.Callback<GetUserQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetUserQuery.Data> response) {
            if(response!=null && response.data()!=null && response.data().getUser()!=null) {
                if (response.data().getUser().workflow() != null) {
                    List<Connector> auxWfList = new ArrayList<>();
                    for(GetUserQuery.Workflow w : response.data().getUser().workflow())
                    {
                        if(w.idwf().equals(MainActivity.wfList.get(currentWfPos).idwf())) {
                            Log.i("ANDREA FINDWF", w.def());
                            try {
                                JSONObject j = new JSONObject(w.def());
                                JSONArray jsonArray = j.getJSONArray("actions_records");
                                Log.i("ANDREA JSONARRAY", jsonArray.toString(1));
                            } catch (JSONException e)
                            {
                                Log.i("ANDREA JSON",e.getMessage());
                            }
                        }
                    }
                  /*  wfList=auxWfList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActiveConnectorAdapter.setItems(wfList);
                            mActiveConnectorAdapter.notifyDataSetChanged();
                        }
                    });*/
                }
            }
        }
        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("ERROR", e.toString());
        }
    };

}