/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar gram_clauses;

options {
    // antlr will generate java lexer and parser
    language = Java;
    // generated parser should create abstract syntax tree
    //output = AST;
}

/////// Parser rules

// logic - WHERE
clauses_expr : expr (LOGIC_OPERATOR clauses_expr)?;
expr :  (not)? (expr_LR | clause);
expr_LR : L clauses_expr R;
clause : attr OPERATOR (string | number |  datetype | NAME);

datetype : DATETYPE STRING;
attr : NAME;
value : string | number;
string : STRING;
number : NUMBER;
not : NOT;



/////// Lexer Rules
NUMBER : DIGIT+ (POINT DIGIT+)?;
STRING : A ~('\'')* A;
LOGIC_OPERATOR : SP (AND | OR) SP;
OPERATOR : NLT | NGT | LT | GT | NEQ | EQ | LIKE;

fragment LETTER : [a-zA-Z];
fragment DIGIT : [0-9];
WS  : [ \t\r\n]+ -> skip ;
SP : (' ')+ -> skip;
L : '(';
R : ')';
fragment POINT : '.';
COMMA : ',';
A : '\'';
B : '"';
AND : 'AND';
OR : 'OR';
NOT : 'NOT';
NLT : '>=';
NGT : '<=';
LT : '<';
GT : '>';
EQ : '=';
NEQ: '!=' | '<>';
UNDER: '_';
LIKE : 'LIKE';
VALUES : 'VALUES';
INSERT : 'INSERT';
INTO : 'INTO';
DATETYPE : 'DATE';

// Avoid conflicts
NAME : LETTER ( UNDER | LETTER | DIGIT | POINT )*; 