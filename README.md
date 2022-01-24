# oracle-actions/setup-java

This action downloads a JDK built by Oracle (Oracle JDK or Oracle OpenJDK build), and installs it using [`actions/setup-java`](https://github.com/actions/setup-java).

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 pre-installed are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Inputs

### `source` Download Site

This `input` specifices from where the JDK build should be download from, it is set by detailt to `oracle.com`.

The following source download sites are supported:

- [`oracle.com`](https://oracle.com) (default) for Oracle JDK 17 (NTFC) and later
- [`java.net`](https://jdk.java.net) for the current OpenJDK GA (General Aavailability) build and for OpenJDK EA (Early-Access) builds (GPL v2 w/CPE), for example JDK 19-ea, Loom, Panama, or Valhalla

### `feature` Java Release or Project Name

The `feature` denotes either a Java feature release (ex. `17`, `18`, ...) or the name of an Early-Access project (ex. `loom`).

### `version` Additional Version Information

The `version` inputs defaults to `latest`.
It can be used to specify an explicit version of a Java release, ex. `17.0.1`.

### `install` Flag

This action uses [`actions/setup-java`](https://github.com/actions/setup-java) to install the downloaded JDK.
Pass `false` to override the default `true` value and skip the installation.

### `uri` Custom JDK

Use this input to download a JDK from the specified URI. The `website`, `feature`, and `version` inputs are ignored.

## Examples for `oracle.com`

The following examples use the [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) to download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

#### Download and install the latest version of the Oracle JDK.

```yaml
steps:
  - name: 'Set up latest Oracle JDK 17'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
```

#### Download and install a specific version of the Oracle JDK.

```yaml
steps:
  - name: 'Set up archived Oracle JDK 17.0.1'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
      version: 17.0.1
```

Make sure to check this note regarding the [use of older builds](#warning).

## Examples for `java.net`

The following examples download and install OpenJDK binaries that are made available under the [GNU General Public License, version 2, with the Classpath Exception](https://openjdk.java.net/legal/gplv2+ce.html).

#### Download and install the OpenJDK build of a given feature release

```yaml
steps:
  - name: 'Set up latest JDK N from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: N # Replace N with GA, EA, 17, 18, 19, ...
```

#### Download and install the Early-Access build of a given OpenJDK project

```yaml
steps:
  - name: 'Set up Early-Access build of a named project from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: Loom # or Panama, Valhalla, ...
```

## Supported JDKs hosted at `oracle.com`

This action only supports Oracle JDKs provided under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

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
Check [src/ListOpenJavaDevelopmentKits.java](src/ListOpenJavaDevelopmentKits.java) for details.

Run `java src/ListOpenJavaDevelopmentKits.java PAGE [MORE...]` to parse <https://jdk.java.net/PAGE/> only and print key-value pairs found on that particular page.

## :warning: <a id="warning"></a>Archived builds

Older versions of the JDK are provided to help developers debug issues in older systems. **They are not updated with the latest security patches and are not recommended for use in production.**


## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
