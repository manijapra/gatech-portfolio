#include <stdlib.h>
#include <stdio.h>
#include "tlb.h"
#include "pagetable.h"
#include "global.h" /* for tlb_size */
#include "statistics.h"

/*******************************************************************************
 * Looks up an address in the TLB. If no entry is found, attempts to access the
 * current page table via cpu_pagetable_lookup().
 *
 * @param vpn The virtual page number to lookup.
 * @param write If the access is a write, this is 1. Otherwise, it is 0.
 * @return The physical frame number of the page we are accessing.
 */
pfn_t tlb_lookup(vpn_t vpn, int write) {
   pfn_t pfn;

   /* 
    * FIX ME : Step 6
    */

   /* 
    * Search the TLB for the given VPN. Make sure to increment count_tlbhits if
    * it was a hit!
    */

	for (int i = 0; i < tlb_size; i++)
	{
		if (tlb[i].valid & (tlb[i].vpn == vpn))
		{
			tlb[i].used = 1;
			count_tlbhits++;
			tlb[i].dirty = write;
			return tlb[i].pfn;
		}
	}
    
   /* If it does not exist (it was not a hit), call the page table reader */
   pfn = pagetable_lookup(vpn, write);

   /* 
    * Replace an entry in the TLB if we missed. Pick invalid entries first,
    * then do a clock-sweep to find a victim.
    */

	for (int j = 0; j < tlb_size; j++)
	{
		if (!(tlb[j].valid))
		{
			tlb[j].valid = 1;
			tlb[j].used = 1;
			tlb[j].dirty = write;
			tlb[j].vpn = vpn;
			tlb[j].pfn = pfn;
			return pfn;
		}
	}


	int index = 0;
	int infinite = 1;
	while (infinite)
	{
		if (!(tlb[index].used))
		{
			tlb[index].valid = 1;
			tlb[index].used = 1;
			tlb[index].dirty = write;
			tlb[index].vpn = vpn;
			tlb[index].pfn = pfn;
			return pfn;
		}
		else
		{
			tlb[index].used = 0;
		}

		if (index < tlb_size)
			index++;
		else
			index = 0;
	}

   /*
    * Perform TLB house keeping. This means marking the found TLB entry as
    * accessed and if we had a write, dirty. We also need to update the page
    * table in memory with the same data.
    *
    * We'll assume that this write is scheduled and the CPU doesn't actually
    * have to wait for it to finish (there wouldn't be much point to a TLB if
    * we didn't!).
    */

   return pfn;
}

