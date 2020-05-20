package org.jige.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.sdk.language.SimpleFileType;
import org.intellij.sdk.language.psi.SimpleFile;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SearchFileService {

    public void searchFile(Project project) {
        LoggerFactory.getLogger(getClass()).info("start -> {}", project.getName());
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(SimpleFileType.INSTANCE, GlobalSearchScope.allScope(project));
        virtualFiles.stream().forEach(virtualFile -> {
            SimpleFile simpleFile = (SimpleFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                LoggerFactory.getLogger(getClass()).info("simpleFile.getName() -> {}", simpleFile.getName());
            }
        });

    }
}
