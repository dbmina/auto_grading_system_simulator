# auto_grading_system_simulator

This is a auto-grading system using JAVA

I implemented an auto-grading system that compiles, executes, and grades students’ submissions.         
To simulate this, we will use a virtual compiler and executer.         

Compiler Class Specifications
Our compiler can understand an imaginary language named SUGO. It converts a source code file (with the extension .sugo) into an executable file (with the extension .yo) .       
The class provides one public method:       
● public void compile(String filePath)        
○ Compiles the specified SUGO file and creates a YO file of the same name in the directory that the SUGO file is in.        
■ For example, compiler.compile(“codes/hello.sugo”) makes a new file named hello.yo in the directory codes/ .        
■ If a YO file of the same name already exists, it will be overwritten.         
○ It throws CompileErrorException if the SUGO code contains syntax errors. There is no way you can prevent this and must handle it.        
○ It throws InvalidFileTypeException if the specified file does not have .sugo extension or the number of periods(.) in the file name is not one.         
○ It throws FileSystemRelatedException if an error occurs while reading or writing a file.       

Executer Class Specifications        
This class runs a YO program and writes the program output to a new file . It also has one public method provided:       
● public void execute(String targetFilePath, String inputFilePath, String outputFilePath)      
○ Executes a .yo file specified by targetFilePath with the program input given by a file at inputFilePath , and creates a new file containing the program output at outputFilePath .      
■ If there is already a file at outputFilePath , it will be overwritten.        
○ It throws RunTimeErrorException if the program with the given input results in an error.         
○ It throws InvalidFileTypeException if the specified file does not have a .yo extension or the number of periods(.) in the file name is not one.      
○ It throws FileSystemRelatedException if an error occurs while reading or writing a file.      

# Function 1: Simple Grader Objective: Implement the gradeSimple method in the Grader class.
Description: to implement a grader that grades students’ submitted codes according to the specification of an exam.     
See below for the description of parameters and a return value of the gradeSimple method.         
● Parameters       
○ ExamSpec examSpec : It contains information about the given exam.         
○ String submissionDirPath : It points to the directory where students’ submitted codes are located.       
※ ) it is guaranteed that a string variable whose name ends with “dirPath”, including this, ends with ‘/’(file separator of the platform)        
● Return value        
○ Type: Map<String, Map<String, List>>          
○ It returns a Map that maps a student ID to a Map , which maps a problem ID to a list of scores for individual test cases.        
■ <studentId, <problemId, list of >       
■ A list of scores for individual test cases should be in ascending order of test case ID.      

● The immediate sub-directories of the submission directory contain students’ codes (‘ student submission directory’ hereafter).      
○ Student submission directories are named with student IDs.         
● The immediate sub-directories of the student submission directories contain students’ codes for each problem ( ‘problem submission directory’ hereafter).          
○ Problem submission directories are named with problem IDs.      
● Every student submits codes for all problems.          
● Each problem submission directory contains exactly one .sugo file named Problem.targetFileName .       
○ The file contains the program that compiles and runs without any errors.        
For each test case, it is needed to compare (1) the output of a student’s program and (2) the desired output, which can be found in an output file of the test case. The two files are considered the same when the contents are literally the same . Give a score specified in the test case for the correct answer, and a 0 for the wrong answer.       

# Function 2: Robust Grader

Objective: Implement the gradeRobust method in the Grader class.       
Description: There are many unexpected situations in the real world. Now you are required to upgrade your grader so that it can deal with various corner cases in the submitted codes. There are 4 groups of corner cases to handle.        

Group 1. Errors       
● Compile error : Compiler.compile method may throw CompileErrorException . In this case, all test cases in the problem should get 0 points.    
● Runtime error : Executer.execute method may throw RunTimeErrorException . In this case, a corresponding test case should get 0 points.       

Group 2. White Spaces and Upper/Lower Cases       
Sometimes, we want to be generous about subtle mistakes like printing trailing whitespaces or confusion of lower and upper cases. More specifically, we would like to consider the following three cases (Note: they are not mutually exclusive). We may tolerate all three cases for generous grading. Or, we may want to be strict about all three cases. Problem.judgingTypes specifies which case we want to be generous about. If Problem.judgingTypes is null or an empty set , it indicates that the grading will be strict about all three cases.        
● Trailing whitespaces : If Problem.judgingTypes includes Problem.TRAILING_WHITESPACES , a student’s output and the desired output are considered the same if the two outputs contain the same string and the only difference is the existence of additional whitespaces at the end of the string.       
● Ignore whitespaces : If Problem.judgingTypes includes Problem.IGNORE_WHITESPACES , remove all whitespaces in a student’s output and the desired output before you compare those.       
● Case-insensitive : If Problem.judgingTypes includes Problem.CASE_INSENSITIVE , do not differentiate lower-cased letters and upper-cased letters.      
In this project, “whitespaces” are defined as spaces (‘ ‘), line feeds (‘ \n ’), and hard tabs (‘ \t ’). Assume that the inputs do not contain other characters that are generally considered as whitespaces such as ‘ \r ’, ‘ U+FEFF ’, etc.       
Group 3. Multiple Source Files      
Not all programs are written in a single file. Also, some problems may require TAs to write additional wrapper codes to thoroughly test students’ codes.       
● Student codes : If a problem submission directory contains multiple .sugo files, you should compile them all before you execute the .yo file specified by Problem.targetFileName . If there is a compilation error for any of the submitted files in the directory, it should be handled as specified in Group 1 above.   
● Wrapper codes : If Problem.wrappersDirPath is not null, copy all .sugo files in that directory to the problem submission directory before compilation; assume that there is no .sugo file with the same name in the submission directory.    
Assume that the values of Problem.wrappersDirPath and Problem.targetFileName are always valid. Also, assume that all .sugo files in a problem submission directory are relevant codes and need to be compiled.      
Group 4. Submission Format Errors
● No submission : Some directories and files in the student submission directories can be missing. Even a student submission directory itself can be absent. You should give 0 points for all problems whose submissions are incomplete.     
○ Even when the whole student submission directory is missing, the result of gradeRobust should contain their scores, filled with 0’s.         
● Submitted only .yo files : Some students might have submitted .yo files instead of .sugo files. For each problem submission directory, if there is at least one .yo file without a .sugo file of the same name, use it during execution but cut the scores by half for all test cases for the problem.       
● Submitted unnecessary .yo files : However, if there are both a .sugo and a .yo file of the same name in a problem submission directory, compile the .sugo file to overwrite the .yo file and use it. In this case, there is no penalty .       
● Wrong directory structure : Sometimes, a problem submission directory contains exactly one directory of an arbitrary name that contains source codes. In this case, there is no penalty .         
● Wrong directory name: The name of a student submission directory may not be the same as the student ID, but starts with it. In this case, there is no penalty .       
○ For example, 2019-12345-LT3 instead of 2019-12345       
○ Assume that a student ID is NOT included more than once in directory names.         
