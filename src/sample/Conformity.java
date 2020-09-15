package sample;

public class Conformity {
    private int value;
    private Piece piece;
    private Piece savedPiece;
    private int pieceX;
    private int pieceY;
    private String sideToChangeInNotSavedPiece;
    private String sideToChangeInSavedPiece;
    private int rotation;

    public Conformity(int value) {
        this.value = value;
    }

    public Conformity(int value, Piece piece, Piece savedPiece, int pieceX, int pieceY, String sideToChangeInNotSavedPiece, String sideToChangeInSavedPiece, int rotation) {
        this.value = value;
        this.piece = piece;
        this.savedPiece = savedPiece;
        this.pieceX = pieceX;
        this.pieceY = pieceY;
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

    public int getPieceX() {
        return pieceX;
    }

    public void setPieceX(int pieceX) {
        this.pieceX = pieceX;
    }

    public int getPieceY() {
        return pieceY;
    }

    public void setPieceY(int pieceY) {
        this.pieceY = pieceY;
    }

    @Override
    public String toString() {
        return "Conformity{" +
                "value=" + value +
                ", piece=" + piece +
                ", pieceX=" + pieceX +
                ", pieceY=" + pieceY +
                '}';
    }
}
