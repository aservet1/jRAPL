#include<stdio.h>
#include <time.h>
#include "CPUScalerShared.h"
#include "EnergyReadingCollector.h"

int msleep(long msec){
    struct timespec ts;
    int res;

    if (msec < 0)
    {
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

void start_collecting(readingCollector *collector){
    char ener_info[512];
    float dram, package, core;
    reading *currentReading = NULL;
    while(1){
        EnergyStatCheck_C(ener_info);
        sscanf(ener_info, "%f#%f#%f", &dram, &package, &core);
        if(currentReading){
            currentReading = currentReading->nextReading = malloc(sizeof(reading));
        } else{
            currentReading = collector->readings = malloc(sizeof(reading));
        }
        currentReading->dram = dram;
        currentReading->core = core;
        currentReading->package = package;
        currentReading->next = NULL;
        msleep(collector->delay);
    }
}

void stop_collecting(readingCollector *collector){
    pthread_cancel(*(collector->thread));
    reading *currentReading = collector->readings;
    printf("DRAM   CORE   PACKAGE\n");
    while(currentReading){
        printf("%f  %f  %f\n", currentReading->dram, currentReading->package, currentReading->core);
        currentReading = currentReading->nextReading;
    }
}