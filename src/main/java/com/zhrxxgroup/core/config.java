package com.zhrxxgroup.core;

public class config {
    public static String PMN = "TEST"; // Package Manager Name
    public static String URL = "https://exemple.com/packages"; // Packages Download url (automatic will be added "URL + /<author>-<package_name>-<version>.zip" also it must be an POST)
    // path to store packages is ~/<PackageManagerName>/packages/
    public static String prefix = "[TEST] "; // Log Prefix

    public static String
            en_followInstructions_message = "Hey, follow the instructions",
            ru_followInstructions_message = "Привет, следуй инструкциям",

            en_searched_package_message = "Searched Package Name: ",
            en_searched_package_author = "Searched Package Author: ",
            en_searched_package_version = "Searched Package Version: ",

            ru_searched_package_message = "Название искомого Пакета: ",
            ru_searched_package_author = "Автор искомого Пакета: ",
            ru_searched_package_version = "Версия искомого Пакета: ";
}
