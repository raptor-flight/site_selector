package com.raptor.ai.site.domain.model.common;

/***
 * <p>
 *  Indicates additions, changes and deletions to the records.(see guide below).
 *  A = Addition
 *  C = Change
 *  D = Delete
 *  Note that where a transaction changes category type due to misallocation (as above)
 *  it will be deleted from the original category type and added to the correct category
 *  with a new transaction unique identifier.
 * </p>
 */
public enum RecordStatus {

    ADDITION("A"),
    CHANGE("C"),
    DELETE("D");

    private String recordStatus;

    RecordStatus(String recordStatus) {
        recordStatus = recordStatus;
    }

    public String getRecordStatus() {
        return recordStatus;
    }
}
