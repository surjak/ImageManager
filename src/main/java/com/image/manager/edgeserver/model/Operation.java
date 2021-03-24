package com.image.manager.edgeserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Operation {
    private OperationType operationType;
    private List<Integer> widths;
    private List<Integer> heights;
}
