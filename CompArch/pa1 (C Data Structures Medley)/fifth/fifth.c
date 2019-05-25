# include <stdio.h>
# include <string.h>
# include <stdlib.h>

int main(int argc, char * argv[]) {
int i;
for(i = 1; i<argc; i++) {	
	int length  = strlen(argv[i]);
	char string[length];
	strcpy(string, argv[i]);
	int j;
	for(j = 0; j < length; j++) {
		if(string[j]=='a' || string[j]=='e' ||
		   string[j]=='o' || string[j]=='i' ||
		   string[j]=='u' || 
		   string[j]=='A' || string[j]=='E' ||
		   string[j]=='I' || string[j]=='O' ||
		   string[j]=='U') {
			printf("%c", string[j]);
		}
	}
}
return 0;
}
