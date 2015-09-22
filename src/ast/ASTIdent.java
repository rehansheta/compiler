/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import edu.utsa.tl13.Compiler;
import edu.utsa.tl13.TL13Grammers;
import iloc.Block;

/**
 *
 * @author Rehan
 */
public class ASTIdent extends Node {

    @Override
    public void print(int nodeId) {

        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode(value, Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
    }

    @Override
    public void addChild(Node child) {
    }

    @Override
    public String typeCheck() {
        if (!SymbleTable.symTableType.containsKey(this.value)) {
            System.err.println(ASTConstants.NO_VAR_ERROR_MESSAGE.replace("######", this.value));
            this.type = ASTConstants.TYPE_ERROR;
        } else {
            this.type = SymbleTable.symTableType.get(this.value);
        }

        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {      
        this.place = "r_" + this.value;
    }
}
