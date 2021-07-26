/**
 * Reference Intel ® 64 and IA-32 Architectures Software Developer’s Manual
 * for those CPUID information (December 2016)
 *
 * Table: CPUID Signature Values of DisplayFamily_DisplayModel
 * Need to add cases for more recent architectures 7th gen onwards - kaby lake and coffee lake
 */

#define SANDYBRIDGE	0x2AU
#define SANDYBRIDGE_EP	0x2DU
#define IVYBRIDGE	0x3AU
#define SKYLAKE1	0x4EU
#define SKYLAKE2	0x5EU
#define HASWELL1	0x3CU
#define HASWELL2	0x45U
#define HASWELL3	0x46U
#define HASWELL_EP	0x3FU
#define BROADWELL	0xD4U
#define BROADWELL2	0x4FU

#define APOLLOLAKE	0x5CU // alejandro's computer
#define COFFEELAKE2	0x9eU // rutvik's first computer
#define KABYLAKE	0x8eU
//#define KABYLAKE_v2 0x9eU //KABYLAKE has 2 cpu model numbers to it (im unsure if this is true, since 0x9e is COFFEELAKE2...)
