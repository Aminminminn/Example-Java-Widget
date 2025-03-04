#!/usr/bin/env python
#
# Python
#
# Copyright 2023-2024 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
#

import os
import sys
import time 
import json
import subprocess
import platform
import shutil

def run_cppcheck():
    subprocess.call(["python", "./scripts/run.py"])

def create_compile_commands_file():
    if platform.system() == "Windows": 
        print("Call build script on Windows")
        subprocess.call("..\\vee\\scripts\\build.bat") # add option export compile commands in cmake.
        shutil.copyfile("./../vee/scripts/armgcc/flexspi_nor_sdram_release_evkb/compile_commands.json", "./compile_commands.json") 
        
        with open('compile_commands.json', "r+") as f:
            data = json.load(f)
            
            newJson = [elem for elem in data if 'mcux-sdk' not in elem['file'] and 'sdk_overlay' not in elem['file'] and 'thirdparty' not in elem['file'] and 'projects/common' not in elem['file']]
            
            f.seek(0)
            json.dump(newJson, f, ensure_ascii=False, indent=4)
            f.truncate()

if __name__ == "__main__":
	create_compile_commands_file()
	run_cppcheck() 
