package info.teksol.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a base class used as a supertype for generated subclasses of given category  */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BaseClass {
    String value();
}
