grammar ImageParams;

fragment DIGIT : [0-9];
fragment LETTER : [A-Za-z];

NUMBER: DIGIT+;
TEXT   : LETTER+ ;
CRLF : '\r'? '\n' | '\r';

params: operation ('&' operation)*;
operation: 'op=' operationName ('&' operationArgument)*;

operationName: TEXT;
operationArgument: operationArgumentName '=' operationArgumentValue;

operationArgumentName: TEXT;
operationArgumentValue: NUMBER;