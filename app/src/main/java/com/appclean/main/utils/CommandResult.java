package com.appclean.main.utils;

public class CommandResult {
    public int result;
    public String successMsg;
    public String errorMsg;

    public CommandResult(int result, String successMsg, String errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "result=" + result +
                ", successMsg='" + successMsg + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
