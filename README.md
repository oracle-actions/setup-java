# oracle-actions/setup-java

Download and set up a JDK built by Oracle for GitHub Actions ready-to-use in your workflow.

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 preinstalled are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Inputs

### `website` Download Site

The `website` input defaults to `oracle.com`.

Supported download site are:

- [`oracle.com`](https://oracle.com) for JDK 17 and later
- [`java.net`](https://jdk.java.net) for current GA and EA builds, for example JDK 19-ea and Loom, Panama, and Valhalla

### `feature` Java Release or Project Name

The `feature` denotes either a Java release feature number (`17`, `18`, ...) or a name of an Early-Access project.

### `version` Addition Version Information

The `version` inputs defaults to `latest`.
It can be used to specify an explicit version of a Java release, like `17.0.1`.

### `install` Flag

The `install` flag defaults to `true`.

Pass `false` to skip running [`actions/setup-java`](https://github.com/actions/setup-java) as part of this action.

### `uri` Custom JDK

Download any JDK from a supported website.
When this input is used, it overrides values given via `website`, `feature`, and `version` inputs.

## Examples for `oracle.com`

Leveraging [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) the following examples download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

```yaml
steps:
  - name: 'Set up latest OracleJDK 17'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
```

```yaml
steps:
  - name: 'Set up archived OracleJDK 17.0.1'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
      version: 17.0.1
```

## Examples for `java.net`

Leveraging a generated [map](#supported-jdks-hosted-at-javanet) the following examples download and set up binaries that are made available under the [GNU General Public License, version 2, with the Classpath Exception](https://openjdk.java.net/legal/gplv2+ce.html).

```yaml
steps:
  - name: 'Set up latest JDK N from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: N # Replace N with GA, EA, 17, 18, 19, ...
```

```yaml
steps:
  - name: 'Set up Early-Access build of a named project from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: Loom # or Panama, Valhalla, ...
```

## Supported JDKs hosted at `java.net`

The [jdk.java.net-uri.properties](jdk.java.net-uri.properties) file provides a set of key-value pairs mapping JDK descriptions to their download links.

```properties
17,17.0.1,linux,x64=https://download.java.net/java/[...]/GPL/openjdk-17.0.1_linux-x64_bin.tar.gz
```

Keys are composed of `FEATURE,VERSION,OS-NAME,OS-ARCH` with:

- `FEATURE`: Either a release feature number or a name of an early-access project
- `VERSION`: Either a specific version or `latest`
- `OS-NAME`: An operating system name, usually one of: `linux`, `macos`, `windows`
- `OS-ARCH`: An operating system architecture, like: `aarch64`, `x64`, or `x64-musl`

Run `java src/ListOpenJavaDevelopmentKits.java` to parse a set of default pages hosted at <https://jdk.java.net> and print all key-value pairs, including aliases.
Consult [src/ListOpenJavaDevelopmentKits.java](src/ListOpenJavaDevelopmentKits.java) for details.

Run `java src/ListOpenJavaDevelopmentKits.java PAGE [MORE...]` to parse <https://jdk.java.net/PAGE/> only and print key-value pairs found on that particular page.

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
