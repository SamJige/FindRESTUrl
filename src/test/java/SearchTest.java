import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jige.service.SearchFileService;
import org.slf4j.LoggerFactory;

public class SearchTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "D:\\code\\csmp\\csmp_scaner_center\\src\\main\\java";
    }

    public void test1() {
        Project project = getProject();
        LoggerFactory.getLogger(getClass()).info("test1 -> start");
        SearchFileService searchFileService = new SearchFileService();
        searchFileService.searchFile(getProject());
    }
}
