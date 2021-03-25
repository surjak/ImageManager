package com.image.manager.edgeserver.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationFactoryTest {

    @Test
    void parseQueryParams() {
        //given
        String query = "op=crop&w=20&h=20&h=10&op=scale&w=500";
        List<Operation> expectedOperations = List.of(
                new CropOperation(List.of(new OperationArgument("w", 20), new OperationArgument("h", 20), new OperationArgument("h", 10))),
                new ScaleOperation(List.of(new OperationArgument("w", 500)))
        );

        //when
        List<Operation> actualOperations = OperationFactory.fromQuery(query);

        //then
        assertThat(actualOperations.size()).isEqualTo(expectedOperations.size());
        assertThat(actualOperations).isEqualTo(expectedOperations);
    }

    @Test
    void throwExceptionWhenParseInvalidOperation() {
        //given
        String query = "op=BADTYPE&w=500&x=20";

        //when then
        assertThrows(IllegalArgumentException.class, () -> OperationFactory.fromQuery(query));
    }
}