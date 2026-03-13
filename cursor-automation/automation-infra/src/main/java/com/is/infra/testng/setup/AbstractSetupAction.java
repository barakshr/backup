package com.is.infra.testng.setup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Base class for SetupAction implementations.
 *
 * Provides getAnnotation() — resolves an annotation from the test method first,
 * then falls back to the declaring class. This supports both method-level and
 * class-level annotation placement, with method taking precedence.
 *
 * Usage in a subclass:
 *   TestSetup setup = getAnnotation(method, TestSetup.class);
 *   return setup != null && setup.createCompany();
 */
public abstract class AbstractSetupAction implements SetupAction {

    /**
     * Resolves an annotation from the test method, falling back to its declaring class.
     * Returns null if neither the method nor the class carries the annotation.
     */
    protected <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        A onMethod = method.getAnnotation(annotationType);
        if (onMethod != null) return onMethod;
        return method.getDeclaringClass().getAnnotation(annotationType);
    }
}
