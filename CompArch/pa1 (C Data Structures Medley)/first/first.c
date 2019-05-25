# include <stdio.h>
# include <stdlib.h>

int main(int argc, char* argv[]){

char * filename = argv[1];
FILE * file = fopen(filename, "r");
if(!file){
	printf("No such file found.");
	return 1;
}

int arrayLength;
fscanf(file, "%d", &arrayLength);
if(arrayLength == 0) {
	printf("File appears to be empty.");
	return 1;
}

int arr [arrayLength];
int j = 0; int evensCt = 0; int oddsCt = 0;
for(j = 0; j < arrayLength; j++){
	fscanf(file, "%d", &arr[j]);
	if(arr[j] % 2 == 0) evensCt++;
	else oddsCt++;
} 

int evens [evensCt]; int odds [oddsCt];
int k = 0; int evensIndex = 0; int oddsIndex = 0;
for(k = 0; k < arrayLength; k++){
	if(arr[k]%2 == 0){
		evens[evensIndex] = arr[k];
		evensIndex++;
	} else {
		odds[oddsIndex] = arr[k];
		oddsIndex++;
	}
}
	
void mergeSort(int arr[], int l, int r);
mergeSort(evens, 0, evensCt-1);
mergeSort(odds, 0, oddsCt-1);

int l;
for(l = 0; l < evensCt; l++) printf("%d\t", evens[l]);
for(l = oddsCt-1; l >= 0; l--) printf("%d\t", odds[l]);

fclose(file);

return 0;


}

void mergeSort(int arr[], int l, int r) { 
    	if (l < r)  { 
        	int m = l+(r-l)/2;    
        	mergeSort(arr, l, m); 
        	mergeSort(arr, m+1, r); 
		void merge(int arr[], int l, int m, int r);
        	merge(arr, l, m, r); 
    	}	 
} 

void merge(int arr[], int l, int m, int r) { 
	int i, j, k; int n1 = m - l + 1; int n2 =  r - m; 
	int L[n1], R[n2]; 

	for (i = 0; i < n1; i++)  L[i] = arr[l + i]; 
	for (j = 0; j < n2; j++) R[j] = arr[m + 1+ j];

	i = 0;  j = 0; k = l; 

	while (i < n1 && j < n2) { 
        	if (L[i] <= R[j]) { 
			arr[k] = L[i]; 
           		i++; 
        	} 
        	else { 
          	  	arr[k] = R[j]; 
            		j++; 
       	 	} 
        	k++; 
    	} 
 
    	while (i < n1) { 
        	arr[k] = L[i]; 
        	i++; k++; 
 	} 
  
    	while (j < n2) { 
        	arr[k] = R[j]; 
        	j++; k++; 
    	} 
}
