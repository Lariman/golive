package org.golive.gift.provider.service.bo;

public class DecrStockNumBO {

    private boolean isSuccess;
    private boolean noStock;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isNoStock() {
        return noStock;
    }

    public void setNoStock(boolean noStock) {
        this.noStock = noStock;
    }
}
