package org.jige.service;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchFileService {
    MyNotifier notifier = new MyNotifier();

    public static class ControllerItem {
        public PsiJavaFile psiFile;
        public PsiClass psiClass;
        public PsiMethod psiMethod;
        public Set<String> url = new HashSet<>(1);
        public boolean isGoodItem = false;

        public ControllerItem(PsiJavaFile psiFile, PsiClass psiClass, PsiMethod psiMethod) {
            this.psiFile = psiFile;
            this.psiClass = psiClass;
            this.psiMethod = psiMethod;
            findMyUrl();
        }

        /**
         * 满足条件的元素
         * 必须有函数
         * 1.controller有mapping 函数没有mapping
         * 2.controller有mapping 函数有mapping
         * 3.controller没有mapping 函数有mapping
         */
        public ControllerItem findMyUrl() {
            if (psiClass == null) {
                return this;
            }
            Set<String> controllerUrlList = new HashSet<>();
            Set<String> mtdUrlList = new HashSet<>();

            //找controller mapping
            Optional<PsiAnnotation> crlAnno = Stream.of(psiClass.getAnnotations())
                    .filter(Objects::nonNull)
                    .filter(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping"))
                    .findAny();
            if (crlAnno.isPresent()) {
                controllerUrlList = getUrlFromAnno(crlAnno.get());
            }
            //找method mapping
            if (psiMethod != null) {
                Optional<PsiAnnotation> mtdAnno = Stream.of(psiMethod.getAnnotations())
                        .filter(Objects::nonNull)
                        .filter(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping"))
                        .findAny();
                if (mtdAnno.isPresent()) {
                    mtdUrlList = getUrlFromAnno(mtdAnno.get());
                }
            }

            url = controllerUrlList;
            if (controllerUrlList.size() == 0) {
                url = mtdUrlList;
            } else {
                url.clear();
                if (mtdUrlList.size() == 0) {
                    mtdUrlList.add("/");
                }
                for (String controllerUrl : controllerUrlList) {
                    for (String mtdUrl : mtdUrlList) {
                        url.add((controllerUrl.endsWith("/") ? controllerUrl : controllerUrl + "/")
                                + (mtdUrl.startsWith("/") ? mtdUrl.substring(1) : mtdUrl));
                    }
                }
            }
            url = url.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            isGoodItem = psiMethod != null && url.size() > 0;
            return this;
        }

        public Stream<ControllerItem> genClass() {
            //只找 有controller 注解的类
            return Stream.of(psiFile.getClasses())
                    .map(clazz -> new ControllerItem(psiFile, clazz, null))
                    .filter(it -> it.psiClass != null)
                    .filter(it -> Stream.of(it.psiClass.getAnnotations())
                            .anyMatch(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Controller")));
        }

        public Stream<ControllerItem> genMethods() {
            return Stream.of(psiClass.getMethods())
                    .map(mtd -> new ControllerItem(psiFile, psiClass, mtd))
                    .filter(it -> it.psiMethod != null);
        }

        private Set<String> getUrlFromAnno(PsiAnnotation annotation) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
            if (value == null) {
                return Collections.emptySet();
            }
            return Stream.of(value.getChildren())
                    .map(PsiElement::getText)
                    .filter(StringUtils::isNotBlank)
                    .map(it -> {
                        if (it.startsWith("\"")) {
                            it = it.substring(1);
                        }
                        if (it.endsWith("\"")) {
                            it = it.substring(0, it.length() - 1);
                        }
                        return it;
                    })
                    .filter(it -> !"{".equals(it))
                    .filter(it -> !"}".equals(it))
                    .filter(it -> !",".equals(it))
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toSet());
        }

        public String toString() {
            return String.format("[%s] --> [%s.%s()] --> [file:%s]",
                    arrToString(url),
                    psiClass != null ? psiClass.getName() : "null",
                    psiMethod != null ? psiMethod.getName() : "null",
                    psiFile != null ? psiFile.getName() : "null");
        }
    }

    static String arrToString(Collection<String> collection) {
        return StringUtils.join(collection, "] [");
    }

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
