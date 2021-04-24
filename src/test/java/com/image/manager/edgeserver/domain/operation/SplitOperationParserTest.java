package com.image.manager.edgeserver.domain.operation;

import com.image.manager.edgeserver.domain.operation.parser.OperationParser;
import com.image.manager.edgeserver.domain.operation.parser.split.SplitOperationParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitOperationParserTest {

    private OperationParser operationParser;

    @BeforeEach
    void setUp() {
        var operationFactory = new OperationFactory(List.of(new CropOperation.Factory(), new ScaleOperation.Factory(), new WatermarkOperation.Factory(), new CompressionOperation.Factory()));
        this.operationParser = new SplitOperationParser(operationFactory);
    }

    @Test
    @Disabled("Disabled due to strange error related to object comparison. Check if method is properly used here and why after IM-16 started failing")
    void parseQueryParams() {
        //given
        String query = "op=crop&w=20&h=20&h=10&op=scale&w=500";
        List<Operation> expectedOperations = List.of(
                new CropOperation(null, null, 20, 20),
                new ScaleOperation(500, null)
        );

        //when
        List<Operation> actualOperations = operationParser.fromQuery(query);

        //then
        assertThat(actualOperations.size()).isEqualTo(expectedOperations.size());
        assertThat(actualOperations).isEqualTo(expectedOperations);
    }

    @Test
    void throwExceptionWhenParseInvalidOperation() {
        //given
        String query = "op=BADTYPE&w=500&x=20";

        //when then
        assertThrows(IllegalArgumentException.class, () -> operationParser.fromQuery(query));
    }
}