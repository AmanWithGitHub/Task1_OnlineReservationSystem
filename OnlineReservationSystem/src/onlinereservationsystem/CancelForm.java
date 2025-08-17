import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CancelForm extends JFrame {

    private JTextField pnrField;
    private JTextArea displayArea;
    private JButton findButton, cancelButton;

    public CancelForm() {
        // Set up the main window
        setTitle("Online Reservation System - Cancellation Form");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null); // Center the window

        // Create the main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // PNR input panel
        JPanel pnrPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnrPanel.add(new JLabel("Enter PNR Number:"));
        pnrField = new JTextField(15);
        findButton = new JButton("Find Reservation");
        pnrPanel.add(pnrField);
        pnrPanel.add(findButton);

        // Display area for reservation details
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Cancel button panel
        JPanel cancelPanel = new JPanel();
        cancelButton = new JButton("Cancel Reservation");
        cancelButton.setEnabled(false); // Initially disabled until a reservation is found
        cancelPanel.add(cancelButton);

        // Add action listeners
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findReservation();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelReservation();
            }
        });

        // Add panels to the main frame
        mainPanel.add(pnrPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(cancelPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void findReservation() {
        displayArea.setText(""); // Clear previous results
        cancelButton.setEnabled(false); // Disable cancel button
        String pnrText = pnrField.getText();

        if (pnrText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int pnr = Integer.parseInt(pnrText);
            String sql = "SELECT * FROM reservations WHERE pnr = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, pnr);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Reservation found, display details
                        String details = "PNR: " + rs.getInt("pnr") + "\n" +
                                        "Name: " + rs.getString("name") + "\n" +
                                        "Age: " + rs.getInt("age") + "\n" +
                                        "Gender: " + rs.getString("gender") + "\n" +
                                        "Train No: " + rs.getString("train_no") + "\n" +
                                        "Train Name: " + rs.getString("train_name") + "\n" +
                                        "Date of Journey: " + rs.getString("doj") + "\n" +
                                        "Source: " + rs.getString("source") + "\n" +
                                        "Destination: " + rs.getString("destination") + "\n" +
                                        "Class Type: " + rs.getString("class_type");
                        displayArea.setText(details);
                        cancelButton.setEnabled(true); // Enable cancel button
                    } else {
                        // No reservation found
                        JOptionPane.showMessageDialog(this, "No reservation found with PNR: " + pnr, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "PNR must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelReservation() {
        String pnrText = pnrField.getText();
        if (pnrText.isEmpty()) return; // Should not happen if button is enabled

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this reservation?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int pnr = Integer.parseInt(pnrText);
                String sql = "DELETE FROM reservations WHERE pnr = ?";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setInt(1, pnr);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation with PNR " + pnr + " has been successfully cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        displayArea.setText("");
                        pnrField.setText("");
                        cancelButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to cancel reservation. PNR not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                // Should be caught by findReservation, but as a safeguard
                JOptionPane.showMessageDialog(this, "PNR must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CancelForm().setVisible(true);
        });
    }
}