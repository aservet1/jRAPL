#include <stdio.h>
#include <unistd.h>

int
core_num() {
	return sysconf(_SC_NPROCESSORS_CONF);
}

int main() {
	printf("cores: %d\n",core_num());
	return 0;
}
