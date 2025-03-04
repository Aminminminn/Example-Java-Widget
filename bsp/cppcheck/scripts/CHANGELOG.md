# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.3] - Unreleased

### Changed

  - Disable all suppressions in suppressions.conf
  - Update .gitignore for the file cppcheck-htmlreport.py.

### Fixed

  - Fix accessing nonexistent keys in d_coverage
  - Fix usage of absolute paths in configuration
  - Use raw string for regexp when dealing with errors

## [2.0.2] - 2024-06-24

### Fixed

  - Fix include directives

## [2.0.1] - 2024-06-18

### Changed

  - Fix module.ivy version

## [2.0.0] - 2024-06-17

### Added

  - Add linux support for script run.py

## [1.1.1] - 2024-02-29

### Added

  - Add utf-8 encoding and error replace during file opening.

### Changed

  - Update `suppressions.conf` to comment default rules excluded and create a section to exclude rules for specific files.
  - Update the required version of cppcheck to 2.13.0.
  - Update the file `MISRA_C_2012_rules.txt` to match with MISRA C:2012 Amendment 1 and 2.


## [1.1.0] - 2023-04-10

### Added

  - Add a default `suppressions.conf` file for default MicroEJ MISRA configuration.
  - Add source encoding iso8859-1 option for HTML report.


## [1.0.0] - 2023-06-27

### Added

  - Initial revision.

---
_Copyright 2023-2024 MicroEJ Corp. All rights reserved._
_Use of this source code is governed by a BSD-style license that can be found with this software._
