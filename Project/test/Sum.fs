BEGINPROG Sum

/* This is a sample program that counts the sum of numbers from 0 to an input inserted by the user 
   Then compare it to another number which consists of user's guess to the intended sum */

  READ(number)              // Read a number from user input
  READ(guess)                // Print 1 in case user's guess was correct and 0 in case wrong
 sum:=0 
WHILE (number >= 0) THEN
       sum := sum + 1
       number:= number - 1   
ENDWHILE
IF (number = guess) THEN
   PRINT(1)
  ELSE 
   PRINT(0)
ENDIF
ENDPROG
