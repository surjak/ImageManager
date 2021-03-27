package com.image.manager.edgeserver.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OperationFactoryTest {

    @Autowired
    private OperationFactory operationFactory;

    @Test
    void parseQueryParams() {
        //given
        String query = "op=crop&w=20&h=20&h=10&op=scale&w=500";
        List<Operation> expectedOperations = List.of(
                new CropOperation(null, null, 20, 20),
                new ScaleOperation(500, null)
        );

        //when
        List<Operation> actualOperations = operationFactory.fromQuery(query);

        //then
        assertThat(actualOperations.size()).isEqualTo(expectedOperations.size());
        assertThat(actualOperations).isEqualTo(expectedOperations);
    }

    @Test
    void throwExceptionWhenParseInvalidOperation() {
        //given
        String query = "op=BADTYPE&w=500&x=20";

        //when then
        assertThrows(IllegalArgumentException.class, () -> operationFactory.fromQuery(query));
    }
}