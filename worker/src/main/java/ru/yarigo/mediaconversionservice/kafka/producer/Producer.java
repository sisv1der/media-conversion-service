package ru.yarigo.mediaconversionservice.kafka.producer;

public interface Producer<T> {

    void produce(T event);
}
