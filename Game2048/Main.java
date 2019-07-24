package Game2048;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main {
	public static void main(String[] args) {
		JFrame game = new JFrame();
		First first = new First();
		game.add(first);
		first.start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == first.start) {
					game.remove(first);
					Game2048 game2048 = new Game2048();
					game.add(game2048);
					game2048.CountDown(game);
					game.add(game2048.label, BorderLayout.SOUTH);
				}
			}
		});
		
		game.setTitle("2048 Game");
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(340, 425);
		game.setResizable(false);

		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}
}
