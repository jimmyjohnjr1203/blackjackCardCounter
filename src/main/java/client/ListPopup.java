package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ListPopup {

    public static void showListPopup(Component parent, String title, java.util.List<String> items) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parent);

        JList<String> list = new JList<>(new DefaultListModel<>());
        DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
        for (String item : items) {
            model.addElement(item);
        }

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // double click to select
                    String selected = list.getSelectedValue();
                    System.out.println("Selected: " + selected);
                    dialog.dispose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    // Example usage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Main Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new FlowLayout());

            JButton showListButton = new JButton("Show List");
            showListButton.addActionListener(e -> {
                java.util.List<String> items = java.util.List.of("Alpha", "Bravo", "Charlie", "Delta");
                showListPopup(frame, "Choose an item", items);
            });

            frame.add(showListButton);
            frame.setVisible(true);
        });
    }
}
