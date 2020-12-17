package org.jige.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * 构造一个索引 里面只包含controller
 * 先不用这种方式
 */
@Deprecated
public class ControllerIndex extends FileBasedIndexExtension<String, String> {
    @NonNls
    public static final ID<String, String> NAME = ID.create("org.jige.service.ControllerIndex");
    private final MyDataIndexer myDataIndexer = new MyDataIndexer();

    @NotNull
    public static Collection<VirtualFile> getFilesByKey(@NotNull String moduleName, @NotNull GlobalSearchScope scope) {
        return FileBasedIndex.getInstance().getContainingFiles(NAME, moduleName, scope);
    }

    @NotNull
    public static Collection<String> getAllKeys(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(NAME, project);
    }

    @Override
    @NotNull
    public ID<String, String> getName() {
        return NAME;
    }

    @Override
    @NotNull
    public DataIndexer<String, String, FileContent> getIndexer() {
        return myDataIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataExternalizer<String> getValueExternalizer() {
        return new DataExternalizer<>() {
            @Override
            public void save(@NotNull DataOutput dataOutput, String s) throws IOException {
                dataOutput.writeChars(s);
            }

            @Override
            public String read(@NotNull DataInput dataInput) throws IOException {
                List<String> lines = new ArrayList<>();
                for (; ; ) {
                    String line = dataInput.readLine();
                    if (StringUtils.isBlank(line)) {
                        break;
                    }
                    lines.add(line);
                }
                return StringUtils.join(lines, "");
            }
        };
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(JavaFileType.INSTANCE);
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    private static class MyDataIndexer implements DataIndexer<String, String, FileContent> {
        @Override
        @NotNull
        public Map<String, String> map(@NotNull final FileContent inputData) {
            PsiFile psiFile = inputData.getPsiFile();
            if (!(psiFile instanceof PsiJavaFile)) {
                LoggerFactory.getLogger(getClass()).info("error not java file name:{} type:{}",
                        inputData.getFileName(),
                        inputData.getFileType().getName());
                return Collections.emptyMap();
            }
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            PsiClass[] classList = javaFile.getClasses();

            Map<String, String> result = new HashMap<>();
            Stream.of(classList)
                    .peek(it -> LoggerFactory.getLogger(getClass()).info("list it -> {}", it.getName()))
                    .filter(it -> Stream.of(it.getAnnotations()).anyMatch(anno -> anno.getQualifiedName().contains("Controller")))
                    .peek(it -> LoggerFactory.getLogger(getClass()).info("filterd it -> {}", it.getName()))
                    .forEach(it -> {
                        result.put(it.getName(), it.getName());
                    });

            return result;
        }
    }

}
