package main.java.executor;

public interface CodeExecutionObserver {
    void onExecutionComplete(String output);
    void onExecutionError(String error);
}