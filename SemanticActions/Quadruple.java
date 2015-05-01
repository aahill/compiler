package SemanticActions;

import java.util.Vector;

public class Quadruple {
    private Vector<String[]> quadruple;
    private int nextQuad;

    public Quadruple(){
    this.quadruple = new Vector<>();
    }


    //Get contents of field in Quad at location index
    public String getField(int quadIndex, int field){
        String[] quadStringArray = this.quadruple.elementAt(quadIndex);
        return quadStringArray[field];
    }

    //Set contents of field in Quad at location index
    public void setField(int quadIndex, int index, String field){
        String[] quadStringArray = this.quadruple.elementAt(quadIndex);
        quadStringArray[index] = field;
    }

    //Return index of next available quadruple
    public int getNextQuad(){
        return nextQuad;
    }

    //Increment the quad index variable
    public void incrementNextQuad(){
        nextQuad += 1;
    }

    //Return the quadruple at index
    public String[] getQuad(int index){
        return this.quadruple.elementAt(index);
    }

    //Add a quadruple to the Quad array
    public void addQuad(String[] quad){
        this.quadruple.add(quad);
    }

    //Print the Quadruple array
    public void print(){
        int quadIndex = 0;
        System.out.println();
        for(String[] quad: this.quadruple) {
            System.out.print(quadIndex+ " ");
            for(String field : quad) {
                System.out.print(field + " ");
            }
            System.out.println();
            quadIndex += 1;
        }
    }
}
