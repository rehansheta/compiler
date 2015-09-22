package edu.utsa.tl13;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
/**
 *
 * @author Rehan
 */
public class TL13Scanner {

    public Reader scanTL13;
    private Scanner scanner;
    private ArrayList<String> tokens = new ArrayList();
    private int tokenIndex = 0;

    public TL13Scanner(String fileName) {
        try {
            this.scanner = new Scanner(new File(fileName));
            tokenizeProgram();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TL13Scanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tokenizeProgram() {
        String[] delimiters = {";", "(", ")", ":=", "*", "div", "mod", "+", "-", "=", "!=", "<", ">", "<=", ">=", " ", "/#", "#\\"};
        StringBuilder regexp = new StringBuilder("");
        for (String s : delimiters) {
            if (regexp.length() > 1) { // We don't want to start with (|
                regexp.append("|");
            }
            regexp.append(Pattern.quote(s));
        }
        String finalRegexp = "(?<=" + regexp + ")|(?=" + regexp + ")";

        boolean multiline = false;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String tempTokens[] = line.trim().split(finalRegexp);
            int idx = 0;
            while (idx < tempTokens.length && !tempTokens[idx].startsWith("%")) {
                // multiline skipping starts
                if (tempTokens[idx].startsWith("/#")) {
                    multiline = true;
                    idx++;
                    while (idx < tempTokens.length && !tempTokens[idx].endsWith("#\\")) {
                        idx++;
                    }
                    if (idx < tempTokens.length && tempTokens[idx].endsWith("#\\")) {
                        multiline = false;
                        idx++;
                    }
                    continue;
                } else if (multiline && tempTokens[idx].endsWith("#\\")) {
                    multiline = false;
                    idx++;
                    continue;
                } else if (multiline) {
                    idx++;
                    continue;
                }
                // multiline skipping ends
                
                if (tempTokens[idx].trim().length() > 0) {
                    if (tempTokens[idx].startsWith(":") || tempTokens[idx].startsWith("!") || tempTokens[idx].startsWith("<") || tempTokens[idx].startsWith(">")) {
                        if ((idx + 1 < tempTokens.length) && "=".equals(tempTokens[idx + 1])) {
                            tokens.add(tempTokens[idx] + tempTokens[idx + 1]);
                            idx++;
                        } else {
                            tokens.add(tempTokens[idx]);
                            idx++;
                        }
                    } else {
                        tokens.add(tempTokens[idx]);
                    }
                }
                idx++;
            }
        }
    }

    public Token getToken() {
        String tokenValue = "";
        if (tokenIndex == tokens.size()) {
            tokenValue = "";
        } else {
            tokenValue = tokens.get(tokenIndex++);
        }
        Compiler.tokenValue = tokenValue;
//            System.out.println("TOKEN : " + tokenValue);

        if (tokenValue.length() < 1) {
            return Token.EOF;
        }

        // Symbols and Operators
        if (tokenValue.equals("(")) {
            return Token.LP;
        } else if (tokenValue.equals(")")) {
            return Token.RP;
        } else if (tokenValue.equals(":=")) {
            return Token.ASGN;
        } else if (tokenValue.equals(";")) {
            return Token.SC;
        } else if (tokenValue.equals("*") || tokenValue.equals("div") || tokenValue.equals("mod")) {
            return Token.OP2;
        } else if (tokenValue.equals("+") || tokenValue.equals("-")) {
            return Token.OP3;
        } else if (tokenValue.equals("=") || tokenValue.equals("!=") || tokenValue.equals("<") || tokenValue.equals(">") || tokenValue.equals("<=") || tokenValue.equals(">=")) {
            return Token.OP4;
        } // Keywords
        else if (tokenValue.equals("if")) {
            return Token.IF;
        } else if (tokenValue.equals("then")) {
            return Token.THEN;
        } else if (tokenValue.equals("else")) {
            return Token.ELSE;
        } else if (tokenValue.equals("begin")) {
            return Token.BEGIN;
        } else if (tokenValue.equals("end")) {
            return Token.END;
        } else if (tokenValue.equals("while")) {
            return Token.WHILE;
        } else if (tokenValue.equals("do")) {
            return Token.DO;
        } else if (tokenValue.equals("program")) {
            return Token.PROGRAM;
        } else if (tokenValue.equals("var")) {
            return Token.VAR;
        } else if (tokenValue.equals("as")) {
            return Token.AS;
        } else if (tokenValue.equals("int")) {
            return Token.INT;
        } else if (tokenValue.equals("bool")) {
            return Token.BOOL;
        } else if (tokenValue.equals("char")) {
            return Token.CHAR;
        } // Built-in Procedures
        else if (tokenValue.equals("writeInt")) {
            return Token.WRITEINT;
        } else if (tokenValue.equals("writeChar")) {
            return Token.WRITECHAR;
        } else if (tokenValue.equals("readInt")) {
            return Token.READINT;
        } else if (tokenValue.equals("readChar")) {
            return Token.READCHAR;
        } // Numbers, Literals, and Identifiers
        else if (tokenValue.equals("false") || tokenValue.equals("true")) {
            return Token.boollit;
        } else if (Pattern.matches("[1-9][0-9]*", tokenValue) || tokenValue.equals("0")) {
            return Token.num;
        } else if (Pattern.matches("[A-Z][A-Z0-9]*", tokenValue)) {
            return Token.ident;
        } else if (Pattern.matches("'.'", tokenValue) || tokenValue.equals("'\\r'")
                || tokenValue.equals("'\\n'") || tokenValue.equals("'\\t'")
                || tokenValue.equals("'\\0'")) {
            return Token.character;
        }
        return Token.INVALID;
    }
}
