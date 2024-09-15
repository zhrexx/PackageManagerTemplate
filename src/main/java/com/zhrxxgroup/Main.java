package com.zhrxxgroup;

import com.zhrxxgroup.core.Downloader;
import com.zhrxxgroup.core.Packages;
import com.zhrxxgroup.core.config;
import com.zhrxxgroup.core.errors.ArgumentHandlingError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final Downloader downloader = new Downloader();
    private static final File configFile = new File(System.getProperty("user.home") + File.separator + config.PMN + File.separator + "c.txt");
    private static final String configDirPath = System.getProperty("user.home") + File.separator + config.PMN;
    private static final File configDir = new File(configDirPath);

    public static void main(String[] args) {
        try {
            setupConfig();
            handleUserInput(args);
        } catch (ArgumentHandlingError | IOException e) {
            System.err.println(config.prefix + "Error: " + e.getMessage());
        }
    }

    private static void setupConfig() throws IOException {
        if (!configDir.exists()) {
            if (!configDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + configDirPath);
            }
        }

        if (!configFile.exists()) {
            configFile.createNewFile();
            try (Scanner scanner = new Scanner(System.in);
                 FileWriter writer = new FileWriter(configFile)) {

                System.out.println(config.prefix + "Select a language");
                System.out.println("\n[english | russian]: ");
                String lang = scanner.nextLine().trim().toLowerCase();
                if ("russian".equals(lang) || "ru".equals(lang)) {
                    writer.write("russian");
                } else {
                    writer.write("english");
                }
            }
        }
    }

    private static void handleUserInput(String[] args) throws ArgumentHandlingError, IOException {
        String language;
        try (Scanner fileScanner = new Scanner(configFile)) {
            if (fileScanner.hasNextLine()) {
                language = fileScanner.nextLine().trim().toLowerCase();
            } else {
                throw new IOException("Config file is empty.");
            }
        }

        if ("russian".equals(language) || "english".equals(language)) {
            if (args.length < 1) {
                try (Scanner inputScanner = new Scanner(System.in)) {
                    promptForAction(inputScanner, language);
                }
            } else {
                handleAction(args[0].toLowerCase(), args, language);
            }
        } else {
            System.err.println(config.prefix + "Error: Unknown language in config file.");
        }
    }

    private static void promptForAction(Scanner scanner, String language) throws ArgumentHandlingError, IOException {
        System.out.println(config.prefix + (language.equals("russian") ? config.ru_followInstructions_message : config.en_followInstructions_message));
        System.out.println("\n[delete | install]: ");
        if (scanner.hasNextLine()) {
            String action = scanner.nextLine().trim().toLowerCase();
            handleAction(action, scanner, language);
        } else {
            throw new ArgumentHandlingError("No input provided.");
        }
    }

    private static void handleAction(String action, Scanner scanner, String language) throws ArgumentHandlingError, IOException {
        switch (action) {
            case "install":
                System.out.print("\n" + (language.equals("russian") ? config.ru_searched_package_message : config.en_searched_package_message));
                String packageName = scanner.nextLine().trim();

                System.out.print("\n" + (language.equals("russian") ? config.ru_searched_package_version : config.en_searched_package_version));
                double packageVersion = parseVersion(scanner.nextLine().trim());

                System.out.print("\n" + (language.equals("russian") ? config.ru_searched_package_author : config.en_searched_package_author));
                String packageAuthor = scanner.nextLine().trim();

                downloader.download(packageName, packageVersion, packageAuthor);
                break;

            case "delete":
                System.out.print("\n" + (language.equals("russian") ? config.ru_searched_package_message : config.en_searched_package_message));
                String delPackageName = scanner.nextLine().trim();

                System.out.print("\n" + (language.equals("russian") ? config.ru_searched_package_version : config.en_searched_package_version));
                double delPackageVersion = parseVersion(scanner.nextLine().trim());

                new Packages().delete(delPackageName, delPackageVersion);
                break;

            default:
                throw new ArgumentHandlingError("Unknown action: " + action);
        }
    }

    private static void handleAction(String action, String[] args, String language) throws ArgumentHandlingError, IOException {
        switch (action) {
            case "install":
                if (args.length < 4) {
                    throw new ArgumentHandlingError("Insufficient arguments for install. Usage: install <package_name> <version> [author]");
                }
                String packageName = args[1];
                double packageVersion = parseVersion(args[2]);
                String packageAuthor = args.length > 3 ? args[3] : "defaultAuthor";
                downloader.download(packageName, packageVersion, packageAuthor);
                break;

            case "delete":
                if (args.length < 3) {
                    throw new ArgumentHandlingError("Insufficient arguments for delete. Usage: delete <package_name> <version>");
                }
                String delPackageName = args[1];
                double delPackageVersion = parseVersion(args[2]);
                new Packages().delete(delPackageName, delPackageVersion);
                break;

            default:
                throw new ArgumentHandlingError("Unknown action: " + action);
        }
    }

    private static double parseVersion(String versionStr) throws ArgumentHandlingError {
        try {
            return Double.parseDouble(versionStr);
        } catch (NumberFormatException e) {
            throw new ArgumentHandlingError("Invalid version number.");
        }
    }
}
