package com.image.manager.edgeserver.domain.operation

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class ScaleOperationFactoryTest {

    private Operation.Factory scaleOperationFactory

    @BeforeEach
    void setUp() {
        this.scaleOperationFactory = new ScaleOperation.Factory()
    }

    @Test
    void 'Should create operation from valid arguments'() {
        // given:

        def arguments = [
            w: "10",
            h: "20"
        ]

        // when:
        ScaleOperation actualOperation = scaleOperationFactory.fromArguments(arguments) as ScaleOperation

        // then:
        assert ReflectionTestUtils.getField(actualOperation, 'w') == 10
        assert ReflectionTestUtils.getField(actualOperation, 'h') == 20
    }

}
