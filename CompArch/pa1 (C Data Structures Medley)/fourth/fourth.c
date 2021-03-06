#include <stdio.h>
#include <stdlib.h>

int main(int argc, char * argv[]){
  char * filename = argv[1];
  FILE * file = fopen(filename, "r");
  if(!file){
    printf("No such file!\n");
    return 0;
  }

  int A_num_rows; int A_num_cols; int B_num_rows; int B_num_cols;
  
  fscanf(file, "%d\t%d\n", &A_num_rows, &A_num_cols);  
  int * arr_A [A_num_rows];
  int i; int j;
  for(i = 0; i < A_num_rows; i++) arr_A[i] = (int *)malloc(A_num_cols * sizeof(int));
  for(i = 0; i < A_num_rows; i++){
    for(j = 0; j < A_num_cols; j++){
      fscanf(file, "%d", &(arr_A[i][j]));
    }
  }

  fscanf(file, "%d\t%d\n", &B_num_rows, &B_num_cols);
  int * arr_B [B_num_rows];
  for(i = 0; i < B_num_rows; i++) arr_B[i] = (int *)malloc(B_num_cols * sizeof(int));
  for(i = 0; i < B_num_rows; i++){
    for(j = 0; j < B_num_cols; j++){
      fscanf(file, "%d", &(arr_B[i][j]));
    }
  }

  fclose(file);

  if(A_num_cols != B_num_rows){
    printf("bad-matrices");
    return 0;
  }

  int * arr_AB [A_num_rows];
  for(i = 0; i < A_num_rows; i++) arr_AB[i] = (int *)malloc(B_num_cols * sizeof(int));

  /* Note A_num_cols == B_num_rows */
  for(i = 0; i < A_num_rows; i++){
    for(j = 0; j < B_num_cols; j++){
      int k; int sum = 0;
      for(k = 0; k < A_num_cols; k++) {
	sum += (arr_A[i][k])*(arr_B[k][j]);
      } 
      arr_AB[i][j] = sum;
    }
  }

  /* overcomplicated printing process bc we can't have extra tabs or line breaks for some reason */
  for(i = 0; i < A_num_rows-1; i++){
    for(j = 0; j < B_num_cols-1; j++){
      printf("%d\t", arr_AB[i][j]);
    }
    printf("%d", arr_AB[i][B_num_cols-1]);
    printf("\n");
  }
  for(j = 0; j < B_num_cols-1; j++){
    printf("%d\t", arr_AB[A_num_rows-1][j]);
  }
  printf("%d", arr_AB[A_num_rows-1][B_num_cols-1]);

  for(i = 0; i < A_num_rows; i++) {  free(arr_A[i]); free(arr_AB[i]);  }
  for(i = 0; i < B_num_rows; i++) free(arr_B[i]);  
  
  return 0;
}
