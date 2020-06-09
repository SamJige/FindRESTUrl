package org.jige.test;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jige.bean.ControllerItem;
import org.slf4j.LoggerFactory;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SearchTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "C:\\Users\\jige1103\\Documents\\codes\\csmp_scaner_center\\src\\main\\java\\com\\qgs\\core\\controller";
    }

    public void test1() {
        myFixture.configureByFiles("InfoController.java");
        myFixture.complete(CompletionType.BASIC, 1);

        Project project = getProject();
        LoggerFactory.getLogger(getClass()).info("test1 -> start");
//        FileLoader fileLoader = new FileLoader();
//        fileLoader.loadFile(getProject());
    }

    public static class FindJavaVisitor extends SimpleFileVisitor<Path> {
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

    public static class MyContent {
        public String name;
        public String content;

        public MyContent(String name, String content) {
            this.name = name;
            this.content = content;
            LoggerFactory.getLogger(getClass()).info("load file -> {}", name);
        }
    }

    //测试: 从java文件里面读取url
    public void test2() throws Exception {
        Path root = Path.of("C:\\Users\\jige1103\\Documents\\codes\\csmp_scaner_center\\src\\main\\java");

        List<Path> result = new ArrayList<>();
        Files.walkFileTree(root, new FindJavaVisitor(result));
        List<MyContent> fileContentList = new ArrayList<>();
        for (Path path : result) {
            fileContentList.add(new MyContent(path.toFile().getName(), Files.readString(path)));
        }

        fileContentList.stream()
                .map(fileContent -> PsiFileFactory.getInstance(getProject()).createFileFromText(fileContent.name, StdFileTypes.JAVA, fileContent.content))
                .map(it -> new ControllerItem((PsiJavaFile) it, null, null))
                .filter(file -> file.psiFile != null)
                .flatMap(ControllerItem::genClass)
                .flatMap(ControllerItem::genMethods)
                .filter(it -> it.isGoodItem)
                .flatMap(ControllerItem::extractUrl)
                .forEach(it -> {
                    LoggerFactory.getLogger(getClass()).info("it -> {}", it);
                });
    }
}
