package com.example.op_time_ize.Model;

public class ToDoModel extends TaskId {

    private String task , due, due_time;
    private int status;

    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public String getDue_time(){
        return due_time;
    }

    public int getStatus() {
        return status;
    }
}
