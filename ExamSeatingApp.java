import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class ExamSeatingApp {
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField roomsField, studentsField, rowsField, seatsPerRowField;
    
    public ExamSeatingApp() {
        frame = new JFrame("Exam Seating Arrangement");
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Number of Rooms:"));
        roomsField = new JTextField(5);
        inputPanel.add(roomsField);
        
        inputPanel.add(new JLabel("Number of Students:"));
        studentsField = new JTextField(5);
        inputPanel.add(studentsField);
        
        inputPanel.add(new JLabel("Rows per Room:"));
        rowsField = new JTextField(5);
        inputPanel.add(rowsField);
        
        inputPanel.add(new JLabel("Seats per Row:"));
        seatsPerRowField = new JTextField(5);
        inputPanel.add(seatsPerRowField);
        
        JButton generateButton = new JButton("Generate Seating Chart");
        inputPanel.add(generateButton);
        
        frame.add(inputPanel, BorderLayout.NORTH);
        
        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        generateButton.addActionListener(e -> generateSeatingChart());
        
        frame.setVisible(true);
    }
    
    private void generateSeatingChart() {
        try {
            int numRooms = Integer.parseInt(roomsField.getText());
            int numStudents = Integer.parseInt(studentsField.getText());
            int numRows = Integer.parseInt(rowsField.getText());
            int seatsPerRow = Integer.parseInt(seatsPerRowField.getText());
            
            int totalSeats = numRooms * numRows * seatsPerRow;
            if (totalSeats / 2 < numStudents) { // Only half seats are used due to spacing
                outputArea.setText("Error: Not enough seats for all students!");
                return;
            }
            
            Queue<Integer> studentQueue = new LinkedList<>();
            for (int i = 1; i <= numStudents; i++) {
                studentQueue.add(i);
            }
            
            StringBuilder seatingChart = new StringBuilder();
            for (int r = 1; r <= numRooms; r++) {
                seatingChart.append("Room ").append(r).append(":\n");
                for (int row = 0; row < numRows; row++) {
                    char rowLabel = (char) ('A' + row);
                    seatingChart.append(rowLabel).append(" Row: ");
                    for (int seat = 1; seat <= seatsPerRow; seat += 2) { // Skipping every alternate seat
                        String seatLabel = rowLabel + String.valueOf(seat);
                        if (!studentQueue.isEmpty()) {
                            seatingChart.append(seatLabel).append(" -> Roll No: ").append(studentQueue.poll()).append("\t");
                        } else {
                            seatingChart.append(seatLabel).append(" -> EMPTY\t");
                        }
                    }
                    seatingChart.append("\n");
                }
                seatingChart.append("\n");
            }
            outputArea.setText(seatingChart.toString());
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid input. Please enter valid numbers.");
        }
    }
    
    public static void main(String[] args) {
        new ExamSeatingApp();
    }
}
