


public class Maze{
	public static Maze createMaze(String serial){
		int width =Integer.parseInt(""+serial.charAt(0)+serial.charAt(1));
		int[][] maze = new int[(serial.length()-2)/width][width];
		int i = 1;
		for(int r = 0; r< maze.length; r++){
			for(int c = 0; c<maze[0].length; c++) {
				maze[r][c] = Integer.parseInt(""+serial.charAt(++i));
			}
		}
		return new Maze(maze);
	}
	public static String dissembleMaze(Maze maze){
		int[][] string = maze.getMaze();
		String toReturn =""+string[0].length;//the first two characters will tell the width of the maze
		for(int[] i: string){for(int j:i){
			toReturn+=j;
		}}
		return toReturn;
	}
	public Square[][] grid;
	public int[][] maze;
	public Maze(int width, int height, int density) {
		this(width,height,density, 0.1);
	}
	public Maze(int[][] maze) {
		this.maze = maze;
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
		if(maze.length>0){
			maze[0][0] = 0; maze [0][maze[0].length-1] = 0;
		 	maze[maze.length-1][0] = 0; maze [maze.length-1][maze[0].length-1] = 0;
		}
	}
	public int[][] getMaze() {
		return maze;
	}
	

	public static Maze makeMaze() {
		int width = 5; int height = 4; int density = 3; double threshold = 0.25;
		Maze maze = new Maze(width, height, density, threshold);
		return maze;
	}
}
