grammar Mindcode;

program : expression_list EOF
        | EOF
        ;

expression_list : expression
                | expression SEMICOLON
                | expression_list expression
                | expression_list SEMICOLON expression
                ;

expression : a_comment                                                                          # comment
           | propaccess                                                                         # property_access
           | cond=expression QUESTION_MARK true_branch=expression COLON false_branch=expression # ternary_op
           | case_expr                                                                          # case_expression
           | if_expr                                                                            # if_expression
           | funcall                                                                            # function_call
           | fundecl                                                                            # function_declaration
           | alloc                                                                              # allocation
           | assign                                                                             # assignment
           | lvalue                                                                             # value
           | while_expression                                                                   # while_loop
           | for_expression                                                                     # for_loop
           | left=expression op=EXP right=expression                                            # binop_exp
           | NOT expression                                                                     # not_expr
           | left=expression op=( MUL | DIV | MOD ) right=expression                            # binop_mul_div_mod
           | left=expression op=( PLUS | MINUS ) right=expression                               # binop_plus_minus
           | left=expression op=( LESS_THAN | LESS_THAN_EQUAL
                                | GREATER_THAN_EQUAL | GREATER_THAN )
                                right=expression                                                # binop_inequality_comparison
           | left=expression op=( NOT_EQUAL | EQUAL | STRICT_EQUAL ) right=expression           # binop_equality_comparison
           | left=expression AND right=expression                                               # binop_and
           | left=expression OR right=expression                                                # binop_or
           | literal_t                                                                          # literal_string
           | numeric_t                                                                          # literal_numeric
           | bool_t                                                                             # literal_bool
           | MINUS numeric_t                                                                    # unary_minus
           | null_t                                                                             # literal_null
           | LEFT_RBRACKET expression RIGHT_RBRACKET                                            # parenthesized_expression
           ;

propaccess : var_ref DOT prop=id
           | unit_ref DOT prop=id
           ;

numeric_t : float_t
          | int_t
          ;

alloc : ALLOCATE alloc_list;

alloc_list : type=(HEAP | STACK) IN id alloc_range?
           | alloc_list COMMA type=(HEAP | STACK) IN id alloc_range?
           ;

alloc_range : LEFT_SBRACKET range RIGHT_SBRACKET;

fundecl : DEF name=id LEFT_RBRACKET args=arg_decl_list RIGHT_RBRACKET body=expression_list END
        | DEF name=id body=expression_list END
        ;

arg_decl_list : lvalue
              | arg_decl_list COMMA lvalue
              ;

while_expression : WHILE cond=expression loop_body END;

for_expression : FOR lvalue IN range loop_body END                                                          # ranged_for
               | FOR init=init_list SEMICOLON cond=expression SEMICOLON increment=incr_list loop_body END   # iterated_for
               ;

loop_body : loop_body expression_list
          | loop_body break_st
          | loop_body continue_st
          | expression_list
          | break_st
          | continue_st
          ;

continue_st : CONTINUE;

break_st : BREAK;

range : start=int_t DOT DOT end=int_t     # inclusive_range
      | start=int_t DOT DOT DOT end=int_t # exclusive_range
      ;

init_list : expression
          | init_list COMMA expression
          ;

incr_list : expression
          | incr_list COMMA expression
          ;

funcall : END LEFT_RBRACKET RIGHT_RBRACKET
        | name=id LEFT_RBRACKET RIGHT_RBRACKET
        | name=id LEFT_RBRACKET params=arg_list RIGHT_RBRACKET
        | obj=propaccess LEFT_RBRACKET params=arg_list RIGHT_RBRACKET
        ;

arg_list : arg
         | arg_list COMMA arg
         ;

arg : expression;

if_expr : IF cond=expression true_branch=expression_list if_trailer? END;

if_trailer : ELSE false_branch=expression_list
           | ELSE IF cond=expression true_branch=expression_list if_trailer
           ;

case_expr : CASE cond=expression alternative_list? ( ELSE else_branch=expression_list )? END;

alternative_list : alternative
                 | alternative_list alternative
                 ;

alternative : WHEN value=expression body=expression_list;

assign : target=lvalue ASSIGN value=expression                             # simple_assign
       | target=lvalue EXP_ASSIGN value=expression                         # exp_assign
       | target=lvalue op=( MUL_ASSIGN | DIV_ASSIGN ) value=expression     # binop_mul_div_assign
       | target=lvalue op=( PLUS_ASSIGN | MINUS_ASSIGN ) value=expression  # binop_plus_minus_assign
       ;

lvalue : unit_ref
       | global_ref
       | heap_ref
       | var_ref
       | propaccess
       ;

heap_ref : name=id LEFT_SBRACKET address=expression RIGHT_SBRACKET;
global_ref : DOLLAR name=id;
unit_ref : AT ref;
var_ref : id;

ref : ID;
int_t : INT;
float_t : FLOAT;
literal_t : LITERAL;
null_t : NULL;
bool_t : true_t  # true_bool_literal
       | false_t # false_bool_literal
       ;
true_t : TRUE;
false_t : FALSE;
id : ID;

a_comment : SL_COMMENT;

ALLOCATE : 'allocate';
BREAK : 'break';
CASE : 'case';
CONTINUE : 'continue';
DEF : 'def';
ELSE : 'else';
END : 'end';
FALSE : 'false';
FOR : 'for';
HEAP : 'heap';
IF : 'if';
IN : 'in';
NULL : 'null';
STACK : 'stack';
TRUE : 'true';
WHEN : 'when';
WHILE : 'while';

ASSIGN : '=';
AT : '@';
COLON : ':';
COMMA : ',';
DIV : '/';
DOLLAR : '$';
DOT : '.';
EXP : ('^' | '**');
MINUS : '-';
MOD : '%';
MUL : '*';
NOT : '!' | 'not';
PLUS : '+';
QUESTION_MARK : '?';
SEMICOLON : ';';

DIV_ASSIGN : '/=';
EXP_ASSIGN : '^=';
MINUS_ASSIGN : '-=';
MUL_ASSIGN : '*=';
PLUS_ASSIGN : '+=';

LESS_THAN : '<';
LESS_THAN_EQUAL : '<=';
NOT_EQUAL  : '!=';
EQUAL  : '==';
STRICT_EQUAL  : '===';
GREATER_THAN_EQUAL  : '>=';
GREATER_THAN : '>';
AND : '&&' | 'and';
OR : '||' | 'or';

LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';
LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_CBRACKET : '{';
RIGHT_CBRACKET : '}';

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

FLOAT : INT DOT INT;
INT : [0-9][0-9]*;

ID : [_a-zA-Z][-a-zA-Z_0-9]*;
SL_COMMENT : ('//' ~('\r' | '\n')* '\r'? '\n');
WS : (' ' | '\t' | '\r' | '\n')+ -> skip;
