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
public class ASTDeclarations extends Node {
    
    public String declType;
    
    @Override
    public void print(int nodeId) {
        
        Compiler.ASTnodeCounter++;
        
        TL13Grammers.ASTWriter.writeNode("decl: '" + value + "'", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
        
        nodeId = Compiler.ASTnodeCounter;
        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(declType, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
    }

    @Override
    public void addChild(Node type) {
       
    }

    @Override
    public String typeCheck() {
        if (this.type == null) {
            this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
            System.out.println("control should not come here!");
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        ILOCInstruction inst = new ILOCInstruction();
        inst.opCode = ILOCConstants.LOADI;
        inst.src1Operand = "0";
        inst.src2Operand = null;
        inst.destOperand = "r_" + this.value;
        
        entryBlock.addInstruction(inst);
    }
}
