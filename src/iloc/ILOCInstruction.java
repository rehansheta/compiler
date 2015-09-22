/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iloc;

import java.io.Serializable;

/**
 *
 * @author Rehan
 */
public class ILOCInstruction implements Serializable {
    public String opCode;
    public String src1Operand;
    public String src2Operand;
    public String destOperand;
    
    public void genILOCInstruction() {
        
    }
    
    public String printILOC() {
        
        String wholeInst = this.opCode;
        
        if (this.opCode.equalsIgnoreCase(ILOCConstants.EXIT)) {
            
        }
        else if ((this.opCode.equalsIgnoreCase(ILOCConstants.LOADI)) || (this.opCode.equalsIgnoreCase(ILOCConstants.I2I))) {
            wholeInst += "\t" + this.src1Operand + " => " + this.destOperand;
        }
        else if ((this.opCode.equalsIgnoreCase(ILOCConstants.ADD)) || (this.opCode.equalsIgnoreCase(ILOCConstants.SUB)) ||
                (this.opCode.equalsIgnoreCase(ILOCConstants.MULT)) || (this.opCode.equalsIgnoreCase(ILOCConstants.DIV)) ||
                (this.opCode.equalsIgnoreCase(ILOCConstants.MOD))) {
            wholeInst += "\t" + this.src1Operand + ", " + this.src2Operand + " => " + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.READINT)) {
            wholeInst += "\t=> " + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.READCHAR)) {
            wholeInst += "\t=> " + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.WRITEINT)) {
            wholeInst += "\t" + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.WRITECHAR)) {
            wholeInst += "\t" + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.JUMPI)) {
            wholeInst += "\t-> " + this.destOperand;
        }
        else if ((this.opCode.equalsIgnoreCase(ILOCConstants.CMP_LE)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_GE) ||
                (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_LT)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_GT) ||
                (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_E)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_NE))))) {
            wholeInst += "\t" + this.src1Operand + ", " + this.src2Operand + " => " + this.destOperand;
        }
        else if (this.opCode.equalsIgnoreCase(ILOCConstants.CBR)) {
            wholeInst += "\t" + this.src1Operand + " -> " + this.src2Operand + ", " + this.destOperand;
        }
              
        //System.out.println(wholeInst);
        return wholeInst;
    }
    
    public String printSSA() {

        String wholeInst = " ";
        String arrow = " <= ";

        if (this.opCode.equalsIgnoreCase(ILOCConstants.EXIT)) {
        } else if ((this.opCode.equalsIgnoreCase(ILOCConstants.LOADI)) || (this.opCode.equalsIgnoreCase(ILOCConstants.I2I))) {
            wholeInst = this.destOperand + arrow + this.opCode + " " + this.src1Operand;
        } else if ((this.opCode.equalsIgnoreCase(ILOCConstants.ADD)) || (this.opCode.equalsIgnoreCase(ILOCConstants.SUB))
                || (this.opCode.equalsIgnoreCase(ILOCConstants.MULT)) || (this.opCode.equalsIgnoreCase(ILOCConstants.DIV))
                || (this.opCode.equalsIgnoreCase(ILOCConstants.MOD))) {
            wholeInst = this.destOperand + arrow + this.opCode + " " + this.src1Operand + ", " + this.src2Operand;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.READINT)) {
            wholeInst = this.destOperand + arrow + this.opCode;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.READCHAR)) {
            wholeInst = this.destOperand + arrow + this.opCode;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.WRITEINT)) {
            wholeInst = this.destOperand + "  " + this.opCode;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.WRITECHAR)) {
            wholeInst = this.destOperand + "  " + this.opCode;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.JUMPI)) {
            wholeInst = this.destOperand + arrow + this.opCode;
        } else if ((this.opCode.equalsIgnoreCase(ILOCConstants.CMP_LE)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_GE)
                || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_LT)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_GT)
                || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_E)) || (this.opCode.equalsIgnoreCase(ILOCConstants.CMP_NE))))) {
            wholeInst = this.destOperand + arrow + this.opCode + " " + this.src1Operand + ", " + this.src2Operand;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.CBR)) {
            wholeInst = this.opCode + " " + this.src1Operand + " -> " + this.src2Operand + ", " + this.destOperand;
        } else if (this.opCode.equalsIgnoreCase(ILOCConstants.PHIFUNC)) {
            wholeInst = this.destOperand + arrow + "phi" + " (" + this.src1Operand + ", " + this.src2Operand  + ")"; 
        } 
        //System.out.println(wholeInst);
        return wholeInst;
    }
}
