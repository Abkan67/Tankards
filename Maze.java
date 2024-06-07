import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Maze extends JComponent{
	public Square[][] grid;
	public int[][] maze;
	public Maze(int width, int height, int density) {
		this(width,height,density, 0.1);
	}
	public Maze(int width, int height, int density, double threshold) {
		grid = new Square[height][width];
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				grid[r][c] = new Square(density, threshold);
				grid[r][c].generateNoise();
			}
		}
		maze = new int[height*density][width*density];
		for (int r0 = 0; r0 < height; r0++) {
			for (int r1 = 0; r1 < density; r1++) {
				for (int c0 = 0; c0 < width; c0++) {
					for (int c1 = 0; c1 < density; c1++) {
						maze[r0*density+r1][c0*density+c1] = (grid[r0][c0].normalizedNoise[r1][c1] != 0) ? 1 : 0;
					}
				}
			}
		}
	}
	public int[][] getMaze() {
		return maze;
	}
	
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		for (int r = 0; r < maze.length; r++) {
			for (int c = 0; c < maze[r].length; c++) {
				if (maze[r][c]==1) {g.draw(new Rectangle2D.Double(getWidth()*c/maze[r].length,getHeight()*r/maze.length,getWidth()/maze[r].length,getHeight()/maze.length));}
			}
		}
	}
	
	public static void main(String[] args) {
		int width = 5; int height = 4; int density = 3; double threshold = 0.25;
		Maze maze = new Maze(width, height, density, threshold);
		int[][] values = maze.getMaze();
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[r].length; c++) {
				System.out.print(values[r][c] + " ");
			}System.out.println();
		}
		JFrame frame = new JFrame();
		frame.setSize(width*150, height*150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(maze);
		frame.setVisible(true);
	}
}
