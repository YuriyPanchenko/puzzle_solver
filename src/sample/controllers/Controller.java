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
                    newPopulations = (ArrayList<Fitness>) Stream.of(newPopulations, createNewPopulation(fitness.getSavedPieces(), fitness.getNotSavedPieces(), fitness.getValue(), fitness.getMinXPosition(), fitness.getMinYPosition()))
                            .flatMap(x -> x.stream())
                            .collect(Collectors.toList());
                }
                //filter populations (remove equals values)
                newPopulations = (ArrayList<Fitness>) newPopulations.stream()
                        .filter(distinctByKey(p -> p.getValue()))
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

    private ArrayList<Fitness> createNewPopulation(LinkedList<Piece> savedPieces, LinkedList<Piece> notSavedPieces, double oldFitnessValue, int minx, int miny){
        double accuracy = 0.01;
        Iterator savedPiecesIterator = savedPieces.iterator();
        int newSupposedMaxValue = -1;
        //this class will save best result
        ArrayList<Conformity> conformityArrayList = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            conformityArrayList.add(new Conformity(-1));
        //finding best result (we compare each of saved pieces with free sides and each of not saved pieces
        while (savedPiecesIterator.hasNext()) {
            Piece savedPiece = (Piece) savedPiecesIterator.next();
            Iterator notSavedPiecesIterator = notSavedPieces.iterator();
            while (notSavedPiecesIterator.hasNext()) {
                Piece piece = (Piece) notSavedPiecesIterator.next();

                if (savedPiece.isLeftSideFree()) {
                    newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getLeftSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() - 1, savedPiece.getPoint().getyValue()), "right", "left", 2));
                    }


                    newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getTopSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() - 1, savedPiece.getPoint().getyValue()), "right", "left", 1));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getRightSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() - 1, savedPiece.getPoint().getyValue()), "right", "left", 0));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getDownSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() - 1, savedPiece.getPoint().getyValue()), "right", "left", 3));
                    }
                }
                if (savedPiece.isTopSideFree()) {
                    newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getLeftSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() - 1), "down", "top", 3));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getTopSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() - 1), "down", "top", 2));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getRightSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() - 1), "down", "top", 1));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getDownSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() - 1), "down", "top", 0));
                    }

                }
                if (savedPiece.isRightSideFree()) {
                    newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getLeftSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() + 1, savedPiece.getPoint().getyValue()), "left", "right", 0));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getTopSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() + 1, savedPiece.getPoint().getyValue()), "left", "right", 3));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getRightSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() + 1, savedPiece.getPoint().getyValue()), "left", "right", 2));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getDownSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue() + 1, savedPiece.getPoint().getyValue()), "left", "right", 1));
                    }

                }
                if (savedPiece.isDownSideFree()) {
                    newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getLeftSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() + 1), "top", "down", 1));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getTopSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() + 1), "top", "down", 0));
                    }

                    newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getRightSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() + 1), "top", "down", 3));
                    }


                    newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getDownSide(), accuracy);
                    if(newSupposedMaxValue > Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())).getValue()){
                        int index = conformityArrayList.indexOf(Collections.min(conformityArrayList, Comparator.comparing(c -> c.getValue())));
                        conformityArrayList.set(index, new Conformity(newSupposedMaxValue, piece, savedPiece, new Point(savedPiece.getPoint().getxValue(), savedPiece.getPoint().getyValue() + 1), "top", "down", 2));
                    }
                }
            }
        }
        //create new population with best results
        ArrayList<Fitness> population = new ArrayList<>();
        for(Conformity conf: conformityArrayList){
            LinkedList<Piece> savedPiecesForFitness = new LinkedList<>();
            for (Piece piece: savedPieces){
                savedPiecesForFitness.add(new Piece(piece));
            }
            LinkedList<Piece> notSavedPiecesForFitness = new LinkedList<>();
            for (Piece piece: notSavedPieces){
                notSavedPiecesForFitness.add(piece);
            }
            Piece pieceToSaveForFitness = new Piece(conf.getPiece());
            String pieceToSaveForFitnessName = pieceToSaveForFitness.getName();
            notSavedPiecesForFitness.removeIf(o -> o.getName().equals(pieceToSaveForFitnessName));
            Point centerForPieceToSave = conf.getNotSavedPiecePoint();
            Iterator savedPiecesForFitnessIterator = savedPiecesForFitness.iterator();
            while (savedPiecesForFitnessIterator.hasNext()){
                Piece piece = (Piece) savedPiecesForFitnessIterator.next();
                if(piece.getPoint().getxValue() == centerForPieceToSave.getxValue()
                    && piece.getPoint().getyValue() + 1 == centerForPieceToSave.getyValue()){
                    piece.setDownSideFree(false);
                    pieceToSaveForFitness.setTopSideFree(false);
                    break;
                }
                if(piece.getPoint().getxValue() == centerForPieceToSave.getxValue() + 1
                    && piece.getPoint().getyValue() == centerForPieceToSave.getyValue()){
                    piece.setLeftSideFree(false);
                    pieceToSaveForFitness.setRightSideFree(false);
                    break;
                }
                if(piece.getPoint().getxValue() == centerForPieceToSave.getxValue()
                    && piece.getPoint().getyValue() == centerForPieceToSave.getyValue() + 1){
                    piece.setTopSideFree(false);
                    pieceToSaveForFitness.setDownSideFree(false);
                    break;
                }
                if(piece.getPoint().getxValue() + 1 == centerForPieceToSave.getxValue()
                        && piece.getPoint().getyValue() == centerForPieceToSave.getyValue()){
                    piece.setRightSideFree(false);
                    pieceToSaveForFitness.setLeftSideFree(false);
                    break;
                }
            }
            pieceToSaveForFitness = modifyPiece(pieceToSaveForFitness, conf.getRotation(), conf.getNotSavedPiecePoint().getxValue(), conf.getNotSavedPiecePoint().getyValue());
            savedPiecesForFitness.add(pieceToSaveForFitness);
            double newSize = savedPiecesForFitness.size();
            double newValue = (oldFitnessValue*(newSize-1)/newSize) + conf.getValue()/newSize;
            int newMinXPosition = Math.min(centerForPieceToSave.getxValue(), minx);
            int newMinYPosition = Math.min(centerForPieceToSave.getyValue(), miny);
            population.add(new Fitness(newValue, savedPiecesForFitness, notSavedPiecesForFitness, newMinXPosition, newMinYPosition));
        }
        return population;
    }

    private Piece modifyPiece(Piece piece, int rotate, int x, int y){
        Piece newPiece = new Piece(piece);
        newPiece.rotate(rotate);
        newPiece.setPoint(new Point(x, y));
        return newPiece;
    }

    //calculating number of coincidences
    private int areSidesJoint(javafx.scene.paint.Color[] side1, javafx.scene.paint.Color[] side2, double accuracy) {
        int size = side1.length;
        int mistakes = 0;
        for(int i = 0; i < size; i++){
            double colorProperty = Math.sqrt(Math.pow(side1[i].getBlue() - side2[size - i - 1].getBlue(), 2)
            + Math.pow(side1[i].getGreen() - side2[size - i - 1].getGreen(), 2)
            + Math.pow(side1[i].getRed() - side2[size - i - 1].getRed(), 2));
            if(colorProperty > accuracy){
                mistakes++;
            }
        }
        return size - mistakes;
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
