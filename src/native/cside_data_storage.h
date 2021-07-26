
#ifndef CSIDE_DATA_STORAGE
#define CSIDE_DATA_STORAGE

#include "energy_stats.h"

typedef struct LinkNode {
	EnergyStats *items;
	struct LinkNode* next;
} LinkNode;

typedef struct LinkedList {
	size_t node_capacity;
	LinkNode* head;
	LinkNode* tail;
	int nItemsAtTail;
	int nItems;
} LinkedList;

LinkedList* newLinkedList(size_t capacity);
void freeLinkedList(LinkedList* esll);
void addItem_LinkedList(LinkedList* l, EnergyStats e); // add to tail
void writeFileCSV_LinkedList(FILE* outfile, LinkedList* l);

typedef struct DynamicArray {
	EnergyStats* items;
	size_t capacity;
	size_t nItems;
} DynamicArray;

DynamicArray* newDynamicArray(size_t capacity);
void freeDynamicArray(DynamicArray* list);
void addItem_DynamicArray(DynamicArray* a, EnergyStats e);
void writeFileCSV_DynamicArray(FILE* outfile, DynamicArray* a);

#endif //CSIDE_DATA_STORAGE
