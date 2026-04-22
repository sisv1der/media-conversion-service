package ru.yarigo.mediaconversionservice.job.producer;

public interface Producer<T> {

    void produce(T event);
}
