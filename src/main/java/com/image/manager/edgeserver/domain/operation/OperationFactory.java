package com.image.manager.edgeserver.domain.operation;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

import static com.image.manager.edgeserver.domain.operation.OperationType.*;

@Service
public class OperationFactory {

    private final Map<OperationType, Function<Map<String, Integer>, Operation>> operationsMap = Map.of(
            SCALE, ScaleOperation::fromArgumentsMap,
            CROP, CropOperation::fromArgumentsMap
    );

    public Operation createOperation(OperationType type, Map<String, Integer> arguments) {
        return operationsMap.get(type).apply(arguments);
    }

}
