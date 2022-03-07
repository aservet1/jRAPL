#ifndef PLATFORM_SUPPORT_H
#define PLATFORM_SUPPORT_H

#include<stdint.h>
#include<stdbool.h>

typedef struct power_domain_support_info_t {
    uint32_t cpuid; const char* name; bool dram; bool pp0; bool pp1; bool pkg;
} power_domain_support_info_t;

extern const power_domain_support_info_t PLATFORM_SUPPORT_TABLE[];
extern const power_domain_support_info_t PLATFORM_NOT_SUPPORTED;
extern const uint32_t KNOWN_PLATFORM_ID_SET[];
extern const int NUM_PLATFORMS_SUPPORTED ;

#endif // PLATFORM_SUPPORT_H
