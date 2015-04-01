package SymbolTable;

import java.util.Enumeration;
import java.util.Hashtable;

public class SymbolTable {
    private Hashtable table;

    public SymbolTable(int size){
        this.table = new Hashtable(size);
    }

    public Object lookup(String toLookup){

        if(table.containsKey(toLookup)){
            return table.get(toLookup);
        }
        else{
            return null;
        }
    }

    public Object insert(String toInsert){
        table.put(toInsert,null);
        return table.get(toInsert);
    }

    public void dumpTable(){
        //table key values will be used to reference the table's key-value pairs
        Enumeration tableValues = table.keys();
        while(tableValues.hasMoreElements()) {
            //get the next variable in the table
            String identifier = (String)tableValues.nextElement();
            //get the value of the variable
            Object identifierValue = table.get(identifier);
            System.out.println("identifier:" + identifier + " : " + identifierValue);
        }

    }
}
