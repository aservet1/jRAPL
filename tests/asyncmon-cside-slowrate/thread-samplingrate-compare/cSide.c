#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>
#include <pthread.h>
#include <assert.h>
#include <errno.h>

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

bool exitflag = true;
int count = 0;
pthread_t tid;

void* run() {
	exitflag = false;
	while (!exitflag) {
		sleep_msec(0);
		count++;
	}
	printf("count: %d\n", count);
	return NULL;
}

JNIEXPORT void JNICALL
Java_threadtimer_CSide_start(JNIEnv *env, jclass cls) {
	pthread_create(&tid, NULL, run, NULL);
}

JNIEXPORT void JNICALL
Java_threadtimer_CSide_stop(JNIEnv *env, jclass cls) {
	exitflag = true;
	pthread_join(tid,NULL);
}
