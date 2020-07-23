package org.jige.service;

import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jige.bean.ControllerItem;
import org.jige.ui.MyUrlTable;
import org.jige.ui.MyUrlTable2;
import org.jige.util.MyKeyListener;
import org.jige.util.StringTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PopupDisplay {
    JTextField searchTextField = new JTextField();

    List<ControllerItem> data = new ArrayList<>();
    MyUrlTable myTable = new MyUrlTable2();
    boolean inputListenerAdded = false;

    //搜索过程中的显示
    public void setLoading() {
        data.clear();
        data.add(new ControllerItem());
        myTable.setLoading();
    }

    //搜索完成的显示
    public void setListData(String searchText, List<ControllerItem> dataIn) {
        StringTools.log("dataIn size: ", dataIn.size());
        data.clear();
        data.addAll(dataIn);
        myTable.setListData(searchText, dataIn);
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
//        showResultList.setModel(new DefaultListModel<>());
        if (!inputListenerAdded) {
            inputListenerAdded = true;
            //监听输入框事件
            searchTextField.addKeyListener(new MyKeyListener() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String text = searchTextField.getText();
                    searchAction.accept(text);
                }
            });
        }

        JPanel panel = myTable.initPanel(selected -> {
            StringTools.log("ListSelection index ", selected);
            naviToCode.accept(data.get(selected));
        }, searchTextField);

        ComponentPopupBuilder builder = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, searchTextField)
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
