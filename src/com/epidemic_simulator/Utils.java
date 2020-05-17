package com.epidemic_simulator;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.*;

public class Utils {
    /** Generate a random int between 0 and maxValue(excluded)
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int maxValue) {
        return random(0,maxValue);
    }

    /** Generate a random int between minValue(included) and maxValue(excluded)
     * @param minValue the minimum value to generate
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int minValue, int maxValue) {
        return minValue + ThreadLocalRandom.current().nextInt(maxValue-minValue);
    }


    /** Generate a boolean, that has *truePercentage*% possibilities of being true.
     * @param truePercentage The percentage rate for the value true.
     * @return the boolean generated.
     */
    public static boolean randomBool(int truePercentage){
        return random(0,100) < truePercentage;
    }

    /**
     * Get the list of classes from a package (inside this same project)
     * @param pkgName the name of the package that should be scanned
     * @return a list of classes
     * @throws IOException actually it shouldn't be thrown theoretically.
     * @throws URISyntaxException could be thrown if the package name is wrong
     */
    public static List<Class> getClassesForPackage(final String pkgName) throws IOException, URISyntaxException {
        final ArrayList<Class> allClasses = new ArrayList<>();

        //formatting package with / instead of . to get path
        final String pkgPath = pkgName.replace('.', '/');
        //getting absolute path on disk
        final URI pkg = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(pkgPath)).toURI();

        //getting root of the package (could be different from the path on disk if the program is being run from inside a jar)
        Path root;
        if (pkg.toString().startsWith("jar:")) {
            try {
                root = FileSystems.getFileSystem(pkg).getPath(pkgPath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(pkgPath);
            }
        } else {
            root = Paths.get(pkg);
        }


        //walk for all files
        try (final Stream<Path> allPaths = Files.walk(root).filter(Files::isRegularFile)) {
            //filter real files (exclude directories)
            allPaths.forEach(file -> {
                try {
                    //turn path back into package name
                    final String path = file.toString().replace('/', '.').replace('\\', '.');
                    final String name = path.substring(path.indexOf(pkgName), path.length() - ".class".length());
                    allClasses.add(Class.forName(name));
                } catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) {
                }
            });
        }
        return allClasses;
    }


    /**
     * This functions convert a method or a class name into a human readable string.
     * It only works with strings that do not contains special characters.
     * Example: animalCageBuilder => Animal cage builder
     * @param javaName the method or class name
     * @return the human readable string
     */
    public static String javaNameToUserString(String javaName) {
        String output = "";

        //removing package name
        int lastPoint;
        if((lastPoint = javaName.lastIndexOf('.'))>0)
            javaName = javaName.substring(lastPoint+1);

        //putting spaces between words
        for (int i = 0; i < javaName.length(); i++) {
            char charAtI = javaName.charAt(i);
            if(Character.isUpperCase(charAtI))
                output += " ";
            output += charAtI;
        }

        //removing first space
        if(output.indexOf(' ') == 0)
            output = output.substring(1);

        //making string lowercase except first character
        output = output.charAt(0) + output.substring(1).toLowerCase();

        return output;
    }

}
