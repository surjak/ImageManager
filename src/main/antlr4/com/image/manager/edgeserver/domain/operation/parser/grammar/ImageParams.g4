grammar ImageParams;

fragment NEGATIVE_SIGN : '-';
fragment DIGIT : [0-9];
fragment LETTER : [A-Za-z];

NUMBER: NEGATIVE_SIGN? DIGIT+;
TEXT   : LETTER+ ;
CRLF : '\r'? '\n' | '\r';


params: (formatOptions '&' operations) | formatOptions | operations;

formatOptions: formatOption ('&' formatOption)*;
formatOption: formatOptionName '=' formatOptionValue;
formatOptionName: TEXT;
formatOptionValue: TEXT;

operations: operation ('&' operation)*;
operation: 'op=' operationName ('&' operationArgument)*;

operationName: TEXT;
operationArgument: operationArgumentName '=' operationArgumentValue;

operationArgumentName: TEXT;
operationArgumentValue: TEXT;