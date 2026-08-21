// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.markup.resource;

import org.jfxcore.markup.MarkupContext;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ClassPathResourceTest {

    @Test
    public void Embedded_Resource_Takes_Precedence_Over_Ordinary_Resource() throws Exception {
        Object value = new ClassPathResource("shared.txt").get(context(String.class));

        assertEquals("embedded", read(value));
        assertTrue(((String)value).endsWith("MyView$808553fe$shared.txt"));
    }

    @Test
    public void Missing_Embedded_Resource_Falls_Back_To_Ordinary_Resource() throws Exception {
        Object value = new ClassPathResource("fallback.txt").get(context(URI.class));

        assertInstanceOf(URI.class, value);
        assertEquals("fallback", read(value));
    }

    @Test
    public void Absolute_And_Subdirectory_Names_Skip_The_Embedded_Probe() throws Exception {
        Object absolute = new ClassPathResource("/org/jfxcore/markup/resource/absolute.txt").get(context(URL.class));
        Object nested = new ClassPathResource("nested/path.txt").get(context(URL.class));

        assertEquals("absolute", read(absolute));
        assertEquals("nested", read(nested));
    }

    @Test
    public void Resource_Is_Converted_To_Each_Supported_Target_Type() throws Exception {
        assertInstanceOf(String.class, new ClassPathResource("fallback.txt").get(context(String.class)));
        assertInstanceOf(URI.class, new ClassPathResource("fallback.txt").get(context(URI.class)));
        assertInstanceOf(URL.class, new ClassPathResource("fallback.txt").get(context(URL.class)));
        assertInstanceOf(String.class, new ClassPathResource("fallback.txt").get(context(CharSequence.class)));
        assertInstanceOf(String.class, new ClassPathResource("fallback.txt").get(context(Object.class)));
    }

    @Test
    public void Missing_Resource_Keeps_The_Original_Failure_Message() {
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> new ClassPathResource("missing.txt").get(context(URL.class)));

        assertEquals("Resource not found: missing.txt", exception.getMessage());
    }

    private String read(Object value) throws Exception {
        URI uri;
        if (value instanceof String string) {
            uri = URI.create(string);
        } else if (value instanceof URI resourceUri) {
            uri = resourceUri;
        } else if (value instanceof URL url) {
            uri = url.toURI();
        } else {
            throw new IllegalArgumentException();
        }

        return Files.readString(Path.of(uri)).trim();
    }

    private MarkupContext context(Class<?> targetType) {
        return new TestMarkupContext(targetType);
    }

    private static final class TestMarkupContext implements MarkupContext {
        private final Object root = new TestRoot();
        private final Class<?> targetType;

        private TestMarkupContext(Class<?> targetType) {
            this.targetType = targetType;
        }

        @Override
        public Object getRoot() {
            return root;
        }

        @Override
        public String getDocumentName() {
            return "MyView";
        }

        @Override
        public Object getAncestor(int index) {
            return index == 0 ? root : null;
        }

        @Override
        public int getAncestorCount() {
            return 1;
        }

        @Override
        public Object getTargetBean() {
            return root;
        }

        @Override
        public String getTargetName() {
            return "value";
        }

        @Override
        public Class<?> getTargetType() {
            return targetType;
        }
    }

    private static final class TestRoot {}
}
