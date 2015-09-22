package edu.utsa.tl13;

import ast.*;
import iloc.Block;
import iloc.ILOCConstants;
import iloc.ILOCInstruction;
import mips.MIPSInstSet;
import ssa.DominFrontier;
import ssa.Dominance;
import ssa.DominatorTree;
import ssa.SSA;

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 *
 *
 *
 * @author Rehan
 */
public class TL13Grammers {

    private TL13Scanner scanner;
    private WriteToFile parseTreeWriter;
    public static WriteToFile ASTWriter;
    public static WriteToFile CFGWriter;
    public static WriteToFile DOMWriter;
    public static WriteToFile MIPSWriter;
    public static WriteToFile SSAWriter;
    public ASTProgram astProgram;
    public MIPSInstSet mipsInst;
    public SymbleTable sTable;
    public Dominance dominance;
    public DominatorTree domTree;
    public DominFrontier domFrontier;
    public SSA ssa;
    public static int registerCount = 0;
    public static int blockCount = 0;

    public TL13Grammers(TL13Scanner scanner, WriteToFile parseTreeWriter) {
        this.scanner = scanner;
        this.parseTreeWriter = parseTreeWriter;
        this.sTable = new SymbleTable();
        this.mipsInst = new MIPSInstSet();
        this.dominance = new Dominance();
        this.domTree = new DominatorTree();
        this.domFrontier = new DominFrontier();
        this.ssa = new SSA();

    }

    public void parseProgram(int PTnodeId, int ASTnodeId) {

        if (Compiler.token == Token.PROGRAM) {

            /**
             * ******** AST node *********
             */
            astProgram = new ASTProgram();
            /**
             * **************************
             */
            ASTProgram PTtemp = new ASTProgram();
            PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
            PTtemp.value = "program";
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("PROGRAM", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(PTnodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("declarations", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(PTnodeId, Compiler.ptNodeCounter);

            /**
             * ******** AST node *********
             */
            ASTDeclarationList astDeclareList = new ASTDeclarationList();
            astProgram.addChild(astDeclareList);
            /**
             * **************************
             */
            parseDeclarations(Compiler.ptNodeCounter, astDeclareList);

            if (Compiler.token == Token.BEGIN) {

                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("BEGIN", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(PTnodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("statementSequence", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(PTnodeId, Compiler.ptNodeCounter);


                /**
                 * ******** AST node *********
                 */
                ASTStatementList astStmtList = new ASTStatementList();
                astProgram.addChild(astStmtList);
                parseStatementSequence(Compiler.ptNodeCounter, astStmtList);
                /**
                 * **************************
                 */
                if (Compiler.token == Token.END) {
                    Compiler.ptNodeCounter++;
                    parseTreeWriter.writeNode("END", Compiler.ptNodeCounter, PTtemp);
                    parseTreeWriter.writeEdge(PTnodeId, Compiler.ptNodeCounter);

                    Compiler.token = scanner.getToken();

                    if (Compiler.token == Token.EOF) {
                        System.out.println("Parsing complete");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        System.err.println("Extra line(s) after the program");
                    }

                    /**
                     * ****** AST Operations ********
                     */
                    //sTable.print();
                    astProgram.typeCheck();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Type checking complete");
                    astProgram.print(ASTnodeId);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("AST build done");
                    /**
                     * *****************************
                     */
                    
                    
                    /**
                     * ****** ILOC Operations ********
                     */
                    Block entryBlock = new Block("entry");
                    Block exitBlock = new Block("exit");

                    ILOCInstruction exitInst = new ILOCInstruction();
                    exitInst.opCode = ILOCConstants.EXIT;

                    astProgram.genILOC(entryBlock);
                    //astProgram.genMIPSInst(entryBlock);

                    entryBlock.addInstruction(TL13Grammers.blockCount, exitInst);
                    entryBlock.addChildBlock(TL13Grammers.blockCount, exitBlock);

                    //entryBlock.genCFG();
                    TL13Grammers.CFGWriter.WriteToCFGFile(entryBlock);
                    /**
                     * *******************************
                     */
                    
                    // ********* SSA extension ********* //
                    dominance.genDOMSet();
                    domTree.genDomTree();
                    domFrontier.genDomFrontier();
                    ssa.genSSA();
                    TL13Grammers.DOMWriter.WriteToDOMFile();
                    TL13Grammers.SSAWriter.WriteToSSAFile();
                    /**
                     * ****** MIPS Operations ********
                     */
                    /*for (int i = 0; i < Block.blockList.size(); i++) {
                        System.out.println(Block.blockList.get(i));
                        Block.blockList.get(i).visited = false;
                    }*/
                    mipsInst.genMIPSInst(entryBlock);
                    mipsInst.printMipsInst();
                    TL13Grammers.MIPSWriter.WriteToMIPSFile(mipsInst);
                    System.out.println("MIPS Code generated.");
                    /**
                     * *******************************
                     */
                    
                } else {
                    System.err.println("TOKEN : END expected but TOKEN : " + Compiler.tokenValue + " found");
                    System.err.println("end of begin");
                    System.err.println("Parsing unsuccessful, AST not built, Type checking not done.");
                }

            } else {
                System.err.println("TOKEN : begin expected but TOKEN : " + Compiler.tokenValue + " found");
                System.err.println("Parsing unsuccessful, AST not built, Type checking not done.");
            }
        } else {
            System.err.println("TOKEN : program expected but TOKEN : " + Compiler.tokenValue + " found");
            System.err.println("Parsing unsuccessful, AST not built, Type checking not done.");
        }
    }

    public void parseDeclarations(int nodeId, Node parent) {

        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.VAR) {

            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("VAR", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            if (Compiler.token == Token.ident) {

                /**
                 * ****** AST Operations ********
                 */
                ASTDeclarations astDeclare = new ASTDeclarations();
                astDeclare.value = Compiler.tokenValue;
                /**
                 * ******************************
                 */
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("ident: " + Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                if (Compiler.token == Token.AS) {
                    Compiler.ptNodeCounter++;
                    parseTreeWriter.writeNode("AS", Compiler.ptNodeCounter, PTtemp);
                    parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                    Compiler.token = scanner.getToken();
                    Compiler.ptNodeCounter++;
                    parseTreeWriter.writeNode("type", Compiler.ptNodeCounter, PTtemp);
                    parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);


                    /**
                     * ****** AST Operations ********
                     */
                    astDeclare.declType = Compiler.tokenValue;
                    parent.addChild(astDeclare);

                    //add to symbol table
                    sTable.insert(astDeclare.value, astDeclare.declType, astDeclare);
                    /**
                     * *****************************
                     */
                    parseType(Compiler.ptNodeCounter);

                    if (Compiler.token == Token.SC) {
                        Compiler.ptNodeCounter++;
                        parseTreeWriter.writeNode(";", Compiler.ptNodeCounter, PTtemp);
                        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                        Compiler.token = scanner.getToken();
                        Compiler.ptNodeCounter++;
                        parseTreeWriter.writeNode("declarations", Compiler.ptNodeCounter, PTtemp);
                        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                        parseDeclarations(Compiler.ptNodeCounter, parent);
                    } else {
                        System.err.println("TOKEN : SC expected but TOKEN : " + Compiler.tokenValue + " found");
                    }
                } else {
                    System.err.println("TOKEN : AS expected but TOKEN : " + Compiler.tokenValue + " found");
                }
            } else {
                System.err.println("TOKEN : IDENT expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        } else {
            //*** epsilon production
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        }
    }

    public void parseType(int nodeId) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if ((Compiler.token == Token.INT) || (Compiler.token == Token.BOOL) || (Compiler.token == Token.CHAR)) {

            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("" + Compiler.token, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
        } else {
            System.err.println("TOKEN : INT|BOOL|CHAR expected but TOKEN : " + Compiler.tokenValue + " found");
        }
    }

    public void parseStatementSequence(int nodeId, Node parent) {

        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        //*** follow of statementSequence (check again), ELSE = ?
        if ((Compiler.token == Token.END) || (Compiler.token == Token.ELSE)) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

        } else {

            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("statement", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            parseStatement(Compiler.ptNodeCounter, parent);

            if (Compiler.token == Token.SC) {
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode(";", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("statementSequence", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                parseStatementSequence(Compiler.ptNodeCounter, parent);
            } else {
                System.err.println("TOKEN : SC expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        }
    }

    public void parseStatement(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.ident) {
            //*** lookahead check, not match
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("assignment", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseAssignment(Compiler.ptNodeCounter, parent);
        } else if (Compiler.token == Token.IF) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("ifStatement", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseIfStatement(Compiler.ptNodeCounter, parent);
        } else if (Compiler.token == Token.WHILE) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("whileStatement", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseWhileStatement(Compiler.ptNodeCounter, parent);
        } else if (Compiler.token == Token.WRITEINT) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("writeint", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseWriteInt(Compiler.ptNodeCounter, parent);
        } else if (Compiler.token == Token.WRITECHAR) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("writechar", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseWriteChar(Compiler.ptNodeCounter, parent);
        } else {
            System.err.println("TOKEN : ident|IF|WHILE|WRITEINT expected but TOKEN : " + Compiler.tokenValue + " found");
        }

    }

    public void parseAssignment(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.ident) {  // match  

            /**
             * ****** AST Operations ********
             */
            ASTIdent astAssignIdent = new ASTIdent();
            astAssignIdent.value = Compiler.tokenValue;
            /**
             * *****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("ident: " + Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            if (Compiler.token == Token.ASGN) { // match


                /**
                 * ****** AST Operations ********
                 */
                ASTAssignments astAssignmnt = new ASTAssignments();
                astAssignmnt.addChild(astAssignIdent);
                parent.addChild(astAssignmnt);
                /**
                 * *****************************
                 */
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode(":=", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("assignment'", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                parseAssignmentPrime(Compiler.ptNodeCounter, astAssignmnt);
            } else {
                System.err.println("TOKEN : ASGN expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        } else {
            System.err.println("Control never comes here!");
        }
    }

    public void parseAssignmentPrime(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.READINT) { // match


            /**
             * ********** AST node *********
             */
            ASTReadInt astReadIntAssign = new ASTReadInt();
            parent.addChild(astReadIntAssign);
            /**
             * *****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("READINT", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            Compiler.token = scanner.getToken();
        } else if (Compiler.token == Token.READCHAR) { // match


            /**
             * ********** AST node *********
             */
            ASTReadChar astReadCharAssign = new ASTReadChar();
            parent.addChild(astReadCharAssign);
            /**
             * *****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("READCHAR", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            Compiler.token = scanner.getToken();
        } else { // we have to check first of expression

            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, parent);
        }
    }

    public void parseIfStatement(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.IF) {


            /**
             * ********* AST node *********
             */
            ASTIfStatements astIfState = new ASTIfStatements();
            parent.addChild(astIfState);
            /**
             * ***************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("IF", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, astIfState);

            if (Compiler.token == Token.THEN) { //*** match


                /**
                 * ********* AST node *********
                 */
                ASTThenStatements astThenState = new ASTThenStatements();
                astIfState.addChild(astThenState);
                /**
                 * ****************************
                 */
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("THEN", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("statementSequence", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                parseStatementSequence(Compiler.ptNodeCounter, astThenState);

                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("elseClause", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                parseElseClause(Compiler.ptNodeCounter, astIfState);

                if (Compiler.token == Token.END) { //*** match
                    Compiler.ptNodeCounter++;
                    parseTreeWriter.writeNode("END", Compiler.ptNodeCounter, PTtemp);
                    parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                    Compiler.token = scanner.getToken();
                } else {
                    System.err.println("TOKEN : END expected but TOKEN : " + Compiler.tokenValue + " found");
                    System.err.println("end of the if");
                }
            } else {
                System.err.println("TOKEN : THEN expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        }
    }

    public void parseElseClause(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.ELSE) {


            /**
             * ********* AST node *********
             */
            ASTElseStatements astElseState = new ASTElseStatements();
            parent.addChild(astElseState);
            /**
             * ****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("ELSE", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("statementSequence", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseStatementSequence(Compiler.ptNodeCounter, astElseState);

        } // follow of else
        else if (Compiler.token == Token.END) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

        } else {
            System.err.println("TOKEN : END expected but TOKEN : " + Compiler.tokenValue + " found");
            System.err.println("end in else");
        }
    }

    public void parseWhileStatement(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.WHILE) { // match


            /**
             * ********* AST node *********
             */
            ASTWhileStatement astWhileStmnt = new ASTWhileStatement();
            parent.addChild(astWhileStmnt);
            /**
             * ****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("WHILE", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, astWhileStmnt);

            if (Compiler.token == Token.DO) { // match


                /**
                 * ******** AST node *********
                 */
                ASTDoStatements astDoStmnt = new ASTDoStatements();
                astWhileStmnt.addChild(astDoStmnt);
                /**
                 * ***************************
                 */
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("DO", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode("statementSequence", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
                parseStatementSequence(Compiler.ptNodeCounter, astDoStmnt);

                if (Compiler.token == Token.END) { //*** match
                    Compiler.ptNodeCounter++;
                    parseTreeWriter.writeNode("END", Compiler.ptNodeCounter, PTtemp);
                    parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                    Compiler.token = scanner.getToken();
                } else {
                    System.err.println("TOKEN : END expected but TOKEN : " + Compiler.tokenValue + " found");
                }

            } else {
                System.err.println("TOKEN : DO expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        }
    }

    public void parseWriteInt(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.WRITEINT) {


            /**
             * ****** AST Operations ********
             */
            ASTWriteInt astWriteIntStmnt = new ASTWriteInt();
            parent.addChild(astWriteIntStmnt);
            /**
             * ******************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("WRITEINT", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, astWriteIntStmnt);
        } else {
            System.out.println("Control never comes here!");
        }

    }
    
    public void parseWriteChar(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.WRITECHAR) {


            /**
             * ****** AST Operations ********
             */
            ASTWriteChar astWriteCharStmnt = new ASTWriteChar();
            parent.addChild(astWriteCharStmnt);
            /**
             * ******************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("WRITECHAR", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, astWriteCharStmnt);
        } else {
            System.out.println("Control never comes here!");
        }

    }

    public void parseExpression(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        /**
         * ******** AST node *********
         */
        ASTExpression astExpr = new ASTExpression();
        parent.addChild(astExpr);
        /**
         * **************************
         */
        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("simpleExression", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        parseSimpleExpression(Compiler.ptNodeCounter, astExpr);


        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("expression'", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        parseExpressionPrime(Compiler.ptNodeCounter, astExpr);
    }

    public void parseExpressionPrime(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.OP4) { // match


            /**
             * ******** AST node *********
             */
            ASTOP4Expressions astOP4Expr = new ASTOP4Expressions();
            astOP4Expr.value = Compiler.tokenValue;
            parent.addChild(astOP4Expr);
            /**
             * ***************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode(Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("simpleExpression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseSimpleExpression(Compiler.ptNodeCounter, astOP4Expr);
        } // follow of parseExpressionPrime
        else if ((Compiler.token == Token.THEN) || (Compiler.token == Token.DO) || (Compiler.token == Token.SC) || (Compiler.token == Token.RP)) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        }
    }

    public void parseSimpleExpression(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        /**
         * ****** AST Operations ********
         */
        ASTSimpleExpression astSimpleexpr = new ASTSimpleExpression();
        parent.addChild(astSimpleexpr);
        /**
         * ******************************
         */
        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("term", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        parseTerm(Compiler.ptNodeCounter, astSimpleexpr);

        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("simpleExpression'", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        parseSimpleExpressionPrime(Compiler.ptNodeCounter, astSimpleexpr);
    }

    public void parseSimpleExpressionPrime(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.OP3) { // match


            /**
             * ********* AST node *********
             */
            ASTOP3Term astOP3term = new ASTOP3Term();
            astOP3term.value = Compiler.tokenValue;
            parent.addChild(astOP3term);
            /**
             * ****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode(Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("term", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseTerm(Compiler.ptNodeCounter, astOP3term);
        } // follow of simpleExpressionPrime
        else if ((Compiler.token == Token.THEN) || (Compiler.token == Token.DO) || (Compiler.token == Token.SC) || (Compiler.token == Token.RP)) {
            //*** epsilon production exist
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        }
    }

    public void parseTerm(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;

        /**
         * ****** AST Operations ********
         */
        ASTTerm astTerm = new ASTTerm();
        parent.addChild(astTerm);
        /**
         * *****************************
         */
        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("factor", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

        parseFactor(Compiler.ptNodeCounter, astTerm);

        Compiler.ptNodeCounter++;
        parseTreeWriter.writeNode("term'", Compiler.ptNodeCounter, PTtemp);
        parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        parseTermPrime(Compiler.ptNodeCounter, astTerm);

    }

    public void parseTermPrime(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if (Compiler.token == Token.OP2) { // match


            /**
             * ********** AST node *********
             */
            ASTOP2Factor astOP2Factor = new ASTOP2Factor();
            astOP2Factor.value = Compiler.tokenValue;
            parent.addChild(astOP2Factor);
            /**
             * ****************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode(Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("factor", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseFactor(Compiler.ptNodeCounter, astOP2Factor);
        } // follow of termPrime
        else if ((Compiler.token == Token.THEN) || (Compiler.token == Token.DO) || (Compiler.token == Token.SC) || (Compiler.token == Token.RP) || (Compiler.token == Token.OP3) || (Compiler.token == Token.OP4)) {
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("e", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
        }
    }

    public void parseFactor(int nodeId, Node parent) {
        ASTProgram PTtemp = new ASTProgram();
        PTtemp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        
        if ((Compiler.token == Token.ident) || (Compiler.token == Token.boollit) || (Compiler.token == Token.num) || (Compiler.token == Token.character)) { //*** match
            /**
             * ******** AST node *********
             */
            if (Compiler.token == Token.ident) {
                ASTIdent astFactorIdent = new ASTIdent();
                astFactorIdent.value = Compiler.tokenValue;

                ASTFactor astFactor = new ASTFactor();
                astFactor.addChild(astFactorIdent);
                parent.addChild(astFactor);
            }

            if (Compiler.token == Token.boollit) {
                ASTBoolIt aSTBoolIt = new ASTBoolIt();
                aSTBoolIt.value = Compiler.tokenValue;

                ASTFactor astFactor = new ASTFactor();
                astFactor.addChild(aSTBoolIt);
                parent.addChild(astFactor);
            }

            if (Compiler.token == Token.num) {
                ASTNum aSTNum = new ASTNum();
                aSTNum.value = Compiler.tokenValue;

                ASTFactor astFactor = new ASTFactor();
                astFactor.addChild(aSTNum);
                parent.addChild(astFactor);
            }
            
            if (Compiler.token == Token.character) {
                ASTChar aSTChar = new ASTChar();
                aSTChar.value = Compiler.tokenValue;

                ASTFactor astFactor = new ASTFactor();
                astFactor.addChild(aSTChar);
                parent.addChild(astFactor);
            }
            /**
             * ********************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("" + Compiler.token + ": " + Compiler.tokenValue, Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            Compiler.token = scanner.getToken();
        } else if (Compiler.token == Token.LP) { // match


            /**
             * ****** AST Operations ********
             */
            ASTFactor astFactor = new ASTFactor();
            parent.addChild(astFactor);
            /**
             * ******************************
             */
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("(", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

            Compiler.token = scanner.getToken();
            Compiler.ptNodeCounter++;
            parseTreeWriter.writeNode("expression", Compiler.ptNodeCounter, PTtemp);
            parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);
            parseExpression(Compiler.ptNodeCounter, astFactor);

            if (Compiler.token == Token.RP) { //*** match
                Compiler.ptNodeCounter++;
                parseTreeWriter.writeNode(")", Compiler.ptNodeCounter, PTtemp);
                parseTreeWriter.writeEdge(nodeId, Compiler.ptNodeCounter);

                Compiler.token = scanner.getToken();
            } else {
                System.err.println("TOKEN : RP expected but TOKEN : " + Compiler.tokenValue + " found");
            }
        } else { // check again
            System.err.println("TOKEN : IDENT|BOOLIT|NUM|CHAR|LP expected but TOKEN : " + Compiler.tokenValue + " found");
        }
    }
}
