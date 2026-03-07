// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.runtime;

import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.lang.ref.WeakReference;
import java.util.Objects;

final class BooleanComplementBinding implements ChangeListener<Boolean>, WeakListener {

    private final WeakReference<Property<Boolean>> property1;
    private final WeakReference<Property<Boolean>> property2;
    private boolean updating;

    public BooleanComplementBinding(Property<Boolean> property1, Property<Boolean> property2) {
        this.property1 = new WeakReference<>(Objects.requireNonNull(property1, "property1 cannot be null"));
        this.property2 = new WeakReference<>(Objects.requireNonNull(property2, "property2 cannot be null"));
    }

    private Property<Boolean> getProperty1() {
        return property1.get();
    }

    private Property<Boolean> getProperty2() {
        return property2.get();
    }

    @Override
    public boolean wasGarbageCollected() {
        return getProperty1() == null || getProperty2() == null;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (updating) {
            return;
        }

        Property<Boolean> property1 = getProperty1();
        Property<Boolean> property2 = getProperty2();

        if (property1 == null || property2 == null) {
            if (property1 != null) {
                property1.removeListener(this);
            }

            if (property2 != null) {
                property2.removeListener(this);
            }
        } else {
            updating = true;
            Property<Boolean> property = property1 != observable ? property1 : property2;

            try {
                property.setValue(newValue == null || !newValue);
            } catch (Throwable ex1) {
                try {
                    property.setValue(oldValue == null || !oldValue);
                } catch (Throwable ex2) {
                    ex2.addSuppressed(ex1);
                    property1.removeListener(this);
                    property2.removeListener(this);

                    throw new RuntimeException(
                        "Bidirectional binding failed together with an attempt to restore the source property to "
                        + "the previous value. Removing the bidirectional binding from both properties.", ex2);
                }

                throw new RuntimeException("Bidirectional binding failed, setting to the previous value.", ex1);
            } finally {
                updating = false;
            }
        }
    }
}
