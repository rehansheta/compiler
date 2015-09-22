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
public class ASTAssignments extends ASTStatements{
    
    public ASTIdent ident;
    public ASTExpression expr;
    public ASTReadInt readInt;
    public ASTReadChar readChar;

    @Override
    public void addChild(Node child) {
        if (child instanceof ASTIdent) {
            this.ident = (ASTIdent) child;
        }
        else if (child instanceof ASTExpression) {
            this.expr = (ASTExpression) child;
        }
        else if (child instanceof ASTReadInt) {
            this.readInt = (ASTReadInt) child;
        }
        else if (child instanceof ASTReadChar) {
            this.readChar = (ASTReadChar) child;
        }
    }

    @Override
    public void print(int nodeId) {
        
        Compiler.ASTnodeCounter++;
        
        if (readInt != null) {
            TL13Grammers.ASTWriter.writeNode(":=readInt", Compiler.ASTnodeCounter, this);
        }
        else if (readChar != null) {
            TL13Grammers.ASTWriter.writeNode(":=readChar", Compiler.ASTnodeCounter, this);
        }
        else {
            TL13Grammers.ASTWriter.writeNode(":=", Compiler.ASTnodeCounter, this);
        }
        
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
        
        nodeId = Compiler.ASTnodeCounter;
        
        ident.print(nodeId);
                        
        if (expr != null) {
            expr.print(nodeId);
        }       
        
    }

    @Override
    public String typeCheck() {
        
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        if (SymbleTable.symTableType.containsKey(ident.value)) {
            if (expr != null) {
                //System.out.println(ident.value + "...." + ident.typeCheck());
                //System.out.println("expr type: " + expr.typeCheck());
                if (ident.typeCheck().equalsIgnoreCase(expr.typeCheck())) {
                    this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
                }
                else if ((ident.typeCheck().equalsIgnoreCase("char") && expr.typeCheck().equalsIgnoreCase("int"))
                       ||(ident.typeCheck().equalsIgnoreCase("int") && expr.typeCheck().equalsIgnoreCase("char"))) {
                    this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
                }
                else {
                    this.type = ASTConstants.TYPE_ERROR;
                    System.err.println(ASTConstants.TYPE_ERROR_MESSAGE.replace("######", "Assignment"));
                }
            }
            else if (readInt != null) {
                if (ident.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
                    this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
                }
                else {
                    this.type = ASTConstants.TYPE_ERROR;
                    System.err.println(ASTConstants.TYPE_ERROR_MESSAGE.replace("######", "readInt"));
                }
            }
            else if (readChar != null) {
                if (ident.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
                    this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
                }
                else {
                    this.type = ASTConstants.TYPE_ERROR;
                    System.err.println(ASTConstants.TYPE_ERROR_MESSAGE.replace("######", "readChar"));
                }
            }
        }
        else {
            ident.typeCheck();
            expr.typeCheck();
            this.type = ASTConstants.TYPE_ERROR;
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        ILOCInstruction inst = new ILOCInstruction();
        
        this.ident.genILOC(entryBlock);
        inst.destOperand = ident.place;
        
        if (readInt != null) {
            this.readInt.genILOC(entryBlock);
            inst.opCode = readInt.place;
        }
        else if (readChar != null) {
            this.readChar.genILOC(entryBlock);
            inst.opCode = readChar.place;
        }
        else if (expr != null) {
            expr.genILOC(entryBlock);
            inst.src1Operand = expr.place;
            inst.opCode = ILOCConstants.I2I;
        }
        
        entryBlock.addInstruction(inst);
    }
    
}
