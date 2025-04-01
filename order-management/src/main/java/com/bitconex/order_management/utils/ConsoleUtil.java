package com.bitconex.order_management.utils;

public class ConsoleUtil {
    public static final String RESET = "\u001B[0m";  // Reset color
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✅ tesst " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "❌ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ️ " + message + RESET);
    }

    public static void print(String message) {
        System.out.println(CYAN + message + RESET);
    }
}
