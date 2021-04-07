package com.image.manager.edgeserver.domain.operation.parser;

import com.image.manager.edgeserver.domain.operation.Operation;

import java.util.List;

public interface OperationParser {

    List<Operation> fromQuery(String query);

}
