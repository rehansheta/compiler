/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Rehan
 */
public class SymbleTable {

    public static HashMap<String, String> symTableType = new HashMap();

    public void insert(String var, String type, ASTDeclarations node) {
        if (!symTableType.containsKey(var)) {
            symTableType.put(var, type);
            node.type = ASTConstants.TYPE_SUCCESS_MESSAGE;
        } else {
            node.type = ASTConstants.TYPE_ERROR;
            System.err.println(ASTConstants.DUPLICATE_VAR_ERROR_MESSAGE.replace("######", node.value));
        }
    }

    public static String getType(String var) {
        return symTableType.get(var);
    }

    public static void print() {

        Iterator itr = symTableType.keySet().iterator();
        System.out.println("Symbol Table:");
        while (itr.hasNext()) {
            String var = (String) itr.next();
            System.out.println(var + " " + getType(var));
        }        
    }
}
