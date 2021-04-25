package com.image.manager.edgeserver.domain.operation;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OperationFactory {

    private final Map<OperationType, Function<Map<String, String>, Operation>> operationsMap;

    public OperationFactory(List<Operation.Factory> factories) {
        operationsMap = factories.stream()
                .collect(Collectors.toMap(Operation.Factory::getSupportedType, o -> o::fromArguments));

        verifyCoverage();
    }

    public Operation createOperation(OperationType type, Map<String, String> arguments) {
        return operationsMap.get(type).apply(arguments);
    }

    private void verifyCoverage() {
        boolean allTypesCovered = Stream.of(OperationType.values())
                .allMatch(this.operationsMap::containsKey);

        if(!allTypesCovered) {
            throw new IllegalStateException("Not all operation types covered in factory");
        }
    }

}
