package mods.ryanjh5521.core.api.traits;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks those methods in a trait for which proxies are to be generated in the user type.
 * If an interface is being used as a trait, instead of a concrete implementation, then
 * the interface must also include these methods or a NoSuchMethodError will be thrown at runtime.
 * The annotation must be present in the concrete class that implements the methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TraitMethod {

}
