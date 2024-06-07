
public class Square {
	public double[][] noise, normalizedNoise;
	public int density;
	public double threshold = 0.1;
	
	public Square(int density) {
		this(density, 0.1);
	}
	
	public Square(int density, double threshold) {
		this.density = density;
		this.threshold = threshold;
		this.noise = new double[density][density];
		this.normalizedNoise = new double[density][density];
	}
	
	public void generateNoise() {
		for (int r = 0; r < noise.length; r++) {
			for (int c = 0; c < noise.length; c++) {
				noise[r][c] = calculateNoise((2.0*c + 1)/(2*density), (2.0*r + 1)/(2*density));
				this.normalizedNoise[r][c] = (Math.abs(noise[r][c]) > threshold) ? ((noise[r][c]< 0) ? -1 : 1) : 0;
			}
		}
	}
	
	public double calculateNoise(double x, double y) {
		double[] dotProducts = new double[4];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				double deg = Math.random()*360;
				Vector gradient = new Vector(Math.cos(deg), Math.sin(deg));
				Vector offset = new Vector(x-i, y-j);
				dotProducts[i+j*2] = calcDotProduct(gradient, offset);
			}
		} 
		return lerp(lerp(dotProducts[0],dotProducts[1],x),lerp(dotProducts[2],dotProducts[3],x),y);
	}
	
	public double calcDotProduct(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
	
	public double lerp(double v1, double v2, double t) {
		return (1 - t) * v1 + t * v2;
	}
	
	public static void main(String[] args) {
		Square skware = new Square(5);
		skware.generateNoise();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(skware.noise[i][j] + " ");
			}
			System.out.println();
		} 
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(skware.normalizedNoise[i][j] + " ");
			}
			System.out.println();
		} 
	}
}
