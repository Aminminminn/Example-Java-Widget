include_guard()
message("segger component is included.")

target_sources(${MCUX_SDK_PROJECT_NAME} PRIVATE
"${CMAKE_CURRENT_LIST_DIR}/SEGGER.h"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_RTT.c"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_RTT.h"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW.c"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW.h"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW_ConfDefaults.h"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW_FreeRTOS.c"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW_FreeRTOS.h"
"${CMAKE_CURRENT_LIST_DIR}/SEGGER_SYSVIEW_Int.h"
"${CMAKE_CURRENT_LIST_DIR}/Config/Cortex-M/SEGGER_SYSVIEW_Config_FreeRTOS.c"
)

target_include_directories(${MCUX_SDK_PROJECT_NAME} PRIVATE
    ${CMAKE_CURRENT_LIST_DIR}/.
    ${CMAKE_CURRENT_LIST_DIR}/Config/.
)
