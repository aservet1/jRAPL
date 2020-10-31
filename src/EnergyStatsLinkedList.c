#include <stdio.h>
#include <stdlib.h>
#include "EnergyStats.h"
#include "EnergyStatsLinkedList.h"

EnergyStatsArrNode*
newEnergyStatsArrNode() {
	EnergyStatsArrNode* esan = malloc(sizeof(EnergyStatsArrNode));
	esan->next = NULL;
	esan->nElems = 0;
	return esan;
}

EnergyStatsLinkList*
newEnergyStatsLinkList() {
	EnergyStatsArrNode* esan = newEnergyStatsArrNode();
	EnergyStatsLinkList* esll = malloc(sizeof(EnergyStatsLinkList));
	//esll->nElems = 0;
	esll->head = esan;
	esll->tail = esan;
	return esll;
}

void
freeEnergyStatsLinkList(EnergyStatsLinkList* esll) {
	if (esll->head == NULL)
		return;
	EnergyStatsArrNode* current = esll->head->next;
	EnergyStatsArrNode* prev = esll->head;
	while(current != NULL) {
		free(prev);
		prev = current;
		current = current->next;
	}
}

void
addItem(EnergyStatsLinkList* l, EnergyStats e) { // add to tail

	if (l->tail->nElems == NODE_CAPACITY) {
		l->tail->next = newEnergyStatsArrNode();
		l->tail = l->tail->next;
	}
	EnergyStatsArrNode* tail = l->tail;
	tail->items[tail->nElems++] = e;
		
}

void
printEnergyStatsLinkList(EnergyStatsLinkList* l) {
	EnergyStatsArrNode* current = l->head;
	while(current != NULL) {
		for (int i = 0; i < current->nElems; i++) {
			char ener_string[512];
			energy_stats_to_string(current->items[i], ener_string);
			printf("%s\n",ener_string);
		} printf(" --\n");

		current = current->next;
	}
}











