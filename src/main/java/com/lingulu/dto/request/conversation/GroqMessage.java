package com.lingulu.dto.request.conversation;

public class GroqMessage {

    private String role;
    private String content;

    public GroqMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
