package org.jige.test;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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

    //测试: 从java文件里面读取url
    public void test2() throws Exception {
        String fileContent = Files.readString(Path.of("C:\\Users\\jige1103\\Documents\\codes\\csmp_scaner_center\\src\\main\\java\\com\\qgs\\core\\controller\\Info2Controller4.java"));
        LoggerFactory.getLogger(getClass()).info("fileContent -> {}", fileContent);
        PsiFile myFile = PsiFileFactory.getInstance(getProject()).createFileFromText("Info2Controller4", StdFileTypes.JAVA, fileContent);
        LoggerFactory.getLogger(getClass()).info("myFile -> {}", myFile.getName());
        Stream.of(myFile)
                .map(it -> new ControllerItem((PsiJavaFile) it, null, null))
                .peek(it -> StringTools.log("it1 ", it.toString()))
                .filter(file -> file.psiFile != null)
                .peek(it -> StringTools.log("it2 ", it.toString()))
                .flatMap(ControllerItem::genClass)
                .flatMap(ControllerItem::genMethods)
                .peek(it -> StringTools.log("it3 ", it.toString()))
                .filter(it -> it.isGoodItem)
                .flatMap(ControllerItem::extractUrl)
                .forEach(it -> {
                    LoggerFactory.getLogger(getClass()).info("it -> {}", it.url);
                });
    }
}
