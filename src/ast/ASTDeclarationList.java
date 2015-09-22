/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import edu.utsa.tl13.Compiler;
import edu.utsa.tl13.TL13Grammers;
import iloc.Block;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Rehan
 */
public class ASTDeclarationList extends Node{
    
    public ArrayList<ASTDeclarations> declarations = new ArrayList();

    @Override
    public void print(int nodeId) {
        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode("decl list", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);
        
        nodeId = Compiler.ASTnodeCounter;
        Iterator itr = declarations.iterator();
        
        while (itr.hasNext()) {
            ASTDeclarations aSTDeclarations = (ASTDeclarations) itr.next();
            aSTDeclarations.print(nodeId);
        }
    }

    @Override
    public void addChild(Node child) {
        declarations.add((ASTDeclarations) child);
    }

    @Override
    public String typeCheck() {
        
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        Iterator itr = declarations.iterator();
        while (itr.hasNext()) {
            ASTDeclarations aSTDeclarations = (ASTDeclarations) itr.next();
            if (aSTDeclarations.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
                this.type = ASTConstants.TYPE_ERROR;
            } else {
                String aSTDeclarationsType = aSTDeclarations.typeCheck();
                if (!this.type.equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
                    this.type = aSTDeclarationsType;
                }
            }
        }
        
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        Iterator itr = declarations.iterator();
        while (itr.hasNext()) {
            ASTDeclarations aSTDeclarations = (ASTDeclarations) itr.next();
            aSTDeclarations.genILOC(entryBlock);
        }
    }
    
}
