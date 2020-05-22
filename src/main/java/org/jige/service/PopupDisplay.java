package org.jige.service;

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

    boolean listenerAdded = false;

    public void setListData(String searchText, List<ControllerItem> dataIn) {
        StringTools.log("dataIn size: ", dataIn.size());
        data.clear();
        data.addAll(dataIn);

        //todo 关键字高亮
        showResultList.setListData(data.stream()
                .map(it -> String.format("%s     #%s%s     %s",
                        it.url,
                        it.className,
                        StringUtils.isNotBlank(it.methodName) ? "." + it.methodName : "",
                        it.projectName))
                .toArray(String[]::new));
    }

    abstract public static class MyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        abstract public void keyReleased(KeyEvent e);
    }

    /**
     * https://www.programcreek.com/java-api-examples/index.php?api=com.intellij.openapi.ui.popup.JBPopup
     *
     * @param currentText  搜索
     * @param searchAction 搜索功能的入口 传入输入框的内容 输入框变化的时候触发
     * @param naviToCode   跳转功能的入口 传入选择的项 点击结果列表的似乎触发
     */
    @NotNull
    public JBPopup createPopup(String currentText, Consumer<String> searchAction, Consumer<ControllerItem> naviToCode) {
        if (StringUtils.isNotBlank(currentText)) {
            searchTextField.setText(currentText);
        }
        searchTextField.requestFocusInWindow();
        JPanel panel = new JPanel(new BorderLayout());

        jScrollPanel.setViewportView(showResultList);
        showResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        showResultList.setModel(new DefaultListModel<>());
        if (!listenerAdded) {
            listenerAdded = true;
            //监听输入框事件
            searchTextField.addKeyListener(new MyKeyListener() {

                @Override
                public void keyReleased(KeyEvent e) {
                    String text = searchTextField.getText();
                    searchAction.accept(text);
                }
            });
            //监听列表点击事件
            showResultList.addListSelectionListener((event) -> {
                if (!event.getValueIsAdjusting()) {
                    // 避免响应两次 https://blog.csdn.net/hepeng19861212/article/details/2121773
                    return;
                }
                int selected = showResultList.getSelectedIndex();
                StringTools.log("ListSelection index ", selected);
                naviToCode.accept(data.get(selected));
            });
        }
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
//                .setAdText(KeymapUtil.getShortcutsText(CommonShortcuts.CTRL_ENTER.getShortcuts()) + " to finish")
                .setRequestFocus(true)
                .setResizable(true)
                .setMayBeParent(true);

        final JBPopup popup = builder.createPopup();
        popup.setMinimumSize(new Dimension(200, 90));
//        AnAction okAction = new DumbAwareAction() {
//            @Override
//            public void actionPerformed(@NotNull AnActionEvent e) {
//                unregisterCustomShortcutSet(popup.getContent());
//                popup.closeOk(e.getInputEvent());
//            }
//        };
//        okAction.registerCustomShortcutSet(CommonShortcuts.CTRL_ENTER, popup.getContent());
        return popup;
    }
}
