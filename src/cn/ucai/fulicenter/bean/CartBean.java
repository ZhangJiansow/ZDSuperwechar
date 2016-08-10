package cn.ucai.fulicenter.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by 27932 on 2016/7/29.
 */
public class CartBean implements Serializable {

    /**
     * id : 7672
     * userName : 7672
     * goodsId : 7672
     * count : 2
     * checked : true
     * goods : GoodDetailsBean
     */

    private int id;
    private String userName;
    private int goodsId;
    private int count;
    @JsonProperty("isChecked")
    private boolean isChecked;
    private GoodDetailsBean goods;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JsonIgnore
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    public GoodDetailsBean getGoods() {
        return goods;
    }

    public void setGoods(GoodDetailsBean goods) {
        this.goods = goods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartBean)) return false;

        CartBean cartBean = (CartBean) o;

        return getId() == cartBean.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return "CartBean{" +
                "id=" + id +
                ", userName=" + userName +
                ", goodsId=" + goodsId +
                ", count=" + count +
                ", checked=" + isChecked +
                ", goods='" + goods + '\'' +
                '}';
    }
}
