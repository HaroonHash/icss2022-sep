package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

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