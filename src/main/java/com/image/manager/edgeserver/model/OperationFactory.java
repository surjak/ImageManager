package com.image.manager.edgeserver.model;

import java.util.LinkedList;
import java.util.List;

public class OperationFactory {

    public static Operation fromQuery(String query) {
        String[] params = query.split("&");
        OperationType operationType = OperationType.valueOf(params[0].toUpperCase());
        List<Integer> widths = new LinkedList<>();
        List<Integer> heights = new LinkedList<>();
        for (int i = 1; i < params.length; i++) {
            String[] values = params[i].split("=");
            String key = values[0];
            String value = values[1];
            if (key.startsWith("w")) {
                widths.add(Integer.valueOf(value));
            } else if (key.startsWith("h")) {
                heights.add(Integer.valueOf(value));
            }
        }
        return new Operation(operationType, widths, heights);
    }

}
