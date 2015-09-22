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
public class ASTIfStatements extends ASTStatements {

    ASTExpression expression;
    ASTThenStatements thenStatements;
    ASTElseStatements elseStatements;

    public ASTIfStatements() {
    }

    @Override
    public void addChild(Node child) {

        if (child instanceof ASTExpression) {
            expression = (ASTExpression) child;
        } else if (child instanceof ASTThenStatements) {
            thenStatements = (ASTThenStatements) child;
        } else if (child instanceof ASTElseStatements) {
            elseStatements = (ASTElseStatements) child;
        }
    }

    @Override
    public void print(int nodeId) {

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode("IF", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);


        nodeId = Compiler.ASTnodeCounter;
        expression.print(nodeId);

        if (thenStatements != null) {
            thenStatements.print(nodeId);
        }

        if (elseStatements != null) {
            elseStatements.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        String thenStatementType = null;
        String elseStatementType = null;

        String expressionType = expression.typeCheck();
        if (thenStatements != null) {
            thenStatementType = thenStatements.typeCheck();
        }
        if (elseStatements != null) {
            elseStatementType = elseStatements.typeCheck();
        }
        if (!expressionType.equalsIgnoreCase(ASTConstants.TYPE_BOOL)
                || (thenStatementType != null && thenStatementType.equalsIgnoreCase(ASTConstants.TYPE_ERROR))
                || (elseStatementType != null && elseStatementType.equalsIgnoreCase(ASTConstants.TYPE_ERROR))) {
            this.type = ASTConstants.TYPE_ERROR;
            System.err.println(ASTConstants.TYPE_ERROR_MESSAGE.replace("######", "if"));
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
        
        Block thenBlock = new Block();       
        condBlock.addChildBlock(thenBlock);           
        condCbrInst.src2Operand = thenBlock.blockID;
                
        if (thenStatements != null) {
            thenStatements.genILOC(thenBlock);
        }

        Block elseBlock = new Block();
        condBlock.addChildBlock(elseBlock);       
        condCbrInst.destOperand = elseBlock.blockID;
        
        if (elseStatements != null) {
            elseStatements.genILOC(elseBlock);
        }
        
        Block exitBlock = new Block();
        //thenBlock.addChildBlock(exitBlock); 
        //elseBlock.addChildBlock(exitBlock);
        
        ILOCInstruction JmpInst = new ILOCInstruction();
        thenBlock.addInstruction(TL13Grammers.blockCount - 2, JmpInst);
        thenBlock.addChildBlock(TL13Grammers.blockCount - 2, exitBlock);
        
        //thenBlock.addInstruction(JmpInst);
        elseBlock.addInstruction(TL13Grammers.blockCount - 1, JmpInst);
        elseBlock.addChildBlock(TL13Grammers.blockCount - 1, exitBlock);
        
        JmpInst.opCode = ILOCConstants.JUMPI;
        JmpInst.destOperand = exitBlock.blockID;
        
        this.begin = condBlock;
        this.after = exitBlock;
    }
}
