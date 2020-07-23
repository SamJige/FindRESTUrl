package org.jige.ui;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.lang3.StringUtils;
import org.jige.bean.ControllerItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class MyUrlTable1 implements MyUrlTable {
    JList<String> showResultList = new JBList<>();
    JBScrollPane jScrollPanel = new JBScrollPane();
    JPanel panel;

    public JPanel initPanel(Consumer<Integer> selectEventAct, JTextField searchTextField) {
        if (panel != null) {
            return panel;
        }
        panel = new JPanel(new BorderLayout());

        jScrollPanel.setViewportView(showResultList);
        showResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //监听列表点击事件
        showResultList.addListSelectionListener((event) -> {
            if (!event.getValueIsAdjusting()) {
                // 避免响应两次 https://blog.csdn.net/hepeng19861212/article/details/2121773
                return;
            }
            int selected = showResultList.getSelectedIndex();
            selectEventAct.accept(selected);
        });

        /////////////////////////////////////////////////////////////
        // 构造弹出框 使用netbeans构造再copy
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                        .addComponent(jScrollPanel)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
        );
        /////////////////////////////////////////////////////////////
        return panel;
    }

    public void setLoading() {
        showResultList.setListData(new String[]{"loading..."});
    }

    public void setListData(String searchText, List<ControllerItem> dataIn) {
        //todo 关键字高亮
        showResultList.setListData(dataIn.stream()
                .map(it -> String.format("%-80s %s#%s%s ",
                        it.url,
                        it.fileWithPath,
                        it.className,
                        StringUtils.isNotBlank(it.methodName) ? "." + it.methodName : ""))
                .toArray(String[]::new));
    }
}