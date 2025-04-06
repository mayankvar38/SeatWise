import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class ExamSeatingApp {
    private JFrame frame;
    private JTable table;
    private JTextField roomsField, studentsField, rowsField, seatsPerRowField;
    private JPanel outputPanel;

    public ExamSeatingApp() {
        frame = new JFrame("Exam Seating Arrangement");
        frame.setSize(1000, 600);
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

        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        frame.add(new JScrollPane(outputPanel), BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateSeatingChart());

        frame.setVisible(true);
    }

    private void generateSeatingChart() {
        outputPanel.removeAll();
        try {
            int numRooms = Integer.parseInt(roomsField.getText());
            int numStudents = Integer.parseInt(studentsField.getText());
            int numRows = Integer.parseInt(rowsField.getText());
            int seatsPerRow = Integer.parseInt(seatsPerRowField.getText());

            if ((numRooms * numRows * (seatsPerRow + 1) / 2) < numStudents) {
                outputPanel.add(new JLabel("Error: Not enough seats with spacing between students!"));
                outputPanel.revalidate();
                outputPanel.repaint();
                return;
            }

            Queue<Integer> studentQueue = new LinkedList<>();
            for (int i = 1; i <= numStudents; i++) {
                studentQueue.add(i);
            }

            for (int r = 1; r <= numRooms; r++) {
                outputPanel.add(new JLabel("Room " + r + ":"));

                String[][] tableData = new String[numRows * seatsPerRow][2];
                String[] columnNames = {"Seat No.", "Roll No."};
                int rowIndex = 0;

                for (int row = 0; row < numRows; row++) {
                    char rowLabel = (char) ('A' + row);
                    for (int seat = 1; seat <= seatsPerRow; seat++) {
                        String seatLabel = rowLabel + String.valueOf(seat);
                        if (seat % 2 == 1 && !studentQueue.isEmpty()) {
                            tableData[rowIndex][0] = seatLabel;
                            tableData[rowIndex][1] = String.valueOf(studentQueue.poll());
                        } else {
                            tableData[rowIndex][0] = seatLabel;
                            tableData[rowIndex][1] = "-";
                        }
                        rowIndex++;
                    }
                }

                JTable table = new JTable(tableData, columnNames);
                table.setEnabled(false);
                table.setRowHeight(25);
                outputPanel.add(new JScrollPane(table));
            }

            outputPanel.revalidate();
            outputPanel.repaint();
        } catch (NumberFormatException ex) {
            outputPanel.add(new JLabel("Invalid input. Please enter valid numbers."));
            outputPanel.revalidate();
            outputPanel.repaint();
        }
    }

    public static void main(String[] args) {
        new ExamSeatingApp();
    }
}