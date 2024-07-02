import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeExecutor {

    private static CodeExecutor instance;

    private CodeExecutor() {}

    public static synchronized CodeExecutor getInstance() {
        if (instance == null) {
            instance = new CodeExecutor();
        }
        return instance;
    }

    public String execute(String code) throws Exception {
        String fileName = "TempCode.java";
        Files.write(Paths.get(fileName), code.getBytes());

        Process compileProcess = Runtime.getRuntime().exec("javac " + fileName);
        compileProcess.waitFor();

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
        StringBuilder errors = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errors.append(line).append("\n");
        }

        if (errors.length() > 0) {
            return "Compilation Errors:\n" + errors.toString();
        }

        Process runProcess = Runtime.getRuntime().exec("java TempCode");
        runProcess.waitFor();

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        while ((line = outputReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        errorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }


}