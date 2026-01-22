# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

This project uses tags and branches for [release management](https://docs.github.com/en/actions/creating-actions/about-custom-actions#using-tags-for-release-management).


## [Unreleased]
### Changed
- Update version of `actions/checkout` to `v6.0.1`
- Update version of `actions/setup-java` to `v5.2.0`

## [1.5.0] - 2025-09-19
### Changed
- Default value of `release` input to Java `25`
- Use SHA-pinned versions of other actions

## [1.4.2] - 2025-05-23
- Various improvements

## [1.4.1] - 2025-03-19
### Changed
- Default value of `release` input to Java `24`

## [1.4.0] - 2024-03-21
### Added
- Java `24` and project `leyden` to the list of Early-Access releases
- New `version: stable` mode for `release: ea` setups
- Retry of failed JDK archive downloads
### Changed
- Default value of `release` input to Java `23`

## [1.3.4] - 2024-03-21
### Fixed
- Support running on ARM64 machines
### Changed
- Default value of `release` input to Java `22`
- Run action with pre-installed Java 21

## [1.3.3] - 2024-01-29
### Added
- Java `23` to the list of Early-Access releases
### Changed
- Update version of `setup-java` to `v4`

## [1.3.2] - 2023-09-19
### Added
- Java `22` to the list of Early-Access releases
### Changed
- Default value of `release` input to Java `21`

## [1.3.1] - 2022-10-20
### Added
- Project `genzgc` to the list of Early-Access releases
### Fixed
- Use GitHub's environment file to set output values 

## [1.3.0] - 2022-09-21
### Changed
- Default value of `release` input to Java `19`

## [1.2.1] - 2022-08-08
### Fixed
- Default `install-as-version` value computation

## [1.2.0] - 2022-08-08
### Added
- Project `jextract` to the list of Early-Access releases
- New `install-as-version` input

## [1.1.2] - 2022-06-10
### Added
- Java `20` to the list of Early-Access releases

## [1.1.1] - 2022-05-03
### Fixed
- Version string parsing when update segment is present

## [1.1.0] - 2022-04-01
### Changed
- Version of `setup-java` to `v3`
- Default value of `release` input to Java `18`

## [1.0.0] - 2022-02-18
### Added
- Initial Release

[Unreleased]: https://github.com/oracle-actions/setup-java/compare/v1.5.0...HEAD
[1.5.0]: https://github.com/oracle-actions/setup-java/compare/v1.4.2...v1.5.0
[1.4.2]: https://github.com/oracle-actions/setup-java/compare/v1.4.1...v1.4.2
[1.4.1]: https://github.com/oracle-actions/setup-java/compare/v1.4.0...v1.4.1
[1.4.0]: https://github.com/oracle-actions/setup-java/compare/v1.3.4...v1.4.0
[1.3.4]: https://github.com/oracle-actions/setup-java/compare/v1.3.3...v1.3.4
[1.3.3]: https://github.com/oracle-actions/setup-java/compare/v1.3.2...v1.3.3
[1.3.2]: https://github.com/oracle-actions/setup-java/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/oracle-actions/setup-java/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/oracle-actions/setup-java/compare/v1.2.1...v1.3.0
[1.2.1]: https://github.com/oracle-actions/setup-java/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/oracle-actions/setup-java/compare/v1.1.2...v1.2.0
[1.1.2]: https://github.com/oracle-actions/setup-java/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/oracle-actions/setup-java/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/oracle-actions/setup-java/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/oracle-actions/setup-java/releases/tag/v1.0.0
