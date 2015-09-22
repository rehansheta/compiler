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
public class ASTFactor extends Node {

    public ASTIdent ident;
    public ASTNum num;
    public ASTBoolIt boolIt;
    public ASTChar character;
    public ASTExpression expr;

    @Override
    public void addChild(Node child) {

        if (child instanceof ASTIdent) {
            ident = (ASTIdent) child;
        } else if (child instanceof ASTNum) {
            num = (ASTNum) child;
        } else if (child instanceof ASTBoolIt) {
            boolIt = (ASTBoolIt) child;
        } else if (child instanceof ASTChar) {
            character = (ASTChar) child;
        } else if (child instanceof ASTExpression) {
            expr = (ASTExpression) child;
        } 
    }

    @Override
    public void print(int nodeId) {

        if (ident != null) {
            ident.print(nodeId);
        } else if (num != null) {
            num.print(nodeId);
        } else if (boolIt != null) {
            boolIt.print(nodeId);
        } else if (character != null) {
            character.print(nodeId);
        }else if (expr != null) {
            expr.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        this.type = ASTConstants.TYPE_ERROR;

        if (ident != null) {
            this.type = ident.typeCheck();
        } else if (num != null) {
            this.type = num.typeCheck();
        } else if (boolIt != null) {
            this.type = boolIt.typeCheck();
        } else if (character != null) {
            this.type = character.typeCheck();
        }else if (expr != null) {
            this.type = expr.typeCheck();
        } else {
            // parsing error
            this.type = ASTConstants.TYPE_ERROR;
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        if (ident != null) {
            ident.genILOC(entryBlock);
            this.place = ident.place;
        } else if (num != null) {
            num.genILOC(entryBlock);
            this.place = num.place;
        } else if (boolIt != null) {
            boolIt.genILOC(entryBlock);
            this.place = boolIt.place;
        } else if (character != null) {
            character.genILOC(entryBlock);
            this.place = character.place;
        }else if (expr != null) {
            expr.genILOC(entryBlock);
            this.place = expr.place;
        }
    }
}
