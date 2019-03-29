package com.example.testcognito;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SetConnectorActivity extends AppCompatActivity {
    
    private List<String> paramList = new ArrayList<>();
    
    private RecyclerView mRecyclerView ;
    
    private ConnParametersRecycleViewAdapter mAdapter;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_connector);
        Connector connector= (Connector) getIntent().getSerializableExtra("connector");

        paramList = connector.getFields();

        mAdapter = new ConnParametersRecycleViewAdapter(paramList,this);

        //RecyclerView setup - available connectors
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
        
        setFields();
    }
    
    public void setFields() {
        Log.i("NUMERO FIELDS",String.valueOf(paramList.size()));

        for(String s: paramList) {
            mAdapter.notifyItemInserted(0);
        }
    }

    public void saveAllParameters(View view) {
        List <String> pList = mAdapter.retrieveParameters();
        Log.i("edittext=",pList.toString());
        SetConnectorActivity.this.finish();
    }

}
