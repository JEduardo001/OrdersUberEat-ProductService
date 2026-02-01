package com.SoftwareOrdersUberEats.productService.constant;


public class TracerConstants {
    public static final String CORRELATION_KEY = "correlationId";
    public static final String CORRELATION_HEADER = "X-Correlation-Id";

    //MESSAGE
    public static final String MESSAGE_REVERSED_STOCK = "Reversed stock {}";
    public static final String MESSAGE_SAVE_EVENT = "Save event topic name topic {}";
    public static final String MESSAGE_SAVE_ENTITY = "Save entity {}";
    public static final String MESSAGE_SEND_EVENT = "Send event id event{}";
    public static final String MESSAGE_DATA_VALIDATION_VERIFY_STOCK_ERROR = "Error data validate to verify product stock";
    public static final String MESSAGE_UPDATE_PRODUCT = "Update product";

    //EXCEPTION
    public static final String EXCEPTION_ALREADY_EVENT_PROCESSED = "Exception already event processed {}";
    //ERROR
    public static final String ERROR_SEND_EVENT = "Error send event";
}