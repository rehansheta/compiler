/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssa;

import ast.SymbleTable;
import iloc.Block;
import iloc.ILOCConstants;
import iloc.ILOCInstruction;
import java.util.*;

/**
 *
 * @author Rehan
 */
public class SSA {

    public static HashMap<String, ArrayList<DTBlock>> varBlockList;
    public String[] varList;
    public static HashMap<String, Integer> counter;
    public static HashMap<String, Stack<Integer>> stack;

    public SSA() {
        varBlockList = new HashMap();
        counter = new HashMap();
        stack = new HashMap();

    }

    public void getGlobalVarList() {
        Collection<String> list = SymbleTable.symTableType.keySet();
        varList = new String[SymbleTable.symTableType.size()];
        list.toArray(varList);
//        for (int i = 0; i < varList.length; i++) {
//            System.out.println(varList[i]);
//        }
    }

    public void setVarInfo() {
        for (int j = 0; j < DominatorTree.blockNode.size(); j++) {
            DTBlock currBlock = DominatorTree.blockNode.get(j);
            currBlock.genDefineVarList();
            currBlock.genUsageVarList();
        }
    }

    public ArrayList<DTBlock> getBlockForVar(String currVar) {
        ArrayList<DTBlock> currlist = varBlockList.get(currVar);
        return currlist;
    }

    public void putBlocksForVars() {

        for (int i = 0; i < varList.length; i++) {
            String currVar = varList[i];
            for (int j = 0; j < DominatorTree.blockNode.size(); j++) {
                DTBlock currBlock = DominatorTree.blockNode.get(j);
                if (currBlock.definationList.contains("r_" + currVar)) {
                    if (!varBlockList.containsKey(currVar)) {
                        ArrayList<DTBlock> initBlock = new ArrayList();
                        initBlock.add(currBlock);
                        varBlockList.put(currVar, initBlock);
                    } else {
                        varBlockList.get(currVar).add(currBlock);
                    }
                }
            }
        }
    }

    public void putBlockForVar(String currVar, DTBlock currBlock) {
        ArrayList<DTBlock> list = varBlockList.get(currVar);
        if (!list.contains(currBlock)) {
            list.add(currBlock);
        }
    }

    public void initSSA() {
        this.setVarInfo();
        this.getGlobalVarList();
        this.putBlocksForVars();
    }

    public void insertPhiFunction() {
        for (int i = 0; i < varList.length; i++) {
            String currVar = varList[i];
            ArrayList<DTBlock> workList = getBlockForVar(currVar);

            for (int j = 0; j < workList.size(); j++) {
                DTBlock b = workList.get(j);
                int dfIndex = Integer.parseInt(b.CFGBlock.blockID.substring(1, b.CFGBlock.blockID.length()));
                for (int k = 0; k < DominFrontier.DFTable.get(dfIndex - 1).size(); k++) {
                    DTBlock d = DominFrontier.DFTable.get(dfIndex - 1).get(k);
                    if (!d.visitedForPhi.contains(currVar)) {
                        d.visitedForPhi.add(currVar);
                        ILOCInstruction phiInst = new ILOCInstruction();
                        phiInst.opCode = ILOCConstants.PHIFUNC;
                        phiInst.src1Operand = "r_" + currVar;
                        phiInst.src2Operand = "r_" + currVar;
                        phiInst.destOperand = "r_" + currVar;
                        d.CFGBlock.instructions.add(k, phiInst);
                        d.SSAinstructions.add(k, (ILOCInstruction)DeepCopy.copy(phiInst));
                        workList.add(d);
                        putBlockForVar(currVar, d);
                    }
                }
            }
        }
    }

    public String newName(String var) {
        int i = counter.get(var);
        counter.put(var, (i + 1));
        Stack s = stack.get(var);
        s.push(i);
        stack.put(var, s);
        return (var + i);
    }

    public boolean containsVar(String var) {
        for (int i = 0; i < varList.length; i++) {
            if (var.equalsIgnoreCase("r_" + varList[i])) {
                return true;
            }
        }
        return false;
    }

    public void renameVariables(DTBlock b) {
        for (int i = 0; i < b.visitedForPhi.size(); i++) {
            ILOCInstruction phiInst = b.CFGBlock.instructions.get(i);
            ILOCInstruction SSAphiInst = b.SSAinstructions.get(i);
            SSAphiInst.destOperand = newName(phiInst.destOperand);
        }

        for (int i = 0; i < b.CFGBlock.instructions.size(); i++) {
            ILOCInstruction inst = b.CFGBlock.instructions.get(i);
            ILOCInstruction SSAinst = b.SSAinstructions.get(i);

            if ((inst.src1Operand != null) && containsVar(inst.src1Operand)) {
                Stack s1 = stack.get(inst.src1Operand);
                SSAinst.src1Operand = inst.src1Operand + (int) s1.elementAt(s1.size() - 1);
            }
            if ((inst.src2Operand != null) && containsVar(inst.src2Operand)) {
                Stack s2 = stack.get(inst.src2Operand);
                SSAinst.src2Operand = inst.src2Operand + (int) s2.elementAt(s2.size() - 1);
            }
            if ((inst.destOperand != null) && containsVar(inst.destOperand)) {
                SSAinst.destOperand = newName(inst.destOperand);
            }
        }

        for (int i = 0; i < b.CFGBlock.childBlock.size(); i++) {
            Block currentBlock = b.CFGBlock.childBlock.get(i);
            if (!currentBlock.blockID.startsWith("B")) {
                break;
            }
            int currBlockId = Integer.parseInt(currentBlock.blockID.substring(1, currentBlock.blockID.length()));
            DTBlock child = DominatorTree.blockNode.get(currBlockId - 1);

            if (child.visitedForPhi.size() > 0) {
                for (int j = 0; j < b.definationList.size(); j++) {
                    String currVar = b.definationList.get(j);
                    for (int k = 0; k < child.visitedForPhi.size(); k++) {
                        String childVar = "r_" + child.visitedForPhi.get(k);
                        if (childVar.equalsIgnoreCase(currVar)) {
                            ILOCInstruction phiInst = child.CFGBlock.instructions.get(k);
                            ILOCInstruction SSAphiInst = child.SSAinstructions.get(k);
                            if (!child.visited) {
                                if (phiInst.src1Operand != null && containsVar(phiInst.src1Operand)) {
                                    Stack s1 = stack.get(phiInst.src1Operand);
                                    SSAphiInst.src1Operand = phiInst.src1Operand + (int) s1.elementAt(s1.size() - 1);
                                }
                            } else {
                                if (phiInst.src2Operand != null && containsVar(phiInst.src2Operand)) {
                                    Stack s2 = stack.get(phiInst.src2Operand);
                                    SSAphiInst.src2Operand = phiInst.src2Operand + (int) s2.elementAt(s2.size() - 1);
                                }
                            }
                        }
                    }
                }
            } 
            child.visited = true;
        }

        for (int i = 0; i < b.DTChildBlock.size(); i++) {
            renameVariables(b.DTChildBlock.get(i));
        }

        for (int i = 0; i < b.CFGBlock.instructions.size(); i++) {
            ILOCInstruction inst = b.CFGBlock.instructions.get(i);
            if ((inst.destOperand != null) && containsVar(inst.destOperand)) {
                String x = inst.destOperand;
                stack.get(x).pop();
            }
        }
    }

    

    public void genSSAInst(DTBlock dtBlock) {
        if (dtBlock.visitedForSSA == true) {
            return;
        }
        dtBlock.visitedForSSA = true;
//        DTBlock root = DominatorTree.blockNode.get(0);
        if ((dtBlock.CFGBlock.blockID.equalsIgnoreCase("entry")) || (dtBlock.CFGBlock.blockID.equalsIgnoreCase("exit"))) {
            //System.out.println(dtBlock.CFGBlock.blockID);
        } else {
            //System.out.println(dtBlock.CFGBlock.blockID);
            Iterator itr = dtBlock.SSAinstructions.iterator();
            while (itr.hasNext()) {
                ILOCInstruction inst = (ILOCInstruction) itr.next();
                inst.printSSA();
            }
        }

        Iterator itr = dtBlock.CFGBlock.childBlock.iterator();
        while (itr.hasNext()) {
            Block curr = (Block) itr.next();
            if (!curr.blockID.startsWith("B")) {
                break;
            }
            int index = Integer.parseInt(curr.blockID.substring(1, curr.blockID.length()));
            DTBlock child = DominatorTree.blockNode.get(index - 1);
            genSSAInst(child);
        }
    }

    public void genSSA() {
        this.initSSA();
        this.insertPhiFunction();

        for (int i = 0; i < varList.length; i++) {
            counter.put("r_" + varList[i], 0);
            Stack s = new Stack();
            s.push(0);
            stack.put("r_" + varList[i], s);
        }
        this.renameVariables(DominatorTree.blockNode.get(0));
        this.genSSAInst(DominatorTree.blockNode.get(0));
    }
}
