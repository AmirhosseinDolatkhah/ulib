package algo;

import jmath.datatypes.functions.Function;

public interface Algorithm extends Function<Object, Object[]> {
    int SEARCH = 0;
    int SORT = 1;

    Object operate(Object... inputs);

    @Override
    default Object valueAt(Object[] objects) {
        return operate(objects);
    }
}
