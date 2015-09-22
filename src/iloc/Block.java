/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iloc;

import edu.utsa.tl13.TL13Grammers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Rehan
 */
public class Block implements Comparable<Block>, Serializable {

    public static ArrayList<Block> blockList = new ArrayList();
    public boolean visitedForBlock = false;
    public boolean visitedForCFG = false;
    public boolean visitedForMIPS = false;
    public boolean visitedForIns = false;
    public boolean visitedForDom = false;
    public boolean visited = false;
    public boolean visited1 = false;
    public String blockID;
    public ArrayList<ILOCInstruction> instructions = new ArrayList();
    public ArrayList<Block> childBlock = new ArrayList();
    public ArrayList<Block> predBlock = new ArrayList();

    public Block() {
        TL13Grammers.blockCount++;
        this.blockID = "B" + TL13Grammers.blockCount;

        // add me to blcok list
        blockList.add(null);
        blockList.add(TL13Grammers.blockCount, this);
    }

    public Block(String blockID) {
        this.blockID = blockID;
    }

    public void addInstruction(ILOCInstruction inst) {
        this.instructions.add(inst);
    }

    public boolean addInstruction(int ID, ILOCInstruction inst) {
        /*
         * if (this.visitedForIns == true) { return false; } this.visitedForIns = true; String blockId = "B" + ID;
         *
         * if (this.blockID.equalsIgnoreCase(blockId)) { this.addInstruction(inst); return true; }
         *
         * Iterator itr = this.childBlock.iterator(); while (itr.hasNext()) { Block curr = (Block) itr.next(); if (curr.blockID.equalsIgnoreCase(blockId)) {
         * curr.addInstruction(inst); return true; } if (curr.addInstruction(ID, inst)) { return true; }
        }
         */
        blockList.get(ID).addInstruction(inst);
        return true;
    }

    public void addChildBlock(Block block) {
        block.predBlock.add(this);
        this.childBlock.add(block);
    }

    public boolean addChildBlock(int ID, Block block) {
        /*
         * if (this.visitedForBlock == true) { return false; } this.visitedForBlock = true; String blockId = "B" + ID;
         *
         * if (this.blockID.equalsIgnoreCase(blockId)) { this.addChildBlock(block); return true; }
         *
         * Iterator itr = this.childBlock.iterator(); while (itr.hasNext()) { Block curr = (Block) itr.next(); System.out.print(curr.blockID + " "); if
         * (curr.blockID.equalsIgnoreCase(blockId)) { curr.addChildBlock(block); return true; } if (curr.addChildBlock(ID, block)) { return true; }
        }
         */
        block.predBlock.add(blockList.get(ID));
        blockList.get(ID).childBlock.add(block);
        return true;
    }

    public void genCFG() {
        if ((this.blockID.equalsIgnoreCase("entry")) || (this.blockID.equalsIgnoreCase("exit"))) {
            System.out.println(this.blockID);
        } else {
            System.out.println(this.blockID);
            Iterator itr = this.instructions.iterator();
            while (itr.hasNext()) {
                ILOCInstruction inst = (ILOCInstruction) itr.next();
                inst.printILOC();
            }
        }

        Iterator itr = this.childBlock.iterator();
        while (itr.hasNext()) {
            Block curr = (Block) itr.next();
            curr.genCFG();
        }
    }

    public Block getCurrBlock(int ID) {

        String blockId = "B" + ID;
        Iterator itr = this.childBlock.iterator();
        while (itr.hasNext()) {
            Block curr = (Block) itr.next();
            if (curr.blockID.equalsIgnoreCase(blockId)) {
                return curr;
            }
            curr.getCurrBlock(ID);
        }
        return this;
    }

    @Override
    public int compareTo(Block o) {
        int blockId1 = Integer.parseInt(this.blockID.substring(1, this.blockID.length()));
        int blockId2 = Integer.parseInt(o.blockID.substring(1, o.blockID.length()));
        return blockId1 - blockId2;
    }
}
