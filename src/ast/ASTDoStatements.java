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
public class ASTDoStatements extends ASTStatements{
    
    ArrayList<ASTStatements> doStatements;

    public ASTDoStatements() {
        doStatements = new ArrayList();
    }

    @Override
    public void addChild(Node child) {
        doStatements.add((ASTStatements)child);
    }

    @Override
    public void print(int nodeId) {
        
        Compiler.ASTnodeCounter++;
        TL13Grammers.ASTWriter.writeNode("stmt list", Compiler.ASTnodeCounter, this);
        TL13Grammers.ASTWriter.writeEdge(nodeId, Compiler.ASTnodeCounter);


        nodeId = Compiler.ASTnodeCounter;
        Iterator itr = doStatements.iterator();
        
        while (itr.hasNext()) {
            ASTStatements astDo = (ASTStatements) itr.next();
            astDo.print(nodeId);
        }

    }

    @Override
    public String typeCheck() {
        Iterator itr = doStatements.iterator();
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
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
        
        Iterator itr = doStatements.iterator();
        ASTStatements astDo;
        
        while (itr.hasNext()) {
            astDo = (ASTStatements) itr.next();
            astDo.genILOC(entryBlock);
            
            if ((astDo instanceof ASTWhileStatement) || (astDo instanceof ASTIfStatements)) {
                entryBlock = astDo.after;
            }
        }
        
        
        
    }
    
}
