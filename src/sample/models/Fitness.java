package sample.models;

import java.util.LinkedList;

public class Fitness {
    private double value = 0;
    private LinkedList<Piece> savedPieces;
    private LinkedList<Piece> notSavedPieces;
    //min positions of saved pieces
    private int minXPosition;
    private int minYPosition;

    public Fitness(double value, LinkedList<Piece> savedPieces, LinkedList<Piece> notSavedPieces) {
        this.value = value;
        this.savedPieces = savedPieces;
        this.notSavedPieces = notSavedPieces;
    }

    public Fitness(double value, LinkedList<Piece> savedPieces, LinkedList<Piece> notSavedPieces, int minXPosition, int minYPosition) {
        this.value = value;
        this.savedPieces = savedPieces;
        this.notSavedPieces = notSavedPieces;
        this.minXPosition = minXPosition;
        this.minYPosition = minYPosition;
    }

    public Fitness(LinkedList<Piece> savedPieces, LinkedList<Piece> notSavedPieces) {
        this.savedPieces = savedPieces;
        this.notSavedPieces = notSavedPieces;
    }

    public int getMinXPosition() {
        return minXPosition;
    }

    public void setMinXPosition(int minXPosition) {
        this.minXPosition = minXPosition;
    }

    public int getMinYPosition() {
        return minYPosition;
    }

    public void setMinYPosition(int minYPosition) {
        this.minYPosition = minYPosition;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LinkedList<Piece> getSavedPieces() {
        return savedPieces;
    }

    public void setSavedPieces(LinkedList<Piece> savedPieces) {
        this.savedPieces = savedPieces;
    }

    public LinkedList<Piece> getNotSavedPieces() {
        return notSavedPieces;
    }

    public void setNotSavedPieces(LinkedList<Piece> notSavedPieces) {
        this.notSavedPieces = notSavedPieces;
    }

    @Override
    public String toString() {
        return "\nFitness{" +
                "value=" + value +
                ", savedPieces=" + savedPieces +
                ", notSavedPieces= " + notSavedPieces +
                /*", notSavedPieces=" + notSavedPieces +*/
                '}';
    }
}
