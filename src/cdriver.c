#include <stdio.h>

#include "cdriver.h"

#define filler NULL, NULL

int main(int argc, char* argv[])
{
	char ener_info[512];
	Java_jrapl_JRAPL_ProfileInit(filler);
	Java_jrapl_EnergyCheckUtils_EnergyStatCheck(ener_info, filler);
	Java_jrapl_JRAPL_ProfileDealloc(filler);
	printf("%s\n", ener_info);
}
