package sample.models;

public class Conformity{
    private int value;
    private Piece piece;
    private Piece savedPiece;
    private Point notSavedPiecePoint;
    private String sideToChangeInNotSavedPiece;
    private String sideToChangeInSavedPiece;
    private int rotation;

    public Conformity(int value) {
        this.value = value;
    }

    public Conformity(int value, Piece piece, Piece savedPiece, Point notSavedPiecePoint, String sideToChangeInNotSavedPiece, String sideToChangeInSavedPiece, int rotation) {
        this.value = value;
        this.piece = piece;
        this.savedPiece = savedPiece;
        this.notSavedPiecePoint = notSavedPiecePoint;
        this.sideToChangeInNotSavedPiece = sideToChangeInNotSavedPiece;
        this.sideToChangeInSavedPiece = sideToChangeInSavedPiece;
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public Piece getSavedPiece() {
        return savedPiece;
    }

    public void setSavedPiece(Piece savedPiece) {
        this.savedPiece = savedPiece;
    }

    public String getSideToChangeInNotSavedPiece() {
        return sideToChangeInNotSavedPiece;
    }

    public void setSideToChangeInNotSavedPiece(String sideToChangeInNotSavedPiece) {
        this.sideToChangeInNotSavedPiece = sideToChangeInNotSavedPiece;
    }

    public String getSideToChangeInSavedPiece() {
        return sideToChangeInSavedPiece;
    }

    public void setSideToChangeInSavedPiece(String sideToChangeInSavedPiece) {
        this.sideToChangeInSavedPiece = sideToChangeInSavedPiece;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Point getNotSavedPiecePoint() {
        return notSavedPiecePoint;
    }

    public void setNotSavedPiecePoint(Point notSavedPiecePoint) {
        this.notSavedPiecePoint = notSavedPiecePoint;
    }

    @Override
    public String toString() {
        return "\nConformity{" +
                "value=" + value +
                ",notSavedPiece=" + piece.getName() +
                ", savedPiece=" + savedPiece.getName() +
                ", sideToChangeInNotSavedPiece='" + sideToChangeInNotSavedPiece + '\'' +
                ", sideToChangeInSavedPiece='" + sideToChangeInSavedPiece + '\'' +
                ", rotation=" + rotation +
                '}';
    }
}
