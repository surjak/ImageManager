package com.image.manager.edgeserver.domain.operation

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.ArgumentMatchers.anyMap
import static org.mockito.ArgumentMatchers.contains
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.junit.jupiter.api.Assertions.*;

class OperationFactoryTest {

    private OperationFactory operationFactory

    private Operation.Factory scaleOperationFactory, cropOperationFactory, watermarkOperationFactory, conversionOperationFactory

    @BeforeEach
    void setUp() {
        this.scaleOperationFactory = mock Operation.Factory
        this.cropOperationFactory = mock Operation.Factory
        this.watermarkOperationFactory = mock Operation.Factory
        this.conversionOperationFactory = mock Operation.Factory

        when(scaleOperationFactory.getSupportedType()).thenReturn(OperationType.SCALE)
        when(cropOperationFactory.getSupportedType()).thenReturn(OperationType.CROP)
        when(watermarkOperationFactory.getSupportedType()).thenReturn(OperationType.WATERMARK)
        when(conversionOperationFactory.getSupportedType()).thenReturn(OperationType.COMPRESSION)

        this.operationFactory = new OperationFactory([scaleOperationFactory, cropOperationFactory, watermarkOperationFactory, conversionOperationFactory])
    }

    @Test
    void 'Should throw exception because of not full coverage'() {
        // given
        def factories = [scaleOperationFactory]

        // when + then:
        assertThrows(
                IllegalStateException,
                () -> new OperationFactory(factories),
                "Not all operation types covered in factory"
        )
    }

    @Test
    void 'Should create proper type of operation'() {
        // given:
        def args = {} as Map<String, Integer>

        def expectedScaleOperation = mock Operation
        def expectedCropOperation = mock Operation

        when(scaleOperationFactory.fromArguments(anyMap())).thenReturn(expectedScaleOperation)
        when(cropOperationFactory.fromArguments(anyMap())).thenReturn(expectedCropOperation)

        // when:
        final def actualCropOperation = operationFactory.createOperation(OperationType.CROP, args)
        final def actualScaleOperation = operationFactory.createOperation(OperationType.SCALE, args)

        // then:
        assert actualCropOperation == expectedCropOperation
        assert actualScaleOperation == expectedScaleOperation
    }

}
