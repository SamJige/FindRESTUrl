package org.jige.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SearchFileService {
    MyNotifier notifier = new MyNotifier();

    public void searchFile(Project project) {
        notifier.notify(project, "start");
        LoggerFactory.getLogger(getClass()).info("start -> {}", project.getName());

        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.allScope(project));

        LoggerFactory.getLogger(getClass()).info("files count: -> {}", virtualFiles.size());
        notifier.notify(project, "files count: ->" + virtualFiles.size());

        virtualFiles.stream().forEach(virtualFile -> {
            PsiFile simpleFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                notifier.notify(project, "file:" + simpleFile.getName());
                LoggerFactory.getLogger(getClass()).info("simpleFile.getName() -> {}", simpleFile.getName());
            }
        });

    }
}
