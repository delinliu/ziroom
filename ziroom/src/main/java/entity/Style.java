package entity;

public class Style {

    // 风格（如：木棉、拿铁）
    private String style;

    // 风格版本（如：4.0）
    private int version;

    @Override
    public boolean equals(Object styleObj) {
        if (styleObj == null || !(styleObj instanceof Style)) {
            return false;
        }
        Style s = (Style) styleObj;
        if (!style.equals(s.style) || version != s.version) {
            return false;
        }
        return true;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
