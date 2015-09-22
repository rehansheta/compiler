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
public class ASTOP3Term extends Node {

    public ArrayList<ASTTerm> terms = new ArrayList();

    public void swapTerms() {
        ASTTerm temp = terms.get(0);
        terms.add(0, terms.get(1));
        terms.add(1, temp);
    }

    @Override
    public void addChild(Node child) {  //rhs: lower index
        terms.add((ASTTerm) child);
    }

    @Override
    public void print(int nodeId) {

        //lhs: lower index ; rhs: upper index
        swapTerms();

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(this.value, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);

        nodeId = Compiler.ASTnodeCounter;

        // left operand
        ASTTerm tempTerm = terms.get(0);
        tempTerm.print(nodeId);

        // right operand
        tempTerm = terms.get(1);
        tempTerm.print(nodeId);
    }

    @Override
    public String typeCheck() {
        if (terms.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
            this.type = ASTConstants.TYPE_INT;
        } else if (terms.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
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
        
        if (this.value.equalsIgnoreCase("+")) {  // "+" | "-"
            inst.opCode = ILOCConstants.ADD;
        }
        else if (this.value.equalsIgnoreCase("-")) {  
            inst.opCode = ILOCConstants.SUB;
        }
        
        // left operand
        ASTTerm tempTerm = terms.get(0);
        tempTerm.genILOC(entryBlock);
        inst.src1Operand = tempTerm.place;

        // right operand
        tempTerm = terms.get(1);
        tempTerm.genILOC(entryBlock);
        inst.src2Operand = tempTerm.place;
        
        inst.destOperand = "r" + TL13Grammers.registerCount;
        this.place = inst.destOperand;
        TL13Grammers.registerCount++;
        
        entryBlock.addInstruction(inst);
    }
}
