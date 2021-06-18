package workers;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import serializables.Task;
import serializables.TaskDone;

public class Worker180 extends Worker {
	private BufferedImage img;
	private BufferedImage logo;
	private InetAddress address;

	public Worker180(String endereco, String PORTO, String type) throws IOException, ClassNotFoundException {
		super(endereco, PORTO, type);
		runWorker();
	}

	protected TaskDone procura(Task info) throws IOException {
		this.address = info.getAddress();
		InputStream in1 = new ByteArrayInputStream(info.getImage());
		img = ImageIO.read(in1);
		in1 = new ByteArrayInputStream(info.getSubimage());
		logo = rotate(ImageIO.read(in1), 180);
		ArrayList<Point[]> coordinates = new ArrayList<>();
		int k = 0;
		for (int imgX = 0; imgX != (img.getWidth() - logo.getWidth()); imgX++) {
			for (int imgY = 0; imgY != (img.getHeight() - logo.getHeight()); imgY++) {
				for (int logoX = 0; logoX != logo.getWidth(); logoX++) {
					if (k == 1) {
						k = 0;
						break;
					}
					for (int logoY = 0; logoY != logo.getHeight(); logoY++) {
						if (img.getRGB(imgX + logoX, imgY + logoY) != logo.getRGB(logoX, logoY)) {
							k = 1;
							break;
						}
						if (logoY == logo.getHeight() - 1 && logoX == logo.getWidth() - 1) {
							Point[] points = new Point[2];
							points[0] = new Point(imgX, imgY);
							points[1] = new Point(imgX + logo.getWidth(), imgY + logo.getHeight());
							coordinates.add(points);
						}
					}
				}
			}
		}
		return new TaskDone(coordinates, address, info.getIndex());
	}
}
