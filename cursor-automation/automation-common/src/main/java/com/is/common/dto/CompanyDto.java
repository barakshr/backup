package com.is.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Represents an IronScales company (test tenant).
 * Stub — fields to be expanded when company creation is implemented.
 */
@Data
@Builder
public class CompanyDto {

    private String companyId;
    private String companyName;
    private String ownerEmail;
    private String tenantId;
    private String domain;
    private String apiToken;
}
