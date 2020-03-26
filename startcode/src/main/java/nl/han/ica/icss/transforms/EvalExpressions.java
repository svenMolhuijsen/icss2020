package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.push(new HashMap<>());

        findVariableAssignments(ast.root);
        replaceVarAssignments(ast.root);
        replaceCalculations(ast.root);
    }

    private void findVariableAssignments(ASTNode node) {


        if (node instanceof VariableAssignment) {
            VariableAssignment assignment = (VariableAssignment) node;
            Literal literal = (Literal) assignment.expression;
            variableValues.getFirst().put(assignment.name.name, literal);
        }

        for (ASTNode child : node.getChildren()) {
            findVariableAssignments(child);
        }
    }

    public void replaceVarAssignments(ASTNode node) {

        if (node instanceof Declaration) {
            Declaration declaration = ((Declaration) node);
            if (declaration.expression instanceof VariableReference) {
                declaration.expression = getVar((VariableReference) declaration.expression);
            }
        } else if (node instanceof Operation) {
            Operation operation = (Operation) node;
            if (operation.lhs instanceof VariableReference) {
                operation.lhs = getVar((VariableReference) operation.lhs);
            }
            if (operation.rhs instanceof VariableReference) {
                operation.rhs = getVar((VariableReference) operation.rhs);
            }
        } else if (node instanceof IfClause) {
            IfClause clause = (IfClause) node;
            if (clause.conditionalExpression instanceof VariableReference) {
                clause.conditionalExpression = getVar((VariableReference) clause.conditionalExpression);
            }
        }


        for (ASTNode child : node.getChildren()) {
            replaceVarAssignments(child);
        }
    }

    private Literal getVar(VariableReference ref) {
        for (HashMap<String, Literal> current : variableValues) {
            if (current.containsKey(ref.name))
                return current.get(ref.name);
        }
        return null;
    }

    private void replaceCalculations(ASTNode node) {
        if (node instanceof Declaration) {
            if (((Declaration) node).expression instanceof Operation) {
                Operation op = (Operation) ((Declaration) node).expression;
                if (op instanceof MultiplyOperation) {
                    ((Declaration) node).expression = createLiteral((Literal) op.lhs, getInt(op.lhs) * getInt(op.rhs));
                } else if (op instanceof AddOperation) {
                    ((Declaration) node).expression = createLiteral((Literal) op.lhs, getInt(op.lhs) + getInt(op.rhs));
                } else if (op instanceof SubtractOperation) {
                    ((Declaration) node).expression = createLiteral((Literal) op.lhs, getInt(op.lhs) - getInt(op.rhs));
                }
            }
        }
        for (ASTNode child : node.getChildren()) {
            replaceCalculations(child);
        }
    }

    private int getInt(Expression expression) {
        if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value;
        } else if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value;
        } else if (expression instanceof ScalarLiteral) {
            return ((ScalarLiteral) expression).value;
        }
        return 0;
    }

    private Literal createLiteral(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        }
        return null;
    }
}
