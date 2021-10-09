package com.rmeunier.colormatchapi.config;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class ImportParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        if (jobParameters == null) {
            throw new JobParametersInvalidException("Job parameters could not be retrieved.");
        }

        if (jobParameters.getString("filePath") == null || jobParameters.getString("filePath").isEmpty()) {
            throw new JobParametersInvalidException("File path could not be found.");
        }
    }
}
