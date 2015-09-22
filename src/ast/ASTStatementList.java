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
public class ASTStatementList extends Node {

    public ArrayList<ASTStatements> bodyStmt;

    public ASTStatementList() {
        bodyStmt = new ArrayList();
    }
    
    

    @Override
    public void print(int nodeId) {

        Compiler.ASTnodeCounter++;
        
        TL13Grammers.ASTWriter.writeNode("stmt list", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);

        nodeId = Compiler.ASTnodeCounter;
        Iterator itr = bodyStmt.iterator();
        while (itr.hasNext()) {
            ASTStatements aSTStatements = (ASTStatements) itr.next();
            aSTStatements.print(nodeId);
        }
    }

    @Override
    public void addChild(Node child) {
        bodyStmt.add((ASTStatements) child);
    }

    @Override
    public String typeCheck() {

        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        Iterator itr = bodyStmt.iterator();
        while (itr.hasNext()) {
            ASTStatements aSTStatements = (ASTStatements) itr.next();
            if (aSTStatements.typeCheck().equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {

                this.type = ASTConstants.TYPE_ERROR;
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
        
        Iterator itr = bodyStmt.iterator();
        while (itr.hasNext()) {
            ASTStatements aSTStatements = (ASTStatements) itr.next();
            aSTStatements.genILOC(entryBlock);
            if ((aSTStatements instanceof ASTWhileStatement) || (aSTStatements instanceof ASTIfStatements)) {
                entryBlock = aSTStatements.after;
            }
        }
    }
}
