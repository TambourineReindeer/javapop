/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 *
 * @author gef
 */
public class Helpers {

    /** 
     * Helper class for getClasses and getPackageNames (using Delegation)
     */
    private static File getClassesHelper(String pckgname)
            throws ClassNotFoundException {
        // Get a File object for the package
        File directory = null;
        
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
            return directory;
       
    }

    public static Class<?>[] getClasses(String pckgname, boolean skipDollarClasses) {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            File directory = getClassesHelper(pckgname);
System.out.println(directory);
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (int i = 0; i < files.length; i++) {
                    // we are only interested in .class files
                    if (files[i].endsWith(".class")) {
                        // get rid of the ".class" at the end
                        String withoutclass = pckgname + '.' + files[i].substring(0, files[i].length() - 6);

                        // in case we don't want $1 $2 etc. endings (i.e. common in GUI classes)
                        if (skipDollarClasses) {
                            int dollar_occurence = withoutclass.indexOf("$");
                            if (dollar_occurence != -1) {
                                withoutclass = withoutclass.substring(0, dollar_occurence);
                            }
                        }

                        // add this class to our list but avoid duplicates
                        boolean already_contained = false;
                        for (Class<?> c : classes) {
                            if (c.getCanonicalName().equals(withoutclass)) {
                                already_contained = true;
                            }
                        }
                        if (!already_contained) {
                            classes.add(Class.forName(withoutclass));
                        }
                    // REMARK this kind of checking is quite slow using reflection, it would be better
                    // to do the class.forName(...) stuff outside of this method and change the method
                    // to only return an ArrayList with fqcn Strings. Also in reality we have the $1 $2
                    // etc. classes in our packages, so we are skipping some "real" classes here
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Class<?>[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }

    public static ArrayList<String> getPackageNames(String basepkgname) {
        ArrayList<String> packages = new ArrayList<String>();
        try {
            File directory = getClassesHelper(basepkgname);
            if (directory.isDirectory() && directory.exists()) {
                for (File f : directory.listFiles()) {
                    if (f.isDirectory()) {
                        packages.add(basepkgname + "." + f.getName());
                    }
                }
            }
            return packages;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            // test getting classes
            Class[] ddd = getClasses("yourpackage.yoursubpackage", true);
            for (int i = 0; i < ddd.length; i++) {
                System.out.println(ddd[i].getCanonicalName());
            }

            // test getting packages
            ArrayList<String> packs = getPackageNames("yourpackage.yoursubpackage");
            for (String s : packs) {
                System.out.println(s);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}