#include "my_malloc.h"

/* You *MUST* use this macro when calling my_sbrk to allocate the
 * appropriate size. Failure to do so may result in an incorrect
 * grading!
 */
#define SBRK_SIZE 2048

/* If you want to use debugging printouts, it is HIGHLY recommended
 * to use this macro or something similar. If you produce output from
 * your code then you may receive a 20 point deduction. You have been
 * warned.
 */
#ifdef DEBUG
#define DEBUG_PRINT(x) printf x
#else
#define DEBUG_PRINT(x)
#endif

/* our freelist structure - this is where the current freelist of
 * blocks will be maintained. failure to maintain the list inside
 * of this structure will result in no credit, as the grader will
 * expect it to be maintained here.
 * Technically this should be declared static for the same reasons
 * as above, but DO NOT CHANGE the way this structure is declared
 * or it will break the autograder.
 */
metadata_t* freelist;

/* OFFSET */
char OFFSET = sizeof(metadata_t);

/* Prototypes */
metadata_t* getBlock(size_t sizeNeeded, int sortType);
metadata_t* incMemory(metadata_t* lastLoc);
void* cleanUp(metadata_t* blockLoc, size_t sizeNeeded, int sortType);
metadata_t* addBlock_Size(metadata_t* p);
metadata_t* addBlock_Addr(metadata_t* p);
/* End Prototypes */


void* my_malloc_size_order(size_t size)
{
  size_t totalSize = OFFSET + size;
  
  /* Check Min & Max size */
  if (totalSize > SBRK_SIZE)
  {
  	ERRNO = SINGLE_REQUEST_TOO_LARGE;
  	return NULL;
  }
  /* end of check min & max size */
  
  /* Remove Block of Memory from FreeList and Return it */
  if (freelist == NULL) 
  {
  	/* Create memory for empty freelist */
  	ERRNO = NO_ERROR;
	metadata_t* temp = (metadata_t*) my_sbrk(SBRK_SIZE);
 	if (temp == NULL)
  	{
  		ERRNO = OUT_OF_MEMORY;
  		return NULL;
  	}
  	temp->size = SBRK_SIZE;
  	temp->in_use = 0;
  	freelist = temp;
  	/* Get the correct block size needed */
  	metadata_t* block = getBlock(totalSize, 0);
  	block->next = NULL;
  	block->prev = NULL;
  	block->in_use = 1;
  	return (metadata_t*)((char*)block + OFFSET);
  }
  else
  {
  	ERRNO = NO_ERROR;
  	/* Get the correct block size needed */
  	metadata_t* block = getBlock(totalSize, 0);
  	if (block == NULL)
  	{
  		ERRNO = OUT_OF_MEMORY;
  		return NULL;
  	}
  	block->next = NULL;
  	block->prev = NULL;
  	block->in_use = 1;
  	return (metadata_t*)((char*)block + OFFSET);
  }
  /* End of Returning Block of Memory from FreeList */
}

void* my_malloc_addr_order(size_t size)
{
  size_t totalSize = OFFSET + size;
  
  /* Check Min & Max size */
  if (totalSize > SBRK_SIZE)
  {
  	ERRNO = SINGLE_REQUEST_TOO_LARGE;
  	return NULL;
  }
  /* end of check min & max size */
  
  /* Remove Block of Memory from FreeList and Return it */
  if (freelist == NULL) 
  {
  	/* Create memory for empty freelist */
  	ERRNO = NO_ERROR;
	metadata_t* temp = (metadata_t*) my_sbrk(SBRK_SIZE);
 	if (temp == NULL)
  	{
  		ERRNO = OUT_OF_MEMORY;
  		return NULL;
  	}
  	temp->size = SBRK_SIZE;
  	temp->in_use = 0;
  	freelist = temp;
  	/* Get the correct block size needed */
  	metadata_t* block = getBlock(totalSize, 1);
  	block->next = NULL;
  	block->prev = NULL;
  	block->in_use = 1;
  	return (metadata_t*)((char*)block + OFFSET);
  }
  else
  {
  	ERRNO = NO_ERROR;
  	/* Get the correct block size needed */
  	metadata_t* block = getBlock(totalSize, 1);
  	if (block == NULL)
  	{
  		ERRNO = OUT_OF_MEMORY;
  		return NULL;
  	}
  	block->next = NULL;
  	block->prev = NULL;
  	block->in_use = 1;
  	return (metadata_t*)((char*)block + OFFSET);//((metadata_t*)block + OFFSET);
  }
  /* End of Returning Block of Memory from FreeList */
}

/* Used to return the correct block the user needs - sortType (0 = Size, 1 = Address) */
metadata_t* getBlock(size_t sizeNeeded, int sortType)
{
  metadata_t *curLoc = freelist;
  
  while (curLoc != NULL)
  {
  	if (curLoc->size < sizeNeeded)
  	{
  		if(curLoc->next == NULL)
  		{
  			/* Freelist doesn't have enough memory */
  			curLoc = incMemory(curLoc);
  		}
  		else
  		{
  			/* else move to next block to see if it meets the sizeNeeded req */
  			curLoc = curLoc->next;
  		}
  	} 
  	else if (curLoc->size >= sizeNeeded)
  	{
  		if (curLoc->size > sizeNeeded)
  		{
  			/* Adjust block size to provide smallest block needed to the user */
  			curLoc = cleanUp(curLoc, sizeNeeded, sortType);
  		}
  		
  		if ( (curLoc->prev == NULL) && (curLoc->next == NULL) )
  		{
  			/* curLoc only block in memory */
  			freelist = NULL;
  			curLoc->in_use = 1;
  			return curLoc;
  		}
  		else if (curLoc->prev == NULL && curLoc->next != NULL)
  		{
  			/* curLoc at the start of freelist */
  			freelist = curLoc->next;
  			(curLoc->next)->prev = NULL;
  			curLoc->in_use = 1;
  			return curLoc;
  		}
  		else if (curLoc->next == NULL && curLoc->prev != NULL)
  		{
  			/* curLoc at the end of the freelist */
  			(curLoc->prev)->next = NULL;
  			curLoc->in_use = 1;
  			return curLoc;
  		}
  		else
  		{
  			/* curLoc in the middle */
  			(curLoc->prev)->next = curLoc->next;
  			(curLoc->next)->prev = curLoc->prev;
  			curLoc->in_use = 1;
  			return curLoc;
  		}
  	}
  }
  return NULL;
}

/* Adds memory to the last block of freelist */
metadata_t* incMemory(metadata_t* lastLoc)
{
  metadata_t* additionalMemory = (metadata_t*)my_sbrk(SBRK_SIZE);
  
  if (additionalMemory == NULL)
  {
  	ERRNO = OUT_OF_MEMORY;
  	return NULL;
  }
  
  additionalMemory->in_use = 0;
  additionalMemory->size = SBRK_SIZE;
  additionalMemory->prev = lastLoc;
  additionalMemory->next = NULL;
  lastLoc->next = additionalMemory;
  return additionalMemory;
}

/* Adjust block to 'split' it into two blocks (newBlock = Leftover memory, blockLoc = sizeNeeded) */
void* cleanUp(metadata_t* blockLoc, size_t sizeNeeded, int sortType)
{
  /* 2 Block Creation Check */
  if (blockLoc->size < (sizeNeeded + OFFSET + 1))
  {
  	return blockLoc;
  }
  /* End of 2 Block Creation Check */
  else
  {
  	/* readjust newBlock and blockLoc to let blockLoc = sizeNeeded, and newBlock = leftover size */
  	metadata_t* newBlock = (metadata_t*) (blockLoc + sizeNeeded);
  	newBlock->in_use = 0;
  	newBlock->size = (blockLoc->size) - sizeNeeded;
  	blockLoc->size = sizeNeeded;
  	
  	/* add newBlock to freelist in the correct loc based on sort */
  	if (sortType == 1)
  	{
  		addBlock_Addr(newBlock);
  	}
  	else
  	{
  		addBlock_Size(newBlock);
  	}
  	return blockLoc;
  }
}

/* adds block to freelist based on size of block */
metadata_t* addBlock_Size(metadata_t* p)
{
  metadata_t* node = p;
  
  /* freelist is empty */
  if (freelist == NULL)
  {
  	freelist = node;
  	return node;
  }
  else
  {
  	metadata_t* curLoc = freelist;
  	while (curLoc != NULL)
  	{
  		/* traverse through freelist till the correct loc to place block is found */
  		if (curLoc->size < node->size)
  		{
  			if (curLoc->next != NULL)
  			{
  				curLoc = curLoc->next;
  			}
  			else
  			{
  				/* block to be added is at the end of the freelist */
		  		curLoc->next = node;
		  		node->prev = curLoc;
		  		node->next = NULL;
		  		node->in_use = 0;
		  		return node;
		  	}
  		}
  		else if (node->size <= curLoc->size)
  		{
	  		if(curLoc->prev == NULL)
	  		{
	  			/* block to be added is at the beginning of the freelist */
	  			curLoc->prev = node;
	  			node->next = curLoc;
	  			node->prev = NULL;
	  			freelist = node;
	  			node->in_use = 0;
	  			return node;
	  		}
	  		else
	  		{
	  			/* block to be added is in the middle of the freelist */
	  			metadata_t* prevNode = curLoc->prev;
	  			node->prev = prevNode;
	  			node->next = curLoc;
	  			curLoc->prev = node;
	  			prevNode->next = node;
	  			node->in_use = 0;
	  			return node;
	  		}
  		}
  	}
  }
  return NULL;
}

/* adds block to freelist based on address of block */
metadata_t* addBlock_Addr(metadata_t* p)
{
	metadata_t* node = p;
	
	if (freelist == NULL) 
	{
		freelist = node;
		return node;
	} 
	else 
	{
		metadata_t* curLoc = freelist;
		
		/* traverse through freelist till the correct loc to place block is found */
		while (curLoc != NULL) 
		{
			if (curLoc < node) 
			{
				if (curLoc->next != NULL) {
					curLoc = curLoc->next;
				}
				else 
				{
					/* block to be added is at the end of the freelist */
					curLoc->next = node;
					node->prev = curLoc;
					node->next = NULL;
					node->in_use = 0;
					return node;
				}
			} 
			else if (curLoc > node) 
			{
				if (curLoc->prev == NULL) 
				{
					/* block to be added is at the first loc of the freelist */
					curLoc->prev = node;
					node->next = curLoc;
					node->prev = NULL;
					freelist = node;
					node->in_use = 0;
					return node;
				} 
				else 
				{
					/* block to be added is in the middle of the freelist */
					metadata_t* prevNode = curLoc->prev;
					prevNode->next = node;
					node->prev = prevNode;
					node->next = curLoc;
					curLoc->prev = node;
					node->in_use = 0;
					return node;
				}
			}
		}
	}
  return NULL;
}

void my_free_size_order(void* ptr)
{
  /* Adjust ptr to reference block including metadata */
  metadata_t* block = (metadata_t*)((char*)ptr - OFFSET);
  /* Add block to freelist */
  block = addBlock_Size(block);
  
  /* Check for adjacent free blocks */
  metadata_t* node = block;
  metadata_t* curLoc = freelist;

  /* traverse the freelist checking for adjacent free blocks */
  while (curLoc != NULL)
  {
  	/* Free Block is to the right of cur node */
  	if ((curLoc->in_use == 0) && (curLoc == (metadata_t*)(node + node->size)))  
	{
		node->size = (node->size + curLoc->size);
		if ((curLoc->next != NULL) && (curLoc->prev != NULL)) 
		{
			curLoc->next->prev = curLoc->prev;
			curLoc->prev->next = curLoc->next;
		} 
		else if ((curLoc->next == NULL) && (curLoc-> prev != NULL)) 
		{
			curLoc->prev->next = NULL;
		} 
		else if ((curLoc->next != NULL) && (curLoc->prev == NULL)) 
		{
			curLoc->next->prev = NULL;
			freelist = curLoc->next;
		} 
	}
	/* Free Block is to the left of cur node */
	else if ((curLoc->in_use == 0) && ((metadata_t*)((char*)curLoc + curLoc->size) == node))
	{
		node->size = (curLoc->size + node->size);
		if ((node->next != NULL) && (node->prev != NULL)) 
		{
			node->next->prev = node->prev;
			node->prev->next = node->next;
		} 
		else if ((node->next == NULL) && (node-> prev != NULL)) 
		{
			node->prev->next = NULL;
		} 
		else if ((node->next != NULL) && (node->prev == NULL)) 
		{
			node->next->prev = NULL;
			freelist = node->next;
		} 
	}
	curLoc = curLoc->next;
  }	
}

void my_free_addr_order(void* ptr)
{
  /* Adjust ptr to reference block including metadata */
  metadata_t* block = (metadata_t*)((char*)ptr - OFFSET);
  /* Add block to freelist */
  block = addBlock_Addr(block);
  
  /* Check for adjacent free blocks */
  metadata_t* node = block;
  metadata_t* curLoc = freelist;

  /* traverse the freelist checking for adjacent free blocks */
  while (curLoc != NULL)
  {
  	/* Free Block is to the right of cur node */
  	if ((curLoc->in_use == 0) && (curLoc == (metadata_t*)(node + node->size)))  
	{
		node->size = (node->size + curLoc->size);
		if ((curLoc->next != NULL) && (curLoc->prev != NULL)) 
		{
			curLoc->next->prev = curLoc->prev;
			curLoc->prev->next = curLoc->next;
		} 
		else if ((curLoc->next == NULL) && (curLoc-> prev != NULL)) 
		{
			curLoc->prev->next = NULL;
		} 
		else if ((curLoc->next != NULL) && (curLoc->prev == NULL)) 
		{
			curLoc->next->prev = NULL;
			freelist = curLoc->next;
		} 
	}
	/* Free Block is to the left of cur node */
	else if ((curLoc->in_use == 0) && ((metadata_t*)((char*)curLoc + curLoc->size) == node))
	{
		node->size = (curLoc->size + node->size);
		if ((node->next != NULL) && (node->prev != NULL)) 
		{
			node->next->prev = node->prev;
			node->prev->next = node->next;
		} 
		else if ((node->next == NULL) && (node-> prev != NULL)) 
		{
			node->prev->next = NULL;
		} 
		else if ((node->next != NULL) && (node->prev == NULL)) 
		{
			node->next->prev = NULL;
			freelist = node->next;
		}
	}
	curLoc = curLoc->next;
  }	
}
