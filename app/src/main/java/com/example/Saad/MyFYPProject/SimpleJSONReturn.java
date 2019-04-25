package com.example.Saad.MyFYPProject;



public class SimpleJSONReturn {
    private String title;
    private String message;

    public SimpleJSONReturn(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
               return title;
    }

    public String getMessage() {
        return message;
    }
}
