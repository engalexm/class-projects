# include "second.h"
# include <stdio.h>
# include <stdlib.h>

int main(int argc, char * argv[]) {

 char * filename = argv[1];
 FILE * file = fopen(filename, "r");
 if(!file){
    printf("error");
    return 0;
 }

 char operation; int key; int count = 0;
 Node * front = NULL; Node * prev = NULL; Node * curr = NULL;
 Node * x;

 while(fscanf(file, "%c\t%d\n", &operation,&key) != EOF ){
   if(operation == 'i'){
     x = malloc(sizeof(Node)); x->data = key;
     if(!front){   front = x; count++;   }
     else {
       prev = NULL; curr = front;
       while(curr) {
	 if(curr->data >= x->data){
	   if(prev){   prev->next = x;   }
	   x->next = curr;
	   if(!prev){   front = x;   }
	   count++;
	   break;
	 }
	 prev = curr; curr = curr->next;
       }
       if(!curr){
	 prev->next = x;
	 count++;
       }
     }
   } else if(operation == 'd'){
     if(front){
       if(front->data <= key){   
	 prev = NULL; curr = front;
	 while(curr){
	   if(curr->data == key){
	     if(!prev){  front = curr->next;   }
	     else {   prev->next = curr->next;   }
	     count--; 
	     break;
	   } 
	   prev = curr; curr = curr->next;
	 }
       }
     }
   }
 }
 fclose(file);

 if(count==0){
   printf("%d\n",0);
 } else {



   printf("%d\n", count);
   
   Node * ptr = front; int last = -2147483642; 
   while(ptr->next){
     if(last != ptr->data){
       printf("%d\t", ptr->data);
     }
     last = ptr->data;
     ptr = ptr->next;
   }
   if(last != ptr->data){
     printf("%d", ptr->data);
   }
 }

 free(x);
 return 0;
}

