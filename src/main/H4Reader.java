package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Diwixis on 10.05.2018.
 */
public class H4Reader {

    public static void read(Scanner scanner) {
        System.out.println("Enter command");
        String command = scanner.nextLine();
        String[] commandWords = command.split(" ");
        if (commandWords[0].equals("folder")) {
            java.util.List<String> names = readFolder(commandWords[1]);
            readFiles(names);
        }
    }

    private static float[][] readFiles(List<String> names) {
        List<float[][]> maps = new ArrayList<>();
        for (String name: names) {
            maps.add(readFile(name));
        }
        int rowCount = getMaxRowCount(maps);
        int columnCount = getMaxColumnCount(maps);

        float[][] resultMap = new float[rowCount][columnCount];
        float[][] countMap = new float[rowCount][columnCount];

        for (float[][] map : maps) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] > 0) {
                        resultMap[i][j] += map[i][j];
                        countMap[i][j] += 1;
                    }
                }
            }
        }


        PrintWriter writer = null;
        try {
            writer = new PrintWriter("result_text_h4.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < resultMap.length; i++) {
            for (int j = 0; j < resultMap[i].length; j++) {
                if (countMap[i][j] > 0 && resultMap[i][j] > 0) {
                    resultMap[i][j] /= countMap[i][j];
//                    resultMap[i][j] /= maps.size();
                    assert writer != null;
                    writer.print(resultMap[i][j] + " ");
                }
//                System.out.print(resultMap[i][j] + " ");
            }
            assert writer != null;
            writer.println();
//            System.out.println();
        }


        assert writer != null;
        writer.close();
        paintMap(resultMap);
        return resultMap;
    }

    private static void paintMap(float[][] resultMap) {
        BufferedImage img = map(resultMap.length, resultMap[0].length);

        for (int i = 0; i < resultMap.length - 1; i++) {
            for (int j = 0; j < resultMap[i].length - 1; j++) {
                float p = resultMap[i][j];
                int col = (int)(225 - (225 * p));
                if (col > 225) col = 225;
                if (col < 0) col = 0;
                Color color = new Color(col, col, col);
                paint(img, i, (resultMap[j].length - 1) - j, color.getRGB());
//                if (p < 0.25) {
//                    paint(img, i, (resultMap[j].length - 1) - j, Color.HSBtoRGB(0.158f, 1f, 1 - p));
//                } else {
//                    paint(img, i, (resultMap[j].length - 1) - j, Color.HSBtoRGB(1f, 1f, 1 - p));
//                }
            }
        }

        savePNG(img, "result.png");
    }

    private static int getMaxColumnCount(List<float[][]> maps) {
        int maxRowCount = 0;
        for (float[][] map : maps) {
            for (float[] row : map) {
                if (row.length > maxRowCount) {
                    maxRowCount = map.length;
                }
            }
        }
        return maxRowCount;
    }

    private static int getMaxRowCount(List<float[][]> maps) {
        int maxRowCount = 0;
        for (float[][] map : maps) {
            if (map.length > maxRowCount) {
                maxRowCount = map.length;
            }
        }
        return maxRowCount;
    }

    private static java.util.List<String> readFolder(String folderName) {
        final File folder = new File(folderName);
        java.util.List<String> fileNames = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            try {
                lines = Files.readAllLines(fileEntry.toPath(), Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lines.size() == 0) {
                break;
            }

            if (lines.get(0).contains("Data_Fields")) {
                fileNames.add(fileEntry.getPath());
            } else if (lines.get(0).equals("Geolocation_Fields/Latitude")) {

            } else if (lines.get(0).equals("Geolocation_Fields/Longitude")) {

            }
//            fileNames.add(fileEntry.getPath());
            System.out.println(fileEntry.getPath());
        }
//        float[][] modis = readFiles(fileNames);
//        float[][] geoLat = get
//        float[][] geoLong
        return fileNames;
    }


    private static float[][] readFile(String fileName) {
        float[][] mapFile = null;
        //чтение файла
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine(); //пропускаем первую строку файла, которая сожержит описание файла.
            String name = line.replace("\t", " ").replace("\"", "").split(" ")[2];
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                String[] lineWords = line.replace("\t", " ").replace("\"", "").split(" ");
                if (mapFile == null) {
                    mapFile = new float[Integer.parseInt(lineWords[2])][Integer.parseInt(lineWords[5])];
                }

                if (!lineWords[6].toLowerCase().equals("nan")) {
                    float p = Float.parseFloat(lineWords[6]);
                    mapFile[Integer.parseInt(lineWords[0])-1][Integer.parseInt(lineWords[3])-1] = p;
                }
            }
            return mapFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void paint (BufferedImage img, int x, int y, int color) {
        img.setRGB(x, y, color);
    }

    private static BufferedImage map( int sizeX, int sizeY ){
        final BufferedImage res = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                res.setRGB(x, y, Color.WHITE.getRGB() );
            }
        }
        return res;
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
