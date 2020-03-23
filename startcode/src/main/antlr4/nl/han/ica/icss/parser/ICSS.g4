grammar ICSS;

//--- LEXER: ---
// IF support:
IF: 'if';
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

stylesheet: (styleRule | variableAssignment)*;
styleRule: selector OPEN_BRACE body CLOSE_BRACE; // .active { body }
selector: CLASS_IDENT | ID_IDENT | LOWER_IDENT; // .active | #menu | a   lower ident for plain alements

body: declaration*;
declaration: property COLON expression SEMICOLON;
property: 'width' | 'height' | 'color' | 'background-color' | 'colour' | 'background-colour';

expression: literal | variableReference;
literal: colorLiteral | pixelLiteral | percentageLiteral | boolLiteral | scalarLiteral;
colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
percentageLiteral : PERCENTAGE;
boolLiteral: TRUE | FALSE;
scalarLiteral: SCALAR;

//VARS

variableAssignment: variableReference ASSIGNMENT_OPERATOR literal SEMICOLON;
variableReference: CAPITAL_IDENT;
