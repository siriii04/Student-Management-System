import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGUI {
    private StudentManager manager;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField courseField;
    private JLabel statusLabel;

    public StudentGUI() {
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default
        }

        manager = new StudentManager();

        frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // Top panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View All");
        JButton searchBtn = new JButton("Search");
        JButton updateBtn = new JButton("Update Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton sortIdBtn = new JButton("Sort by ID");
        JButton sortNameBtn = new JButton("Sort by Name");
        JButton filterBtn = new JButton("Filter by Course");
        JButton clearBtn = new JButton("Clear Filter");

        buttonPanel.add(addBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(sortIdBtn);
        buttonPanel.add(sortNameBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(clearBtn);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.LIGHT_GRAY);
        searchPanel.add(new JLabel("Search (ID or Name):"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Course Filter:"));
        courseField = new JTextField(10);
        searchPanel.add(courseField);

        // Combine top panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Age", "Course", "Sub1", "Sub2", "Sub3", "Sub4", "Sub5", "Percentage %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230)); // Light blue selection
        JScrollPane scrollPane = new JScrollPane(table);

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setOpaque(true);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        // Action listeners
        addBtn.addActionListener(e -> addStudent());
        viewBtn.addActionListener(e -> viewStudents());
        searchBtn.addActionListener(e -> searchStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        sortIdBtn.addActionListener(e -> { manager.sortById(); viewStudents(); statusLabel.setText("Sorted by ID"); });
        sortNameBtn.addActionListener(e -> { manager.sortByName(); viewStudents(); statusLabel.setText("Sorted by Name"); });
        filterBtn.addActionListener(e -> filterByCourse());
        clearBtn.addActionListener(e -> viewStudents());

        // Initial view
        viewStudents();

        frame.setVisible(true);
    }

    private void addStudent() {
        try {
            String idStr = JOptionPane.showInputDialog(frame, "Enter ID:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr.trim());

            String name = JOptionPane.showInputDialog(frame, "Enter Name:");
            if (name == null) return;
            name = name.trim();

            String ageStr = JOptionPane.showInputDialog(frame, "Enter Age:");
            if (ageStr == null) return;
            int age = Integer.parseInt(ageStr.trim());

            String course = JOptionPane.showInputDialog(frame, "Enter Course:");
            if (course == null) return;
            course = course.trim();

            int[] marks = new int[5];
            for (int i = 0; i < 5; i++) {
                String markStr = JOptionPane.showInputDialog(frame, "Enter Mark for Subject " + (i + 1) + " (0-100):");
                if (markStr == null) return;
                marks[i] = Integer.parseInt(markStr.trim());
                if (marks[i] < 0 || marks[i] > 100) {
                    JOptionPane.showMessageDialog(frame, "Marks must be between 0 and 100!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Student s = new Student(id, name, age, course, marks);
            manager.addStudent(s);
            viewStudents();
            statusLabel.setText("Student added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid number input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudents() {
        tableModel.setRowCount(0);
        for (Student s : manager.getStudents()) {
            tableModel.addRow(new Object[]{s.id, s.name, s.age, s.course, s.marks[0], s.marks[1], s.marks[2], s.marks[3], s.marks[4], String.format("%.2f", s.percentage)});
        }
        statusLabel.setText("Viewing all students (" + manager.getStudents().size() + " total)");
    }

    private void searchStudent() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            viewStudents();
            return;
        }
        tableModel.setRowCount(0);
        try {
            int id = Integer.parseInt(query);
            Student s = manager.findStudent(id);
            if (s != null) {
                tableModel.addRow(new Object[]{s.id, s.name, s.age, s.course, s.marks[0], s.marks[1], s.marks[2], s.marks[3], s.marks[4], String.format("%.2f", s.percentage)});
                statusLabel.setText("Found student by ID");
            } else {
                JOptionPane.showMessageDialog(frame, "Student not found!", "Info", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Student not found");
            }
        } catch (NumberFormatException e) {
            // Search by name
            boolean found = false;
            for (Student s : manager.getStudents()) {
                if (s.name.toLowerCase().contains(query.toLowerCase())) {
                    tableModel.addRow(new Object[]{s.id, s.name, s.age, s.course, s.marks[0], s.marks[1], s.marks[2], s.marks[3], s.marks[4], String.format("%.2f", s.percentage)});
                    found = true;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(frame, "No students found with that name!", "Info", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("No matches found");
            } else {
                statusLabel.setText("Found students by name");
            }
        }
    }

    private void updateStudent() {
        try {
            String idStr = JOptionPane.showInputDialog(frame, "Enter Student ID to update:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr.trim());

            String name = JOptionPane.showInputDialog(frame, "Enter new Name:");
            if (name == null) return;
            name = name.trim();

            String ageStr = JOptionPane.showInputDialog(frame, "Enter new Age:");
            if (ageStr == null) return;
            int age = Integer.parseInt(ageStr.trim());

            String course = JOptionPane.showInputDialog(frame, "Enter new Course:");
            if (course == null) return;
            course = course.trim();

            int[] marks = new int[5];
            for (int i = 0; i < 5; i++) {
                String markStr = JOptionPane.showInputDialog(frame, "Enter new Mark for Subject " + (i + 1) + " (0-100):");
                if (markStr == null) return;
                marks[i] = Integer.parseInt(markStr.trim());
                if (marks[i] < 0 || marks[i] > 100) {
                    JOptionPane.showMessageDialog(frame, "Marks must be between 0 and 100!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            manager.updateStudent(id, name, age, course, marks);
            viewStudents();
            statusLabel.setText("Student updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        try {
            String idStr = JOptionPane.showInputDialog(frame, "Enter Student ID to delete:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr.trim());

            manager.deleteStudent(id);
            viewStudents();
            statusLabel.setText("Student deleted!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByCourse() {
        String course = courseField.getText().trim();
        if (course.isEmpty()) {
            viewStudents();
            return;
        }
        tableModel.setRowCount(0);
        for (Student s : manager.filterByCourse(course)) {
            tableModel.addRow(new Object[]{s.id, s.name, s.age, s.course, s.marks[0], s.marks[1], s.marks[2], s.marks[3], s.marks[4], String.format("%.2f", s.percentage)});
        }
        statusLabel.setText("Filtered by course: " + course);
    }
}