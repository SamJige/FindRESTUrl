package org.jige.service;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class FindUrlService extends AnAction implements ChooseByNameContributorEx {
    FileLoader fileLoader = new FileLoader();
    UrlSearchMatcher urlSearchMatcher = new UrlSearchMatcher();
    PopupDisplay popupDisplay = new PopupDisplay();

    @Override
    public void actionPerformed(AnActionEvent e) {
        //获取当前在操作的工程上下文
        Project project = e.getData(PlatformDataKeys.PROJECT);

        popupDisplay.createPopup(
                "",
                searchText -> {
                    if (StringUtils.isBlank(searchText)) {
                        popupDisplay.setListData(Collections.emptyList());
                        return;
                    }
                    searchUrl(
                            project,
                            searchText,
                            list -> popupDisplay.setListData(list));
                },
                (selected) -> {
                    StringTools.log("selected: ", selected.toString());
                })
                .showInBestPositionFor(e.getDataContext());

        int listeners = popupDisplay.showResultList.getListSelectionListeners().length;
        StringTools.log("listeners ", listeners);
    }

    void searchUrl(Project project, String searchText, Consumer<List<ControllerItem>> searchFinishedAct) {
        fileLoader.loadFile(
                project,
                list -> {
                    StringTools.log("loadFile finished size:", list.size());
                    searchFinishedAct.accept(urlSearchMatcher.searchUrl(list, searchText));
                });
    }
//    @NotNull
//    JBPopup createPopupBak() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.add(myTextField, BorderLayout.CENTER);
//        ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, myTextField)
//                .setCancelOnClickOutside(true)
//                .setAdText(KeymapUtil.getShortcutsText(CommonShortcuts.CTRL_ENTER.getShortcuts()) + " to finish")
//                .setRequestFocus(true)
//                .setResizable(true)
//                .setMayBeParent(true);
//
//        final JBPopup popup = builder.createPopup();
//        popup.setMinimumSize(new Dimension(200, 90));
//        AnAction okAction = new DumbAwareAction() {
//            @Override
//            public void actionPerformed(@NotNull AnActionEvent e) {
//                unregisterCustomShortcutSet(popup.getContent());
//                popup.closeOk(e.getInputEvent());
//            }
//        };
//        okAction.registerCustomShortcutSet(CommonShortcuts.CTRL_ENTER, popup.getContent());
//        return popup;
//    }


    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope globalSearchScope, @Nullable IdFilter idFilter) {

    }

    @Override
    public void processElementsWithName(@NotNull String s, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters findSymbolParameters) {

    }
}
