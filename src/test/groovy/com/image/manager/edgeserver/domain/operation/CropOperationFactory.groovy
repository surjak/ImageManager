package com.image.manager.edgeserver.domain.operation

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class CropOperationFactory {

    private Operation.Factory cropOperationFactory

    @BeforeEach
    void setUp() {
        this.cropOperationFactory = new CropOperation.Factory()
    }

    @Test
    void 'Should create operation from valid arguments'() {
        // given:

        def arguments = [
                x: "25",
                y: "35",
                w: "10",
                h: "20"
        ]

        // when:
        CropOperation actualOperation = cropOperationFactory.fromArguments(arguments) as CropOperation

        // then:
        assert ReflectionTestUtils.getField(actualOperation, 'x') == 25
        assert ReflectionTestUtils.getField(actualOperation, 'y') == 35
        assert ReflectionTestUtils.getField(actualOperation, 'w') == 10
        assert ReflectionTestUtils.getField(actualOperation, 'h') == 20

    }

}
