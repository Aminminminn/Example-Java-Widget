/*
 * Copyright 2022 NXP
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/***********************************************************************************************************************
 * This file was generated by the MCUXpresso Config Tools. Any manual edits made to this file
 * will be overwritten if the respective MCUXpresso Config Tools is used to update this file.
 **********************************************************************************************************************/

#ifndef _PIN_MUX_H_
#define _PIN_MUX_H_

/*!
 * @addtogroup pin_mux
 * @{
 */

/***********************************************************************************************************************
 * API
 **********************************************************************************************************************/

#if defined(__cplusplus)
extern "C" {
#endif

#define BOARD_INITPINS_IOMUXC_GPR_GPR43_GPIO_MUX3_GPIO_SEL_HIGH_MASK 0x8000U /*!< GPIO3 and CM7_GPIO3 share same IO MUX function, GPIO_MUX3 selects one GPIO function: affected bits mask */
/*!
 * @brief Calls initialization functions.
 *
 */
void BOARD_InitBootPins(void);

/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitLpuartPins(void);              /* Function assigned for the Cortex-M7F */

/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitMipiPanelPins(void);           /* Function assigned for the Cortex-M7F */

/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitPins(void);                    /* Function assigned for the Cortex-M7F */

/* GPIO_AD_16 (coord N17), SDIO_RST */
/* Routed pin properties */
#define BOARD_INITPINSM2_SDIO_RST_PERIPHERAL                               GPIO9   /*!< Peripheral name */
#define BOARD_INITPINSM2_SDIO_RST_SIGNAL                                 gpio_io   /*!< Signal name */
#define BOARD_INITPINSM2_SDIO_RST_CHANNEL                                    15U   /*!< Signal channel */

/* Symbols to be used with GPIO driver */
#define BOARD_INITPINSM2_SDIO_RST_GPIO                                     GPIO9   /*!< GPIO peripheral base pointer */
#define BOARD_INITPINSM2_SDIO_RST_GPIO_PIN                                   15U   /*!< GPIO pin number */
#define BOARD_INITPINSM2_SDIO_RST_GPIO_PIN_MASK                      (1U << 15U)   /*!< GPIO pin mask */

/* GPIO_AD_31 (coord J17), WL_RST */
/* Routed pin properties */
#define BOARD_INITPINSM2_WL_RST_PERIPHERAL                                 GPIO9   /*!< Peripheral name */
#define BOARD_INITPINSM2_WL_RST_SIGNAL                                   gpio_io   /*!< Signal name */
#define BOARD_INITPINSM2_WL_RST_CHANNEL                                      30U   /*!< Signal channel */

/* Symbols to be used with GPIO driver */
#define BOARD_INITPINSM2_WL_RST_GPIO                                       GPIO9   /*!< GPIO peripheral base pointer */
#define BOARD_INITPINSM2_WL_RST_GPIO_PIN                                     30U   /*!< GPIO pin number */
#define BOARD_INITPINSM2_WL_RST_GPIO_PIN_MASK                        (1U << 30U)   /*!< GPIO pin mask */
/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitEnetPins(void);                /* Function assigned for the Cortex-M7F */

/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitEnet1GPins(void);              /* Function assigned for the Cortex-M7F */

/*!
 * @brief Configures pin routing and optionally pin electrical features.
 *
 */
void BOARD_InitPinsM2(void);                  /* Function assigned for the Cortex-M7F */

/**
 * @brief Initialize pins for the WIFI module.
*/
void BOARD_InitPinsSDIO(void);

#if defined(__cplusplus)
}
#endif

/*!
 * @}
 */
#endif /* _PIN_MUX_H_ */

/***********************************************************************************************************************
 * EOF
 **********************************************************************************************************************/
