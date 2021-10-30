
#include <time.h>
#include <errno.h>
#include <sys/time.h>

unsigned long
usec_since_epoch() {
	struct timeval t; gettimeofday(&t,0);
	return t.tv_sec * 1000000UL + t.tv_usec;
}

int
sleep_millisecond(long msec) {
	struct timespec ts;
	int res;

	if (msec < 0) {
		errno = EINVAL;
		return -1;
	}

	ts.tv_sec = msec / 1000;
	ts.tv_nsec = (msec % 1000) * 1000000;

	do {
		res = nanosleep(&ts, &ts);
	} while (res && errno == EINTR);

	return res;
} /* https://stackoverflow.com/questions/1157209/is-there-an-alternative-sleep-function-in-c-to-milliseconds */
