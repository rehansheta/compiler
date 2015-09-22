/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import edu.utsa.tl13.TL13Grammers;
import iloc.Block;

/**
 *
 * @author Rehan
 */
public class ASTProgram extends Node {

    public ASTDeclarationList declList;
    public ASTStatementList stmtList;

    @Override
    public void print(int nodeId) {

        TL13Grammers.ASTWriter.writeNode("program", nodeId, this);

        declList.print(nodeId);
        stmtList.print(nodeId);
    }

    @Override
    public void addChild(Node child) {
        if (child instanceof ASTDeclarationList) {
            declList = (ASTDeclarationList) child;
        } else if (child instanceof ASTStatementList) {
            stmtList = (ASTStatementList) child;
        }
    }

    @Override
    public String typeCheck() {
        this.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        String decListType = declList.typeCheck();
        String stmtListType = stmtList.typeCheck();

        if (decListType.equalsIgnoreCase(ASTConstants.TYPE_ERROR) || stmtListType.equalsIgnoreCase(ASTConstants.TYPE_ERROR)) {
            this.type = ASTConstants.TYPE_ERROR;
        }

        return this.type;
    }

    @Override
    public void genILOC(Block entryBlock) {

        Block bodyBlock = new Block();
        entryBlock.addChildBlock(bodyBlock);

        this.declList.genILOC(bodyBlock);
        this.stmtList.genILOC(bodyBlock);

    }
    
}
