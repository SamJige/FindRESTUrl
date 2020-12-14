package org.jige.test;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FindJavaVisitor extends SimpleFileVisitor<Path> {
    public List<Path> result;

    public FindJavaVisitor(List<Path> result) {
        this.result = result;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(".java")) {
            result.add(file);
        }
        return FileVisitResult.CONTINUE;
    }
}