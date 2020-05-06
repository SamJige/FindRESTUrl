package org.jige.service;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jige.bean.ListItem;
import org.jige.bean.MyList;

import java.util.Arrays;

public class FindUrlService extends AnAction implements ChooseByNameContributorEx {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //获取当前在操作的工程上下文
        Project project = e.getData(PlatformDataKeys.PROJECT);
        MyList list = new MyList("Find Result", Arrays.asList(
                new ListItem("aa")
                , new ListItem("bb")
        ));
        JBPopupFactory.getInstance().createListPopup(list).showInBestPositionFor(e.getDataContext());
    }

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope globalSearchScope, @Nullable IdFilter idFilter) {

    }

    @Override
    public void processElementsWithName(@NotNull String s, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters findSymbolParameters) {

    }
}
