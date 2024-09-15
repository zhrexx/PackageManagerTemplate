package com.zhrxxgroup;

import com.zhrxxgroup.core.Downloader;
import com.zhrxxgroup.core.Packages;
import com.zhrxxgroup.core.config;
import com.zhrxxgroup.core.errors.ArgumentHandlingError;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static Downloader downloader = new Downloader();

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                Scanner scanner = new Scanner(System.in);

                System.out.println(config.prefix + "Hey, follow the instructions");
                System.out.println("\n[delete | install]: ");
                String action = scanner.nextLine().toLowerCase();
                handleAction(action, scanner);
            } else {
                String action = args[0].toLowerCase();
                handleAction(action, args);
            }
        } catch (ArgumentHandlingError | IOException e) {
            System.err.println(config.prefix + "Error: " + e.getMessage());
        }
    }

    private static void handleAction(String action, Scanner scanner) throws ArgumentHandlingError, IOException {
        switch (action) {
            case "install":
                System.out.print("\nSearched Package Name: ");
                String packageName = scanner.nextLine();

                System.out.print("\nSearched Package Version: ");
                double packageVersion = 0;
                try {
                    packageVersion = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new ArgumentHandlingError("Invalid version number.");
                }

                System.out.print("\nSearched Package Author: ");
                String packageAuthor = scanner.nextLine();

                downloader.download(packageName, packageVersion, packageAuthor);
                break;

            case "delete":
                System.out.print("\nSearched Package Name: ");
                String delPackageName = scanner.nextLine();

                System.out.print("\nSearched Package Version: ");
                double delPackageVersion = 0;
                try {
                    delPackageVersion = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new ArgumentHandlingError("Invalid version number.");
                }

                new Packages().delete(delPackageName, delPackageVersion);
                break;

            default:
                throw new ArgumentHandlingError("Unknown action: " + action);
        }
    }

    private static void handleAction(String action, String[] args) throws ArgumentHandlingError, IOException {
        switch (action) {
            case "install":
                if (args.length < 4) {
                    throw new ArgumentHandlingError("Insufficient arguments for install. Usage: install <package_name> <version> [author]");
                }
                String packageName = args[1];
                double packageVersion;
                try {
                    packageVersion = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    throw new ArgumentHandlingError("Invalid version number.");
                }
                String packageAuthor = args.length > 3 ? args[3] : "defaultAuthor";
                downloader.download(packageName, packageVersion, packageAuthor);
                break;

            case "delete":
                if (args.length < 3) {
                    throw new ArgumentHandlingError("Insufficient arguments for delete. Usage: delete <package_name> <version>");
                }
                String delPackageName = args[1];
                double delPackageVersion;
                try {
                    delPackageVersion = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    throw new ArgumentHandlingError("Invalid version number.");
                }
                new Packages().delete(delPackageName, delPackageVersion);
                break;

            default:
                throw new ArgumentHandlingError("Unknown action: " + action);
        }
    }
}
