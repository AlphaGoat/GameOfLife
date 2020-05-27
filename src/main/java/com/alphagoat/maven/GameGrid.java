/* Heavily inspired (and almost completely copied from) Hovercraft Full of Eel's
 * post on stackoverflow
 * https://stackoverflow.com/questions/36380516/drawing-a-grid-in-a-jframe */
package com.alphagoat.maven;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.util.Random;

public class GameGrid extends JFrame {

    /* Dimensions of game grid */
    private int num_rows;
    private int num_cols;

    private int GAP = 3;
    private Color BG = Color.BLACK;
    private Color EMPTY_COLOR = Color.WHITE;
    private Color COLONY_COLOR = Color.YELLOW;
    private boolean stopFlag = false;

    private GridIdentifier[][] grid;
    private GridPanel[][] gridPanels;

    /* Main panel with the game grid */
    private JPanel mainPanel;

    /* Rule set (responsible for taking in the current game grid
     * as input and outputting the next state of the game grid as
     * output */
    private RuleSet ruleSet = new RuleSet();

    public static void main(String args[]) {

        int num_rows = 20;
        int num_cols = 20;

        /* Handle command line arguments */
        if (args.length == 1) {
            try {
                num_rows = num_cols = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                /* User input could not be parsed. Display help message */
//                Help();
                System.out.println("Usage: GameOfLife [options]\n" +
                        "\toptional args:\n" +
                            "\t\tnum_rows (int): number of rows in game grid. Will also\n" +
                                "be used as number of columns if no num_cols arg is provided\n" +
                            "\t\tnum_cols (int): number of columns in game grid.\n");
            }
        }

        else if (args.length > 1) {
            try {
                num_rows = Integer.parseInt(args[0]);
                num_cols = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                /* User input could not be parsed. Display help message */
//                Help();
                System.out.println("Usage: GameOfLife [options]\n" +
                        "\toptional args:\n" +
                            "\t\tnum_rows (int): number of rows in game grid. Will also\n" +
                                "be used as number of columns if no num_cols arg is provided\n" +
                            "\t\tnum_cols (int): number of columns in game grid.\n");
            }
        }

        GameGrid gameGrid = new GameGrid(num_rows, num_cols);
        gameGrid.constructGUI();
    }

    public GameGrid(int num_rows, int num_cols) {
        this.num_rows = num_rows;
        this.num_cols = num_cols;
    }

    private JPanel initGridPanel() {
        /* Initializes grid panel for game of life */
        JPanel gameGridPanel = new JPanel(new GridLayout(this.num_rows, this.num_cols));
        gameGridPanel.setBorder(BorderFactory.createEmptyBorder(this.GAP, this.GAP, this.GAP, this.GAP));
        gameGridPanel.setBackground(BG);
        gameGridPanel.setSize(new Dimension(480, 480));
        this.gridPanels = new GridPanel[num_rows][num_cols];
        this.grid = new GridIdentifier[num_rows][num_cols];

        for (int i = 0; i < gridPanels.length; i++) {
            for (int j = 0; j < gridPanels[i].length; j++) {
                /* Place empty space in game grid */
                this.gridPanels[i][j] = new GridPanel(GridIdentifier.EMPTY_SPACE, i, j);
                this.grid[i][j] = GridIdentifier.EMPTY_SPACE;
                gameGridPanel.add(this.gridPanels[i][j]);
            }
        }

        return gameGridPanel;
    }

    class GridPanel extends JPanel {
        GridIdentifier identifier;
        int row;
        int col;

        public GridPanel(GridIdentifier identifier, int row, 
                int col) {
            this.identifier = identifier;
            this.row = row;
            this.col = col;
            setBorder(BorderFactory.createLineBorder(BG, 1, false));
            setBackground(identifier.getColor());
            addMouseListener(new PanelMouseListener(this.identifier));
        }

        private class PanelMouseListener extends MouseAdapter {
            private GridIdentifier identifier;

            public PanelMouseListener(GridIdentifier identifier) {
                this.identifier = identifier;
            }

            public void mousePressed(MouseEvent e) {
                switch(this.identifier.getValue()) {
                    case 0:
                        this.identifier = GridIdentifier.COLONY;
                        setBackground(GridIdentifier.COLONY.getColor());
                        grid[row][col] = this.identifier;
                        break;

                    case 1:
                        this.identifier = GridIdentifier.EMPTY_SPACE;
                        setBackground(GridIdentifier.EMPTY_SPACE.getColor());
                        grid[row][col] = this.identifier;
                        break;
                }
            }
        }

        public void changeColor() {
            setBackground(this.identifier.getColor());
        }
    }

    public JPanel initButtonPanel() {

        JPanel buttonPanel = new JPanel();

        /* Button to start game of life */
        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.setVisible(true);
        startButton.addActionListener(new StartListener());

        /* Button to stop game of life at current grid state */
        JButton stopButton = new JButton();
        stopButton.setText("Stop");
        stopButton.setVisible(true);
        stopButton.addActionListener(new StopListener());

        /* Slider to adjust the size of the game grid before the game 
         * is run */
        JSlider dimSlider = new JSlider(JSlider.HORIZONTAL, 20, 50, 20);
        dimSlider.addChangeListener(new DimSliderListener());


        /* Combo box with predefined starting colonies */
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(dimSlider);
        buttonPanel.setVisible(true);


        return buttonPanel;
    }                           

    Timer gameStartTimer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startGameOfLife();
        }
    });

    class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            gameStartTimer.start();
        }
    }

    class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            gameStartTimer.stop();
        }
    }

    class DimSliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent ev) {
           this.num_rows = this.num_cols = ev.getSource().getValue();
           remove(mainPanel);
           mainPanel = initGridPanel();
           getContentPane.add(mainPanel, BorderLayout.CENTER);
        }
    }

    private void startGameOfLife() {
        /* Function that runs through main loop of game.
         * Terminates after five turns of no change of state */
    
        /* Consult rule set to obtain the next state of the game */
        this.grid = ruleSet.iterateGrid(this.grid);

        /* Change grid display on gui to reflect new state of grid */
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (this.grid[i][j] != this.gridPanels[i][j].identifier) {
                    this.gridPanels[i][j].identifier = this.grid[i][j];
                    this.gridPanels[i][j].changeColor();
                }
            }
        }
    }

    public void constructGUI() {

        /* Initialize panel holding game grid */
//        JPanel gameGridPanel = initGridPanel();
        mainPanel = initGridPanel();

        /* Initialize panel for game control buttons */
        JPanel buttonPanel = initButtonPanel();

//        JFrame frame = new JFrame("Game of Life");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(gameGridPanel, BorderLayout.CENTER);
//        frame.add(buttonPanel, BorderLayout.SOUTH);
        setDefaultColseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane.add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

//        frame.pack();
//        frame.setLocationByPlatform(true);
        pack();
        setLocationRelativeTo(null);
//        frame.setLocationRelativeTo(null);

//        frame.setVisible(true);
//        frame.setSize(new Dimension(480, 480));
        setVisible(true);
        setSize(new Dimension(480, 480));
    }                           

}

//class Help {
//    public Help() {
//        /* Prints out help message to user */
//        System.out.println("Usage: GameOfLife [options]\n" +
//                "\toptional args:\n" +
//                    "\t\tnum_rows (int): number of rows in game grid. Will also\n" +
//                        "be used as number of columns if no num_cols arg is provided\n" +
//                    "\t\tnum_cols (int): number of columns in game grid.\n");
//    }
//}






