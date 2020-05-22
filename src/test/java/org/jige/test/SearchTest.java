package org.jige.test;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.slf4j.LoggerFactory;

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
}
