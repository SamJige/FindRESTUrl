package org.jige.test;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jige.bean.ControllerItem;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SearchTest extends LightJavaCodeInsightFixtureTestCase {
    //要查代码的目录
    public static String staticPath = "D:\\code\\csmp\\csmp-total\\common_csmp_rbac\\src\\main\\java\\qgs\\csmp\\info\\controller";

    @Override
    protected String getTestDataPath() {
        return staticPath;
    }

    //测试: 从java文件里面读取url
    @Test
    @Ignore("edit staticPath to test")
    public void test2() throws Exception {
        Path root = Path.of(staticPath);

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
//                                .peek(it -> StringTools.log("it3 ", it.toString()))
                .filter(it -> it.isGoodItem)
                .flatMap(ControllerItem::extractUrl)
                .forEach(it -> {
                    LoggerFactory.getLogger(getClass()).info("it -> {}", it);
                });
    }
}
