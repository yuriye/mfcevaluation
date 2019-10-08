package ru.ys.mfc.view;

import javax.swing.*;
import java.awt.*;

public class ProgressFrame extends JFrame {
    private JLabel informStringLabel = new JLabel();
    private JPanel panel = new JPanel();

    public ProgressFrame(String title) {
        super(title);
//        this.setTitle("Оценка качеcтва оказания услуг");
        this.informStringLabel.setText("Опрос начался...");
        this.panel.setPreferredSize(new Dimension(400, 150));
        this.panel.add(this.informStringLabel);
        this.add(this.panel, "North");
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void setInformString(String informString) {
        this.informStringLabel.setText(informString);
    }
}
