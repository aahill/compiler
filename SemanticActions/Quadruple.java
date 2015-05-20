package SemanticActions;

import java.util.Vector;

public class Quadruple {
    private Vector<String[]> quadruple;
    private int nextQuad;

    public Quadruple(){
        this.quadruple = new Vector<>();
        this.nextQuad = 0;
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
        int quadIndex = 1;
        System.out.println();
        //the first element in the quadruples array is always the CODE quad
        System.out.println(this.quadruple.get(0)[0]);
        for(int i = 1; i < this.quadruple.size(); i++) {
            String[] quad = quadruple.get(i);
            System.out.print(quadIndex + ": ");
            for(int i2 = 0; i2 < quad.length - 1; i2++){
                if(!quad[i2+1].equals("")) {
                    System.out.print(quad[i2] + ", ");
                }
                else{
                    System.out.print(quad[i2]);
                }
            }
            System.out.print(quad[quad.length-1]);
            System.out.println();
            quadIndex += 1;
        }
    }
}
