package com.chillies.hearttohome.exceptions;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     Object fieldValue) {

        super(resourceName + " with " + fieldName + " '" +
                fieldValue + "' not found.");
    }
}