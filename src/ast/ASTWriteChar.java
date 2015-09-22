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
public class ASTWriteChar extends ASTStatements {

    public ASTExpression expression;

    @Override
    public void addChild(Node child) {
        expression = (ASTExpression) child;
    }

    @Override
    public void print(int nodeId) {
        Compiler.ASTnodeCounter++;
        
        TL13Grammers.ASTWriter.writeNode("WriteChar", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);


        nodeId = Compiler.ASTnodeCounter;
        expression.print(nodeId);
    }

    @Override
    public String typeCheck() {
        if (expression.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR) || expression.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
            this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        } else {
            this.type = ASTConstants.TYPE_ERROR;
            System.err.println(ASTConstants.TYPE_ERROR_MESSAGE.replace("######", "WriteChar"));
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        ILOCInstruction inst = new ILOCInstruction();
        inst.opCode = ILOCConstants.WRITECHAR;
        
        expression.genILOC(entryBlock);
        inst.destOperand = expression.place;
        
        entryBlock.addInstruction(inst);       
    }
}
