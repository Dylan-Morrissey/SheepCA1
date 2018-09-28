import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.out;
//import static sun.swing.SwingUtilities2.drawRect;

public class Main extends Application {



    private int imageArray[];
    Slider slider = new Slider(0,255,200);
    public static void main(String[] args) {
        launch(args); //Method inside application which calls start down below
    }

    private void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1500, 840);
        //scene.getStylesheets().add("SHEEP.CSS");
        primaryStage.setTitle("Sheep Aerial Recognition");

        Image image = new Image("sheep.jpg");
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitWidth(1500);
        iv.setImage(image);
        root.getChildren().add(iv);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File Explore");

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);
        //File Menu
        Menu menuFile = new Menu("File");
        //Menu  File Items
        MenuItem newMenuItem = new MenuItem("New...");
        newMenuItem.setOnAction(e -> {
            setExtFilters(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                openNewImageWindow(file);
            }
        });
        MenuItem saveMenuItem = new MenuItem("Save...");
        MenuItem openMenuItem = new MenuItem("Open");
        openMenuItem.setOnAction((ActionEvent e) -> {
            setExtFilters(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                openNewImageWindow(file);
            }
        });

        MenuItem exitMenuItem = new MenuItem("Exit");

        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        menuFile.getItems().addAll(newMenuItem, saveMenuItem, openMenuItem, new SeparatorMenuItem(), exitMenuItem);

        //Edit Menu
        Menu menuEdit = new Menu("Edit");
        MenuItem cutMenuItem = new MenuItem("Cut");
        MenuItem copyMenuItem = new MenuItem("Copy");
        MenuItem pasteMenuItem = new MenuItem("Paste");

        menuEdit.getItems().addAll(cutMenuItem, copyMenuItem, pasteMenuItem);

        //View Menu
        Menu menuView = new Menu("View");
        ToggleGroup viewToggle = new ToggleGroup();
        RadioMenuItem greyScaleMenuItem = new RadioMenuItem("GreyScale");
        greyScaleMenuItem.setOnAction(e -> {
            if (greyScaleMenuItem.isSelected()) {
                out.print(" Grey Working");
            }
        });

        RadioMenuItem blackWhiteMenuItem = new RadioMenuItem("Black and White");
        blackWhiteMenuItem.setOnAction(e -> {
            if (blackWhiteMenuItem.isSelected()) {
                out.print(" Black Working");
            }
        });

        RadioMenuItem originalMenuItem = new RadioMenuItem("Original");
        originalMenuItem.setOnAction(e -> {
            if (originalMenuItem.isSelected()) {
                out.print(" Oringinal Working");
            }
        });

        originalMenuItem.setSelected(true);
        greyScaleMenuItem.setToggleGroup(viewToggle);
        blackWhiteMenuItem.setToggleGroup(viewToggle);
        originalMenuItem.setToggleGroup(viewToggle);
        menuView.getItems().addAll(originalMenuItem, new SeparatorMenuItem(), blackWhiteMenuItem, new SeparatorMenuItem(), greyScaleMenuItem);
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private  void union(int[]a, int x, int y){
        a[find(a,y)]=find(a,x);
    }

    private int find(int[] a, int id) {
        return a[id]==id ? id : find(a,a[id]);
    }
    private  void setBlackWhite(BufferedImage img){
        for (int w = 0; w < img.getWidth(); w++) {
            for (int h = 0; h < img.getHeight(); h++) {
                int rgb = img.getRGB(w, h);
                int iRed = rgb >> 16 & 0xff;
                int iGreen = rgb >> 8 & 0xff;
                int iBlue = rgb & 0xff;

                if ((iGreen < slider.getValue() && (iRed < slider.getValue()) && (iBlue < slider.getValue()))){
                    img.setRGB(w, h, 0);
                } else {
                    img.setRGB(w, h, 0xffffff);
                }
            }
        }
    }

    private void BlackWhite(File file, ImageView imageView) throws IOException {
        BufferedImage img;
        File f;
        f = file;
        img = ImageIO.read(f);

        assert img != null;
        setBlackWhite(img);
        imageArray = new int[img.getWidth() * img.getHeight()];
        System.out.println(imageArray.length);
        imageView.setImage(SwingFXUtils.toFXImage(img, null));
        //Goes through the pixels of the image,gets the ARGB value, if pixel is black, set value in array to 0, if pixel is white, set value in array to be position of pixel
        for (int h = 0; h < img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                int argb = SwingFXUtils.toFXImage(img, null).getPixelReader().getArgb(w, h);
                imageArray[w + h * img.getWidth()] = argb != -16777216 ? w + h * img.getWidth() : 0;
            }
        }
    }
    Label count = new Label();
    private  void countImage(File file, int[] imageArray, Image image) throws IOException {
        ArrayList<Integer> sheepCounter = new ArrayList<>();
        File f;
        f = file;
        BufferedImage img = ImageIO.read(f);
        setBlackWhite(img);

        for (int i = 0; i < imageArray.length; i++) {
            if ((imageArray[i] != 0) && (imageArray[i + 1] != 0)) {
                if (Math.max(imageArray[i], imageArray[i + 1]) == imageArray[i]) {
                    union(imageArray, imageArray[i + 1], imageArray[i]);
                } else {
                    union(imageArray, imageArray[i], imageArray[i + 1]);
                }
            }
        }
        for (int i = 0; i < imageArray.length; i++) {

            if ((imageArray[i] != 0 && imageArray[i + img.getWidth()] != 0)) {
                if (Math.max(imageArray[i], imageArray[i + img.getWidth()]) == imageArray[i]) {
                    union(imageArray, imageArray[i + img.getWidth()], imageArray[i]);
                } else {
                    union(imageArray, imageArray[i], imageArray[i + img.getWidth()]);
                }
            }
            System.out.println("Value of " + i +" is " + find(imageArray, i));

        }
        System.out.println(img.getHeight());
        System.out.println(img.getWidth());
        int rootNode;
        for (int nodes : imageArray) {
            rootNode = find(imageArray, nodes);
            if ((!sheepCounter.contains(rootNode) && rootNode != 0)) {
                sheepCounter.add(rootNode);
            }
        }

        System.out.println(sheepCounter);
        System.out.println(sheepCounter.size());
        count= new Label("There are " + sheepCounter.size() +" Sheep In this Image ");

     /* for (int i =0; i<imageArray.length; i++){
            int space = 0;
            int x = 0;
            int y = 0;
            if(find(imageArray, i) != 0){
                if (find(imageArray,i)==imageArray[i]) {
                     x = i;
                     y = i/img.getHeight();
                }else if (find(imageArray,i)== find(imageArray,-1)){
                          space++;
                }

            }
            //drawRect(x,y,space,space/img.getHeight());
        }*/
    }



    private void openNewImageWindow(File file) {
        Stage secondStage = new Stage();
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> secondStage.close());
        MenuItem menuItem_Save = new MenuItem("Save Image");
        Menu menuView = new Menu("View");
        ToggleGroup viewToggle = new ToggleGroup();
        Label name = new Label(file.getAbsolutePath());
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView();

        RadioMenuItem originalMenuItem = new RadioMenuItem("Original");
        RadioMenuItem blackWhiteMenuItem = new RadioMenuItem("Black and White");
        RadioMenuItem countMenuItem = new RadioMenuItem("Count");
        originalMenuItem.setOnAction((ActionEvent e) -> {
            originalMenuItem.setSelected(true);
            blackWhiteMenuItem.setSelected(false);
            Image origimage = new Image(file.toURI().toString());
            imageView.setImage(origimage);
        });

        blackWhiteMenuItem.setOnAction((ActionEvent e) -> {
                    originalMenuItem.setSelected(false);
            try {
                BlackWhite(file, imageView);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        final VBox vbox = new VBox();
            countMenuItem.setOnAction((ActionEvent g) -> {
                originalMenuItem.setSelected(false);
                try {
                    countImage(file, imageArray, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vbox.getChildren().add(count);
            });

        blackWhiteMenuItem.setToggleGroup(viewToggle);
        menuView.getItems().addAll(originalMenuItem, new SeparatorMenuItem(), blackWhiteMenuItem, countMenuItem);
        menuFile.getItems().addAll(menuItem_Save, new SeparatorMenuItem(), exitMenuItem);
        menuBar.getMenus().addAll(menuFile, menuView);


        menuItem_Save.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");

            File file1 = fileChooser.showSaveDialog(secondStage);
            if (file1 != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(),
                            null), ".jpg", file1);
                } catch (IOException ex) {
                    Logger.getLogger(
                            Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 0, 10));
        vbox.getChildren().addAll(name, imageView);




        Label sliderValue = new Label(Double.toString(slider.getValue()));
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                BlackWhite(file, imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sliderValue.setText(String.format("%.2f", newValue));
        });
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        vbox.getChildren().add(slider);
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setFitWidth(1000);
        imageView.setFitWidth(550);

        Scene scene = new Scene(new VBox(), 1000, 550);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, vbox);
        secondStage.setTitle(file.getName());
        secondStage.setScene(scene);

        secondStage.show();
    }
}