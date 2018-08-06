package OOP.Solution;

import OOP.Provided.OOPExpectedException;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface OOPExceptionRule {
    // TODO bom
        //OOPExpectedException exp() default OOPExpectedException.none();

}
