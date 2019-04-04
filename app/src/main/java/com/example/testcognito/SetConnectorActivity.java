package com.example.testcognito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SetConnectorActivity extends AppCompatActivity {
    
    private List<String> fieldList = new ArrayList<>();
    private List<String> paramList = new ArrayList<>();
    private Connector connector;
    private RecyclerView mRecyclerView ;
    private ConnParametersRecycleViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_connector);
        connector= (Connector) getIntent().getSerializableExtra("connector");
        fieldList = connector.getFields();
        mAdapter = new ConnParametersRecycleViewAdapter(fieldList,this);

        //RecyclerView setup - available connectors
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);

        //Mostra le view (edittext) in cui digitare i valori per i campi parametri dei connettori
        setFields();
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
        String s = "{\\\"actions_records\\\":[{\\\"action\\\":\\\""
                    +connector.getAction()
                    +formatParameters(pList);
        Log.i("ANDREAedittext=",s);

        ConnectorActivity.inputUpdateWf.add(connector.getPosition(),s);
        Log.i("ANDREA UPDATE", ConnectorActivity.inputUpdateWf.toString());
        //sostituisco i parametri del connettore vecchio se presente
        //TODO: qua  a volte succedono casini
        if(ConnectorActivity.inputUpdateWf.get(connector.getPosition()+1)!=null)
        ConnectorActivity.inputUpdateWf.remove(connector.getPosition()+1);

        SetConnectorActivity.this.finish();
    }

    //Ritorna una stringa da concatenare al JSON del connettore contenente
    // i parametri opportunemente formattati
    public String formatParameters(List<String> pList) {
        String formattedS= "\\,"+"\\params :[";
        for(int i=0;i<pList.size();i++) {

            //virgola finale o no
            if(i+1!=pList.size())
                formattedS+=pList.get(i)+"\\"+",";
            else
                formattedS+=pList.get(i)+"\\";

        }
        formattedS+="]}]}";
        return formattedS;
    }
    //esempio formato json parametri
    //{\"actions_records\":[{\"action\":\"tv_schedule\",\"params\":[\"cielo\",\"19:00\"]}]}
}
