package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import org.w3c.dom.css.CSSStyleRule;

public class Generator {

	public String generate(AST ast) {
        return generateStyleSheet((Stylesheet) ast.root);
	}

//    private String generateStyleSheet(Stylesheet root) {
////        return generateStylerule((Stylerule)root.getChildren().get(0));
//        String result = "";
//        for (ASTNode child : root.getChildren()) {
//            if (child instanceof Stylerule) {
//                result =  generateStylerule((Stylerule) child);
//            }
//        }
//        return result;
//    }

    private String generateStyleSheet(Stylesheet root) {
        String result = "";
        for (ASTNode child : root.getChildren()) {
            if (child instanceof Stylerule) {
                result += generateStylerule((Stylerule) child);
            }
        }
        return result;
    }

//    private String generateStylerule(Stylerule stylerule) {
//        String result = stylerule.selectors.get(0).toString() + " {\n";
////        result += generateDeclaration((Declaration) stylerule.getChildren().get(0));
//
//        result += "/t" + generateDeclaration(stylerule.body.get(0));
//        result += "}\n";
//        return result;
//    }

    private String generateStylerule(Stylerule stylerule) {
        String result = stylerule.selectors.get(0).toString() + " {\n";
        for (ASTNode child : stylerule.body) {
            if (child instanceof Declaration) {
                result += generateDeclaration((Declaration) child) + "\n";
            }
        }
        result += "}\n";
        return result;
    }

//    private String generateDeclaration(ASTNode astNode) {
//        return "Declaration";
////        Declaration declaration = (Declaration) astNode;
////        return declaration.property.name + ": " + declaration.expression.toString() + ";\n";
//    }

    private String generateDeclaration(Declaration declaration) {
        return "  " + declaration.property.name + ": " + formatExpression(declaration.expression) + ";";
    }

    private String formatExpression(Expression expression) {
        if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        } else if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        } else if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        } else if (expression instanceof ScalarLiteral) {
            return String.valueOf(((ScalarLiteral) expression).value);
        }
        return expression.toString();
    }

}
