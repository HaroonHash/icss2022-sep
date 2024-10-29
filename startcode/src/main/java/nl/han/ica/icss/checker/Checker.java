package nl.han.ica.icss.checker;

import com.sun.jdi.Value;
import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStyleSheet(ast.root);
    }

    private void checkStyleSheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

//    private void checkDeclaration(Declaration node) {
//        String propertyName = node.property.name;
//        Expression expression = node.expression;
//
//        if (propertyName.equals("width")) {
//            // 'width' expects a pixel value
//            if (!(expression instanceof PixelLiteral ||
//                    (expression instanceof VariableReference && lookupVariableType((VariableReference) expression) == ExpressionType.PIXEL))) {
//                node.setError("Property 'width' must be a pixel value.");
//            }
//        } else if (propertyName.equals("color") || propertyName.equals("background-color")) {
//            // 'color' and 'background-color' expect a color value
//            if (!(expression instanceof ColorLiteral ||
//                    (expression instanceof VariableReference && lookupVariableType((VariableReference) expression) == ExpressionType.COLOR))) {
//                node.setError("Property '" + propertyName + "' must be a color value.");
//            }
//        } else {
//            // For any other property, literals are valid
//            if (!(expression instanceof Literal || expression instanceof VariableReference)) {
//                node.setError("Invalid expression type in declaration for property '" + propertyName + "'.");
//            }
//        }
//    }

    private void checkDeclaration(Declaration node) {
        String propertyName = node.property.name;
        Expression expression = node.expression;

        if (propertyName.equals("width")) {
            if (!isPixelExpression(expression)) {
                node.setError("Property 'width' must be a pixel value.");
            }
        } else if (propertyName.equals("color") || propertyName.equals("background-color")) {
            if (!(expression instanceof ColorLiteral ||
                    (expression instanceof VariableReference && lookupVariableType((VariableReference) expression) == ExpressionType.COLOR))) {
                node.setError("Property '" + propertyName + "' must be a color value.");
            }
        } else {
            if (!(expression instanceof Literal || expression instanceof VariableReference)) {
                node.setError("Invalid expression type in declaration for property '" + propertyName + "'.");
            }
        }
    }

    // Helper method to check if an expression results in a pixel value
    private boolean isPixelExpression(Expression expression) {
        if (expression instanceof PixelLiteral) {
            return true;
        } else if (expression instanceof VariableReference) {
            return lookupVariableType((VariableReference) expression) == ExpressionType.PIXEL;
        } else if (expression instanceof AddOperation || expression instanceof SubtractOperation) {
            Operation operation = (Operation) expression;
            return isPixelExpression(operation.lhs) && isPixelExpression(operation.rhs);
        } else if (expression instanceof MultiplyOperation) {
            Operation operation = (Operation) expression;
            // Allow scalar * pixel or pixel * scalar
            return (isPixelExpression(operation.lhs) && operation.rhs instanceof ScalarLiteral) ||
                    (operation.lhs instanceof ScalarLiteral && isPixelExpression(operation.rhs));
        }
        return false;
    }


    private ExpressionType lookupVariableType(VariableReference varRef) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(varRef.name)) {
                return scope.get(varRef.name);
            }
        }
        return null;
    }

    private void checkVariableAssignment(VariableAssignment node) {
        Expression expression = node.expression;
        ExpressionType type = getExpressionType(expression);

        if (type != ExpressionType.UNDEFINED) {
            // Store the variable name and type in the global scope (first element in variableTypes)
            if (variableTypes.getSize() == 0) {
                variableTypes.addFirst(new HashMap<>());
            }
            variableTypes.get(0).put(node.name.name, type);  // Add to the global scope
        } else {
            node.setError("Invalid variable assignment type for: " + node.name.name);
        }
    }

    // Helper method to get the type of an expression
    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else {
            return ExpressionType.UNDEFINED;
        }
    }


    private void checkOperation(Operation node) {
        if (node.lhs instanceof Operation) {
            checkOperation((Operation) node.lhs);
        }
        if (node.rhs instanceof Operation) {
            checkOperation((Operation) node.rhs);
        }

        if (node instanceof AddOperation) {
            checkAddOperation((AddOperation) node);
        } else if (node instanceof MultiplyOperation) {
            checkMultiplyOperation((MultiplyOperation) node);
        } else if (node instanceof SubtractOperation) {
            checkSubtractOperation((SubtractOperation) node);
        }
    }

    private void checkAddOperation(AddOperation node) {
        if ((node.lhs instanceof PixelLiteral || node.lhs instanceof ScalarLiteral || node.lhs instanceof VariableReference) &&
                (node.rhs instanceof PixelLiteral || node.rhs instanceof ScalarLiteral || node.rhs instanceof VariableReference)) {
            // Valid operation
        } else {
            node.setError("Invalid operation: Add operation requires pixel, scalar literals, or variable references");
        }
    }

    private void checkSubtractOperation(SubtractOperation node) {
        if ((node.lhs instanceof PixelLiteral || node.lhs instanceof ScalarLiteral || node.lhs instanceof VariableReference) &&
                (node.rhs instanceof PixelLiteral || node.rhs instanceof ScalarLiteral || node.rhs instanceof VariableReference)) {
            // Valid operation
        } else {
            node.setError("Invalid operation: Subtract operation requires pixel, scalar literals, or variable references");
        }
    }

    private void checkMultiplyOperation(MultiplyOperation node) {
        if ((node.lhs instanceof PixelLiteral || node.lhs instanceof ScalarLiteral || node.lhs instanceof VariableReference) &&
                (node.rhs instanceof PixelLiteral || node.rhs instanceof ScalarLiteral || node.rhs instanceof VariableReference)) {
            // Valid operation
        } else {
            node.setError("Invalid operation: Multiply operation requires pixel, scalar literals, or variable references");
        }
    }

    private void checkIfClause(IfClause ifClause) {
        if (!(ifClause.conditionalExpression instanceof VariableReference)) {
            ifClause.setError("Condition in if clause must be a variable reference");
        }
        for (ASTNode child : ifClause.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            } else if (child instanceof ElseClause) {
                checkElseClause((ElseClause) child);
            }
        }
    }

    private void checkElseClause(ElseClause elseClause) {
        for (ASTNode child : elseClause.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }
    }
}

