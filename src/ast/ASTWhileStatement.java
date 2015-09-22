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
public class ASTWhileStatement extends ASTStatements {

    ASTExpression expression;
    ASTDoStatements doStatements;

    @Override
    public void addChild(Node child) {

        if (child instanceof ASTExpression) {
            this.expression = (ASTExpression) child;
        } else if (child instanceof ASTDoStatements) {
            this.doStatements = (ASTDoStatements) child;
        }
    }

    @Override
    public void print(int nodeId) {

        Compiler.ASTnodeCounter++;
        
        TL13Grammers.ASTWriter.writeNode("While", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);


        nodeId = Compiler.ASTnodeCounter;
        expression.print(nodeId);

        if (doStatements != null) {
            doStatements.print(nodeId);
        }

    }

    @Override
    public String typeCheck() {
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        String expressionType = expression.typeCheck();
        String doStatementsType = doStatements.typeCheck();
        if (!expressionType.equalsIgnoreCase(ASTConstants.TYPE_BOOL)
                || doStatementsType.equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
            this.type = ASTConstants.TYPE_ERROR;
        } else {
            this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        Block condBlock = new Block();
        entryBlock.addChildBlock(condBlock);
               
        ILOCInstruction entryJmpInst = new ILOCInstruction();
        entryBlock.addInstruction(entryJmpInst);
        
        entryJmpInst.opCode = ILOCConstants.JUMPI;
        entryJmpInst.destOperand = condBlock.blockID;
        
        expression.genILOC(condBlock);
        
        /*if (expression.op4Expr == null) {
            ILOCInstruction condCmpInst = new ILOCInstruction();
            condBlock.addInstruction(condCmpInst);
            condCmpInst.opCode = ILOCConstants.CMP_E;
            condCmpInst.src1Operand = expression.place;
            condCmpInst.src2Operand = "0";
            condCmpInst.destOperand = "r" + TL13Grammers.registerCount;
            expression.place = condCmpInst.destOperand;
        }*/
        
        ILOCInstruction condCbrInst = new ILOCInstruction();
        condBlock.addInstruction(condCbrInst);
        
        condCbrInst.opCode = ILOCConstants.CBR;
        condCbrInst.src1Operand = expression.place;
        
        Block bodyBlock = new Block();       
        condBlock.addChildBlock(bodyBlock);        
        condCbrInst.src2Operand = bodyBlock.blockID;     

        if (doStatements != null) {
            doStatements.genILOC(bodyBlock);
        }
        
        ILOCInstruction bodyJmpInst = new ILOCInstruction();
        
        bodyBlock.addInstruction(TL13Grammers.blockCount, bodyJmpInst);
        bodyBlock.addChildBlock(TL13Grammers.blockCount, condBlock);
        
        Block exitBlock = new Block();
        condBlock.addChildBlock(exitBlock);
        condCbrInst.destOperand = exitBlock.blockID; 
        
        bodyJmpInst.opCode = ILOCConstants.JUMPI;
        bodyJmpInst.destOperand = condBlock.blockID;
        
        this.begin = condBlock;
        this.after = exitBlock;        
    }
}
