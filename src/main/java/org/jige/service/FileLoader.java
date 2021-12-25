package org.jige.service;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FileLoader {
    MyNotifier notifier = new MyNotifier();

    /**
     * 直接从java文件里面搜索
     */
    public void loadFile(Project project, Consumer<List<ControllerItem>> loadedAction) {
        //使用异步操作 避免阻塞
        CompletableFuture
                .supplyAsync(() -> {
                    notifier.notify(project, "start");
                    LoggerFactory.getLogger(getClass()).info("start -> {}", project.getName());
                    List<ControllerItem> fileResult = new ArrayList<>();
                    ReadAction.run(() -> {
//                        Collection<String> keys = ControllerIndex.getAllKeys(project);
//                        LoggerFactory.getLogger(getClass()).info("keys -> {}", keys.size());
                        long begin = System.currentTimeMillis();

                        /*
                        首先找到所有的java文件
                        然后对文件进行筛选 找到全部的java类 要有controller注解
                        然后找到controller类里面全部的方法 要有mapping注解
                         */
                        FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
                                .stream()
                                .map(it -> new ControllerItem((PsiJavaFile) PsiManager.getInstance(project).findFile(it), null, null))
                                .filter(file -> file.psiFile != null)
                                .flatMap(ControllerItem::genClass)
                                .flatMap(ControllerItem::genMethods)
//                                .peek(it -> StringTools.log("it3 ", it.toString()))
                                .filter(it -> it.isGoodItem)
                                .flatMap(ControllerItem::extractUrl)
                                .forEach(fileResult::add);

                        StringTools.log("supplyAsync size:", fileResult.size(), " time cost:", System.currentTimeMillis() - begin, "ms");
                        notifier.notify(project, "time cost(ms):" + (System.currentTimeMillis() - begin));
                    });
                    return fileResult;
                })
                .whenComplete((list, err) -> {
                    StringTools.log("whenComplete size:", list.size());
                    loadedAction.accept(list);
                });
    }
}
