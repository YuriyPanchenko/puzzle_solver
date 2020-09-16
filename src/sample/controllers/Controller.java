package sample.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

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
import sample.models.Conformity;
import sample.models.Piece;

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

            System.out.println(pieces);

        });

        solveButton.setOnAction(event -> {
            LinkedList<Piece> notSavedPieces = this.pieces;
            LinkedList<Piece> savedPieces = new LinkedList<>();
            savedPieces.add(notSavedPieces.get(0));
            notSavedPieces.remove(0);
            picture = new GridPane();
            vBox.getChildren().addAll(picture);
            Iterator savedPiecesIterator = savedPieces.iterator();
            while (notSavedPieces.size() > 0) {
                int newSupposedMaxValue = 0;
                //this class will save best result
                Conformity conformity = new Conformity(newSupposedMaxValue);
                //finding best result (we compare each of saved pieces with free sides and each of not saved pieces
                while (savedPiecesIterator.hasNext()) {
                    Piece savedPiece = (Piece) savedPiecesIterator.next();
                    double accuracy = 0.01;
                    Iterator notSavedPiecesIterator = notSavedPieces.iterator();
                    while (notSavedPiecesIterator.hasNext()) {
                        Piece piece = (Piece) notSavedPiecesIterator.next();
                        if (savedPiece.isLeftSideFree()) {
                            newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getLeftSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() - 1, savedPiece.getyPosition(), "right", "left", 2);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getTopSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() - 1, savedPiece.getyPosition(), "right", "left", 1);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getRightSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() - 1, savedPiece.getyPosition(), "right", "left", 0);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getLeftSide(), piece.getDownSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() - 1, savedPiece.getyPosition(), "right", "left", 3);

                        }
                        if (savedPiece.isTopSideFree()) {
                            newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getLeftSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() - 1, "down", "top", 3);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getTopSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() - 1, "down", "top", 2);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getRightSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() - 1, "down", "top", 1);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getTopSide(), piece.getDownSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() - 1, "down", "top", 0);

                        }
                        if (savedPiece.isRightSideFree()) {
                            newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getLeftSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() + 1, savedPiece.getyPosition(), "left", "right", 0);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getTopSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() + 1, savedPiece.getyPosition(), "left", "right", 3);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getRightSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() + 1, savedPiece.getyPosition(), "left", "right", 2);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getRightSide(), piece.getDownSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition() + 1, savedPiece.getyPosition(), "left", "right", 1);

                        }
                        if (savedPiece.isDownSideFree()) {
                            newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getLeftSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() + 1, "top", "down", 1);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getTopSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() + 1, "top", "down", 0);

                            newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getRightSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() + 1, "top", "down", 3);


                            newSupposedMaxValue = areSidesJoint(savedPiece.getDownSide(), piece.getDownSide(), accuracy);
                            if (newSupposedMaxValue > conformity.getValue())
                                conformity = new Conformity(newSupposedMaxValue, piece, savedPiece, savedPiece.getxPosition(), savedPiece.getyPosition() + 1, "top", "down", 2);

                        }
                    }
                }
                //save best result
                Piece pieceToSave = conformity.getPiece();
                notSavedPieces.remove(pieceToSave);
                switch (conformity.getSideToChangeInNotSavedPiece()) {
                    case "left": {
                        pieceToSave.setLeftSideFree(false);
                        conformity.getSavedPiece().setRightSideFree(false);
                        break;
                    }
                    case "top": {
                        pieceToSave.setTopSideFree(false);
                        conformity.getSavedPiece().setDownSideFree(false);
                        break;
                    }
                    case "right": {
                        pieceToSave.setRightSideFree(false);
                        conformity.getSavedPiece().setLeftSideFree(false);
                        break;
                    }
                    case "down": {
                        pieceToSave.setDownSideFree(false);
                        conformity.getSavedPiece().setTopSideFree(false);
                        break;
                    }
                }
                pieceToSave = modifyPiece(pieceToSave, conformity.getRotation(), conformity.getPieceX(), conformity.getPieceY());
                savedPieces.add(pieceToSave);
                savedPiecesIterator = savedPieces.iterator();
            }
            //add image views

            for (Piece savedPiece : savedPieces) {
                ImageView imageView = new ImageView(savedPiece.getImage());
                imageView.setRotate(90 * savedPiece.getRotation());
                picture.add(imageView,
                        (int) (savedPiece.getxPosition() - Piece.min) * savedPiece.getSize(),
                        (int) (savedPiece.getyPosition() - Piece.min) * savedPiece.getSize());
            }
        });
    }

    private Piece modifyPiece(Piece piece, int rotate, int x, int y){
        Piece newPiece = piece;
        newPiece.rotate(rotate);
        newPiece.setxPosition(x);
        newPiece.setyPosition(y);
        return newPiece;
    }
    //calculating number of mistakes

    private int areSidesJoint(javafx.scene.paint.Color[] side1, javafx.scene.paint.Color[] side2, double accuracy) {
        int size = side1.length;
        int mistakes = 0;
        for(int i = 0; i < size; i++){
            if((Math.abs(side1[i].getBlue() - side2[size - i - 1].getBlue()) >accuracy)
                && (Math.abs(side1[i].getRed() - side1[size - i - 1].getRed()) > accuracy)
                && (Math.abs(side1[i].getGreen() - side2[size - i - 1].getGreen()) > accuracy)){
                mistakes++;
            }
        }
        return size - mistakes;
    }
}
