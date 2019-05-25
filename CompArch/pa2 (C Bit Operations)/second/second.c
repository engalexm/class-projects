#include "second.h"
#include <stdio.h>
#include <stdlib.h>

int numBits(unsigned short x);
int numOneBitPairsOf(unsigned short x);

int main(int argc, char * argv []) {
  unsigned short x = (unsigned short) strtoul(argv[1], NULL, 0);

  if(numBits(x) % 2 == 0) printf("%s\t", "Even-Parity");
  else printf("%s\t", "Odd-Parity");

  printf("%d\n", numOneBitPairsOf(x));

  return 0;
}

int numBits(unsigned short x){
  int count = 0;
  unsigned short i;
  for(i = 0; i < 16; i++){
    unsigned short currBit = get(x, i);
    count += currBit == 1 ? 1 : 0;
  }
  return count;
}

int numOneBitPairsOf(unsigned short x){
  int count = 0;
  unsigned short prevBit = 0;
  unsigned short currBit = get(x, 0);
  unsigned short j = 0;
  while(j < 16){
    if(prevBit == 1 && currBit == 1){
      count++;
      prevBit = 0; 
    } else {
      prevBit = currBit;
    }
    j++; 
    currBit = get(x, j);
  }
  return count;
}


