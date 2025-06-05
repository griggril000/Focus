import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class App {
	private static JFrame frame;
	private static String frameTitle;
	private static boolean breakTimerOpen = false;
	private static JSpinner focusTimeSpinner;
	private static Timer focusTimer;
	private static JSpinner breakTimeSpinner;
	private static Timer breakTimer;
	private static JLabel breakTimerLabel;
	private static JButton startButton;
	private static JMenuBar menuBar;
	private static JMenu optionsMenu;
	private static JCheckBoxMenuItem showTimeMenuItem;
	private static JCheckBoxMenuItem windowOnTopMenuItem;
	private static JPanel timerPanel;
	private static JLabel focusTimerLabel;
	private static boolean darkMode = false;
	private static int sessionCount = 0;
	private static JLabel sessionCounterLabel;
	private static JCheckBoxMenuItem darkModeMenuItem;
	private static JPanel mainPanel; // New: holds timer, controls, etc.
	private static JCheckBoxMenuItem lockedModeMenuItem; // Locked Mode menu item
	private static boolean lockedMode = false; // Locked Mode state

	public static void main(String[] args) {
		setSystemLookAndFeel();
		SwingUtilities.invokeLater(App::createAndShowGUI);
	}

	// Set system look and feel for native appearance
	private static void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not set system look and feel: " + e.getMessage());
		}
	}

	// Create and display the main GUI
	private static void createAndShowGUI() {
		frame = new JFrame();
		frameTitle = "Focus Timer";
		frame.setTitle(frameTitle);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new java.awt.Dimension(400, 300));
		frame.setPreferredSize(new java.awt.Dimension(600, 400));
		frame.setLayout(new BorderLayout(0, 0));

		setupSpinners();
		setupMenuBar();
		setupMainPanel();
		setupSessionCounter();

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// Setup the focus and break time spinners
	private static void setupSpinners() {
		SpinnerNumberModel focusSpinnerModel = new SpinnerNumberModel(25, 1, 60, 1);
		focusTimeSpinner = new JSpinner(focusSpinnerModel);
		SpinnerNumberModel breakSpinnerModel = new SpinnerNumberModel(5, 1, 15, 1);
		breakTimeSpinner = new JSpinner(breakSpinnerModel);
	}

	// Setup the timer panel (now just the timer label, centered)
	private static void setupTimerPanel() {
		timerPanel = new JPanel();
		timerPanel.setOpaque(false);
		timerPanel.setLayout(new BorderLayout());
		timerPanel.setBorder(new TitledBorder(null, "Timer", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		focusTimerLabel = new JLabel("00:00");
		focusTimerLabel.setFont(new Font("Arial", Font.BOLD, 56));
		focusTimerLabel.setVerticalAlignment(SwingConstants.CENTER);
		focusTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timerPanel.add(focusTimerLabel, BorderLayout.CENTER);
	}

	// Setup the main panel with timer, spinners, and start button (all centered)
	private static void setupMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
		mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 40, 30, 40));

		setupTimerPanel();
		mainPanel.add(timerPanel);

		mainPanel.add(javax.swing.Box.createVerticalStrut(30));

		JPanel controlsPanel = new JPanel(new GridBagLayout());
		controlsPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		JLabel focusSpinnerLabel = new JLabel("Focus Time (minutes):");
		focusSpinnerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 0;
		controlsPanel.add(focusSpinnerLabel, gbc);
		gbc.gridx = 1;
		controlsPanel.add(focusTimeSpinner, gbc);

		JLabel breakSpinnerLabel = new JLabel("Break Time (minutes):");
		breakSpinnerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gbc.gridx = 0;
		gbc.gridy = 1;
		controlsPanel.add(breakSpinnerLabel, gbc);
		gbc.gridx = 1;
		controlsPanel.add(breakTimeSpinner, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		startButton = new JButton("Start");
		controlsPanel.add(startButton, gbc);
		startButton.addActionListener(e -> onStartButtonClicked());

		mainPanel.add(controlsPanel);
		mainPanel.add(javax.swing.Box.createVerticalGlue());

		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	// Setup the menu bar and options (remains at the top)
	private static void setupMenuBar() {
		menuBar = new JMenuBar();
		menuBar.setForeground(new Color(0, 0, 0));
		optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);

		showTimeMenuItem = new JCheckBoxMenuItem("Show time in title bar");
		showTimeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK));
		optionsMenu.add(showTimeMenuItem);

		windowOnTopMenuItem = new JCheckBoxMenuItem("Keep this window on top");
		windowOnTopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK));
		windowOnTopMenuItem.addActionListener(e -> Options.windowOnTop(frame, windowOnTopMenuItem));
		optionsMenu.add(windowOnTopMenuItem);

		darkModeMenuItem = new JCheckBoxMenuItem("Dark Mode");
		darkModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK));
		darkModeMenuItem.addActionListener(e -> toggleDarkMode());
		optionsMenu.add(darkModeMenuItem);

		lockedModeMenuItem = new JCheckBoxMenuItem("Locked Mode");
		lockedModeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));
		lockedModeMenuItem.addActionListener(e -> lockedMode = lockedModeMenuItem.isSelected());
		optionsMenu.add(lockedModeMenuItem);

		frame.setJMenuBar(menuBar);
	}

	// Disable or enable all options menu items
	private static void setOptionsMenuEnabled(boolean enabled) {
		showTimeMenuItem.setEnabled(enabled);
		windowOnTopMenuItem.setEnabled(enabled);
		darkModeMenuItem.setEnabled(enabled);
		lockedModeMenuItem.setEnabled(enabled);
	}

	// Handle start button click
	private static void onStartButtonClicked() {
		int focusMinutes = (int) focusTimeSpinner.getValue();
		int breakMinutes = (int) breakTimeSpinner.getValue();
		if (lockedMode) {
			frame.dispose();
			frame.setUndecorated(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setOptionsMenuEnabled(false); // Disable options in locked mode
		}
		startFocusTimer(focusMinutes, breakMinutes);
		startButton.setEnabled(false);
		focusTimeSpinner.setEnabled(false);
		breakTimeSpinner.setEnabled(false);
		if (!lockedMode) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	// Improved: Focus timer logic is now clearer and uses local variables for
	// minutes/seconds
	private static void startFocusTimer(int focusMinutes, int breakMinutes) {
		if (focusTimer != null) {
			focusTimer.stop();
		}
		final JFrame messageFrame = new JFrame();
		final int[] time = { focusMinutes, 0 }; // [minutes, seconds]
		focusTimerLabel.setText(String.format("%02d:%02d", time[0], time[1]));
		focusTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (time[1] == 0) {
					if (time[0] == 0) {
						messageFrame.dispose();
						if (!breakTimerOpen) {
							if (lockedMode) {
								// Exit full screen and allow closing
								frame.dispose();
								frame.setUndecorated(false);
								frame.setExtendedState(JFrame.NORMAL);
								frame.setVisible(true);
								frame.setAlwaysOnTop(false);
								frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
								setOptionsMenuEnabled(true); // Re-enable options after locked mode
							}
							openBreakTimerWindow(breakMinutes);
							breakTimerOpen = true;
						}
						focusTimer.stop();
						return;
					} else {
						time[0]--;
						time[1] = 59;
					}
				} else {
					time[1]--;
				}
				focusTimerLabel.setText(String.format("%02d:%02d", time[0], time[1]));
				Options.showTime(frame, frameTitle, showTimeMenuItem, time[0], time[1]);
				if (time[0] == 0 && time[1] == 59 && focusMinutes == 1) {
					SwingUtilities.invokeLater(() -> {
						String message = "One minute until break time. :)";
						messageFrame.setAlwaysOnTop(true);
						JOptionPane.showMessageDialog(messageFrame, message, "1 Minute Remaining",
								JOptionPane.INFORMATION_MESSAGE);
						messageFrame.setAlwaysOnTop(false);
					});
				}
			}
		});
		focusTimer.start();
	}

	// Improved: Break timer logic is now clearer and uses local variables for
	// minutes/seconds
	private static void openBreakTimerWindow(int breakMinutes) {
		JFrame breakFrame = new JFrame("Break Timer");
		breakFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Allow closing break timer window
		breakTimerLabel = new JLabel();
		breakTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		breakTimerLabel.setVerticalAlignment(SwingConstants.CENTER);
		breakFrame.getContentPane().add(breakTimerLabel, BorderLayout.CENTER);
		if (breakTimer != null) {
			breakTimer.stop();
		}
		// Apply dark mode to break timer window if enabled
		if (darkMode) {
			Color bg = new Color(40, 44, 52);
			Color fg = Color.WHITE;
			breakFrame.getContentPane().setBackground(bg);
			breakFrame.setBackground(bg);
			breakTimerLabel.setForeground(fg);
			breakTimerLabel.setBackground(bg);
		} else {
			breakFrame.getContentPane().setBackground(Color.WHITE);
			breakFrame.setBackground(Color.WHITE);
			breakTimerLabel.setForeground(Color.BLACK);
			breakTimerLabel.setBackground(Color.WHITE);
		}
		final int[] time = { breakMinutes, 0 }; // [minutes, seconds]
		breakTimerLabel.setText("Break time: " + String.format("%02d:%02d", time[0], time[1]));
		breakTimerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		breakTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (time[1] == 0) {
					if (time[0] == 0) {
						breakFrame.dispose();
						breakTimerOpen = false;
						sessionCount++;
						sessionCounterLabel.setText("Sessions Completed: " + sessionCount);
						startButton.setEnabled(true);
						focusTimeSpinner.setEnabled(true);
						breakTimeSpinner.setEnabled(true);
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						breakTimer.stop();
						// Bring focus timer window to front after break
						SwingUtilities.invokeLater(() -> {
							frame.setAlwaysOnTop(true);
							frame.toFront();
							frame.requestFocus();
							frame.setAlwaysOnTop(false);
						});
						return;
					} else {
						time[0]--;
						time[1] = 59;
					}
				} else {
					time[1]--;
				}
				breakTimerLabel.setText("Break time: " + String.format("%02d:%02d", time[0], time[1]));
			}
		});
		breakTimer.start();
		breakFrame.setSize(300, 200);
		breakFrame.setLocationRelativeTo(null);
		breakFrame.setVisible(true);
	}

	// Setup the session counter label (remains at the bottom)
	private static void setupSessionCounter() {
		sessionCounterLabel = new JLabel("Sessions Completed: 0");
		sessionCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sessionCounterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		sessionCounterLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
		frame.getContentPane().add(sessionCounterLabel, BorderLayout.SOUTH);
	}

	// Toggle dark mode
	private static void toggleDarkMode() {
		darkMode = !darkMode;
		Color bg = darkMode ? new Color(40, 44, 52) : Color.WHITE;
		Color fg = darkMode ? Color.WHITE : Color.BLACK;
		frame.getContentPane().setBackground(bg);
		timerPanel.setBackground(bg);
		focusTimerLabel.setForeground(fg);
		sessionCounterLabel.setForeground(fg);
		sessionCounterLabel.setBackground(bg);
		// Set timer panel border title color
		if (timerPanel.getBorder() instanceof TitledBorder) {
			TitledBorder border = (TitledBorder) timerPanel.getBorder();
			border.setTitleColor(fg);
		}
		// Set all components in the main panel
		for (java.awt.Component c : frame.getContentPane().getComponents()) {
			if (c instanceof JPanel) {
				c.setBackground(bg);
				for (java.awt.Component cc : ((JPanel) c).getComponents()) {
					cc.setBackground(bg);
					cc.setForeground(fg);

					// Set label style
					if (cc instanceof JLabel) {
						cc.setForeground(fg);
					}
					// Set spinner label style (for nested panels)
					if (cc instanceof JPanel) {
						for (java.awt.Component ccc : ((JPanel) cc).getComponents()) {
							if (ccc instanceof JLabel) {
								ccc.setForeground(fg);
							}
						}
					}
				}
			} else {
				c.setBackground(bg);
				c.setForeground(fg);
			}
		}
		// Set start button style
		if (startButton != null) {
			startButton.setBackground(darkMode ? new Color(60, 63, 65) : new JButton().getBackground());
			startButton.setForeground(Color.BLACK); // Always use black text for the button
		}
		frame.repaint();
	}
}