package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private final AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private final Stack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.root = stylesheet;
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        Stylerule rule = new Stylerule();
        currentContainer.peek().addChild(rule);
        currentContainer.push(rule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        TerminalNode tag = ctx.LOWER_IDENT();
        TerminalNode id = ctx.ID_IDENT();
        TerminalNode classs = ctx.CLASS_IDENT();


        if (tag != null) {
            currentContainer.peek().addChild(new TagSelector(ctx.children.get(0).getText()));
            return;
        } else if (id != null) {
            currentContainer.peek().addChild(new IdSelector(ctx.children.get(0).getText()));
            return;
        } else if (classs != null) {
            currentContainer.peek().addChild(new ClassSelector(ctx.children.get(0).getText()));
            return;
        }

    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();//!IMPORTANT TO GET UP ONE LEVEL
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        currentContainer.peek().addChild(new PropertyName(ctx.children.get(0).getText()));
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        currentContainer.peek().addChild(new ColorLiteral(ctx.COLOR().toString()));
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        currentContainer.peek().addChild(new PercentageLiteral(ctx.PERCENTAGE().toString()));
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        currentContainer.peek().addChild(new PixelLiteral(ctx.PIXELSIZE().toString()));
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        currentContainer.peek().addChild(new BoolLiteral(ctx.getText()));
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        currentContainer.peek().addChild(new ScalarLiteral(ctx.SCALAR().toString()));
    }


    //OPERATIONS
    @Override
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        MultiplyOperation mul = new MultiplyOperation();
        currentContainer.peek().addChild(mul);
        currentContainer.push(mul);
    }

    @Override
    public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        currentContainer.pop();

    }

    @Override
    public void enterAddOperation(ICSSParser.AddOperationContext ctx) {
        AddOperation add = new AddOperation();
        currentContainer.peek().addChild(add);
        currentContainer.push(add);
    }

    @Override
    public void exitAddOperation(ICSSParser.AddOperationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        SubtractOperation min = new SubtractOperation();
        currentContainer.peek().addChild(min);
        currentContainer.push(min);
        return;
    }

    @Override
    public void exitSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        currentContainer.pop();
    }

    //CALCULATION
    //TODO LET THIS WORK WITHOUT LABELS (see g4 file)
//    @Override
//    public void enterOperation(ICSSParser.OperationContext ctx) {
//        TerminalNode PLUS = ctx.;
//        TerminalNode MIN = ctx.MIN();
//        TerminalNode  MUL = ctx.MUL();
//
//        if(PLUS != null){
//            AddOperation add = new AddOperation();
//            currentContainer.peek().addChild(add);
//            currentContainer.push(add);
//            return;
//        }else if(MIN != null){
//            SubtractOperation min = new SubtractOperation();
//            currentContainer.peek().addChild(min);
//            currentContainer.push(min);
//            return;
//        }else if(MUL != null){
//            MultiplyOperation mul = new MultiplyOperation();
//            currentContainer.peek().addChild(mul);
//            currentContainer.push(mul);
//            return;
//
//        }
//    }
//
//    @Override
//    public void exitOperation(ICSSParser.OperationContext ctx) {
//
//    }


    //VARAIBLES
    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        currentContainer.peek().addChild(new VariableReference(ctx.CAPITAL_IDENT().toString()));
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        currentContainer.pop();
    }

    //IFELSE
    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.peek().addChild(ifClause);
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        currentContainer.pop();
    }

}
