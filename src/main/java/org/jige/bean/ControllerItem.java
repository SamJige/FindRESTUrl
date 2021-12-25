package org.jige.bean;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerItem {
    public PsiJavaFile psiFile;
    public PsiClass psiClass;
    public PsiMethod psiMethod;
    public Set<String> urls = new HashSet<>();
    public String url;
    public boolean isGoodItem = false;
    Set<String> controllerUrlList = new HashSet<>();
    Set<String> mtdUrlList = new HashSet<>();

    public String projectName;
    public String fileName;
    public String fileWithPath;
    public String className;
    public String methodName;

    public ControllerItem() {
    }

    public ControllerItem(PsiJavaFile psiFile, PsiClass psiClass, PsiMethod psiMethod) {
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.psiMethod = psiMethod;
        //fixme 子模块名称显示不出来
        projectName = psiClass != null ? psiClass.getProject().getName() : null;
        fileName = psiFile != null ? psiFile.getName() : null;
        fileWithPath = getFilePath();
        className = psiClass != null ? psiClass.getName() : null;
        methodName = psiMethod != null ? psiMethod.getName() : null;
        findMyUrl();
    }

    public ControllerItem(PsiJavaFile psiFile, PsiClass psiClass, PsiMethod psiMethod, String url) {
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.psiMethod = psiMethod;
        projectName = psiFile != null ? psiFile.getProject().getName() : null;
        fileName = psiFile != null ? psiFile.getName() : null;
        fileWithPath = getFilePath();
        className = psiClass != null ? psiClass.getName() : null;
        methodName = psiMethod != null ? psiMethod.getName() : null;
        this.url = url;
    }

    private String getFilePath() {
        String basePath = psiFile.getProject().getBasePath();
        VirtualFile vfile = psiFile.getVirtualFile();
        String filePath = "";
        if (vfile != null) {
            filePath = vfile.getPath();
        }
        if (StringUtils.isNotBlank(basePath)) {
            filePath = filePath.replace(basePath, "");
        }
        String[] pathList = StringUtils.split(filePath, "/");
        if (pathList != null && pathList.length >= 2) {
            filePath = pathList[0] + "/.../" + pathList[pathList.length - 1];
        }
        return psiFile != null ?
                (vfile != null ? filePath : fileName) : null;
    }

    public ControllerItem testInit(String testUrl) {
        url = testUrl;
        return this;
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

        //找controller mapping
        Stream.of(psiClass.getAnnotations())
                .filter(Objects::nonNull)
                .filter(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping"))
                .findAny()
                .ifPresent(psiAnnotation -> controllerUrlList.addAll(getUrlFromAnno(psiAnnotation)));
        Stream.of(psiClass.getAnnotations())
                .filter(Objects::nonNull)
                .filter(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Controller"))
                .findAny()
                .ifPresent(psiAnnotation -> controllerUrlList.addAll(getUrlFromAnno(psiAnnotation)));

        //找method mapping
        if (psiMethod != null) {
            Stream.of(psiMethod.getAnnotations())
                    .filter(Objects::nonNull)
                    .filter(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping"))
                    .findAny()
                    .ifPresent(psiAnnotation -> mtdUrlList.addAll(getUrlFromAnno(psiAnnotation)));
        }

        urls.addAll(controllerUrlList);
        if (controllerUrlList.size() == 0) {
            urls.clear();
            urls.addAll(mtdUrlList);
        } else {
            urls.clear();
            if (mtdUrlList.size() == 0) {
                mtdUrlList.add("/");
            }
            //如果controller里面有url配置 要跟函数里面的url 组合起来
            for (String controllerUrl : controllerUrlList) {
                for (String mtdUrl : mtdUrlList) {
                    urls.add((controllerUrl.endsWith("/") ? controllerUrl : controllerUrl + "/")
                            + (mtdUrl.startsWith("/") ? mtdUrl.substring(1) : mtdUrl));
                }
            }
        }
        urls = urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        isGoodItem = psiMethod != null && urls.size() > 0;
        return this;
    }

    //fixme 也可能被其他的注解干扰
    public Stream<ControllerItem> genClass() {
        //只找 有controller 注解的类
        return Stream.of(psiFile.getClasses())
                .filter(Objects::nonNull)
                //only public
                .filter(clazz -> clazz.hasModifierProperty(PsiModifier.PUBLIC))
                .filter(clazz -> Stream.of(clazz.getAnnotations())
                        .anyMatch(anno ->
                                Objects.requireNonNull(anno.getQualifiedName()).endsWith("Controller")
                                        || Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping")
                        ))
                .map(clazz -> new ControllerItem(psiFile, clazz, null));
    }

    //fixme 也可能被其他的注解干扰
    public Stream<ControllerItem> genMethods() {
        return Stream.of(psiClass.getMethods())
                .filter(Objects::nonNull)
                //only public
                .filter(mtd -> mtd.hasModifierProperty(PsiModifier.PUBLIC))
                .filter(mtd -> Stream.of(mtd.getAnnotations())
                        .anyMatch(anno -> Objects.requireNonNull(anno.getQualifiedName()).endsWith("Mapping")))
                .map(mtd -> new ControllerItem(psiFile, psiClass, mtd));
    }

    public Stream<ControllerItem> extractUrl() {
        return urls.stream()
                .map(it -> new ControllerItem(psiFile, psiClass, psiMethod, it));
    }

    /**
     * 从注解里面读取url
     * 包含几种情况
     * ("/xxx")
     * (value="/xxx")
     * ({"/xxx","/xxxx"})
     * (value={"/xxx","/xxxx"})
     */
    private Stream<String> fixUrlStream(Stream<String> rawUrl) {
        return rawUrl
                .filter(StringUtils::isNotBlank)
                .map(it -> {
                    if (it.startsWith("{") && it.endsWith("}")) {
                        it = it.substring(1, it.length() - 1);
                    }
                    return it;
                })
                .flatMap(it -> Stream.of(StringUtils.split(it, ",")))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(it -> {
                    if (it.startsWith("\"")) {
                        it = it.substring(1);
                    }
                    if (it.endsWith("\"")) {
                        it = it.substring(0, it.length() - 1);
                    }
                    return it;
                })
                .filter(StringUtils::isNotBlank)
                .distinct();
    }

    private Set<String> getUrlFromAnno(PsiAnnotation annotation) {
// @xxxMapping("xxxx")
// @xxxMapping(value = "xxxx")
        List<String> valueList = new ArrayList<>();
        valueList.addAll(Stream.of(annotation.findAttributeValue("value"))
                .filter(Objects::nonNull)
                .map(PsiElement::getText)
//                        .peek(it -> StringTools.log("value: ", it))
                .collect(Collectors.toList()));

//  @xxxMapping(path = "xxxx")
        valueList.addAll(Stream.of(annotation.findAttributeValue("path"))
                .filter(Objects::nonNull)
                .map(PsiElement::getText)
//                        .peek(it -> StringTools.log("value: ", it))
                .collect(Collectors.toList()));

        if (valueList.size() == 0) {
            return Collections.emptySet();
        }
        return fixUrlStream(valueList.stream())
                .collect(Collectors.toSet());
    }

    public String toString() {
        return String.format("%s : [%s.%s()] -->\t [file:%s] \tcontrollerUrlList:%s \tmtdUrlList:%s --> \t[%s]",
                url,
                className,
                methodName,
                fileWithPath,
                StringUtils.join(controllerUrlList, ","),
                StringUtils.join(mtdUrlList, ","), arrToString(urls));
    }

    private String arrToString(Collection<String> collection) {
        return StringUtils.join(collection, "] [");
    }
}