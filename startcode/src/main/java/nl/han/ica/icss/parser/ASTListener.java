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
        if (ctx.COLOR() != null) {
            Expression colorLiteral = new ColorLiteral(ctx.COLOR().getText());
            currentContainer.push(colorLiteral);
        } else if (ctx.PIXELSIZE() != null) {
            Expression pixelLiteral = new PixelLiteral(ctx.PIXELSIZE().getText());
            currentContainer.push(pixelLiteral);
        } else if (ctx.PERCENTAGE() != null) {
            Expression percentageLiteral = new PercentageLiteral(ctx.PERCENTAGE().getText());
            currentContainer.push(percentageLiteral);
        } else if (ctx.TRUE() != null || ctx.FALSE() != null) {
            Expression boolLiteral = new BoolLiteral(ctx.getText());
            currentContainer.push(boolLiteral);
        }
    }

    @Override
    public void exitValue(ICSSParser.ValueContext ctx) {
        Expression value = (Expression) currentContainer.pop();
        currentContainer.peek().addChild(value);
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
        if (ctx.PLUS() != null) {
            AddOperation addOperation = new AddOperation();
            currentContainer.push(addOperation);
        } else if (ctx.MIN() != null) {
            SubtractOperation subtractOperation = new SubtractOperation();
            currentContainer.push(subtractOperation);
        } else if (ctx.MUL() != null) {
            MultiplyOperation multiplyOperation = new MultiplyOperation();
            currentContainer.push(multiplyOperation);
        }
    }

    @Override
    public void exitOperator(ICSSParser.OperatorContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

//    @Override
//    public void enterEveryRule(ParserRuleContext ctx) {
//        super.enterEveryRule(ctx);
//    } moet dit????

    //    @Override
//    public void enterValue(ICSSParser.ValueContext ctx) {
//        Value value = new Value();
//        Value value = (Value) currentContainer.peek();
//        if (ctx.COLOR() != null) {
//            value = new ColorLiteral(ctx.COLOR().getText());
//        } else if (ctx.PIXELSIZE() != null) {
//            value = new value(ctx.PIXELSIZE().getText());
//        } else if (ctx.PERCENTAGE() != null) {
//            value = new PercentageLiteral(ctx.PERCENTAGE().getText());
//        } else if (ctx.BOOL() != null) {
//            value = new BoolLiteral(ctx.BOOL().getText());
//        } else if (ctx.VARIABLE() != null) {
//            value = new VariableReference(ctx.VARIABLE().getText());
//        }
//    }

    //    @Override
//    public void enterSelector(ICSSParser.SelectorContext ctx) {
////        Selector selector = new Selector(ctx.getText());
//        Selector selector = new Selector("TAG");
//        currentContainer.push(selector);
//    }
//
//    @Override
//    public void exitSelector(ICSSParser.SelectorContext ctx) {
//        Selector selector = (Selector) currentContainer.pop();
//        currentContainer.peek().addChild(selector);
//    }

//    @Override
//    public void enterTagSelecter(ICSSParser.TagSelecterContext ctx) {
//        TagSelector tagSelector = new TagSelector(ctx.getText());
//        currentContainer.push(tagSelector);
//    }
//
//    @Override
//    public void exitTagSelecter(ICSSParser.TagSelecterContext ctx) {
//        TagSelector tagSelector = (TagSelector) currentContainer.pop();
//        currentContainer.peek().addChild(tagSelector);
//    }

}