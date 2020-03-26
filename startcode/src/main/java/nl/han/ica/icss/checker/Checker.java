package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

import static nl.han.ica.icss.ast.types.ExpressionType.*;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkRecursive(ast.root);
    }

    public void checkRecursive(ASTNode node) {
        if (node instanceof Stylesheet | node instanceof Stylerule) {
            variableTypes.addFirst(new HashMap<>());
        } else if (node instanceof VariableAssignment) {
            variableTypes.getFirst().put(((VariableAssignment) node).name.name, findType(((VariableAssignment) node).expression));
        }

        CH01checkVarExist(node);
        CH02checkOperands(node);
        CH03checkNoColourOperations(node);
        CH04checkRightType(node);
        CH04checkRightType(node);
        CH05checkifBooleanUsed(node);
        CH06checkifBooleanUsed(node);

        //check every subNode
        for (ASTNode n : node.getChildren()
        ) {
            checkRecursive(n);
        }
        if (node instanceof Stylesheet | node instanceof Stylerule) {
            variableTypes.pop();
        }
    }

    //    CH00	Minimaal drie van onderstaande checks moeten zijn geïmplementeerd	Must
//    CH01	Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn.	Should	4
    private void CH01checkVarExist(ASTNode node) {
        if (node instanceof VariableReference) {
            for (HashMap<String, ExpressionType> item : variableTypes) {
                if (item.containsKey(((VariableReference) node).name)) {
                    return;
                }
            }
            node.setError("Variable not defined");
        }
    }

    //    CH02	Controleer of de operanden van de operaties plus en min van gelijk type zijn en dat vermenigvuldigen enkel met scalaire waarden gebeurt. Je mag geen pixels bij percentages optellen bijvoorbeeld.	Should	4
    //    CH03	Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).	Should	2
    private void CH02checkOperands(ASTNode node) {
        if (node instanceof Operation) {
            ExpressionType left = findType(((Operation) node).lhs);
            ExpressionType right = findType(((Operation) node).rhs);

            if (node instanceof AddOperation || node instanceof SubtractOperation) {
                if (left != right) {
                    node.setError("Types not compatible");
                    return;
                }
            } else if (node instanceof MultiplyOperation) {
                if (left != SCALAR && right != SCALAR) {
                    node.setError("Types not compatible, cannot add pixels and percentages");
                    return;
                }
            }
            if (left == COLOR || right == COLOR) {
                node.setError("cannot use colors in operations");
                return;
            }
        }
    }

    private void CH03checkNoColourOperations(ASTNode node) {

    }

    //    CH04	Controleer of bij declaraties het type van de value klopt met de property. Declaraties zoals width: #ff0000 of color: 12px zijn natuurlijk onzin.	Should	2
    private void CH04checkRightType(ASTNode node) {
        if (node instanceof Declaration) {
            String left = ((Declaration) node).property.name;

            ExpressionType right = findType(((Declaration) node).expression);
            System.out.print(left);
            System.out.println(right);


            if ((left.equals("color") || left.equals("background-color")) && (right == COLOR)) {
                return;
            }
            if ((left.equals("width") || left.equals("height")) && (right == PIXEL || right == PERCENTAGE)) {
                return;
            }
            if ((left.equals("true") || left.equals("false")) && right == BOOL) {
                return;
            }
            //TODO: CHECK IN FUNCTIONS

//            node.setError("Values not compatible!");
        }
    }

    //    CH05	Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal)	Should	4
    private void CH05checkifBooleanUsed(ASTNode node) {

    }

    //    CH06	Controleer of variabelen enkel binnen hun scope gebruikt worden	Should	4
    private void CH06checkifBooleanUsed(ASTNode node) {

    }


    private ExpressionType findType(Expression expression) {
        if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ScalarLiteral) {
            return SCALAR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference) {
            for (HashMap<String, ExpressionType> item : variableTypes) {
                if (item.containsKey(((VariableReference) expression).name)) {
                    return item.get(((VariableReference) expression).name);
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }
}
