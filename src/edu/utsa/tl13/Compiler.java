package edu.utsa.tl13;

import ast.ASTConstants;
import ast.ASTProgram;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author Rehan
 */
public class Compiler {

    public static Token token;
    public static String tokenValue;
    public static int ptNodeCounter;
    public static int ASTnodeCounter;

    public static void main(String[] args) {
        //String inputFileName = "sqrt.tl13";
        String inputFileName = args[0];
        int baseNameOffset = inputFileName.length() - 5;

        String baseName;
        if (inputFileName.substring(baseNameOffset).equals(".tl13"))
            baseName = inputFileName.substring(0,baseNameOffset);
        else
            throw new RuntimeException("inputFileName does not end in .tl13");

        String dotPTOutputFileName = baseName + ".pt.dot";
        String dotASTOutputFileName = baseName + ".ast.dot";
        String dotCFGOutputFileName = baseName + ".iloc.cfg.dot";
        String MIPSOutputFileName = baseName + ".s";
        String dotDOMOutputFileName = baseName + ".domTree.dot";
        String dotSSAOutputFileName = baseName + ".ssa.dot";
        
        TL13Grammers.ASTWriter = new WriteToFile();
        TL13Grammers.ASTWriter.openFile(dotASTOutputFileName);
        TL13Grammers.ASTWriter.writeHeader();
        
        WriteToFile parseTreeWriter = new WriteToFile();
        parseTreeWriter.openFile(dotPTOutputFileName);
        parseTreeWriter.writeHeader();
        
        TL13Grammers.CFGWriter = new WriteToFile();
        TL13Grammers.CFGWriter.openCFGFile(dotCFGOutputFileName);
        
        TL13Grammers.DOMWriter = new WriteToFile();
        TL13Grammers.DOMWriter.openDOMFile(dotDOMOutputFileName);
        
        TL13Grammers.MIPSWriter = new WriteToFile();
        TL13Grammers.MIPSWriter.openMIPSFile(MIPSOutputFileName);

        TL13Grammers.SSAWriter = new WriteToFile();
        TL13Grammers.SSAWriter.openSSAFile(dotSSAOutputFileName);

        TL13Scanner scanner = new TL13Scanner(inputFileName);
        TL13Grammers grammers = new TL13Grammers(scanner, parseTreeWriter);


        Compiler.token = scanner.getToken();
        Compiler.ptNodeCounter = 1;
        Compiler.ASTnodeCounter = 1;
        
        ASTProgram temp = new ASTProgram();
        temp.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        parseTreeWriter.writeNode("program", Compiler.ptNodeCounter, temp);
        //TL13Grammers.ASTWriter.writeNode("program", Compiler.ASTnodeCounter);
        grammers.parseProgram(Compiler.ptNodeCounter, Compiler.ASTnodeCounter);

        /*Token currToken = scanner.getToken();
          while (currToken != Token.EOF) {
            System.out.println(currToken);
            currToken = scanner.getToken();
        }*/

        parseTreeWriter.writeFooter();
        parseTreeWriter.closeFile();
        
        TL13Grammers.ASTWriter.writeFooter();
        TL13Grammers.ASTWriter.closeFile();
    }
}
