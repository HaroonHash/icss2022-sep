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
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
//stylesheet: EOF; //EOF IS FILLER (ZELF VERANDEREN) ( TIS MAIN DING ZOALS REEL)
stylesheet: stylerule+ EOF;
stylerule: variable*? selector OPEN_BRACE declaration* CLOSE_BRACE;
selector: (ID_IDENT | CLASS_IDENT | LOWER_IDENT | CAPITAL_IDENT)+;
declaration: property COLON (value | VAR_IDENT) SEMICOLON;
property: LOWER_IDENT;
value: COLOR | PIXELSIZE | PERCENTAGE | SCALAR | TRUE | FALSE | ID_IDENT | CLASS_IDENT | LOWER_IDENT | CAPITAL_IDENT;
variable: VAR_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;
//expression:
//if_clause:
//else_clause:
//identifier:
//
