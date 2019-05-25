#include "third.h"
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char * argv []) {
  unsigned short x = (unsigned short) strtoul(argv[1], NULL, 0);
  unsigned short l;
  unsigned short r;
  int isPalindrome = 1;
  for(l = 0, r = 15; l <= 7 && r >= 8; l++, r--){
    unsigned short leftBit = get(x, l);
    unsigned short rightBit = get(x, r);
    if(leftBit != rightBit) {
      isPalindrome = 0;
      break;
    }
  } 
  
  if(isPalindrome == 1) printf("%s\n", "Is-Palindrome");
  else printf("%s\n", "Not-Palindrome");

  return 0;
}
