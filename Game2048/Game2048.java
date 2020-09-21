package Game2048;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game2048 extends JPanel {
	private static final Color BG_COLOR = new Color(0xbbada0);
	private static final String FONT_NAME = "Arial";
	private static final int TILE_SIZE = 64;
	private static final int TILES_MARGIN = 16;
	static JLabel label;
	private Tile[] myTiles;
	static boolean myWin = false;
	static boolean myLose = false;
	int myScore = 0;
	Image imgfor2, imgfor4, imgfor8, imgfor16, imgfor32, imgfor64, imgfor128, imgfor256, imgfor512, imgfor1024, imgfor2048;
	public long startTime=0;
	public long endTime=0;
	public long now=0;
	
	public Game2048() {
		setPreferredSize(new Dimension(340, 400));
		setFocusable(true);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					resetGame();
				if (!canMove())
					myLose = true;
				if (!myWin && !myLose){
					switch (e.getKeyCode()){
						case KeyEvent.VK_LEFT:  
							left();
							break;
						case KeyEvent.VK_RIGHT:
							right();
							break;
						case KeyEvent.VK_DOWN:
							down();
							break;
						case KeyEvent.VK_UP:
							up();
							break;
					}
				}
				if (!myWin && !canMove()) {
					myLose = true;
				}
				repaint();
			}
		});
		resetGame();
	}
	public void resetGame() {
		myScore = 0;
		myWin = false;
		myLose = false;
		myTiles = new Tile[4 * 4];
		for (int i = 0; i < myTiles.length; i++) {
			myTiles[i] = new Tile();
		}
		addTile();
		addTile();
		startTime = System.currentTimeMillis();
	}

	public void left() {
		boolean needAddTile = false;
		for (int i = 0; i < 4; i++) {
			Tile[] line = getLine(i);
			Tile[] merged = mergeLine(moveLine(line));
			setLine(i, merged);
			if (!needAddTile && !compare(line, merged)) {
				needAddTile = true;
			}
		}
		if (needAddTile) {
			addTile();
		}
	}
	
	public void right() {
		myTiles = rotate(180);
		left();
		myTiles = rotate(180);
	}
	
	public void up() {
		myTiles = rotate(270);
		left();
		myTiles = rotate(90);
	}
	
	public void down() {
		myTiles = rotate(90);
		left();
		myTiles = rotate(270);
	}
	
	private Tile tileAt(int x, int y) {
		return myTiles[x + y * 4];
	}
	
	private void addTile() {
		List<Tile> list = availableSpace();
		if (!availableSpace().isEmpty()) {
			int index = (int) (Math.random() * list.size()) % list.size();
			Tile emptyTime = list.get(index);
			emptyTime.value = Math.random() < 0.9 ? 2 : 4;
		}
	}
	
	private List<Tile> availableSpace() {
		final List<Tile> list = new ArrayList<Tile>(16);
		for (Tile t : myTiles) {
			if (t.isEmpty()) {
				list.add(t);
			}
		}
		return list;
	}
	
	private boolean isFull() {
		return availableSpace().size() == 0;
	}
	
	boolean canMove() {
		if (!isFull()) {
			return true;
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				Tile t = tileAt(x, y);
				if ((x < 3 && t.value == tileAt(x + 1, y).value)|| ((y < 3) && t.value == tileAt(x, y + 1).value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean compare(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		} else if (line1.length != line2.length) {
			return false;
		}
		for (int i = 0; i < line1.length; i++) {
			if (line1[i].value != line2[i].value) {
				return false;
			}
		}
		return true;
	}
	
	private Tile[] rotate(int angle) {
		Tile[] newTiles = new Tile[4 * 4];
		int offsetX = 3, offsetY = 3;
		if (angle == 90) {
			offsetY = 0;
		} else if (angle == 270) {
			offsetX = 0;
		}
		
		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[(newX) + (newY) * 4] = tileAt(x, y);
			}
		}
		return newTiles;
	}
	
	private Tile[] moveLine(Tile[] oldLine) {
		LinkedList<Tile> l = new LinkedList<Tile>();
		for (int i = 0; i < 4; i++) {
			if (!oldLine[i].isEmpty())
				l.addLast(oldLine[i]);
		}
		if (l.size() == 0) {
			return oldLine;
		} else {
			Tile[] newLine = new Tile[4];
			ensureSize(l, 4);
			for (int i = 0; i < 4; i++) {
				newLine[i] = l.removeFirst();
			}
			return newLine;
		}
	}
	
	private Tile[] mergeLine(Tile[] oldLine) {
		LinkedList<Tile> list = new LinkedList<Tile>();
		for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
			int num = oldLine[i].value;
			if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
				num *= 2;
				myScore += num;
				int ourTarget = 2048;
				if (num == ourTarget) {
					myWin = true;
				}
				i++;
			}
			list.add(new Tile(num));
		}
		if (list.size() == 0) {
			return oldLine;
		} else {
			ensureSize(list, 4);
			return list.toArray(new Tile[4]);
		}
	}
	
	private static void ensureSize(java.util.List<Tile> l, int s) {
		while (l.size() != s) {
			l.add(new Tile());
		}
	}
	
	private Tile[] getLine(int index) {
		Tile[] result = new Tile[4];
		for (int i = 0; i < 4; i++) {
			result[i] = tileAt(i, index);
		}
		return result;
	}
	
	private void setLine(int index, Tile[] re) {
		System.arraycopy(re, 0, myTiles, index * 4, 4);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				drawTile(g, myTiles[x + y * 4], x, y);
			}
		}
	}
	
	private void drawTile(Graphics g2, Tile tile, int x, int y) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		imgfor2 = tk.getImage("2.png");
		imgfor4 = tk.getImage("4.png");
		imgfor8 = tk.getImage("8.png");
		imgfor16 = tk.getImage("16.png");
		imgfor32 = tk.getImage("32.png");
		imgfor64 = tk.getImage("64.png");
		imgfor128 = tk.getImage("128.png");
		imgfor256 = tk.getImage("256.png");
		imgfor512 = tk.getImage("512.png");
		imgfor1024 = tk.getImage("1024.png");
		imgfor2048 = tk.getImage("2048.png");
		Graphics2D g = ((Graphics2D) g2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		
		int value = tile.value;
		int xOffset = offsetCoors(x);
		int yOffset = offsetCoors(y);
		g.setColor(tile.getBackground());
		g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
		g.setColor(tile.getForeground());
		final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
		final Font font = new Font(FONT_NAME, Font.BOLD, size);
		g.setFont(font);
		
		String s = String.valueOf(value);
		int s1 = Integer.valueOf(value);
		final FontMetrics fm = getFontMetrics(font);
		
		final int w = fm.stringWidth(s);
		final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];
		
		switch (s1) {
			case (2):     g.drawImage(imgfor2, xOffset + (TILE_SIZE - w) / 2-22, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);  break;
			case (4):    g.drawImage(imgfor4, xOffset + (TILE_SIZE - w) / 2-22, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);break;
			case (8):    g.drawImage(imgfor8, xOffset + (TILE_SIZE - w) / 2-22, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);break;
			case (16):    g.drawImage(imgfor16, xOffset + (TILE_SIZE - w) / 2-12, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);break;
			case (32):   g.drawImage(imgfor32, xOffset + (TILE_SIZE - w) / 2-12, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);break;
			case (64):  g.drawImage(imgfor64, xOffset + (TILE_SIZE - w) / 2-12, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -48,TILE_SIZE,TILE_SIZE,this);break;
			case (128):    g.drawImage(imgfor128, xOffset + (TILE_SIZE - w) / 2-5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -46,TILE_SIZE,TILE_SIZE,this);break;
			case (256):  g.drawImage(imgfor256, xOffset + (TILE_SIZE - w) / 2-5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -46,TILE_SIZE,TILE_SIZE,this);break;
			case (512):  g.drawImage(imgfor512, xOffset + (TILE_SIZE - w) / 2-5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -46,TILE_SIZE,TILE_SIZE,this);break;
			case (1024):  g.drawImage(imgfor1024, xOffset + (TILE_SIZE - w) / 2-5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2-46,TILE_SIZE,TILE_SIZE,this);break;
			case (2048):  g.drawImage(imgfor2048, xOffset + (TILE_SIZE - w) / 2-5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 -46,TILE_SIZE,TILE_SIZE,this);break;
		}
		
		if (myWin || myLose) {
			endTime = System.currentTimeMillis();
			g.setColor(new Color(255, 255, 255, 30));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(78, 139, 202));
			g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
			long end = (endTime-startTime)/1000;
			g.drawString("T : "+end+"s", 62,300);
			if (myWin) {
				g.drawString("You won!", 68, 150);
			}
			if (myLose) {
				g.drawString("Game over!", 50, 130);
				g.drawString("You lose!", 64, 200);
			}
			if (myWin || myLose) {
				g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				g.setColor(new Color(128, 128, 128, 128));
				g.drawString("Press ESC to play again", 80, getHeight() - 40);
			}
		}
		now = System.currentTimeMillis();
		g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
		g.drawString("Score: " + myScore, 200, 365);
	}
	
	private static int offsetCoors(int arg) {
		return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
	}
	
	static class Tile {
		int value;
		
		public Tile() {
			this(0);
		}
		
		public Tile(int num) {
			value = num;
		}
		
		public boolean isEmpty() {
			return value == 0;
		}
		
		public Color getForeground() {
			return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
		}
		
		public Color getBackground() {
			switch (value) {
				case 2:    return new Color(0xeee4da);
				case 4:    return new Color(0xede0c8);
				case 8:    return new Color(0xf2b179);
				case 16:   return new Color(0xf59563);
				case 32:   return new Color(0xf67c5f);
				case 64:   return new Color(0xf65e3b);
				case 128:  return new Color(0xedcf72);
				case 256:  return new Color(0xedcc61);
				case 512:  return new Color(0xedc850);
				case 1024: return new Color(0xedc53f);
				case 2048: return new Color(0xedc22e);
			}
			return new Color(0xcdc1b4);
		}
	}
	
	public static void CountDown(JFrame jf) {
		label = new JLabel("����");
		label.setFont(new Font("", Font.BOLD, 15));
		(new MyThread()).start(); //������ ����
	}
}    

class MyThread extends Thread {
	@Override
	public void run() {
		for (int i = 1; ; i++) {
			try {
				Thread.sleep(1000);// �����带 ������ 1�ʰ� ����...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Game2048.myWin || Game2048.myLose)
				i = 0;
			Game2048.label.setText(i+ " ");
		}
	}
}

