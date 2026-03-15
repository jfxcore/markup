// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import org.jfxcore.markup.MarkupContext;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

final class ResourceContextHelper {

    private ResourceContextHelper() {}

    static ResourceContext getResourceContext(MarkupContext markupContext, Class<?> extensionType) {
        if (!(markupContext.getRoot() instanceof ResourceContextProvider provider)) {
            throw new RuntimeException(
                String.format("%s requires the root element to implement %s",
                    extensionType.getSimpleName(), ResourceContextProvider.class.getName()));
        }

        if (markupContext.getRoot() instanceof Node root) {
            var cachedResourceContext = (ResourceContext)root.getProperties().get(ResourceContextHelper.class);
            if (cachedResourceContext == null) {
                cachedResourceContext = provider.getResourceContext();
                root.getProperties().put(ResourceContextHelper.class, cachedResourceContext);
            }

            return cachedResourceContext;
        }

        return provider.getResourceContext();
    }

    static Object getResource(Class<?> targetType, ResourceContext resourceContext,
                              String key, Object[] formatArguments) {
        return isFormattedStringTarget(targetType, formatArguments)
            ? resourceContext.getString(key, snapshotArguments(formatArguments))
            : resourceContext.getObject(key, targetType);

    }

    static boolean isFormattedStringTarget(Class<?> targetType, Object[] formatArguments) {
        return targetType.isAssignableFrom(String.class)
            && (targetType == String.class || formatArguments != null);
    }

    static Object[] snapshotArguments(Object[] formatArguments) {
        if (formatArguments == null || formatArguments.length == 0) {
            return formatArguments;
        }

        Object[] args = new Object[formatArguments.length];

        for (int i = 0; i < formatArguments.length; ++i) {
            args[i] = formatArguments[i] instanceof ObservableValue<?> value
                ? value.getValue()
                : formatArguments[i];
        }

        return args;
    }
}
