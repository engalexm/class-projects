#include "third.h"
#include <stdio.h>
#include <stdlib.h>

int insert(Node ** hash, int key);
int search(Node ** hash, int key);
void deleteBuckets(Node ** hash, int hashCode);
int modulo(int x, int y);

int main (int argc, char * argv[]){
  
  Node ** hash = calloc(10000, sizeof(Node));
  
  char * filename = argv[1];
  FILE * file = fopen(filename, "r");
  if(!file){
    printf("No such file!/n");
    return 0;
  }
  
  int total_collisions = 0;
  int total_found = 0;
  char operation; int key; 
 
  while(fscanf(file, "%c\t%d\n", &operation,&key) != EOF) {
    if(operation == 'i'){
      total_collisions += insert(hash, key);
    } else if(operation == 's'){
      total_found += search(hash, key);
    }
  }
  
  printf("%d\n%d\n", total_collisions, total_found);

  fclose(file);

  file = fopen(filename, "r");
  if(!file){
    printf("No such file!/n");
    return 0;
  }

  while(fscanf(file, "%c\t%d\n", &operation,&key) != EOF) {
    if(operation == 'i'){
      deleteBuckets(hash, modulo(key, 10000));
    } else if(operation == 's'){
    }
  }

  fclose(file);
 
  free(hash);

  return 0;
}

/* returns 1 if collisions took place, 0 otherwise */
int insert(Node** hash, int key){
  int hashCode = modulo(key, 10000);
  Node * newNode = calloc(1,sizeof(Node));
  newNode->data = key;
  newNode->next = NULL;
  
  if(!(hash[hashCode])){
    hash[hashCode] = newNode;
    return 0;
  } else {
    newNode->next = &(*(hash[hashCode]));
    hash[hashCode] = newNode;
    return 1;
  }
} 

/* returns 1 if found, 0 otherwise */
int search(Node** hash, int key){
  int hashCode = modulo(key, 10000);
  if(!(hash[hashCode])){
    return 0;
  }
   
  Node * ptr = calloc(1, sizeof(Node));
  ptr = hash[hashCode];
 
  while(ptr != NULL){
    if(ptr->data == key){
      return 1;
    }
    ptr = ptr->next;
  }
  
  free(ptr);
  return 0;
}

void deleteBuckets(Node** hash, int hashCode){
  Node * curr = calloc(1, sizeof(Node));
  curr = hash[hashCode];
  Node * nxt = calloc(1, sizeof(Node));

  while(curr){
    nxt = curr->next;
    free(curr);
    curr = nxt;
  }
  
  hash[hashCode] = NULL;
  free(curr);
  free(nxt);

  return;
}


/* lmao why is % in C not a regular modulo that's wack */
int modulo(int x, int y){
  if(y < 0) return modulo(x, -y);
  int ret = x % y;
  if(ret < 0) ret += y;
  return ret;
}

