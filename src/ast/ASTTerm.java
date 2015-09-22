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
public class ASTTerm extends Node {

    public ASTOP2Factor op2Fact;
    public ASTFactor fact;

    @Override
    public void addChild(Node child) {

        if (child instanceof ASTOP2Factor) {
            op2Fact = (ASTOP2Factor) child;
        } else if (child instanceof ASTFactor) {
            fact = (ASTFactor) child;
        }
    }

    @Override
    public void print(int nodeId) {

        if (op2Fact != null) {
            op2Fact.addChild(fact);
            op2Fact.print(nodeId);
        } else if (fact != null) {
            fact.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        if (op2Fact != null) {
            if (fact != null) {
                if (fact.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_INT)) {
                    this.type = op2Fact.typeCheck();
                } else if (fact.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_CHAR)) {
                    this.type = op2Fact.typeCheck();
                } else {
                    op2Fact.typeCheck();
                    this.type = ASTConstants.TYPE_ERROR;
                }
            } else {
                this.type = ASTConstants.TYPE_ERROR;
            }
        } else if (fact != null) {
            this.type = fact.typeCheck();
        } else {
            this.type = ASTConstants.TYPE_ERROR;
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        if (op2Fact != null) {
            op2Fact.genILOC(entryBlock);
            this.place = op2Fact.place;
        } else if (fact != null) {
            fact.genILOC(entryBlock);
            this.place = fact.place;
        }
    }
}
