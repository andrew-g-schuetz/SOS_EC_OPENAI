import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameBoardGUI extends JFrame {

    private Game game;
    private JButton[][] buttons;
    private SOSGameMode gameMode;
    private JRadioButton player1S, player1O, player2S, player2O;
    private ButtonGroup player1Group, player2Group;
    private boolean playerOneComputer;
    private boolean playerTwoComputer;

    public GameBoardGUI(Game game) {
        this.game = game;

        // Set game mode (Simple or General)
        if (game.getGameType().equals("Simple Game")) {
            this.gameMode = new SimpleGame(game);
        } else if (game.getGameType().equals("General Game")) {
            this.gameMode = new GeneralGame(game);
        }
        setTitle("SOS Game - " + game.getCurrentPlayer().getName() + "'s Turn");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for the game board
        JPanel boardPanel = new JPanel(new GridLayout(game.getBoard().getSize(), game.getBoard().getSize()));
        buttons = new JButton[game.getBoard().getSize()][game.getBoard().getSize()];
        initializeButtons(boardPanel);

        // Panel for the player selection
        JPanel playerSelectionPanel = new JPanel(new GridLayout(2, 2));
        player1S = new JRadioButton("S");
        player1O = new JRadioButton("O");
        player2S = new JRadioButton("S");
        player2O = new JRadioButton("O");

        player1Group = new ButtonGroup();
        player2Group = new ButtonGroup();

        // Group the radio buttons for each player
        player1Group.add(player1S);
        player1Group.add(player1O);
        player2Group.add(player2S);
        player2Group.add(player2O);

        // Default selection for each player
        player1S.setSelected(true);
        player2S.setSelected(true);

        playerSelectionPanel.add(new JLabel(game.getPlayerOne().getPlayerType() + " Player 1: " + game.getPlayerOne().getName()));
        playerSelectionPanel.add(player1S);
        playerSelectionPanel.add(player1O);
        playerSelectionPanel.add(new JLabel(game.getPlayerTwo().getPlayerType() + " Player 2: " + game.getPlayerTwo().getName()));
        playerSelectionPanel.add(player2S);
        playerSelectionPanel.add(player2O);

        // Add the panels to the frame
        add(boardPanel, BorderLayout.CENTER);
        add(playerSelectionPanel, BorderLayout.SOUTH);

        setVisible(true);

        // If it's the computer's turn, make the computer's first move automatically
        if (game.getCurrentPlayer().getPlayerType().equals("Computer")) {
            makeComputerMove();
        }
    }

    // Initialize the game board buttons
    private void initializeButtons(JPanel boardPanel) {
        for (int i = 0; i < game.getBoard().getSize(); i++) {
            for (int j = 0; j < game.getBoard().getSize(); j++) {
                buttons[i][j] = new JButton("-");
                int row = i;
                int col = j;

                // Add action listener to each button for making a move
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get the current player's letter based on the radio button selection
                        char currentLetter = game.getCurrentPlayer().getLetter();
                        if (game.getCurrentPlayer().getName().equals(game.getPlayerOne().getName())) {
                            if (player1S.isSelected()) {
                                currentLetter = 'S';
                            } else if (player1O.isSelected()) {
                                currentLetter = 'O';
                            }
                        } else {
                            if (player2S.isSelected()) {
                                currentLetter = 'S';
                            } else if (player2O.isSelected()) {
                                currentLetter = 'O';
                            }
                        }

                        // Make the human move
                        if (gameMode.makeMove(row, col, currentLetter)) {
                            buttons[row][col].setText(String.valueOf(currentLetter));


                            // Check if the game type is "Simple Game" and if an "SOS" is detected
                            if (game.getGameType().equals("Simple Game") && game.getBoard().checkForSOS(row, col)) {
                                gameMode.showResults();
                                dispose();
                                return;
                            }

                            if (!game.getBoard().checkForSOS(row, col)) {
                                game.switchTurns();
                            }

                            // Check if the game is over (board full or end condition met)
                            if (gameMode.isGameOver()) {
                                gameMode.showResults();
                                dispose();
                            } else {
                                updateTitle();

                            }


                            // Make the computer move if it's the computer's turn
                            if (game.getCurrentPlayer().getPlayerType().equals("Computer")) {

                                makeComputerMove();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid move. Try again.");
                        }
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }
    }

    private void makeComputerMove() {
        // If it's the computer's turn, make a random move
        int row, col;
        char currentLetter = game.getCurrentPlayer().getLetter();


        // Keep trying until we find an empty spot
        while (true) {
            row = (int) (Math.random() * game.getBoard().getSize());
            col = (int) (Math.random() * game.getBoard().getSize());

            // Check if the spot is empty (not 'S' or 'O')
            if (game.getBoard().getCell(row, col) == '-') {
                break;
            }
        }

        // Make the computer's move
        if (gameMode.makeMove(row, col, currentLetter)) {

            Timer timer = new Timer(2000, e ->{
                buttons[row][col].setText(String.valueOf(currentLetter));
            });

            //buttons[row][col].setText(String.valueOf(currentLetter));
            if (game.getGameType().equals("Simple Game") && game.getBoard().checkForSOS(row, col)) {
                gameMode.showResults();
                dispose();
                return;
            }

//            try {
//                // Delay for 2 seconds (2000 milliseconds)
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }

            if (!game.getBoard().checkForSOS(row, col)) {
                game.switchTurns();
            }

            // Check if the game is over (board full or end condition met)
            if (gameMode.isGameOver()) {
                gameMode.showResults();
                dispose();
            } else {
                updateTitle();
            }



        }
    }




    private void updateTitle() {
        setTitle("SOS Game - " + game.getCurrentPlayer().getName() + "'s Turn");
    }
}