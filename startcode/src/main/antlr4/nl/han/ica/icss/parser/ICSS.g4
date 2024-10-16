grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Variable identifier
VAR_IDENT: [A-Z][a-zA-Z0-9]*;

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
DIV: '/';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: stylerule+;
stylerule: variable* selector+ OPEN_BRACE (declaration | if_clause)* CLOSE_BRACE;
declaration: property COLON ((value | VAR_IDENT) operator?)+ SEMICOLON;
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT | CAPITAL_IDENT;
operator: (PLUS | MIN | MUL | DIV);
property: LOWER_IDENT;
value: COLOR #color| PIXELSIZE #pixelsize | PERCENTAGE #percentage| SCALAR #scalar| TRUE #truebool| FALSE #falsebool | VAR_IDENT #varident;
variable: VAR_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;
if_clause: IF BOX_BRACKET_OPEN condition BOX_BRACKET_CLOSE OPEN_BRACE (declaration | nested_if_clause)* CLOSE_BRACE else_clause?;
nested_if_clause: IF BOX_BRACKET_OPEN condition BOX_BRACKET_CLOSE OPEN_BRACE declaration* CLOSE_BRACE else_clause?;
else_clause: ELSE OPEN_BRACE declaration* CLOSE_BRACE;
condition: VAR_IDENT;