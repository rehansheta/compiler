/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssa;

import iloc.Block;
import iloc.ILOCInstruction;
import java.util.ArrayList;

/**
 *
 * @author Rehan
 */
public class DominFrontier {
    public static ArrayList<ArrayList<DTBlock>> DFTable;

    public DominFrontier() {
        DFTable = new ArrayList();
    }
    
   public void printDF() {
        ArrayList<DTBlock> DF = new ArrayList();

        for (int i = 0; i < DFTable.size(); i++) {
            System.out.print("DF(B" + (i + 1) + "): { ");
            DF = DFTable.get(i);
            for (int j = 0; j < DF.size(); j++) {
                DTBlock currentBlock = DF.get(j);
                System.out.print(currentBlock.CFGBlock.blockID + " ");
            }
            System.out.println("}");
        }
        System.out.println();
    }
    
    public void genDomFrontier() {
        for (int i = 0; i < DominatorTree.blockNode.size(); i++) {
            DFTable.add(i, new ArrayList());
        }
        
        //System.out.println("DominatorTree.blockNode.size(): " + DominatorTree.blockNode.size());
        for (int i = 0; i < DominatorTree.blockNode.size(); i++) {
            DTBlock currBlock = DominatorTree.blockNode.get(i);
            if (currBlock.CFGBlock.predBlock.size() > 1) {
                ArrayList<Block> predList = currBlock.CFGBlock.predBlock;
                for (int j = 0; j < predList.size(); j++) {
                    DTBlock runnerBlock = new DTBlock();
                    runnerBlock.CFGBlock = ((Block) DeepCopy.copy(predList.get(j)));
                    runnerBlock.SSAinstructions = (ArrayList<ILOCInstruction>) DeepCopy.copy(predList.get(j).instructions);
                
                    int currBlockId = Integer.parseInt(currBlock.CFGBlock.blockID.substring(1, currBlock.CFGBlock.blockID.length()));
                    DTBlock IDOMn = DominatorTree.IDOMTable.get(currBlockId - 1);
                    
                    while (!runnerBlock.CFGBlock.blockID.equalsIgnoreCase(IDOMn.CFGBlock.blockID)) {
                        int runnerId = Integer.parseInt(runnerBlock.CFGBlock.blockID.substring(1, runnerBlock.CFGBlock.blockID.length()));
                        DFTable.get(runnerId - 1).add(currBlock);
                        runnerBlock = DominatorTree.IDOMTable.get(runnerId - 1);
                    }
                }
            }
        }
        printDF();
    }
}
