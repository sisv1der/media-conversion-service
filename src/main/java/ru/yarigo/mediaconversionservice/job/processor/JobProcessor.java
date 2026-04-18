package ru.yarigo.mediaconversionservice.job.processor;

public interface JobProcessor<T> {

    void process(T job);
}
