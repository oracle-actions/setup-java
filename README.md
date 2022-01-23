# oracle-actions/setup-java

Download and set up a JDK built by Oracle for GitHub Actions ready-to-use in your workflow.

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 preinstalled are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Supported Download Sites

- [`oracle.com`](https://oracle.com) for JDK 17 and later
- [`java.net`](https://jdk.java.net) for current GA and EA builds, for example JDK 19-ea and Loom, Panama, and Valhalla

## Examples for `oracle.com`

Leveraging [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) the following examples download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

```yaml
- name: 'Set up latest OracleJDK 17'
  uses: oracle-actions/setup-java@VERSION
  with:
    website: oracle.com
    feature: 17
```

```yaml
steps:
- name: 'Set up archived OracleJDK 17.0.1'
  uses: oracle-actions/setup-java@VERSION
  with:
    website: oracle.com
    feature: 17
    version: 17.0.1
```

## Examples for `java.net`

```yaml
- name: 'Set up latest OpenJDK N from jdk.java.net'
  uses: sormuras/setup-jdk@main
   with:
    website: java.net
    feature: N # Replace N with GA, EA, 17, 18, 19, ...
```

```yaml
- name: 'Set up Early-Access build of a named project from jdk.java.net'
  uses: oracle-actions/setup-java@VERSION
   with:
    website: java.net
    feature: Loom # or Panama, Valhalla, ...
```

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
