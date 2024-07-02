import javax.swing.*;
import java.awt.*;

public class CodeRunnerUI {
    private JFrame frame;
    private JTextArea codeArea;
    private JTextArea outputArea;
    private JButton runButton;
    private CodeExecutor codeExecutor;

    public CodeRunnerUI() {
        codeExecutor = CodeExecutor.getInstance();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Java Code Runner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        codeArea = new JTextArea(20, 50);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeArea.setBorder(BorderFactory.createTitledBorder("Enter Java Code Here"));

        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputArea.setBorder(BorderFactory.createTitledBorder("Output"));

        runButton = new JButton("Run Code");
        runButton.addActionListener(e -> executeCode());

        pane.add(codeScrollPane, BorderLayout.CENTER);
        pane.add(outputScrollPane, BorderLayout.SOUTH);
        pane.add(runButton, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    private void executeCode() {
        String code = codeArea.getText();
        if (!code.isEmpty()) {
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return codeExecutor.execute(code);
                }

                @Override
                protected void done() {
                    try {
                        outputArea.setText(get());
                    } catch (Exception e) {
                        outputArea.setText("Error: " + e.getMessage());
                    }
                }
            }.execute();
        } else {
            outputArea.setText("Please enter some Java code to run.");
        }
    }
}