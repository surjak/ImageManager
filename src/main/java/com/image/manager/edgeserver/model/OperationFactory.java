package com.image.manager.edgeserver.model;

import java.util.*;

public class OperationFactory {

    public static List<Operation> fromQuery(String wholeQuery) {
        if(wholeQuery != null) {
            String[] operations = wholeQuery.split("op=");
            List<Operation> operationList = new LinkedList<>();
            for (int i = 1; i < operations.length; i++) {
                operationList.add(OperationFactory.fromSubQuery(operations[i]));
            }
            return operationList;
        }
        return Collections.emptyList();
    }

    public static Operation fromSubQuery(String query) {
        String[] params = query.split("&");
        Map<String, Integer> arguments = new HashMap<>();
        OperationType operationType = OperationType.valueOf(params[0].toUpperCase());
        for (int i = 1; i < params.length; i++) {

            String[] values = params[i].split("=");
            String key = values[0];
            String value = values[1];
            arguments.put(key, Integer.valueOf(value));
        }
        return fromOperationAndArguments(operationType, arguments);
    }

    private static Operation fromOperationAndArguments(OperationType operationType, Map<String, Integer> arguments) {
        return switch (operationType) {
            case CROP -> new CropOperation(operationType, arguments);
            case SCALE -> new ScaleOperation(operationType, arguments);
        };
    }
}
