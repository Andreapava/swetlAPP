package com.example.testcognito;

public class Connector {

    private String cnName;

    private String cnDef;

    public Connector(String connectorName, String connectorURL) {
        this.cnName = connectorName;
        this.cnDef = connectorURL;
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
        return cn.cnName.equals(cnName) && cn.cnDef.equals(cnDef);
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + cnName.hashCode();
        res = 31 * res + cnDef.hashCode();
        return res;
    }

}