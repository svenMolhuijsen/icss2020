package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Generator {

    public String generate(AST ast) {
        String s = generateRecursive(ast.root);

        return s;

    }

    private String generateRecursive(ASTNode node) {
        String s = "";

        if (node instanceof Stylerule) {
            s += ((Stylerule) node).selectors.get(0);
            s += " {\n";
        }
        if (node instanceof Declaration) {
            s += "  " + ((Declaration) node).property.name + ": ";
            s += getExpressionString(((Declaration) node).expression) + ";\n";
        }

        //check every subNode
        for (ASTNode n : node.getChildren()
        ) {
            s += generateRecursive(n);
        }

        if (node instanceof Stylerule) {

            s += "}\n";
        }
        return s;
    }

    //chosen for this to eliminate refactoring due to regenaration of .g4 file
    private String getExpressionString(Expression exp) {
        if (exp instanceof ColorLiteral) {
            return ((ColorLiteral) exp).value;
        }
        if (exp instanceof PixelLiteral) {
            return ((PixelLiteral) exp).value + "px";
        }
        if (exp instanceof PercentageLiteral) {
            return ((PercentageLiteral) exp).value + "%";
        }
        if (exp instanceof ScalarLiteral) {
            return ((ScalarLiteral) exp).value + "";
        }
        return null;
    }


}
