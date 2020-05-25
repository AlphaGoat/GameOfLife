/* Heavily inspired (and almost completely copied from) Hovercraft Full of Eel's
 * post on stackoverflow
 * https://stackoverflow.com/questions/36380516/drawing-a-grid-in-a-jframe */
package com.alphagoat.maven;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

import java.util.Random;

public class GameGrid extends JPanel {

    private static int GAP = 3;
    private static Color BG = Color.BLACK;
    private static Color EMPTY_COLOR = Color.WHITE;
    private static Color COLONY_COLOR = Color.YELLOW;
    private static GridIdentifier[][] grid;

    public GameGrid(int num_rows, int num_cols) {
        /* Initializes grid for game of life */
        JPanel mainPanel = new JPanel(new GridLayout(num_rows, num_cols));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        mainPanel.setBackground(BG);
        mainPanel.setSize(new Dimension(480, 480));
        JPanel[][] gridPanels = new JPanel[num_rows][num_cols];
        grid = new GridIdentifier[num_rows][num_cols];

        Random rand = new Random();
        for (int i = 0; i < gridPanels.length; i++) {
            for (int j = 0; j < gridPanels[i].length; j++) {
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();
                Color randomColor = new Color(r, g, b);
                gridPanels[i][j] = new JPanel();
                gridPanels[i][j].setSize(new Dimension(480 / num_rows, 480 / num_cols));
              //gridPanels[i][j].setBackground(BG);
               gridPanels[i][j].setBorder(BorderFactory.createLineBorder(BG, 1, false));
               gridPanels[i][j].setBackground(EMPTY_COLOR);
                gridPanels[i][j].setSize(new Dimension(480 / num_rows, 480 / num_cols));
                mainPanel.add(gridPanels[i][j]);
            
                /* Place empty space in game grid */    
//                gridPanels[i][j] = new GridPanel(GridIdentifier.EMPTY_SPACE);
//                grid[i][j] = GridIdentifier.EMPTY_SPACE;
            
            }
        }

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
//        add(new JButton(new AssignColonyAction("Assign Colony")), BorderLayout.PAGE_END);
    }

    class GridPanel extends JPanel {
        GridIdentifier identifier;

        public GridPanel(GridIdentifier identifier) {
            this.identifier = identifier;
            setBorder(BorderFactory.createLineBorder(BG, 1, false));
            setBackground(EMPTY_COLOR);
//            addMouseListener(new MouseAdapter() {
//                @Override
//                public void mousePressed(MouseEvent e) {
//                    switch(this.identifier.getValue()) {
//                        case 0:
//                            setBackground(COLONY_COLOR);
//                            this.identifier = GridIdentifier.COLONY;
//                            break;
//
//                        case 1:
//                            setBackground(EMPTY_COLOR);
//                            this.identifier = GridIdentifier.EMPTY_SPACE;
//                            break;
//                    }
//                }
//            });
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
                        setBackground(COLONY_COLOR);
                        this.identifier = GridIdentifier.COLONY;
                        break;

                    case 1:
                        setBackground(EMPTY_COLOR);
                        this.identifier = GridIdentifier.EMPTY_SPACE;
                        break;
                }
            }
        }

        public void changeColor() {
            /* Changes color of jpanel grid based on identity of 
             * the grid space it represents */
            switch (this.identifier.getValue()) {

                case 0:
                    setBackground(COLONY_COLOR);
                    break;

                case 1:
                    setBackground(EMPTY_COLOR);
                    break;
            }
        }
    }
//
    public enum IDENTIFIER {
        EMPTY_SPACE,
        COLONY
    }
//
    private static void initGui() {
        GameGrid mainPane = new GameGrid(20, 20);
        JFrame frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPane);
        frame.pack();
//        frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        frame.setSize(new Dimension(480, 480));
    }                           

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initGui();
        });
    }

                                
}


