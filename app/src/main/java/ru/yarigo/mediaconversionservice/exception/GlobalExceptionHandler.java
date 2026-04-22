package ru.yarigo.mediaconversionservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yarigo.mediaconversionservice.job.exception.FileProcessingFailedException;
import ru.yarigo.mediaconversionservice.job.exception.JobAlreadyProcessingException;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.web.exception.TooEarlyException;

import static ru.yarigo.mediaconversionservice.exception.ProblemDetailProvider.getProblemDetail;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(WebRequest request) {
        return getProblemDetail(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "ENTITY_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(FileProcessingFailedException.class)
    public ProblemDetail handleException(WebRequest request) {
        return getProblemDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Content unprocessable",
                "FILE_PROCESSING_FAILED",
                request
        );
    }
    @ExceptionHandler(TooEarlyException.class)
    public ProblemDetail handleTooEarlyException(WebRequest request) {
        return getProblemDetail(
                HttpStatus.TOO_EARLY,
                "Processing haven't finished yet",
                "TOO_EARLY",
                request
        );
    }

    @ExceptionHandler(JobProcessingException.class)
    public ProblemDetail handleJobProcessingException(WebRequest request) {
        return getProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Job processing failed",
                "JOB_PROCESSING_FAILED",
                request
        );
    }

    @ExceptionHandler(JobAlreadyProcessingException.class)
    public ProblemDetail handleJobAlreadyProcessingException(WebRequest request) {
        return getProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Job already being processed",
                "JOB_ALREADY_PROCESSING",
                request
        );
    }
}
