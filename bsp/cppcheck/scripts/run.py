#!/usr/bin/env python
#
# Python
#
# Copyright 2020-2024 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
#

import os
import shutil
import sys
import re
import time 
import datetime
import platform
import subprocess
import pathlib
import argparse
import urllib.error
import urllib.request
import collections

SCRIPT_PATH = pathlib.Path(__file__).parent
DEFAULT_REPORT_FILE_PATH = pathlib.Path(SCRIPT_PATH.parent, 'report.xml')
DEFAULT_COVERAGE_FILE_PATH = pathlib.Path(SCRIPT_PATH.parent, 'unverified.txt')
DEFAULT_SUPPRESSIONS_FILE_PATH = pathlib.Path(SCRIPT_PATH.parent, 'suppressions.conf')
CPPCHECK_PROGRAMS = {
    'linux': 'cppcheck',
    'windows': 'C:\\Program Files\\Cppcheck\\cppcheck.exe'
}
HTML_REPORT_PROGRAM_URL = 'https://raw.githubusercontent.com/danmar/cppcheck/main/htmlreport/cppcheck-htmlreport'
DEFAULT_HTML_REPORT_PROGRAM = pathlib.Path(SCRIPT_PATH, 'cppcheck-htmlreport.py')
YEAR_STR = str(datetime.datetime.today().year)

############################################
# Searches and returns all directories with
# a specific name within a search path
#
def find_dirs(dirname, search_path):
    result = []

    # Wlaking top-down from the root
    for root, folder, files in os.walk(search_path):
        if root.endswith(dirname):
            result.append(root)
    return result


###########################################################
# Fills a d_conf dictionnary with all configuration flags
# found in ../cppcheck.conf file
# Each d_conf key correspond to a different run configuration
# provided with # config <conf_name> in ../cppcheck.conf
# All configuration flags provided before the run configurations
# are placed in the "global" configuration key.
def parse_conf(d_conf):
    # Configuration file
    conf_file = "../cppcheck.conf"
    conf_name = "global"
    # Define cproject
    cproject = "..\\..\\src\\main\\c"

    errors = 0

    # search for inc folders and add them as include dirs
    inc_dirs = find_dirs("inc", cproject.replace("\\", "/"))
    d_conf[conf_name] += [j for i in (["-I", x] for x in inc_dirs) for j in i]

    if not os.path.isfile(conf_file):
        print("-I- no cppcheck.conf file found, defaulting to standard configuration")
        d_conf[conf_name].append(cproject)
        return

    # read conf file
    with open(conf_file, "r", encoding='utf-8', errors='replace') as f:
        line_num = 0
        for line in f.readlines():
            line_num += 1
            line = line.strip()

            # check if config name line
            if "# config" in line:
                config = line.replace("# config", "").strip()
                if config in d_conf.keys():
                    print("-E- Duplicated definition of config %s line %d" % (config, line_num))
                    errors += 1
                else:
                    print("-I- Find config %s" % config)
                    d_conf[config] = list()
                    conf_name = config
                continue

            # skip comment and blank lines
            if line.startswith("#") or line == "":  # Skip blank and comment lines
                continue

            # check if include dir, ignore dir
            prefixes = ["-I", "-i"]
            header = ""
            for prefix in prefixes:
                if line.startswith(prefix):
                    line = line.replace(prefix, "").strip()
                    header = prefix

            # check that folder exists if source or include dir
            if not line.startswith("-"):
                source = line
                source = source.replace("\\", "/")
                source = str(pathlib.Path("..", line))

                if not os.path.isdir(source) and not os.path.isfile(source):
                    print("-E- Can not find %s defined in %s" % (line, conf_file))
                    errors += 1
                else:
                    print("-I- Add %s from %s" % (line, conf_file))
                    d_conf[conf_name].append(header)
                    d_conf[conf_name].append(source)
                continue

            # Add other lines as with no check
            d_conf[conf_name].append(line)

    if errors:
        print()
        print("-E- Check the above lines for incorrect paths in %s files" % conf_file)
        sys.exit(-1)



##########################################################
# Executes cppcheck with the provided options
def exec_cppcheck(cppcheckargs, conf):
    # look up cppcheck program for the current system
    system_name = platform.system().lower()
    if system_name not in CPPCHECK_PROGRAMS:
        raise Exception(f"System '{system_name}' not supported")

    cppcheck = CPPCHECK_PROGRAMS[system_name]
    build_dir = f'.cppcheck.{conf}'

    # (re-)create build directory as needed
    if os.path.isdir(build_dir):
        shutil.rmtree(build_dir)
    os.mkdir(build_dir)

    # get cppcheck version
    p = subprocess.run([cppcheck, '--version'],
            capture_output=True)
    if p.returncode:
        raise Exception('Failed to get cppcheck version')
    m = re.match(r'^Cppcheck\s+(.+)\s*$', p.stdout.decode(), re.I)
    if not m:
        raise Exception('Failed to parse cppcheck version')
    cppcheck_version = m.group(1)

    # TODO Compare with expected version

    # 
    p = subprocess.run([cppcheck] + cppcheckargs + [f'--cppcheck-build-dir={build_dir}'],
            stderr=sys.stderr, stdout=sys.stdout)
    if p.returncode:
        sys.exit(-1)

##########################################################
# Parses the .a1.dump files generated by cppcheck to
# determine the source files that have been verified.
# For each verified source file, the list of verified
# lines are stored in a d_coverage[<filename>] set.
def check_cppcoverage(conf, d_coverage):
    build_dir = ".cppcheck." + conf

    # get list of dump files
    dumpfiles = [x for x in os.listdir(build_dir) if "a1.dump" in x]

    for dumpfile in dumpfiles:
        dumpfile = build_dir + "/" + dumpfile
        with open(dumpfile, "r", encoding='utf-8', errors='replace') as f:
            for line in f.readlines():
                if "<tok " in line:
                    continue

                m = re.match(r'.*<file.*name="(.*?)".*', line)
                if m:
                    file = m.group(1)
                    d_coverage[conf][file].add(-1)

                m = re.match(r'.*file="(.*?)".*line\w*="(\d+).*', line)
                if m:
                    file = m.group(1)
                    linenr = m.group(2)
                    d_coverage[conf][file].add(int(linenr))

##########################################################
# Prints lines that have not been verified
def print_coverage(d_coverage, coverage_file):
    # merge all conf_coverage
    d_all_coverage = dict()

    for conf in d_coverage.keys():
        for file in d_coverage[conf].keys():
            if file not in d_all_coverage.keys():
                d_all_coverage[file] = set()
            for linenr in d_coverage[conf][file]:
                d_all_coverage[file].add(linenr)

    with open(coverage_file, "w", encoding='utf-8', errors='replace') as fw:
        fw.write(
'''# 
# TXT
# 
# Copyright ''' + YEAR_STR + ''' MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# 

''')

        fw.write("--- CPPCheck unverified lines --\n\n")
        for file in d_all_coverage.keys():
            fw.write("--- %s\n" % file)

            skip_next = False
            with open(file, "r", encoding='utf-8', errors='replace') as fr:
                for (i, line) in enumerate(fr.readlines()):
                    linenr = i + 1
                    if linenr not in d_all_coverage[file]:
                        if skip_next:
                            skip_next = line.strip().endswith('\\')
                            continue
                        if not line.strip():
                            continue
                        if line.strip().startswith("//"):
                            continue
                        if line.strip().startswith("/*"):
                            continue
                        if line.strip().startswith("*"):
                            continue
                        fw.write("%d %s\n" % (linenr, line.strip()))

                    # skip multiline lines
                    skip_next =  line.strip().endswith('\\')


##########################################################
# Merges runs reports
# Adds copyright to report.xml
def merge_report(report_file, d_conf, d_coverage):
    # Merge reports
    # get errors for each configuration
    d_errors = dict()
    for conf in d_conf.keys():
        # Skip global if other configurations exists
        if "global" in conf and len(d_conf.keys()) != 1:
            continue
        config_report = conf + "_report.xml"

        with open(config_report, "r", encoding='utf-8', errors='replace') as f:
            config_str = f.read()
        os.remove(config_report)

        errors = re.findall(r"<error .*?</error>", config_str, re.DOTALL)

        for error in errors:
            # Skip error if Unmatched suppression on not verified line
            m = re.match(r'.*Unmatched suppression:.*file="(.*?)" line="(\d+).*', error, re.DOTALL)
            if m:
                file = m.group(1).replace("\\", "/")
                line = int(m.group(2))

                if line not in d_coverage[conf][file]:
                    continue

            if error not in d_errors.keys():
                d_errors[error] = list()
            d_errors[error].append(conf)

    # update error text msg with configuration
    l_errors = list()
    for error in d_errors.keys():
        error = error.replace('msg="', 'msg="(' + ",".join(d_errors[error]) + ") ")
        l_errors.append(error)

    # replace <error> in report.xml from report files
    with open(report_file, "r", encoding='utf-8', errors='replace') as f:
        report_string = f.read()

    error_tag = "zzzzzzz"
    regex0 = re.compile(r"(<errors>.*</errors>)", re.DOTALL)
    report_string = regex0.sub(error_tag, report_string)

    report_string = report_string.replace(error_tag,
                                          '<errors>' +
                                          "\n    ".join(l_errors) +
                                          '\n    </errors>\n    '
                                          )
    with open(report_file, "w", encoding='utf-8', errors='replace') as f:
        f.write(report_string)

    # Prepend report.xml with copyright
    if not os.path.isfile(report_file):
        print("-E- Can not find %s after cppcheck execution, check your setup." % report_file)
        sys.exit(-1)

    # Read report_file
    report_lines = []
    with open(report_file, "r", encoding='utf-8', errors='replace') as f:
        report_lines += f.readlines()

    with open(report_file, "w", encoding='utf-8', errors='replace') as fw:
        # Write first line of the report
        fw.write(report_lines[0])

        if os.path.isfile("../copyright.conf"):
            with open("../copyright.conf", "r", encoding='utf-8', errors='replace') as fr:
                for line in fr.readlines():
                    fw.write(line)
        else:
            fw.write(
                "<!--\n Copyright " + YEAR_STR + " MicroEJ Corp. All rights reserved.\n Use of this source code is governed by a "
                "BSD-style license that can be found with this software.\n-->\n")

        # Write back report lines
        for line in report_lines[1:]:
            fw.write(line)


def get_file_age(file):
    x=os.stat(file)
    return (time.time()-x.st_mtime) 

##########################################################
# Generates html report
# Adds copyright to report.xml
def html_report(report_file, html_report_program):

    if not html_report_program.is_file():
        try:
            urllib.request.urlretrieve(HTML_REPORT_PROGRAM_URL, html_report_program)
        except urllib.error.URLError as e:
            raise Exception(f"Failed to download '{html_report_program.name}': {e}")

    p = subprocess.run([sys.executable, html_report_program,
            '--source-encoding=iso8859-1',
            '--title=cppcheck',
            f'--file={report_file}',
            f'--report-dir={SCRIPT_PATH}/../html-report',
            '--source-dir=.'])
    if p.returncode:
        raise Exception('Failed to generate HTML report')


##########################################################
# Main program
def main():
    # handle command-line parsing
    parser = argparse.ArgumentParser(
        description='Run cppcheck static code analyzer.')
    parser.add_argument('-c', '--coverage', required=False, type=str, dest='coverage_file_path', default=DEFAULT_COVERAGE_FILE_PATH,
        help=f'Path to the output coverage file (default: {DEFAULT_COVERAGE_FILE_PATH}).')
    parser.add_argument('-r', '--report', required=False, type=str, dest='report_file_path', default=DEFAULT_REPORT_FILE_PATH,
        help=f'Path to the output report file (default: {DEFAULT_REPORT_FILE_PATH}).')
    parser.add_argument('-p', '--reportprogram', required=False, type=str, dest='html_report_program', default=DEFAULT_HTML_REPORT_PROGRAM,
        help=f'Path to the html report generation script (default: {DEFAULT_HTML_REPORT_PROGRAM}).')
    parser.add_argument('-s', '--suppressions', required=False, dest='suppressions_file_paths', action='append', default=[DEFAULT_SUPPRESSIONS_FILE_PATH],
        help=f'Path to an input suppressions file (default: {DEFAULT_SUPPRESSIONS_FILE_PATH}).')
    arguments = parser.parse_args()

    # sanitize arguments
    coverage_file_path = pathlib.Path(arguments.coverage_file_path)
    report_file_path = pathlib.Path(arguments.report_file_path)
    html_report_program = pathlib.Path(arguments.html_report_program)
    suppressions_file_paths = [
        pathlib.Path(p)
        for p in arguments.suppressions_file_paths
        if os.path.isfile(p)
    ]

    # Change dir to script folder
    os.chdir(SCRIPT_PATH)

    # get configurations from cpp_check conf files
    d_conf = dict()
    d_conf["global"] = list()
    parse_conf(d_conf)

    # initialize coverage dictionnary
    d_coverage = collections.defaultdict(lambda: collections.defaultdict(set))

    # Define cppcheck default arguments
    cppcheckargs = [
        "--inline-suppr",
        "--xml",
        f"--output-file={report_file_path}",
        "--platform=arm32-wchar_t2.xml",
        "--std=c99",
        "--enable=all",
        "--addon=misra.json",
        "--verbose",
        *[f"--suppressions-list={p}" for p in suppressions_file_paths],
        "-DCPPCHECK",
    ]

    # Run cppcheck for each configuration
    for conf in d_conf:
        # Skip global if other configurations exists
        if 'global' == conf and len(d_conf) != 1:
            continue
        elif 'global' != conf:
            # add global configuration to current config
            d_conf[conf] += d_conf['global']

        # check that at least one source folder is provided
        source_folder = [x for x in d_conf[conf] if not x.startswith("-")]

        if not source_folder:
            pass

        # add conf to cppcheckargs
        runargs = cppcheckargs + d_conf[conf]

        # exec cppcheck
        exec_cppcheck(runargs, conf)

        # check coverage
        check_cppcoverage(conf, d_coverage)

        # copy report file
        config_report = conf + "_report.xml"
        shutil.copy(report_file_path, config_report)

    # print unchecked lines for each files
    print_coverage(d_coverage, coverage_file_path)

    # merge and clean report file
    merge_report(report_file_path, d_conf, d_coverage)

    # Generate htmlreport
    html_report(report_file_path, html_report_program)


#####################################
# Main execution

if __name__ == "__main__":
    main()
