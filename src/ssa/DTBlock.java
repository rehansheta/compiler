/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssa;

import iloc.Block;
import iloc.ILOCInstruction;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rehan
 */
public class DTBlock implements Comparable<DTBlock>, Serializable{
    public ArrayList<DTBlock> DTChildBlock;
    public ArrayList<DTBlock> DTPredBlock;
    public ArrayList<String> definationList;
    public ArrayList<String> UsageList;
    public Block CFGBlock;
    public ArrayList<String> visitedForPhi;
    public boolean visited = false;
    public boolean visitedForSSA = false;
    public boolean visitedForSSAOut = false;
    public ArrayList<ILOCInstruction> SSAinstructions = new ArrayList();

    public DTBlock() {
        DTChildBlock = new ArrayList();
        DTPredBlock = new ArrayList();
        definationList = new ArrayList();
        UsageList = new ArrayList();
        visitedForPhi = new ArrayList();
    }
    
    public void addDTChildBlock(DTBlock block) {
        this.DTChildBlock.add(block);
    }
    
    public void addDTPredBlock(DTBlock block) {
        this.DTPredBlock.add(block);
    }

    @Override
    public int compareTo(DTBlock o) {
        int blockId1 = Integer.parseInt(CFGBlock.blockID.substring(1, CFGBlock.blockID.length()));
        int blockId2 = Integer.parseInt(o.CFGBlock.blockID.substring(1, o.CFGBlock.blockID.length()));
        return blockId1 - blockId2;
    }
    
    public void genDefineVarList() {
        for (int i = 0; i < CFGBlock.instructions.size(); i++) {
            ILOCInstruction inst = CFGBlock.instructions.get(i);
            if (inst.destOperand != null) {
                definationList.add(inst.destOperand);
            }
        }
    }
    
    public void genUsageVarList() {
        for (int i = 0; i < CFGBlock.instructions.size(); i++) {
            ILOCInstruction inst = CFGBlock.instructions.get(i);
            if (inst.src1Operand != null) {
                UsageList.add(inst.src1Operand);
            }
            if (inst.src2Operand != null) {
                UsageList.add(inst.src2Operand);
            }
        }
    }
    
}
