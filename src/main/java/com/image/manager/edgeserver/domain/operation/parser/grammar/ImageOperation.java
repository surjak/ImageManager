package com.image.manager.edgeserver.domain.operation.parser.grammar;

import com.image.manager.edgeserver.domain.operation.OperationType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
class ImageOperation {

    private OperationType type;
    private List<ImageOperationArgument> arguments = new ArrayList<>();

    public Map<String, Integer> getArgumentsMap() {
        return this.arguments.stream()
                .collect(Collectors.toMap(ImageOperationArgument::getName, ImageOperationArgument::getValue));
    }

}
