# Overview

This repository contains a python wrapping script (`run.py`) over cppcheck tool. 
The main objective of this wrapping script is to standardize the way cppcheck is executed in the different projects.

Another objective is to ensure that all code lines have been covered by cppcheck by running it with different configurations of the c defines.

# Usage

This repository must be added as a git submodule folder named `scripts` in a cppcheck folder of the project.
It must be associated with a cppcheck.conf and a suppressions.conf file.
It generates a `report.xml` and an `unverified.txt` file.

Template:
```
  <project_root> --- src
                      |--- main
                            |--- c
                                 |--- inc
                                 |--- src
                 --- cppcheck
                       |--- cppcheck.conf
                       |--- suppressions.conf
                       |--- report.xml
                       |--- unverified.txt
                       |--- scripts
                             | < this repository as a submodule> 
```
The `run.py` script must be run from the cppcheck folder with `python ./scripts/run.py`. 

By default, all source files in src/main/c/src will be verified by cppcheck.

Note: if you have a cppcheck.conf file, the source directory must be specified in it. Otherwise you can run `python ./scripts/run.py <source_folder>` but without a cppcheck.conf file present.

# Requirements

## cppcheck

On MS Windows the cppcheck tools must be installed in that folder: `C:\Program Files\Cppcheck`. You can get it from https://github.com/danmar/cppcheck/releases.

Select the version 2.13.0.

The cppcheck addons scripts must be installed in `C:\Program Files\Cppcheck\addons` folder.

On Linux install cppcheck package.

## python modules

The `pygments` python module is required during the creation of the html report page.
It can be installed with this command: `pip install pygments`.

# Dependencies

_All dependencies are retrieved transitively by MicroEJ Module Manager_.

# Source

N/A.

# Restrictions

None.

# Setup

## cppcheck.conf

The `cppcheck.conf` file provided a list of configurations cppcheck will run on. Each configuration contains extra arguments that will be added to the cppcheck command line for this configuration run.

A configuration starts with this tag: `# config <config_name>`.
Every other line staring with a `#` are commented out.

Next lines can contain new cppcheck arguments like for instance:
- `-i ../src/main/c/subfolder` to exclude all source s from the `src/main/c/subfolder` folder
- `-DNUMBER_OF_BUFFERS=3` to configure the `NUMBER_OF_BUFFERS` c define to the value `3`
  
Every line added before a `# config <config_name>` will be used for all configurations.

## suppressions.conf

The `suppressions.conf` contains the list of misra-rules that are ignored during the check. 
The rules can be ignored for all source files, for example with this line:
`misra-c2012-17.1`

They can also be ignored for a specific file, for example with this line:
`misra-c2012-12.2:..\..\src\main\c\ui\src\<filename.c>`

Suppression can also be added in the code source adding a comment line before the source code line, for instance:
`// cppcheck-suppress [misra-c2012-9.3] array is fully initialized`

Refer to the cppcheck documentation for more information.

Please use the `suppressions.conf` provided in this repository in order to retrieve the default MicroEJ configuration for all C developments.

# Outputs

## report.xml

The `report.xml` contains the list of failling rules associated with the source code file and line involved.
For instance:
```
<error id="misra-c2012-10.7" severity="style" msg="(FULL) If a composite expression is used as one operand of an operator in which the usual arithmetic conversions are performed then the other operand shall not have wider essential type" verbose="If a composite expression is used as one operand of an operator in which the usual arithmetic conversions are performed then the other operand shall not have wider essential type" file0=".cppcheck.FULL/display_dma.a1.dump">
            <location file="..\..\src\main\c\ui\src\display_dma.c" line="110" column="42"/>
        </error>
```

## html-report folder

This folder contains an html view of the `report.xml` file.

## unverified.txt

The `unverified.txt` file contains the lines for each file that have not been verified by cppcheck. If any, new configurations must be created to check those lines.


---
_Copyright 2022-2024 MicroEJ Corp. All rights reserved._
_Use of this source code is governed by a BSD-style license that can be found with this software._
