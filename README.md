# oracle-actions/setup-java

Download and set up a JDK built by Oracle for GitHub Actions ready-to-use in your workflow.

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 preinstalled are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Supported Download Sites

- [`oracle.com`](https://oracle.com) for JDK 17 and later

## Examples for `oracle.com`

Leveraging [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) the following examples download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

```yaml
- name: 'Set up latest OracleJDK 17'
  uses: oracle-actions/setup-java@VERSION
  with:
    website: oracle.com
    feature: 17
    version: latest
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

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
