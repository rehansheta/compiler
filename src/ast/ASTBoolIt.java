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
public class ASTBoolIt extends Node {

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
        this.type = ASTConstants.TYPE_BOOL;
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        ILOCInstruction inst = new ILOCInstruction();
        entryBlock.addInstruction(inst);
        
        inst.opCode = ILOCConstants.LOADI;
        
        if (this.value.equalsIgnoreCase("true")) {
            inst.src1Operand = "1";
        }
        else if (this.value.equalsIgnoreCase("false")) {
            inst.src1Operand = "0";          
        }
        
        inst.destOperand = "r" + TL13Grammers.registerCount;
        
        this.place = inst.destOperand;
        TL13Grammers.registerCount++;
    }
    
}
