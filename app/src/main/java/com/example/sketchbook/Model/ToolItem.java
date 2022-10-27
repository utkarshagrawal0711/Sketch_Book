package com.example.sketchbook.Model;

public class ToolItem {
    private int toolNumber;
    private String toolName;

    public ToolItem(int toolNumber, String toolName) {
        this.toolNumber = toolNumber;
        this.toolName = toolName;
    }

    public int getToolNumber() {
        return toolNumber;
    }

    public void setToolNumber(int toolNumber) {
        this.toolNumber = toolNumber;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
