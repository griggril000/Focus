import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class App {
    public static JFrame frame;
    public static String frameTitle;
    private static boolean breakTimerOpen = false;
    private static JSpinner focusTimeSpinner;
    private static Timer focusTimer;
    private static JSpinner breakTimeSpinner;
    private static Timer breakTimer;
    private static JLabel breakTimerLabel;
    public static int seconds = 0;
    private static JButton startButton;
    private static JPanel timerPanel;
    private static JLabel focusTimerLabel;
    private static JPanel optionsPanel;
    private static JMenuBar menuBar;
    private static JMenu optionsMenu;
    private static JCheckBox showTimeCheckbox;
    private static JCheckBox windowOnTopCheckBox;

    public static void main(String[] args) {
        // Makes the program look like the rest of the user's system interface.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Handle exceptions (e.g., ClassNotFoundException, InstantiationException,
            // etc.)
        }

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();
            // This will make it easier to change the title if we want, as well as append
            // anything to it.
            frameTitle = "Focus Timer";
            frame.setTitle(frameTitle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create number spinners for focus and break times
            SpinnerNumberModel focusSpinnerModel = new SpinnerNumberModel(25, 1, 60, 1);
            focusTimeSpinner = new JSpinner(focusSpinnerModel);
            SpinnerNumberModel breakSpinnerModel = new SpinnerNumberModel(5, 1, 15, 1);
            breakTimeSpinner = new JSpinner(breakSpinnerModel);

            // Add the timer label and input fields to the frame
            JPanel panel = new JPanel(new GridLayout(2, 2));

            JLabel focusSpinnerLabel = new JLabel("Focus Time (minutes):");
            focusSpinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(focusSpinnerLabel);
            panel.add(focusTimeSpinner);
            JLabel breakSpinnerLabel = new JLabel("Break Time (minutes):");
            breakSpinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(breakSpinnerLabel);
            panel.add(breakTimeSpinner);

            JPanel sidePanel = new JPanel();
            sidePanel.setLayout(new BorderLayout(0, 0));

            frame.getContentPane().add(sidePanel, BorderLayout.EAST); // Adjust BorderLayout placement if needed

            timerPanel = new JPanel();
            timerPanel.setBorder(new TitledBorder(null, "Timer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            sidePanel.add(timerPanel, BorderLayout.WEST);

            focusTimerLabel = new JLabel("00:00");
            focusTimerLabel.setVerticalAlignment(SwingConstants.CENTER);
            focusTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            timerPanel.add(focusTimerLabel);

            optionsPanel = new JPanel();
            sidePanel.add(optionsPanel, BorderLayout.EAST);
            optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

            menuBar = new JMenuBar();
            optionsPanel.add(menuBar);

            optionsMenu = new JMenu("Options");
            menuBar.add(optionsMenu);

            showTimeCheckbox = new JCheckBox("Show time in title bar");
            optionsMenu.add(showTimeCheckbox);

            windowOnTopCheckBox = new JCheckBox("Keep this window on top");
            windowOnTopCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Settings.windowOnTop(frame, windowOnTopCheckBox);
                }
            });
            optionsMenu.add(windowOnTopCheckBox);

            // Create a start button
            startButton = new JButton("Start");
            // When the button is clicked, run the code in the brackets {}
            startButton.addActionListener(e -> {
                int focusMinutes = (int) focusTimeSpinner.getValue();
                int breakMinutes = (int) breakTimeSpinner.getValue();

                startFocusTimer(focusMinutes, breakMinutes);

                // Once our timer starts, disable the start button so it can't be pressed.
                startButton.setEnabled(false);

                // Disable the spinners
                focusTimeSpinner.setEnabled(false);
                breakTimeSpinner.setEnabled(false);

                // On timer start, the focus window will not be able to be closed.
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            });

            // Add the start button to the frame
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.getContentPane().add(startButton, BorderLayout.SOUTH);
            frame.setSize(350, 150);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void startFocusTimer(int focusMinutes, int breakMinutes) {
        // Stop the previous countdown timer (if running)
        if (focusTimer != null) {
            focusTimer.stop();
        }

        JFrame messageFrame = new JFrame();

        focusTimer = new Timer(1000, new ActionListener() {
            int minutes = focusMinutes;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (seconds == 0) {
                    if (minutes == 0) {
                        // Focus timer finished, handle break time
                        // Close the 1 minute reminder window
                        messageFrame.dispose();
                        // Open the break timer window if not already open
                        if (!breakTimerOpen) {
                            openBreakTimerWindow(breakMinutes);
                            breakTimerOpen = true; // Mark break timer as open
                        }
                        // Reset the focus timer
                        minutes = focusMinutes;
                        seconds = 0;
                        focusTimer.stop(); // Stop the timer
                    } else {
                        minutes--;
                        seconds = 59;
                    }
                } else {
                    seconds--;
                }

                // Set the focus timer label to the minutes and seconds remaining
                focusTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                // Call the showTime method. (If the showTime checkbox is checked, the method
                // will show the remaining time in the titlebar.)
                Settings.showTime(frame, frameTitle, showTimeCheckbox, minutes, seconds);

                // Check for 1 minute remaining and schedule info window display
                if (minutes == 1 && seconds == 0) {
                    SwingUtilities.invokeLater(() -> {
                        String message = "One minute until break time. :)";
                        messageFrame.setAlwaysOnTop(true);
                        JOptionPane.showMessageDialog(messageFrame, message, "1 Minute Remaining",
                                JOptionPane.INFORMATION_MESSAGE);
                        messageFrame.setAlwaysOnTop(false); // Reset after display

                    });
                }
            }
        });
        focusTimer.start();
    }

    private static void openBreakTimerWindow(int breakMinutes) {
        JFrame breakFrame = new JFrame("Break Timer");
        breakFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        breakFrame.setAlwaysOnTop(true);
        breakFrame.setUndecorated(true);
        breakFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        breakTimerLabel = new JLabel();

        // Center the break timer label both vertically and horizontally
        breakTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        breakTimerLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add the break timer label to the frame
        breakFrame.getContentPane().add(breakTimerLabel, BorderLayout.CENTER);

        // Stop the previous break timer (if running)
        if (breakTimer != null) {
            breakTimer.stop();
        }

        breakTimer = new Timer(1000, new ActionListener() {
            int minutes = breakMinutes;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (seconds == 0) {
                    if (minutes == 0) {
                        // Break timer finished, reset the timer
                        minutes = breakMinutes;
                        seconds = 0;
                        focusTimer.stop(); // Stop the timer
                        // Close the window
                        breakFrame.dispose();
                        breakTimerOpen = false;
                        // Enable the focus timer window.
                        startButton.setEnabled(true); // Make the start button for the focus timer clickable again.
                        focusTimeSpinner.setEnabled(true);
                        breakTimeSpinner.setEnabled(true);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Make the focus timer window closable.
                    } else {
                        minutes--;
                        seconds = 59;
                    }
                } else {
                    seconds--;
                }
                breakTimerLabel.setText("Break time: " + String.format("%02d:%02d", minutes, seconds));
                breakTimerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            }
        });
        breakTimer.start();

        // Set the break timer window size and center it
        breakFrame.setSize(300, 200); // Adjust as needed
        breakFrame.setLocationRelativeTo(null);

        // Show the break timer window
        breakFrame.setVisible(true);
    }
}