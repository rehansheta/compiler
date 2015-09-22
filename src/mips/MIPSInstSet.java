/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mips;

import iloc.Block;
import iloc.ILOCConstants;
import iloc.ILOCInstruction;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Rehan
 */
public class MIPSInstSet {

    public static int offset = 0;
    public static int tempCount = 0;
    public ArrayList<ILOCInstruction> mipsInstSet = new ArrayList();
    public static HashMap<String, Integer> symTableOffset = new HashMap();

    public MIPSInstSet() {
    }

    public void addMipsInst(ILOCInstruction inst) {
        //TODO:: create mips instructions here
        if (!inst.opCode.startsWith("B")) {
            ILOCInstruction mipsCOMMENT = new ILOCInstruction();
            mipsCOMMENT.opCode = "# " + inst.opCode;
            mipsCOMMENT.src1Operand = inst.src1Operand;
            mipsCOMMENT.src2Operand = inst.src2Operand;
            mipsCOMMENT.destOperand = inst.destOperand;

            this.mipsInstSet.add(mipsCOMMENT);
        } else {
            this.mipsInstSet.add(inst);
        }

        if (inst.opCode.equalsIgnoreCase(ILOCConstants.LOADI)) {

            ILOCInstruction mipsLOADI = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOADI.opCode = MIPSConstants.LI;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOADI.destOperand = "$t" + tempCount;
            mipsSTORE.destOperand = "$t" + tempCount;
            //tempCount++;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }
            //if (inst.src1Operand.contains("r")) {
            //mipsLOAD.opCode = MIPSConstants.LW;
            // } else {

            mipsLOADI.src1Operand = inst.src1Operand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";
            //}
            this.mipsInstSet.add(mipsLOADI);
            this.mipsInstSet.add(mipsSTORE);


        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.I2I)) {
            ILOCInstruction mipsLOAD = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD.opCode = MIPSConstants.LW;
            mipsADD.opCode = MIPSConstants.ADD;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD.destOperand = "$t" + tempCount;
            mipsADD.destOperand = "$t" + tempCount;
            mipsSTORE.destOperand = "$t" + tempCount;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsADD.src1Operand = mipsADD.destOperand;
            mipsADD.src2Operand = "$zero";
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            this.mipsInstSet.add(mipsLOAD);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.ADD)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsADD.opCode = MIPSConstants.ADDU;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsADD.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsADD.src1Operand = mipsLOAD1.destOperand;
            mipsADD.src2Operand = mipsLOAD2.destOperand;

            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.SUB)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSUB = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSUB.opCode = MIPSConstants.SUBU;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSUB.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSUB.src1Operand = mipsLOAD1.destOperand;
            mipsSUB.src2Operand = mipsLOAD2.destOperand;

            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSUB);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.MULT)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsMULT = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsMULT.opCode = MIPSConstants.MUL;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsMULT.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsMULT.src1Operand = mipsLOAD1.destOperand;
            mipsMULT.src2Operand = mipsLOAD2.destOperand;

            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsMULT);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.DIV)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsDIV = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsDIV.opCode = MIPSConstants.DIV;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsDIV.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsDIV.src1Operand = mipsLOAD1.destOperand;
            mipsDIV.src2Operand = mipsLOAD2.destOperand;

            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsDIV);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.MOD)) {
            //rem rdest, rsrc1, rsrc2
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsMOD = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsMOD.opCode = MIPSConstants.REM;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsMOD.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsMOD.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsMOD.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsMOD);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CBR)) {
            ILOCInstruction mipsLOAD = new ILOCInstruction();
            ILOCInstruction mipsBNE = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();       // ??
            ILOCInstruction mipsJUMP = new ILOCInstruction();

            mipsLOAD.opCode = MIPSConstants.LW;
            mipsBNE.opCode = MIPSConstants.BNE;
            mipsSTORE.opCode = MIPSConstants.SW;
            mipsJUMP.opCode = MIPSConstants.J;

            mipsLOAD.destOperand = "$t" + tempCount;
            mipsBNE.destOperand = mipsLOAD.destOperand;
            mipsSTORE.destOperand = mipsLOAD.destOperand;
            mipsJUMP.destOperand = inst.destOperand;

            mipsLOAD.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsBNE.src1Operand = "$zero";
            mipsSTORE.src1Operand = mipsLOAD.src1Operand;

            mipsBNE.src2Operand = inst.src2Operand;

            this.mipsInstSet.add(mipsLOAD);
            this.mipsInstSet.add(mipsBNE);
            this.mipsInstSet.add(mipsSTORE);
            this.mipsInstSet.add(mipsJUMP);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_E)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSEQ = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSEQ.opCode = MIPSConstants.SEQ;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSEQ.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSEQ.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSEQ.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSEQ);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_GE)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSGE = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSGE.opCode = MIPSConstants.SGE;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSGE.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSGE.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSGE.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSGE);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_GT)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSGT = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSGT.opCode = MIPSConstants.SGT;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSGT.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSGT.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSGT.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSGT);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_LE)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSLE = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSLE.opCode = MIPSConstants.SLE;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSLE.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSLE.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSLE.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSLE);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_LT)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSLT = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSLT.opCode = MIPSConstants.SLT;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSLT.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSLT.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSLT.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSLT);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.CMP_NE)) {
            ILOCInstruction mipsLOAD1 = new ILOCInstruction();
            ILOCInstruction mipsLOAD2 = new ILOCInstruction();
            ILOCInstruction mipsSNE = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD1.opCode = MIPSConstants.LW;
            mipsLOAD2.opCode = MIPSConstants.LW;
            mipsSNE.opCode = MIPSConstants.SNE;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD1.destOperand = "$t" + tempCount;
            tempCount++;
            mipsLOAD2.destOperand = "$t" + tempCount;
            mipsSNE.destOperand = mipsLOAD1.destOperand;
            mipsSTORE.destOperand = mipsLOAD1.destOperand;
            tempCount--;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD1.src1Operand = symTableOffset.get(inst.src1Operand) + "($fp)";
            mipsLOAD2.src1Operand = symTableOffset.get(inst.src2Operand) + "($fp)";
            mipsSNE.src1Operand = mipsLOAD1.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsSNE.src2Operand = mipsLOAD2.destOperand;

            this.mipsInstSet.add(mipsLOAD1);
            this.mipsInstSet.add(mipsLOAD2);
            this.mipsInstSet.add(mipsSNE);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.READINT)) {
            ILOCInstruction mipsLOAD = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();

            mipsLOAD.opCode = MIPSConstants.LI;
            mipsSYSCALL.opCode = MIPSConstants.SYSCALL;
            mipsADD.opCode = MIPSConstants.ADD;
            mipsSTORE.opCode = MIPSConstants.SW;

            mipsLOAD.destOperand = "$v0";
            mipsADD.destOperand = "$t" + tempCount;
            mipsSTORE.destOperand = "$t" + tempCount;

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD.src1Operand = "5";
            mipsADD.src1Operand = mipsLOAD.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";

            mipsADD.src2Operand = "$zero";

            this.mipsInstSet.add(mipsLOAD);
            this.mipsInstSet.add(mipsSYSCALL);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSTORE);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.READCHAR)) {
            ILOCInstruction mipsLOAD = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSTORE = new ILOCInstruction();
            
            ILOCInstruction mipsLOADI2 = new ILOCInstruction();
            ILOCInstruction mipsLOADA = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL2 = new ILOCInstruction();

            mipsLOAD.opCode = MIPSConstants.LI;
            mipsSYSCALL.opCode = MIPSConstants.SYSCALL;
            mipsADD.opCode = MIPSConstants.ADD;
            mipsSTORE.opCode = MIPSConstants.SW;
            
            mipsLOADI2.opCode = MIPSConstants.LI;
            mipsLOADA.opCode = MIPSConstants.LA;
            mipsSYSCALL2.opCode = MIPSConstants.SYSCALL;

            mipsLOAD.destOperand = "$v0";
            mipsADD.destOperand = "$t" + tempCount;
            mipsSTORE.destOperand = "$t" + tempCount;
            
            mipsLOADI2.destOperand = "$v0";
            mipsLOADA.destOperand = "$a0";

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOAD.src1Operand = "12";
            mipsADD.src1Operand = mipsLOAD.destOperand;
            mipsSTORE.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";
            
            mipsLOADI2.src1Operand = "4";
            mipsLOADA.src1Operand = "newline";

            mipsADD.src2Operand = "$zero";

            this.mipsInstSet.add(mipsLOAD);
            this.mipsInstSet.add(mipsSYSCALL);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSTORE);
            
            this.mipsInstSet.add(mipsLOADI2);
            this.mipsInstSet.add(mipsLOADA);
            this.mipsInstSet.add(mipsSYSCALL2);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.WRITEINT)) {
            ILOCInstruction mipsLOADI1 = new ILOCInstruction();
            ILOCInstruction mipsLOADW = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL1 = new ILOCInstruction();

            ILOCInstruction mipsLOADI2 = new ILOCInstruction();
            ILOCInstruction mipsLOADA = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL2 = new ILOCInstruction();

            mipsLOADI1.opCode = MIPSConstants.LI;
            mipsLOADW.opCode = MIPSConstants.LW;
            mipsADD.opCode = MIPSConstants.ADD;
            mipsSYSCALL1.opCode = MIPSConstants.SYSCALL;

            mipsLOADI2.opCode = MIPSConstants.LI;
            mipsLOADA.opCode = MIPSConstants.LA;
            mipsSYSCALL2.opCode = MIPSConstants.SYSCALL;

            mipsLOADI1.destOperand = "$v0";
            mipsLOADW.destOperand = "$t" + tempCount;
            mipsADD.destOperand = "$a0";
            mipsLOADI2.destOperand = "$v0";
            mipsLOADA.destOperand = "$a0";

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOADI1.src1Operand = "1";
            mipsLOADW.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";
            mipsADD.src1Operand = mipsLOADW.destOperand;
            mipsLOADI2.src1Operand = "4";
            mipsLOADA.src1Operand = "newline";

            mipsADD.src2Operand = "$zero";

            this.mipsInstSet.add(mipsLOADI1);
            this.mipsInstSet.add(mipsLOADW);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSYSCALL1);

            this.mipsInstSet.add(mipsLOADI2);
            this.mipsInstSet.add(mipsLOADA);
            this.mipsInstSet.add(mipsSYSCALL2);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.WRITECHAR)) {
            ILOCInstruction mipsLOADI1 = new ILOCInstruction();
            ILOCInstruction mipsLOADW = new ILOCInstruction();
            ILOCInstruction mipsADD = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL1 = new ILOCInstruction();

            ILOCInstruction mipsLOADI2 = new ILOCInstruction();
            ILOCInstruction mipsLOADA = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL2 = new ILOCInstruction();

            mipsLOADI1.opCode = MIPSConstants.LI;
            mipsLOADW.opCode = MIPSConstants.LW;
            mipsADD.opCode = MIPSConstants.ADD;
            mipsSYSCALL1.opCode = MIPSConstants.SYSCALL;

            mipsLOADI2.opCode = MIPSConstants.LI;
            mipsLOADA.opCode = MIPSConstants.LA;
            mipsSYSCALL2.opCode = MIPSConstants.SYSCALL;

            mipsLOADI1.destOperand = "$v0";
            mipsLOADW.destOperand = "$t" + tempCount;
            mipsADD.destOperand = "$a0";
            mipsLOADI2.destOperand = "$v0";
            mipsLOADA.destOperand = "$a0";

            if (!symTableOffset.containsKey(inst.destOperand)) {
                symTableOffset.put(inst.destOperand, offset);
                offset = offset - 4;
                //mipsInst.
            }

            mipsLOADI1.src1Operand = "11";
            mipsLOADW.src1Operand = symTableOffset.get(inst.destOperand) + "($fp)";
            mipsADD.src1Operand = mipsLOADW.destOperand;
            mipsLOADI2.src1Operand = "4";
            mipsLOADA.src1Operand = "newline";

            mipsADD.src2Operand = "$zero";

            this.mipsInstSet.add(mipsLOADI1);
            this.mipsInstSet.add(mipsLOADW);
            this.mipsInstSet.add(mipsADD);
            this.mipsInstSet.add(mipsSYSCALL1);

            this.mipsInstSet.add(mipsLOADI2);
            this.mipsInstSet.add(mipsLOADA);
            this.mipsInstSet.add(mipsSYSCALL2);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.EXIT)) {
            ILOCInstruction mipsLOAD = new ILOCInstruction();
            ILOCInstruction mipsSYSCALL = new ILOCInstruction();

            mipsLOAD.opCode = MIPSConstants.LI;
            mipsSYSCALL.opCode = MIPSConstants.SYSCALL;

            mipsLOAD.destOperand = "$v0";
            mipsLOAD.src1Operand = "10";

            this.mipsInstSet.add(mipsLOAD);
            this.mipsInstSet.add(mipsSYSCALL);

        } else if (inst.opCode.equalsIgnoreCase(ILOCConstants.JUMPI)) {
            ILOCInstruction mipsJUMP = new ILOCInstruction();

            mipsJUMP.opCode = MIPSConstants.J;
            mipsJUMP.destOperand = inst.destOperand;

            this.mipsInstSet.add(mipsJUMP);
        }
        //this.mipsInstSet.add(inst);
    }

    public ILOCInstruction getMipsInst(int index) {
        return this.mipsInstSet.get(index);
    }

    public void printMipsInst() {
        for (int i = 0; i < mipsInstSet.size(); i++) {
            ILOCInstruction inst = this.getMipsInst(i);

            if (inst.opCode.startsWith("B")) {
                System.out.println(inst.opCode);

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
                }  else if (inst.opCode.equalsIgnoreCase("# " + ILOCConstants.WRITECHAR)) {
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
                System.out.println("\n\t" + wholeInst);

            } else {
                if ((inst.destOperand == null) && (inst.src1Operand == null) && (inst.src2Operand == null)) {
                    System.out.println("\t" + inst.opCode);
                } else if ((inst.src1Operand == null) && (inst.src2Operand == null)) {
                    System.out.println("\t" + inst.opCode + " " + inst.destOperand);
                } else if ((inst.src2Operand == null)) {
                    System.out.println("\t" + inst.opCode + " " + inst.destOperand + ", " + inst.src1Operand);
                } else {
                    System.out.println("\t" + inst.opCode + " " + inst.destOperand + ", " + inst.src1Operand + ", " + inst.src2Operand);

                }
            }
        }
    }

    public void genMIPSInst(Block entryBlock) {
        if (entryBlock.visitedForMIPS == true) {
            return;
        }
        entryBlock.visitedForMIPS = true;

        if (entryBlock.blockID.startsWith("B")) {
            ILOCInstruction inst = new ILOCInstruction();
            inst.opCode = entryBlock.blockID + ":";
            addMipsInst(inst);
        }
        for (int j = 0; j < entryBlock.instructions.size(); j++) {
            addMipsInst(entryBlock.instructions.get(j));
        }

        for (int i = 0; i < entryBlock.childBlock.size(); i++) {
            genMIPSInst(entryBlock.childBlock.get(i));
        }
    }
}
