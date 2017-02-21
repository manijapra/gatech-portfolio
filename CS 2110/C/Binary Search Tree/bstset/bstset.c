/**
 * CS 2110 - Fall 2015 - HW11
 * Andrew Wilder
 *
 * Name: ***** Mani Japra (902958199) !! *****
 * bstset.c: Complete the functions!
 */

#include <stdlib.h>
#include <stdio.h>
#include "bstset.h"

/* The bnode struct. This struct is not visible to the user of the bstset.
 * Do not modify this in any way or else you will get a zero. You have been
 * warned! As a design paradigm, only this file should know about this struct.
 * Do not add this struct definition to other files.
 */
typedef struct bnode_t {
	struct bnode_t *left;
	struct bnode_t *right;
	void *data;
} node;

/* There should be absolutely no global data here or else you are doing
 * something wrong. This library should work for multiple bst sets!
 *
 * However, you absolutely may add helper functions here if you would like.
 * Several of these functions have simple, elegant recursive solutions.
 */
 
 int containsHelper(node *n, void *data, bst_comp comp_func)
 {
 	int comp = comp_func(n->data, data);
 	if (n == NULL)
 	{
 		return 0;
 	}
 	else if (comp == 0)
 	{
 		return 1;
 	}
 	else if (comp < 0)
 	{
 		if (n->right != NULL) return containsHelper(n->right, data, comp_func);
 	}
 	else if (comp > 0)
 	{
 		if (n->left != NULL) return containsHelper(n->left, data, comp_func);
 	}
 	return 0;
 }
 
 void inOrder(node *n, bst_op do_func)
 {
 	if (n == NULL)
 	{
 	
 	}
 	else
 	{
 		inOrder(n->left, do_func);
 		do_func(n->data);
 		inOrder(n->right, do_func);
 		
 	}
 }
 
  void deleteNode(node *n, bst_op free_func)
 {
 	if (n == NULL)
 	{
 	
 	}
 	else
 	{
 		deleteNode(n->left, free_func);
 		deleteNode(n->right, free_func);
 		free_func(n->data);
 		free(n);
 		
 	}
 }
/**
 * create_node
 *
 * Helper function that creates a node by allocating memory for it on the heap,
 * and initializing any pointers in it to NULL.
 *
 * @param data A void pointer to data the user is adding to the bst set.
 * @return The node
 */
static node *create_node(void *data) {
	node *temp =  malloc(sizeof(node));
	if (temp == NULL) return NULL;
	temp->left = NULL;
	temp->right = NULL;
	temp->data = data;
	return temp;
}

/**
 * create_bstset
 *
 * Creates a bst set by allocating memory for it on the heap. Be sure to
 * initialize the size to zero, as well as root to NULL.
 *
 * @return An empty bst set
 */
bstset *create_bstset(void) {
	bstset *bst = malloc(sizeof(bstset));
	bst->root = NULL;
	bst->size = 0;
	return bst;
}

/**
 * add
 *
 * Adds the data to the bst set in the appropriate sorted location.
 * Use comp_func on data, as well as the data contained in a node to determine
 * what to do. The specification for comp_func is as follows:
 *   comp_func(data1, data2)
 *     data1 > data2: returns a positive number
 *     data1 < data2: returns a negative number
 *     data1 = data2: returns zero
 * If the data we are trying to add is already in the bst set, do not modify
 * the bst set.
 *
 * @param bst The bst set we are adding to.
 * @param data The data we are adding to the bst set.
 * @param comp_func The comparison function for two pieces of data.
 * @return 0 if the data was added, 1 if the data was already in the bst set.
 */
int add(bstset *bst, void *data, bst_comp comp_func) {
	node *root = bst->root;
	if (root == NULL) {
		bst->root = create_node(data);
		bst->size = bst->size + 1;
		return 0;
	}
	else
	{
		while(root != NULL)
		{
			int comp = comp_func(root->data, data);
			if(comp > 0)
			{
				if(root->left == NULL)
				{
					node *temp = create_node(data);
					root->left = temp;
					root = NULL;
				}
				else
				{
					root = root->left;
				}
			}
			else if(comp < 0)
			{
				if(root->right == NULL)
				{
					node *temp = create_node(data);
					root->right = temp;
					root = NULL;
				}
				else
				{
					root = root->right;
				}
			}
			else
			{
				return 1;
			}
		}
		bst->size = bst->size + 1;
		return 0;
	}
	return -1;
}

/**
 * min
 *
 * Gets the minimum element of the bst set.
 * Recall that bst structures are such that the value to the left of the
 * current node is less than the current node. This means that the minimum
 * value should be contained all the way down the left branch of the bst set.
 *
 * @param bst The bst set.
 * @return The minimum element, or NULL if nothing in the bst set
 */
void *min(bstset *bst) {
	node *root = bst->root;
	node *min = NULL;
	if (root == NULL) {
		return NULL;
	}
	else
	{
		min = root;
		while(min->left != NULL)
		{
			min = min->left;
		}
		return min->data;
	}
	return NULL;
}

/**
 * max
 *
 * Gets the maximum element of the bst set.
 * Recall that bst structures are such that the value to the right of the
 * current node is greater than the current node. This means that the maximum
 * value should be contained all the way down the right branch of the bst set.
 *
 * @param bst The bst set.
 * @return The maximum element, or NULL if nothing in the bst set
 */
void *max(bstset *bst) {
	node *root = bst->root;
	node *max = NULL;
	if (root == NULL) {
		return NULL;
	}
	else
	{
		max = root;
		while(max->right != NULL)
		{
			max = max->right;
		}
		return max->data;
	}
	return NULL;
}

/**
 * contains
 *
 * Tells if the given data is contained in the bst set.
 * Use comp_func to determine if the given data is equal to something in the
 * bst set. You should not traverse the entire bst to find the data, because it
 * is a sorted structure! Any node should only be visited once in trying to
 * find the data.
 *
 * @param bst The bst set.
 * @param comp_func The comparison function for two pieces of data.
 * @return 1 if the data was in the bst set, 0 otherwise.
 */
int contains(bstset *bst, void *data, bst_comp comp_func) {
	node *root = bst->root;
	if (root == NULL) {
		return 0;
	}
	else
	{
		return containsHelper(root, data, comp_func);
	}
}

/**
 * traverse
 *
 * Do something to each piece of data in the bst.
 * The traversal method you should use is "in-order" traversal:
 *   Recurse left (if there is a left)
 *   Do something to current node's data
 *   Recurse right (if there is a right)
 *
 * The effect of processing the data is in this order:
 *       _4_
 *      /   \
 *     2     6
 *    / \   / \
 *   1   3 5   7
 *
 * @param bst The bst set.
 * @param do_func The function to perform on each piece of data.
 */
void traverse(bstset *bst, bst_op do_func) {
	node *root = bst->root;
	if (root != NULL)
	{
		inOrder(root, do_func);
	}
}

/**
 * destroy
 *
 * Destroy the bst set. Everything in the bst set including bstset structure,
 * nodes and data should all be freed from the heap.
 * Since the only one who knows how to free the data is the user, you must use
 * free_func (which is written by the user) to free the data, instead of
 * regular free. However, regular free will suffice for the bst structure and
 * nodes since they were created with malloc.
 *
 * @param bst The bst set.
 * @param free_func The function that the user created to free the data.
 */
void destroy(bstset *bst, bst_op free_func) {
	node *root = bst->root;
	deleteNode(root, free_func);
	free(bst);
}

