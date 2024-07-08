package main.java.executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeExecutor {

    private static CodeExecutor instance;
    private final List<CodeExecutionObserver> observers;

    private CodeExecutor() {
        observers = new ArrayList<>();
    }

    public static synchronized CodeExecutor getInstance() {
        if (instance == null) {
            instance = new CodeExecutor();
        }
        return instance;
    }

    public void addObserver(CodeExecutionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CodeExecutionObserver observer) {
        observers.remove(observer);
    }

    public void execute(String code) {
        new Thread(() -> {
            try {
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
                    notifyExecutionError("Compilation Errors:\n" + errors.toString());
                    return;
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

                notifyExecutionComplete(output.toString());
            } catch (Exception e) {
                notifyExecutionError("Error: " + e.getMessage());
            }
        }).start();
    }

    private void notifyExecutionComplete(String output) {
        for (CodeExecutionObserver observer : observers) {
            observer.onExecutionComplete(output);
        }
    }

    private void notifyExecutionError(String error) {
        for (CodeExecutionObserver observer : observers) {
            observer.onExecutionError(error);
        }
    }

}