package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
        applyStyleSheet((Stylesheet) ast.root);

    }

    private void applyStyleSheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            applyStylerule((Stylerule) child);
        }
    }

    private void applyStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
        }

    }

    private void applyDeclaration(Declaration node) {
        node.expression = evalExpression(node.expression);
    }

    private Expression evalExpression(Expression expression) {
        if (expression instanceof Literal) {
            return expression;
        } else if (expression instanceof Operation) {
            Operation operation = (Operation) expression;
            Expression left = evalExpression(operation.lhs);
            Expression right = evalExpression(operation.rhs);

            if (operation instanceof AddOperation) {
                return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value + "px");
            } else if (operation instanceof SubtractOperation) {
                return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value + "px");
            } else if (operation instanceof MultiplyOperation) {
                return new PixelLiteral(((PixelLiteral) left).value * ((PixelLiteral) right).value + "px");
            }
        }
        expression.setError("Unexpected expression type: " + expression.getClass().getName());
//        throw new IllegalArgumentException("Unexpected expression type: " + expression.getClass().getName());
        return expression;
    }


}
