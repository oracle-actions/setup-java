# oracle-actions/setup-java

This action downloads a JDK built by Oracle (Oracle JDK or Oracle OpenJDK), and installs it using [`actions/setup-java`](https://github.com/actions/setup-java).

## Input Overview

| Input Name | Default Value | Description                                                                                                 |
|------------|:--------------|-------------------------------------------------------------------------------------------------------------|
| `website`  | `oracle.com`  | Specifies from where the JDK should be download from.                                                       |
| `feature`  | `17`          | Denotes a Java feature release number (`17`, `18`, ...) or a name of an Early-Access project (`Loom`, ...). |
| `version`  | `latest`      | Specifies an explicit version of a Java release.                                                            |
| `install`  | `true`        |                                                                                                             |

### Input `website`

The `website` specifies from where the JDK should be download from.
It defaults to `oracle.com`.

Following values are supported:

- [`oracle.com`](https://www.oracle.com/java/technologies/downloads/) for Oracle JDK 17 and later.

  This action only supports Oracle JDKs provided under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

- [`java.net`](https://jdk.java.net) for the current OpenJDK General Availability build and for OpenJDK Early-Access builds.

  Early-Access builds include the [mainline](https://github.com/openjdk/jdk/tags) JDK, and project Loom, Panama, and Valhalla.
 
  The [jdk.java.net-uri.properties](jdk.java.net-uri.properties) file provides a set of key-value pairs mapping JDK descriptions to their download links.

### Input `feature`

The `feature` input denotes a Java feature release number (`17`, `18`, ...) or a name of an Early-Access project (`Loom`, ...).
It defaults to the latest long-term support release for the Java SE platform., which is `17` as of today.

### Input `version`

The `version` input can be used to specify an explicit version of a Java release, ex. `17.0.1`.
It is set by default to `latest`.

___

**WARNING!**

Older versions of the JDK are provided to help developers debug issues in older systems.
**They are not updated with the latest security patches and are not recommended for use in production.**

___

### Input `install`

The `install` input enables or disables the automatic JDK installation of the downloaded JDK archive file.
It is enabled by default by using `true` as its value.

This action delegates to [`actions/setup-java`](https://github.com/actions/setup-java) in order to install the downloaded JDK archive file using default
Pass `false` to skip the automatic JDK installation and invoke `actions/setup-java` with your custom settings.

### Input `uri`

Use the `uri` input to download a JDK from the specified URI originating from a supported website.
The value of inputs `website`, `feature`, and `version` ignored.

## Examples for `oracle.com`

The following examples use the [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) to download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

### Download and install the latest version of Oracle JDK

```yaml
steps:
  - name: 'Set up latest Oracle JDK 17'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
```

### Download and install a specific version of Oracle JDK

```yaml
steps:
  - name: 'Set up archived Oracle JDK 17.0.1'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      feature: 17
      version: 17.0.1
```
___

**WARNING!**

Older versions of the JDK are provided to help developers debug issues in older systems.
**They are not updated with the latest security patches and are not recommended for use in production.**

___

## Examples for `java.net`

The following examples download and install OpenJDK binaries that are made available under the [GNU General Public License, version 2, with the Classpath Exception](https://openjdk.java.net/legal/gplv2+ce.html).

### Download and install an OpenJDK build of a given feature release

```yaml
steps:
  - name: 'Set up latest JDK N from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: N # Replace N with GA, EA, 17, 18, 19, ...
```

### Download and install an Early-Access build of a named OpenJDK project

```yaml
steps:
  - name: 'Set up Early-Access build of a named project from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: java.net
      feature: Loom # or Panama, Valhalla, ...
```

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 pre-installed are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
