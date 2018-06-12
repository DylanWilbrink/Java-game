package Whackamole;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Whackamole implements ActionListener {
	private static final String OFF_STRING = "----";
	private static final String UP_STRING = "klik";
	private static final String DOWN_STRING = "Geklikt";
	private static final Color OFF_COLOR = Color.LIGHT_GRAY;
	private static final Color UP_COLOR = Color.RED;
	private static final Color DOWN_COLOR = Color.GREEN;

	private static int count = 20;
	private static int score;
	private JButton startButton;
	private JButton[] buttons;
	private JLabel timeLabel, scoreLabel;
	private JTextArea timeArea;
	private JTextArea scoreArea;
	private static Random random = new Random();

	public Whackamole() {
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);

		JFrame frame = new JFrame("Whack-a-Mole");
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pane = new JPanel();

		startButton = new JButton("Start");
		pane.add(startButton);
		startButton.addActionListener(this);

		timeLabel = new JLabel("Time Left:");
		pane.add(timeLabel);

		timeArea = new JTextArea(1, 5);
		timeArea.setEditable(false);
		pane.add(timeArea);
		timeArea.setVisible(true);

		scoreLabel = new JLabel("Score:");
		pane.add(scoreLabel);

		scoreArea = new JTextArea(1, 5);
		scoreArea.setEditable(false);
		pane.add(scoreArea);
		scoreArea.setVisible(true);

		buttons = new JButton[25];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton(OFF_STRING);
			buttons[i].setOpaque(true);
			buttons[i].setFont(font);
			buttons[i].setBackground(OFF_COLOR);
			pane.add(buttons[i]);
			buttons[i].addActionListener(this);
		}

		frame.setContentPane(pane);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		new Whackamole();
	}

	public static class MoleThread extends Thread {
		JButton button;

		MoleThread(JButton button) {
			this.button = button;
			if (count > -1) {
				if (button.getText().equals(OFF_STRING)) {
					button.setText(UP_STRING);
					button.setBackground(UP_COLOR);

				} else {
					button.setText(OFF_STRING);
					button.setBackground(OFF_COLOR);
				}
			}
		}

		public void run() {
			while (count > -1) {
				int randomSleepTime = random.nextInt(10000);
				synchronized (button) {
					if (button.getText().equals(OFF_STRING)) {
						button.setText(UP_STRING);
						button.setBackground(UP_COLOR);

					} else {
						button.setText(OFF_STRING);
						button.setBackground(OFF_COLOR);
					}

				}
				try {
					Thread.sleep(randomSleepTime);
				} catch (InterruptedException e) {
					// Should not happen
					throw new AssertionError(e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}

			}

			if (count == -1) {
				button.setText(OFF_STRING);
				button.setBackground(OFF_COLOR);
			}

		}
	}
	
	public void playSound(String soundName)
	 {
	   try 
	   {
	    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
	    Clip clip = AudioSystem.getClip( );
	    clip.open(audioInputStream);
	    clip.start( );
	   }
	   catch(Exception ex)
	   {
	     System.out.println("Error with playing sound.");
	     ex.printStackTrace( );
	   }
	 }

	@Override
	public void actionPerformed(ActionEvent e) {

		Thread[] moleThread = new Thread[buttons.length];

		if (e.getSource() == startButton) {
			startButton.setEnabled(false);
			playSound("sound/newstart.wav"); //start geluid
			

			// create timer, start timer, create array of mole threads, start
			Thread timerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (count > -1) {

						try {

							timeArea.setText("" + count);
							count--;
							Thread.sleep(2000);
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count = 30;
					score = 0;
					timeArea.setText("" + count);
					scoreArea.setText("" + score);
					startButton.setEnabled(true);
				}
			});

			timerThread.start();

			for (int i = 0; i < moleThread.length; i++) {
				int randomMoleNum = random.nextInt(buttons.length);
				JButton button = buttons[randomMoleNum];
				moleThread[i] = new MoleThread(button);
				moleThread[i].start();

			}
		}
		for (int i = 0; i < buttons.length; i++) {
			if (e.getSource() == buttons[i]) {
				if (count > 0) {
					if (buttons[i].getText().equals(UP_STRING)) {
						score++;
						scoreArea.setText("" + score);
						buttons[i].setText(DOWN_STRING);
						buttons[i].setBackground(DOWN_COLOR);
						
						playSound("sound/jump.wav"); // mol maakt geluid wanneer erop geklikt wordt.

					}
				}
			}

		}
	}

}