package edu.utsa.tl13;

import ast.ASTDeclarations;
import ast.Node;
import iloc.Block;
import iloc.ILOCConstants;
import iloc.ILOCInstruction;
import java.io.FileWriter;
import java.io.IOException;
import mips.MIPSInstSet;
import ssa.DTBlock;
import ssa.DominatorTree;

public class WriteToFile {

    private FileWriter fileStream;
    private FileWriter cfgStream;
    private FileWriter mipsStream;
    private FileWriter ssaStream;
    private FileWriter domStream;

    public void writeNode(String label, int node, Node ASTnode) {
        try {
            if (!ASTnode.type.startsWith("Error")) {
                if (label.equalsIgnoreCase("stmt list") || label.equalsIgnoreCase("decl list")) {
                    fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/x11/white\",shape=box, color=white]" + "\n");
                } else {
                    if (ASTnode.type.equalsIgnoreCase("INT")) {
                        fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/pastel13/3\",shape=box]" + "\n");
                    } else if (ASTnode.type.equalsIgnoreCase("BOOL")) {
                        fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/pastel13/2\",shape=box]" + "\n");
                    } else if (ASTnode instanceof ASTDeclarations) {
                        ASTDeclarations n = (ASTDeclarations) ASTnode;
                        if ((n.declType.equalsIgnoreCase("INT")) && label.startsWith("decl: '")) {
                            fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/pastel13/3\",shape=box]" + "\n");
                        } else if ((n.declType.equalsIgnoreCase("BOOL")) && label.startsWith("decl: '")) {
                            fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/pastel13/2\",shape=box]" + "\n");
                        } else {
                            fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/x11/gray\",shape=box]" + "\n");
                        }
                    } else {
                        fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/x11/gray\",shape=box]" + "\n");
                    }
                }

            } else {
                if (label.equalsIgnoreCase("stmt list") || label.equalsIgnoreCase("decl list")) {
                    fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/x11/white\",shape=box, color=white]" + "\n");
                } else {
                    fileStream.write("  n" + node + " [label=\"" + label + "\",fillcolor=\"/pastel13/1\",shape=box]" + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCFG(Block block) throws IOException {
        if (block.visitedForCFG == true) {
            return;
        }
        block.visitedForCFG = true;

        String instructions = "";
        for (int j = 0; j < block.instructions.size(); j++) {
            instructions += block.instructions.get(j).printILOC() + "\\n";
        }

        instructions = instructions.replace(">", "&gt;");
        instructions = block.blockID + "\\n" + instructions;

        cfgStream.write(block.blockID + "[shape = record, label = \"" + instructions + "\"]");
        cfgStream.write(";\n");

        for (int i = 0; i < block.childBlock.size(); i++) {
            writeCFG(block.childBlock.get(i));
            cfgStream.write(block.blockID + " -> " + block.childBlock.get(i).blockID + ";\n");
        }
    }

    public void writeSSA(DTBlock dtBlock) throws IOException {
        if (dtBlock.visitedForSSAOut == true) {
            return;
        }
        dtBlock.visitedForSSAOut = true;

        String instructions = "";
        for (int j = 0; j < dtBlock.SSAinstructions.size(); j++) {
            instructions += dtBlock.SSAinstructions.get(j).printSSA() + "\\n";
        }

        instructions = instructions.replace("<", "&lt;");
        instructions = instructions.replace(">", "&gt;");
        instructions = dtBlock.CFGBlock.blockID + "\\n" + instructions;

        ssaStream.write(dtBlock.CFGBlock.blockID + "[shape = record, label = \"" + instructions + "\"]");
        ssaStream.write(";\n");

        for (int i = 0; i < dtBlock.CFGBlock.childBlock.size(); i++) {
            Block child = dtBlock.CFGBlock.childBlock.get(i);
            if (!child.blockID.startsWith("B")) {
                ssaStream.write("Exit" + "[shape = record, label = \"" + "exit" + "\"]");
                ssaStream.write(";\n");
                ssaStream.write(dtBlock.CFGBlock.blockID + " -> Exit;\n");
                break;
            }
            int childId = Integer.parseInt(child.blockID.substring(1, child.blockID.length()));
            DTBlock currBock = DominatorTree.blockNode.get(childId - 1);
            writeSSA(currBock);
            ssaStream.write(dtBlock.CFGBlock.blockID + " -> " + child.blockID + ";\n");
        }
    }

    public void writeDOM() throws IOException {

        for (int index = 0; index < DominatorTree.blockNode.size(); index++) {
            DTBlock block = DominatorTree.blockNode.get(index);

            if (block.CFGBlock.visitedForDom == true) {
                return;
            }
            block.CFGBlock.visitedForDom = true;

            domStream.write(block.CFGBlock.blockID + "[shape = record, label = \"" + block.CFGBlock.blockID + "\"]");
            domStream.write(";\n");

            for (int i = 0; i < block.DTChildBlock.size(); i++) {
                domStream.write(block.DTChildBlock.get(i).CFGBlock.blockID + "[shape = record, label = \"" + block.DTChildBlock.get(i).CFGBlock.blockID + "\"]");
                domStream.write(";\n");
                domStream.write(block.CFGBlock.blockID + " -> " + block.DTChildBlock.get(i).CFGBlock.blockID + ";\n");
            }
        }
    }

    public void writeMIPS(MIPSInstSet mipsInst) throws IOException {
        for (int i = 0; i < mipsInst.mipsInstSet.size(); i++) {
            ILOCInstruction inst = mipsInst.getMipsInst(i);

            if (inst.opCode.startsWith("B")) {
                mipsStream.write("\n\n" + inst.opCode);

            } else if (inst.opCode.startsWith("#")) {
                String wholeInst = inst.opCode;

                if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.EXIT)) {
                } else if ((inst.opCode.equalsIgnoreCase("# " + ILOCConstants.LOADI)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.I2I))) {
                    wholeInst += " " + inst.src1Operand + " => " + inst.destOperand;
                } else if ((inst.opCode.equalsIgnoreCase("# " + ILOCConstants.ADD)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.SUB))
                        || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.MULT)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.DIV))
                        || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.MOD))) {
                    wholeInst += " " + inst.src1Operand + ", " + inst.src2Operand + " => " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.READINT)) {
                    wholeInst += " => " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.READCHAR)) {
                    wholeInst += " => " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.WRITEINT)) {
                    wholeInst += " " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.WRITECHAR)) {
                    wholeInst += " " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.JUMPI)) {
                    wholeInst += " -> " + inst.destOperand;
                } else if ((inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_LE)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_GE)
                        || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_LT)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_GT)
                        || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_E)) || (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CMP_NE))))) {
                    wholeInst += " " + inst.src1Operand + ", " + inst.src2Operand + " => " + inst.destOperand;
                } else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.CBR)) {
                    wholeInst += " " + inst.src1Operand + " -> " + inst.src2Operand + ", " + inst.destOperand;
                }
                mipsStream.write("\n\n\t" + wholeInst);

            } else {
                if ((inst.destOperand == null) && (inst.src1Operand == null) && (inst.src2Operand == null)) {
                    mipsStream.write("\n\t" + inst.opCode);
                } else if ((inst.src1Operand == null) && (inst.src2Operand == null)) {
                    mipsStream.write("\n\t" + inst.opCode + " " + inst.destOperand);
                } else if ((inst.src2Operand == null)) {
                    mipsStream.write("\n\t" + inst.opCode + " " + inst.destOperand + ", " + inst.src1Operand);
                } else {
                    mipsStream.write("\n\t" + inst.opCode + " " + inst.destOperand + ", " + inst.src1Operand + ", " + inst.src2Operand);

                }
            }
        }
    }

    public void WriteToCFGFile(Block entryBlock) {
        try {
            cfgStream.write("digraph tl13cfg {\n");
            cfgStream.write("node [shape = record];\n");
            cfgStream.write("edge [tailport = s];\n");

            writeCFG(entryBlock);

            cfgStream.write("}");
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally {
            try {
                cfgStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void WriteToSSAFile() {
        try {
            ssaStream.write("digraph tl13cfg {\n");
            ssaStream.write("node [shape = record];\n");
            ssaStream.write("edge [tailport = s];\n");

            String instructions = "entry";

            ssaStream.write("Entry" + "[shape = record, label = \"" + instructions + "\"]");
            ssaStream.write(";\n");

            writeSSA(DominatorTree.blockNode.get(0));

            ssaStream.write("Entry -> " + DominatorTree.blockNode.get(0).CFGBlock.blockID + ";\n");

            ssaStream.write("}");
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally {
            try {
                ssaStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void WriteToDOMFile() {
        try {
            domStream.write("digraph tl13cfg {\n");
            domStream.write("node [shape = record];\n");
            domStream.write("edge [tailport = s];\n");

            writeDOM();

            domStream.write("}");
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally {
            try {
                domStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void WriteToMIPSFile(MIPSInstSet mipsInst) {
        try {
            mipsStream.write("\t.data\n");
            mipsStream.write("newline:	.asciiz \"\\n\"\n");
            mipsStream.write("\t.text\n");
            mipsStream.write("\t.globl main\n");
            mipsStream.write("main:\n");
            mipsStream.write("\tli $fp, 0x7ffffffc");

            writeMIPS(mipsInst);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                mipsStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void writeEdge(int node1, int node2) {
        try {
            fileStream.write("  n" + node1 + " -> " + "n" + node2 + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeHeader() {
        try {
            fileStream.write("digraph parseTree {" + "\n");
            fileStream.write("  ordering=out;" + "\n");
            fileStream.write("  node [shape = box, style = filled];" + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFooter() {
        try {
            fileStream.write("}\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFile(String fileName) {
        try {
            fileStream = new FileWriter(fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openCFGFile(String fileName) {
        try {
            cfgStream = new FileWriter(fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openDOMFile(String fileName) {
        try {
            domStream = new FileWriter(fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openMIPSFile(String fileName) {
        try {
            mipsStream = new FileWriter(fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openSSAFile(String fileName) {
        try {
            ssaStream = new FileWriter(fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            fileStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
