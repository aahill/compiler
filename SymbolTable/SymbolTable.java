package SymbolTable;

import java.util.Enumeration;
import java.util.Hashtable;

public class SymbolTable {
    private Hashtable<String, SymbolTableEntry> table;

    //default constructor for creating sized hashtable
    public SymbolTable(int size){
        this.table = new Hashtable<>(size);
    }

    /**
     * searches for entry in the table by looking for the name value of the entry.
     * @param entryName the name 'key' used to find the entry
     * @return a reference to the entry if found, otherwise null
     */
    public SymbolTableEntry lookup(String entryName){

        if(table.containsKey(entryName)){
            return table.get(entryName);
        }
        else{
            return null;
        }
    }

    /**
     * inserts a table entry into the symbol table
     * @param entry the SymbolTableEntry to insert
     * @return a reference to the newly inserted entry
     */
    public SymbolTableEntry insert(SymbolTableEntry entry){
        table.put(entry.getName(),entry);
        return table.get(entry.getName());
    }

    /**
     * prints the entries in the symbol table by creating an enumeration of the key-value pairs, and iteratively
     * printing them out to the console
     */
    public void dumpTable(){
        //table key values will be used to reference the table's key-value pairs
        Enumeration tableValues = table.keys();
        while(tableValues.hasMoreElements()) {
            //get the next variable in the table
            String identifier = (String)tableValues.nextElement();
            //get the value of the variable
            SymbolTableEntry identifierValue = table.get(identifier);
            //System.out.println("identifier:" + identifier + " : " + identifierValue);
            identifierValue.print();

        }

    }
}
