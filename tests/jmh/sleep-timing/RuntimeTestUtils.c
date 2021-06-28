
#include <time.h>
#include <errno.h>

//timestamping macros
#define STARTSTAMP	gettimeofday(&start, NULL);
#define STOPSTAMP	gettimeofday(&end, NULL); timersub(&end, &start, &diff);
#define DIFF_USEC	diff.tv_sec*1000000 + diff.tv_usec

// global static timestamp variables make this not thread safe
static struct timeval start, end, diff;

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
}

JNIEXPORT jlong JNICALL
Java_jRAPL_Sleeping_cSleepTimed(JNIEnv* env, jclass jcls, jint time) {
	STARTSTAMP;
	sleep_millisecond((int)time);
	STOPSTAMP;
	return DIFF_USEC;
}


JNIEXPORT void JNICALL
Java_jRAPL_Sleeping_cSleep(JNIEnv* env, jclass jcls, jint time) {
	sleep_millisecond((int)time);
}
