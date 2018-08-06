package OOP.Solution;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static OOP.Solution.OOPTestClass.OOPTestClassType.UNORDERED;
import static OOP.Solution.OOPTestClass.OOPTestClassType.UNORDERED;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface OOPTestClass {
    enum OOPTestClassType {
        ORDERED,
        UNORDERED
    }

    OOPTestClassType value() default UNORDERED;

}
