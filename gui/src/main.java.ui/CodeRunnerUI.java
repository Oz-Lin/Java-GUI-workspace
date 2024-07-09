package main.java.ui;

import main.java.executor.CodeExecutionObserver;
import main.java.executor.CodeExecutor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CodeRunnerUI implements CodeExecutionObserver {
    private JFrame frame;
    private RSyntaxTextArea codeArea;
    private JTextArea outputArea;
    private JButton runButton;
    private final CodeExecutor codeExecutor;
    private final TemplateManager templateManager;

    public CodeRunnerUI() {
        codeExecutor = CodeExecutor.getInstance();
        codeExecutor.addObserver(this);
        templateManager = new TemplateManager();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Java Code Runner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        createMenuBar();

        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        codeArea = new RSyntaxTextArea(20, 50);
        codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeArea.setCodeFoldingEnabled(true);
        RTextScrollPane codeScrollPane = new RTextScrollPane(codeArea);
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

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveCode());

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> loadCode());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        JMenu templateMenu = new JMenu("Templates");
        JMenuItem helloWorldItem = new JMenuItem("Hello World");
        helloWorldItem.addActionListener(e -> loadTemplate("Hello World"));

        templateMenu.add(helloWorldItem);
        menuBar.add(templateMenu);

        frame.setJMenuBar(menuBar);
    }

    private void loadTemplate(String templateName) {
        String template = templateManager.getTemplate(templateName);
        if (template != null) {
            codeArea.setText(template);
        } else {
            outputArea.setText("Template not found: " + templateName);
        }
    }

    private void saveCode() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(codeArea.getText());
            } catch (IOException e) {
                outputArea.setText("Error saving file: " + e.getMessage());
            }
        }
    }

    private void loadCode() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                codeArea.read(reader, null);
            } catch (IOException e) {
                outputArea.setText("Error loading file: " + e.getMessage());
            }
        }
    }

    private void executeCode() {
        String code = codeArea.getText();
        if (!code.isEmpty()) {
            codeExecutor.execute(code);
        } else {
            outputArea.setText("Please enter some Java code to run.");
        }
    }

    @Override
    public void onExecutionComplete(String output) {
        SwingUtilities.invokeLater(() -> outputArea.setText(output));
    }

    @Override
    public void onExecutionError(String error) {
        SwingUtilities.invokeLater(() -> outputArea.setText(error));
    }
}