import javax.swing.JFrame;
import javax.swing.JCheckBox;

public class Settings {
	// Shows the remaining time in the title bar.
	public static void showTime(JFrame frame, String frameTitle, JCheckBox showTimeCheckbox, int minutes, int seconds) {
		if (showTimeCheckbox.isSelected()) {
			frame.setTitle(frameTitle + " - " + String.format("%02d:%02d", minutes, seconds));
		} else {
			frame.setTitle(frameTitle);
		}
	}

	// Sets the focus window to always on top
	public static void windowOnTop(JFrame frame, JCheckBox windowOnTopCheckBox) {
		if (windowOnTopCheckBox.isSelected()) {
			frame.setAlwaysOnTop(true);
		} else {
			frame.setAlwaysOnTop(false);
		}
	}
}
