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
        def expectedWidth = 10
        def expectedHeight = 20

        def arguments = [
            w: expectedWidth,
            h: expectedHeight
        ]

        // when:
        ScaleOperation actualOperation = scaleOperationFactory.fromArguments(arguments) as ScaleOperation

        // then:
        assert ReflectionTestUtils.getField(actualOperation, 'w') == expectedWidth
        assert ReflectionTestUtils.getField(actualOperation, 'h') == expectedHeight
    }

}
