package org.jige.service;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PopupDisplay {
    JTextField searchTextField = new JTextField();
    JScrollPane jScrollPanel = new JBScrollPane();
    JList<String> showResultList = new JBList<>();

    List<ControllerItem> data = new ArrayList<>();

    public void setListData(List<ControllerItem> dataIn) {
        StringTools.log("dataIn size: ", dataIn.size());
        data.clear();
        data.addAll(dataIn);

        showResultList.setListData(data.stream()
                .map(it -> it.url).toArray(String[]::new));

    }

    /**
     * https://www.programcreek.com/java-api-examples/index.php?api=com.intellij.openapi.ui.popup.JBPopup
     */
    @NotNull
    public JBPopup createPopup(String currentText, Consumer<String> searchAction) {
        if (StringUtils.isNotBlank(currentText)) {
            searchTextField.setText(currentText);
        }
        JPanel panel = new JPanel(new BorderLayout());

        jScrollPanel.setViewportView(showResultList);
        showResultList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        showResultList.setModel(new DefaultListModel<>());
        //监听输入框事件
        searchTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchTextField.getText();
                showResultList.setListData(data.stream()
                        .map(it -> it.url).toArray(String[]::new));
                searchAction.accept(text);

            }
        });
        //监听列表点击事件
        showResultList.addListSelectionListener((event) -> {
            int firstIdx = event.getFirstIndex();
            int lastIdx = event.getFirstIndex();
        });
        /////////////////////////////////////////////////////////////
        // 构造弹出框 使用netbeans构造再copy
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(searchTextField)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
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
}
