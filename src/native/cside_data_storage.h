
#ifndef CSIDE_DATA_STORAGE
#define CSIDE_DATA_STORAGE

#include "energy_check_utils.h"

typedef struct LinkNode {
	energy_measurement_t *items;
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
void addItem_LinkedList(LinkedList* l, energy_measurement_t e); // add to tail
void writeFileCSV_LinkedList(FILE* outfile, LinkedList* l);

typedef struct DynamicArray {
	energy_measurement_t* items;
	size_t capacity;
	size_t nItems;
} DynamicArray;

DynamicArray* newDynamicArray(size_t capacity);
void freeDynamicArray(DynamicArray* list);
void addItem_DynamicArray(DynamicArray* a, energy_measurement_t e);
void writeFileCSV_DynamicArray(FILE* outfile, DynamicArray* a);

#endif //CSIDE_DATA_STORAGE
