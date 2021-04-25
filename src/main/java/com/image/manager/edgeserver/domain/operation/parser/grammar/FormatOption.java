package com.image.manager.edgeserver.domain.operation.parser.grammar;

import com.image.manager.edgeserver.domain.operation.OperationType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class FormatOption implements Operationable {

    public static final String VALUE = "valueKey";
    private OperationType type;
    private String optionValue;

    public Map<String, String> getArgumentsMap() {
        return Map.of(VALUE, optionValue);
    }
}
