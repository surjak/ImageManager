package com.image.manager.edgeserver.domain.operation.parser.grammar;

import com.image.manager.edgeserver.domain.operation.OperationType;

import java.util.Map;

public interface Operationable {
    OperationType getType();
    Map<String, String> getArgumentsMap();
}
