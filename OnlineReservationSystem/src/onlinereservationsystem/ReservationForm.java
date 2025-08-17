import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;
import java.text.SimpleDateFormat;
import javax.swing.JFormattedTextField;

public class ReservationForm extends JFrame {

    private JTextField nameField, ageField, trainNoField, trainNameField, sourceField, destinationField;
    private JComboBox<String> genderComboBox, classTypeComboBox;
    private JDatePickerImpl datePicker; 
    private JButton insertButton;

    public ReservationForm() {
        // Set up the main window (JFrame)
        setTitle("Online Reservation System - Reservation Form");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));

        // Create and add components
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        
        JLabel genderLabel = new JLabel("Gender:");
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);

        JLabel trainNoLabel = new JLabel("Train No:");
        trainNoField = new JTextField();
        
        JLabel trainNameLabel = new JLabel("Train Name:");
        trainNameField = new JTextField();
        
        JLabel dojLabel = new JLabel("Date of Journey:"); 
        
        // **CORRECTED JDatePicker IMPLEMENTATION**
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        
        // This anonymous class provides the required formatter
        JFormattedTextField.AbstractFormatter formatter = new JFormattedTextField.AbstractFormatter() {
            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            @Override
            public Object stringToValue(String text) {
                try {
                    return sdf.parse(text);
                } catch (java.text.ParseException e) {
                    return null;
                }
            }
            
            @Override
            public String valueToString(Object value) {
                if (value != null) {
                    java.util.Calendar cal = (java.util.Calendar) value;
                    return sdf.format(cal.getTime());
                }
                return "";
            }
        };
        
        datePicker = new JDatePickerImpl(datePanel, formatter); // Now passing both required arguments
        
        JLabel sourceLabel = new JLabel("Source:");
        sourceField = new JTextField();
        
        JLabel destinationLabel = new JLabel("Destination:");
        destinationField = new JTextField();
        
        JLabel classTypeLabel = new JLabel("Class Type:");
        String[] classes = {"First Class", "Second Class", "Economy"};
        classTypeComboBox = new JComboBox<>(classes);

        insertButton = new JButton("Insert Reservation");

        // Add components to the form panel
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(genderLabel);
        formPanel.add(genderComboBox);
        formPanel.add(trainNoLabel);
        formPanel.add(trainNoField);
        formPanel.add(trainNameLabel);
        formPanel.add(trainNameField);
        formPanel.add(dojLabel);
        formPanel.add(datePicker); 
        formPanel.add(sourceLabel);
        formPanel.add(sourceField);
        formPanel.add(destinationLabel);
        formPanel.add(destinationField);
        formPanel.add(classTypeLabel);
        formPanel.add(classTypeComboBox);
        
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertReservation();
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(insertButton, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void insertReservation() {
        String name = nameField.getText();
        int age = 0;
        try {
            age = Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String gender = (String) genderComboBox.getSelectedItem();
        String trainNo = trainNoField.getText();
        String trainName = trainNameField.getText();
        String source = sourceField.getText();
        String destination = destinationField.getText();
        String classType = (String) classTypeComboBox.getSelectedItem();
        
        // Getting the date from the JDatePicker and formatting it correctly
        Date selectedDate = (Date) datePicker.getModel().getValue();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date of journey.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String doj = sdf.format(selectedDate);
        
        String sql = "INSERT INTO reservations (name, age, gender, train_no, train_name, doj, source, destination, class_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, trainNo);
            pstmt.setString(5, trainName);
            pstmt.setString(6, doj); 
            pstmt.setString(7, source);
            pstmt.setString(8, destination);
            pstmt.setString(9, classType);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Reservation inserted successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert reservation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        genderComboBox.setSelectedIndex(0);
        trainNoField.setText("");
        trainNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");
        classTypeComboBox.setSelectedIndex(0);
        datePicker.getModel().setValue(null); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReservationForm().setVisible(true);
        });
    }
}