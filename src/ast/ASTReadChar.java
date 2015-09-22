/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import iloc.Block;
import iloc.ILOCConstants;

/**
 *
 * @author Rehan
 */
public class ASTReadChar extends ASTStatements {

    @Override
    public void addChild(Node child) {
    }

    @Override
    public void print(int nodeId) {
        
    }

    @Override
    public String typeCheck() {
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        return this.type;
    }

    @Override
    public void genILOC(Block body) {
        this.place = ILOCConstants.READCHAR;
    }
    
}
