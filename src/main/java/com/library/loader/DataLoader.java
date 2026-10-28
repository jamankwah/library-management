package com.library.loader;

import com.library.exception.DataLoadException;

import java.nio.file.Path;
import java.util.List;

/** Contract for anything able to turn a file into domain objects. */
public interface DataLoader<T> {

    List<T> load(Path path) throws DataLoadException;
}