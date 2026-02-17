package ru.yarigo.mediaconversionservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yarigo.mediaconversionservice.conversion.exception.UnsupportedMediaFormatException;
import ru.yarigo.mediaconversionservice.conversion.job.exception.FileProcessingFailedException;
import ru.yarigo.mediaconversionservice.conversion.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.conversion.job.web.exception.TooEarlyException;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex, "ENTITY_NOT_FOUND", request);
    }

    @ExceptionHandler(FileProcessingFailedException.class)
    public ProblemDetail handleException(FileProcessingFailedException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex, "FILE_PROCESSING_FAILED", request);
    }

    @ExceptionHandler(TooEarlyException.class)
    public ProblemDetail handleTooEarlyException(TooEarlyException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.TOO_EARLY, ex, "TOO_EARLY", request);
    }

    @ExceptionHandler(UnsupportedMediaFormatException.class)
    public ProblemDetail handleUnsupportedMediaFormat(UnsupportedMediaFormatException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex, "UNSUPPORTED_MEDIA_TYPE", request);
    }

    @ExceptionHandler(JobProcessingException.class)
    public ProblemDetail handleJobProcessingException(JobProcessingException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex, "JOB_PROCESSING_FAILED", request);
    }



    private ProblemDetail buildProblemDetail(
            HttpStatus status,
            Exception ex,
            String errorCode,
            WebRequest request) {
        String message = ex.getMessage();
        String path = request.getDescription(false).replaceAll("uri=", "");
        Instant timestamp = Instant.now();

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(URI.create("about:blank"));
        pd.setTitle(status.getReasonPhrase());
        pd.setDetail(Optional.ofNullable(message).orElse("UnexpectedError"));
        pd.setProperty("errorCode", errorCode);
        pd.setProperty("timestamp", timestamp.toString());
        pd.setInstance(URI.create(path));

        return pd;
    }
}
