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
public class DominatorTree {

    public static ArrayList<DTBlock> blockNode;
    public static ArrayList<DTBlock> IDOMTable;

    public DominatorTree() {
        blockNode = new ArrayList();
        IDOMTable = new ArrayList();
    }

    public void printIDOM() {
        for (int i = 0; i < IDOMTable.size(); i++) {
            System.out.println("IDOM(B" + (i + 1) + "): { " + IDOMTable.get(i).CFGBlock.blockID + " }");
        }
        System.out.println();
    }

    public void genDomTree() {

        Block b = Block.blockList.get(1);
        b.visited1 = true;

        DTBlock root = new DTBlock();
        root.CFGBlock = (Block) DeepCopy.copy(Block.blockList.get(1));
        root.SSAinstructions = (ArrayList<ILOCInstruction>) DeepCopy.copy(root.CFGBlock.instructions);
        blockNode.add(root);
        
        IDOMTable.add(0, new DTBlock());  // IDOM is empty for the root
        IDOMTable.get(0).CFGBlock = new Block(null);
        
        for (int outer = 2; outer < Dominance.DOMTable.size(); outer++) {
            DTBlock currentBlock = new DTBlock();
            DTBlock save = currentBlock;

            for (int i = outer; i < Dominance.DOMTable.size(); i++) {
                currentBlock.CFGBlock = (Block) DeepCopy.copy(Block.blockList.get(i));
                currentBlock.SSAinstructions = (ArrayList<ILOCInstruction>) DeepCopy.copy(currentBlock.CFGBlock.instructions);
                ArrayList<Block> IDOMorig = Dominance.DOMTable.get(i);
                for (int j = 0; j < IDOMorig.size(); j++) {
                    Block currentDom = (Block) DeepCopy.copy(IDOMorig.get(j));
                    if (currentDom.blockID.equalsIgnoreCase(root.CFGBlock.blockID) && currentBlock.CFGBlock.visited1 == false) {
                        root.addDTChildBlock(currentBlock);
                        currentBlock.addDTPredBlock(root);
                        blockNode.add(currentBlock);
                        
                        int currBlockId = Integer.parseInt(currentBlock.CFGBlock.blockID.substring(1, currentBlock.CFGBlock.blockID.length()));
                        IDOMTable.add(currBlockId - 1, root);
                        currentBlock.CFGBlock.visited1 = true;
                        Block.blockList.get(i).visited1 = true;
                        break;
                    }
                }
                currentBlock = new DTBlock();
            }
            root = save;
        }

        printIDOM();
    }
}
