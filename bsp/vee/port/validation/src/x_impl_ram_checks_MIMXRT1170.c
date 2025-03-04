/*
 * C
 *
 * Copyright 2014-2023 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
#include <stdlib.h>
#include <stdint.h>
#include <stdio.h>
#include <stdbool.h>
#include <fsl_common.h>
#include "x_ram_checks.h"
#include "bsp_util.h"
#include "dtcram_buffer.h"
#include "ocram_buffer.h"

#define ZONE_SDRAM  0
#define ZONE_OCRAM  1
#define ZONE_DTCRAM 2

#define ZONES_NUMBER (1)
#define ZONE_TESTED ZONE_SDRAM

static X_RAM_CHECKS_zone_t zones[ZONES_NUMBER];

#define SDRAM_BUFFER_SIZE (500*1024)
AT_NONCACHEABLE_SECTION(uint8_t sdram_buffer[SDRAM_BUFFER_SIZE]);

/* Lazy initialization flag */
static bool initialize;

/* private function */
static void initialize_ram_zones(void) {
	if(initialize == false)	{
		initialize = true;

		uint8_t* start_address_p;
		uint32_t zone_size;

		switch (ZONE_TESTED) {
		case ZONE_SDRAM:
			start_address_p = sdram_buffer;
			zone_size = SDRAM_BUFFER_SIZE;
			break;
		case ZONE_DTCRAM:
			start_address_p = dtcram_buffer;
			zone_size = DTCRAM_BUFFER_SIZE;
			break;
		case ZONE_OCRAM:
			start_address_p = ocram_buffer;
			zone_size = OCRAM_BUFFER_SIZE;
			break;
		default:
			start_address_p = NULL;
			zone_size = 0;
			break;
		}
		
		if (start_address_p != NULL) {
			zones[0].start_address = (uint32_t)(start_address_p);
			zones[0].end_address = (uint32_t)start_address_p + zone_size - 1;
		} else {
			printf("Failed to allocate RAM memory for tests!\n");
		}
	}
}

/**
 * 
 * @brief  this function provide a definition array of  memory zones to test with 32-bit accesses.
 *
 * @return array of X_RAM_CHECKS_zone_t
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get32bitZones(void)
{
	initialize_ram_zones();
	return &zones[0];
}


/**
 * @brief this function provide a definition array of  memory zones to test with 16-bit accesses.
 *
 * @return array of X_RAM_CHECKS_zone_t
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get16bitZones(void)
{
	initialize_ram_zones();
	return &zones[0];
}


/**
 * @brief 
 *
 * @return array of X_RAM_CHECKS_zone_t
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get8bitZones(void)
{
	initialize_ram_zones();
	return &zones[0];
}


/**
 * @brief 
 *
 * @return number of zones to test
 */

uint8_t X_RAM_CHECKS_get32bitZoneNumber(void)
{
	initialize_ram_zones();
	return ZONES_NUMBER;
}


/**
 * @brief 
 *
 * @return 
 */

uint8_t X_RAM_CHECKS_get16bitZoneNumber(void)
{
	initialize_ram_zones();
	return ZONES_NUMBER;
}


/**
 * @brief 
 *
 * @return 
 */

uint8_t X_RAM_CHECKS_get8bitZoneNumber(void)
{
	initialize_ram_zones();
	return ZONES_NUMBER;
}


/**
 * @brief 
 *
 * @return 
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get32bitSourceZone(void)
{
	initialize_ram_zones();
	return &zones[0];
}


/**
 * @brief 
 *
 * @return 
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get16bitSourceZone(void)
{
	initialize_ram_zones();
	return &zones[0];
}


/**
 * @brief 
 *
 * @return 
 */

X_RAM_CHECKS_zone_t* X_RAM_CHECKS_get8bitSourceZone(void)
{
	initialize_ram_zones();
	return &zones[0];
}
