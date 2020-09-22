package sample.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.models.*;
import sample.models.Point;
import sample.service.Service;

import javax.imageio.ImageIO;

public class Controller {
    private LinkedList<Piece> pieces = new LinkedList<>();
    private GridPane picture;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button selectImagesButton;

    @FXML
    private ListView<ImageView> imagesListView;

    @FXML
    private Button solveButton;

    @FXML
    private VBox vBox;

    @FXML
    void initialize() {
        solveButton.setVisible(false);
        selectImagesButton.setOnAction(event -> {
            Stage stage =  (Stage) ((Node)event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG =
                    new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
            FileChooser.ExtensionFilter extFilterjpg =
                    new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
            FileChooser.ExtensionFilter extFilterPNG =
                    new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
            FileChooser.ExtensionFilter extFilterpng =
                    new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters()
                    .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
            //Show open file dialog
            List<File> list = fileChooser.showOpenMultipleDialog(stage);
            //add new values
           if (list != null) {
               ObservableList<ImageView> views = FXCollections.observableArrayList();
                for (File file : list) {
                    try {
                        BufferedImage bufferedImage =ImageIO.read(file);
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        pieces.add(new Piece(image, file.getName()));
                        views.addAll(new ImageView(image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imagesListView.setItems(views);
                solveButton.setVisible(true);
            }

            vBox.getChildren().remove(picture);
        });

        solveButton.setOnAction(event -> {
            //every time we will add new piece to result
            int numberOfPictures = this.pieces.size() - 1;
            //it'll contain all pieces and show their
            picture = new GridPane();
            vBox.getChildren().addAll(picture);
            //list of supposed solutions
            ArrayList<Fitness> populations = new ArrayList<>();
            //add to each of populations first piece
            for (int j = 0; j < 5; j++){
                LinkedList<Piece> notSavedPieces = new LinkedList<>(this.pieces);
                LinkedList<Piece> savedPieces = new LinkedList<>();
                savedPieces.add(new Piece(notSavedPieces.get(j)));
                notSavedPieces.remove(j);
                savedPieces.getFirst().setPoint(new Point(0, 0));
                populations.add(new Fitness(0, savedPieces, notSavedPieces));

            }
            while (numberOfPictures > 0) {
                ArrayList<Fitness> newPopulations = new ArrayList<>();
                //creating new populations
                for (Fitness fitness: populations){
                    newPopulations = (ArrayList<Fitness>) Stream.of(newPopulations, Service.createNewPopulation(fitness.getSavedPieces(), fitness.getNotSavedPieces(), fitness.getValue(), fitness.getMinXPosition(), fitness.getMinYPosition()))
                            .flatMap(x -> x.stream())
                            .collect(Collectors.toList());
                }
                //filter populations (remove equals values)
                newPopulations = (ArrayList<Fitness>) newPopulations.stream()
                        .filter(Service.distinctByKey(p -> p.getValue()))
                        .collect(Collectors.toList());
                int sublistSize = Math.min(4, newPopulations.size());
                //sort by conformity
                Collections.sort(newPopulations, Comparator.comparing(o -> o.getValue()));
                Collections.reverse(newPopulations);
                //remove weak populations
                populations = new ArrayList<>(newPopulations.subList(0, sublistSize));
                numberOfPictures--;
            }
            //choose the best result
            Fitness finalPopulation = Collections.max(populations, Comparator.comparing(o -> o.getValue()));
            LinkedList<Piece> savedPieces = finalPopulation.getSavedPieces();
            int minx = finalPopulation.getMinXPosition();
            int miny = finalPopulation.getMinYPosition();
            //add image views
            for (Piece savedPiece : savedPieces) {
                ImageView imageView = new ImageView(savedPiece.getImage());
                imageView.setRotate(90 * savedPiece.getRotation());
                picture.add(imageView,
                         (savedPiece.getPoint().getxValue() - minx) * savedPiece.getSize(),
                        (int) (savedPiece.getPoint().getyValue() - miny) * savedPiece.getSize());
            }
        });
    }
}
