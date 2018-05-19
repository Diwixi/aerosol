package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by Diwixis on 10.05.2018.
 */
public class H5Reader {

    public static void read(Scanner scanner) {
        System.out.println("Enter command");
        String command = scanner.nextLine();
        String[] commandWords = command.split(" ");
        if (commandWords[0].equals("folder")) {
            java.util.List<String> names = readFolder(commandWords[1]);
            readFile(names);
        }
    }

    public static java.util.List<String> readFolder  (String folderName) {
        final File folder = new File(folderName);
        java.util.List<String> fileNames = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                readFolder(fileEntry.getName());
            } else {
                fileNames.add(fileEntry.getName());
                System.out.println(fileEntry.getName());
            }
        }
        return fileNames;
    }

    private static java.util.List<Float> getNumFromFile(String fileName) {
        java.util.List<Float> numbers = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(fileName + ".txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine(); //пропускаем первую строку файла, которая сожержит описание файла.
            String name = line.replace("\t", " ").replace("\"", "").split(" ")[2];

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                String[] lineWords = line.replace("\t", " ").replace("\"", "").split(" ");
                numbers.add(Float.parseFloat(lineWords[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return numbers;
    }

    private static void readFile(java.util.List<String> fileNames) {
        BufferedImage img = null;
        String name = "exitFile";

        int sizeX = 4300;
        int sizeY = 4300;
        Dimension imgDim = new Dimension(sizeX,sizeY);
        img = new BufferedImage(imgDim.width, imgDim.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.fillRect(0, 0, imgDim.width, imgDim.height);
        g2d.setColor(Color.BLACK);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);

        java.util.List<Float> values = new ArrayList<>();

        for (int i = 0; i < fileNames.size(); i++){
//            System.out.println(color.toString() + " - " + fileNames.get(i));

            java.util.List<Float> numbers = getNumFromFile(fileNames.get(i));
            if (values.size() == 0) {
                values.addAll(numbers);
            } else {
                values = summLists(values, numbers);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) / fileNames.size());
        }


        int oldX = 20;
        int oldY = 20;
        int newX;
        int newY;
        Color color = Color.getHSBColor((float) 10 / (float)fileNames.size(), 1f, 1.0f);

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != 0) {
                newX = (int)(sizeX * (values.get(i) / 5)) + 20;
                newY = i + 20;
                g2d.setColor(color);
                g2d.drawLine(oldX, oldY, newX, newY);
                oldX = newX;
                oldY = newY;
            }
        }

        savePNG(img, name + ".png");
    }

    private static java.util.List<Float> summLists(java.util.List<Float> list1, java.util.List<Float> list2) {
        if (list1.size() < list2.size()) {
            Collections.copy(list1, alignmentList(list1, list2.size() - list1.size()));
        } else if (list2.size() < list1.size()) {
            Collections.copy(list1, alignmentList(list2, list1.size() - list2.size()));
        }

        for (int i = 0; i < list1.size(); i++) {
            list1.set(i, list1.get(i) + list2.get(i));
        }

        return list1;
    }

    private static java.util.List<Float> alignmentList(java.util.List<Float> arr, int addCount) {
        for (int i = 0; i < addCount; i++) {
            arr.add(0f);
        }
        return arr;
    }

    private static void savePNG( final BufferedImage bi, final String path ){
        try {
            RenderedImage rendImage = bi;
//                ImageIO.write(rendImage, "bmp", new File(path));
            ImageIO.write(rendImage, "PNG", new File(path));
            //ImageIO.write(rendImage, "jpeg", new File(path));
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
