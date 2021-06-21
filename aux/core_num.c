#include <stdio.h>
#include <unistd.h>

int
core_num() {
	return sysconf(_SC_NPROCESSORS_CONF); //passed in is number of configured processors
}

int main() {
	printf("cores: %d\n",core_num());
	return 0;
}
