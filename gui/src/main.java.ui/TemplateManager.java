package main.java.ui;

import java.util.HashMap;
import java.util.Map;

public class TemplateManager {
    private final Map<String, String> templates;

    public TemplateManager() {
        templates = new HashMap<>();
        loadTemplates();
    }

    private void loadTemplates() {
        templates.put("Hello World", "public class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n");
        // Add more templates as needed
    }

    public String getTemplate(String name) {
        return templates.get(name);
    }
}