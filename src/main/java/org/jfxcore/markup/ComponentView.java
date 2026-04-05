// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies an FXML view for a component class.
 * <p>
 * The annotation is consumed during annotation processing, it is not retained in the compiled class.
 * It contains the FXML source text that is compiled as though it were provided by a separate FXML
 * source file associated with the annotated class.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * package com.sample;
 *
 * import javafx.scene.control.Button;
 * import javafx.scene.layout.StackPane;
 * import org.jfxcore.markup.ComponentView;
 *
 * @ComponentView("""
 *     <StackPane>
 *         <Button fx:id="myButton"/>
 *     </StackPane>
 * """)
 * public class MyControl extends MyControlBase {
 *
 *     public MyControl() {
 *         initializeComponent();
 *         myButton.setText("Hello!");
 *     }
 * }
 * }</pre>
 *
 * <h2>Remarks</h2>
 * <ul>
 *     <li>Import declarations of the Java source file also apply to markup, they do not need to be redeclared
 *         as {@code <?import?>} processing instructions.
 *     <li>The {@code fx:class} attribute is not allowed, because the annotated Java class is statically known
 *         to be the code-behind class of the markup.
 *     <li>The namespaces {@code xmlns="http://javafx.com/javafx"} and {@code xmlns:fx="http://jfxcore.org/fxml/2.0"}
 *         do not need to be declared; they are implicitly declared by the compiler. However, if the {@code xmlns:fx}
 *         namespace is explicitly declared using a different prefix, the {@code fx} prefix will not be implicitly
 *         declared by the compiler.
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ComponentView {

    /**
     * {@return the FXML source text associated with the annotated class}
     */
    String value();
}
