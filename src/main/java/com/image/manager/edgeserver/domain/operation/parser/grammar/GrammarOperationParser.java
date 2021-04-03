package com.image.manager.edgeserver.domain.operation.parser.grammar;

import com.image.manager.edgeserver.domain.operation.Operation;
import com.image.manager.edgeserver.domain.operation.OperationFactory;
import com.image.manager.edgeserver.domain.operation.parser.OperationParser;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GrammarOperationParser implements OperationParser {

    private final OperationFactory operationFactory;

    private final ParseTreeWalker walker = new ParseTreeWalker();
    private final ImageParamsEvaluator evaluator = new ImageParamsEvaluator();

    @Override
    public List<Operation> fromQuery(String query) {
        ImageParamsLexer serverLogLexer = new ImageParamsLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(serverLogLexer);
        ImageParamsParser imageParamsParser = new ImageParamsParser(tokens);

        walker.walk(evaluator, imageParamsParser.params());

        return evaluator.getImageParams()
                .getOperations()
                .stream()
                .map(operation -> operationFactory.createOperation(operation.getType(), operation.getArgumentsMap()))
                .collect(Collectors.toList());
    }

}
