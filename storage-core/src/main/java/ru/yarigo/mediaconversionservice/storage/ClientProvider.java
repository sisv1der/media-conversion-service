package ru.yarigo.mediaconversionservice.storage;

public interface ClientProvider<T> {

    T createClient();
}
