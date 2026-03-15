// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import javafx.scene.Node;

/**
 * Provides the {@link ResourceContext} used by resource-related markup extensions in an FXML document.
 * <p>
 * The root element of the FXML document is expected to implement this interface when the document uses
 * {@link StaticResource} or {@link DynamicResource} markup extensions. The returned {@link ResourceContext}
 * supplies localized strings and typed resource values for the document.
 * <p>
 * The returned resource context is expected to remain valid for repeated lookups. When the root element is a
 * {@link Node}, the {@link #getResourceContext()} method is only invoked once, and the returned context is then
 * cached in the node's {@link Node#getProperties() properties} map for subsequent lookups.
 * <p>
 * Example:
 * <pre>{@code
 * public class MyPane extends MyPaneBase implements ResourceContextProvider {
 *
 *     private final ResourceContext resourceContext =
 *         ResourceContext.ofResourceBundle(ResourceBundle.getBundle("com.example.messages"));
 *
 *     @Override
 *     public ResourceContext getResourceContext() {
 *         return resourceContext;
 *     }
 * }
 * }</pre>
 */
public interface ResourceContextProvider {

    /**
     * Returns the resource context used to resolve localized strings and typed resource values.
     *
     * @return the resource context
     */
    ResourceContext getResourceContext();
}
