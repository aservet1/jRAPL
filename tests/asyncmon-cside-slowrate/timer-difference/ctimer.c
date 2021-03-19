#include<stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<time.h>
#include<sys/time.h>
#include<sys/types.h>

// #include<jni.h>
//
// JNIEXPORT jlong JNICALL
// Java_timertest_TimerTest_usecsCtimer(JNIEnv *env, jclass jcls, jint msec) {
//	return (jlong)usecs(msec);
// }

int sleep_msec(long msec) {
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

long usecs(int msec) {
	struct timeval bef, aft, diff;
	gettimeofday(&bef,NULL);
	sleep_msec(msec);
	gettimeofday(&aft,NULL);
	timersub(&aft, &bef, &diff);
	long usec = (diff.tv_sec*1000000) + diff.tv_usec;
	return usec;
}

void go(int s, int trials) {
	long buf[trials];

	int warmups = 100;
    for (int w = 0; w < warmups; w++) {
		buf[w%trials] = usecs(s);
	}

	for (int i = 0; i < trials; i++) {
		buf[i] = usecs(s);
	}

	printf("["); for (int i = 0; i < trials-1; i++) {
		printf("%ld,",buf[i]);
	} printf("%ld]",buf[trials-1]);
}

int main(int argc, char *argv[])
{
	if (argc != 3) {
		printf("usage: %s <msecs> <number of trials>\n",argv[0]);
		exit(1017);
	}
	int s = atoi(argv[1]);
	int trials = atoi(argv[2]);

	go(s,trials);
	
	return 0;
}
