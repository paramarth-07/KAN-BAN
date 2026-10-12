# Technical Findings

- Standalone Apache Maven 3.9.9 is installed at `/tmp/apache-maven-3.9.9/bin/mvn`.
- Temurin OpenJDK 25 is installed at `/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home` and compiles Java 21 / 25 code seamlessly.
- Spring Boot 3.3.4 / 3.4.0 provides compatibility with Java 21+, Hibernate 6, and Spring Security 6.
- Thymeleaf Spring Security Extras: `thymeleaf-extras-springsecurity6` provides `sec:authorize` and `sec:authentication` directives.
- Playful Geometric design tokens extracted from `ui-ux-pro-max/data/design-prompts.json`:
  - Background `#FFFDF5`, Foreground `#1E293B`, Accent `#8B5CF6`, Secondary `#F472B6`, Tertiary `#FBBF24`, Quaternary `#34D399`.
  - 4px hard shadows `4px 4px 0px 0px #1E293B`, 2px solid borders.
