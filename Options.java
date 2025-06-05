import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class Options {
	// Shows the remaining time in the title bar.
	public static void showTime(JFrame frame, String frameTitle, JCheckBoxMenuItem showTimeMenuItem, int minutes,
			int seconds) {
		if (showTimeMenuItem.isSelected()) {
			frame.setTitle(frameTitle + " - " + String.format("%02d:%02d", minutes, seconds));
		} else {
			frame.setTitle(frameTitle);
		}
	}

	// Sets the focus window to always on top
	public static void windowOnTop(JFrame frame, JCheckBoxMenuItem windowOnTopMenuItem) {
		if (windowOnTopMenuItem.isSelected()) {
			frame.setAlwaysOnTop(true);
		} else {
			frame.setAlwaysOnTop(false);
		}
	}

	// Enables or disables all options menu items
	public static void setOptionsMenuEnabled(JCheckBoxMenuItem showTimeMenuItem, JCheckBoxMenuItem windowOnTopMenuItem,
			JCheckBoxMenuItem darkModeMenuItem, JCheckBoxMenuItem lockedModeMenuItem, boolean enabled) {
		showTimeMenuItem.setEnabled(enabled);
		windowOnTopMenuItem.setEnabled(enabled);
		darkModeMenuItem.setEnabled(enabled);
		lockedModeMenuItem.setEnabled(enabled);
	}

	// Toggles dark mode for the main window and its components
	public static void toggleDarkMode(JFrame frame, JPanel timerPanel, JLabel focusTimerLabel,
			JLabel sessionCounterLabel, JButton startButton, boolean darkMode) {
		Color bg = darkMode ? new Color(40, 44, 52) : Color.WHITE;
		Color fg = darkMode ? Color.WHITE : Color.BLACK;
		frame.getContentPane().setBackground(bg);
		timerPanel.setBackground(bg);
		focusTimerLabel.setForeground(fg);
		sessionCounterLabel.setForeground(fg);
		sessionCounterLabel.setBackground(bg);
		if (timerPanel.getBorder() instanceof TitledBorder) {
			TitledBorder border = (TitledBorder) timerPanel.getBorder();
			border.setTitleColor(fg);
		}
		for (java.awt.Component c : frame.getContentPane().getComponents()) {
			if (c instanceof JPanel) {
				c.setBackground(bg);
				for (java.awt.Component cc : ((JPanel) c).getComponents()) {
					cc.setBackground(bg);
					cc.setForeground(fg);
					if (cc instanceof JLabel) {
						cc.setForeground(fg);
					}
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
		if (startButton != null) {
			startButton.setBackground(darkMode ? new Color(60, 63, 65) : new JButton().getBackground());
			startButton.setForeground(Color.BLACK);
		}
		frame.repaint();
	}
}
