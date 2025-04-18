import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.Queue;

public class ExamSeatingApp {
    private JFrame frame;
    private JTable table;
    private JTextField roomsField, studentsField, rowsField, seatsPerRowField;
    private JPanel outputPanel;
    private JButton prevButton, nextButton;
    private int currentRoom = 0;
    private java.util.List<String[][]> allRoomData;

    public ExamSeatingApp() {
        frame = new JFrame("Exam Seating Arrangement");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Number of Rooms:"), gbc);
        gbc.gridx = 1;
        roomsField = new JTextField(5);
        inputPanel.add(roomsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Number of Students:"), gbc);
        gbc.gridx = 1;
        studentsField = new JTextField(5);
        inputPanel.add(studentsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Rows per Room:(Example : A,B,C)"), gbc);
        gbc.gridx = 1;
        rowsField = new JTextField(5);
        inputPanel.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Seats per Row:(Example : A1,B2,C3)"), gbc);
        gbc.gridx = 1;
        seatsPerRowField = new JTextField(5);
        inputPanel.add(seatsPerRowField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton generateButton = new JButton("Generate Seating Chart");
        inputPanel.add(generateButton, gbc);

        frame.add(inputPanel, BorderLayout.NORTH);

        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(outputPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel navPanel = new JPanel();
        prevButton = new JButton("Previous Room");
        nextButton = new JButton("Next Room");
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        frame.add(navPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateSeatingChart());
        prevButton.addActionListener(e -> showRoom(currentRoom - 1));
        nextButton.addActionListener(e -> showRoom(currentRoom + 1));

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

            allRoomData = new LinkedList<>();

            for (int r = 1; r <= numRooms; r++) {
                String[][] tableData = new String[numRows * seatsPerRow][2];
                int rowIndex = 0;

                for (int row = 0; row < numRows; row++) {
                    char rowLabel = (char) ('A' + row);
                    for (int seat = 1; seat <= seatsPerRow; seat++) {
                        String seatLabel = rowLabel + String.valueOf(seat);
                        tableData[rowIndex][0] = seatLabel;
                        if (seat % 2 == 1 && !studentQueue.isEmpty()) {
                            tableData[rowIndex][1] = String.valueOf(studentQueue.poll());
                        } else {
                            tableData[rowIndex][1] = "-";
                        }
                        rowIndex++;
                    }
                }
                allRoomData.add(tableData);
            }

            currentRoom = 0;
            showRoom(currentRoom);

        } catch (NumberFormatException ex) {
            outputPanel.add(new JLabel("Invalid input. Please enter valid numbers."));
            outputPanel.revalidate();
            outputPanel.repaint();
        }
    }

    private void showRoom(int roomIndex) {
        if (roomIndex < 0 || roomIndex >= allRoomData.size()) return;
        currentRoom = roomIndex;
        outputPanel.removeAll();

        JLabel title = new JLabel("Room " + (currentRoom + 1) + ":");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        outputPanel.add(title);

        String[][] data = allRoomData.get(currentRoom);
        String[] columnNames = {"Seat No.", "Roll No."};
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(Math.max(30, frame.getHeight() / 30));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth() - 50, frame.getHeight() - 300));
        outputPanel.add(scrollPane);

        outputPanel.revalidate();
        outputPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamSeatingApp::new);
    }
}
