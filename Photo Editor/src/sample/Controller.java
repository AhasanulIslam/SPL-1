package sample;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import processing_image.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public static boolean isCroped = false, isMirrored = false, isTextAdded = false;
    public static String currState = "";
    File input = null;
    @FXML
    ImageView preview;
    @FXML
    TextField crop;
    @FXML
    TextField brightness;
    @FXML
    Slider cropslider;
    @FXML
    AnchorPane sliderHolder;
    @FXML
    AnchorPane effectHolder;
    @FXML
    AnchorPane rootpane;

    Slider cropSlider;
    Slider brightnessSlider;

    @FXML
    AnchorPane addTextHolder;
    @FXML
    TextField text, textSize, posx, posy;
    @FXML
    ColorPicker color;

    int r = 255, g = 255, b = 255;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cropslider = new Slider();
        brightnessSlider = new Slider();
        addTextHolder.setVisible(false);



        cropslider.setMin(0);
        cropslider.setMax(100);
        cropslider.setValue(100);
        cropslider.setBlockIncrement(10);
        cropslider.setOrientation(Orientation.VERTICAL);
        cropslider.setPrefHeight(435);
        cropslider.setPrefWidth(18);
        cropslider.setLayoutX(99);
        cropslider.setLayoutY(14);

        brightnessSlider.setMin(-100);
        brightnessSlider.setMax(100);
        brightnessSlider.setValue(0);
        brightnessSlider.setBlockIncrement(10);
        brightnessSlider.setOrientation(Orientation.VERTICAL);
        brightnessSlider.setPrefHeight(435);
        brightnessSlider.setPrefWidth(18);
        brightnessSlider.setLayoutX(34);
        brightnessSlider.setLayoutY(14);

        cropslider.getStylesheets().add("button.css");
        cropslider.getStyleClass().add("userIDField");

        brightnessSlider.getStylesheets().add("button.css");
        brightnessSlider.getStyleClass().add("userIDField");

        sliderHolder.getChildren().add(cropslider);
        sliderHolder.getChildren().add(brightnessSlider);

        sliderHolder.setVisible(false);
        effectHolder.setVisible(false);


        try {
            preview.setImage(new Image(new FileInputStream("src/resources/sampletree.jfif")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            cropslider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                    if (!isNowChanging) {
                        if(input!=null)
                        {
                            double value = cropslider.getValue();
                            if(value>0) {
                                Crop img = new Crop(input);
                                try {
                                    currState = "1.jpg";
                                    img.crop(value/100);
                                } catch (IOException e) {

                                }
                                try {
                                    preview.setImage(new Image(new FileInputStream("1.jpg")));
                                    isCroped = true;
                                } catch (FileNotFoundException e) {

                                }
                            }
                        }
                    }
                }
            });
        }catch(Exception e)
        {
        }

        //brightness suru....

        try
        {
            brightnessSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                    if (!isNowChanging) {
                        if(input!=null)
                        {
                            double value = brightnessSlider.getValue();
                            currState = "brightness.jpg";
                            Brightness img = new Brightness(input);
                            try {
                                img.bright(value);
                            } catch (IOException e) {

                            }
                            try {
                                preview.setImage(new Image(new FileInputStream("brightness.jpg")));
                            } catch (FileNotFoundException e) {

                            }
                        }
                    }
                }
            });
        }catch(Exception e){}

        //brightness sesh....


        color.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                javafx.scene.paint.Color c = color.getValue();
                r = (int) (c.getRed()*255);
                g = (int) (c.getGreen()*255);
                b = (int) (c.getBlue()*255);

            }
        });

    }

    public void addTextHolder(ActionEvent event)
    {
        addTextHolder.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), addTextHolder);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

    }

    public void setAddText(ActionEvent event)
    {
        String rawText;
        int rawTextSize, rawPosX, rawPosY;
        try {
            rawText = text.getText();
            rawTextSize = Integer.parseInt(textSize.getText());
            rawPosX = Integer.parseInt(posx.getText());
            rawPosY = Integer.parseInt(posy.getText());
        }
        catch (Exception e){
            FadeTransition ft = new FadeTransition(Duration.millis(1000), addTextHolder);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();
            addTextHolder.setVisible(false);
            return;
        }

        setText(rawText, rawTextSize, rawPosX, rawPosY);
        try {
            preview.setImage(new Image(new FileInputStream("1.jpg")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FadeTransition ft = new FadeTransition(Duration.millis(1000), addTextHolder);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        addTextHolder.setVisible(false);
        currState = "1.jpg";

    }

    public void setText(String text1, int size1, int posx1, int posy1)
    {
        try {

            BufferedImage image = null;

            if(isCroped || isMirrored) {
                image = ImageIO.read(new File("1.jpg")); // read image
            } else {
                image = ImageIO.read(input);
            }



            Graphics graphics = image.getGraphics(); // make graphics object using image
            graphics.setFont(graphics.getFont().deriveFont(new Float(size1))); // set text size [i.e: graphics.getFont().deriveFont(Xf)]
            // here 'X' represent the size of the text
            graphics.setColor(new Color(r, g, b)); // set text color [i.e: type Color. and pressed ctrl+space and you will get lot of color suggestion to set a color of text]
            graphics.drawString(text1, posx1, posy1); // Type your text here and set the 'x' , 'y' position of your text
            graphics.dispose(); // write text to image

            ImageIO.write(image, "jpg", new File("1.jpg")); // save the final image
            isTextAdded = true;

        } catch (Exception e) {
            System.out.println("Can't read image. Try again!");
        }
    }

    public void setExplore(ActionEvent event)
    {
        sliderHolder.setVisible(true);
        effectHolder.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), effectHolder);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        FadeTransition ft2 = new FadeTransition(Duration.millis(2000), sliderHolder);
        ft2.setFromValue(0);
        ft2.setToValue(1);
        ft2.play();

    }

    public void setUnnExplore(ActionEvent event)
    {

        FadeTransition ft = new FadeTransition(Duration.millis(1000), effectHolder);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();

        FadeTransition ft2 = new FadeTransition(Duration.millis(1000), sliderHolder);
        ft2.setFromValue(1);
        ft2.setToValue(0);
        ft2.play();
    }


    public void setOpen(ActionEvent event) throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Import Picture", "*.jpg", "*.png", "*.jpeg"));
        List<File> musicListf = fc.showOpenMultipleDialog(null);
        if (musicListf != null) {
            for (File f : musicListf) {
                input = f;
            }
        }

        if (input != null) {
            preview.setImage(new Image(new FileInputStream(input)));
        }
    }

    public void setSave(ActionEvent event) throws IOException {
        if(input == null) return;

        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter exfilter = new FileChooser.ExtensionFilter("JPG Images(*.jpg)", "*.jpg");
        fc.getExtensionFilters().add(exfilter);

        File dest = fc.showSaveDialog(null);

       if(dest!=null)
       {
          /* File src = new File(currState);

           FileInputStream fin = new FileInputStream(new File(currState));
           byte[] ara = fin.readAllBytes();
           Files.
           FileOutputStream fout = new FileOutputStream(dest);
           fout.write(ara);

           fin.close();
           fout.close();*/

           Path src = Paths.get(currState);
           Path dest2 = Paths.get(dest.toString());

           Files.copy(src, dest2);
       }
    }

    public void setGreen(ActionEvent event) throws IOException {
        if (input != null) {
            greenImage img = null;
            currState = "green.jpg";

            if(isMirrored || isCroped || isTextAdded)
                img = new greenImage(new File("1.jpg"));
            else
                img = new greenImage(input);

            img.setGreenFilter();
            img.greenFilter();
            preview.setImage(new Image(new FileInputStream("green.jpg")));

        }
    }

    public void setRed(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            redImage img = null;
            currState = "red.jpg";

            if(isCroped || isMirrored || isTextAdded)
                img = new redImage(new File("1.jpg"));
            else
                img = new redImage(input);

            img.setRedFilter();
            img.redFilter();
            preview.setImage(new Image(new FileInputStream("red.jpg")));
        }
    }

    public void setBlue(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            blueImage img = null;

            currState = "blue.jpg";
            if(isMirrored || isCroped || isTextAdded)
                img = new blueImage(new File("1.jpg"));
            else
                img = new blueImage(input);
            img.setBlueFilter();
            img.blueFilter();
            preview.setImage(new Image(new FileInputStream("blue.jpg")));
        }
    }

    public void setGrayScale(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            grayScale img = null;

            currState = "gray.jpg";
            if(isCroped || isMirrored || isTextAdded)
                img = new grayScale(new File("1.jpg"));
            else
                img = new grayScale(input);

            img.setGrayFilter();
            img.grayFilter();
            preview.setImage(new Image(new FileInputStream("gray.jpg")));
        }
    }

    public void setSepia(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            Sapia img = null;
            currState = "sepia.jpg";

            if(isMirrored || isCroped || isTextAdded)
                img = new Sapia(new File("1.jpg"));
            else
                img = new Sapia(input);

            img.setsepiaFilter();
            img.sepiaFilter();
            preview.setImage(new Image(new FileInputStream("sepia.jpg")));
        }
    }

    public void setnegative(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            negativeImage img = new negativeImage(input);
            img.setNegetiveFilter();
            img.negetiveFilter();
            preview.setImage(new Image(new FileInputStream("negetive.jpg")));
        }
    }

    public void setMirror(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            Mirror img = null;
            currState = "1.jpg";

            if(isCroped || isTextAdded)
                img = new Mirror(new File("1.jpg"));
            else
                img = new Mirror(input);

            img.setMirrorFilter();
            img.mirrorFilter();
            preview.setImage(new Image(new FileInputStream("1.jpg")));
            isMirrored = true;
        }
    }

    public void setSolarise(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            Solarise img = null;
            if(isCroped || isMirrored || isTextAdded)
                img = new Solarise(new File("1.jpg"));
            else
                img = new Solarise(input);
            currState = "solarise.jpg";

            img.setSolariseFilter();
            img.solariseFilter();
            preview.setImage(new Image(new FileInputStream("solarise.jpg")));

        }

    }

    public void setYellow(ActionEvent event) throws IOException, InterruptedException {
        if (input != null) {
            yellowImage ye = null;

            if(isMirrored || isCroped || isTextAdded)
                ye = new yellowImage(new File("1.jpg"));
            else
                ye = new yellowImage(input);

            currState = "yellow.jpg";
            ye.setYellowImageFilter();
            ye.yellowImageFilter();
            preview.setImage(new Image(new FileInputStream("yellow.jpg")));

        }

    }

    public void setExit(ActionEvent event)
    {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), rootpane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        ft.setOnFinished(e->{
            System.exit(0);
        });

    }

}