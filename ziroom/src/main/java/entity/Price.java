package entity;

public class Price {

    // 描述（如：月付、季付、半年付、年付）
    private String desc;

    // 每个月租金
    private int rentPerMonth;

    // 押金
    private int deposit;

    // 每年服务费
    private int servicePerYear;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRentPerMonth() {
        return rentPerMonth;
    }

    public void setRentPerMonth(int rentPerMonth) {
        this.rentPerMonth = rentPerMonth;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getServicePerYear() {
        return servicePerYear;
    }

    public void setServicePerYear(int servicePerYear) {
        this.servicePerYear = servicePerYear;
    }

    /**
     * 每个月收费金额
     */
    public int totalPerMonth() {
        return rentPerMonth + servicePerYear / 12;
    }
}
