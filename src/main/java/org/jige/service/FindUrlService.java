package org.jige.service;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class FindUrlService extends AnAction implements ChooseByNameContributorEx {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //获取当前在操作的工程上下文
        Project project = e.getData(PlatformDataKeys.PROJECT);
        createPopup("text").showInBestPositionFor(e.getDataContext());
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


    /**
     * https://www.programcreek.com/java-api-examples/index.php?api=com.intellij.openapi.ui.popup.JBPopup
     */
    @NotNull
    JBPopup createPopup(String currentText) {
        JTextField jTextField1 = new JTextField();
        JScrollPane jScrollPane1 = new JBScrollPane();
        JList<String> jList1 = new JBList<>();
        if (StringUtils.isNotBlank(currentText)) {
            jTextField1.setText(currentText);
        }
        JPanel panel = new JPanel(new BorderLayout());

        jScrollPane1.setViewportView(jList1);
        jList1.setModel(new javax.swing.AbstractListModel<>() {
            String[] strings = {};

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        //监听输入框事件
        jTextField1.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String text = jTextField1.getText();
                jList1.setListData(Arrays.asList(text + "1", text + "2", text + "3").toArray(new String[0]));
            }
        });
//        jTextField1.addActionListener(event -> {
//            String text = jTextField1.getText();
//            jList1.setListData(Arrays.asList(text + "1", text + "2", text + "3").toArray(new String[0]));
//        });
        //监听列表点击事件
        jList1.addListSelectionListener((event) -> {
            int firstIdx = event.getFirstIndex();
            int lastIdx = event.getFirstIndex();
        });
        /////////////////////////////////////////////////////////////
        // 构造弹出框 使用netbeans构造再copy
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField1)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
        );
        /////////////////////////////////////////////////////////////

        ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, null)
                .setCancelOnClickOutside(true)
                .setAdText(KeymapUtil.getShortcutsText(CommonShortcuts.CTRL_ENTER.getShortcuts()) + " to finish")
                .setRequestFocus(true)
                .setResizable(true)
                .setMayBeParent(true);

        final JBPopup popup = builder.createPopup();
        popup.setMinimumSize(new Dimension(200, 90));
        AnAction okAction = new DumbAwareAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                unregisterCustomShortcutSet(popup.getContent());
                popup.closeOk(e.getInputEvent());
            }
        };
        okAction.registerCustomShortcutSet(CommonShortcuts.CTRL_ENTER, popup.getContent());
        return popup;
    }

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope globalSearchScope, @Nullable IdFilter idFilter) {

    }

    @Override
    public void processElementsWithName(@NotNull String s, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters findSymbolParameters) {

    }
}
