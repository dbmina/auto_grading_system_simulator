package cpta;

import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.ExamSpec;
import cpta.exam.Problem;
import cpta.exam.Student;
import cpta.exam.TestCase;
import cpta.exceptions.CompileErrorException;
import cpta.exceptions.FileSystemRelatedException;
import cpta.exceptions.InvalidFileTypeException;
import cpta.exceptions.RunTimeErrorException;

import java.io.*;
import java.util.*;

public class Grader {
    Compiler compiler;
    Executer executer;

    public Grader(Compiler compiler, Executer executer) {
        this.compiler = compiler;
        this.executer = executer;
    }

     public void compile (String path) throws InvalidFileTypeException, FileSystemRelatedException, CompileErrorException {
        compiler.compile(path);
     }

     public void execute(String targetFile, String inputFile, String outputFile) throws InvalidFileTypeException, FileSystemRelatedException, RunTimeErrorException {
        executer.execute(targetFile, inputFile, outputFile);
     }

    public Map<String,Map<String, List<Double>>> gradeSimple(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-1

        ArrayList<Student> studentList=new ArrayList<>(examSpec.students);
        ArrayList<Problem> problemList=new ArrayList<>(examSpec.problems);
        HashMap<String, Map<String, List<Double>>> finalList=new HashMap<>();
        for( int i=0; i<studentList.size();i++) {
            HashMap<String, List<Double>> eachStudent=new HashMap<>();
            Student currentStudent=studentList.get(i);
            for(int j=0; j<problemList.size(); j++) {
                ArrayList<Double> eachGrade=new ArrayList<>();
                Problem currentProblem = problemList.get(j);
                ArrayList<TestCase> testCase=new ArrayList(currentProblem.testCases);



                try {
                    compiler.compile(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                            currentProblem.targetFileName);

                } catch (FileSystemRelatedException e) {
                    e.printStackTrace();
                } catch (InvalidFileTypeException e) {
                    e.printStackTrace();
                } catch (CompileErrorException e) {
                    e.printStackTrace();
                }
                for(int k=0; k<testCase.size(); k++) {


                    String testLine;
                    String compareLine;
                    String string=null;
                    String compareString = null;
                    TestCase currentTest = testCase.get(k);
                    FileReader caseRead;
                    FileReader realRead;
                    BufferedReader bufferRead;
                    BufferedReader newBuffer;
                    try {

                        String[] yofile=currentProblem.targetFileName.split("\\.");
                        executer.execute(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                        yofile[0]+".yo",currentProblem.testCasesDirPath + currentTest.inputFileName,
                                currentProblem.testCasesDirPath + currentTest.outputFileName+"output"
                        );
                        caseRead = new FileReader(currentProblem.testCasesDirPath + currentTest.outputFileName);
                        realRead = new FileReader(currentProblem.testCasesDirPath + currentTest.outputFileName+"output");
                        bufferRead = new BufferedReader(caseRead);
                        newBuffer=new BufferedReader(realRead);

                        while ((testLine = bufferRead.readLine()) != null) {

                            compareString=compareString+testLine+"\n";
                        }
                        while ((compareLine = newBuffer.readLine()) != null) {

                            string=string+compareLine+"\n";
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (RunTimeErrorException e) {
                        e.printStackTrace();
                    } catch (FileSystemRelatedException e) {
                        e.printStackTrace();
                    } catch (InvalidFileTypeException e) {
                        e.printStackTrace();
                    }

                    if (compareString.equals(string)){
                        eachGrade.add(currentTest.score);
                    }
                    else{
                        eachGrade.add(0.0);
                    }

                }

                eachStudent.put(currentProblem.id, eachGrade);
            }

            finalList.put(currentStudent.id, eachStudent);

        }
        return finalList;
    }

    public Map<String,Map<String, List<Double>>> gradeRobust(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-2
        ArrayList<Student> studentList = new ArrayList<>(examSpec.students);
        ArrayList<Problem> problemList = new ArrayList<>(examSpec.problems);
        HashMap<String, Map<String, List<Double>>> finalList = new HashMap<>();

        for (int i = 0; i < studentList.size(); i++) {

            HashMap<String, List<Double>> eachStudent = new HashMap<>();
            Student currentStudent = studentList.get(i);
            for (int j = 0; j < problemList.size(); j++) {
                boolean noSubmission = false;
                boolean onlyyofile = false;
                ArrayList<Double> eachGrade = new ArrayList<>();
                Problem currentProblem = problemList.get(j);
                ArrayList<TestCase> testCase = new ArrayList(currentProblem.testCases);
                boolean compileError = false;

                File f = null;
                File[] file = new File(submissionDirPath).listFiles();
                if (file != null) {
                    for (File a_file : file) {
                        if (a_file.getName().substring(0, 10).equals(currentStudent.id) && a_file.getName().length() >= 10) {
                            try {
                                boolean a = a_file.renameTo(new File(submissionDirPath + currentStudent.id));
                            } catch (Exception e) {

                            }
                        }
                    }
                }
                if (currentProblem.wrappersDirPath != null) {
                    File previousFile = new File(currentProblem.wrappersDirPath);
                    File[] movingFile = null;
                    if (previousFile != null) {
                        movingFile = previousFile.listFiles();
                    }
                    File[] insideDirectory = null;

                    FileInputStream first = null;
                    FileWriter second = null;
                    for (int tracing = 0; tracing < movingFile.length; tracing++) {
                            try {
                                first = new FileInputStream(movingFile[tracing].getPath());
                                boolean exist = false;
                                File hi = new File(submissionDirPath + currentStudent.id+"/"+currentProblem.id);
                                File[] newFile = null;
                                if (hi.exists()) {
                                    newFile = new File(submissionDirPath + currentStudent.id).listFiles();
                                }

                                if (hi == null || !hi.exists()) {
                                    File making = new File(submissionDirPath + currentStudent.id + "/" +
                                            currentProblem.id);
                                    making.mkdirs();
                                }
                                second = new FileWriter(submissionDirPath + currentStudent.id +
                                        "/" + currentProblem.id + "/" + movingFile[tracing].getName());
                                String writing = "";
                                int in;
                                while ((in = first.read()) != -1) {
                                    writing += (char) in;
                                }
                                second.write(writing);
                            } catch (FileNotFoundException e) {
                                compileError=true;
                            } catch (IOException e) {
                                compileError=true;
                            } catch (NullPointerException e) {
                                compileError=true;
                            } finally {
                                if (first != null) {
                                    try {
                                        first.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (second != null) {
                                    try {
                                        second.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                    }


                }
                String yofilepath="";
                try {
                    File newly = new File(submissionDirPath + currentStudent.id + "/" + currentProblem.id);
                    File[] listed = null;
                    if (newly != null && newly.exists()) {
                        listed = newly.listFiles();
                    }
                    if (newly != null) {
                        if (!newly.exists()) {
                            noSubmission = true;
                        }
                    }
                    if (listed != null || listed.length!=0) {
                        for (File ff : listed) {
                            if (ff != null) {
                                if (ff.isDirectory()) {
                                    File[] inin = ff.listFiles();
                                    if (inin != null) {
                                        for (File file1 : inin) {
                                            if (file1 != null) {
                                                boolean a = file1.renameTo(new File(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                                        file1.getName()));
                                                if (a == false) {
                                                    File deleting = new File(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                                            file1.getName());
                                                    deleting.delete();
                                                    file1.renameTo(new File(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                                            file1.getName()));
                                                }
                                            }

                                        }
                                          }
                                }
                            }
                        }
                    }



                    File fn = new File(submissionDirPath + currentStudent.id + "/" + currentProblem.id);

                    boolean bothyo=false;
                    boolean bothsugo = false;
                    if (fn.exists()) {
                         bothyo = false;
                         bothsugo = false;
                        ArrayList<String> sugoname=new ArrayList<>();
                        ArrayList<String> yoname=new ArrayList<>();

                        File[] filing = fn.listFiles();
                        if (filing != null) {
                            for (File fi : filing) {
                                if (fi.getName().contains(".sugo")) {
                                    bothsugo = true;
                                    sugoname.add(fi.getName().split("\\.")[0]);
                                }
                                if (fi.getName().contains(".yo")) {
                                    bothyo=true;
                                    yoname.add(fi.getName().split("\\.")[0]);
                                }
                            }
                        }
                        for(String str: yoname){
                            if (sugoname.contains(str)){
                                File fil=new File(submissionDirPath + currentStudent.id +
                                        "/" + currentProblem.id+"/"+str+".yo");
                                        fil.delete();
                                    }
                                    else{
                                        onlyyofile=true;
                                       yofilepath=submissionDirPath + currentStudent.id +
                                               "/" + currentProblem.id+"/"+str+".yo";
                                    }
                                }
                            }



                    File[] newlist =null;

                    if (fn!=null &&fn.exists()){
                        newlist=fn.listFiles();}


                    if (newlist == null || newlist.length == 0) {
                        noSubmission = true;
                    }

                    if(!onlyyofile &&!noSubmission) {
                        compile(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                currentProblem.targetFileName);
                    }
                    if (onlyyofile){
                        if (bothsugo){
                            compile(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                    currentProblem.targetFileName);
                        }
                    }



                    if (newlist != null) {
                        for (int z = 0; z < newlist.length; z++) {
                            if (newlist[z]!=null){
                            if (newlist[z].getName().contains(".sugo")) {
                                if (!((submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                        currentProblem.targetFileName).equals(
                                        submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                                newlist[z].getName()))) {
                                    try {
                                        compile(newlist[z].getPath());
                                    } catch (FileSystemRelatedException e) {
                                        compileError = true;
                                    } catch (InvalidFileTypeException e) {
                                        compileError = true;
                                    } catch (CompileErrorException e) {
                                        compileError = true;
                                    }
                                }
                            }}
                        }
                    }

                } catch (FileSystemRelatedException e) {

                        noSubmission = true;

                } catch (InvalidFileTypeException e) {

                        noSubmission = true;

                } catch (CompileErrorException e) {
                    compileError = true;
                } catch (NullPointerException e) {

                }

                for (int k = 0; k < testCase.size(); k++) {
                    boolean runtimeError = false;

                    String testLine;
                    String compareLine;
                    String string = "";
                    String compareString = "";
                    TestCase currentTest = testCase.get(k);
                    FileReader caseRead = null;
                    FileReader realRead = null;
                    BufferedReader bufferRead = null;
                    BufferedReader newBuffer = null;
                    try {

                        String[] yofile = currentProblem.targetFileName.split("\\.");
                        if (!noSubmission && !compileError) {
                            if (onlyyofile){
                                execute(yofilepath, currentProblem.testCasesDirPath + currentTest.inputFileName,
                                        currentProblem.testCasesDirPath + currentTest.outputFileName + "output.txt");
                            }
                            else {
                                execute(submissionDirPath + currentStudent.id + "/" + currentProblem.id + "/" +
                                                yofile[0] + ".yo", currentProblem.testCasesDirPath + currentTest.inputFileName,
                                        currentProblem.testCasesDirPath + currentTest.outputFileName + "output.txt");
                            }
                            caseRead = new FileReader(currentProblem.testCasesDirPath + currentTest.outputFileName);
                            realRead = new FileReader(currentProblem.testCasesDirPath + currentTest.outputFileName + "output.txt");
                            bufferRead = new BufferedReader(caseRead);
                            newBuffer = new BufferedReader(realRead);


                            while ((testLine = bufferRead.readLine()) != null) {

                                compareString = compareString + testLine;
                            }
                            while ((compareLine = newBuffer.readLine()) != null) {

                                string = string + compareLine;
                            }
                        }
                    } catch (RunTimeErrorException e) {
                        runtimeError = true;
                    }
                    catch (InvalidFileTypeException e) {
                        runtimeError = true;
                    }
                    catch (FileSystemRelatedException e) {
                        runtimeError = true;
                    } catch (FileNotFoundException e) {
                        runtimeError = true;
                    }  catch (IOException e) {
                        compileError = true;
                    } finally {
                        try {
                            if (caseRead != null) {
                                caseRead.close();
                            }
                            if (realRead != null) {
                                realRead.close();
                            }
                            if (bufferRead != null) {
                                bufferRead.close();
                            }
                            if (newBuffer != null) {
                                newBuffer.close();
                            }
                        } catch (IOException e) {
                            compileError = true;
                        }

                    }
                    if (compileError){
                        eachGrade.add(0.0);
                    }
                    else if (noSubmission){
                        eachGrade.add(0.0);
                    }
                    else if (!runtimeError && compareString.equals(string)) {
                        if (onlyyofile) {
                            eachGrade.add(currentTest.score * 0.5);
                        } else {
                            eachGrade.add(currentTest.score);
                        }
                    } else if (runtimeError) {
                        eachGrade.add(0.0);
                    } else {
                        String tmpString = string;
                        String tmpCompareString = compareString;
                      if (currentProblem.judgingTypes!=null){
                        if (currentProblem.judgingTypes.contains("trailing-whitespaces")) {

                            for (int h = compareString.length() - 1; h >= 0; h--) {
                                if (compareString.charAt(h) == ' ' || compareString.charAt(h) == '\n' || compareString.charAt(h) == '\t') {
                                    tmpCompareString = compareString.substring(0, h);
                                } else {
                                    break;
                                }
                            }
                            for (int h = string.length() - 1; h >= 0; h--) {
                                if (string.charAt(h) == ' ' || string.charAt(h) == '\n' || string.charAt(h) == '\t') {
                                    tmpString = string.substring(0, h);
                                } else {
                                    break;
                                }
                            }

                        }

                        if (currentProblem.judgingTypes.contains("ignore-whitespaces")) {
                            tmpString = tmpString.replace(" ", "");
                            tmpString = tmpString.replace("\n", "");
                            tmpString = tmpString.replace("\t", "");
                            tmpCompareString = tmpCompareString.replace(" ", "");
                            tmpCompareString = tmpCompareString.replace("\n", "");
                            tmpCompareString = tmpCompareString.replace("\t", "");

                        }
                        if (currentProblem.judgingTypes.contains("case-insensitive")) {
                            tmpString = tmpString.toLowerCase();
                            tmpCompareString = tmpCompareString.toLowerCase();

                        }}
                        if (noSubmission) {
                            eachGrade.add(0.0);
                        } else if (onlyyofile) {
                            eachGrade.add(currentTest.score * 0.5);
                        } else if (tmpCompareString.equals(tmpString)) {
                            eachGrade.add(currentTest.score);
                        } else {
                            eachGrade.add(0.0);
                        }
                    }


                }
                eachStudent.put(currentProblem.id, eachGrade);




            }

            finalList.put(currentStudent.id, eachStudent);

        }
        return finalList;
    }}

