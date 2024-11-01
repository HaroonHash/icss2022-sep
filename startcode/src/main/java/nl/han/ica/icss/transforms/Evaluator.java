package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        transformStylesheet((Stylesheet) ast.root);
    }

    private void transformStylesheet(Stylesheet node) {
        ArrayList<ASTNode> transformedBody = new ArrayList<>();
        for (ASTNode child : node.body) {
            if (child instanceof VariableAssignment) {
                evaluateVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                transformStylerule((Stylerule) child);
                transformedBody.add(child); // Add transformed stylerule
            }
        }
        node.body = transformedBody;
    }

    private void transformStylerule(Stylerule stylerule) {
        ArrayList<ASTNode> transformedBody = new ArrayList<>(); // Changed to ArrayList
        variableValues.addFirst(new HashMap<>());

        for (ASTNode child : stylerule.body) {
            applyRuleBody(child, transformedBody);
        }

        stylerule.body = transformedBody; // Set transformed body
        variableValues.removeFirst();
    }

    // Handles each ASTNode type, including IfClause transformation
    private void applyRuleBody(ASTNode astNode, ArrayList<ASTNode> parentBody) {
        if (astNode instanceof VariableAssignment) {
            evaluateVariableAssignment((VariableAssignment) astNode);
        } else if (astNode instanceof Declaration) {
            applyDeclaration((Declaration) astNode);
            parentBody.add(astNode);
        } else if (astNode instanceof IfClause) {
            transformIfClause((IfClause) astNode, parentBody);
        }
    }

    private void transformIfClause(IfClause ifClause, ArrayList<ASTNode> parentBody) {
        Expression condition = evaluateExpression(ifClause.conditionalExpression);

        if (condition instanceof BoolLiteral) {
            if (((BoolLiteral) condition).value) {
                // If condition is true, add only IfClause body
                for (ASTNode ifChild : ifClause.body) {
                    applyRuleBody(ifChild, parentBody);
                }
            } else if (ifClause.elseClause != null) {
                // If condition is false and ElseClause exists, add ElseClause body
                for (ASTNode elseChild : ifClause.elseClause.body) {
                    applyRuleBody(elseChild, parentBody);
                }
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);
    }

    private void evaluateVariableAssignment(VariableAssignment node) {
        Literal value = (Literal) evaluateExpression(node.expression);
        if (variableValues.getSize() == 0) {
            variableValues.addFirst(new HashMap<>());
        }
        variableValues.get(0).put(node.name.name, value);
    }

    private Expression evaluateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return expression;
        } else if (expression instanceof VariableReference) {
            for (int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> scope = variableValues.get(i);
                if (scope.containsKey(((VariableReference) expression).name)) {
                    return scope.get(((VariableReference) expression).name);
                }
            }
            expression.setError("Undefined variable: " + ((VariableReference) expression).name);
        } else if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        return expression;
    }

    private Expression evaluateOperation(Operation operation) {
        Expression left = evaluateExpression(operation.lhs);
        Expression right = evaluateExpression(operation.rhs);

        if (operation instanceof AddOperation && left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value + "px");
        } else if (operation instanceof SubtractOperation && left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value + "px");
        } else if (operation instanceof MultiplyOperation) {
            if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
                return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value + "px");
            } else if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value + "px");
            }
        }
        operation.setError("Invalid operation with incompatible types.");
        return operation;
    }
}
