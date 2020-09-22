package sample.models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Piece {
    private String name;
    private int size;

    private Image image;
    private boolean isRightSideFree;
    private boolean isLeftSideFree;
    private boolean isTopSideFree;
    private boolean isDownSideFree;
    private Color[] leftSide;
    private Color[] topSide;
    private Color[] rightSide;
    private Color[] downSide;

    private int rotation = 0;

    private Point point;

    public static int min = 0;

    public Piece(Piece piece) {
        this.name = piece.getName();
        this.image = piece.getImage();
        this.size = piece.getSize();
        this.isLeftSideFree = piece.isLeftSideFree();
        this.isRightSideFree = piece.isRightSideFree();
        this.isDownSideFree = piece.isDownSideFree();
        this.isTopSideFree = piece.isTopSideFree();
        this.point = piece.getPoint();
        this.rotation = piece.getRotation();

        this.leftSide = piece.getLeftSide();
        this.topSide = piece.getTopSide();
        this.rightSide = piece.getRightSide();
        this.downSide = piece.getDownSide();
    }

    public Piece(Image image, String name) {
        this.name = name;
        this.image = image;
        this.size = (int)image.getHeight();
        this.isLeftSideFree = true;
        this.isRightSideFree = true;
        this.isDownSideFree = true;
        this.isTopSideFree = true;

        this.leftSide = new Color[size];
        this.topSide = new Color[size];
        this.rightSide = new Color[size];
        this.downSide = new Color[size];

        for(int i = 0; i < size; i++){
            leftSide[i] = image.getPixelReader().getColor(0,size - i - 1);
            topSide[i] = image.getPixelReader().getColor(i, 0);
            rightSide[i] = image.getPixelReader().getColor(size - 1, i);
            downSide[i] = image.getPixelReader().getColor(size - i - 1, size-1);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isRightSideFree() {
        return isRightSideFree;
    }

    public void setRightSideFree(boolean rightSideFree) {
        isRightSideFree = rightSideFree;
    }

    public boolean isLeftSideFree() {
        return isLeftSideFree;
    }

    public void setLeftSideFree(boolean leftSideFree) {
        isLeftSideFree = leftSideFree;
    }

    public boolean isTopSideFree() {
        return isTopSideFree;
    }

    public void setTopSideFree(boolean topSideFree) {
        isTopSideFree = topSideFree;
    }

    public boolean isDownSideFree() {
        return isDownSideFree;
    }

    public void setDownSideFree(boolean downSideFree) {
        isDownSideFree = downSideFree;
    }

    public Color[] getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(Color[] leftSide) {
        this.leftSide = leftSide;
    }

    public Color[] getTopSide() {
        return topSide;
    }

    public void setTopSide(Color[] topSide) {
        this.topSide = topSide;
    }

    public Color[] getRightSide() {
        return rightSide;
    }

    public void setRightSide(Color[] rightSide) {
        this.rightSide = rightSide;
    }

    public Color[] getDownSide() {
        return downSide;
    }

    public void setDownSide(Color[] downSide) {
        this.downSide = downSide;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "\nPiece{" +
                "name='" + name + '\'' +
                ", isRightSideFree=" + isRightSideFree +
                ", isLeftSideFree=" + isLeftSideFree +
                ", isTopSideFree=" + isTopSideFree +
                ", isDownSideFree=" + isDownSideFree +
                ", rotation= " + rotation +
                ", point=" + point +
                '}';
    }

    public void rotate(int rotateNumber){
        for(int i = 0; i < rotateNumber; i++){
            this.rotation = (this.rotation + 1)%4;
            Color []tmp;
            tmp = Arrays.copyOf(leftSide, leftSide.length);
            this.leftSide = Arrays.copyOf(topSide, topSide.length);
            this.topSide = Arrays.copyOf(rightSide, rightSide.length);
            this.rightSide = Arrays.copyOf(downSide, downSide.length);
            this.downSide = Arrays.copyOf(tmp, tmp.length);
        }
    }
}
