package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class ASTListener extends ICSSBaseListener {

    private AST ast;
    private IHANStack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
        ast.root = stylesheet;
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = (Stylerule) currentContainer.pop();
        currentContainer.peek().addChild(stylerule);
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        Selector selector;
        if (ctx.ID_IDENT() != null) {
            selector = new IdSelector(ctx.ID_IDENT().getText());
        } else if (ctx.CLASS_IDENT() != null) {
            selector = new ClassSelector(ctx.CLASS_IDENT().getText());
        } else {
            selector = new TagSelector(ctx.getText());
        }
        currentContainer.push(selector);
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
        Selector selector = (Selector) currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = (Declaration) currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        PropertyName propertyName = new PropertyName(ctx.getText());
        currentContainer.push(propertyName);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
        PropertyName propertyName = (PropertyName) currentContainer.pop();
        currentContainer.peek().addChild(propertyName);
    }

    @Override
    public void enterVariable(ICSSParser.VariableContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        VariableReference varRef = new VariableReference(ctx.VAR_IDENT().getText());
        variableAssignment.addChild(varRef);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariable(ICSSParser.VariableContext ctx) {
        VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVarident(ICSSParser.VaridentContext ctx) {
        VariableReference varRef = new VariableReference(ctx.VAR_IDENT().getText());
        currentContainer.push(varRef);
    }

    @Override
    public void exitVarident(ICSSParser.VaridentContext ctx) {
        if (currentContainer.peek() instanceof VariableReference) {
            VariableReference varRef = (VariableReference) currentContainer.pop();
            currentContainer.peek().addChild(varRef);
        }
    }

    @Override
    public void enterColor(ICSSParser.ColorContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.COLOR().getText());
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColor(ICSSParser.ColorContext ctx) {
        if (currentContainer.peek() instanceof ColorLiteral) {
            ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
            currentContainer.peek().addChild(colorLiteral);
        }
    }

    @Override
    public void enterPixelsize(ICSSParser.PixelsizeContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.PIXELSIZE().getText());
        currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixelsize(ICSSParser.PixelsizeContext ctx) {
        if (currentContainer.peek() instanceof PixelLiteral) {
            PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
            currentContainer.peek().addChild(pixelLiteral);
        }
    }

    @Override
    public void enterPercentage(ICSSParser.PercentageContext ctx) {
        PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.PERCENTAGE().getText());
        currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentage(ICSSParser.PercentageContext ctx) {
        if (currentContainer.peek() instanceof PercentageLiteral) {
            PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
            currentContainer.peek().addChild(percentageLiteral);
        }
    }

    @Override
    public void enterScalar(ICSSParser.ScalarContext ctx) {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.SCALAR().getText());
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalar(ICSSParser.ScalarContext ctx) {
        if (currentContainer.peek() instanceof ScalarLiteral) {
            ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
            currentContainer.peek().addChild(scalarLiteral);
        }
    }

    @Override
    public void enterTruebool(ICSSParser.TrueboolContext ctx) {
        BoolLiteral boolLiteral = new BoolLiteral("TRUE");
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitTruebool(ICSSParser.TrueboolContext ctx) {
        if (currentContainer.peek() instanceof BoolLiteral) {
            BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
            currentContainer.peek().addChild(boolLiteral);
        }
    }

    @Override
    public void enterFalsebool(ICSSParser.FalseboolContext ctx) {
        BoolLiteral boolLiteral = new BoolLiteral("FALSE");
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitFalsebool(ICSSParser.FalseboolContext ctx) {
        if (currentContainer.peek() instanceof BoolLiteral) {
            BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
            currentContainer.peek().addChild(boolLiteral);
        }
    }

    @Override
    public void enterAddExpression(ICSSParser.AddExpressionContext ctx) {
        AddOperation addOperation = new AddOperation();
        currentContainer.push(addOperation);
    }

    @Override
    public void exitAddExpression(ICSSParser.AddExpressionContext ctx) {
        AddOperation addOperation = (AddOperation) currentContainer.pop();
        currentContainer.peek().addChild(addOperation);
    }

    @Override
    public void enterSubExpression(ICSSParser.SubExpressionContext ctx) {
        SubtractOperation subtractOperation = new SubtractOperation();
        currentContainer.push(subtractOperation);
    }

    @Override
    public void exitSubExpression(ICSSParser.SubExpressionContext ctx) {
        SubtractOperation subtractOperation = (SubtractOperation) currentContainer.pop();
        currentContainer.peek().addChild(subtractOperation);
    }

    @Override
    public void enterMulExpression(ICSSParser.MulExpressionContext ctx) {
        MultiplyOperation multiplyOperation = new MultiplyOperation();
        currentContainer.push(multiplyOperation);
    }

    @Override
    public void exitMulExpression(ICSSParser.MulExpressionContext ctx) {
        MultiplyOperation multiplyOperation = (MultiplyOperation) currentContainer.pop();
        currentContainer.peek().addChild(multiplyOperation);
    }

    @Override
    public void enterIf_clause(ICSSParser.If_clauseContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIf_clause(ICSSParser.If_clauseContext ctx) {
        IfClause ifClause = (IfClause) currentContainer.pop();
        currentContainer.peek().addChild(ifClause);
    }

    @Override
    public void enterNested_if_clause(ICSSParser.Nested_if_clauseContext ctx) {
        IfClause nestedIfClause = new IfClause();
        currentContainer.push(nestedIfClause);
    }

    @Override
    public void exitNested_if_clause(ICSSParser.Nested_if_clauseContext ctx) {
        IfClause nestedIfClause = (IfClause) currentContainer.pop();
        currentContainer.peek().addChild(nestedIfClause);
    }

    @Override
    public void enterElse_clause(ICSSParser.Else_clauseContext ctx) {
        ElseClause elseClause = new ElseClause();
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElse_clause(ICSSParser.Else_clauseContext ctx) {
        ElseClause elseClause = (ElseClause) currentContainer.pop();
        currentContainer.peek().addChild(elseClause);
    }

    @Override
    public void enterCondition(ICSSParser.ConditionContext ctx) {
        VariableReference condition = new VariableReference(ctx.getText());
        currentContainer.push(condition);
    }

    @Override
    public void exitCondition(ICSSParser.ConditionContext ctx) {
        VariableReference condition = (VariableReference) currentContainer.pop();
        currentContainer.peek().addChild(condition);
    }
}