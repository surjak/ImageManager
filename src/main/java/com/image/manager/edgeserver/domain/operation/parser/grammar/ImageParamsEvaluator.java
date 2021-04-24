package com.image.manager.edgeserver.domain.operation.parser.grammar;


import com.image.manager.edgeserver.domain.operation.OperationType;
import lombok.Getter;

class ImageParamsEvaluator extends ImageParamsBaseListener {

    @Getter
    private ImageParams imageParams;
    private FormatOption currentFormatOption;
    private ImageOperation currentOperation;
    private ImageOperationArgument currentOperationArgument;

    @Override
    public void enterParams(ImageParamsParser.ParamsContext ctx) {
        this.imageParams = new ImageParams();
    }

    @Override
    public void enterFormatOption(ImageParamsParser.FormatOptionContext ctx) {
        this.currentFormatOption = new FormatOption();
    }

    @Override
    public void enterFormatOptionName(ImageParamsParser.FormatOptionNameContext ctx) {
        this.currentFormatOption.setType(OperationType.getByName(ctx.getText()));
    }

    @Override
    public void enterFormatOptionValue(ImageParamsParser.FormatOptionValueContext ctx) {
        this.currentFormatOption.setOptionValue(ctx.getText());
    }

    @Override
    public void enterOperation(ImageParamsParser.OperationContext ctx) {
        this.currentOperation = new ImageOperation();
    }

    @Override
    public void enterOperationName(ImageParamsParser.OperationNameContext ctx) {
        this.currentOperation.setType(OperationType.getByName(ctx.getText()));
    }

    @Override
    public void enterOperationArgument(ImageParamsParser.OperationArgumentContext ctx) {
        this.currentOperationArgument = new ImageOperationArgument();
    }

    @Override
    public void enterOperationArgumentName(ImageParamsParser.OperationArgumentNameContext ctx) {
        this.currentOperationArgument.setName(ctx.getText());
    }

    @Override
    public void enterOperationArgumentValue(ImageParamsParser.OperationArgumentValueContext ctx) {
        this.currentOperationArgument.setValue(ctx.getText());
    }

    @Override
    public void exitFormatOption(ImageParamsParser.FormatOptionContext ctx) {
        this.imageParams.getFormatOptions().add(this.currentFormatOption);
    }

    @Override
    public void exitOperationArgument(ImageParamsParser.OperationArgumentContext ctx) {
        this.currentOperation.getArguments().add(this.currentOperationArgument);
    }

    @Override
    public void exitOperation(ImageParamsParser.OperationContext ctx) {
        this.imageParams.getOperations().add(this.currentOperation);
    }

}
