import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ExamSeatingApp {
    private JFrame frame;
    private JTextField roomsField, studentsField, rowsField, seatsPerRowField;
    private JPanel outputPanel;
    private JButton prevButton, nextButton, pdfButton;
    private int currentRoom = 0;
    private List<String[][]> allRoomData;
    private ImageIcon chairIcon;

    public ExamSeatingApp() {
        frame = new JFrame("SeatWise");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());

        try {
            chairIcon = new ImageIcon("chair.png");
            Image img = chairIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            chairIcon = new ImageIcon(img);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Chair image not found!");
        }

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Number of Rooms:"), gbc);
        gbc.gridx = 1;
        roomsField = new JTextField(10);
        inputPanel.add(roomsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Number of Students:"), gbc);
        gbc.gridx = 1;
        studentsField = new JTextField(10);
        inputPanel.add(studentsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Rows per Room:"), gbc);
        gbc.gridx = 1;
        rowsField = new JTextField(10);
        inputPanel.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Seats per Row:"), gbc);
        gbc.gridx = 1;
        seatsPerRowField = new JTextField(10);
        inputPanel.add(seatsPerRowField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
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
        pdfButton = new JButton("Download PDF");
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(pdfButton);
        frame.add(navPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(this::generateSeatingChart);
        prevButton.addActionListener(e -> showRoom(currentRoom - 1));
        nextButton.addActionListener(e -> showRoom(currentRoom + 1));
        pdfButton.addActionListener(e -> generatePDF());

        frame.setVisible(true);
    }

    private void generateSeatingChart(ActionEvent e) {
        outputPanel.removeAll();
        try {
            int numRooms = Integer.parseInt(roomsField.getText());
            int numStudents = Integer.parseInt(studentsField.getText());
            int numRows = Integer.parseInt(rowsField.getText());
            int seatsPerRow = Integer.parseInt(seatsPerRowField.getText());

            int usableSeatsPerRow = (seatsPerRow + 1) / 2;
            int totalUsableSeats = numRooms * numRows * usableSeatsPerRow;

            if (totalUsableSeats < numStudents) {
                outputPanel.add(new JLabel("Error: Not enough seats available with spacing."));
                outputPanel.revalidate();
                outputPanel.repaint();
                return;
            }

            Queue<String> studentQueue = new LinkedList<>();
            for (int i = 1; i <= numStudents; i++) {
                studentQueue.add("S" + i);
            }

            allRoomData = new LinkedList<>();

            for (int r = 0; r < numRooms; r++) {
                String[][] roomLayout = new String[numRows][seatsPerRow];
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < seatsPerRow; j++) {
                        if (j % 2 == 0 && !studentQueue.isEmpty()) {
                            roomLayout[i][j] = studentQueue.poll();
                        } else {
                            roomLayout[i][j] = "-";
                        }
                    }
                }
                allRoomData.add(roomLayout);
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

        JLabel title = new JLabel("Room " + (currentRoom + 1));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputPanel.add(title);

        String[][] data = allRoomData.get(roomIndex);

        JPanel seatGrid = new JPanel(new GridLayout(data.length, data[0].length, 15, 15));
        seatGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                JPanel seatPanel = new JPanel();
                seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
                seatPanel.setPreferredSize(new Dimension(80, 100));
                seatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                String seatName = (char) ('A' + i) + String.valueOf(j + 1);
                String studentRoll = data[i][j];

                JLabel seatLabel = new JLabel(seatName, SwingConstants.CENTER);
                seatLabel.setFont(new Font("Arial", Font.BOLD, 12));
                seatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel chairLabel = new JLabel(chairIcon);
                chairLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel studentLabel = new JLabel(studentRoll, SwingConstants.CENTER);
                studentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                studentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                seatPanel.add(seatLabel);
                seatPanel.add(chairLabel);
                seatPanel.add(studentLabel);

                seatGrid.add(seatPanel);
            }
        }

        outputPanel.add(seatGrid);
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    private void generatePDF() {
        if (allRoomData == null || allRoomData.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No data to export. Please generate seating chart first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("SeatingArrangement.pdf"));
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new java.io.FileOutputStream(fileToSave));
                document.open();

                for (int roomIndex = 0; roomIndex < allRoomData.size(); roomIndex++) {
                    String[][] data = allRoomData.get(roomIndex);

                    if (roomIndex > 0) document.newPage();

                    Paragraph title = new Paragraph("Room " + (roomIndex + 1),
                            new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(10f);
                    document.add(title);

                    PdfPTable table = new PdfPTable(data[0].length);
                    table.setSpacingBefore(10);
                    table.setWidthPercentage(100);

                    for (int i = 0; i < data.length; i++) {
                        for (int j = 0; j < data[i].length; j++) {
                            String seatLabel = (char) ('A' + i) + String.valueOf(j + 1);
                            String student = data[i][j];
                            String content = seatLabel + "\n" + (student.equals("-") ? "Empty" : student);

                            PdfPCell cell = new PdfPCell(new Phrase(content));
                            cell.setFixedHeight(50);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            table.addCell(cell);
                        }
                    }

                    document.add(table);
                }

                document.close();
                JOptionPane.showMessageDialog(frame, "PDF saved to:\n" + fileToSave.getAbsolutePath());

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error generating PDF: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExamSeatingApp::new);
    }
}
