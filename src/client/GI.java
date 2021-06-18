package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GI {
	private JFrame frame;
	private JFileChooser fcPasta= new JFileChooser();
	private JFileChooser fcImagem= new JFileChooser();
	private DefaultListModel<String> model = new DefaultListModel<>();
	private HashMap<String, ImageIcon> imagens;
	private Client client;
	private DefaultListModel<String> modelWorkers = new DefaultListModel<>();
	private JList<String> workersList;
	
	public GI(Client client) {
		this.client = client;
	}
	
	public void open() {
		createInterface();
		frame.setVisible(true);
		frame.setSize(900, 700);
	}
	
	public void createInterface() {
		frame = new JFrame("LogoFinder App");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
	}
	
	public void addModel(String name) {
	 	model.addElement(name);
	}
	
	public void addWorkers(String name) {
		modelWorkers.addElement(name);
	}
	
	public void updateWorkers(DefaultListModel<String> model) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				workersList.setModel(model);
			}
		});
		
	}

	private void addFrameContent() {
		JPanel procura = new JPanel();
		procura.setLayout(new BorderLayout());

		JPanel pastaImagem = new JPanel();
		pastaImagem.setLayout(new GridLayout(2, 1));

		JTextField textoPasta = new JTextField("         ");
		pastaImagem.add(textoPasta);
		JTextField textoImagem = new JTextField("         ");
		pastaImagem.add(textoImagem);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 1));
		JButton pasta = new JButton("Pasta");
		JLabel imagem = new JLabel();
		JScrollPane scroll = new JScrollPane(imagem);
		pasta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fcPasta.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fcPasta.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					textoPasta.setText(fcPasta.getSelectedFile().getName());
					scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				}
			}
		});
		buttons.add(pasta);
		JButton buttonImagem = new JButton("Imagem");
		buttonImagem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = fcImagem.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					textoImagem.setText(fcImagem.getSelectedFile().getName());
				}
			}
		});
		workersList = new JList<>(modelWorkers);
		workersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollArea = new JScrollPane(workersList);
		/*workersList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if(workersList.getSelectedValue().equals("Procura simples"))
						
				}
			}
		});*/
		JButton procurar = new JButton("Procurar");
		procurar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File[] array = fcPasta.getSelectedFile().listFiles();
					byte[] k;
					ArrayList<byte[]> images = new ArrayList<>();
					ArrayList<String> type = (ArrayList<String>) workersList.getSelectedValuesList();
					ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
					ArrayList<String> imagesName = new ArrayList<>();
					byte[] logo;
					for (int x = 0; x != array.length; x++) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						BufferedImage img = ImageIO.read(array[x]);
						bufferedImages.add(img);
						imagesName.add(array[x].getName());
						ImageIO.write(img, "png", baos);
						baos.flush();
						k = baos.toByteArray();
						images.add(k);
						baos.close();
					}
					ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
					BufferedImage img = ImageIO.read(fcImagem.getSelectedFile());
					ImageIO.write(img, "png", baos2);
					baos2.flush();
					logo = baos2.toByteArray();
					baos2.close();
					InputStream in1 = new ByteArrayInputStream(logo);
					img = ImageIO.read(in1);
					client.sendData(images, logo, type);
					imagens = client.draw(bufferedImages, imagesName);
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}

		});
		JList<String> list = new JList<>(model);
		JScrollPane scrollArea2 = new JScrollPane(list);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					imagem.setIcon(imagens.get(list.getSelectedValue()));
				}
			}
		});
		buttons.add(buttonImagem);

		procura.add(pastaImagem, BorderLayout.CENTER);
		procura.add(buttons, BorderLayout.EAST);
		procura.add(procurar, BorderLayout.SOUTH);
		frame.getContentPane().add(scroll, BorderLayout.CENTER);
		frame.add(procura, BorderLayout.SOUTH);
		frame.add(scrollArea2, BorderLayout.EAST);
		frame.add(scrollArea, BorderLayout.WEST);
	}
}
