public class PredefinedFunctions {
	
	
public String readWrite() { 
	String equation= "@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\r\n" + 
			"\r\n" + 
			"; Function Attrs: nounwind uwtable\r\n" + 
			"define i32 @readInt() #0 {\r\n" + 
			"  %x = alloca i32, align 4\r\n" + 
			"  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)\r\n" + 
			"  %2 = load i32, i32* %x, align 4\r\n" + 
			"  ret i32 %2\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"declare i32 @__isoc99_scanf(i8*, ...) #1\r\n" + 
			"\r\n" + 
			"@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\r\n" + 
			"\r\n" + 
			"; Function Attrs: nounwind uwtable\r\n" + 
			"define void @println(i32 %x) #0 {\r\n" + 
			"  %1 = alloca i32, align 4\r\n" + 
			"  store i32 %x, i32* %1, align 4\r\n" + 
			"  %2 = load i32, i32* %1, align 4\r\n" + 
			"  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\r\n" + 
			"  ret void\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"declare i32 @printf(i8*, ...) #1\r\n"
			+ "\r\n";
	return equation; 
	
}

}
