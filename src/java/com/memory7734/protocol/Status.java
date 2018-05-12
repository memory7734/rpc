package com.memory7734.protocol;

public class Status {
    private String taskGroup;
    private String taskID;
    private String error;
    private Object result;
    private TaskStatus status;

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "taskGroup='" + taskGroup + '\'' +
                ", taskID='" + taskID + '\'' +
                ", error='" + error + '\'' +
                ", result=" + result +
                ", status=" + status +
                '}';
    }
}
