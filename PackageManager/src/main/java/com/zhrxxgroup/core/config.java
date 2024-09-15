package com.zhrxxgroup.core;

public class config {
    public static String PMN = "TEST"; // Package Manager Name
    public static String URL = "https://exemple.com/packages"; // Packages Download url (automatic will be added "URL + /<author>-<package_name>-<version>.zip" also it must be an POST)
    // path to store packages is ~/<PackageManagerName>/packages/
    public static String prefix = "[TEST] "; // Log Prefix
}
