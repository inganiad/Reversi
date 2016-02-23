import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ReversiWithGUI{
	public static void main(String[] args){
	
		reversiGUI gui = new reversiGUI();
		
	}
}
	
class reversiGUI extends JFrame{

	private final int WIDTH = 600;
					  
	private Color[] colors = {Color.blue, Color.red},
	
					colorList = {Color.blue, Color.red, Color.white, Color.black, 
								 Color.cyan, Color.magenta, new Color(204,85,0), new Color(80,0,0)};
	
	private ColorPanel[] board;
	private ColorPanel turnPanel = new ColorPanel(colors[0]);
					   
	
	private int color = 01,
				nextColor = 01,
				
				size = 6,
				nextSize = 6,
				sizeSquared,
				
				player = 1,
				nextPlayer = 1,
				
				difficulty = 0,
				nextDifficulty = 0;
				
	private int[] playerScore = new int[2];
	
	private boolean isCompTurn = false,
					turnSkipped = false;
	
	
	private HashMap<Integer, ArrayList<Integer>> moves;
	private HashMap<String,Integer> colorMap = new HashMap<String,Integer>(),
									sizeMap = new HashMap<String,Integer>(),
									playerMap = new HashMap<String,Integer>(),
									difficultyMap = new HashMap<String,Integer>();
	
	private Container pane;
	
	private JPanel boardPanel = new JPanel(new GridLayout(size,size)),
				   infoPanel = new JPanel(new GridLayout(3,7));
	
	

	
	//================================Constrctors==========================================================
	
	
	
	public reversiGUI(){
	
		JFrame GUI = new JFrame("Reversi GUI");
		
		GUI.setSize(WIDTH, 850);
		GUI.setResizable(false);
		GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// GUI.setLocation(1750, 250);
		GUI.setLocationRelativeTo(null);
		
		
		//
		// Create and populate menus and menu items
		//
		
		JMenuBar menuBar = new JMenuBar();
		
		
		//Add game menu
		JMenu gameMenu = new JMenu("Game");
		
		JMenuItem newGameItem = new JMenuItem("New Game"),
				  restartGameItem = new JMenuItem("Restart Game");
		
		newGameItem.addActionListener(new SettingListener("new"));
		restartGameItem.addActionListener(new SettingListener("restart"));
		
		gameMenu.add(newGameItem);
		gameMenu.add(restartGameItem);
		
		// gameMenu.setMnemonic(KeyEvent.VK_G);
		
		menuBar.add(gameMenu);
		
		
		
		String[] menuTitles = {"Colors","Size","Players","Difficulty"};
		// int[] menuMnemonics = {KeyEvent.VK_C,KeyEvent.VK_S,KeyEvent.VK_P,KeyEvent.VK_D};
		
		String[][] buttonTitles = {
									{"Blue/Red","White/Black","Cyan/Magenta","UT/A&M"},
									{"6 X 6","8 X 8","10 X 10","12 X 12","14 X 14","16 X 16"},
									{"1P (Human vs. Computer)","2P (Human vs. Human)"},
									{"Easy","Hard"}
								  },
								  
				   buttonSettings = {
									  {"01","23","45","67"},
									  {"6","8","10","12","14","16"},
									  {"one","two"},
									  {"easy","hard"}
									};
							 
		// int[][] buttonMnemonics = {
									// {KeyEvent.VK_B, KeyEvent.VK_W, KeyEvent.VK_Y, KeyEvent.VK_U},
									// {},
									// {KeyEvent.VK_1, KeyEvent.VK_2},
									// {KeyEvent.VK_E, KeyEvent.VK_H}
								  // };
		
		//Add buttons to button groups and their respective menu, and add menu to menuBar
		for (int menuIndex = 0; menuIndex < menuTitles.length; menuIndex++){
			
			JMenu tempMenu = new JMenu(menuTitles[menuIndex]);
			ButtonGroup tempGroup = new ButtonGroup();
			
			// tempMenu.setMnemonic(menuMnemonics[menuIndex]);
		
			for (int buttonIndex = 0; buttonIndex < buttonTitles[menuIndex].length; buttonIndex++){
			
				JRadioButtonMenuItem tempButton = new JRadioButtonMenuItem(buttonTitles[menuIndex][buttonIndex]);
				
				tempButton.addActionListener(new SettingListener(buttonSettings[menuIndex][buttonIndex]));
				
				if (buttonIndex == 0){
					tempButton.setSelected(true);
				}
				
				// if (menuIndex != 1){
					// tempButton.setMnemonic(buttonMnemonics[menuIndex][buttonIndex]);
				// }
				
				tempGroup.add(tempButton);
				tempMenu.add(tempButton);
				
			}
			
			menuBar.add(tempMenu);
			
		}
		
		GUI.setJMenuBar(menuBar);
		
		
		//Populate maps
		
		colorMap.put("01",01);
		colorMap.put("23",23);
		colorMap.put("45",45);
		colorMap.put("67",67);
		
		sizeMap.put("6",6);
		sizeMap.put("8",8);
		sizeMap.put("10",10);
		sizeMap.put("12",12);
		sizeMap.put("14",14);
		sizeMap.put("16",16);
		
		playerMap.put("one",1);
		playerMap.put("two",2);
		
		difficultyMap.put("easy",0);
		difficultyMap.put("hard",1);
		
		
		//Run a new game
		pane = GUI.getContentPane();
		
		newGame("new");
		
		GUI.setVisible(true);
		
		
	}
	

	
	//================================Utility Methods======================================================
	
	
	
	private void swap(int[] x, int i, int j){
	
		int temp = x[i];
		x[i] = x[j];
		x[j] = temp;
	
	}
	
	private void swap(Color[] x, int i, int j){
	
		Color temp = x[i];
		x[i] = x[j];
		x[j] = temp;
	
	}
	
	private void setAbsoluteSize(JPanel panel, int width, int height){
		panel.setMinimumSize(new Dimension(width,height));
		panel.setPreferredSize(new Dimension(width,height));
		panel.setMaximumSize(new Dimension(width,height));
	}
	
	
	//================================Variable Methods=====================================================
	
	
	private void makeNewBoard(){
	
		board = new ColorPanel[sizeSquared];
		
		for (int i = 0; i < sizeSquared; i++){
			board[i] = new ColorPanel(Color.darkGray);
			board[i].addMouseListener(new PanelListener(i));
		}
		
		board[ (size-1)*(size-1)/2 + size/2 - 1] = new ColorPanel(colors[0]);
		board[ (size-1)*(size-1)/2 + size/2    ] = new ColorPanel(colors[1]);
		board[ size*size/2 + size/2 - 1		   ] = new ColorPanel(colors[1]);
		board[ size*size/2 + size/2 		   ] = new ColorPanel(colors[0]);
	
		for (ColorPanel p: board){
			p.setBorder(BorderFactory.createLineBorder(Color.gray));
		}
	
		playerScore[0] = 2;
		playerScore[1] = 2;
	}
	
	private HashMap<Integer, ArrayList<Integer>> findMoves(){
	
		HashMap<Integer, ArrayList<Integer>> moveMap = new HashMap<Integer, ArrayList<Integer>>();
	
		for (int moveIndex = 0; moveIndex < board.length; moveIndex++){
			
			if (board[moveIndex].getColor().equals(Color.darkGray)){
				
				int x = moveIndex%size, 
					y = moveIndex/size,
					posM = size+1,
					negM = size-1;
				
				//Holds appropriate values for checking indices in all 8 directions 
				int[] increment = { -posM,	//Negative diagonal, m > 0
									-size,			//Negative vertical
									-negM,	//Negative diagonal, m < 0
									-1,					//Negative horizontal
									1,					//Positive horizontal
									negM,	//Positive diagonal, m < 0
									(size),				//Positive vertical
									posM};	//Positive diagonal, m > 0
				
				
				//Mathematically calculated values which create the "boundaries" in all 8 directions
				int[] terminate = 	{	
										moveIndex - posM * x, 
										x, 
										moveIndex - negM * (negM-x), 
										size * y, 
										size * (y+1) - 1, 
										moveIndex + negM * x, 
										sizeSquared - (size-x), 
										moveIndex + posM * (negM-x)
									};
								  
				
				ArrayList<Integer> caps = new ArrayList<Integer>();
				
				for (int i = 0; i < 8; i++){
					
					int incrementValue = increment[i],
						terminateValue = terminate[i];
						
					ArrayList<Integer> tempList = new ArrayList<Integer>();
						
					if (moveIndex == terminateValue){
						continue;
					}
						
					for (int pos = moveIndex + incrementValue; 
						pos >= 0 && pos < sizeSquared; 
						pos += incrementValue){
						
						if (board[pos].getColor().equals(colors[0])){
							caps.addAll(tempList);
							break;
						}
						else if (board[pos].getColor().equals(colors[1])){
							tempList.add(pos);
						}
						else if (board[pos].getColor().equals(Color.darkGray)){
							break;
						}
						
						if (pos == terminateValue){
							break;
						}
						
					}
				}
				
				if (caps.size() > 0){
					moveMap.put(moveIndex, caps);
				}
				
			}
		}
	
		// for (int key: moveMap.keySet())
			// System.out.print("\n"+key);
		return moveMap;
	}
	
	private void flipPanels(int moveIndex,ArrayList<Integer> toFlip){
	
		int temp = 0;
			
		board[moveIndex] = new ColorPanel(colors[0]);
		board[moveIndex].setBorder(BorderFactory.createLineBorder(Color.gray));
		
		for (int i: toFlip){
			board[i] = new ColorPanel(colors[0]);
			board[i].setBorder(BorderFactory.createLineBorder(Color.gray));
			temp++;
			
		}
		
		if ( colors[0] == colorList[color/10]){
			playerScore[0] += temp + 1;
			playerScore[1] -= temp;
		}
		else{
			playerScore[1] += temp + 1;
			playerScore[0] -= temp;
		}
		
		
	}
	
	private void handleHumanMove(int moveIndex){
		
		if (!isCompTurn && moves.keySet().contains(moveIndex)){
		
			flipPanels(moveIndex, moves.get(moveIndex));
			nextMove();	
			
		}
		
	}
	
	private void nextMove(){
	
		if (player == 1){
			isCompTurn = !isCompTurn;
		}
		
		swap(colors, 0, 1);
		moves = findMoves();
		paintBoard();
		paintInfoPanel();
		
		
		
		if (moves.size() < 1){
		
			JOptionPane.showMessageDialog(null,"No possible moves, skipping turn.");
		
			if (turnSkipped == true){
				JOptionPane.showMessageDialog(null,"No possible moves for the either color, game over.");
				endGame();
			}
			else{
				turnSkipped = true;
				nextMove();
			}	
			
		}
		else{
			turnSkipped = false;
			if (isCompTurn){
				handleCompMove();
			}	
		}
		
	}
	
	private void updateSettings(String setting){
	
		if (setting == "new"){
			newGame("new");
		}
		if (setting == "restart"){
			newGame("restart");
		}
		
		if (colorMap.keySet().contains(setting)){
			nextColor = colorMap.get(setting);
			return;
		}
		
		if (sizeMap.keySet().contains(setting)){
			nextSize = sizeMap.get(setting);
			return;
		}
		
		if (playerMap.keySet().contains(setting)){
			nextPlayer = playerMap.get(setting);
			return;
		}
		
		if (difficultyMap.keySet().contains(setting)){
			nextDifficulty = difficultyMap.get(setting);
			return;
		}
	
	}
	
	private void handleCompMove(){
	
		Random numGen = new Random();
		
		
		ArrayList<Integer> bestMoveList = new ArrayList<Integer>();
		
		if (difficulty == 0){
			for (int move: moves.keySet()){
				bestMoveList.add(move);
			}
		}
		else{
		
			int maxCaps = 0;
			
			for (int move: moves.keySet()){
			
				if (moves.get(move).size() > maxCaps){
				
					maxCaps = moves.get(move).size();
					bestMoveList = new ArrayList<Integer>();
					bestMoveList.add(move);
					
				}
				else if (moves.get(move).size() == maxCaps){
					bestMoveList.add(move);
				}
			
			}
		}
		
		//Chose random move
		int randomListIndex = numGen.nextInt(bestMoveList.size()),
			count = 0;
		
		for (int move: bestMoveList){
			
			if (count == randomListIndex){
				flipPanels(move, moves.get(move));
				// JOptionPane.showMessageDialog(null, "Computer chose "+move);
				
				break;
			}
			
			count++;
		}
		
		
		nextMove();
			
	}
	
	private void endGame(){
		if (playerScore[0] > playerScore[1]){
			JOptionPane.showMessageDialog(null,"Player 1 Wins!!");
		}
		else{
			JOptionPane.showMessageDialog(null,"Player 2 Wins!!");
		}
	}
	
	//================================GUI Methods==========================================================
	
	
	private void paintBoard(){
		
		boardPanel.removeAll();
		boardPanel.setLayout(new GridLayout(size, size));
		
		for (ColorPanel panel: board){
			boardPanel.add(panel);
		}
		
		boardPanel.setBackground(Color.darkGray);
		boardPanel.revalidate();
		
	}
	
	private void newGame(String gameType){
	
		if (gameType.equals("new")){
			color = nextColor;
			
			size = nextSize;
			
			player = nextPlayer;
			
			difficulty = nextDifficulty;
			
			sizeSquared = size * size;
		}
		
		colors[0] = colorList[color/10];
		colors[1] = colorList[color%10];
		
		makeNewBoard();
		paintBoard();
		paintInfoPanel();
		
		pane.removeAll();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		setAbsoluteSize(boardPanel, WIDTH, WIDTH);
		setAbsoluteSize(infoPanel, WIDTH, 250);
		
		pane.add(boardPanel);
		pane.add(infoPanel);
		
		moves = findMoves();
	}
	
	private void paintInfoPanel(){
	
	
		//Update score and turn panel
		turnPanel = new ColorPanel(colors[0]);
		turnPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		JPanel[] scorePanels = new JPanel[2];
		Color[] playerColor = {colorList[color/10],colorList[color%10]};
		
		for (int panelCount = 0; panelCount < scorePanels.length; panelCount++){
		
			scorePanels[panelCount] = new JPanel(new GridLayout(size,size));
			
			scorePanels[panelCount].setBackground(Color.darkGray);
			scorePanels[panelCount].setBorder(BorderFactory.createLineBorder(Color.gray));
			
			
			for (int gridIndex = 0; gridIndex < sizeSquared; gridIndex++){
			
				ColorPanel tempScoreColorPanel;
				
				if (gridIndex < playerScore[panelCount]){
					tempScoreColorPanel = new ColorPanel(playerColor[panelCount]);
					tempScoreColorPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
				}
				else{
					tempScoreColorPanel = new ColorPanel(Color.darkGray);
				}
				
				scorePanels[panelCount].add(tempScoreColorPanel);
				
			}
		}
		
		//Paint info panel
		infoPanel.removeAll();
		infoPanel.setLayout(new GridLayout(3,7));
		infoPanel.setBackground(Color.darkGray);
		
		for (int gridIndex = 0; gridIndex < 21; gridIndex++){
		
			if (gridIndex == 8){
				infoPanel.add(scorePanels[0]);
			}
			else if (gridIndex == 10){
				infoPanel.add(turnPanel);
			}
			else if (gridIndex == 12){
				infoPanel.add(scorePanels[1]);
			}
			else{
				infoPanel.add(new ColorPanel(Color.darkGray));
			}
		
		}
		
		infoPanel.revalidate();
	}
	
	//================================Listeners============================================================
	
	private class PanelListener extends MouseAdapter{

		private int panelNumber = 0;
		
		public PanelListener(int location){
			panelNumber = location;
		}
		
		public void mouseClicked(MouseEvent e){
			
			handleHumanMove(panelNumber);
			// findColors();
			// moves = findMoves();
			// JOptionPane.showMessageDialog(null, "You clicked on panel "+panelNumber);
		}
	}
	
	private class SettingListener implements ActionListener{
	
		private String setting;
		
		public SettingListener(String str){
		
			setting = str;
		
		}
		
		public void actionPerformed(ActionEvent e){
		
			updateSettings(setting);
		
		}
	
	}
	
	
}

class ColorPanel extends JPanel{

	private Color bgColor;
	
	public ColorPanel(Color bg){
	
		bgColor = bg;
		setBackground(bgColor);
		
	}
	
	public Color getColor(){
		return bgColor;
	} 
	
}

