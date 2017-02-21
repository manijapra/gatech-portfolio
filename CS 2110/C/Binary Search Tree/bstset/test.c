#include "bstset.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

/* Here we are going to write some functions to support a bst set that stores
 * person data (name, age)
 */
typedef struct person_t {
	char *name;
	int age;
} person;

/* Example functions given to you. If you want to truly grasp these concepts, try
 * writing your own structs and functions as well!
 */

// Create a new person
person *create_person(const char *name, int age) {
	person *p = (person*) malloc(sizeof(person));
	p->name = malloc(strlen(name) + 1);
	strcpy(p->name, name);
	p->age = age;
	return p;
}

// Print a person
void print_person(void *data) {
	person *p = (person*) data;
	printf("%s, %d\n", p->name, p->age);
}

// Free a person
void free_person(void *data) {
	// This is safe because we should only be passing in person struct pointers
	person *p = (person*) data;
	// free any malloc'd pointers contained in the person struct (just name)
	free(p->name);
	// Now free the struct itself; this takes care of non-malloc'd data, like age.
	free(p);
}

static int comparisons;

// Compare people by age
int person_age_comp(const void *data1, const void *data2) {
	person *p1 = (person*) data1;
	person *p2 = (person*) data2;
	++comparisons;
	return p1->age - p2->age;
}

// Compare people by name
int person_name_comp(const void *data1, const void *data2) {
	person *p1 = (person*) data1;
	person *p2 = (person*) data2;
	++comparisons;
	return strcmp(p1->name, p2->name);
}

/* This main function does a little testing
 * Like all good CS Majors you should test
 * your code here. There is no substitute for testing
 * and you should be sure to test for all edge cases
 * e.g., calling destroy on an empty bst.
 */
int main(void) {

	printf("\nTEST CASE 1\nAn empty bst should have size 0:\n");
	bstset *bst1 = create_bstset();
	printf("Size: %d\n", bst1->size);

	printf("\nTEST CASE 2\nAfter adding one element, size should be 1:\n");
	add(bst1, create_person("Andrew", 25), person_name_comp);
	printf("Size: %d\n", bst1->size);

	printf("\nTEST CASE 3\nTraversal should print the one element:\n");
	traverse(bst1, print_person);

	printf("\nTEST CASE 4\nThe one element should be the min:\n");
	print_person(min(bst1));

	printf("\nTEST CASE 5\nThe one element should be the max:\n");
	print_person(max(bst1));

	printf("\nTEST CASE 6\nAndrew should be contained in the bst:\n");
	person *andrew = create_person("Andrew", 25);
	printf("%s in bst\n", contains(bst1, andrew, person_name_comp) ? "is" : "isn\'t");

	printf("\nTEST CASE 7\nCollin should not be contained in the bst:\n");
	person *collin = create_person("Collin", 23);
	printf("%s in bst\n", contains(bst1, collin, person_name_comp) ? "is" : "isn\'t");

	// Free these people because we are done with them
	free_person(andrew);
	free_person(collin);

	printf("\nTEST CASE 8\nThere should be 7 elements in this bst:\n");
        /*      _N_
	 *     /   \
         *    H     B
	 *   / \   / \
	 *  Y   M A   L
	 */
	bstset *bst2 = create_bstset();
	int result[7];
	result[0] = add(bst2, create_person("Nick", 23), person_age_comp);
	result[1] = add(bst2, create_person("Henry", 21), person_age_comp);
	result[2] = add(bst2, create_person("Marie", 22), person_age_comp);
	result[3] = add(bst2, create_person("Yuanhan", 19), person_age_comp);
	result[4] = add(bst2, create_person("Brandon", 28), person_age_comp);
	result[5] = add(bst2, create_person("Andrew", 25), person_age_comp);
	result[6] = add(bst2, create_person("Leahy", 66), person_age_comp);
	printf("Size: %d\n", bst2->size);

	printf("\nTEST CASE 9\nadd should return 0 for all the added new elements:\n");
	printf("Assert will fail if add returned a nonzero value...\n");
	for(int i = 0; i < 7; ++i) {
		assert(!result[i]);
	}
	printf("Passed test!\n");

	printf("\nTEST CASE 10\nThe minimum element should be Yuanhan:\n");
	print_person(min(bst2));

	printf("\nTEST CASE 11\nThe maximum element should be Leahy:\n");
	print_person(max(bst2));

	printf("\nTEST CASE 12\nBrandon should be in the bst (2 comparisons):\n");
	person *brandon = create_person("Brandon", 28);
	comparisons = 0;
	printf("%s in bst\n", contains(bst2, brandon, person_age_comp) ? "is" : "isn\'t");
	printf("Comparisons: %d\n", comparisons);

	printf("\nTEST CASE 13\nBao should not be in the bst (3 comparisons):\n");
	person *bao = create_person("Bao", 20);
	comparisons = 0;
	printf("%s in bst\n", contains(bst2, bao, person_age_comp) ? "is" : "isn\'t");
	printf("Comparisons: %d\n", comparisons);

	printf("\nTEST CASE 14\nThe names should be printed in ascending age:\n");
	traverse(bst2, print_person);

	printf("\nTEST CASE 15\nAdding an existing element should return 1:\n");
	printf("Return value: %d\n", add(bst2, brandon, person_age_comp));

	printf("\nTEST CASE 16\nThe bst should still be size 7:\n");
	printf("Size: %d\n", bst2->size);

	// Done with these 2 people now
	free_person(brandon);
	free_person(bao);

	printf("\nTEST CASE 17\nThe same 7 people now sorted by name:\n");
        /*        N
	 *       / \
         *      H   Y
	 *     / \
	 *    B   M
	 *   /   /
	 *  A   L
	 */
	bstset *bst3 = create_bstset();
	add(bst3, create_person("Nick", 23), person_name_comp);
	add(bst3, create_person("Henry", 21), person_name_comp);
	add(bst3, create_person("Marie", 22), person_name_comp);
	add(bst3, create_person("Yuanhan", 19), person_name_comp);
	add(bst3, create_person("Brandon", 28), person_name_comp);
	add(bst3, create_person("Andrew", 25), person_name_comp);
	add(bst3, create_person("Leahy", 66), person_name_comp);
	traverse(bst3, print_person);
	printf("\n");

	// TODO
	printf("Make sure to write more test cases as well in test.c! Also test using valgrind.\n");

	// Clean up
	destroy(bst1, free_person);
	destroy(bst2, free_person);
	destroy(bst3, free_person);

	return 0;
}

