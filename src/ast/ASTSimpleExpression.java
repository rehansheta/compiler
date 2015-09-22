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
public class ASTSimpleExpression extends Node {

    public ASTOP3Term op3Exprs;
    public ASTTerm term;

    @Override
    public void addChild(Node child) {
        if (child instanceof ASTOP3Term) {
            op3Exprs = (ASTOP3Term) child;
        } else if (child instanceof ASTTerm) {
            term = (ASTTerm) child;
        }
    }

    @Override
    public void print(int nodeId) {

        if (op3Exprs != null) {
            op3Exprs.addChild(term);
            op3Exprs.print(nodeId);
        } else if (term != null) {
            term.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        if (op3Exprs != null) {
            if (term != null) {
                if (term.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
                    this.type = op3Exprs.typeCheck();
                } else if (term.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
                    this.type = op3Exprs.typeCheck();
                } else {
                    op3Exprs.typeCheck();
                    this.type = ASTConstants.TYPE_ERROR;
                }
            } else {
                this.type = ASTConstants.TYPE_ERROR;
            }
        } else if (term != null) {
            this.type = term.typeCheck();
        } else {
            this.type = ASTConstants.TYPE_ERROR;
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        if (op3Exprs != null) {
            op3Exprs.genILOC(entryBlock);
            this.place = op3Exprs.place;
        } else if (term != null) {
            term.genILOC(entryBlock);
            this.place = term.place;
        }
    }
}
