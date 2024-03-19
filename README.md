# oracle-actions/setup-java

This action downloads a Java Development Kit (JDK) built by Oracle and installs it using [`actions/setup-java`](https://github.com/actions/setup-java).

JDKs built by Oracle are [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) and [Oracle OpenJDK](https://jdk.java.net).

## Input Overview

| Input Name            | Default Value | Description                                                     |
|-----------------------|--------------:|-----------------------------------------------------------------|
| `website`             |  `oracle.com` | From where the JDK should be downloaded from.                   |
| `release`             |          `22` | Java feature release number or name of an Early-Access project. |
| `version`             |      `latest` | An explicit version of a Java release.                          |
| `install`             |        `true` | Install the downloaded JDK archive file.                        |
| `install-as-version`  |       _empty_ | Control the value passed as `java-version`                      |
| `uri`                 |       _empty_ | Custom URI of a JDK archive file to download.                   |

### Input `website`

The `website` input specifies from where the JDK should be downloaded from.
It defaults to `oracle.com`.

Following values are supported:

- [`oracle.com`](https://www.oracle.com/java/technologies/downloads/) for Oracle JDK 17 and later.

  This action only supports Oracle JDKs provided under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

- [`jdk.java.net`](https://jdk.java.net) for the current OpenJDK General Availability build and for OpenJDK Early-Access builds.

  Early-Access builds include the [mainline](https://github.com/openjdk/jdk/tags) JDK, Generational ZGC, Project Loom and jextract, Panama, Valhalla, etc.

  The [jdk.java.net-uri.properties](jdk.java.net-uri.properties) file provides a set of key-value pairs mapping OpenJDK descriptions to their download links.

### Input `release`

The `release` input denotes a Java feature release number (`17`, `18`, ...) or a name of an Early-Access project (`loom`, ...).
It defaults to the current General-Availability Release for the Java SE platform., which is `22` as of today.

Note that websites may offer a different set of available releases.
For example, `oracle.com` only offers releases of `17` and above; it does not offer any Early-Access releases.

Note also that websites may stop offering any release at any time.
Please consult the website for details which release is offered for how long.

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

### Input `install-as-version`

The `install-as-version` input allows overriding the value passed as `java-version` to the underlying `actions/setup-java` action.

Supported values of `install-as-version` include:
- `PARSE_URI` parses the computed or given URI for a valid Java version string, ex. `17.0.1`.
- `HASH_URI` returns the `hashCode()` of the computed or given URI as a string, ex. `12345`.
- All strings [supported by `actions/setup-java`](https://github.com/actions/setup-java#supported-version-syntax)

The default value of `install-as-version` depends on the `release` input documented above:
- If `release` input starts with a digit, `install-as-version` defaults to `PARSE_URI`.
- If `release` input does not start with a digit, `install-as-version` defaults to `HASH_URI`.

### Input `uri`

Use the `uri` input to download a JDK from the specified URI originating from a supported website.
The value of inputs `website`, `release`, and `version` ignored.

## Examples for `oracle.com`

The following examples use the [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/) to download and set up binaries that are made available under the [Oracle No-Fee Terms and Conditions License](https://www.java.com/freeuselicense/).

### Download and install the latest version of Oracle JDK

```yaml
steps:
  - name: 'Set up latest Oracle JDK 22'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      release: 22
```

### Download and install a specific version of Oracle JDK

```yaml
steps:
  - name: 'Set up archived Oracle JDK 17.0.1'
    uses: oracle-actions/setup-java@v1
    with:
      website: oracle.com
      release: 17
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

All [environments](https://github.com/actions/virtual-environments#available-environments) that have Java 17 pre-installed are supported.
These include the following labels: `ubuntu-latest`, `macos-latest`, and `windows-latest`.

## More information

Make sure to check [the announcement and the FAQ](https://inside.java/2022/03/11/setup-java/) on Inside Java.

## Status

[![.github/workflows/test.yml](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml/badge.svg)](https://github.com/oracle-actions/setup-java/actions/workflows/test.yml)
