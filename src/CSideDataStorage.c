
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "CSideDataStorage.h"

DynamicArray* newDynamicArray(int capacity)
{
	DynamicArray* list = (DynamicArray*)malloc(sizeof(DynamicArray));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (EnergyStats*)malloc(sizeof(EnergyStats)*capacity);
	return list;
}

void freeDynamicArray(DynamicArray* list)
{
	free(list->items);
	list->items = NULL;
	free(list);
	list = NULL;
}

LinkNode*
newLinkNode() {
	LinkNode* node = malloc(sizeof(LinkNode));
	node->next = NULL;
	return node;
}

LinkedList*
newLinkedList() {
	LinkNode* node = newLinkNode();
	LinkedList* list = malloc(sizeof(LinkedList));
	list->head = node;
	list->tail = node;
	list->nItemsAtTail = 0;
	list->nItems = 0;
	return list;
}

void
freeLinkedList(LinkedList* l) {
	if (l->head == NULL)
		return;
	LinkNode* current = l->head->next;
	LinkNode* prev = l->head;
	while(current != NULL) {
		free(prev);
		prev = current;
		current = current->next;
	}
	free(prev);
	//free(l->head);
	//free(l->tail);
	l->head = NULL;
	l->tail = NULL;
	free(l);
	l = NULL;
}

void
addItem_LinkedList(LinkedList* l, EnergyStats stats) { // add to tail

	if (l->nItemsAtTail == NODE_CAPACITY) {
		l->tail->next = newLinkNode();
		l->tail = l->tail->next;
		l->nItemsAtTail = 0;
	}
	l->tail->items[l->nItemsAtTail++] = stats;
	l->nItems++;
		
}

void addItem_DynamicArray(DynamicArray* a, EnergyStats stats) {

	if (a->nItems >= a->capacity)
	{
		a->capacity *= 2;
		a->items = realloc(a->items, a->capacity*sizeof(EnergyStats));
		assert(a->items != NULL);
	}
	a->items[a->nItems++] = stats;

}

void
writeToFile_DynamicArray(FILE* outfile, DynamicArray* a) {
	for (int i = 0; i < a->nItems; i++) {
		EnergyStats current = a->items[i];
		char csv_string[512];
		energy_stats_csv_string(current, csv_string);
		fprintf(outfile,"%s\n",csv_string);
	}
}

void
writeToFile_LinkedList(FILE* outfile, LinkedList* l) {
	LinkNode* current = l->head;
	while(current != NULL) {
		int upperbound = (current == l->tail) ?
			(l->nItemsAtTail) : (NODE_CAPACITY);
		for (int i = 0; i < upperbound; i++) {
			char ener_string[512];
			energy_stats_csv_string(current->items[i], ener_string);
			fprintf(outfile,"%s\n",ener_string);
		} //printf(" --\n"); // delimits between the contents of each node (keep for debugging purposes)
		current = current->next;
	}
}







