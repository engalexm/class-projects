#include "sixth.h"
#include <stdio.h>
#include <stdlib.h>

void inorder(Node * root);
void freeTree(Node * root);
Node * insert(Node * root, int key);

int main(int argc, char * argv []){
  
  char * filename = argv[1];
  FILE * file = fopen(filename, "r");
  if(!file){
    printf("error");
    return 0;
  }

  char operation; int key; 
  Node * root = malloc(sizeof(Node));
  root = NULL;

  while(fscanf(file, "%c\t%d\n", &operation,&key) != EOF ){
    if(operation == 'i'){
      root = insert(root, key);
    }
  }
  fclose(file);

  inorder(root);
  freeTree(root);

  return 0;
}

void inorder(Node * root){
  if(!root) return;
  if(root->left) inorder(root->left);
  printf("%d\t", root->data);
  if(root->right) inorder(root->right);
  return;
}

Node * insert(Node * root, int key){
  Node * newNode = malloc(sizeof(Node));
  newNode->data = key;

  if(!root) {
    root = newNode;
    return root;
  }

  Node * ptr = root; Node * parent = NULL;

  while(ptr){
    if(ptr->data == key) {
      return root;
    } else if(ptr->data < key){
      parent = ptr;
      ptr = ptr->right;
    } else if (ptr->data > key) {
      parent = ptr;
      ptr = ptr->left;
    } 
  }

  if(parent->data < key)       parent->right = newNode;
  else if(parent->data > key)  parent->left = newNode;

  return root;
}

void freeTree(Node * root){

  if(!root) return;
  if(root->left) freeTree(root->left);
  if(root->right) freeTree(root->right);
  free(root);

  return;
}
