grammar Mindcode;

program : expression_list;

expression_list : expression terminator
                | expression_list expression terminator
                | terminator
                ;

expression : while_statement
           | funcall_statement
           | control_statement
           | rvalue
           ;

control_statement : target=id DOT property=id ASSIGN value=rvalue;

funcall_statement : name=id LEFT_RBRACKET params_list RIGHT_RBRACKET;

params_list : rvalue?
            | rvalue COMMA params_list
            ;

while_statement : WHILE rvalue LEFT_CBRACKET crlf? block_body RIGHT_CBRACKET;

block_body : block_statement_list;

block_statement_list : expression terminator
                     | block_statement_list expression terminator
                     ;

lvalue : id;

rvalue : lvalue
       | assignment
       | if_expression
       | literal_t
       | bool_t
       | float_t
       | int_t
       | null_t
       | sensor_read
       | heap_read
       | rvalue op=EXP rvalue
       | op=NOT rvalue
       | rvalue op=( MUL | DIV | MOD ) rvalue
       | rvalue op=( PLUS | MINUS ) rvalue
       | rvalue op=( LESS | GREATER | LESS_EQUAL | GREATER_EQUAL ) rvalue
       | rvalue op=( STRICT_EQUAL | EQUAL | NOT_EQUAL ) rvalue
       | rvalue op=( OR | AND ) rvalue
       | LEFT_RBRACKET rvalue RIGHT_RBRACKET
       ;

if_expression : IF cond=rvalue LEFT_CBRACKET ( terminator )? true_branch=block_body RIGHT_CBRACKET ( ELSE LEFT_CBRACKET ( terminator )? false_branch=block_body RIGHT_CBRACKET )?;

heap_read : target=id LEFT_SBRACKET addr=address RIGHT_SBRACKET;

address : int_t;

sensor_read : target=id DOT resource
            | target=id DOT liquid
            | target=id DOT sensor
            ;

assignment : target=lvalue ASSIGN value=rvalue
           | target=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) value=rvalue
           | heap_read ASSIGN value=rvalue
           | heap_read op=( PLUS_ASSIGN | MINUS_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | EXP_ASSIGN ) value=rvalue
           ;

id : ID;

literal_t : LITERAL;

float_t : FLOAT;

int_t : INT;

bool_t : TRUE
       | FALSE
       ;

null_t : NULL;

terminator : terminator SEMICOLON
           | terminator crlf
           | SEMICOLON
           | crlf
           | EOF
           ;

crlf : CRLF;

resource : COPPER
         | LEAD
         | METAGLASS
         | GRAPHITE
         | SAND
         | COAL
         | TITANIUM
         | SCRAP
         | SILICON
         | PYRATITE
         ;

liquid : WATER
       | SLAG
       | CRYOFLUID
       ;

sensor : id;

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

WHILE : 'while';
IF : 'if';
ELSE : 'else';

DOT : '.';
COMMA : ',';
SEMICOLON : ';';

ASSIGN : '=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
MOD_ASSIGN : '%=';
EXP_ASSIGN : '**=';

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EXP : '**';

COPPER : 'copper';
LEAD : 'lead';
METAGLASS : 'metaglass';
GRAPHITE : 'graphite';
SAND : 'sand';
COAL : 'coal';
TITANIUM : 'titanium';
SCRAP : 'scrap';
SILICON : 'silicon';
PYRATITE : 'pyratite';
WATER : 'water';
SLAG : 'slag';
CRYOFLUID : 'cryofluid';

TRUE : 'true';
FALSE : 'false';

STRICT_EQUAL : '===';
EQUAL : '==';
NOT_EQUAL : '!=';
GREATER : '>';
LESS : '<';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';

AND : 'and' | '&&';
OR : 'or' | '||';
NOT : 'not' | '!';

LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';
LEFT_CBRACKET : '{';
RIGHT_CBRACKET : '}';

NULL : 'null';

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]+;
ID : [a-zA-Z_][a-zA-Z0-9_]*;

CRLF : '\r'? '\n';

WS : (' ' | '\t')+ -> skip;
