package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
    
import java.util.HashMap;
    
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
            variableTypes.addFirst(new HashMap<>());  // New scope for stylerule
            for (ASTNode child : node.getChildren()) {
                if (child instanceof Declaration) {
                    checkDeclaration((Declaration) child);
                } else if (child instanceof VariableAssignment) {
                    checkVariableAssignment((VariableAssignment) child);
                }
            }
            variableTypes.removeFirst();  // End stylerule scope
        }


        //Change to cases for AL03 eis?
        private void checkDeclaration(Declaration node) {
            String propertyName = node.property.name;
            Expression expression = node.expression;

            // Check if the property is `color` or `background-color` (expect color type)
            if (propertyName.equals("color") || propertyName.equals("background-color")) {
                if (!(expression instanceof ColorLiteral ||
                        (expression instanceof VariableReference && lookupVariableType((VariableReference) expression) == ExpressionType.COLOR))) {
                    node.setError("Property '" + propertyName + "' must be a color value.");
                }
            }
            // Validate for `width` and `height` (expect pixel type)
            else if (propertyName.equals("width") || propertyName.equals("height")) {
                if (!isPixelExpression(expression)) {
                    node.setError("Property '" + propertyName + "' must be a pixel value.");
                }
            }
            // Additional properties, such as ensuring only literals or variable references
            else {
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
            varRef.setError("Variable '" + varRef.name + "' is not defined.");
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

        // Helper method to resolve the type of an expression, including variable references (to reduce code duplication/clutter in tthe operation check)
        private ExpressionType resolveExpressionType(Expression expression) {
            if (expression instanceof VariableReference) {
                ExpressionType type = lookupVariableType((VariableReference) expression);
                if (type == null) {
                    expression.setError("Undefined variable reference in expression.");
                }
                return type;
            }
            return getExpressionType(expression);
        }



        private void checkOperation(Operation node) {
            // Check operations recursively for nested expressions
            if (node.lhs instanceof Operation) {
                checkOperation((Operation) node.lhs);
            }
            if (node.rhs instanceof Operation) {
                checkOperation((Operation) node.rhs);
            }

            // Apply specific checks based on operation type
            if (node instanceof AddOperation) {
                checkAddOperation((AddOperation) node);
            } else if (node instanceof MultiplyOperation) {
                checkMultiplyOperation((MultiplyOperation) node);
            } else if (node instanceof SubtractOperation) {
                checkSubtractOperation((SubtractOperation) node);
            }
        }

        private void checkAddOperation(AddOperation node) {
            ExpressionType leftType = resolveExpressionType(node.lhs);
            ExpressionType rightType = resolveExpressionType(node.rhs);

            if (leftType != rightType || leftType == ExpressionType.COLOR) {
                node.setError("Add operation requires both operands to be of the same non-color type.");
            }
        }

        private void checkSubtractOperation(SubtractOperation node) {
            ExpressionType leftType = resolveExpressionType(node.lhs);
            ExpressionType rightType = resolveExpressionType(node.rhs);

            if (leftType != rightType || leftType == ExpressionType.COLOR) {
                node.setError("Subtract operation requires both operands to be of the same non-color type.");
            }
        }

        private void checkMultiplyOperation(MultiplyOperation node) {
            ExpressionType leftType = resolveExpressionType(node.lhs);
            ExpressionType rightType = resolveExpressionType(node.rhs);

            if ((leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) ||
                    leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
                node.setError("Multiply operation requires at least one operand to be a scalar and neither operand to be a color.");
            }
        }

        //extra prints voor debugging
        private void checkIfClause(IfClause ifClause) {
            Expression condition = ifClause.conditionalExpression;
            ExpressionType conditionType = resolveExpressionType(condition);
            if (conditionType != ExpressionType.BOOL) {
                ifClause.setError("Condition in if clause must be a boolean.");
            }

            // New scope for IfClause body
            variableTypes.addFirst(new HashMap<>());  // Push a new scope for nested variables

            for (ASTNode child : ifClause.getChildren()) {
                if (child instanceof Declaration) {
                    System.out.println("Declaration");
                    checkDeclaration((Declaration) child);
                } else if (child instanceof IfClause) {
                    System.out.println("IfClause");
                    checkIfClause((IfClause) child); //Recusrive for nested if clauses
                } else if (child instanceof ElseClause) {
                    System.out.println("ElseClause");
                    checkElseClause((ElseClause) child);
                }
            }
            System.out.println("Remove scope for IfClause");
            variableTypes.removeFirst();  // Remove scope for IfClause
        }

        private void checkElseClause(ElseClause elseClause) {
            variableTypes.addFirst(new HashMap<>());  // New scope for ElseClause

            for (ASTNode child : elseClause.getChildren()) {
                if (child instanceof Declaration) {
                    checkDeclaration((Declaration) child);
                } else if (child instanceof IfClause) {
                    checkIfClause((IfClause) child);
                }
            }
            variableTypes.removeFirst();  // Remove scope after else clause
        }

    }
    
