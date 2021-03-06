package com.example.testcognito;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConnParametersRecycleViewAdapter extends RecyclerView.Adapter<ConnParametersRecycleViewAdapter.ViewHolder> {

    private final List<String> fieldList;
    public static List<String> paramList;
    private final SetConnectorActivity activity;
    private String longitude;
    private String latitude;
    private  String exParams;

    public ConnParametersRecycleViewAdapter(List<String> fieldList, SetConnectorActivity activity, String exParams) {
        this.fieldList = fieldList;
        paramList= new ArrayList<>();
        this.activity = activity;
        this.exParams=exParams;
    }

    @Override
    public ConnParametersRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parameter_row, parent, false);

        return new ConnParametersRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConnParametersRecycleViewAdapter.ViewHolder holder, int position) {

        //set the name of the field the user has to provide
        holder.fieldName.setText(fieldList.get(position));

        if(longitude!=null && latitude!= null){
            if(position==0)
            holder.editText.setText(longitude);
            else
                holder.editText.setText(latitude);
        }

        if(exParams!=null)
        holder.editText.setText(extractParam(exParams,position));


    }

    //estrae i parametri da produrre negli edittext
    public String extractParam(String allParams, int position) {
        try {
            JSONObject o = new JSONObject(allParams);
            JSONArray Jarray = o.getJSONArray("params");
            return Jarray.getString(position);
        }catch(JSONException JSONe) {
            Log.i("ANDREA JSONEXCPT",JSONe.getMessage());
        }
        return "";
    }

    public void setLocation(double lon, double lat) {
        longitude=String.valueOf(lon);
        latitude=String.valueOf(lat);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View parentView;
        public final TextView fieldName;
        public EditText editText;
        public String parameter;

        public ViewHolder(View itemView) {

            super(itemView);

            this.editText = itemView.findViewById(R.id.editTxt_name);
            this.parentView = itemView;
            this.fieldName = itemView.findViewById(R.id.field_name);

            
            //save all parameters of the connector in List paramList
            this.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(paramList.isEmpty() || getAdapterPosition()+1>paramList.size()) {
                        paramList.add(editText.getText().toString());
                    }
                    else
                    {
                        paramList.set(getAdapterPosition(),editText.getText().toString());
                    }
                    Log.i("PARAMLIST: ", paramList.toString());
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + fieldName + "'";
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {

        return fieldList.size();
    }

    public List<String> retrieveParameters()
    {
        return paramList;
    }

}
