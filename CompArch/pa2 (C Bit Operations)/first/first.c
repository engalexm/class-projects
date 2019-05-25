#include <stdio.h>
#include <stdlib.h>
#include <string.h>

unsigned short get(unsigned short x, unsigned short n);
unsigned short comp(unsigned short x, unsigned short n);
unsigned short set(unsigned short x, unsigned short n, unsigned short v);

int main(int argc, char * argv[]){
  char * filename = argv[1];
  FILE * file = fopen(filename, "r");
  if(!file){
    printf("ERROR: No such file!\n");
    return 0;
  }

  unsigned short x;
  fscanf(file, "%hu\n", &x);

  char argument [10];
  unsigned short n; unsigned short v;

  while(fscanf(file, "%s\t%hu\t%hu\n", &argument[0], &n, &v) != EOF ){
    if(strcmp(argument, "get") == 0){
      printf("%hu\n", get(x, n));
    } else if(strcmp(argument, "comp") == 0){
      x = comp(x, n);
      printf("%hu\n", x);
    } else if(strcmp(argument, "set") == 0) {
      x = set(x, n, v);
      printf("%hu\n", x);
    } else {
      printf("ERROR: Invalid argument \"%s\".\n", argument);
      return 0;
    }
  }

  return 0;
}

unsigned short get(unsigned short x, unsigned short n){
  return (x >> n) & 1;
}

unsigned short comp(unsigned short x, unsigned short n){
  unsigned short complement_nth_bit = get(x, n) == 0 ? 1 : 0;
  return set(x, n, complement_nth_bit);
}

unsigned short set(unsigned short x, unsigned short n, unsigned short v){
  return v==1 ? (1 << n) | x : ~(1 << n) & x; 
}
