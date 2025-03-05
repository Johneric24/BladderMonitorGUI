import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BladderAppGui extends JFrame {
    
    private JLabel bladderImage;
    private JLabel timeLabel;
    private JPanel historyPanel;
    private LinkedList<String> historyList = new LinkedList<>();

    public BladderAppGui() {
        // Set system default look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Window settings
        setTitle("Bladder App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Bladder Monitoring");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Measure Button
        JButton measureButton = new JButton("Measure Bladder");
        measureButton.setFont(new Font("Arial", Font.BOLD, 18));
        measureButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        measureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        measureButton.setBackground(new Color(50, 50, 50)); // Steel Blue
        measureButton.setForeground(Color.DARK_GRAY);
        measureButton.setFocusPainted(false);
measureButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));        

        measureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBladderState();
            }
        });

        // Bladder Image
        bladderImage = new JLabel(loadImage("src/assets/BladderSize.png", 300, 300));
        bladderImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Current Time Label
        timeLabel = new JLabel("Time: " + getCurrentTime());
        timeLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // History Panel
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Measurement History"));
        historyPanel.setBackground(Color.LIGHT_GRAY);

        // Add components to panel
        mainPanel.add(measureButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(bladderImage);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(timeLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(historyPanel);

        // Add panel to frame
        add(mainPanel, BorderLayout.CENTER);
    }

    private void updateBladderState() {
        String bladderData = BladderApp.getBladderState();
        String timestamp = getCurrentTime();
        String entry = timestamp + " - " + bladderData;

        if (bladderData.equals("Empty")) {
            bladderImage.setIcon(loadImage("src/assets/Empty.png", 300, 300));
        } else if (bladderData.equals("Half-Full")) {
            bladderImage.setIcon(loadImage("src/assets/Half-Full.png", 300, 300));
        } else if (bladderData.equals("Full")) {
            bladderImage.setIcon(loadImage("src/assets/Full.png", 300, 300));
        } else {
            System.out.println("Bladder State Not Found");
            return;
        }

        // Add new entry to history (limit to last 3 entries)
        if (historyList.size() >= 3) {
            historyList.removeFirst(); // Remove oldest entry
        }
        historyList.add(entry);

        // Update UI
        updateHistoryPanel();
        timeLabel.setText("Time: " + getCurrentTime());
    }

    private void updateHistoryPanel() {
        historyPanel.removeAll();
        for (String record : historyList) {
            JLabel historyLabel = new JLabel(record);
            historyLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            historyPanel.add(historyLabel);
        }
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private ImageIcon loadImage(String resourcePath, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }

    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        return currentDateTime.format(formatter);
    }
}
