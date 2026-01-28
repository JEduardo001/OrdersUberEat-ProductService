package com.SoftwareOrdersUberEats.productService.interfaces;

public interface IConsumer {
    void handleVerifyProductStock(String rawEvent);
    void handleRevertStockProducts(String rawEvent);
}
