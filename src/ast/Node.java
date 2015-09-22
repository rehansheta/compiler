/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import iloc.Block;

/**
 *
 * @author Rehan
 */
public abstract class Node {
    
    public String value;
    public String type;
    
    /*********code for ILOC*************/
    public Block body;
    public Block begin;
    public Block after;
    public String place;    // register that holds the result
    /***********************************/
    
    public abstract void print(int nodeId);
    public abstract void addChild(Node child);
    public abstract String typeCheck();
    public abstract void genILOC(Block body);
    //public abstract void genMIPS(Block body);
}
