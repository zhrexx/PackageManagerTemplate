package com.zhrxxgroup.core;

import java.io.File;

public class Packages {
    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }

        return dir.delete();
    }

    public boolean delete(String packageName, double version) {
        String path = System.getProperty("user.home") + File.separator + config.PMN + File.separator + "packages" + File.separator + packageName + "-" + version;
        File directory = new File(path);

        if (deleteDirectory(directory)) {
            System.out.println(config.prefix + "Directory deleted successfully: " + path);
            return true;
        } else {
            System.out.println(config.prefix + "Failed to delete the directory: " + path);
            return false;
        }
    }
}
