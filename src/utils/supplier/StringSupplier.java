package utils.supplier;

import jmath.datatypes.functions.NoArgFunction;

import java.awt.*;

@FunctionalInterface
public interface StringSupplier extends NoArgFunction<String> {
    Font defaultFont = new Font("serif", Font.BOLD, 14);
    Color defaultColor = Color.GREEN;

    String getText();

    default Color getColor() {
        return defaultColor;
    }

    default Font getFont() {
        return defaultFont;
    }

    default Point getPosOnScreen() {
        return null;
    }

    @Override
    default String value() {
        return getText();
    }
}
