package entity;

import java.util.HashMap;
import java.util.Map;

public class Price implements Comparable<Price> {

    // 描述（如：月付、季付、半年付、年付）
    private String desc;

    // 每个月租金
    private int rentPerMonth;

    // 押金
    private int deposit;

    // 每年服务费
    private int servicePerYear;

    private static Map<String, Integer> descMap;
    static {
        descMap = new HashMap<>();
        descMap.put("月付", 0);
        descMap.put("季付", 1);
        descMap.put("半年付", 2);
        descMap.put("年付", 3);
    }

    @Override
    public int compareTo(Price o) {
        if (rentPerMonth < o.rentPerMonth) {
            return -1;
        }
        if (rentPerMonth > o.rentPerMonth) {
            return 1;
        }
        if (deposit < o.deposit) {
            return -1;
        }
        if (deposit > o.deposit) {
            return 1;
        }
        if (servicePerYear < o.servicePerYear) {
            return -1;
        }
        if (servicePerYear > o.servicePerYear) {
            return 1;
        }
        Integer o0 = descMap.get(desc);
        Integer o1 = descMap.get(o.desc);
        if (o0 != null && o1 != null) {
            return o0 - o1;
        } else if (o0 != null && o1 == null) {
            return -1;
        } else if (o0 == null && o1 != null) {
            return 1;
        } else {
            return desc.compareTo(o.desc);
        }
    }

    @Override
    public boolean equals(Object priceObj) {
        if (priceObj == null || !(priceObj instanceof Price)) {
            return false;
        }
        Price price = (Price) priceObj;
        if (!desc.equals(price.desc) || rentPerMonth != price.rentPerMonth || deposit != price.deposit
                || servicePerYear != price.servicePerYear) {
            return false;
        }
        return true;
    }

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
