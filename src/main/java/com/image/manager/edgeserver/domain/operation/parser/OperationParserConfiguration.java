package com.image.manager.edgeserver.domain.operation.parser;

import com.image.manager.edgeserver.domain.operation.OperationFactory;
import com.image.manager.edgeserver.domain.operation.parser.grammar.GrammarOperationParser;
import com.image.manager.edgeserver.domain.operation.parser.split.SplitOperationParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OperationParserConfiguration {

    @Bean
    public OperationParser splitOperationParser(OperationFactory operationFactory) {
        return new SplitOperationParser(operationFactory);
    }

    @Bean
    @Primary
    public OperationParser grammarOperationParser(OperationFactory operationFactory) {
        return new GrammarOperationParser(operationFactory);
    }

}
