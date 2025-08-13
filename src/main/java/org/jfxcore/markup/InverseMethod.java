// Copyright (c) 2025, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the name of an inverse method for a compiled method binding.
 * <p>
 * For any method <em>A m(B)</em>, the corresponding inverse method is <em>B n(A)</em>.
 * Only methods that accept a single parameter and return a single value can have an inverse method.
 * The inverse method can be any method (instance or static) declared on the same class.
 * It is legal for a method to be its own inverse method if its return type and parameter type are the same.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface InverseMethod {
    String value();
}
