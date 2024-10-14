package nl.han.ica.icss.parser;

import java.util.Stack;


import com.sun.jdi.Value;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.management.ValueExp;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
//	private IHANStack<ASTNode> currentContainer;
    private Stack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		//currentContainer = new HANStack<>();
        currentContainer = new Stack<>();
	}
    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        //stylesheet -> stylerule -> variable | selector
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
        Selector selector = new TagSelector(ctx.getText());
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
    public void enterValue(ICSSParser.ValueContext ctx) {
        Expression expression = null;
        if (ctx.COLOR() != null) {
            expression = new ColorLiteral(ctx.COLOR().getText());
        } else if (ctx.PIXELSIZE() != null) {
            expression = new PixelLiteral(ctx.PIXELSIZE().getText());
        } else if (ctx.PERCENTAGE() != null) {
            expression = new PercentageLiteral(ctx.PERCENTAGE().getText());
        } else if (ctx.SCALAR() != null) {
            expression = new ScalarLiteral(ctx.SCALAR().getText());
        } else if (ctx.TRUE() != null || ctx.FALSE() != null) {
            expression = new BoolLiteral(ctx.getText());
        }
        if (expression != null) {
            currentContainer.push(expression);
        }
    }

    @Override
    public void exitValue(ICSSParser.ValueContext ctx) {
        if (!currentContainer.isEmpty() && currentContainer.peek() instanceof Expression) {
            Expression value = (Expression) currentContainer.pop();
            if (!currentContainer.isEmpty() && currentContainer.peek() instanceof Declaration) {
                Declaration declaration = (Declaration) currentContainer.peek();
                declaration.expression = value;
            } else {
                currentContainer.peek().addChild(value);
            }
        }
    }

    @Override
    public void enterVariable(ICSSParser.VariableContext ctx) {
        Expression variableReference = new VariableReference(ctx.VAR_IDENT().getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariable(ICSSParser.VariableContext ctx) {
        Expression variableReference = (Expression) currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
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
    public void enterOperator(ICSSParser.OperatorContext ctx) {
        Operation operation = null;
        if (ctx.PLUS() != null) {
            operation = new AddOperation();
        } else if (ctx.MIN() != null) {
            operation = new SubtractOperation();
        } else if (ctx.MUL() != null) {
            operation = new MultiplyOperation();
        }
        if (operation != null) {
            currentContainer.push(operation);
        }
    }

    @Override
    public void exitOperator(ICSSParser.OperatorContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }
}