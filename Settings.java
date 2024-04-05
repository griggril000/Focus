import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;

public class Settings {
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
}
