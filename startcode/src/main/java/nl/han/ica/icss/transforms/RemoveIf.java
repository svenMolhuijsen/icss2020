package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RemoveIf implements Transform {

    private HashMap<String, Expression> variableReferences;

    @Override
    public void apply(AST ast) {
        findIfClauses(ast.root, ast.root.getChildren());
    }

    private void findIfClauses(ASTNode parent, List<ASTNode> children) {
        for (ASTNode child : children) {
            if (child instanceof IfClause) {
                removeIfClause((IfClause) child, parent);
            }
            findIfClauses(child, child.getChildren());
        }
    }

    //RETURNS list of ASTNode, used for recursion within tree
    private List<ASTNode> removeIfClause(IfClause ifClause, ASTNode parent) {
        List<ASTNode> values = new ArrayList<>();

        if (ifClause.conditionalExpression instanceof BoolLiteral) {
            //checks if if clause is true
            if (((BoolLiteral) ifClause.conditionalExpression).value) {
                //if true put all values in List
                for (ASTNode node : ifClause.body) {
                    if (node instanceof IfClause) {
                        //If one of the children in the body is a boolLiteral do Recursion and add the result to this list
                        values.addAll(removeIfClause((IfClause) node, ifClause));
                    } else {
                        //else add all
                        values.add(node);
                    }
                }

            }
        }

        if (parent instanceof Stylerule) {
            //removechild did not work
            Stylerule stylerule = (Stylerule) parent;
            stylerule.body.remove(ifClause);
        } else {
            parent.removeChild(ifClause);
        }

        //pass on children of if clause to parentNode
        if (!(parent instanceof IfClause)) {
            for (ASTNode node : values) {
                parent.addChild(node);
            }
        }

        //to make recursion work
        return values;
    }

}
