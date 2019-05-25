//LRU implemented as a queue
//See report.txt for details

typedef struct QNode {
  struct QNode *prev, *next;
  char * tag;
} QNode;

typedef struct Queue {
  int size;
  int max_size;
  QNode *front, *rear;
} Queue;

QNode * newQNode(char * tag);
Queue * createQueue(int max_size);
int isQueueFull(Queue * queue);
int isQueueEmpty(Queue * queue);
Queue * deQueue(Queue * queue);
char * q_peek_rear(Queue * queue);
Queue * enqueue(Queue * queue, char * tag);
Queue * updateQueue(Queue * queue, char * tag);

QNode * newQNode(char * tag) {
  QNode * temp = (QNode *)calloc(1, sizeof(QNode));
  temp->tag = (char *) calloc((strlen(tag)+1), sizeof(char));
  strcpy(temp->tag, tag);
  temp->prev = temp->next = NULL;
  return temp;
}

Queue * createQueue(int max_size) {
  Queue * queue = (Queue *) calloc(1, sizeof(Queue));
  queue->size = 0;
  queue->front = queue->rear = NULL;
  queue->max_size = max_size;
  return queue;
}

int isQueueFull(Queue * queue) { return queue->size == queue->max_size; }

int isQueueEmpty(Queue * queue) { return queue->rear == NULL; }

Queue * deQueue(Queue * queue) {
  if(isQueueEmpty(queue))  return queue;

  if(queue->front == queue->rear) {
    free(queue->front->tag);
    //free(queue->front);
    queue->front = NULL;
  }
  queue->rear = queue->rear->prev;

  if(queue->rear) {
    free(queue->rear->next->tag);
    //free(queue->rear->next);
    queue->rear->next = NULL;
  }

  queue->size--;
  return queue;
}

char * q_peek_rear(Queue * queue) {
  if(queue->rear) return queue->rear->tag;
  else return "empty";
}

Queue * enqueue(Queue * queue, char * tag) {
  if(isQueueFull(queue)) {
    deQueue(queue);
  }

  QNode * temp = newQNode(tag);
  temp->next = queue->front;

  if(isQueueEmpty(queue)) queue->rear = queue->front = temp;
  else {
    queue->front->prev = temp;
    queue->front = temp;
  }

  queue->size++;
  return queue;
}

// Case 1: Block not in cache, put at front (most recently accessed)
// Case 2: Block in cache, move to front (most recently accessed)
Queue * updateQueue(Queue * queue, char * tag) {
  QNode * ptr = (QNode *) calloc(1, sizeof(QNode));
  for(ptr = queue->front; ptr != NULL; ptr = ptr->next) {
    if(strcmp(ptr->tag, tag) == 0) {
      if(ptr != queue->front) {
        ptr->prev->next = ptr->next;
        if(ptr->next) ptr->next->prev = ptr->prev;

        if(ptr == queue->rear) {
          queue->rear = ptr->prev;
          queue->rear->next = NULL;
        }

        ptr->next = queue->front;
        ptr->prev = NULL;

        ptr->next->prev = ptr;
        queue->front = ptr;
      }
      return queue;
    }
  }
  queue = enqueue(queue, tag);
  return queue;
}

void longToBinary(unsigned long n, char * target);
void hexToBinary(char * hex);
void zero_extend(char * binary);
void get_tag_bits (char * address, int num_tag_bits, char * target);
int get_set_bits (char * address, int num_tag_bits, int num_set_bits);
char * strrev(char *str);

typedef struct Block{
  int valid_bit;
  char * tag;
} Block;

typedef struct Set {
  int size;
  int max_size;
  Block * blox;
  Queue * times;
} Set;

char *strrev(char *str){
  char *p1, *p2;

  if (! str || ! *str)
    return str;
  for (p1 = str, p2 = str + strlen(str) - 1; p2 > p1; ++p1, --p2) {
    *p1 ^= *p2;
    *p2 ^= *p1;
    *p1 ^= *p2;
  }
  return str;
}

void longToBinary(unsigned long n, char * target) {
  target[0] = '\0';

  while(n > 0) {
    if(n % 2 == 0) {
      strcat(target,"0");
    } else {
      strcat(target,"1");
    }
    n = n / 2;
  }
  strcat(target, "\0");
  target = strrev(target);

  return;
}

void hexToBinary(char * hex) {
  int i;
  char tmp[49];
  tmp[0] = '\0';
  for(i = 2; i < strlen(hex); i++) {
    switch(hex[i]) {
    case '0': strcat(tmp,"0000"); break;
    case '1': strcat(tmp,"0001"); break;
    case '2': strcat(tmp,"0010"); break;
    case '3': strcat(tmp,"0011"); break;
    case '4': strcat(tmp,"0100"); break;
    case '5': strcat(tmp,"0101"); break;
    case '6': strcat(tmp,"0110"); break;
    case '7': strcat(tmp,"0111"); break;
    case '8': strcat(tmp,"1000"); break;
    case '9': strcat(tmp,"1001"); break;
    case 'a': strcat(tmp,"1010"); break;
    case 'b': strcat(tmp,"1011"); break;
    case 'c': strcat(tmp,"1100"); break;
    case 'd': strcat(tmp,"1101"); break;
    case 'e': strcat(tmp,"1110"); break;
    case 'f': strcat(tmp,"1111"); break;
    }
  }
  strcat(tmp, "\0");

  strcpy(hex, tmp);
  strcat(hex, "\0");
  return;
}

void zero_extend(char * binary) {
  int i; int offset = 48 - strlen(binary);
  char tmp[49];
  tmp[0] = '\0';
  for(i = 0; i < offset; i++) {
    strcat(tmp, "0");
  }
  strcat(tmp, binary);
  strcat(tmp, "\0");

  strcpy(binary, tmp);
  strcat(binary, "\0");
  return;
}

void get_tag_bits (char * address, int num_tag_bits, char * target) {
  char tmp[num_tag_bits + 1];
  tmp[0] = '\0';
  int i;
  for(i = 0; i < num_tag_bits; i++) {
    if(address[i] == '0') strcat(tmp, "0");
    else if(address[i] == '1') strcat(tmp, "1");
  }
  strcat(tmp, "\0");

  strcpy(target, tmp);
  return;
}

int get_set_bits (char * address, int num_tag_bits, int num_set_bits) {
  char result[num_set_bits+1];
  result[0] = '\0';
  int i;
  for(i = num_tag_bits; i < num_tag_bits + num_set_bits; i++) {
    if(address[i] == '0') strcat(result, "0");
    else if(address[i] == '1') strcat(result, "1");
  }
  strcat(result, "\0");
  int res = (int) strtoul(result, NULL, 2);
  return res;
}
