package org.jige.bean;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerItem {
    public PsiJavaFile psiFile;
    public PsiClass psiClass;
    public PsiMethod psiMethod;
    public Set<String> url = new HashSet<>();
    public boolean isGoodItem = false;
    Set<String> controllerUrlList = new HashSet<>();
    Set<String> mtdUrlList = new HashSet<>();

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

        url.addAll(controllerUrlList);
        if (controllerUrlList.size() == 0) {
            url.clear();
            url.addAll(mtdUrlList);
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
        List<String> valueList =
                Stream.of(annotation.findAttributeValue("value"))
                        .map(it -> it.getText())
                        .peek(it -> System.out.println("value:" + it))
//                        .filter(it -> it.getAttributeName().equals("value"))
//                        .map(PsiNameValuePair::getLiteralValue)
                        .collect(Collectors.toList());
        if (valueList.size() == 0) {
            return Collections.emptySet();
        }
        return fixUrlStream(valueList.stream())
                .collect(Collectors.toSet());
    }

    public String toString() {
        return String.format("[%s.%s()] -->\t [file:%s] \tcontrollerUrlList:%s \tmtdUrlList:%s --> \t[%s] ",
                psiClass != null ? psiClass.getName() : "null",
                psiMethod != null ? psiMethod.getName() : "null",
                psiFile != null ? psiFile.getName() : "null",
                StringUtils.join(controllerUrlList, ","),
                StringUtils.join(mtdUrlList, ","), arrToString(url));
    }

    private String arrToString(Collection<String> collection) {
        return StringUtils.join(collection, "] [");
    }
}