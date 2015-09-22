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

/**
 *
 * @author Rehan
 */
public class ASTNum extends Node {

    @Override
    public void print(int nodeId) {

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(value, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
    }

    @Override
    public void addChild(Node child) {
    }

    @Override
    public String typeCheck() {
        if (this.value.length() > 10) {
            this.type = ASTConstants.TYPE_ERROR;
            System.err.println(ASTConstants.ILLEGAL_INT_ERROR_MESSAGE.replace("######", this.value));
        } 
        else {
            try {
            int numValue = Integer.parseInt(this.value);

            if ((numValue >= ASTConstants.MIN_INT) && (numValue <= ASTConstants.MAX_INT)) {
                this.type = ASTConstants.TYPE_INT;
            } else {
                this.type = ASTConstants.TYPE_ERROR;
                System.err.println(ASTConstants.ILLEGAL_INT_ERROR_MESSAGE.replace("######", this.value));
            }
            } catch (NumberFormatException ex) {
                this.type = ASTConstants.TYPE_ERROR;
                System.err.println(ASTConstants.ILLEGAL_INT_ERROR_MESSAGE.replace("######", this.value));
            }
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        ILOCInstruction inst = new ILOCInstruction();
        entryBlock.addInstruction(inst);
        
        inst.opCode = ILOCConstants.LOADI;
        inst.src1Operand = this.value;
        inst.src2Operand = null;
        inst.destOperand = "r" + TL13Grammers.registerCount;
        
        this.place = inst.destOperand;
        TL13Grammers.registerCount++;
    }
}
