package OnlineJudge.OnlineJudge.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;

@Service
public class CompilerService {

    @Value("${file.upload-dir}")
    private String sourceDirectoryPath;

    @Value("${file.compiled-dir}")
    private String compiledDirectoryPath;
    public String  saveCodeToFileAndExecudeCode(String code,String language){


        // Create Source  directory if it does not exist
        File sourceDir = new File(sourceDirectoryPath);
        if(! sourceDir.exists() && !sourceDir.mkdir()){
            return "Error : Could not create source directory";
        }

        // Create compiled  directory if it does not exist
        File compiledDir = new File(compiledDirectoryPath);
        if (!compiledDir.exists() && !compiledDir.mkdirs()) {
            return "Error: Failed to create compiled directory.";
        }

        // Generate a unique class name and file name
        String className = "Code_" + UUID.randomUUID().toString().replace("-", "");
        String fileName = className + "." + language;
        // Store the code in a file in the source directory
        File sourceFile = new File(sourceDir, fileName);

        // Replace the class name placeholder in the user-provided code
        if(language.equals("java")){
            String regex = "class\\s+\\w+";
            code = code.replaceFirst(regex, "class " + className) ;

        }

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile));
            writer.write(code);
            writer.close();
        }
        catch (Exception e) {

            e.printStackTrace();
            return "Error: Failed to save code to file.";
        }


        if(language.equals("java")){
            return  exceuteJavaCode(sourceFile,compiledDir,className);
        }
        else if(language.equals("cpp")){
            return  executeCppCode(sourceFile,compiledDir,className);
        }
        return "Failed to save and run the code ";
    }



    public String exceuteJavaCode(File sourceFile, File compiledDir,String className) {

        // Compile the Java file using the Java Compiler (javac)
        String compileCommand="javac -d \"" + compiledDir.getAbsolutePath() + "\" \"" + sourceFile.getAbsolutePath() + "\"";
        try{
            Process compileProcess = Runtime.getRuntime().exec(compileCommand);
            int compileResult=compileProcess.waitFor();
            if(compileResult!=0){
                return "Error: Compilation failed.";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Run the compiled class file using the Java runtime (java)
        //String className=sourceFile.getName().replace(".java","");
        String runCommand="java -cp \""+compiledDir.getAbsolutePath()+"\" "+className;
        try{
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            int runResult=runProcess.waitFor();
            if(runResult!=0){
                return "Error: Failed to execute the code.";
            }

            // Capture the output of the program
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line=reader.readLine())!=null){
                output.append(line).append("\n");
            }
            return "Output of the executed code:\n" + output.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to execute the code.";
        }
    }
    public String executeCppCode(File sourceFile, File compiledDir, String executableName) {

        // Compile the C++ file
        String compileCommand = "g++ \"" + sourceFile.getAbsolutePath() + "\" -o \"" + compiledDir.getAbsolutePath() + File.separator + executableName + "\"";
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCommand);
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                return "Error: Compilation failed.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to compile C++ code.";
        }

        // Run the compiled executable
        String runCommand = compiledDir.getAbsolutePath() + File.separator + executableName;
        try {
            Process runProcess = Runtime.getRuntime().exec(runCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return "Output of the executed C++ code:\n" + output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to execute C++ code.";
        }
    }

}
