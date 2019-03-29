package com.example.testcognito;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connector implements Serializable {

    private String cnName;
    private String cnType;
    private String cnDef;
    private Integer paramNumber;
    //TODO: list?
    private List<String> paramFields;
    private String param1;
    private String param2;
    private String param3;

    public static class ConnectorBuilder {
        private String cnName;
        private String cnType;
        private String cnDef;
        private Integer paramNumber;
        //TODO: list?
        private List<String> paramFields= new ArrayList<>();
        private String param1;
        private String param2;
        private String param3;


        public Connector build() {
            return new Connector(cnName,cnType,cnDef,paramFields,paramNumber,param1,param2,param3);
        }
        public ConnectorBuilder name(String name) {
            this.cnName = name;
            return this;
        }

        public ConnectorBuilder type(String type) {
            this.cnType = type;
            return this;
        }

        public ConnectorBuilder definition(String def) {
            this.cnDef = def;
            return this;
        }

        public ConnectorBuilder paramNumber(Integer number) {
            this.paramNumber = number;
            return this;
        }

        public ConnectorBuilder addField(String field) {
            this.paramFields.add(field);
            return this;
        }

        public ConnectorBuilder parameter1(String param) {
            this.param1 = param;
            return this;
        }

        public ConnectorBuilder parameter2(String param) {
            this.param2 = param;
            return this;
        }

        public ConnectorBuilder parameter3(String param) {
            this.param3 = param;
            return this;
        }
    }
    private Connector(String cnName, String cnType, String cnDef, List<String> paramFields, Integer paramNumber, String param1, String param2, String param3) {
        this.cnName=cnName;
        this.cnType=cnType;
        this.cnDef=cnDef;
        this.paramFields=paramFields;
        this.paramNumber=paramNumber;
        this.param1=param1;
        this.param2=param2;
        this.param3=param3;
    }




    @Override
    public String toString() {
        return cnName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Connector))
            return false;

        Connector cn = (Connector) obj;
        return cn.cnName.equals(cnName) && cn.cnDef.equals(cnDef) && cn.cnType.equals(cnType);
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + cnName.hashCode();
        res = 31 * res + cnDef.hashCode();
        return res;
    }

    public String getType() {
        return cnType;
    }

    public String getName() {
        return cnName;
    }

    public List<String> getFields() { return paramFields;}

    public Integer getParamNumber() {return paramNumber;}
}