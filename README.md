# oracle-actions/setup-java

This action downloads a JDK built by Oracle and installs it using [`actions/setup-java`](https://github.com/actions/setup-java).

JDKs built by Oracle are Oracle JDK and Oracle OpenJDK.

## Input Overview

| Input Name | Default Value | Description                                                     |
|------------|--------------:|-----------------------------------------------------------------|
| `website`  |  `oracle.com` | From where the JDK should be download from.                     |
| `release`  |          `17` | Java feature release number or name of an Early-Access project. |
| `version`  |      `latest` | An explicit version of a Java release.                          |
| `install`  |        `true` | Install the downloaded JDK archive file.                        |
| `uri`      |       _empty_ | Custom URI of a JDK archive file to download                    |

### Input `website`

The `website` input specifies from where the JDK should be download from.
It defaults to `oracle.com`.

Following values are supported:

- [`oracle.com`](https://www.oracle.com/java/technologies/downloads/) for Oracle JDK 17 and later.

  This action only supports Oracle JDKs provided under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

- [`jdk.java.net`](https://jdk.java.net) for the current OpenJDK General Availability build and for OpenJDK Early-Access builds.

  Early-Access builds include the [mainline](https://github.com/openjdk/jdk/tags) JDK, and project Loom, Panama, and Valhalla.
 
  The [jdk.java.net-uri.properties](jdk.java.net-uri.properties) file provides a set of key-value pairs mapping OpenJDK descriptions to their download links.

### Input `release`

The `release` input denotes a Java feature release number (`17`, `18`, ...) or a name of an Early-Access project (`loom`, ...).
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

## Examples for `jdk.java.net`

The following examples download and install OpenJDK binaries that are made available under the [GNU General Public License, version 2, with the Classpath Exception](https://openjdk.java.net/legal/gplv2+ce.html).

### Download and install an OpenJDK build of a given release

```yaml
steps:
  - name: 'Set up latest JDK N from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: jdk.java.net
      release: N # Replace N with GA, EA, 17, 18, 19, ...
```

### Download and install an Early-Access build of a named OpenJDK project

```yaml
steps:
  - name: 'Set up Early-Access build of a named project from jdk.java.net'
    uses: oracle-actions/setup-java@v1
    with:
      website: jdk.java.net
      release: loom # or panama, valhalla, ...
```

## Supported GitHub Actions Virtual Environments

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 11 pre-installed are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
