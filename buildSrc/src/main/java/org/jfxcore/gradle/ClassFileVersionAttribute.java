// Copyright (c) 2026, JFXcore. All rights reserved.
// Use of this source code is governed by the BSD-3-Clause license that can be found in the LICENSE file.

package org.jfxcore.gradle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class ClassFileVersionAttribute {

    public static final String ATTRIBUTE_NAME = "org.jfxcore.markup.version";

    private ClassFileVersionAttribute() {}

    /**
     * Adds or replaces the org.jfxcore.markup.version attribute on every .class file below the given directory.
     */
    public static void addToDirectory(Path classesDirectory, String version) throws IOException {
        if (!Files.isDirectory(classesDirectory)) {
            return;
        }

        byte[] payload = version.getBytes(StandardCharsets.UTF_8);

        try (Stream<Path> paths = Files.walk(classesDirectory)) {
            Iterator<Path> iterator = paths
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".class"))
                .iterator();

            while (iterator.hasNext()) {
                addToClassFile(iterator.next(), payload);
            }
        }
    }

    private static void addToClassFile(Path classFile, byte[] payload) throws IOException {
        byte[] input = Files.readAllBytes(classFile);
        ClassReader reader = new ClassReader(input);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public void visitAttribute(Attribute attribute) {
                if (!ATTRIBUTE_NAME.equals(attribute.type)) {
                    super.visitAttribute(attribute);
                }
            }

            @Override
            public void visitEnd() {
                super.visitAttribute(new RawAttribute(ATTRIBUTE_NAME, payload));
                super.visitEnd();
            }
        };

        reader.accept(visitor, 0);
        Files.write(classFile, writer.toByteArray());
    }

    private static final class RawAttribute extends Attribute {
        private final byte[] payload;

        RawAttribute(String name, byte[] payload) {
            super(name);
            this.payload = payload.clone();
        }

        @Override
        protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
            return new ByteVector().putByteArray(payload, 0, payload.length);
        }
    }
}
