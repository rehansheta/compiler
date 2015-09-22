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
public class ASTOP4Expressions extends Node {

    public ArrayList<ASTSimpleExpression> exprs = new ArrayList();

    public void swapExprs() {
        ASTSimpleExpression temp = exprs.get(0);
        exprs.add(0, exprs.get(1));
        exprs.add(1, temp);
    }

    @Override
    public void addChild(Node child) {
        exprs.add((ASTSimpleExpression) child);  //rhs: lower index
    }

    @Override
    public void print(int nodeId) {

        //lhs: lower index ; rhs: upper index
        swapExprs();

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(this.value, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);

        nodeId = Compiler.ASTnodeCounter;

        // left operand
        ASTSimpleExpression tempExpr = exprs.get(0);
        tempExpr.print(nodeId);

        // right operand
        tempExpr = exprs.get(1);
        tempExpr.print(nodeId);
    }

    @Override
    public String typeCheck() {
        if (exprs.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
            this.type = ASTConstants.TYPE_BOOL;
        } else if (exprs.get(0).typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
            this.type = ASTConstants.TYPE_BOOL;
        } else {
            System.err.println(ASTConstants.TYPE_MISMATCH_ERROR_MESSAGE.replace("######", this.value));
            this.type = ASTConstants.TYPE_ERROR;
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        ILOCInstruction inst = new ILOCInstruction();
        
        if (this.value.equalsIgnoreCase("=")) {  //"=" | "!=" | "<" | ">" | "<=" | ">="
            inst.opCode = ILOCConstants.CMP_E;
        }
        else if (this.value.equalsIgnoreCase("!=")) {  
            inst.opCode = ILOCConstants.CMP_NE;
        }
        else if (this.value.equalsIgnoreCase("<")) {  
            inst.opCode = ILOCConstants.CMP_LT;
        }
        else if (this.value.equalsIgnoreCase(">")) {  
            inst.opCode = ILOCConstants.CMP_GT;
        }
        else if (this.value.equalsIgnoreCase("<=")) {  
            inst.opCode = ILOCConstants.CMP_LE;
        }
        else if (this.value.equalsIgnoreCase(">=")) {  
            inst.opCode = ILOCConstants.CMP_GE;
        }
        
        // left operand
        ASTSimpleExpression tempExpr = exprs.get(0);
        tempExpr.genILOC(entryBlock);
        inst.src1Operand = tempExpr.place;

        // right operand
        tempExpr = exprs.get(1);
        tempExpr.genILOC(entryBlock);
        inst.src2Operand = tempExpr.place;
        
        inst.destOperand = "r" + TL13Grammers.registerCount;
        this.place = inst.destOperand;
        TL13Grammers.registerCount++;
        
        entryBlock.addInstruction(inst);
    }
}
