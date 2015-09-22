/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssa;

import edu.utsa.tl13.TL13Grammers;
import iloc.Block;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rehan
 */
public class Dominance {

    private int blockCount;
    public static ArrayList<ArrayList<Block>> DOMTable;
    

    public Dominance() {
        this.blockCount = TL13Grammers.blockCount;
        DOMTable = new ArrayList();
        
    }

    public void printDominance(int funcBlockCount) {
        ArrayList<Block> DOM = new ArrayList();

        System.out.println("\n");
        for (int i = 1; i <= funcBlockCount; i++) {
            System.out.print("DOM(B" + i + "): { ");
            DOM = DOMTable.get(i);
            for (int j = 0; j < DOM.size(); j++) {
                Block currentBlock = DOM.get(j);
                System.out.print(currentBlock.blockID + " ");
            }
            System.out.println("}");
        }
        System.out.println();
    }

    private void printList(List a) {

        for (int i = 0; i < a.size(); i++) {
            System.out.print("-" + ((Block) a.get(i)).blockID + " ");
        }
        System.out.println("");
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static boolean equalLists(List<Block> one, List<Block> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        one = new ArrayList(one);
        two = new ArrayList(two);

        Collections.sort(one);
        Collections.sort(two);

        return one.equals(two);
    }

    public void genDOMSet() {
        boolean changed = true;
        ArrayList<Block> DOM = new ArrayList();

        int funcBlockCount = 0;
        for (int i = 1; i < Block.blockList.size() && Block.blockList.get(i) != null; i++) {
            funcBlockCount++;
        }

        DOMTable.add(0, new ArrayList());  // useless
        DOMTable.add(1, new ArrayList());  // empty list

        DOMTable.get(1).add(Block.blockList.get(1));

        for (int i = 2; i <= funcBlockCount; i++) {
            DOMTable.add(i, new ArrayList());
            for (int j = 1, k = 0; j <= funcBlockCount; j++, k++) {
                ArrayList tempDomSet = DOMTable.get(i);
                tempDomSet.add(Block.blockList.get(j));
            }
        }

        while (changed) {
            changed = false;

            for (int i = 2; i <= funcBlockCount; i++) {
                Block currentBlock = Block.blockList.get(i);
                ArrayList<Block> predBlockList = currentBlock.predBlock;

                ArrayList<Block> intersectionResult = new ArrayList();

                Block predBlock = predBlockList.get(0);
                String s = predBlock.blockID.substring(1, predBlock.blockID.length());
                int blockId = Integer.parseInt(s);
                intersectionResult = (ArrayList<Block>) DOMTable.get(blockId).clone();

                for (int j = 1; j < predBlockList.size(); j++) {
                    predBlock = predBlockList.get(j);
                    s = predBlock.blockID.substring(1, predBlock.blockID.length());
                    blockId = Integer.parseInt(s);
                    intersectionResult = (ArrayList<Block>) intersection((ArrayList<Block>) intersectionResult.clone(), DOMTable.get(blockId));
                }

                ArrayList<Block> temp = new ArrayList();
                ArrayList<Block> tempI = new ArrayList();
                temp.add(currentBlock);
                for (int j = 0; j < intersectionResult.size(); j++) {
                    temp.add(intersectionResult.get(j));
                    tempI.add(intersectionResult.get(j));
                }

                int currBlockId = Integer.parseInt(currentBlock.blockID.substring(1, currentBlock.blockID.length()));
                if (!equalLists(temp, DOMTable.get(currBlockId))) {
                    ArrayList a = DOMTable.get(currBlockId);
                    a.removeAll(a);
                    a.addAll(temp);
                    changed = true;
                }
            }
        }

        printDominance(funcBlockCount);

    }
}
