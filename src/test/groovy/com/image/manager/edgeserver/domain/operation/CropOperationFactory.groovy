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
        def expectedX = 25
        def expectedY = 35

        def expectedWidth = 10
        def expectedHeight = 20

        def arguments = [
                x: expectedX,
                y: expectedY,
                w: expectedWidth,
                h: expectedHeight
        ]

        // when:
        CropOperation actualOperation = cropOperationFactory.fromArguments(arguments) as CropOperation

        // then:
        assert ReflectionTestUtils.getField(actualOperation, 'x') == expectedX
        assert ReflectionTestUtils.getField(actualOperation, 'y') == expectedY
        assert ReflectionTestUtils.getField(actualOperation, 'w') == expectedWidth
        assert ReflectionTestUtils.getField(actualOperation, 'h') == expectedHeight

    }

}
