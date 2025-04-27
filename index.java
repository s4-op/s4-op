import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Vector;

public class LibraryManagementSystemAdvanced extends JFrame {

    private JTextField idField, titleField, authorField;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private static final String FILE_NAME = "library_data.dat";

    public LibraryManagementSystemAdvanced() {
        setTitle("Library Management System - Advanced");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();

        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton issueButton = new JButton("Issue Book");
        JButton returnButton = new JButton("Return Book");
        JButton saveButton = new JButton("Save Data");
        JButton loadButton = new JButton("Load Data");
        JButton searchButton = new JButton("Search Book");
        JButton clearButton = new JButton("Clear All");
        JButton countButton = new JButton("Count Books");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(countButton);
        buttonPanel.add(refreshButton);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Status"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Adding to Frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addBook());
        deleteButton.addActionListener(e -> deleteBook());
        issueButton.addActionListener(e -> issueBook());
        returnButton.addActionListener(e -> returnBook());
        saveButton.addActionListener(e -> saveData());
        loadButton.addActionListener(e -> loadData());
        searchButton.addActionListener(e -> searchBook());
        clearButton.addActionListener(e -> clearAllBooks());
        countButton.addActionListener(e -> countBooks());
        refreshButton.addActionListener(e -> refreshTable());

        // Load data at start
        loadData();
    }

    private void addBook() {
        String id = idField.getText();
        String title = titleField.getText();
        String author = authorField.getText();

        if (!id.isEmpty() && !title.isEmpty() && !author.isEmpty()) {
            tableModel.addRow(new Object[]{id, title, author, "Available"});
            idField.setText("");
            titleField.setText("");
            authorField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete!");
        }
    }

    private void issueBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String status = (String) tableModel.getValueAt(selectedRow, 3);
            if (status.equals("Available")) {
                tableModel.setValueAt("Issued", selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(this, "Book is already issued!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to issue!");
        }
    }

    private void returnBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String status = (String) tableModel.getValueAt(selectedRow, 3);
            if (status.equals("Issued")) {
                tableModel.setValueAt("Available", selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(this, "Book is not issued!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to return!");
        }
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            Vector<Vector<Object>> dataVector = (Vector<Vector<Object>>) (Vector<?>) tableModel.getDataVector();
            out.writeObject(dataVector);
            JOptionPane.showMessageDialog(this, "Data saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data!");
            e.printStackTrace();
        }
    }

    private void loadData() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Vector<Vector<Object>> dataVector = (Vector<Vector<Object>>) in.readObject();
                for (Vector<Object> row : dataVector) {
                    tableModel.addRow(row);
                }
                JOptionPane.showMessageDialog(this, "Data loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading data!");
                e.printStackTrace();
            }
        }
    }

    private void searchBook() {
        String searchTitle = JOptionPane.showInputDialog(this, "Enter book title to search:");
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            boolean found = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String title = (String) tableModel.getValueAt(i, 1);
                if (title.equalsIgnoreCase(searchTitle.trim())) {
                    bookTable.setRowSelectionInterval(i, i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Book not found!");
            }
        }
    }

    private void clearAllBooks() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete ALL books?", "Warning", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
        }
    }

    private void countBooks() {
        int available = 0, issued = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String status = (String) tableModel.getValueAt(i, 3);
            if ("Available".equals(status)) {
                available++;
            } else if ("Issued".equals(status)) {
                issued++;
            }
        }
        JOptionPane.showMessageDialog(this, "Available: " + available + "\nIssued: " + issued);
    }

    private void refreshTable() {
        bookTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementSystemAdvanced().setVisible(true));
    }
}
