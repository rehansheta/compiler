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
public class ASTElseStatements extends ASTStatements {
    ArrayList<ASTStatements> elseStatements;

    public ASTElseStatements() {
        elseStatements = new ArrayList();
    }
    
    @Override
    public void addChild(Node child) {
        elseStatements.add((ASTStatements)child);
    }

    @Override
    public void print(int nodeId) {
        Compiler.ASTnodeCounter++;
        
        TL13Grammers.ASTWriter.writeNode("stmt list", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);


        nodeId = Compiler.ASTnodeCounter;
        Iterator itr = elseStatements.iterator();
        while (itr.hasNext()) {
            ASTStatements astElse = (ASTStatements) itr.next();
            astElse.print(nodeId);
        }
    }

    @Override
    public String typeCheck() {
        Iterator itr = elseStatements.iterator();
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        while (itr.hasNext()) {
            ASTStatements aSTStatements = (ASTStatements) itr.next();
            if (aSTStatements.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
                this.type = ASTConstants.TYPE_ERROR;
                //break;
            } else {
                String aSTStatementsType = aSTStatements.typeCheck();
                if (!this.type.equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
                    this.type = aSTStatementsType;
                }
            }
        }
        return this.type;
    }
    
    @Override
    public void genILOC(Block entryBlock) {
        
        Iterator itr = elseStatements.iterator();
        
        while (itr.hasNext()) {
            ASTStatements astElse = (ASTStatements) itr.next();
            astElse.genILOC(entryBlock);
            
            if ((astElse instanceof ASTWhileStatement) || (astElse instanceof ASTIfStatements)) {
                entryBlock = astElse.after;
            }
        }
    }
}
