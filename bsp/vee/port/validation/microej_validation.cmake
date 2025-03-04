# Copyright 2023-2024 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.

include_guard()
message("microej/tests component is included.")

target_sources(${MCUX_SDK_PROJECT_NAME} PRIVATE
	${CMAKE_CURRENT_LIST_DIR}/src/core_portme.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_core_core_benchmark.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_core_main.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_core_print.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_core_ram.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_core_time_base.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_llkernel_main.c
	${CMAKE_CURRENT_LIST_DIR}/src/t_llkernel.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_impl_core_benchmark.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_impl_ram_checks_MIMXRT1170.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_impl_ram_checks.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_impl_ram_speed.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_ram_checks.c
	${CMAKE_CURRENT_LIST_DIR}/src/x_ram_speed.c
	${CMAKE_CURRENT_LIST_DIR}/src/dtcram_buffer.c
	${CMAKE_CURRENT_LIST_DIR}/src/ocram_buffer.c
)
target_include_directories(${MCUX_SDK_PROJECT_NAME} PRIVATE    ${CMAKE_CURRENT_LIST_DIR}/inc)