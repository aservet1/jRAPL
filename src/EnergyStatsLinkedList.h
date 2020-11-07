
#ifndef ESTATSLINKLIST
#define ESTATSLINKLIST

#include "EnergyStats.h"

#define NODE_CAPACITY 100 //customize later, or maybe not
typedef struct EnergyStatsArrNode {
	EnergyStats items[NODE_CAPACITY];
	struct EnergyStatsArrNode* next;
	int nElems;
	//TODO: make a count at the end for 'the last node' instead of one guy per node, since we know that all previous nodes will be at capacity
} EnergyStatsArrNode;

typedef struct EnergyStatsLinkList {
	EnergyStatsArrNode* head;
	EnergyStatsArrNode* tail;
	//int nElems;
} EnergyStatsLinkList;

EnergyStatsLinkList* newEnergyStatsLinkList();
void freeEnergyStatsLinkList(EnergyStatsLinkList* esll);
void addItem(EnergyStatsLinkList* l, EnergyStats e); // add to tail
void printEnergyStatsLinkList(EnergyStatsLinkList* l);

#endif //ESTATSLINKLIST
