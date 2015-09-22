/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import edu.utsa.tl13.Compiler;
import edu.utsa.tl13.TL13Grammers;
import iloc.Block;
import iloc.ILOCConstants;
import iloc.ILOCInstruction;
import java.util.ArrayList;

/**
 *
 * @author Rehan
 */
public class ASTOP2Factor extends Node {

    public ArrayList<ASTFactor> facts = new ArrayList();

    public void swapFacts() {
        ASTFactor temp = facts.get(0);
        facts.add(0, facts.get(1));
        facts.add(1, temp);
    }

    @Override
    public void addChild(Node child) {  //rhs: lower index 
        facts.add((ASTFactor) child);
    }

    @Override
    public void print(int nodeId) {

        //lhs: lower index ; rhs: upper index
        swapFacts();

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(this.value, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);

        nodeId = Compiler.ASTnodeCounter;

        // left operand
        ASTFactor tempFact = facts.get(0);
        tempFact.print(nodeId);

        // right operand
        tempFact = facts.get(1);
        tempFact.print(nodeId);
    }

    @Override
    public String typeCheck() {
        if (facts.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
            this.type = ASTConstants.TYPE_INT;
        } else if (facts.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
            this.type = ASTConstants.TYPE_CHAR;
        } else {
            System.err.println(ASTConstants.TYPE_MISMATCH_ERROR_MESSAGE.replace("######", this.value));
            this.type = ASTConstants.TYPE_ERROR;
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        ILOCInstruction inst = new ILOCInstruction();       
        
        if (this.value.equalsIgnoreCase("*")) {  // "*" | "div" | "mod"
            inst.opCode = ILOCConstants.MULT;
        }
        else if (this.value.equalsIgnoreCase("div")) {  
            inst.opCode = ILOCConstants.DIV;
        }
        else if (this.value.equalsIgnoreCase("mod")) {  
            inst.opCode = ILOCConstants.MOD;
        }
        
        // left operand
        ASTFactor tempFact = facts.get(0);
        tempFact.genILOC(entryBlock);
        inst.src1Operand = tempFact.place;

        // right operand
        tempFact = facts.get(1);
        tempFact.genILOC(entryBlock);
        inst.src2Operand = tempFact.place;
        
        inst.destOperand = "r" + TL13Grammers.registerCount;
        this.place = inst.destOperand;
        TL13Grammers.registerCount++;
        
        entryBlock.addInstruction(inst);
    }
}
