/*
 * Copyright 2019, 2021, 2023-2024 NXP
 * All rights reserved.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright 2023 MicroEJ Corp. This file has been modified by MicroEJ Corp.
 */

/* FreeRTOS kernel includes. */
#include "FreeRTOS.h"
#include "task.h"
#include "cpuload.h"

#include "nxpvee_ui.h"
#include "fsl_debug_console.h"
#include "fsl_soc_src.h"
#include "fsl_dmamux.h"
#include "fsl_edma.h"
#include "pin_mux.h"
#include "board.h"
#ifdef ENABLE_SYSTEM_VIEW
#include "SEGGER_SYSVIEW.h"
#include "SEGGER_debug_def.h"
#endif

#if BOARD_NETWORK_USE_100M_ENET_PORT
#include "fsl_phyksz8081.h"
#else
#include "fsl_phyrtl8211f.h"
#endif
#include "fsl_enet.h"

#include "fsl_gpio.h"
#include "fsl_enet.h"
#include "pin_mux.h"
#include "lwip/sockets.h"

#include "microej_main.h"
#include "sdcard_helper.h"
#include <assert.h>
#include "ksdk_mbedtls.h"
#ifndef __MCUXPRESSO
#include "tree_version.h"
#endif

#include "ecom_wifi_configuration.h"

/*******************************************************************************
 * Definitions
 ******************************************************************************/
#define MICROEJ_TASK_STACK_SIZE (10 * 1024)
#define microej_PRIORITY (configMAX_PRIORITIES - 6)
#define DEMO_EDMA          DMA0
#define DEMO_DMAMUX        DMAMUX0
/*******************************************************************************
 * Code
 ******************************************************************************/
static void microej_task(void *pvParameters);

/* ENET root clocks defines. */
#define ENET_CLOCK_ROOT_Mux  4
#define ENET1_CLOCK_ROOT_Divider  10
#define ENET2_CLOCK_ROOT_Divider  4

/* Initialize Enet Gpio Pins. */
#define ENET_RST_GPIO GPIO11
#define ENET_RST_GPIO_PIN 14

/* Initialize Enet timeouts. */
#define ENET_RST_ASSERT_TIME_US 10000
#define ENET_RST_SETTLE_TIME_US 30000

/* Global extern variables ---------------------------------------------------*/

TaskHandle_t pvMicrojvmCreatedTask = NULL;

void BOARD_InitModuleClock(void)
{
    const clock_sys_pll1_config_t sysPll1Config = {
        .pllDiv2En = true,
    };
    CLOCK_InitSysPll1(&sysPll1Config);

#if BOARD_NETWORK_USE_100M_ENET_PORT
    clock_root_config_t rootCfg = {.mux = ENET_CLOCK_ROOT_Mux, .div = ENET1_CLOCK_ROOT_Divider}; /* Generate 50M root clock. */
    CLOCK_SetRootClock(kCLOCK_Root_Enet1, &rootCfg);
#else
    clock_root_config_t rootCfg = {.mux = ENET_CLOCK_ROOT_Mux, .div = ENET2_CLOCK_ROOT_Divider}; /* Generate 125M root clock. */
    CLOCK_SetRootClock(kCLOCK_Root_Enet2, &rootCfg);
#endif
}

void IOMUXC_SelectENETClock(void)
{
#if BOARD_NETWORK_USE_100M_ENET_PORT
    IOMUXC_GPR->GPR4 |= IOMUXC_GPR_GPR4_ENET_REF_CLK_DIR_MASK; /* 50M ENET_REF_CLOCK output to PHY and ENET module. */
#else
    IOMUXC_GPR->GPR5 |= IOMUXC_GPR_GPR5_ENET1G_RGMII_EN_MASK; /* bit1:iomuxc_gpr_enet_clk_dir
                                                                 bit0:GPR_ENET_TX_CLK_SEL(internal or OSC) */
#endif
}

static void Initialize_ENET_Pins(void)
{
	gpio_pin_config_t gpio_config = {kGPIO_DigitalOutput, 0, kGPIO_NoIntmode};

	BOARD_InitEnet1GPins();
	GPIO_PinInit(ENET_RST_GPIO, ENET_RST_GPIO_PIN, &gpio_config);

	/* For a complete PHY reset of RTL8211FDI-CG, this pin must be asserted low for at least 10ms. And
	 * wait for a further 30ms(for internal circuits settling time) before accessing the PHY register */
	GPIO_WritePinOutput(ENET_RST_GPIO, ENET_RST_GPIO_PIN, 0);
	SDK_DelayAtLeastUs(ENET_RST_ASSERT_TIME_US, CLOCK_GetFreq(kCLOCK_CpuClk));
	GPIO_WritePinOutput(ENET_RST_GPIO, ENET_RST_GPIO_PIN, 1);
	SDK_DelayAtLeastUs(ENET_RST_SETTLE_TIME_US, CLOCK_GetFreq(kCLOCK_CpuClk));

	EnableIRQ(ENET_1G_MAC0_Tx_Rx_1_IRQn);
	EnableIRQ(ENET_1G_MAC0_Tx_Rx_2_IRQn);
}

static void BOARD_ResetDisplayMix(void)
{
    /*
     * Reset the displaymix, otherwise during debugging, the
     * debugger may not reset the display, then the behavior
     * is not right.
     */
    SRC_AssertSliceSoftwareReset(SRC, kSRC_DisplaySlice);
    while (kSRC_SliceResetInProcess == SRC_GetSliceResetState(SRC, kSRC_DisplaySlice))
    {
    }
}

static void dma_init(void) {
    DMAMUX_Init(DEMO_DMAMUX);
    edma_config_t dmaConfig;
    NVIC_SetPriority(DMA_ERROR_IRQn, 5);
    NVIC_SetPriority(DMA4_DMA20_IRQn, 4U);
    EDMA_GetDefaultConfig(&dmaConfig);
    dmaConfig.enableRoundRobinArbitration = true;
    EDMA_Init(DEMO_EDMA, &dmaConfig);
}

int main(void)
{
    /* Init board hardware. */
    BOARD_ConfigMPU();
    BOARD_BootClockRUN();
    BOARD_ResetDisplayMix();
    BOARD_InitLpuartPins();
    BOARD_InitMipiPanelPins();
    BOARD_MIPIPanelTouch_I2C_Init();
    BOARD_InitDebugConsole();
    BOARD_InitPinsSDIO();

    dma_init();

    /* Added in order to initialize Gpio Pins for ENET. */
    BOARD_InitPins();
    /* Initialize clock module. */
    BOARD_InitModuleClock();
    /* Select ENET clock. */
    IOMUXC_SelectENETClock();
    Initialize_ENET_Pins();
    /* used in order to initialize the true random number generator TRNG */
    /* The CRYPTO_InitHardware is responsible for initializing various hardware components related to cryptography according to options configured */
    status_t status = CRYPTO_InitHardware() ;
    assert( status == kStatus_Success );
    (void) status;

#ifdef ENABLE_SYSTEM_VIEW
    SEGGER_SYSVIEW_Conf();
#endif
/* Start the MicroJvm thread */

    /* ERR050396
     * Errata description:
     * AXI to AHB conversion for CM7 AHBS port (port to access CM7 to TCM) is by a NIC301 block, instead of XHB400
     * block. NIC301 doesn't support sparse write conversion. Any AXI to AHB conversion need XHB400, not by NIC. This
     * will result in data corruption in case of AXI sparse write reaches the NIC301 ahead of AHBS. Errata workaround:
     * For uSDHC, don't set the bit#1 of IOMUXC_GPR28 (AXI transaction is cacheable), if write data to TCM aligned in 4
     * bytes; No such write access limitation for OCRAM or external RAM
     */
    IOMUXC_GPR->GPR28 &= (~IOMUXC_GPR_GPR28_AWCACHE_USDHC_MASK);


#if defined(SD_ENABLED)
    START_SDCARD_Task(NULL);
#endif

    if (xTaskCreate(microej_task, "Microej task", MICROEJ_TASK_STACK_SIZE, NULL, microej_PRIORITY, &pvMicrojvmCreatedTask) !=
        pdPASS)
    {
        PRINTF("Task creation failed!.\r\n");
        while (1)
            ;
    }
#if ENABLE_SYSTEM_VIEW == 1
    // start_sysview_logging:
    PRINTF("SEGGER_RTT block address: %p\n", &(_SEGGER_RTT));
    SEGGER_SYSVIEW_setMicroJVMTask((U32)pvMicrojvmCreatedTask);
#endif // ENABLE_SYSTEM_VIEW
    vTaskStartScheduler();
    for (;;)
        ;
}


/*!
 * @brief Task responsible for printing of "Hello world." message.
 */
static void microej_task(void *pvParameters)
{
    PRINTF("microej_task\r\n");
    PRINTF("NXP VEE Port '%s' '%s'\r\n", VEE_VERSION, GIT_SHA_1);
    /* Start the CPU Load task */
    cpuload_init();
    microej_main(0, NULL);
    vTaskDelete(NULL);
}

void vApplicationMallocFailedHook()
{
    PRINTF(("\r\nERROR: Malloc failed to allocate memory\r\n"));

    /* Loop forever */
    for (;;)
        ;
}

/*!
 * @brief Call the cpuload_idle function in FreeRTOS ilde hook function
 */

void vApplicationIdleHook(void)
{
	cpuload_idle();
}
