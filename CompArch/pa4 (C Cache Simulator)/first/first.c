//Cache simulator
//See first.h and report.txt for details

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include "first.h"

int hitsCt = 0; int missCt = 0; int readsCt = 0; int writesCt = 0;

Set * initialize_cache(int num_sets, int num_block_bytes, int assoc, int num_tag_bits, Set * cache);
void free_cache(int num_sets, int assoc, Set * cache);
Set * read(char * t_tag, int t_set, int prefetch, char * ff_tag, int ff_set, Set * cache);
Set * write(char * t_tag, int t_set, int prefetch, char * ff_tag, int ff_set, Set * cache);
Set * insert_block(char * t_tag, int t_set, Set * cache);
Set * evict_block(int t_set, Set * cache);

int main(int argc, char * argv []) {
  // Parse args
  if(argc != 6) { printf("error\n"); return 0; }

  int num_cache_bytes = (int) strtoul(argv[1], NULL, 10);
  if(num_cache_bytes % 2 != 0) { printf("error\n"); return 0; }

  if(strcmp(argv[3], "lru") != 0) { printf("error\n"); return 0; }

  int num_block_bytes = (int) strtoul(argv[4], NULL, 10);
  if(num_block_bytes % 2 != 0) { printf("error\n"); return 0; }

  char * filename = argv[5];

  int assoc;
  if(strcmp(argv[2], "direct") == 0) { assoc = 1; }
  else if(strcmp(argv[2], "assoc") == 0) { assoc = num_cache_bytes / num_block_bytes; }
  else if(strstr(argv[2], "assoc:") != NULL) {
    assoc = argv[2][6] - '0';
    if(assoc > (num_cache_bytes / num_block_bytes) || assoc < 0 || (assoc != 1 && assoc % 2 != 0)) {
      printf("error\n");
      return 0;
    }
  } else {
    printf("error\n");
    return 0;
  }

  // Calculate stuff & initialize
  int num_sets = num_cache_bytes / (num_block_bytes * assoc);
  int num_block_bits = log(num_block_bytes) / log(2);
  int num_set_bits = log(num_sets) / log(2);
  int num_tag_bits = 48 - (num_block_bits + num_set_bits);

  char * garbage; char op; char address_str[49]; char fetched_friend_str[49];
  unsigned long address; unsigned long fetched_friend;
  char t_tag[num_tag_bits + 1]; int t_set;
  char ff_tag[num_tag_bits + 1]; int ff_set;

  garbage = (char *) calloc(49, sizeof(char));

  Set * cache = (Set *) calloc(num_sets, sizeof(Set));
  cache = initialize_cache(num_sets, num_block_bytes, assoc, num_tag_bits, cache);

  // Run without prefetch
  printf("no-prefetch\n");
  int prefetch = 0;

  FILE * file = fopen(filename, "r");
  if(!file) {
    printf("error\n");
    return 0;
  }

  while(fscanf(file, "%s %c %s\n", garbage, &op, address_str) != EOF) {
    if(strcmp(garbage, "#eof") == 0) { break; }

    address = strtoul(address_str, NULL, 0);
    hexToBinary(address_str);
    zero_extend(address_str);
    get_tag_bits(address_str, num_tag_bits, t_tag);
    t_set = num_sets == 1 ? 0 : get_set_bits(address_str, num_tag_bits, num_set_bits);

    fetched_friend = address + (unsigned long) num_block_bytes;
    longToBinary(fetched_friend, fetched_friend_str);
    zero_extend(fetched_friend_str);
    get_tag_bits(fetched_friend_str, num_tag_bits, ff_tag);
    ff_set = num_sets == 1 ? 0 : get_set_bits(fetched_friend_str, num_tag_bits, num_set_bits);

    if(op == 'R') {
      cache = read(t_tag, t_set, prefetch, ff_tag, ff_set, cache);
    } else if(op == 'W') {
      cache = write(t_tag, t_set, prefetch, ff_tag, ff_set, cache);
    }
  }

  fclose(file);

  printf("Memory reads: %d\n", readsCt);
  printf("Memory writes: %d\n", writesCt);
  printf("Cache hits: %d\n", hitsCt);
  printf("Cache misses: %d\n", missCt);

  free_cache(num_sets, assoc, cache);
  free(cache);

  cache = (Set *) calloc(num_sets, sizeof(Set));
  cache = initialize_cache(num_sets, num_block_bytes, assoc, num_tag_bits, cache);

  // Run with prefetch
  printf("with-prefetch\n");
  prefetch = 1; hitsCt = 0; missCt = 0; readsCt = 0; writesCt = 0;

  file = fopen(filename, "r");
  if(!file) {
    printf("error\n");
    return 0;
  }

  while(fscanf(file, "%s %c %s\n", garbage, &op, address_str) != EOF) {
    if(strcmp(garbage, "#eof") == 0) { break; }

    address = strtoul(address_str, NULL, 0);
    hexToBinary(address_str);
    zero_extend(address_str);
    get_tag_bits(address_str, num_tag_bits, t_tag);
    t_set = num_sets == 1 ? 0 : get_set_bits(address_str, num_tag_bits, num_set_bits);

    fetched_friend = address + (unsigned long) num_block_bytes;
    longToBinary(fetched_friend, fetched_friend_str);
    zero_extend(fetched_friend_str);
    get_tag_bits(fetched_friend_str, num_tag_bits, ff_tag);
    ff_set = num_sets == 1 ? 0 : get_set_bits(fetched_friend_str, num_tag_bits, num_set_bits);

    if(op == 'R') {
      cache = read(t_tag, t_set, prefetch, ff_tag, ff_set, cache);
    } else if(op == 'W') {
      cache = write(t_tag, t_set, prefetch, ff_tag, ff_set, cache);
    }
  }

  fclose(file);

  printf("Memory reads: %d\n", readsCt);
  printf("Memory writes: %d\n", writesCt);
  printf("Cache hits: %d\n", hitsCt);
  printf("Cache misses: %d\n", missCt);

  free(garbage);
  free_cache(num_sets, assoc, cache);
  free(cache);

  return 0;
}

Set * initialize_cache(int num_sets, int num_block_bytes, int assoc, int num_tag_bits, Set * cache) {
  int i; int j;
  for(i = 0; i < num_sets; i++){
    cache[i].size = 0;
    cache[i].max_size = assoc;
    cache[i].blox = (Block *) calloc(assoc, sizeof(Block));
    cache[i].times = createQueue(assoc);
    for(j = 0; j < assoc; j++){
      cache[i].blox[j].valid_bit = 0;
      cache[i].blox[j].tag = (char *)calloc((num_tag_bits + 1), sizeof(char));
    }
  }
  return cache;
}

void free_cache(int num_sets, int assoc, Set * cache) {
  int i; int j;
  for(i = 0; i < num_sets; i++){
    for(j = 0; j < assoc; j++) {
      free(cache[i].blox[j].tag);
    }
    QNode * ptr;
    for(ptr = cache[i].times->front; ptr != NULL; ptr = ptr->next) {
      free(ptr->tag);
    }
    free(cache[i].times);
    free(cache[i].blox);
  }
}

Set * read(char * t_tag, int t_set, int prefetch, char * ff_tag, int ff_set, Set * cache) {
  int hit = 0;
  if(cache[t_set].size != 0) {
    int i;
    for(i = 0; i < cache[t_set].max_size; i++){
      if(strcmp(cache[t_set].blox[i].tag, t_tag) == 0 && cache[t_set].blox[i].valid_bit == 1){
        hitsCt++; hit = 1; break;
      }
    }
  }
  if(hit == 0) {
    missCt++;
    if(cache[t_set].size == cache[t_set].max_size) { cache = evict_block(t_set, cache); }
    cache = insert_block(t_tag, t_set, cache);
    readsCt++;
  }
  cache[t_set].times = updateQueue(cache[t_set].times, t_tag);

  if(prefetch == 1 && hit == 0){
    if(cache[ff_set].size != 0) {
      int j;
      for(j = 0; j < cache[ff_set].max_size; j++){
        if(strcmp(cache[ff_set].blox[j].tag, ff_tag) == 0 && cache[ff_set].blox[j].valid_bit == 1){
          return cache;
        }
      }
    }
    if(cache[ff_set].size == cache[ff_set].max_size) { cache = evict_block(ff_set, cache); }
    cache = insert_block(ff_tag, ff_set, cache);
    readsCt++;
    cache[ff_set].times = updateQueue(cache[ff_set].times, ff_tag);
  }
  return cache;
}

Set * write(char * t_tag, int t_set, int prefetch, char * ff_tag, int ff_set, Set * cache) {
  int hit = 0;
  if(cache[t_set].size != 0) {
    int i;
    for(i = 0; i < cache[t_set].max_size; i++){
      if(strcmp(cache[t_set].blox[i].tag, t_tag) == 0 && cache[t_set].blox[i].valid_bit == 1){
        hitsCt++; hit = 1; break;
      }
    }
  }
  if(hit == 0) {
    missCt++;
    if(cache[t_set].size == cache[t_set].max_size) { cache = evict_block(t_set, cache); }
    cache = insert_block(t_tag, t_set, cache);
    readsCt++;
  }
  writesCt++;
  cache[t_set].times = updateQueue(cache[t_set].times, t_tag);

  if(prefetch == 1 && hit == 0){
    if(cache[ff_set].size != 0) {
      int j;
      for(j = 0; j < cache[ff_set].max_size; j++){
        if(strcmp(cache[ff_set].blox[j].tag, ff_tag) == 0 && cache[ff_set].blox[j].valid_bit == 1){
          return cache;
        }
      }
    }
    if(cache[ff_set].size == cache[ff_set].max_size) { cache = evict_block(ff_set, cache); }
    cache = insert_block(ff_tag, ff_set, cache);
    readsCt++;
    cache[ff_set].times = updateQueue(cache[ff_set].times, ff_tag);
  }
  return cache;
}

/* ASSUMES THERE IS SPACE IN THE SET, i.e. some node has an invalid bit.
Go to set; find any node with invalid bit (0); if just evicted, then there is only one;
replace it with target block, update set size */
Set * insert_block(char * t_tag, int t_set, Set * cache) {
  int i;
  for(i = 0; i < cache[t_set].max_size; i++){
    if(cache[t_set].blox[i].valid_bit == 0){
      cache[t_set].blox[i].valid_bit = 1;
      strcpy(cache[t_set].blox[i].tag, t_tag);
      cache[t_set].size++;
      return cache;
    }
  }
  return cache;
}

/* Set LRU block to invalid, update set size */
Set * evict_block(int t_set, Set * cache) {
  if(cache[t_set].size != cache[t_set].max_size) { return cache; } // error.
  int i;
  for(i = 0; i < cache[t_set].max_size; i++){
    if(strcmp(q_peek_rear(cache[t_set].times), cache[t_set].blox[i].tag) == 0){
      cache[t_set].blox[i].valid_bit = 0;
      cache[t_set].size--;
      return cache;
    }
  }
  return cache; // LRU not found... error.
}
