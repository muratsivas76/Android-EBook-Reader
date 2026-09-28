import java.util.*;
import java.io.*;

//File [] dizi = ...;
//Sorting sorting = new Sorting();
//Arrays.sort(dizi, sorting);

public class Sorting implements Comparator<File>
{
   
   public Sorting()
   {}
   
   public int compare(File r1 , File r2)
   {
      return ( (r1.getName().toLowerCase()).compareTo (r2.getName().toLowerCase()) );
   }
   
}//class end
