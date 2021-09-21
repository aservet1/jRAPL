
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "arch_spec.h"
#include "cside_data_storage.h"

DynamicArray* newDynamicArray(size_t capacity) {
	DynamicArray* list = (DynamicArray*)malloc(sizeof(DynamicArray));
	list->capacity = capacity;
	list->nItems = 0;
	list->items = (energy_measurement_t*)malloc(sizeof(energy_measurement_t)*capacity);
	return list;
}

void freeDynamicArray(DynamicArray* list) {
	free(list->items);
	list->items = NULL;
	free(list);
	list = NULL;
}

LinkNode*
newLinkNode(size_t capacity) {
	LinkNode* node = malloc(sizeof(LinkNode));
	node->next = NULL;
	node->items = (energy_measurement_t*)malloc(sizeof(energy_measurement_t)*capacity);
	return node;
}

LinkedList*
newLinkedList(size_t node_capacity) {
	LinkNode* node = newLinkNode(node_capacity);
	LinkedList* list = malloc(sizeof(LinkedList));
	list->node_capacity = node_capacity;
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
		free(prev->items);
		free(prev);
		prev = current;
		current = current->next;
	}
	free(prev->items);
	free(prev); //free(l->head); //free(l->tail);
	l->head = NULL;
	l->tail = NULL;
	free(l);
	l = NULL;
}

void
addItem_LinkedList(LinkedList* l, energy_measurement_t stats) { // add to tail
	if (l->nItemsAtTail == l->node_capacity) {
		l->tail->next = newLinkNode(l->node_capacity);
		l->tail = l->tail->next;
		l->nItemsAtTail = 0;
	}
	l->tail->items[l->nItemsAtTail++] = stats;
	l->nItems++;
}

void addItem_DynamicArray(DynamicArray* a, energy_measurement_t stats) {
	if (a->nItems >= a->capacity) {
		a->capacity *= 2;
		a->items = realloc(a->items, a->capacity*sizeof(energy_measurement_t));
		assert(a->items != NULL);
	}
	a->items[a->nItems++] = stats;
}

void
writeFileCSV_DynamicArray(FILE* outfile, DynamicArray* a) {
	int num_sockets = getSocketNum();
	energy_measurement_t energy_measurement_per_socket[num_sockets];
	char csv_line_buffer[512];
	for (int i = 0; i < a->nItems; i+= num_sockets) {
		for (int j = 0; j < num_sockets; j++) { energy_measurement_per_socket[j] = a->items[i+j]; }
		energy_measurement_csv_string(energy_measurement_per_socket, csv_line_buffer);
		fprintf(outfile,"%s\n",csv_line_buffer);
	}
}

void
writeFileCSV_LinkedList(FILE* outfile, LinkedList* l) {
	int local_index = 0;
	char csv_line_buffer[512];
	int num_sockets = getSocketNum();
	energy_measurement_t energy_measurement_per_socket[num_sockets];

	LinkNode* current = l->head;
	for (int global_index = 0; global_index < l->nItems; global_index+=num_sockets) {
		local_index = global_index % l->node_capacity;

		if (local_index == 0 && global_index != 0) {
			current = current->next; // fprintf(outfile," --\n"); // delimits between the contents of each node (useful to uncomment for debugging)
		}

		for (int j = 0; j < num_sockets; j++) { energy_measurement_per_socket[j] = current->items[local_index+j]; }

		energy_measurement_csv_string(energy_measurement_per_socket, csv_line_buffer);
		fprintf(outfile,"%s\n", csv_line_buffer);
	}
}

// 	LinkNode* current = l->head;
// 	while(current != NULL) {
// 		int upperbound = (current == l->tail) ?
// 			(l->nItemsAtTail) : (NODE_CAPACITY);
// 		for (int i = 0; i < upperbound; i++) {
// 			char ener_string[512];
// 			energy_stats_csv_string(current->items[i], ener_string);
// 			fprintf(outfile,"%s\n",ener_string);
// 		} //printf(" --\n"); // delimits between the contents of each node (this is useful to uncomment for if you're printing out all of the contents, to see the samples grouped by node.)
// 		current = current->next;
// 	}

