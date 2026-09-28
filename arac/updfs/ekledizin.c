#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>

char * mkpath(char * , char *);
int dirlist1(char *);
void sonuc(off_t);
void mesaj();
int getlinem (char [] , char [] , int , FILE *) ;
char * diger(char *);
int line = 0  ;
int rr ;
off_t boy ;
FILE * cikti ;

int main(int argc , char * argv[])
{
    if(argc < 2)
    {
      mesaj();
      abort();
    }
    char direct [PATH_MAX + 1] = ".";
    strcpy(direct , argv[1]);
    printf("%s:\n" , direct);
    if ( ( cikti = fopen("zresult.txt" , "a") ) == NULL )
    {
       printf("%s:\n" , "zresult.txt could not produce.");
       //printf("%s\n" , "Çýktý sadece ekrana yazdýrýlacak");
     }
     if(!dirlist1(direct))
         printf("%s\n" , direct);
     if(boy > 0)
        sonuc(boy);
     return 0 ;
 }
 
 void mesaj()
 {
    printf("%s\n" , "Kullaným:\n./ekledizin [<dizin>]\n");
 }       
 
 void sonuc(off_t xy)
 {
    printf ("\nLook at zresult.txt.\n\n");
    
     float x = (float)xy ;
     if(x >= 1000000)
     {
        x = x /1e6 ;
	printf("\n%s%f%s\n" , "Toplam bulunan dosya boyutu : " , x , " MB");
     }
     else
     {
        x = x /1e4 ;
	printf("\n%s%f%s\n" , "Toplam bulunan dosya boyutu : " , x , " KB");
     } 	
   }
   
  int dirlist1(char * direct)
  {
     DIR * dir_p ;
     struct dirent * slot_p;
     struct stat inodbf ;
     char filepath [PATH_MAX+1] ;
     if ( ( dir_p = opendir(direct) ) != NULL )
     {
     while ( ( slot_p = readdir(dir_p) ) != NULL )
      {   
	 if(slot_p->d_ino != 0)
	 {
	     strcpy(filepath , direct);
	     mkpath(filepath , slot_p->d_name);
	     if(stat(filepath , &inodbf) == 0)
	     {
	         if(S_ISREG(inodbf.st_mode))
		 {
		    char * ext ;
		    ext = rindex(filepath , '.');
		    if (ext == NULL) continue;
		    
		    FILE * fp;
		    fp = fopen(filepath , "r");
		    if (fp == NULL) continue;
		    
		    if ((strcmp (ext, ".txt")  == 0) || 
			(strcmp (ext, ".html") == 0) )
		     {
		             char s [3000] ;
			     int len , son ;
			     len = 3000 ; 
			     line = 0;
			     if(cikti != NULL)
			     {
			      fprintf(cikti , "\n%s\n" , "==Y=E=N=I==D=O=S=Y=A==============");
			      fprintf(cikti , "%s" , filepath);
			      fprintf(cikti , "\n%s\n" , "===Y=E=N=I==D=O=S=Y=A=============");
			     }
			     while ( ( son = getlinem(s , filepath , len , fp)) > 0 );
			     boy += inodbf.st_size ;
		     }
		  }
		  else if ( ( S_ISDIR(inodbf.st_mode) &&
		              strcmp(slot_p->d_name , ".") != 0 &&
			      strcmp(slot_p->d_name , "..") != 0))
	          {
		              diger(filepath);
			      printf("-----------------------------------\n");
			      printf("%s  Dikkat!! Bu bir dizindir..\n" , filepath);
			      dirlist1(filepath);
		  }
	     }
	  }
	}  
     }
     closedir(dir_p);
     return 0 ;
   }
   
   //int getlinem (char [] , char [] , int , FILE *) ;
   int getlinem (char s [] , char isim [] , int lim , FILE * ff)
   {
            int chr , sayac ;
	    for ( sayac = 0 ; sayac < lim-1 && (chr = getc(ff)) != EOF && chr != '\n' ; sayac++)
	          s[sayac] = chr ;
	    if( chr =='\n' )
	    {
	        s [sayac] = chr ;
		++sayac ;
		++line ;
	    }
	    s [sayac] = '\0' ;
	    char pat [1000] ;
	    strcpy(pat , s);
            if( cikti != NULL )
	    {
	         if( ( rr = fprintf(cikti , "%s" , pat)) != -1) ;
	    }
	    return sayac ;
    }
    
    char * mkpath (char * direct , char * filename)
    {
        if(direct[strlen(direct) - 1] != '/')
	     strcat(direct , "/");
	strcat(direct , filename);
	return direct ;
     }
     
     char * diger (char * path)
     {
        if(path[strlen(path) - 1] != '/')
	     strcat(path , "/");
	return path ;
     } 
//gcc -Wall -o ekledizin ekledizin.c
