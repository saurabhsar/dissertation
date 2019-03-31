package di;

import com.google.inject.Injector;

public class DI {

    private static Injector injector;

    public static Injector di() {
        return injector;
    }

    public static void init(Injector otherInjector) {
        injector = otherInjector;

    }
}
