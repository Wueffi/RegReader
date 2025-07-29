package wueffi.regreader;

public class RegReaderHUD {
    public String Name;
    public String HudColor;
    public Integer DisplayBase;
    public Boolean ColoredNames;
    public Integer RectangleWidth;
    public Integer xPos;
    public Integer yPos;

    public RegReaderHUD(String name, String hudColor, Integer displayBase, Boolean colordNames, Integer rectangleWidth, Integer xPos, Integer yPos) {
        this.Name = name;
        this.HudColor = hudColor;
        this.DisplayBase = displayBase;
        this.ColoredNames = colordNames;
        this.RectangleWidth = rectangleWidth;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public String getHUDName() {
        return Name;
    }

    public void setHUDName(String newName) {
        this.Name = newName;
    }

    public String getHUDColor() {
        return HudColor;
    }

    public void setHUDColor(String newColor) {
        this.HudColor = newColor;
    }

    public Integer getDisplayBase() {
        return DisplayBase;
    }

    public void setDisplayBase(Integer newBase) {
        this.DisplayBase = newBase;
    }

    public Boolean getColoredNames() {
        return ColoredNames;
    }

    public void setColoredNames(Boolean newState) {
        this.ColoredNames = newState;
    }

    public Integer getRectangleWidth() {
        return RectangleWidth;
    }

    public void setRectangleWidth(Integer newWidth) {
        this.RectangleWidth = newWidth;
    }

    public Integer getxPos() {
        return this.xPos;
    }

    public void setxPos(Integer newPos) {
        this.xPos = newPos;
    }

    public Integer getyPos() {
        return this.yPos;
    }

    public void setyPos(Integer newPos) {
        this.yPos = newPos;
    }


}