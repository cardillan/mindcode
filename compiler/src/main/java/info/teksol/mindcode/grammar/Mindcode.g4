grammar Mindcode;

program : expression_list EOF
        | EOF
        ;

expression_list : expression
                | expression SEMICOLON
                | expression_list expression
                | expression_list SEMICOLON expression
                ;

expression : MINUS numeric_t                                                                    # literal_minus
           | indirectpropaccess                                                                 # indirect_prop_access
           | case_expr                                                                          # case_expression
           | if_expr                                                                            # if_expression
           | funcall                                                                            # function_call
           | printf                                                                             # print_format
           | propaccess                                                                         # property_access
           | fundecl                                                                            # function_declaration
           | alloc                                                                              # allocation
           | lvalue                                                                             # value
           | while_expression                                                                   # while_loop
           | do_while_expression                                                                # do_while_loop
           | for_expression                                                                     # for_loop
           | NOT expression                                                                     # not_expr
           | BITWISE_NOT expression                                                             # bitwise_not_expr
           | left=expression op=EXP right=expression                                            # binop_exp
           | MINUS expression                                                                   # unary_minus
           | left=expression op=( MUL | DIV | IDIV | MOD ) right=expression                     # binop_mul_div_mod
           | left=expression op=( PLUS | MINUS ) right=expression                               # binop_plus_minus
           | left=expression op=( SHIFT_LEFT | SHIFT_RIGHT ) right=expression                   # binop_shift
           | left=expression BITWISE_AND right=expression                                       # binop_bitwise_and
           | left=expression op=( BITWISE_OR | BITWISE_XOR ) right=expression                   # binop_bitwise_or
           | left=expression op=( LESS_THAN | LESS_THAN_EQUAL
                                | GREATER_THAN_EQUAL | GREATER_THAN )
                                right=expression                                                # binop_inequality_comparison
           | left=expression op=( NOT_EQUAL | EQUAL 
                                | STRICT_NOT_EQUAL | STRICT_EQUAL ) right=expression            # binop_equality_comparison
           | left=expression AND right=expression                                               # binop_and
           | left=expression OR right=expression                                                # binop_or
           | <assoc=right> cond=expression QUESTION_MARK
                                (true_branch=expression COLON false_branch=expression)          # ternary_op
           | assign                                                                             # assignment
           | literal_t                                                                          # literal_string
           | numeric_t                                                                          # literal_numeric
           | bool_t                                                                             # literal_bool
           | null_t                                                                             # literal_null
           | LEFT_RBRACKET expression RIGHT_RBRACKET                                            # parenthesized_expression
           | break_st                                                                           # break_exp
           | continue_st                                                                        # continue_exp
           | return_st                                                                          # return_exp
           ;

indirectpropaccess : target=var_ref DOT SENSOR LEFT_RBRACKET expr=expression RIGHT_RBRACKET;

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

fundecl : ( inline = INLINE)? DEF name=id LEFT_RBRACKET args=arg_decl_list RIGHT_RBRACKET body=expression_list END
        | ( inline = INLINE)? DEF name=id LEFT_RBRACKET RIGHT_RBRACKET body=expression_list END
        | ( inline = INLINE)? DEF name=id body=expression_list END
        ;

arg_decl_list : var_ref
              | arg_decl_list COMMA var_ref
              ;

while_expression : ( label=loop_label COLON )? WHILE cond=expression loop_body END;

do_while_expression : ( label=loop_label COLON )? DO loop_body LOOP WHILE cond=expression;

for_expression : ( label=loop_label COLON )? FOR lvalue IN range_expression loop_body END       # ranged_for
               | ( label=loop_label COLON )? FOR init=init_list SEMICOLON cond=expression
                            SEMICOLON increment=incr_list loop_body END                         # iterated_for
               | ( label=loop_label COLON )? FOR lvalue IN      
                            LEFT_RBRACKET values=loop_value_list RIGHT_RBRACKET loop_body END   # for_each
               ;

loop_body : loop_body expression_list
          | expression_list
          ;

loop_value_list : expression
                | loop_value_list COMMA expression
                ;
				
continue_st : CONTINUE ( label=loop_label )? ;

break_st : BREAK ( label=loop_label )? ;

return_st : RETURN ( retval=expression )? ;

range : start=int_t DOT DOT end=int_t     # inclusive_range
      | start=int_t DOT DOT DOT end=int_t # exclusive_range
      ;

range_expression : start=expression DOT DOT end=expression     # inclusive_range_exp
                 | start=expression DOT DOT DOT end=expression # exclusive_range_exp
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

printf : PRINTF LEFT_RBRACKET RIGHT_RBRACKET
       | PRINTF LEFT_RBRACKET format=literal_t RIGHT_RBRACKET
       | PRINTF LEFT_RBRACKET format=literal_t COMMA params=arg_list RIGHT_RBRACKET
       ;

arg_list : arg
         | arg_list COMMA arg
         ;

arg : expression;

if_expr : IF cond=expression true_branch=expression_list? if_trailer? END;

if_trailer : ELSE false_branch=expression_list?
           | ELSIF cond=expression true_branch=expression_list? if_trailer?
           ;

case_expr : CASE cond=expression alternative_list? ( ELSE else_branch=expression_list )? END;

alternative_list : alternative
                 | alternative_list alternative
                 ;

alternative : WHEN values=when_value_list THEN? body=expression_list?;

when_expression : expression
                | range_expression
                ;

when_value_list : when_expression
                | when_value_list COMMA when_expression
                ;

assign : <assoc=right> target=lvalue ASSIGN value=expression                             # simple_assign
       | <assoc=right> target=lvalue op=( EXP_ASSIGN | 
                                          MUL_ASSIGN | DIV_ASSIGN | IDIV_ASSIGN | MOD_ASSIGN |
                                          PLUS_ASSIGN | MINUS_ASSIGN |
                                          SHIFT_LEFT_ASSIGN | SHIFT_RIGHT_ASSIGN |
                                          BITWISE_AND_ASSIGN | BITWISE_OR_ASSIGN | BITWISE_XOR_ASSIGN |
                                          AND_ASSIGN | OR_ASSIGN ) value=expression      # compound_assign
       ;

lvalue : unit_ref
       | global_ref
       | heap_ref
       | var_ref
       | propaccess
       ;

loop_label : ID;

heap_ref : name=id LEFT_SBRACKET address=expression RIGHT_SBRACKET;
global_ref : DOLLAR name=id;
unit_ref : AT ref;
var_ref : id;

ref : ID;
int_t : decimal_int | hex_int;
float_t : FLOAT;
literal_t : LITERAL;
null_t : NULL;
bool_t : true_t  # true_bool_literal
       | false_t # false_bool_literal
       ;
true_t : TRUE;
false_t : FALSE;
id : ID;

decimal_int : INT;
hex_int : HEXINT;

ALLOCATE : 'allocate';
BREAK : 'break';
CASE : 'case';
CONST : 'const';
CONTINUE : 'continue';
DEF : 'def';
DO : 'do';
ELSE : 'else';
ELSIF : 'elsif';
END : 'end';
FALSE : 'false';
FOR : 'for';
HEAP : 'heap';
IF : 'if';
IN : 'in';
INLINE : 'inline';
LOOP : 'loop';
NULL : 'null';
PRINTF : 'printf';
RETURN : 'return';
SENSOR : 'sensor';
STACK : 'stack';
THEN : 'then';
TRUE : 'true';
WHEN : 'when';
WHILE : 'while';

ASSIGN : '=';
AT : '@';
COLON : ':';
COMMA : ',';
DIV : '/';
IDIV : '\\';
DOLLAR : '$';
DOT : '.';
EXP : '**';
MINUS : '-';
MOD : '%';
MUL : '*';
NOT : '!' | 'not';
BITWISE_NOT : '~';
PLUS : '+';
QUESTION_MARK : '?';
SEMICOLON : ';';

EXP_ASSIGN : '**=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
IDIV_ASSIGN : '\\=';
MOD_ASSIGN : '%=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';

SHIFT_LEFT_ASSIGN : '<<=';
SHIFT_RIGHT_ASSIGN : '>>=';
BITWISE_AND_ASSIGN : '&=';
BITWISE_OR_ASSIGN : '|=';
BITWISE_XOR_ASSIGN : '^=';
AND_ASSIGN : '&&=';
OR_ASSIGN : '||=';

LESS_THAN : '<';
LESS_THAN_EQUAL : '<=';
NOT_EQUAL  : '!=';
EQUAL  : '==';
STRICT_EQUAL  : '===';
STRICT_NOT_EQUAL  : '!==';
GREATER_THAN_EQUAL  : '>=';
GREATER_THAN : '>';
AND : '&&' | 'and';
OR : '||' | 'or';

SHIFT_LEFT : '<<';
SHIFT_RIGHT : '>>';
BITWISE_AND : '&';
BITWISE_OR : '|';
BITWISE_XOR : '^';

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
HEXINT : '0' [xX] [a-f0-9]+;

ID : [_a-zA-Z][-a-zA-Z_0-9]*;
SL_COMMENT : ('//' ~('\r' | '\n')* '\r'? '\n') -> skip;
WS : (' ' | '\t' | '\r' | '\n')+ -> skip;
