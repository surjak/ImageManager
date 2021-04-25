package com.image.manager.edgeserver.domain.operation.parser.grammar;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
class ImageParams {

    private List<FormatOption> formatOptions = new ArrayList<>();
    private List<ImageOperation> operations = new ArrayList<>();

}
