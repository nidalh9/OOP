package OOP.Solution;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static OOP.Solution.OOPTestClass.OOPTestClassType.UNORDERED;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface OOPSetup {}
