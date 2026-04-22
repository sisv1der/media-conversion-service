package ru.yarigo.mediaconversionservice.job.processor;

public interface JobProcessor<T> {

    T process(T job);
}
