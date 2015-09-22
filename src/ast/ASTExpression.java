/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import iloc.Block;

/**
 *
 * @author Rehan
 */
public class ASTExpression extends Node {

    public ASTOP4Expressions op4Expr;
    public ASTSimpleExpression expr;

    @Override
    public void addChild(Node child) {
        if (child instanceof ASTOP4Expressions) {
            op4Expr = (ASTOP4Expressions) child;
        } else if (child instanceof ASTSimpleExpression) {
            expr = (ASTSimpleExpression) child;
        }
    }

    @Override
    public void print(int nodeId) {

        if (op4Expr != null) {
            op4Expr.addChild(expr);
            op4Expr.print(nodeId);
        } else if (expr != null) {
            expr.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        if (op4Expr != null) {
            if (expr != null) {
                if (expr.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
                    this.type = op4Expr.typeCheck();
                } else if (expr.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
                    this.type = op4Expr.typeCheck();
                } else {
                    op4Expr.typeCheck();
                    this.type = ASTConstants.TYPE_ERROR;
                }
            } else {
                this.type = ASTConstants.TYPE_ERROR;
            }
        } else if (expr != null) {
            this.type = expr.typeCheck();
        } else {
            // parsing error
            this.type = ASTConstants.TYPE_ERROR;
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        if (op4Expr != null) {
            op4Expr.genILOC(entryBlock);
            this.place = op4Expr.place;
        } else if (expr != null) {
            expr.genILOC(entryBlock);
            this.place = expr.place;
        }
    }
}
