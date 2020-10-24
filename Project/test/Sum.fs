BEGINPROG Sum

/* This is a sample program that counts the sum of numbers from 0 to an input inserted by the user */

  READ(number)              // Read a number from user input
  sum:=0 
WHILE (number >= 0) THEN
   IF (number > 0) THEN
       sum := sum + 1
       number:= number - 1
   ELSE 
      PRINT(sum)   
   ENDIF  
ENDWHILE
ENDPROG
