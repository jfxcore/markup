// Copyright (c) 2025, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup;

import javafx.beans.NamedArg;

/**
 * Provides information about the context in which a {@link MarkupExtension} is applied.
 */
public interface MarkupContext {

    /**
     * Returns the root element of the FXML document.
     *
     * @return the root element of the FXML document
     */
    Object getRoot();

    /**
     * Gets an ancestor element in the FXML document tree.
     *
     * @param index the index of the ancestor, where 0 is the current element, 1 is the parent element,
     *              and {@code getAncestorCount() - 1} is the root element
     * @return the parent element
     */
    Object getAncestor(int index);

    /**
     * Gets the number of ancestors in the FXML document tree.
     *
     * @return the number of ancestors, including the current element
     */
    int getAncestorCount();

    /**
     * Returns the object that contains the property targeted by the markup extension.
     * <p>
     * If the markup extension is applied to a constructor argument or a method argument, this method
     * returns {@code null} to indicate that no bean information is available.
     *
     * @return the bean, or {@code null}
     */
    Object getTargetBean();

    /**
     * Returns the name of the property or constructor argument targeted by the markup extension.
     * <p>
     * If the markup extension is applied to a named constructor argument annotated with {@link NamedArg},
     * this method returns the name of the constructor argument; if the markup extension is applied to a
     * method argument, this method returns {@code null}.
     *
     * @return the name of the targeted property or constructor argument, or {@code null}
     */
    String getTargetName();

    /**
     * Returns the type of the property, constructor argument, or method argument targeted by the
     * markup extension.
     * <p>
     * The target type is not necessarily the same as the type of the targeted property. For example, when a
     * {@code MarkupExtension.Supplier<String>} extension is applied to a {@code ListProperty<String>}, the
     * target type is {@code String} because the provided value is added to the list.
     *
     * @return the target type
     */
    Class<?> getTargetType();
}
