package bdbt_bada_project.SpringApplication.Helpers;

import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.entities.AcademyEntity;
import bdbt_bada_project.SpringApplication.entities.LecturerEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ServerGUI {

    public static void start(GlobalDataManager globalDataManager) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server State");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pełny ekran
            frame.setLayout(new GridLayout(3, 1)); // Podział na 3 sekcje pionowe

            // Sekcja 1: Informacje o akademii
            JPanel academyPanel = createPanel("Academy Information");
            JTable academyTable = createTable();
            academyPanel.add(new JScrollPane(academyTable));
            frame.add(academyPanel);

            // Sekcja 2: Informacje o użytkownikach
            JPanel userAccountsPanel = createPanel("User Accounts");
            JTable userAccountsTable = createTable();
            userAccountsPanel.add(new JScrollPane(userAccountsTable));
            frame.add(userAccountsPanel);

            // Sekcja 3: Sesje aktywne
            JPanel activeSessionsPanel = createPanel("Active Sessions");
            JTable activeSessionsTable = createTable();
            activeSessionsPanel.add(new JScrollPane(activeSessionsTable));
            frame.add(activeSessionsPanel);

            // Sekcja 4: Informacje o studentach
            JPanel studentsPanel = createPanel("Students Data");
            JTable studentsTable = createTable();
            studentsPanel.add(new JScrollPane(studentsTable));
            frame.add(studentsPanel);

// Sekcja 5: Informacje o wykładowcach
            JPanel lecturersPanel = createPanel("Lecturers Data");
            JTable lecturersTable = createTable();
            lecturersPanel.add(new JScrollPane(lecturersTable));
            frame.add(lecturersPanel);


            // Timer do dynamicznego odświeżania
            Timer timer = new Timer(1000, e -> {
                updateAcademyTable(globalDataManager, (DefaultTableModel) academyTable.getModel());
                updateUserAccountsTable(globalDataManager, (DefaultTableModel) userAccountsTable.getModel());
                updateActiveSessionsTable(globalDataManager, (DefaultTableModel) activeSessionsTable.getModel());
                updateStudentsTable(globalDataManager, (DefaultTableModel) studentsTable.getModel());
                updateLecturersTable(globalDataManager, (DefaultTableModel) lecturersTable.getModel());
            });
            timer.start();

            // Pierwsze wypełnienie danych
            updateAcademyTable(globalDataManager, (DefaultTableModel) academyTable.getModel());
            updateUserAccountsTable(globalDataManager, (DefaultTableModel) userAccountsTable.getModel());
            updateActiveSessionsTable(globalDataManager, (DefaultTableModel) activeSessionsTable.getModel());
            updateStudentsTable(globalDataManager, (DefaultTableModel) studentsTable.getModel());
            updateLecturersTable(globalDataManager, (DefaultTableModel) lecturersTable.getModel());

            // Wyświetlenie okna
            frame.setVisible(true);
        });
    }

    private static JPanel createPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        return panel;
    }

    private static JTable createTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Field");
        model.addColumn("Value");
        JTable table = new JTable(model);

        // Kolorowanie wartości
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) { // Kolumna z wartościami
                    if (value == null) {
                        cell.setBackground(Color.RED); // `null` na czerwono
                    } else if (value.toString().isEmpty()) {
                        cell.setBackground(Color.YELLOW); // Pusty napis na żółto
                    } else {
                        cell.setBackground(Color.GREEN); // Normalna wartość na zielono
                    }
                } else {
                    cell.setBackground(Color.WHITE); // Kolumna z polami
                }
                return cell;
            }
        });

        return table;
    }

    private static void updateAcademyTable(GlobalDataManager globalDataManager, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Wyczyść tabelę przed aktualizacją
        AcademyEntity academy = globalDataManager.getAcademyEntity();
        if (academy != null) {
            tableModel.addRow(new Object[]{"Academy Name", academy.getName()});
            tableModel.addRow(new Object[]{"Academy Email", academy.getEmail()});
            tableModel.addRow(new Object[]{"Academy Phone", academy.getPhone()});
            tableModel.addRow(new Object[]{"Academy Address", academy.getAddress() != null ? academy.getAddress().toString() : null});
            tableModel.addRow(new Object[]{"Academy Dean", academy.getDean() != null ? academy.getDean().getFirstName() + " " + academy.getDean().getLastName() : null});
        } else {
            tableModel.addRow(new Object[]{"Academy Data", null});
        }
    }

    private static void updateUserAccountsTable(GlobalDataManager globalDataManager, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Wyczyść tabelę przed aktualizacją
        if (!globalDataManager.userAccounts.isEmpty()) {
            for (int i = 0; i < globalDataManager.userAccounts.size(); i++) {
                tableModel.addRow(new Object[]{"User " + (i + 1), globalDataManager.userAccounts.get(i)});
            }
        } else {
            tableModel.addRow(new Object[]{"No user accounts", null});
        }
    }


    private static void updateActiveSessionsTable(GlobalDataManager globalDataManager, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Wyczyść tabelę przed aktualizacją
        if (!globalDataManager.getActiveSessions().isEmpty()) {
            int index = 1;
            for (UserSessionController.UserSession session : globalDataManager.getActiveSessions()) {
                tableModel.addRow(new Object[]{"Session " + index++, "User ID: " + session.getId()});
            }
        } else {
            tableModel.addRow(new Object[]{"No active sessions", null});
        }
    }
    private static void updateStudentsTable(GlobalDataManager globalDataManager, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Wyczyść tabelę przed aktualizacją
        if (globalDataManager.studentsData != null && !globalDataManager.studentsData.isEmpty()) {
            for (Map.Entry<Integer, StudentData> entry : globalDataManager.studentsData.entrySet()) {
                Integer studentId = entry.getKey();
                StudentData student = entry.getValue();
                String studentInfo = student.getFirstName() + " " + student.getLastName() + " (ID: " + studentId + ")";
                tableModel.addRow(new Object[]{"Student", studentInfo});
            }
        } else {
            tableModel.addRow(new Object[]{"No student data", null});
        }
    }

    private static void updateLecturersTable(GlobalDataManager globalDataManager, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Wyczyść tabelę przed aktualizacją
        if (globalDataManager.lecturersData != null && !globalDataManager.lecturersData.isEmpty()) {
            for (Map.Entry<Integer, LecturerEntity> entry : globalDataManager.lecturersData.entrySet()) {
                Integer lecturerId = entry.getKey();
                LecturerEntity lecturer = entry.getValue();
                String lecturerInfo = lecturer.getFirstName() + " " + lecturer.getLastName() + " (ID: " + lecturerId + ")";
                tableModel.addRow(new Object[]{"Lecturer", lecturerInfo});
            }
        } else {
            tableModel.addRow(new Object[]{"No lecturer data", null});
        }
    }

}
