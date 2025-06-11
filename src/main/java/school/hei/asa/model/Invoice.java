package school.hei.asa.model;

import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;

public record Invoice(String base64Image, ThInvoiceForm invoiceData) {}
