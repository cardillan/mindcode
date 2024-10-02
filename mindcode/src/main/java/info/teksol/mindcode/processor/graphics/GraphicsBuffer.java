package info.teksol.mindcode.processor.graphics;

import info.teksol.mindcode.compiler.instructions.DrawInstruction;
import info.teksol.mindcode.processor.ExecutionException;
import info.teksol.mindcode.processor.ProcessorFlag;

import java.util.ArrayList;
import java.util.List;

public class GraphicsBuffer {
    private final int sizeLimit;
    private final List<DrawInstruction> drawInstructions = new ArrayList<>();

    // In Mindustry Logic, the transformation matrix is owned by the processor and is not reset when the
    // processor's code is recompiled.
    // Here we want to reset the matrix for each independent run
    private final TransformationMatrix transformationMatrix = new TransformationMatrix();

    public GraphicsBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public boolean draw(DrawInstruction instruction) {
        drawInstructions.add(instruction);

        // Only report the error once
        if (drawInstructions.size() == sizeLimit + 1) {
            throw new ExecutionException(ProcessorFlag.ERR_GRAPHICS_BUFFER_OVERFLOW, "Capacity of the graphics buffer (" + sizeLimit + ") exceeded.");
        }

        return true;
    };

    public List<DrawInstruction> getDrawInstructions() {
        return drawInstructions;
    }

    public TransformationMatrix getTransformationMatrix() {
        return transformationMatrix;
    }

    public void clear() {
        drawInstructions.clear();
    }
}
