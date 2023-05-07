package Storyboard2.Core;

public enum TileData {
    DISPLAY(0), COLLISION(1), OVERLAY(2), INTERACTION(3);

    private final int dataIndex;
    TileData(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public int parseData(String data) {
        return Integer.parseInt(data.split(":")[dataIndex]);
    }
}
