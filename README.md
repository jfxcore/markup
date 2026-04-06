# JFXcore.markup
Support library for FXML 2.0 markup. This library is only required for FXML 2.0 documents that use
one of the following FXML 2.0 features:
- [Markup extensions](https://jfxcore.github.io/fxml-compiler/markup-extension.html)
- [Embedded markup](https://jfxcore.github.io/fxml-compiler/getting-started/embedded.html) using the `ComponentView` annotation
- The `InverseMethod` annotation for compiled bindings

# Releases
The latest release is available on [Maven Central](https://central.sonatype.com/artifact/org.jfxcore/markup/0.1.0).

## Maven
```xml
<dependency>
    <groupId>org.jfxcore</groupId>
    <artifactId>markup</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Gradle
```kotlin
dependencies {
    implementation("org.jfxcore:markup:0.2.0")
}
```
