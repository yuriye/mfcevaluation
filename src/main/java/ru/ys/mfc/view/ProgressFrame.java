package ru.ys.mfc.view;

import ru.ys.mfc.Main;

import javax.swing.*;
import java.awt.*;

public class ProgressFrame extends JFrame {
    private JLabel informStringLabel = new JLabel();
    private JPanel panel = new JPanel();
    private JProgressBar progressBar = new JProgressBar();

    public ProgressFrame(String title) {
        super(title);
        this.setTitle("Оценка качеcтва оказания услуг для МКГУ");
        this.informStringLabel.setText("Опрос начался...");
        this.panel.setPreferredSize(new Dimension(300, 50));
        this.panel.add(this.informStringLabel);
        this.add(this.panel, BorderLayout.NORTH);
        this.progressBar.setMaximum(5);
        this.add(this.progressBar, BorderLayout.CENTER);
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener((actionEvent) -> {
            Main.exit(0);
        });
        this.add(closeButton, BorderLayout.EAST);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void increaseProgressBar(int size, String text) {
        this.progressBar.setValue(size);
    }

    public void setInformString(String informString) {
        this.informStringLabel.setText(informString);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
