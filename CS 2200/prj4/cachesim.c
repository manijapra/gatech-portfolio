/* CS 2200 - Project 4 - Spring 2016
 * Name - Mani Japra
 * GTID - 902958199
 */

#include "cachesim.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

uint64_t c, s, b;

typedef struct {
    int dirty;
    int tag;
    int valid;
    int LFUcount;
} block;

typedef struct {
    block* blocks;
} set;

set* cache;

uint64_t extract_index(uint64_t address) {
    address >>= b;
    uint64_t indexMask = ~(-1 << s); 
    return address & indexMask;
}


uint64_t extract_tag(uint64_t address) {
    address >>= (b+c);
    int tagMask =  ~(-1 << (64 - (b + c)));
    return address & tagMask;
}

/**
 * Sub-routine for initializing your cache with the parameters.
 * You may initialize any global variables here.
 *
 * @param C The total size of your cache is 2^C bytes
 * @param S The set associativity is 2^S
 * @param B The size of your block is 2^B bytes
 */
void cache_init(uint64_t C, uint64_t S, uint64_t B) {
    
    c = C;
    s = S;
    b = B;
    
    int numBlocksPerSet = 1 << (c - b - s);
    int numSets = 1 << s;
    
    cache = (set*)(malloc(numSets* sizeof(set)));
    
    if (NULL == cache) 
    {
        //Malloc Failed
    }
    else 
    {
		  int i;
        for (i=0; i < numSets; i++) 
        {
            cache[i].blocks = (block*)(malloc(numBlocksPerSet* sizeof(block)));
            
            if (NULL == cache[i].blocks) 
            {
                //Malloc Failed
            }
            else 
            {
					 int j;
                for (j= 0; j < numBlocksPerSet; j++)
                {
		    			  cache[i].blocks[j].dirty = 0;
                    cache[i].blocks[j].tag = 0;
		    			  cache[i].blocks[j].valid = 0;
		    			  cache[i].blocks[j].LFUcount = 0;
                }
            }
        }
    }
}

/**
 * Subroutine that simulates one cache event at a time.
 * @param rw The type of access, READ or WRITE
 * @param address The address that is being accessed
 * @param stats The struct that you are supposed to store the stats in
 */
void cache_access (char rw, uint64_t address, struct cache_stats_t *stats) {
   	
    stats->accesses++;
    
    int numBlocksPerSet = 1 << (c - b - s);
    
    uint64_t index = extract_index(address);
    uint64_t tag = extract_tag(address);
    set theSet = cache[index];
    
    int LFUindex = 0;
    int LFUmin = theSet.blocks[0].LFUcount;
    
    if (rw == READ) 
    {
    
        stats->reads++;
        int missed = 1;
		  int i;
		  
        for (i = 0; i < numBlocksPerSet; i++) 
        {
            if (theSet.blocks[i].LFUcount < LFUmin) 
            {
                LFUmin = theSet.blocks[i].LFUcount;
                LFUindex = i;
            }
            if (theSet.blocks[i].tag == tag && theSet.blocks[i].valid) 
            {
                missed = 0;
                theSet.blocks[i].LFUcount++;
            }
        }
        
        if (missed) 
        {
            stats->read_misses++;
            if (theSet.blocks[LFUindex].valid == 0)
            {
		         theSet.blocks[LFUindex].tag = tag;
		         theSet.blocks[LFUindex].valid = 1;
		         theSet.blocks[LFUindex].LFUcount++;
		         if (theSet.blocks[LFUindex].dirty) 
		         {
		             stats->write_backs++;
		         }
			 		theSet.blocks[LFUindex].dirty = 0;
	    		}
        }
    }
    else if (rw == WRITE) 
    {
        stats->writes++;
        int missed = 1;
		  int i;
		  
        for(i = 0; i < numBlocksPerSet; i++) 
        {
            if (theSet.blocks[i].LFUcount < LFUmin) 
            {
                LFUmin = theSet.blocks[i].LFUcount;
                LFUindex = i;
            }
            if (theSet.blocks[i].tag == tag && theSet.blocks[i].valid) 
            {
                missed = 0;
                theSet.blocks[i].LFUcount++;
                theSet.blocks[i].dirty = 1;
                theSet.blocks[i].valid = 1;
            }
        }
        if (missed) 
        {
            stats->write_misses++;
            theSet.blocks[LFUindex].tag = tag;
            theSet.blocks[LFUindex].LFUcount = 1;
	    		if (theSet.blocks[LFUindex].dirty) 
	    		{
                stats->write_backs++;
                theSet.blocks[LFUindex].dirty = 0;
            }
            theSet.blocks[LFUindex].valid = 1;
        }
    }
    else 
    {
        //RW not READ or WRITE
    }
}

/**
 * Subroutine for cleaning up memory operations and doing any calculations
 * Make sure to free malloced memory here.
 *
 */
void cache_cleanup (struct cache_stats_t *stats) {
    
    stats->misses = stats->read_misses + stats->write_misses;
    
    stats->miss_rate = stats->misses/((float)stats->accesses);
    
    stats->avg_access_time = stats->access_time + (stats->miss_rate * stats->miss_penalty);

    int numSets = 1 << s;
    int i;
    
    for ( i = 0; i < numSets; i++) 
    {
        free(cache[i].blocks);
    }
    
    free(cache);
}
