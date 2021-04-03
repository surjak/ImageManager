package com.image.manager.edgeserver.domain.operation.parser.split;

import com.image.manager.edgeserver.domain.operation.*;
import com.image.manager.edgeserver.domain.operation.parser.OperationParser;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class SplitOperationParser implements OperationParser {

    private final OperationFactory operationFactory;
    
    @Override
    public List<Operation> fromQuery(String query) {
        if (query != null) {
            String[] operations = query.split("op=");
            List<Operation> operationList = new LinkedList<>();
            for (int i = 1; i < operations.length; i++) {
                operationList.add(fromSubQuery(operations[i]));
            }
            return operationList;
        }
        return Collections.emptyList();
    }

    private Operation fromSubQuery(String query) {
        String[] params = query.split("&");
        //optimizer here
        Map<String, Integer> arguments = new HashMap();
        OperationType operationType = OperationType.valueOf(params[0].toUpperCase());
        for (int i = 1; i < params.length; i++) {

            String[] values = params[i].split("=");
            String key = values[0];
            String value = values[1];
            if (arguments.containsKey(key)) {
                //skip the repeating properties for now, leave it for optimizer
                continue;
            }
            arguments.put(key, Integer.valueOf(value));
        }
        
        return operationFactory.createOperation(operationType, arguments);
    }
    
}
