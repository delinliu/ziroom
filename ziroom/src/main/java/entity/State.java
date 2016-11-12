package entity;

public enum State {

    Available(0, "可入住"), Configuring(1, "配置中"), Unavailable(2, "已入住");

    State(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    private int state;
    private String desc;

    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

}
