package org.jige.service;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jige.bean.ControllerItem;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SearchFileService {
    MyNotifier notifier = new MyNotifier();

    /**
     * 直接从java文件里面搜索
     */
    public void searchFile(Project project) {
        //使用异步操作 避免阻塞
        CompletableFuture
                .supplyAsync(() -> {
                    notifier.notify(project, "start");
                    LoggerFactory.getLogger(getClass()).info("start -> {}", project.getName());
                    List<VirtualFile> virtualFiles = new ArrayList<>();
                    ReadAction.run(() -> {
//                        Collection<String> keys = ControllerIndex.getAllKeys(project);
//                        LoggerFactory.getLogger(getClass()).info("keys -> {}", keys.size());
                        long begin = new Date().getTime();

                        FileTypeIndex.getFiles(StdFileTypes.JAVA, GlobalSearchScope.projectScope(project))
                                .stream()
                                .map(it -> new ControllerItem((PsiJavaFile) PsiManager.getInstance(project).findFile(it), null, null))
                                .filter(file -> file.psiFile != null)
                                .flatMap(ControllerItem::genClass)
                                .flatMap(ControllerItem::genMethods)
                                .filter(it -> it.isGoodItem)
                                .forEach(it -> {
                                    notifier.notify(project, it.toString());
                                });

                        LoggerFactory.getLogger(getClass()).info("time cost -> {}ms", new Date().getTime() - begin);
                        notifier.notify(project, "time cost(ms):" + (new Date().getTime() - begin));
                    });
                    LoggerFactory.getLogger(getClass()).info("files count: -> {}", virtualFiles.size());
                    notifier.notify(project, "files count: ->" + virtualFiles.size());
                    return virtualFiles;
                })
                .whenComplete((list, err) ->
                        ReadAction.run(() -> list
                                .stream()
                                .map(it -> PsiManager.getInstance(project).findFile(it))
                                .filter(Objects::nonNull)
                                .map(PsiFileSystemItem::getName)
                                .collect(Collectors.toList())
                                .forEach(name -> {
                                    notifier.notify(project, "file:" + name);
                                    LoggerFactory.getLogger(getClass()).info("simpleFile.getName() -> {}", name);
                                })));
    }
}
