package sample.models;

public class Point {
    private int xValue;
    private int yValue;

    public Point(int xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public Point() {
    }

    public int getxValue() {
        return xValue;
    }

    public void setxValue(int xValue) {
        this.xValue = xValue;
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }

    @Override
    public String toString() {
        return "Point{" +
                "xValue=" + xValue +
                ", yValue=" + yValue +
                '}';
    }
}
