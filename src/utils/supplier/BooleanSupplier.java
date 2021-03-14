package utils.supplier;

import jmath.datatypes.functions.NoArgFunction;

@FunctionalInterface
public interface BooleanSupplier extends NoArgFunction<Boolean> {
    boolean is();

    @Override
    default Boolean value() {
        return is();
    }
}
